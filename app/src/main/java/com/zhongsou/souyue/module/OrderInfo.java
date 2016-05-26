package com.zhongsou.souyue.module;

import com.zhongsou.souyue.DontObfuscateInterface;

/**
 * User: liyc
 * Date: 14-1-16
 * Time: 下午3:12
 */
public class OrderInfo implements DontObfuscateInterface {

    private String payUrl;  //支付宝

    private String tn;      //银联

    private String mode;    //测试，正式切换

    //惠多宝
    private String partner = "";
    private String seller_id = ""; //收款账号
    private String out_trade_no = ""; //订单ID
    private String subject = "";	//商品标题
    private String body = "";	//商品描述
    private String total_fee = "";	//商品价格
    private String notify_url = "";	//服务器异步
    private String return_url = "";	//页面跳转同步通知页面路径
    private String appScheme = "";
    private String orderSpec = "";	//将商品信息拼接成字符串
    private String _input_charset = "";	//参数编码字符集
    private String it_b_pay = "";		//未付款交易超时时间
    private String payment_type = "";	//支付类型,仅支持1，商品购买
    private String app_id = "";			//客户端号

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public String getTn() {
        return tn;
    }

    public void setTn(String tn) {
        this.tn = tn;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getReturn_url() {
        return return_url;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
    }

    public String getAppScheme() {
        return appScheme;
    }

    public void setAppScheme(String appScheme) {
        this.appScheme = appScheme;
    }

    public String getOrderSpec() {
        return orderSpec;
    }

    public void setOrderSpec(String orderSpec) {
        this.orderSpec = orderSpec;
    }

    public String get_input_charset() {
        return _input_charset;
    }

    public void set_input_charset(String _input_charset) {
        this._input_charset = _input_charset;
    }

    public String getIt_b_pay() {
        return it_b_pay;
    }

    public void setIt_b_pay(String it_b_pay) {
        this.it_b_pay = it_b_pay;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }
}
