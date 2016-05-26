package com.zhongsou.souyue.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.SearchAdapter;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.util.JSONUtils;
import com.zhongsou.souyue.common.utils.CommSharePreference;
import com.zhongsou.souyue.common.utils.StringUtils;
import com.zhongsou.souyue.fragment.SearchDataFragment;
import com.zhongsou.souyue.fragment.SearchShowFragment;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.sub.SuberSearchRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YanBin
 * @version V1.0
 * @project trunk
 * @Description 统一搜索页面
 * @date 2016/4/20
 */
public class SearchAllActivity extends BaseActivity implements
        TextWatcher, View.OnClickListener,
        TextView.OnEditorActionListener, View.OnFocusChangeListener {

    private SearchDataFragment searchDataFragment;  //搜索结果
    private SearchShowFragment searchShowFragment;  //搜索推荐
    private CharSequence keyword;
    private EditText etSearch;
    private List<SuberedItemInfo> actList;   //自动提示信息列表
    private ListView lvAutoComplete;
    private View shadow;
    public static boolean isFromUserInput;    //是否来自用户输入
    public static boolean isSearching;    //是否正在搜索
    private InputMethodManager mInputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_all);
        initView();
        initData();
        initFragment();
        hideKeyboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (etSearch != null) {
            etSearch.clearFocus();
        }
    }

    private void initView() {
        etSearch = (EditText) findViewById(R.id.search_edit);
        lvAutoComplete = (ListView) findViewById(R.id.search_auto_complete);
        Button btnCancel = (Button) findViewById(R.id.search_cancel);
        shadow = findViewById(R.id.search_shadow);
        etSearch.addTextChangedListener(this);
        etSearch.setOnEditorActionListener(this);
        etSearch.setOnFocusChangeListener(this);
        etSearch.setOnClickListener(this);

        btnCancel.setOnClickListener(this);
        shadow.setOnClickListener(this);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void initData() {
        searchDataFragment = new SearchDataFragment();
        searchShowFragment = new SearchShowFragment();

        actList = new ArrayList<>();
    }

    private void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.search_fragment_container,
                searchDataFragment);
        transaction.add(R.id.search_fragment_container,
                searchShowFragment);
        showFragment(searchShowFragment, searchDataFragment);
        transaction.commit();
    }

    private void showFragment(Fragment showFragment, Fragment hideFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(hideFragment);
        transaction.show(showFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.search_cancel:
                etSearch.setText("");
                onClickCancel();
                clearEditFocus();
                break;
            case R.id.search_shadow:
                setAutoVisibility(View.GONE);
                clearEditFocus();
                break;
            case R.id.search_edit:
                isFromUserInput = true;
                isSearching = false;
                break;
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK
//                && event.getRepeatCount() == 0) {
//            onClickCancel();
//            return false;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    public void onClickCancel() {
        if (!searchDataFragment.isHidden()) {
            showFragment(searchShowFragment, searchDataFragment);
        } else {
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        keyword = s;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (StringUtils.isEmpty(keyword)) {
            setAutoVisibility(View.GONE);
        }
        if (keyword.length() != 0 && isFromUserInput) {
            SuberSearchRequest request = new SuberSearchRequest(HttpCommon.SUBER_SERACH_METHOD, this);
            request.setParams(null, keyword.toString());
            CMainHttp.getInstance().doRequest(request);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_SEARCH:
            case EditorInfo.IME_ACTION_SEND:
            case EditorInfo.IME_ACTION_GO:
            case EditorInfo.IME_ACTION_DONE:
            case EditorInfo.IME_ACTION_UNSPECIFIED:
                startSearch(v);
                return true;
            default:
                return false;
        }
    }

    private void startSearch(TextView v) {
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
        setAutoVisibility(View.GONE);
        if (keyword != null && keyword.length() > 0) {
            isSearching = true;
            searchResult(keyword.toString());
            saveHistory(keyword.toString());
            searchShowFragment.notifyHistoryData();
        }
    }

    /**
     * 根据输入内容搜索结果
     */
    public void searchResult(String word) {
        if (word != null) {
            etSearch.clearFocus();
            if (keyword == null) {
                keyword = "";
            }
            if (!word.equals(keyword.toString())) {
                etSearch.setText(word);
                etSearch.setSelection(word.length());
            }
            searchDataFragment.doRequest(word);
            showFragment(searchDataFragment, searchShowFragment);

            searchDataFragment.setLoadingAnim();

            if(searchDataFragment != null && !searchDataFragment.isHidden()){
                searchDataFragment.gotoFirstItem();
            }
        }
        setAutoVisibility(View.GONE);
    }

    /**
     * 存储历史纪录
     */
    public void saveHistory(String word) {
        if (StringUtils.isEmpty(word)) return;

        String strHistory = CommSharePreference.getInstance()
                .getValue(CommSharePreference.DEFAULT_USER, "searchHistory", null);

        if (!StringUtils.isEmpty(strHistory)) {
            strHistory += ",";
        } else {
            strHistory = "";
        }
        strHistory += word;

        CommSharePreference.getInstance()
                .putValue(CommSharePreference.DEFAULT_USER, "searchHistory", strHistory);
    }

    @Override
    public void onHttpResponse(IRequest request) {
        int id = request.getmId();
        HttpJsonResponse response = (HttpJsonResponse) request.getResponse();
        switch (id) {
            case HttpCommon.SUBER_SERACH_METHOD: // 搜索
                getSearchSuccess(response);
                break;
        }
    }

    @Override
    public void onHttpError(IRequest request) {

    }

    private void getSearchSuccess(HttpJsonResponse response) {
        if (isSearching) return;
        actList = JSONUtils.fromJsonArray(
                response.getBodyArray(),
                new TypeToken<List<SuberedItemInfo>>() {
                }.getType());
        showAuto();
    }

    private void showAuto() {
        setAutoVisibility(View.VISIBLE);
        lvAutoComplete.setBackgroundColor(getResources().getColor(R.color.white));
        SearchAdapter adapter = new SearchAdapter(this, actList, R.layout.item_search_list);
        lvAutoComplete.setAdapter(adapter);
        lvAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isFromUserInput = false;
                String text = actList.get(position).getTitle();
                setAutoVisibility(View.GONE);
                searchResult(text);
                saveHistory(text);
            }
        });
    }

    private void hideKeyboard(IBinder windowToken) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(windowToken, 0);
    }

    public void setAutoVisibility(int visibility) {
        shadow.setVisibility(visibility);
        lvAutoComplete.setVisibility(visibility);
    }

    public void setEditHint(String word, String hint) {
        if (word != null) {
            etSearch.setText("");
        }
        etSearch.setHint(hint);
    }

    public void clearEditFocus() {
        etSearch.clearFocus();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();

        if (id == R.id.search_edit) { //搜索框
            if (!hasFocus) {
                hideKeyboard(etSearch.getWindowToken());
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        hideKeyboard();
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        InputMethodManager im = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(etSearch.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 显示软键盘
     */
    public void showKeyboard() {
        InputMethodManager im = ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE));
        im.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //点击空白隐藏键盘
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }
}
