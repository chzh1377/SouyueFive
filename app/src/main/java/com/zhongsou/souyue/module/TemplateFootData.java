package com.zhongsou.souyue.module;

/**
 * @author YanBin
 * @version V1.0
 * @project trunk
 * @Description Class Description
 * @date 2016/3/31
 */
public class TemplateFootData {
//    body: {
//        subHtml: " <div class="quan_boxbg"> <img src="/d3api2/gtpl/detail/images/quan_bg.jpg" class="quan_bg"> <div class="quan_nr"> <ul class="quan_info"> <li class="quan_logo"><img src="http://souyue-xqq.b0.upaiyun.com/Interest/1508/1915/54/11439970862.gif"></li> <li class="quan_title"> <h3>逗比圈</h3> <p>逗比圈（兴趣圈）逗比圈（兴趣圈）逗比圈（兴趣圈）逗比圈（兴趣圈）</p> </li> <a href="javascript:void(0)" class="quan_btn1" data-interest=1800 data-srpID=8843d6330fb84a5205647f42f6afe853 isSub=1 data-type=0>订阅</a> </ul> </div> </div> ",
//        xgtjHtml: " <section class="sy_sct_a"> <h3 class="div_lst_bt">相关推荐</h3> <div class="div_lst"> <ul> <li onclick="javascript:openBlog('1');"><a>ttt1</a></li><li onclick="javascript:openBlog('2');"><a>ttt2</a></li><li onclick="javascript:openBlog('3');"><a>ttt3</a></li> </ul> </div></section>",
//                dytjHtml: "<section class="sy_sct_b "><h3 class="div_lst_bt">订阅推荐</h3><div class="quan_box"><ul class="quan_info"><li class="quan_logo"><img src=interestLogo1 ></li><li class="quan_title"><h3>interestName1</h3><p class="txt">interestDesc</p></li><a href="javascript:void(0)" class="quan_btn1" data-interest=interestId1 data-srpId=srpId1 isSub=1>订阅</a></ul></div></section>",
//                rwgxHtml: " <div class="person_rwcon"><div class="person_rwcon_bt"><span>深扒之keyword</span></div><ul class="clearfix"><li data-srpId='srpId1' data-keyword='name1' class='rengwu'><a href="javascript:void(0)"><span><img src="image1"></span><i>name1</i><em>ship</em></a></li></ul></div>",
//    }

    private String subHtml = ""; //订阅
    private String xgtjHtml = "";    //相关推荐
    private String dytjHtml = "";    //订阅推荐
    private String rwgxHtml = "";
    private String fxxgHtml = "";    //分享相关

    private TemplateCommentsInfo commentsInfo;

    public String getSubHtml() {
        return subHtml;
    }

    public void setSubHtml(String subHtml) {
        this.subHtml = subHtml;
    }

    public String getXgtjHtml() {
        return xgtjHtml;
    }

    public void setXgtjHtml(String xgtjHtml) {
        this.xgtjHtml = xgtjHtml;
    }

    public String getDytjHtml() {
        return dytjHtml;
    }

    public void setDytjHtml(String dytjHtml) {
        this.dytjHtml = dytjHtml;
    }

    public String getRwgxHtml() {
        return rwgxHtml;
    }

    public TemplateCommentsInfo getCommentsInfo() {
        return commentsInfo;
    }

    public void setCommentsInfo(TemplateCommentsInfo commentsInfo) {
        this.commentsInfo = commentsInfo;
    }

    public void setRwgxHtml(String rwgxHtml) {
        this.rwgxHtml = rwgxHtml;
    }

    public String getFxxgHtml() {
        return fxxgHtml;
    }

    public void setFxxgHtml(String fxxg) {
        this.fxxgHtml = fxxg;
    }


//    订阅源
//    人物关系
//    分享得红包
//    广告     --去掉--
//    相关推荐
//    订阅推荐


    public String getHtmlString() {
        String result = (getSubHtml().trim()
                + getRwgxHtml().trim()
                + getFxxgHtml().trim()
                + getXgtjHtml().trim()
                + getDytjHtml().trim() );
        result = result.replaceAll("\n","");    //替换掉换行符，换行符引起兼容性问题
        return result;
    }
}
