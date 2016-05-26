package com.zhongsou.souyue.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.SuberRecomendAdapter;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.util.JSONUtils;
import com.zhongsou.souyue.circle.view.InterestDialog;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.db.SuberDao;
import com.zhongsou.souyue.db.SuberDaoImp;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.sub.SubUpdateRequest;
import com.zhongsou.souyue.net.sub.SuberRecomendRequest;
import com.zhongsou.souyue.net.sub.SuberSearchRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CSuberListHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.List;

public class SubSearchActivity extends BaseActivity implements View.OnClickListener, TextWatcher, IVolleyResponse {

    private static final String SUBER_ADD = "add";
    private static final String SUBER_DEL = "del";
    private PullToRefreshListView mPullGridView;
    private EditText etSearch;
    private ImageView cleanEdit;
    private CharSequence keyword;
    private String token;
    private String type;
    private SuberedItemInfo item;
    private InterestDialog suberDialg;
    protected CSuberListHttp mVolleyHttp;
    private SuberRecomendAdapter mRecommendAdapter;
    private List<SuberedItemInfo> infos;
    private SuberDao suberDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_search);

        token = SYUserManager.getInstance().getToken();
        mVolleyHttp = new CSuberListHttp(this);
        suberDao = new SuberDaoImp();

        initView();
        initGridView();
    }

    private void initView() {
        suberDialg = new InterestDialog(this);
        etSearch = (EditText) findViewById(R.id.sub_search_edit);
        cleanEdit = (ImageView) findViewById(R.id.sub_search_clean);
        mPullGridView = (PullToRefreshListView) findViewById(R.id.sub_search_grid);
        ImageButton goBack = (ImageButton) findViewById(R.id.sub_search_goback);
        Button search = (Button) findViewById(R.id.sub_search_button);

        findViewById(R.id.sub_search_layout).setOnClickListener(this);
        goBack.setOnClickListener(this);
        search.setOnClickListener(this);
        cleanEdit.setOnClickListener(this);
        etSearch.addTextChangedListener(this);
        mPullGridView.setOnClickListener(this);
    }

    private void initGridView() {
        etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                }
            }
        });
//        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if(actionId == EditorInfo.IME_ACTION_SEARCH){   //点击输入法中的搜索按钮
//                    InputMethodManager imm = (InputMethodManager) v.getContext()
//                            .getSystemService(Context.INPUT_METHOD_SERVICE);
//                    if (imm.isActive()) {
//                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
//                    }
//                    searchResult();
//                    return true;
//                }
//                return false;
//            }
//        });
        mPullGridView.setMode(PullToRefreshBase.Mode.DISABLED);
        mPullGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mRecommendAdapter.clickBtnOther(position - 1);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.sub_search_layout:
                etSearch.clearFocus();
                break;
            case R.id.sub_search_grid:
                etSearch.clearFocus();
                break;
            case R.id.sub_search_clean: //清空对话框内容按钮
                etSearch.getText().clear();
                break;
            case R.id.sub_search_goback:
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                break;
            case R.id.sub_search_button:
                searchResult();
                break;
        }
    }

    /**
     * 根据输入内容搜索结果
     */
    private void searchResult() {
        etSearch.clearFocus();
        if (keyword != null) {
            cleanEdit.setVisibility(View.VISIBLE);
            SuberSearchRequest request = new SuberSearchRequest(HttpCommon.SUBER_SERACH_METHOD, this);
            request.setParams(token, keyword.toString());
            CMainHttp.getInstance().doRequest(request);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before,
                              int count) {
        keyword = s;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (keyword.length() == 0) {
            cleanEdit.setVisibility(View.GONE);
//            getHotRecommendData();
        }
// else {
//            cleanEdit.setVisibility(View.VISIBLE);
//            SuberSearchRequest request = new SuberSearchRequest(HttpCommon.SUBER_SERACH_METHOD, this);
//            request.setParams(token, keyword.toString());
//            CMainHttp.getInstance().doRequest(request);
//        }
    }

    /**
     * 获取热门推荐
     */
    private void getHotRecommendData() {
        String vc = DeviceInfo.getAppVersion();
        String imei = DeviceInfo.getDeviceId();
        SuberRecomendRequest.send(HttpCommon.SUBER_REMCOMMEND_METHOD, token, vc, imei, this);
    }

    @Override
    public void onHttpResponse(IRequest _request) {
        super.onHttpResponse(_request);
        HttpJsonResponse response = (HttpJsonResponse) _request.getResponse();

        switch (_request.getmId()) {

            case HttpCommon.SUBER_UPDATE_METHOD: // 订阅
                long id = 0;
                try {
                    id = response.getBody().getAsJsonArray("id").get(0).getAsLong();
                } catch (Exception e) {
                    id = response.getBody().get("interest_id").getAsLong();
                } finally {
                    sysp.putBoolean(SYSharedPreferences.KEY_UPDATE_CIRCLE, true);
                    if (type.equals(SUBER_ADD)) {
                        suberDialg.subscribe();
                        UmengStatisticUtil.onEvent(this, UmengStatisticEvent.SUBSCRIBE_ADD_HOT_CLICK);
                    } else {
                        suberDialg.unsubscribe();
                    }
                    mRecommendAdapter.updateStatus(item);
                    if (type.equals(SUBER_ADD)) {
                        item.setId(id);
                    }
                    if ("0".equals(item.getStatus())) {
                        suberDao.addOne(item);
                        sysp.setSuberSrpId(item.getSrpId());//保存id到sysp
                    } else {
                        suberDao.clearOne(item);
                    }
                }
                break;

            case HttpCommon.SUBER_REMCOMMEND_METHOD: // 热门推荐
                infos = JSONUtils.fromJsonArray(response.getBodyArray(), new TypeToken<List<SuberedItemInfo>>() {
                }.getType());
                doNotify(infos);
                break;

            case HttpCommon.SUBER_SERACH_METHOD: // 搜索
                infos = JSONUtils.fromJsonArray(response.getBodyArray(), new TypeToken<List<SuberedItemInfo>>() {
                }.getType());
                doNotify(infos);
                break;

            default:
                break;
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        super.onHttpError(request);
        HttpJsonResponse response = (HttpJsonResponse) request.getResponse();

        switch (request.getmId()) {

            case HttpCommon.SUBER_UPDATE_METHOD: // 订阅
                if (suberDialg != null && suberDialg.isShowing()) {
                    suberDialg.dismiss();
                }
                break;
            default:
                break;
        }
    }

    private void doNotify(List<SuberedItemInfo> infoList) {
        mRecommendAdapter = new SuberRecomendAdapter(this, infoList);
        mRecommendAdapter.setmClickBtnAdd(new SuberRecomendAdapter.ClickBtnAdd() {
            @Override
            public void clickBtnAdd(int position) {
                etSearch.clearFocus();
                item = infos.get(position);
                type = SUBER_ADD;
                if ("0".equals(item.getStatus())) {
                    type = SUBER_DEL;
                }
                if ("1".equals(item.getType())) { // 私密圈
                    IntentUtil.gotoSecretCricleCard(SubSearchActivity.this, item.getId(), 2);
                    return;
                }
                suberDialg.show();
                suberDialg.progress();
                SubUpdateRequest.send(HttpCommon.SUBER_UPDATE_METHOD, token, DeviceInfo.getDeviceId(), item.getSrpId(), type, item
                        .getCategory(), item.getId() + "", item.getKeyword(), SubSearchActivity.this);
            }
        });
        mPullGridView.setAdapter(mRecommendAdapter);
        mRecommendAdapter.notifyDataSetChanged();
    }
}
