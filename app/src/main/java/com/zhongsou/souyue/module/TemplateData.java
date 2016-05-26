package com.zhongsou.souyue.module;

import java.util.List;

/**
 * @author YanBin
 * @version V1.0
 * @project trunk
 * @Description 模板请求 返回数据实体
 * @date 2016/3/26
 */
public class TemplateData {
    //数据格式
//    body: {
//        showData: {},
//        notShowData: {},
//        widgetData: {}
//    }

    private TemplateShowData showData;
    private TemplateKeyData notShowData;
    private CWidgetHead widgetHead;
    private TemplateWidgetData widgetData;

    public TemplateShowData getShowData() {
        return showData;
    }

    public void setShowData(TemplateShowData showData) {
        this.showData = showData;
    }

    public TemplateKeyData getNotShowData() {
        return notShowData;
    }

    public void setNotShowData(TemplateKeyData notShowData) {
        this.notShowData = notShowData;
    }

    public TemplateWidgetData getWidgetData() {
        return widgetData;
    }

    public void setWidgetData(TemplateWidgetData widgetData) {
        this.widgetData = widgetData;
    }

    public CWidgetHead getWidgetHead() {
        return widgetHead;
    }

    public void setWidgetHead(CWidgetHead widgetHead) {
        this.widgetHead = widgetHead;
    }
}
