package com.zhongsou.souyue.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.LoginActivity;
import com.zhongsou.souyue.activity.PickerMethod;
import com.zhongsou.souyue.activity.SRPActivity;
import com.zhongsou.souyue.activity.SelfCreateActivity;
import com.zhongsou.souyue.content.IShareContentProvider;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.dialog.ShareToSouyueFriendsDialog;
import com.zhongsou.souyue.im.ac.IMShareActivity;
import com.zhongsou.souyue.im.module.ImShareNews;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.search.GetSrpIndexDataRequest;
import com.zhongsou.souyue.net.share.ShareAllPlat;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.share.ShareByWeixin;
import com.zhongsou.souyue.share.ShareMenuDialog;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.ui.webview.CustomWebView;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.*;
import com.zhongsou.souyue.uikit.LoginAlert;
import com.zhongsou.souyue.utils.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bob zhou on 14-9-2.
 * <p/>
 * 搜悦4.0 第三代srp优化，引入首页fragment
 */
public class IndexFragment extends SRPFragment 
        implements
        GotoSrpListener, GotoShareListener, 
        OnJSClickListener, GotoInterestListener, 
        ReadNovelDictionaryListener, ReadNovelContentListener, 
        GetLocalCookieListener, SetLocalCookieListener,IVolleyResponse,
        IShareContentProvider, PickerMethod {

    private Activity context;

    private CustomWebView webView;

    private ProgressBarHelper progress;

    private String srpId;

    private String keyword;

    private String latitude; // 纬度

    private String longitude; // 经度

    private String city; // 城市

    private int from; // 0:搜索页 1：订阅页

    private String userId;

    private List<Template> list;

    private String cachePath;

    private Map<String, String> templateMap = new HashMap<String, String>();

    private Map<String, File> templateFileMap = new HashMap<String, File>();

    int show_navigation;

    private JsonObject request;

    static final String TAG = "IndexFragment";

    boolean isNeedDownload = false;

    public static final String BASE_URL = "http://zhongsou.com/";

    public static final int IDSTRING_LENGTH = 20;

    public static final int HEADSTR_LENGTH = 30;

    /**
     * js接口引入的变量 start
     */
    private SearchResultItem sri;

    private Object object = new Object();

    private ProgressDialog progressDialog;

    private String utype;

    private Bitmap msharebitmap = null;


    private ShareContent content;
    private String SUPPER_SHARE_URL;
    private boolean isfreeTrial;
    private String sourcePageUrl;
    private long newsId = 0;
    /**
     * js接口引入的变量 end
     */

    private boolean inHomeOnClick = false;
    private boolean isFirst = false;

    public boolean isInHomeOnClick() {
        return inHomeOnClick;
    }

    public void setInHomeOnClick(boolean inHomeOnClick) {
        this.inHomeOnClick = inHomeOnClick;
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 100) {
//                Log.d("IndexFragment 拼装H5页面内容 begin: ", System.currentTimeMillis() + "");
                String html = getHtml();
//                Log.d("IndexFragment 拼装H5页面内容 end: ", System.currentTimeMillis() + "");

//                Log.d("IndexFragment 加载H5页面 begin: ", System.currentTimeMillis() + "");
                MakeCookie.synCookies(getActivity(), BASE_URL);
                if(webView != null && progress != null) {
                    webView.loadDataWithBaseURL(BASE_URL, html, "text/html", "utf-8", null);
                    progress.goneLoading();
                }
            }
            return false;
        }
    }) ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_srp_index, container, false);
        initUI(view);
        initParaData();
        if (getActivity().getExternalCacheDir() == null) {
            Toast.makeText(getActivity(), "页面无法加载, 请挂载sd卡，然后重试", Toast.LENGTH_SHORT).show();
        } else {
            cachePath = getActivity().getExternalCacheDir().getPath();
            reset();

            Log.d("IndexFragment JSON : ", System.currentTimeMillis() + "");
            GetSrpIndexDataRequest request = new GetSrpIndexDataRequest(HttpCommon.GET_SRP_INDEX_DATA_REQUEST_ID, this);
            request.setForceRefresh(true);
            request.setParams(srpId,
                    keyword,
                    latitude,
                    longitude,
                    city,
                    userId,
                    from);
            mMainHttp.doRequest(request);
        }
        isFirst = true;
        return view;
    }

    private void initUI(View view) {
        webView = (CustomWebView) view.findViewById(R.id.fragment_srp_index_webview);
//        webView.addJavascriptInterface(new EntJsInterface(), "ent");
        webView.setGotoSrpListener(this);
        webView.setGotoShareListener(this);
        webView.setOnJSClickListener(this);
        webView.setGotoInterestListener(this);
        webView.setReadNovelDictionaryListener(this);
        webView.setReadNovelContentListener(this);
//        webView.setDownLoadRadioListener(this);
//        webView.setDownLoadNoverListener(this);
        webView.getCookeiListener(this);
        webView.setCookeiListener(this);
        progress = new ProgressBarHelper(getActivity(), view.findViewById(R.id.ll_data_loading));
        progress.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
            @Override
            public void clickRefresh() {
                GetSrpIndexDataRequest request
                        = new GetSrpIndexDataRequest(HttpCommon.GET_SRP_INDEX_DATA_REQUEST_ID, IndexFragment.this);
                request.setForceRefresh(true);
                request.setParams(srpId,
                        keyword,
                        latitude,
                        longitude,
                        city,
                        userId,
                        from);
                mMainHttp.doRequest(request);
            }
        });

        WebViewClient wvc = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
//                Log.d("IndexFragment 加载H5页面 end: ", System.currentTimeMillis() + "");
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("mailto:") || url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        };
        webView.setWebViewClient(wvc);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirst = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isFirst){
        	webView.loadUrl("javascript:onPageFocus()");
        }
        isFirst = false;
    }
    private void reset() {
        isNeedDownload = false;
        templateMap = new HashMap<String, String>();
    }


    private void initParaData() {
        srpId = getArguments().getString("srpId");
        keyword = getArguments().getString("keyword");
        from = getArguments().getBoolean("isSearch") ? 0 : 1;
        userId = SYUserManager.getInstance().getUserId();
        city = SYSharedPreferences.getInstance().getString(
                SYSharedPreferences.KEY_CITY, "");
        latitude = SYSharedPreferences.getInstance().getString(
                SYSharedPreferences.KEY_LAT, "");
        longitude = SYSharedPreferences.getInstance().getString(
                SYSharedPreferences.KEY_LNG, "");
        context = this.getActivity();
    }

    public void getSrpIndexDataSuccess(HttpJsonResponse response) {

//        Log.d("IndexFragment 获取页面JSON数据 end: ", System.currentTimeMillis() + "");

        show_navigation = response.getBody().get("show_navigation").getAsInt();
        request = response.getBody().getAsJsonObject("request");
        JsonArray array = response.getBody().getAsJsonArray("template_series");
        if (array == null || array.size() == 0) {
            return;
        }
        list = new Gson().fromJson(array, new TypeToken<List<Template>>() {
        }.getType());
        try {
            getTemplates(list);
            if (!isNeedDownload) {
//                Log.d("IndexFragment 拼装H5页面内容 begin: ", System.currentTimeMillis() + "");
                String html = getHtml();
//                Log.d("IndexFragment 拼装H5页面内容 end: ", System.currentTimeMillis() + "");

//                Log.d("IndexFragment 加载H5页面 begin: ", System.currentTimeMillis() + "");
                MakeCookie.synCookies(getActivity(), BASE_URL);
                webView.loadDataWithBaseURL(BASE_URL, html, "text/html", "utf-8", null);
                progress.goneLoading();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取最终的html，用于webview展示
     *
     * @return
     */
    private String getHtml() {
        Map<String, Object> map = getBodyAndList();
        List<Template> templateList = (List<Template>) map.get("list");
        String json = getJson(templateList);
        String body = map.get("body").toString();
        return mergeToHtml(json, body);
    }

    /**
     * 获取模板内容,分成两种情况 1:本地有模板文件，则读取文件获取内容 2:本地没有模板文件，则开启线程下载内容
     *
     * @param initList
     */
    private void getTemplates(List<Template> initList) {
        if (CollectionUtils.isEmpty(initList)) {
            Toast.makeText(getActivity(), "response data list is null", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> downIds = new ArrayList<String>();
        for (Template template : initList) {
            String fileName = "srp_" + template.id + "_" + template.version + ".txt";
            String filePath = cachePath + "/" + fileName;
            File templateFile = new File(filePath);
            if (!templateFile.exists()) {
                downIds.add(template.id);
                templateFileMap.put(template.id, templateFile);
                isNeedDownload = true;
            } else {
                readTemplateFile(templateFile, template.id);
            }
        }
        if (CollectionUtils.isNotEmpty(downIds)) {
//            Log.d("IndexFragment 下载模版文件 begin: ", System.currentTimeMillis() + "");
            String idsStr = StringUtils.join(downIds, ",");
            new DownloadTemplateThread(idsStr).start();
        }
    }

    private void readTemplateFile(File templateFile, String id) {
        StringBuffer tmpl = new StringBuffer();
        try {
            FileInputStream fis = new FileInputStream(templateFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "utf-8"));
            String tempStr = "";
            while ((tempStr = reader.readLine()) != null) {
                tmpl.append(tempStr);
            }
            fis.close();
            templateMap.put(id, tmpl.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 组装body和 template list
     * <p/>
     * 组装规则是，当template的 print==0 时将模板内容赋给当前template 当template的 print==1
     * 时模板内容依次组装到body上
     *
     * @return 携带body和template list 的map
     */

    private Map<String, Object> getBodyAndList() {
        StringBuffer body = new StringBuffer();
        List<Template> templateList = new ArrayList<Template>();
        for (Template template : list) {
            String tmpl = templateMap.get(template.id);
            if (template.print == 0) {
                template.tmpl = tmpl;
            }
            if (template.print == 1) {
                body.append(tmpl);
            }
            templateList.add(template);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", templateList);
        map.put("body", body.toString());
        return map;
    }

    /**
     * 获取javascript 需要的json变量
     *
     * @param list 最终组装好的，template list
     * @return
     */
    private String getJson(List<Template> list) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("request", request);
        jsonObject.addProperty("show_navigation", show_navigation);
        jsonObject.add("template_series", new Gson().toJsonTree(list));
        return "var json=" + jsonObject.toString() + ";";
    }

    /**
     * 将json和body组装成最终给webview 展示的html
     *
     * @param json json
     * @param body body内容
     * @return
     */
    private String mergeToHtml(String json, String body) {
        StringBuffer html = new StringBuffer();
        html.append("<!DOCTYPE HTML>\n<html>\n<script type=\"text/javascript\">")
                .append(json)
                .append("</script>\n")
                .append(body)
                .append("</html>");

        return html.toString();
    }

    /**
     * 下载并保存文件
     *
     * @param url 下载文件的url地址
     *            /
     */
    private void downloadAndSaveFile(String url) {
        String downLoadStr = doGet(url);
//        Log.d("IndexFragment 下载模版文件 end: ", System.currentTimeMillis() + "");
//        Log.d("IndexFragment 存储模版文件 begin: ", System.currentTimeMillis() + "");
        if (StringUtils.isEmpty(downLoadStr)) {
            Log.d(TAG, "下载文件内容为空");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progress.showNetError();
                }
            });
            return;
        }
        subDownLoadContent(downLoadStr);

        handler.sendEmptyMessage(100);
    }

    private void saveTemplate(String content, File file) {
        if (StringUtils.isNotEmpty(content)) {
            FileOutputStream fos = null;
            try {
                file.createNewFile();
                fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
                BufferedWriter bw = new BufferedWriter(osw);
                bw.write(content.trim());
                bw.flush();
                bw.close();
                osw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "下载文件内容为空");
        }
    }


    /**
     * 此http请求因为比较特殊，故没有用项目封装 请求连接超时，和读取超时时间都是5秒
     *
     * @param urlStr
     * @return
     */
    public String doGet(String urlStr) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet(urlStr);
        try {
            httpClient.getParams().setParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            httpClient.getParams().setParameter(
                    CoreConnectionPNames.SO_TIMEOUT, 5000);
            HttpResponse resp = httpClient.execute(get);
            if (resp != null) {
                return EntityUtils.toString(resp.getEntity());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
        return null;
    }

    /**
     * 截取各个模板，需要根据协议递归截取
     */
    public void subDownLoadContent(String downLoadStr) {
        try {
            while (StringUtils.isNotEmpty(downLoadStr)) {
                String id = downLoadStr.substring(0, IDSTRING_LENGTH).trim();
                int length = Integer.parseInt(downLoadStr.substring(IDSTRING_LENGTH, HEADSTR_LENGTH).trim());
                String content = downLoadStr.substring(HEADSTR_LENGTH, HEADSTR_LENGTH + length);
                downLoadStr = downLoadStr.substring(HEADSTR_LENGTH + length);
                templateMap.put(id, content);
                saveTemplate(content, templateFileMap.get(id));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

//        Log.d("IndexFragment 存储模版文件 end: ", System.currentTimeMillis() + "");
    }


    @Override
    public void loadData() {

    }

    /**
     * js 接口引入的方法 start
     */
    @Override
    public void gotoSRP(String keyword, String srpId) {
        if (!StringUtils.isEmpty(keyword) && !StringUtils.isEmpty(srpId)) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), SRPActivity.class);
            intent.putExtra("keyword", keyword);
            intent.putExtra("srpId", srpId);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
    }

    @Override
    public void gotoShare() {
        Intent i = new Intent();
        if (SYUserManager.USER_ADMIN.equals(utype)) {
            i.setClass(getActivity(), SelfCreateActivity.class);
        } else {
            i.setClass(getActivity(), LoginActivity.class);
        }
        startActivity(i);
    }

    @Override
    public void gotoInterest(long interest_id) {
        IntentUtil.gotoSecretCricleCard(getActivity(), interest_id);
    }

    @Override
    public void onJSClick(JSClick jsc) {
        setInHomeOnClick(false);
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.stopLoading();
            }
        });
        try {
            //未抽取            
            toSRIObj(jsc);
            // 打开新页（抽取）{"category":"pasePage","description":"","image":"","url":"http://sports.syd.com.cn/system/2014/08/29/010473947.shtml","title":"新页抽取","srpId":"","phoneNumber":"","md5":"","keyword":"测试","isPasePage":true,"isRecharge":false,"isShare":false,"isShowimage":false,"isSrp":false,"isTel":false,"isWebView":false,"isGoLogin":false,"isExchange":false,"isComment":false,"isBrowser":false,"isAskfor":false,"index":0}
            // 打开M内新页（不抽取）{"category":"pasePage","description":"","image":"","url":"http://mtest.zhongsou.com/index#page_summary_detail?url\u003dhttp://sports.syd.com.cn/system/2014/08/29/010473947.shtml\u0026k\u003d李娜\u0026kid\u003d337842","title":"新页不抽取","srpId":"","phoneNumber":"","md5":"","keyword":"测试","isPasePage":true,"isRecharge":false,"isShare":false,"isShowimage":false,"isSrp":false,"isTel":false,"isWebView":false,"isGoLogin":false,"isExchange":false,"isComment":false,"isBrowser":false,"isAskfor":false,"index":0}
            // 打开M内新页（不抽取）{"category":"pasePage","description":"","image":"","url":"http://mtest.zhongsou.com","title":"新页不抽取","srpId":"","phoneNumber":"","md5":"","keyword":"测试","isPasePage":true,"isRecharge":false,"isShare":false,"isShowimage":false,"isSrp":false,"isTel":false,"isWebView":false,"isGoLogin":false,"isExchange":false,"isComment":false,"isBrowser":false,"isAskfor":false,"index":0}
            // 三方新页（不抽取）{"category":"original","description":"","image":"","url":"http://wapbaike.baidu.com/view/24211.htm?adapt\u003d1","title":"第三方页不抽取","srpId":"","phoneNumber":"","md5":"","keyword":"测试","isPasePage":false,"isRecharge":false,"isShare":false,"isShowimage":false,"isSrp":false,"isTel":false,"isWebView":false,"isGoLogin":false,"isExchange":false,"isComment":false,"isBrowser":false,"isAskfor":false,"index":0}

            //文件上传js测试
            // if("selectImg".equals(jsc.category())){
            // mWebView.loadUrl("javascript:"+jsc.getCallback()+"('"+"{\"imgWidth\":\"100\"}"+"')");
            // }
            
            //多出来一个分支            
            if (jsc.isSrp()) {
                if (keyword.equals(jsc.keyword()) || srpId.equals(jsc.srpId())) {
                    setInHomeOnClick(true);
                    sendBroadCastToSwitchWidget(sri.md5());
                } else {
                    toSrp();
                }
                return;
            }
  
            ImJump2SouyueUtil.IMAndWebJump(getActivity(),jsc,sri);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void toSRIObj(final JSClick jsc) {// 转换成SearchResultItem对象
        if (null == sri)
            sri = new SearchResultItem();
        sri.title_$eq(jsc.title());
        sri.keyword_$eq(jsc.keyword());
        sri.srpId_$eq(jsc.srpId());
        sri.url_$eq(jsc.url());
        sri.md5_$eq(jsc.md5());
        ArrayList<String> t = new ArrayList<String>();
        t.add(jsc.image());
        sri.image_$eq(t);
        sri.description_$eq(jsc.description());
        if (jsc.image() != null) {
            if (sri.image().get(0) != null && !sri.image().get(0).equals("")
                    && sri.image().get(0).length() > 1) {
                showProgress("加载中,请稍候...");
                ThreadPoolUtil.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            msharebitmap = null;
                            msharebitmap = SaveBitmap.getImage(sri.image().get(
                                    0));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Message message = new Message();
                        message.what = 98;
                        message.obj = msharebitmap;
                        handler.sendMessage(message);
                    }
                });
            } else {
                msharebitmap = null;
                if (jsc.isShare()) {
                    toShare();
                    return;
                }
            }
        }
    }

    private void toShare() {// 调用分享菜单
        if (StringUtils.isEmpty(sri.url())) {
            return;
        }
        if (StringUtils.isNotEmpty(sri.url())) {
//            http.shortURL(sri.url());
            // 短链接 为何没有回调？
//            ShortURLReq req = new ShortURLReq(HttpCommon.CIRLCE_SHORT_URL_ID,null);
//            req.setParams(sri.url());
//            CMainHttp.getInstance().doRequest(req);
        }
    }

    private void toSrp() {
        Intent intent = new Intent(getActivity(), SRPActivity.class);
        intent.putExtra("keyword", sri.keyword());
        intent.putExtra("srpId", sri.srpId());
        intent.putExtra("currentTitle", sri.title());
        intent.putExtra("md5", sri.md5());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public void sendBroadCastToSwitchWidget(String md5) {
        Intent data = new Intent();
        data.setAction(SRPActivity.SWITCH_WIDGET);
        data.putExtra("md5", md5);
        data.putExtra("backHome", isInHomeOnClick());
        getActivity().sendBroadcast(data);
    }

    public void showProgress(String message) {
        synchronized (object) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

//    @Override
//    public void downloadFiction(String id, String name, String img, String length, String url,String version) {
//        ImJump2SouyueUtil.downloadFiction(getActivity(),id,name,img,length,url,version);
//    }

//    @Override
//    public void downloadVideo(String id, String name, String img, String length, String urls) {
//        ImJump2SouyueUtil.downloadVideo(getActivity(),id,name,img,length,urls);
//    }

    @Override
    public String getFictionIndex(String novelId) {
        String str1 = ImJump2SouyueUtil.getIndex(novelId);
        Log.d("长度是：", str1.length() + "");
        return str1;
    }

    @Override
    public String getFictionContent(String novelId, int begin, int offset) {
        String str = ImJump2SouyueUtil.getContent(novelId, begin, offset);
        return str;
    }

    @Override
    public void getLocalCookie(String key) {
        String str = SYSharedPreferences.getInstance().getString("srp_" + key,
                "");
        try {
            webView.loadUrl("javascript:getLocalCookieCallback('"
                    + URLEncoder.encode(str, "utf-8").replace("+", "%20")
                    + "')");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLocalCookie(String key, String value) {
        SYSharedPreferences.getInstance().putString("srp_" + key, value);
    }

    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()){
            case HttpCommon.GET_SRP_INDEX_DATA_REQUEST_ID:
                getSrpIndexDataSuccess(request.<HttpJsonResponse>getResponse());
        }
    }


    /**
     * js 接口引入的方法 end
     */
    class DownloadTemplateThread extends Thread {
        String ids;

        DownloadTemplateThread(String ids) {
            this.ids = ids;
        }

        @Override
        public void run() {
            downloadAndSaveFile(UrlConfig.getSrpIndexTemplates + this.ids);
        }
    }


    class Template implements Serializable ,DontObfuscateInterface{
        String id;
        String version; // 版本
        int type; // 类型
        int print; // 打印出其中模板print属性值为1模板内容
        String url; // 下载模板的url
        JsonObject data; // 数据
        String tmpl; // 模板类容

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getPrint() {
            return print;
        }

        public void setPrint(int print) {
            this.print = print;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public JsonObject getData() {
            return data;
        }

        public void setData(JsonObject data) {
            this.data = data;
        }

        public String getTmpl() {
            return tmpl;
        }

        public void setTmpl(String tmpl) {
            this.tmpl = tmpl;
        }
    }

//    class EntJsInterface implements DontObfuscateInterface{
//
//        /**
//         * 跳转到商家首页接口 mall_id: 商家ID mall_name: 商家名称 mall_type: 商家类型 0-普通商家 1-合作商家
//         * city: 如果是普通商家，需要传入商家所在城市
//         */
//        @JavascriptInterface
//        public void redirectShopHome(String mall_idStr, String mall_name,
//                                     int mall_type, String city) {
//            long mall_id = Long.parseLong(mall_idStr);
//            if (mall_type == SearchShop.TYPE_INSIDE) {
//                UIHelper.showEntHomeFromSouyue(context, mall_name, mall_id, false);
//            } else {
//                SearchShop shop = new SearchShop();
//                shop.setSid(mall_id);
//                shop.setName(mall_name);
//                UIHelper.showCommonShopFromSouyue(context, city, shop);
//            }
//        }
//
//        /**
//         * 跳转到普通商家的分店列表接口 mall_id: 商家ID lng: 经度(当前位置) lat: 纬度（当前位置） city:
//         * 商家的所在城市
//         */
//        @JavascriptInterface
//        public void redirectShopList(String mall_idStr, Double lng, Double lat, String city) {
//            long mall_id = Long.parseLong(mall_idStr);
//            SearchParam searchParam = new SearchParam();
//            searchParam.setCity(city);
//            searchParam.setSid((int) mall_id);
//            searchParam.setLat(lat);
//            searchParam.setLng(lng);
//            UIHelper.showEntSearchSubbranch(context, searchParam);
//        }
//
//        /**
//         * 跳转到地图导航接口 mall_name: 商家名称 lng: 经度(商家位置) lat: 纬度(商家位置) address: 商家地址
//         */
//        @JavascriptInterface
//        public void redirectShopMap(String mall_name, String lng, String lat, String address) {
//            UIHelper.goToMapLocation(context, "", mall_name, Double.parseDouble(lng), Double.parseDouble(lat), address);
//        }
//    }

    @Override
    public void loadData(int args) {

        if (!Utils.isSDCardExist()) {
            Toast.makeText(MainApplication.getInstance(),
                    getString(R.string.sdcard_exist), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (CMainHttp.getInstance().isNetworkAvailable(getActivity())) {
            utype = SYUserManager.getInstance().getUserType();
            content = getShareContent();
            content.setKeyword(sri.keyword());
            content.setCallback(sri.callback());
            content.setSrpId(sri.srpId());
            Message msg = new Message();
            msg.obj = content;
            boolean islogin = (SYUserManager.getInstance().getUser().userType()
                    .equals(SYUserManager.USER_ADMIN));
            switch (args) {
                case ShareMenuDialog.SHARE_TO_SYIMFRIEND:
                    if (islogin) {
                        ImShareNews imsharenews = new ImShareNews(
                                content.getKeyword(), content.getSrpId(),
                                content.getTitle(), content.getUrl(),
                                content.getPicUrl());
//                        ContactsListActivity.startSYIMFriendAct(
//                                context, imsharenews);
                        IMShareActivity.startSYIMFriendAct(
                                context, imsharenews);
                    } else {
                        IntentUtil.toLogin(context);
                    }
                    break;
                case ShareMenuDialog.SHARE_TO_DIGEST:
                    if (null != utype && !utype.equals("1")) {
                        Bundle b = new Bundle();
                        b.putString("warningInfo",
                        		CommonStringsApi.SHARE_JHQ_WARNING);
                        context.showDialog(0, b);
                    } else {
                        // 登陆用户直接分享到精华区
                        shareToDigest();
                    }
                    break;
                case ShareMenuDialog.SHARE_TO_SINA:
                    msg.what = ShareMenuDialog.SHARE_TO_SINA;
                    handler.sendMessage(msg);
                    break;
                case ShareMenuDialog.SHARE_TO_WEIX:
                    ShareByWeixin.getInstance().share(content, false);
                    break;
                case ShareMenuDialog.SHARE_TO_FRIENDS:
                    ShareByWeixin.getInstance().share(content, true);
                    break;
                case ShareMenuDialog.SHARE_TO_SYFRIENDS:
                    isfreeTrial = SYUserManager.getInstance().getUser().freeTrial();
                    if (isfreeTrial) {
                        Dialog alertDialog = new AlertDialog.Builder(context)
                                .setMessage(getString(R.string.share_mianshen))
                                .setPositiveButton(
                                        getString(R.string.alert_assent),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                share2SYwangyou();
                                            }
                                        })
                                .setNegativeButton(
                                        getString(R.string.alert_cancel),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                dialog.dismiss();
                                            }
                                        }).create();
                        alertDialog.show();
                    } else {
                        share2SYwangyou();
                    }
                    break;
                case ShareMenuDialog.SHARE_TO_INTEREST:
                    LoginAlert loginDialog = new LoginAlert(
                            context,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    shareToInterest();
                                }
                            }, CommonStringsApi.SHARE_JHQ_WARNING, 0);
                    loginDialog.show();
                    break;
                default:
                    break;
            }
        } else {
            SouYueToast.makeText(context,
                    getString(R.string.nonetworkerror), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

    }

    private void share2SYwangyou() {
        Bundle b = new Bundle();
        if (null != utype && !utype.equals("1")) {
            b.putString("warningInfo",
                    getString(R.string.share_syfriend_warning));
            context.showDialog(1, b);
        } else {
            Intent i = new Intent();
            b.putSerializable("searchResultItem", sri);
            i.setClass(getActivity(),
                    ShareToSouyueFriendsDialog.class);
            i.putExtras(b);
            i.putExtra("content", content.getContent());
            startActivity(i);
        }
    }

    private void shareToInterest() {
        com.zhongsou.souyue.circle.model.ShareContent interestmodel = new com.zhongsou.souyue.circle.model.ShareContent();
        interestmodel.setTitle(sri.title());
        interestmodel.setImages(sri.image());
        interestmodel.setKeyword(sri.keyword());
        interestmodel.setSrpId(sri.srpId());
        interestmodel.setBrief(sri.description());
        interestmodel
                .setTextType(com.zhongsou.souyue.circle.model.ShareContent.TYPESOURCE);
        interestmodel.setNewsUrl(SUPPER_SHARE_URL != null ? SUPPER_SHARE_URL
                : ZSEncode.encodeURI(sourcePageUrl));
        com.zhongsou.souyue.circle.ui.UIHelper.shareToInterest(
                context, interestmodel);
    }

    // 分享到精华区
    private void shareToDigest() {
        if (newsId > 0){
            ShareAllPlat share = new ShareAllPlat(HttpCommon.SHARE_TO_PLATOM,this);
            share.setParams(newsId);
            mMainHttp.doRequest(share);
//            http.share(SYUserManager.getInstance().getToken(), newsId);
        }else {
            ShareAllPlat share = new ShareAllPlat(HttpCommon.SHARE_TO_PLATOM,this);
            share.setParams(sourcePageUrl, StringUtils.shareTitle(sri.title(),
                            sri.description()), null != sri.image()
                            && sri.image().size() > 0 ? sri.image().get(0) : "", sri
                            .description(), pubDate() + "", sri.source(),
                    sri.keyword(), sri.srpId());
            mMainHttp.doRequest(share);
//            http.share(SYUserManager.getInstance().getToken(), sourcePageUrl, StringUtils.shareTitle(sri.title(),
//                            sri.description()), null != sri.image()
//                            && sri.image().size() > 0 ? sri.image().get(0) : "", sri
//                            .description(), pubDate() + "", sri.source(),
//                    sri.keyword(), sri.srpId());
        }

    }

    private Long pubDate() {
        long pubDate = 0;
        try {
            pubDate = Long.parseLong(sri.date());
        } catch (Exception e) {
        }
        return pubDate;
    }

    @Override
    public ShareContent getShareContent() {
        Bitmap imageBitmap = null;
        String str = sri.image() != null && sri.image().size() > 0 ? sri
                .image().get(0) : "";
        if (!TextUtils.isEmpty(str)) {
//            AQuery query = new AQuery(context);
//            imageBitmap = query.getCachedImage(sri.image().get(0));
            File fileImage = PhotoUtils.getImageLoader().getDiskCache().get(sri.image().get(0));
            if(fileImage != null){
                imageBitmap = BitmapFactory.decodeFile(fileImage.getAbsolutePath());
            }
        }
        ShareContent shareContent = null;
        shareContent = new ShareContent(StringUtils.shareTitle(sri.title(),
                sri.description()), SUPPER_SHARE_URL != null ? SUPPER_SHARE_URL
                : ZSEncode.encodeURI(sourcePageUrl), imageBitmap,
                StringUtils.shareDesc(sri.description()), str);
        return shareContent;
    }

    @Override
    public void onHttpError(IRequest request) {
        switch (request.getmId()){
            case HttpCommon.SHARE_TO_PLATOM:
                SouYueToast.makeText(getActivity(), R.string.share_fail,
                        SouYueToast.LENGTH_SHORT).show();
                break;
            default:
                progress.showNetError();
                if(getActivity() != null) {
                	Toast.makeText(getActivity(), "服务器内部错误", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
	public void onDestroy() {
		if (webView != null) {
			final ViewGroup viewGroup = (ViewGroup) webView.getParent();
			if (viewGroup != null) {
				viewGroup.removeView(webView);
				viewGroup.removeAllViews();
			}
			webView.destroy();
			webView = null;
		}
		super.onDestroy();
	}
}
