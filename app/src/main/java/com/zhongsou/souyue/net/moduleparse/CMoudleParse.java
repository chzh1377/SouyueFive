package com.zhongsou.souyue.net.moduleparse;

import android.text.TextUtils;

import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.circle.activity.DetailModuleActivity;
import com.zhongsou.souyue.circle.util.StringUtils;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.TemplateUtils;
import com.zhongsou.souyue.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 模板相关
 * Created by lvqiang on 15/6/8.
 */
public class CMoudleParse extends ABaseParse {

//    public static final int INDEX_MODLE_NAME = 0;
//    public static final int INDEX_MODLE_VALUE = 1;
//    public static final int INDEX_MODLE_SIGNID = 2;
//    public static final int INDEX_MODLE_IMAGE = 3;
//    public static final int INDEX_MODLE_SHORT_URL = 4;
//    public static final int INDEX_MODLE_SHARE_IMG_URL = 5;
//    public static final int INDEX_MODLE_BRIEF = 6;
//    public static final int INDEX_MODLE_POSTING_STATE = 7;
//    public static final int INDEX_MODLE_POSTING_TITLE = 8;

    public static CMoudleParse mParser;

    public static CMoudleParse getInstance() {
        if (mParser == null) {
            mParser = new CMoudleParse();
        }
        return mParser;
    }

//    public Object parserModule(HttpJsonResponse http) throws Exception {
//
//
////        String content = new String(data,
////                HttpHeaderParser.parseCharset(headers, "utf-8"));
////        JsonObject res = JsonParser.parse(content).getAsJsonObject();
////        HttpJsonResponse http = new HttpJsonResponse(res);
//        if (http.getCode() != 200) {
//            return http;
//        }
//        String result = DETAIL_MODULE;
//        String module = Utils.getJsonValue(http.getBody(), "module", "mod_blog_001");
//        String v = getDetailModule(module);
//        if (!TextUtils.isEmpty(v)) {
//            result = v;
//        }
//        JsonObject json = http.getBody();
//        String title = Utils.getJsonValue(json, "title", "");
//        result = result.replace("${title}", title);
//        result = result.replace("${source}", Utils.getJsonValue(json, "source", ""));
//        result = result.replace("${text}", Utils.getJsonValue(json, "text", ""));
//        result = result.replace("${datetime}", Utils.getJsonValue(json, "datetime", ""));
//        result = result.replace("${url}", Utils.getJsonValue(json, "url", ""));
//        result = result.replace("${viewSource}", Utils.getJsonValue(json, "viewSource", ""));
//        result = result.replace("${aboutReadData}", Utils.getJsonValue(json, "aboutReadData", ""));
//        result = result.replace("${jsData}", Utils.getJsonValue(json, "jsData", ""));
//        result = result.replace("${isNewData}", Utils.getJsonValue(json, "isNewData", "false"));
////        "shortUrl": "http://i.souyue.mobi/u/tYfk",
////                "shareViewImgUrl": "http://souyue-image.b0.upaiyun.com/user/0008/21313292.jpg",
////                "shareBrief": "去向何方新浪体育讯北京时间6月18日，据RealGM报道，本赛季的NBA最佳第六人得主路易斯-威廉姆",
////                "posting_state": 0
//        String shortUrl = Utils.getJsonValue(json, "shortUrl", "");
//        String shareViewImgUrl = Utils.getJsonValue(json, "shareViewImgUrl", "");
//        String brief = Utils.getJsonValue(json, "shareBrief", "");
//        int postingState = Utils.getJsonValue(json, "posting_state", 0);
//        JsonArray array = Utils.getJsonArrayValue(json, "image");
//
//        String signid = Utils.getJsonValue(json, "signId", "");
//        List<Object> list = new ArrayList<Object>();
//        list.add(INDEX_MODLE_NAME, v);
//        list.add(INDEX_MODLE_VALUE, result);
//        list.add(INDEX_MODLE_SIGNID, signid);
//        list.add(INDEX_MODLE_IMAGE, array);
//        list.add(INDEX_MODLE_SHORT_URL, shortUrl);
//        list.add(INDEX_MODLE_SHARE_IMG_URL, shareViewImgUrl);
//        list.add(INDEX_MODLE_BRIEF, brief);
//        list.add(INDEX_MODLE_POSTING_STATE, postingState);
//        list.add(INDEX_MODLE_POSTING_TITLE, title);
//        return list;
//    }

    public String bindMapData(Map<String, String> map, int type) {
        String result;
        if (map == null) return null;
        String templateVersion = map.get("templateVersion");
        result = getTemplateString(templateVersion, type);

        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String templateName = "${" + entry.getKey() + "}";
            result = result.replace(templateName, entry.getValue());
        }
        return result;
    }

    /**
     * 获得未处理的模板的内容
     *
     * @param templateVersion 模板html所在的目录
     * @return
     */
    private String getTemplateString(String templateVersion, int type) {
        //      服务端格式：  "templateVersion": "detail_content_1"
        String templatePath = null;
        String result;
        if (StringUtils.isEmpty(templateVersion)) {   //选择默认模板文件
            templatePath = TemplateUtils.TEMPLATE_DEFAULT;
        } else {  //根据传入的属性选择模板文件
            templatePath = templateVersion;
//            templatePath = "detail_content_1";     //TODO test 由服务器传入 ,服务器只需要将templateVersion="detail_content_1"即可
        }

        String dir = TemplateUtils.getTemplatePath(MainApplication.getInstance()) + File.separator; //模板目录

        File file = new File(dir + templatePath);
        if(!file.exists()){
            templatePath = TemplateUtils.TEMPLATE_DEFAULT;
        }

        DetailModuleActivity.templatePath = templatePath;   //将当前使用的模板版本保存

        String htmlName = "";
        if(DetailModuleActivity.CIRCLE_TYPE_NEWS == type){
            htmlName = TemplateUtils.TEMPLATE_SRP;
        }else if(DetailModuleActivity.CIRCLE_TYPE_CIRCLE == type){
            htmlName = TemplateUtils.TEMPLATE_INTEREST;
        }

        String path = templatePath + File.separator + htmlName;   //相对路径
        result = getTempStringByPath(dir, path);

        return result;
    }
}
