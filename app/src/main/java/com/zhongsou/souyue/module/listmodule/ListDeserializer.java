package com.zhongsou.souyue.module.listmodule;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * BaseListData解析器
 * Created by lvqiang on 15/12/23.
 */
public class ListDeserializer implements JsonDeserializer<BaseListData> {
    private Gson mGson;
    public ListDeserializer(){
        mGson = new Gson();
    }
    @Override
    public BaseListData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        int viewType = obj.get("viewType").getAsInt();
        int invokeType = obj.get("invokeType").getAsInt();
        return getBaseListData(obj,viewType,invokeType);
    }

    public BaseListData getBaseListData(JsonObject obj,int viewType,int invokeType){
        BaseListData data;
        switch (viewType){
            case BaseListData.view_Type_default:
            case BaseListData.view_Type_img_1:
            case BaseListData.view_Type_img_3:
            case BaseListData.view_Type_JOKE:
            case BaseListData.view_Type_video_one_Img:

                data = mGson.fromJson(obj,DefaultItemBean.class);
                break;
            case BaseListData.view_Type_img_b:
            case BaseListData.view_Type_img_f:
            case BaseListData.view_Type_video_0:
            case BaseListData.VIEW_TYPE_IMG_CAROUSEL:
                data = mGson.fromJson(obj,SigleBigImgBean.class);
                break;
            case BaseListData.view_Type_SPECIA:
                data = mGson.fromJson(obj,SpecialItemData.class);
                break;
            case BaseListData.view_Type_SEARCH_RESULT:
                data = mGson.fromJson(obj,SearchItemData.class);
                break;
            default:
                data = mGson.fromJson(obj,DefaultItemBean.class);
        }
        BaseInvoke baseInvoke = mGson.fromJson(obj,BaseInvoke.class);
        baseInvoke.setType(invokeType);
        data.setJsonResource(obj.toString());
        baseInvoke.setData(data);
        data.setInvoke(baseInvoke);
        return data;
    }
}
