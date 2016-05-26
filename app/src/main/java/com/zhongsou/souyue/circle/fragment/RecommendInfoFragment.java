package com.zhongsou.souyue.circle.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.tencent.mm.sdk.platformtools.Log;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SRPActivity;
import com.zhongsou.souyue.activity.TouchGalleryActivity;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.circle.activity.CircleInboxActivity;
import com.zhongsou.souyue.circle.activity.CircleRecommendInfoActivity;
import com.zhongsou.souyue.circle.model.RecommendInfo;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.TouchGallerySerializable;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.circle.CicleAuditRequest;
import com.zhongsou.souyue.net.circle.CircleGetUserRecommendInfoRequest;
import com.zhongsou.souyue.net.circle.CircleReadNewsDetailReq;
import com.zhongsou.souyue.net.circle.CircleRecomendRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.ui.webview.CustomWebView;
import com.zhongsou.souyue.ui.webview.JavascriptInterface;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.SettingsManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;

import java.util.Arrays;
import java.util.List;

//import com.alibaba.fastjson.JSON;

/**
 * Created by wenlong on 14-5-5.
 */
@SuppressLint("ValidFragment")
public class RecommendInfoFragment extends Fragment implements JavascriptInterface.ImagesListener,IVolleyResponse {

    public static final int AUDIT_REFUSE = 1;
    public static final int AUDIT_INTEREST = 2;
    public static final int AUDIT_ENSSENCE = 3;
    public static final int AUDIT_UNREFUSE = 4;

    private View root;
    private TextView title, srp, nickname, time, text;
    private LinearLayout ll_images;
    private CustomWebView webView;

    private long recommend_id;
    private int recommend_type;
    private int temp_recommend_state;

//    private Http http;
//    private AQuery aQuery;

    private String token;

    private RecommendInfo recommendInfo;

    private OnRecommendStateChangedListener onRecommendStateChangedListener;

    private boolean canRecommend = false;
    private boolean canInitState = false;

    private CircleRecommendInfoActivity activity;

    public List<String> imageUrls;
    private CMainHttp mCMainHttp;

    public RecommendInfoFragment(long recommend_id, int recommend_type,
                                 OnRecommendStateChangedListener onRecommendStateChangedListener){
        this.recommend_id = recommend_id;
        this.recommend_type = recommend_type;
        this.onRecommendStateChangedListener = onRecommendStateChangedListener;
//        http = new Http(this);
        mCMainHttp = CMainHttp.getInstance();
        token = SYUserManager.getInstance().getToken();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (CircleRecommendInfoActivity) getActivity();
//        aQuery = new AQuery(activity);

     //   activity.showLoading();
        if (recommend_type == CircleInboxActivity.RECOMMEND_TYPE_USER) {
//            http.getUserRecommend(recommend_id);
            CircleGetUserRecommendInfoRequest.send(HttpCommon.CIRCLE_GETUSERRECOMMENDINFO_REQUESTID,this,recommend_id);
        } else {

            CircleRecomendRequest request = new CircleRecomendRequest(HttpCommon.CIRCLE_SYSRECOMMEND_REQUEST,this);
            request.addParams(recommend_id);
            mCMainHttp.doRequest(request);

         //   http.getSysRecommend(recommend_id);
        }

        canRecommend = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.circle_fragment_recommend_info, null);
        initViews();

        return root;
    }

    private void initViews(){
        title = (TextView) root.findViewById(R.id.tv_circle_recommend_info_title);
        srp = (TextView) root.findViewById(R.id.tv_circle_recommend_info_srp);
        nickname = (TextView) root.findViewById(R.id.tv_circle_recommend_info_nickname);
        time = (TextView) root.findViewById(R.id.tv_circle_recommend_info_time);
        text = (TextView) root.findViewById(R.id.tv_circle_recommend_info_text);
        ll_images = (LinearLayout) root.findViewById(R.id.ll_circle_recommend_info_imgs);

        webView = (CustomWebView) root.findViewById(R.id.wv_circle_recommend_info_text);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        setWebViewClient();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void getUserRecommendSuccess(HttpJsonResponse res){
        onResult(res);
    }


    private void onResult(HttpJsonResponse res){

        if (res != null && res.getBody() != null) {
//            RecommendInfo info = JSON.parseObject(res.getBody().toString(), RecommendInfo.class);
            RecommendInfo info = new Gson().fromJson(res.getBody().toString(),RecommendInfo.class);
            if (info != null) {
                if (info.getText_type() == 0)
                    activity.goneLoading();
                updateUIDate(info);
                canRecommend = true;
            }
        }

    }

    /**
     * 拒绝
     */
    public void refuseRecommend(){
        if (canRecommend) {
            if (recommendInfo.getAudit_state() != RecommendInfo.RECOMMEND_STATE_REFUSED) {

                CicleAuditRequest request = new CicleAuditRequest(HttpCommon.CIRCLE_AUDIT_REQEUST,this);
                request.addParams(recommend_id, recommend_type, AUDIT_REFUSE);
                mCMainHttp.doRequest(request);

               // http.auditRecommend(recommend_id, recommend_type, AUDIT_REFUSE, token);
                temp_recommend_state = RecommendInfo.RECOMMEND_STATE_REFUSED;
            } else {
                unrefuseRecommend();
            }
        }
    }

    /**
     * 取消拒绝
     */
    public void unrefuseRecommend() {
        if (canRecommend) {
          //  http.auditRecommend(recommend_id, recommend_type, AUDIT_UNREFUSE, token);
            CicleAuditRequest request = new CicleAuditRequest(HttpCommon.CIRCLE_AUDIT_REQEUST,this);
            request.addParams(recommend_id, recommend_type, AUDIT_REFUSE);
            mCMainHttp.doRequest(request);
            temp_recommend_state = RecommendInfo.RECOMMEND_STATE_UNERCOMMEND;
        }
    }

    /**
     * 选入圈吧
     */
    public boolean toInterest() {
        if (canRecommend && (recommendInfo.getAudit_state() != RecommendInfo.RECOMMEND_STATE_INTEREST)) {
          //  http.auditRecommend(recommend_id, recommend_type, AUDIT_INTEREST, token);
            CicleAuditRequest request = new CicleAuditRequest(HttpCommon.CIRCLE_AUDIT_REQEUST,this);
            request.addParams(recommend_id, recommend_type, AUDIT_REFUSE);
            mCMainHttp.doRequest(request);
            temp_recommend_state = RecommendInfo.RECOMMEND_STATE_INTEREST;
            return true;
        } else {
            if (recommendInfo != null)
                onRecommendStateChangedListener.onRecommendStateChange(recommendInfo.getRecommend_id(), recommendInfo.getAudit_state());
            return false;
        }
    }

    /**
     * 选入精华区
     */
    public boolean toEnssence() {
        if (canRecommend && recommendInfo.getAudit_state() != RecommendInfo.RECOMMEND_STATE_ENSSENCE) {
          //  http.auditRecommend(recommend_id, recommend_type, AUDIT_ENSSENCE, token);
            CicleAuditRequest request = new CicleAuditRequest(HttpCommon.CIRCLE_AUDIT_REQEUST,this);
            request.addParams(recommend_id, recommend_type, AUDIT_REFUSE);
            mCMainHttp.doRequest(request);
            temp_recommend_state = RecommendInfo.RECOMMEND_STATE_ENSSENCE;
            return true;
        } else {
            if (recommendInfo != null)
                onRecommendStateChangedListener.onRecommendStateChange(recommendInfo.getRecommend_id(), recommendInfo.getAudit_state());
            return false;
        }
    }

    /**
     * 审批回调
     * @param res

     */
    public void auditRecommendSuccess(HttpJsonResponse res){

            int state = res.getBody().get("state").getAsInt();

            switch (state) {
                case 0:
                    //失败
                    SouYueToast.makeText(activity, "操作失败", SouYueToast.LENGTH_SHORT).show();
                    if (onRecommendStateChangedListener != null) {
                        onRecommendStateChangedListener.onRecommendStateChange(recommendInfo.getRecommend_id(),recommendInfo.getAudit_state());
                    }
                    break;
                case 1:
                    //成功
                    SouYueToast.makeText(activity, "操作成功", SouYueToast.LENGTH_SHORT).show();
                    if (onRecommendStateChangedListener != null) {
                        onRecommendStateChangedListener.onRecommendStateChange(recommendInfo.getRecommend_id(),temp_recommend_state);
                    }
                    recommendInfo.setAudit_state(temp_recommend_state);
                    break;
                case 2:
                    //非圈主操作
                    SouYueToast.makeText(activity, "非圈主操作", SouYueToast.LENGTH_SHORT).show();
                    if (onRecommendStateChangedListener != null) {
                        onRecommendStateChangedListener.onRecommendStateChange(recommendInfo.getRecommend_id(), recommendInfo.getAudit_state());
                    }
                    break;
                case 3:
                    //帖子不存在
                    SouYueToast.makeText(activity, "帖子不存在", SouYueToast.LENGTH_SHORT).show();
                    if (onRecommendStateChangedListener != null) {
                        onRecommendStateChangedListener.onRecommendStateChange(recommendInfo.getRecommend_id(), recommendInfo.getAudit_state());
                    }
                    break;
            }

    }

    private int height, width;
    private void updateUIDate(RecommendInfo recommendInfo) {
        this.recommendInfo = recommendInfo;
        if (!"".equals(recommendInfo.getTitle())&& recommendInfo.getTitle()!=null)
            title.setText(recommendInfo.getTitle());
        else
            title.setVisibility(View.GONE);
        time.setText(StringUtils.convertDate(recommendInfo.getCreate_time()+""));
        //如果是系统推荐，则不显示用户昵称
        if (recommend_type == CircleInboxActivity.RECOMMEND_TYPE_SYS) {
            recommendInfo.setNickname("");
        }

        nickname.setText(recommendInfo.getNickname() == null? "" :recommendInfo.getNickname());
        srp.setText(recommendInfo.getSrp_word());

        srp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSrpActivity(RecommendInfoFragment.this.recommendInfo.getSrp_word(), RecommendInfoFragment.this.recommendInfo.getSrp_id());
            }
        });

        if (recommendInfo.getText_type() == 0){
            webView.setVisibility(View.GONE);
            text.setVisibility(View.VISIBLE);
            text.setText(recommendInfo.getContent());
        } else {
            text.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            title.setVisibility(View.GONE);

            String toBeReadUrl = UrlConfig.HOST + recommendInfo.getUrl();
//            http.readNewsDetail(toBeReadUrl + "&fontSize=" + SettingsManager.getInstance().getFontSize() + "&hasPic=" + (SettingsManager.getInstance().isLoadImage() ? "1" : "0"),
//                    containsUGC(toBeReadUrl) ? true : false);
            CircleReadNewsDetailReq req = new CircleReadNewsDetailReq(HttpCommon.CIRCLE_READNEWSDETAIL_REQUESTID,toBeReadUrl + "&fontSize=" + SettingsManager.getInstance().getFontSize() + "&hasPic=" + (SettingsManager.getInstance().isLoadImage() ? "1" : "0"),this);
            req.setRefresh(containsUGC(toBeReadUrl));
            mCMainHttp.doRequest(req);
        }


        final List<String> images = recommendInfo.getImages();

        DisplayMetrics dm = getResources().getDisplayMetrics();
        final int w = 200, h = 110;
        height = dm.widthPixels - 40;
        width = height;
        if (recommendInfo.getText_type() == 0 && images != null && images.size() > 0) {
            for (int i = 0; i < images.size(); i++) {
                String imgUrl = images.get(i).replace("!ios", "").replace("!android", "");
                ImageView item = new ImageView(getActivity());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -1);
                params.setMargins(20, 20, 20, 0);
                params.height = dm.widthPixels - 40;
                params.width = dm.widthPixels - 40;
                item.setLayoutParams(params);
                item.setScaleType(ImageView.ScaleType.CENTER_CROP);
                item.setBackgroundResource(R.drawable.ent_image_default);
                if (!TextUtils.isEmpty(imgUrl)) {
                   //aQuery.id(item).image(  Utils.getImageUrl(imgUrl), true, true, width, 0, null, AQuery.FADE_IN);
                    PhotoUtils.showCard(PhotoUtils.UriType.HTTP,  Utils.getImageUrl(imgUrl),item);
                }
                final int pos = i;
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), TouchGalleryActivity.class);
                        TouchGallerySerializable tg = new TouchGallerySerializable();
                        tg.setItems(images);
                        tg.setClickIndex(pos);
                        Bundle extras = new Bundle();
                        extras.putSerializable("touchGalleryItems", tg);
                        intent.putExtras(extras);
                        getActivity().startActivity(intent);
                    }
                });

                ll_images.addView(item);
            }
        }
        if (canInitState) {
            initState();
        }

    }

    private boolean containsUGC(String url) {
        if (!TextUtils.isEmpty(url))
            return url.toLowerCase().contains("ugc.groovy");
        return false;
    }

    public void readNewsDetailSuccess(String str) {
        activity.goneLoading();
        if (webView != null) {
            webView.loadDataWithBaseURL(UrlConfig.HOST, str, "text/html", "utf-8", null);
        }
    }


    public void initState(){
        if (onRecommendStateChangedListener != null && recommendInfo!= null) {
            onRecommendStateChangedListener.onRecommendStateChange(recommendInfo.getRecommend_id() ,recommendInfo.getAudit_state());
        } else if (recommendInfo == null){
            canInitState = true;
        }

    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus status) {
//
//        if ("getUserRecommend".equals(methodName)) {
//            activity.showNetError();
//        }
//    }

    @Override
    public void onHttpResponse(IRequest request) {

        switch (request.getmId()){
            case HttpCommon.CIRCLE_SYSRECOMMEND_REQUEST:
                HttpJsonResponse response1 = request.getResponse();

                onResult(response1);
                break;

            case HttpCommon.CIRCLE_AUDIT_REQEUST:
                HttpJsonResponse response2 = request.getResponse();

                auditRecommendSuccess(response2);
                break;
            case HttpCommon.CIRCLE_GETUSERRECOMMENDINFO_REQUESTID:
                HttpJsonResponse response3 = request.getResponse();

                getUserRecommendSuccess(response3);
                break;
            case HttpCommon.CIRCLE_READNEWSDETAIL_REQUESTID:
                readNewsDetailSuccess(request.<String>getResponse());
                break;

        }
    }



    @Override
    public void onHttpError(IRequest request) {
         switch (request.getmId()){
             case HttpCommon.CIRCLE_SYSRECOMMEND_REQUEST:
                 activity.showNetError();
                 //TODO 加载错误
                 canRecommend = false;
                 break;
             case HttpCommon.CIRCLE_AUDIT_REQEUST:
                 if (onRecommendStateChangedListener != null) {
                     onRecommendStateChangedListener.onRecommendStateChange(recommendInfo.getRecommend_id() ,recommendInfo.getAudit_state());
                 }
                 SouYueToast.makeText(getActivity(), "审核失败", SouYueToast.LENGTH_SHORT).show();
                 break;
             case HttpCommon.CIRCLE_GETUSERRECOMMENDINFO_REQUESTID:
                 activity.showNetError();
                 break;
         }
    }

    @Override
    public void onHttpStart(IRequest request) {

    }

    /**
     * 审批状态发生改变的监听器
     */
    public static interface OnRecommendStateChangedListener {

        /**
         * 审批状态发生改变时调用
         * @param state 当前的审批状态
         */
        public void onRecommendStateChange(long recommend_id, int state) ;
    }

    public void reLoad(){
        if (recommend_type == CircleInboxActivity.RECOMMEND_TYPE_USER) {
//            http.getUserRecommend(recommend_id);
            CircleGetUserRecommendInfoRequest.send(HttpCommon.CIRCLE_GETUSERRECOMMENDINFO_REQUESTID,this,recommend_id);
        } else {
          // http.getSysRecommend(recommend_id);
            CircleRecomendRequest request = new CircleRecomendRequest(HttpCommon.CIRCLE_SYSRECOMMEND_REQUEST,this);
            request.addParams(recommend_id);
            mCMainHttp.doRequest(request);
        }

        canRecommend = false;
    }
    private void openSrpActivity(String srpWord, String srpId){
        Intent intent = new Intent(getActivity(), SRPActivity.class);
        intent.putExtra("keyword", srpWord);
        intent.putExtra("srpId", srpId);
        startActivity(intent);
    }
    public boolean needLoading(){
        return !canRecommend;
    }


    // 设置WebView拦截里面的图片链接和查看原文
    private void setWebViewClient() {
        webView.getSettings().setUseWideViewPort(false);
        webView.setImagesListener(this);
        webView.setWebViewClient(new WebViewClient() {

            /*
             * @Override public void onPageFinished(WebView view, String url) {
             * super.onPageFinished(view, url); }
             */
            public void onReceivedSslError(WebView view, android.webkit.SslErrorHandler handler, android.net.http.SslError error) {
                handler.proceed();
            };

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //toSourcePage(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!TextUtils.isEmpty(url)) {
                    if (url.toLowerCase().startsWith("showimage")) {
                        int imagepos = 0;
                        try {
                            imagepos = Integer.parseInt(url.substring(url.lastIndexOf("//") + 2, url.length()));

                            if (null == imageUrls || imageUrls.size() == 0)
                                imageUrls = recommendInfo.getImages();

                            if (imageUrls.size() == 1 && "".equals(imageUrls.get(0)))
                                imageUrls = recommendInfo.getImages();

                            if (imageUrls != null && imageUrls.size() > 0 && imagepos < imageUrls.size()) {
                                Intent intent = new Intent();
                                intent.setClass(getActivity(), TouchGalleryActivity.class);
                                TouchGallerySerializable tg = new TouchGallerySerializable();
                                tg.setItems(imageUrls);
                                tg.setClickIndex(imagepos);
                                Bundle extras = new Bundle();
                                extras.putSerializable("touchGalleryItems", tg);
                                intent.putExtras(extras);
                                startActivity(intent);
                            }
                        } catch (Exception e) {

                        }
                    } else {// 点击查看原文
                        Intent webViewIntent = new Intent();
                        webViewIntent.setClass(getActivity(), WebSrcViewActivity.class);
                        webViewIntent.putExtra(WebSrcViewActivity.PAGE_URL, url);
                        startActivity(webViewIntent);
                    }
                }
                // 处理内部点击url拦截 在自定义webview中打开，指向jianxing－fan
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });
    }

    @Override
    public void setImages(String images) {
        if (null != images) {
            imageUrls = Arrays.asList(images.trim().split(" "));
            Log.i("", "imageUrls size: " + imageUrls.size());
        }
    }
}
