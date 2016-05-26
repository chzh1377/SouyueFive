package com.zhongsou.souyue.module;

import java.util.List;

/**
 * @author YanBin
 * @version V1.0
 * @project trunk
 * @Description widgetData对象实体
 * @date 2016/3/26
 */
public class TemplateWidgetData {
    //    widgetData: {
//        nav: [],
//        isMenuShow: true
//    }
    private List<TemplateWidgetNavData> nav;
    private boolean isMenuShow;

    public List<TemplateWidgetNavData> getNavDataList() {
        return nav;
    }

    public void setNavDataList(List<TemplateWidgetNavData> navDataList) {
        this.nav = navDataList;
    }

    public boolean isMenuShow() {
        return isMenuShow;
    }

    public void setIsMenuShow(boolean isMenuShow) {
        this.isMenuShow = isMenuShow;
    }
}
