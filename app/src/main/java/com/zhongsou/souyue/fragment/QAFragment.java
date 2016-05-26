package com.zhongsou.souyue.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.QaAdapter;
import com.zhongsou.souyue.module.NavigationBar;
import com.zhongsou.souyue.module.SearchResult;
import com.zhongsou.souyue.net.srp.SrpAskRequest;
import com.zhongsou.souyue.net.volley.CDetailHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.SYInputMethodManager;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * Fragment 有问必答
 *
 * @author chefb@zhongsou.com
 */
@SuppressLint("ValidFragment")
public class QAFragment extends SRPFragment implements OnClickListener,IVolleyResponse {

    public static final int layoutId = R.layout.my_ask;
    private EditText et_question;
    private Button btn_send_ask;
    private View tv_no_question;
    private String q_a_md5;
    private String q_a_kid;

    public QAFragment(Context context, NavigationBar nav) {
        this(context,nav,null);
    }
    
    public QAFragment(Context context, NavigationBar nav,String type) {
        super(context, nav,type);
    }
    
    public QAFragment() {
    }

    public void setType(String type) {
        super.type = type;
    }

    public void setKeyWord(String keyWord) {
        super.keyWord = keyWord;
    }

    public void setSrpid(String srpId) {
        super.srpId = srpId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null)
            this.nav = (NavigationBar) savedInstanceState.getSerializable("nav");

        View view = View.inflate(activity, layoutId, null);
        inits(view);
        return view;
    }

    @Override
    protected void inits(View view) {
        et_question = (EditText) view.findViewById(R.id.et_myask);
        btn_send_ask = (Button) view.findViewById(R.id.btn_send_ask);
        tv_no_question = view.findViewById(R.id.tv_no_question);
        btn_send_ask.setOnClickListener(this);
        super.inits(view);
    }

    @Override
    public void searchResultSuccess(SearchResult searchResult) {
        if (searchResult.items().size() == 0) {
            tv_no_question.setVisibility(View.VISIBLE);
            customListView.setVisibility(View.GONE);
        } else {
            tv_no_question.setVisibility(View.GONE);
            customListView.setVisibility(View.VISIBLE);
        }
        q_a_md5 = searchResult.md5();
        q_a_kid = searchResult.kid();
//		if(((QaAdapter)adapter).isRefresh)
//			adapter.clearDatas();
        super.searchResultSuccess(searchResult);
    }

    public void searchResultToPullDownRefreshSuccess(final SearchResult sr) {
        if (sr.items().size() == 0) {
            tv_no_question.setVisibility(View.VISIBLE);
            customListView.setVisibility(View.GONE);
        } else {
            tv_no_question.setVisibility(View.GONE);
            customListView.setVisibility(View.VISIBLE);
        }
        q_a_md5 = sr.md5();
        q_a_kid = sr.kid();
        super.searchResultToPullDownRefreshSuccess(sr);
    }

    public void createPBHelper(View view, final NavigationBar nav) {
        tv_no_question.setVisibility(View.GONE);
        super.createPBHelper(view, nav);
    }

    @Override
    public void onClick(View v) {
        String answerString = et_question.getText().toString();
        if (TextUtils.isEmpty(answerString)) {
            SouYueToast.makeText(activity, R.string.contentisnull, 0).show();
            return;
        }
        if (TextUtils.isEmpty(q_a_md5) && TextUtils.isEmpty(q_a_kid))
            return;

        tv_no_question.setVisibility(View.GONE);
        customListView.setVisibility(View.VISIBLE);

        new SYInputMethodManager(activity).hideSoftInput();
//        http.wendaAsk(SYUserManager.getInstance().getToken(), SYUserManager.getInstance().getName(), q_a_md5, q_a_kid,
//                getKeyword(), getSrpId(), answerString);
        SrpAskRequest askRequest = new SrpAskRequest(HttpCommon.SRP_QA_ASK_REQUEST, this);
        askRequest.addParams(SYUserManager.getInstance().getName(), q_a_md5, q_a_kid, getKeyword(), getSrpId(), answerString);
        mainHttp.doRequest(askRequest);
        btn_send_ask.setClickable(false);
    }

/*    public void wendaAskSuccess(AjaxStatus as) {
        btn_send_ask.setClickable(true);
        SouYueToast.makeText(activity, R.string.askSuccess, 0).show();
        et_question.setText("");
        // 重新请求一次 刷新问题
        ((QaAdapter) adapter).isRefresh = true;
//		loadDataForce(http, 0, nav.url());
        http.searchResultForceRefresh(nav.url(), 0, SYUserManager.getInstance().getToken());
    }*/
    
    public void wendaAskSuccess() {
        btn_send_ask.setClickable(true);
        SouYueToast.makeText(activity, R.string.askSuccess, 0).show();
        et_question.setText("");
        // 重新请求一次 刷新问题
        ((QaAdapter) adapter).isRefresh = true;
//        http.searchResultForceRefresh(nav.url(), 0, SYUserManager.getInstance().getToken());
        SrpAskRequest askRequest = new SrpAskRequest(HttpCommon.SRP_QA_ASK_REQUEST, this);
        askRequest.addParams(nav.url(), 0, 10, true);
        mainHttp.doRequest(askRequest);
    }
    
//    @Override
//    public void onHttpError(String methodName) {
//        tv_no_question.setVisibility(View.GONE);
//        if ("wendaAsk".equals(methodName)) {
//            SouYueToast.makeText(activity, R.string.askFail, 0).show();
//            return;
//        }
//        btn_send_ask.setClickable(true);
//        super.onHttpError(methodName);
//    }
    
	@Override
	public void onHttpResponse(IRequest request) {
		  int id = request.getmId();
	        switch (id) {
	            case CDetailHttp.HTTP_GET_COMMENT_COUNT:
	                wendaAskSuccess();
	                break;
	        }
	}

	@Override
	public void onHttpError(IRequest request) {
		 tv_no_question.setVisibility(View.GONE);
		 btn_send_ask.setClickable(true);
		 switch (request.getmId()){
         case HttpCommon.SRP_QA_ASK_REQUEST:
        	 SouYueToast.makeText(activity, R.string.askFail, 0).show();
             break;
         case HttpCommon.SRP_QA_ASK_REFRESH_REQUEST:
        	 break;
         default:
             super.onHttpError(request);
             break;
		 }
	}

	@Override
	public void onHttpStart(IRequest request) {
		
	}
}
