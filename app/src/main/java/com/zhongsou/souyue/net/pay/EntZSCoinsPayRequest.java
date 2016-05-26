package com.zhongsou.souyue.net.pay;

import com.zhongsou.souyue.activity.ChargeActivity;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.EntBaseRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

import java.util.HashMap;

/**
 * 中搜币充值接口
 */
public class EntZSCoinsPayRequest extends EntBaseRequest {


    public EntZSCoinsPayRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    /**
     * 充值通道
     */

//    public static final String ZSB_ALIPAY = "zsb.alimobilepay"; // 中搜币充值(支付宝)
    public static final String ZSB_ALIPAY = "zsb.pay"; // 中搜币充值(支付宝)
    public static final String ZSB_UPPAY = "zsb.upmpPay"; // 中搜币充值(银联)
    public static final String ZSB_HDBPAY = "zsb.hdbpay"; // 中搜币充值(惠多宝支付)

    @Override
    public int getMethod() {
        return REQUEST_METHOD_GET;
    }

    @Override
    public String getUrl() {
        return API_URL;
    }


    /**
     * * 获取中搜币充值payUrl(支付宝)
     *
     * @param b       冲中搜币的个数
     * @param t       类型，后台用的，与中搜币个数对应的字典表
     * @param payType 支付类型，1：支付宝，2：银联
     */
    public void setParams(User user, int t, int b, int payType) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("sy_user_id", user.userId());
        params.put("sy_user_name", user.userName());
        params.put("t", t);
        params.put("b", b);
        String url = "";
        if (payType == ChargeActivity.PAY_TYPE_ALIPAY) {
            url = ZSB_ALIPAY;
        }
        if (payType == ChargeActivity.PAY_TYPE_YINLIAN) {
            url = ZSB_UPPAY;
        }
        if (payType == ChargeActivity.PAY_TYPE_HDB) {
            url = ZSB_HDBPAY;
        }
        String param = encodeParams(params);
        param = param.replace("\n", "");
        addParams("m", url);
        addParams("p", param);
    }

    /**
     * 充值接口
     *
     * @param id
     * @param resp
     * @param user
     * @param t
     * @param b
     * @param payType
     */
    public static void send(int id, IVolleyResponse resp, User user, int t, int b, int payType) {
        EntZSCoinsPayRequest request = new EntZSCoinsPayRequest(id, resp);
        request.setParams(user, t, b, payType);
        CMainHttp.getInstance().doRequest(request);
    }
}
