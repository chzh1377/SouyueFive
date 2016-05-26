package com.zhongsou.souyue.circle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.adapter.CircleSignatureAdapter;
import com.zhongsou.souyue.circle.adapter.SignatureItem;
import com.zhongsou.souyue.circle.model.SignatureBean;
import com.zhongsou.souyue.dialog.SYProgressDialog;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.personal.UserAddMood;
import com.zhongsou.souyue.net.personal.UserDeleteMood;
import com.zhongsou.souyue.net.personal.UserMoodList;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zcz on 2014/11/4. 我的首页中心情签名页面 YanBin
 */
public class NewSignatureActivity extends BaseActivity implements
        ProgressBarHelper.ProgressBarClickListener, View.OnClickListener, AbsListView.OnScrollListener {
    // private ListView signature_list;
    private LayoutInflater inflater;
    private View headView;
    private TextView title;
    private ImageButton btn_option;
    private ArrayList<Integer> arrface;
    private ArrayList<Integer> focusface;
    private ArrayList<String> faceStr;
    private ArrayList<SignatureItem> itemArr;
    private RelativeLayout face_rel_1, face_rel_2, face_rel_3, face_rel_4,
            face_rel_5, face_rel_6, face_rel_7, face_rel_8;
    private ImageView face_image1, face_image2, face_image3, face_image4,
            face_image5, face_image6, face_image7, face_image8;
    private TextView face_title1, face_title2, face_title3, face_title4,
            face_title5, face_title6, face_title7, face_title8;

    private EditText mood_edit;
    private String TempHint = "开心";
    private int num = 50;// 设置编辑框最大输入个数
    private TextView str_count;
    private long userId;
//    private Http http;
    private Button moodPublish;
    private PullToRefreshListView signature_list;
    private List<SignatureBean> signatureArr;
    private ProgressBarHelper pbHelp;
    private TextView getMore;
    private int mood_id = 0; // 表情id默认为0

    private CircleSignatureAdapter adapter;
    private int visibleLast = 0;
    private int visibleCount = 0;
    private boolean needLoad;
    private boolean hasMore = true;
    private boolean isLoadAll;
    public boolean isSelf = false;
    private String signature;
    // private InterestDialog interestDialog;
    private SYProgressDialog sydialog;

    // sydialog = new SYProgressDialog(this, 0, "正在发送回复");
    // private GridView faceGrid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_new_signature);
        userId = getIntent().getLongExtra("userId", 0);
        isSelf = 0 != userId
                && SYUserManager.getInstance().getUser().userId() == userId;
        // userId = SYUserManager.getInstance().getUser().userId();
        findViews();
        // 如果需要头部则执行下面代码
        if (isSelf) {
            headView = inflater.inflate(R.layout.circle_signature_edit, null);
            signature_list.getRefreshableView().addHeaderView(headView);
            headView.findViewById(R.id.headContent).setVisibility(View.VISIBLE);
            initArr();
            initHeadView();
            initHeadListener();
        } else {
            title.setText(R.string.history_signatrue);
        }
//		else {
//			headView.findViewById(R.id.headContent).setVisibility(View.GONE);
//		}
        adapter = new CircleSignatureAdapter(this, focusface, isSelf);
        initData();
        bindListener();
    }

    private void findViews() {
        pbHelp = new ProgressBarHelper(this, findViewById(R.id.ll_data_loading));
        pbHelp.setProgressBarClickListener(this);
        pbHelp.showLoading();
        inflater = LayoutInflater.from(this);
        signature_list = (PullToRefreshListView) findViewById(R.id.signature_list);
        // signature_list。set
        signature_list.setPullToRefreshEnabled(false);
        // 添加底部加载
        View footerView = getLayoutInflater().inflate(
                R.layout.cricle_single_list_refresh_footer, null);

        signature_list.getRefreshableView().addFooterView(footerView);
        // 加载失败
        getMore = (TextView) footerView.findViewById(R.id.get_more);
        getMore.setFocusableInTouchMode(false);
        getMore.setOnClickListener(this);

        // adapter =new CircleSignatureAdapter(this,
        // signatureArr, focusface);
        // adapter.setLoadingDataListener(this);
        signature_list.setOnScrollListener(this);

        title = (TextView) findViewById(R.id.activity_bar_title);
        btn_option = (ImageButton) findViewById(R.id.btn_option);
        btn_option.setVisibility(View.INVISIBLE);
        title.setText(R.string.signatrue_tit);
        focusface = new ArrayList<Integer>();
        focusface.add(R.drawable.face_happy_focus);
        focusface.add(R.drawable.face_laugh_focus);
        focusface.add(R.drawable.face_adorable_focus);
        focusface.add(R.drawable.face_dull_focus);
        focusface.add(R.drawable.face_angry_focus);
        focusface.add(R.drawable.face_sad_focus);
        focusface.add(R.drawable.face_depressed_focus);
        focusface.add(R.drawable.face_cry_focus);
        sydialog = new SYProgressDialog(this, 0, "处理中");
    }

    private void initData() {
//        http = new Http(this);
        int pageNo = (adapter.getCount() + 10) / 10;
        UserMoodList list = new UserMoodList(HttpCommon.USER_MOOD_LIST_REQUEST, this);
        list.setParams(userId + "", pageNo + "");
        mMainHttp.doRequest(list);
//		http.getMoodList(SYUserManager.getInstance().getToken(), userId + "",
//				pageNo + "");
    }

    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()) {
            case HttpCommon.USER_MOOD_LIST_REQUEST:
                getMoodListSuccess(request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.USER_DELETE_MODE_REQUEST:
                delMoodSuccess();
                break;
            case HttpCommon.USER_ADD_MODE_REQUEST:
                addMoodSuccess();
        }
    }

    public void getMoodListSuccess(HttpJsonResponse res) {
        signature_list.setVisibility(View.VISIBLE);
        pbHelp.goneLoading();
        needLoad = true;
        signatureArr = obj2SignatureList(res);
        if (adapter.getCount() == 0) {
            adapter.setSignatureArr(signatureArr);
            signature_list.setAdapter(adapter);
        } else {
            adapter.addSignatureArr(signatureArr);
            adapter.notifyDataSetChanged();
        }
        if (!hasMore) {
            getMore.setVisibility(View.VISIBLE);
            getMore.setText(R.string.cricle_no_more_data);
        }
        if (adapter.getCount() == 0) {
            if (isSelf) {
                getMore.setVisibility(View.VISIBLE);
                getMore.setText(R.string.no_data);
            } else {
                pbHelp.showNoData();
            }
        }
        signature_list.onRefreshComplete();
    }

    public void addMoodSuccess() {
        sydialog.dismiss();
        Toast.makeText(this, "心情发表成功", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.putExtra("signature", signature);
        intent.putExtra("mood_id", mood_id);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void delMood(String id) {
        sydialog.show();
        UserDeleteMood mood = new UserDeleteMood(HttpCommon.USER_DELETE_MODE_REQUEST, this);
        mood.setParams(id);
        mMainHttp.doRequest(mood);
//		http.delMood(SYUserManager.getInstance().getToken(), id);
    }

    public void delMoodSuccess() {
        if (adapter.getCount() == 0) {
            getMore.setVisibility(View.VISIBLE);
            getMore.setText(R.string.no_data);
        }
        adapter.getSignatureArr().remove(adapter.delPos);
        adapter.notifyDataSetChanged();
        sydialog.dismiss();
        Toast.makeText(this, "删除成功", Toast.LENGTH_LONG).show();
    }

    private List<SignatureBean> obj2SignatureList(HttpJsonResponse res) {
        int statusCode = res.getCode();
        if (statusCode != 200) {
            return null;
        }
        hasMore = res.getHead().get("hasMore").getAsBoolean();
        isLoadAll = !hasMore;
        return new Gson().fromJson(res.getBodyArray(),
                new TypeToken<List<SignatureBean>>() {
                }.getType());
    }

    private void bindListener() {
        signature_list.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                initData();
            }
        });
    }

    public void onBackPressClick(View v) {
        finish();
    }

    @Override
    public void onClick(View v) {
        int clickId = v.getId();
        switch (clickId) {
            case R.id.mood_publish:
                sydialog.show();
                signature = mood_edit.getText().toString();
                if (TextUtils.isEmpty(signature)) {
                    signature = mood_edit.getHint().toString();
                    if (TextUtils.isEmpty(signature)) {
                        signature = faceStr.get(mood_id);
                    }
                }
                UserAddMood addmood = new UserAddMood(HttpCommon.USER_ADD_MODE_REQUEST, this);
                addmood.setParams(mood_id + "", signature);
                mMainHttp.doRequest(addmood);
//			http.addMood(SYUserManager.getInstance().getUser().token(), mood_id
//					+ "", signature);
                break;
            case R.id.mood_edit:
                String moodText = mood_edit.getText().toString();
                if (TextUtils.isEmpty(moodText)) {
                    mood_edit.setHint("");
                }
                ;
                break;
        }
    }

    /**
     * tableLayout 布局初始化笑脸模块
     */
    public void initHeadView() {
        moodPublish = (Button) headView.findViewById(R.id.mood_publish);
    }

    /**
     * 初始化头部监听
     */
    private void initHeadListener() {
        for (int i = 0; i < itemArr.size(); i++) {
            final SignatureItem item = itemArr.get(i);
            final int j = i;
            item.rel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    for (int j = 0; j < itemArr.size(); j++) {
                        if (itemArr.get(j).ifFocus == true) {
                            // 设置这个为false
                            itemArr.get(j).getImage()
                                    .setImageResource(arrface.get(j));
                            itemArr.get(j).getRel()
                                    .setBackgroundColor(getResources().getColor(R.color.white));
                            itemArr.get(j).ifFocus = false;
                        }
                    }

                    item.rel.setBackgroundColor(getResources().getColor(R.color.face_back));// 设置当前表情背景颜色
                    item.getImage().setImageResource(focusface.get(j));
                    // mood_edit.clearFocus();
                    // if ("".equals(mood_edit.getText().toString())) {
                    mood_edit.setHint(faceStr.get(j));
                    // }
                    InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (TextUtils.isEmpty(mood_edit.getText().toString())) {
                        im.hideSoftInputFromWindow(NewSignatureActivity.this.getCurrentFocus()
                                .getWindowToken(), 0);
                    }
                    item.ifFocus = true;
                    mood_id = j;
                }
            });
        }

        //为输入框设置文本修改的监听
        mood_edit.addTextChangedListener(new TextWatcher() {
//            int selectionStart;
//            int selectionEnd;

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                StringBuilder sb = new StringBuilder(s);
                int number = num - sb.length();
                if (number >= 0)
                    str_count.setText(String.valueOf(number));    //设置剩余字符数
////                selectionStart = mood_edit.getSelectionStart();
////                selectionEnd = mood_edit.getSelectionEnd();
//                try {
//                    if (sb.length() > num) {    //长度大于50
//                        sb.delete(num, Integer.MAX_VALUE);
//                        mood_edit.setText(sb);
//                        mood_edit.setSelection(num);// 设置光标在最后
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });

        mood_edit.setOnClickListener(this);
        moodPublish.setOnClickListener(this);
    }

    /**
     * 初始化表情数据
     */
    private void initArr() {
        arrface = new ArrayList<Integer>();
        faceStr = new ArrayList<String>();
        itemArr = new ArrayList<SignatureItem>();
        mood_edit = (EditText) headView.findViewById(R.id.mood_edit);
        mood_edit.requestFocus();
        str_count = (TextView) headView.findViewById(R.id.str_count);
        arrface.add(R.drawable.face_happy);
        arrface.add(R.drawable.face_laugh);
        arrface.add(R.drawable.face_adorable);
        arrface.add(R.drawable.face_dull);
        arrface.add(R.drawable.face_angry);
        arrface.add(R.drawable.face_sad);
        arrface.add(R.drawable.face_depressed);
        arrface.add(R.drawable.face_cry);

        faceStr.add(getString(R.string.face_happy));
        faceStr.add(getString(R.string.face_laugh));
        faceStr.add(getString(R.string.face_adorable));
        faceStr.add(getString(R.string.face_dull));
        faceStr.add(getString(R.string.face_angry));
        faceStr.add(getString(R.string.face_sad));
        faceStr.add(getString(R.string.face_depressed));
        faceStr.add(getString(R.string.face_cry));

        face_rel_1 = (RelativeLayout) headView.findViewById(R.id.face_rel_1);
        face_rel_2 = (RelativeLayout) headView.findViewById(R.id.face_rel_2);
        face_rel_3 = (RelativeLayout) headView.findViewById(R.id.face_rel_3);
        face_rel_4 = (RelativeLayout) headView.findViewById(R.id.face_rel_4);
        face_rel_5 = (RelativeLayout) headView.findViewById(R.id.face_rel_5);
        face_rel_6 = (RelativeLayout) headView.findViewById(R.id.face_rel_6);
        face_rel_7 = (RelativeLayout) headView.findViewById(R.id.face_rel_7);
        face_rel_8 = (RelativeLayout) headView.findViewById(R.id.face_rel_8);

        face_image1 = (ImageView) headView.findViewById(R.id.face_image1);
        face_image2 = (ImageView) headView.findViewById(R.id.face_image2);
        face_image3 = (ImageView) headView.findViewById(R.id.face_image3);
        face_image4 = (ImageView) headView.findViewById(R.id.face_image4);
        face_image5 = (ImageView) headView.findViewById(R.id.face_image5);
        face_image6 = (ImageView) headView.findViewById(R.id.face_image6);
        face_image7 = (ImageView) headView.findViewById(R.id.face_image7);
        face_image8 = (ImageView) headView.findViewById(R.id.face_image8);

        face_title1 = (TextView) headView.findViewById(R.id.face_title1);
        face_title2 = (TextView) headView.findViewById(R.id.face_title2);
        face_title3 = (TextView) headView.findViewById(R.id.face_title3);
        face_title4 = (TextView) headView.findViewById(R.id.face_title4);
        face_title5 = (TextView) headView.findViewById(R.id.face_title5);
        face_title6 = (TextView) headView.findViewById(R.id.face_title6);
        face_title7 = (TextView) headView.findViewById(R.id.face_title7);
        face_title8 = (TextView) headView.findViewById(R.id.face_title8);

        itemArr.add(new SignatureItem(face_rel_1, face_image1, face_title1, 1,
                true));
        itemArr.add(new SignatureItem(face_rel_2, face_image2, face_title2, 2,
                false));
        itemArr.add(new SignatureItem(face_rel_3, face_image3, face_title3, 3,
                false));
        itemArr.add(new SignatureItem(face_rel_4, face_image4, face_title4, 4,
                false));
        itemArr.add(new SignatureItem(face_rel_5, face_image5, face_title5, 5,
                false));
        itemArr.add(new SignatureItem(face_rel_6, face_image6, face_title6, 6,
                false));
        itemArr.add(new SignatureItem(face_rel_7, face_image7, face_title7, 7,
                false));
        itemArr.add(new SignatureItem(face_rel_8, face_image8, face_title8, 8,
                false));
    }

    @Override
    public void onHttpError(IRequest request) {
        needLoad = true;
        if (sydialog != null) {
            sydialog.dismiss();
        }

        getMore.setVisibility(View.VISIBLE);
        getMore.setText(R.string.cricle_net_error);
        signature_list.onRefreshComplete();
        Toast.makeText(this, "网络异常，请重试", Toast.LENGTH_LONG).show();
        switch (request.getmId()) {
            case HttpCommon.USER_MOOD_LIST_REQUEST:
                if (adapter != null && adapter.getCount() == 0) {
                    pbHelp.showNetError();
                }
                break;
            default://根据@onHttpError不做处理
        }
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus status) {
//        // TODO Auto-generated method stub
//        needLoad = true;
//        if (sydialog != null) {
//            sydialog.dismiss();
//        }
//        if ("addMood".equals(methodName)) {
//
//        } else if ("delMood".equals(methodName)) {
//
//        } else {
//            if (adapter != null && adapter.getCount() == 0) {
//                pbHelp.showNetError();
//            }
//        }
//        getMore.setVisibility(View.VISIBLE);
//        getMore.setText(R.string.cricle_net_error);
//        signature_list.onRefreshComplete();
//        Toast.makeText(this, "网络异常，请重试", Toast.LENGTH_LONG).show();
//    }

    @Override
    public void clickRefresh() {
        // TODO Auto-generated method stub
        initData();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub
        int itemsLastIndex = adapter.getCount();
        if (itemsLastIndex < 0) {
            return;
        }
        int lastIndex = itemsLastIndex;
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                && visibleLast >= lastIndex && needLoad && hasMore) {
            needLoad = false;
            initData();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        visibleCount = visibleItemCount;
        visibleLast = firstVisibleItem + visibleItemCount - 2;
        if (isLoadAll) {
            ++visibleLast;
        }

    }
}
