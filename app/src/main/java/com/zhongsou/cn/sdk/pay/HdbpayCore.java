package com.zhongsou.cn.sdk.pay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/* *
 *类名：HdbpayFunction
 *功能：惠多宝接口公用函数类
 *详细：该类是请求、通知返回两个文件所调用的公用函数核心处理文件，不需要修改
 *版本：1.0
 *日期：2015-08-10
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究惠多宝接口使用，只是提供一个参考。
 */

public class HdbpayCore {

    /** 
     * 除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }

    /** 
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }
    
    

	 /**
    * 生成签名结果
    * @param sPara 要签名的数组
    * @return 签名结果字符串
    */
	public static String buildRequestMysign(Map<String, String> sPara,String private_key) {
   	String prestr = HdbpayCore.createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
       String mysign = "";
       if(HdbpayConfig.sign_type.equals("RSA") ){
       	mysign = RSA.sign(prestr, private_key, HdbpayConfig.input_charset);
       }
       return mysign;
   }
	
   /**
    * 生成要请求给惠多宝的参数数组
    * @param sParaTemp 请求前的参数数组
    * @return 要请求的参数数组
    */
	public static Map<String, String> buildRequestPara(Map<String, String> sParaTemp,String private_key) {
       //除去数组中的空值和签名参数
       Map<String, String> sPara = HdbpayCore.paraFilter(sParaTemp);
       //生成签名结果
       String mysign = buildRequestMysign(sPara,private_key);

       //签名结果与签名方式加入请求提交参数组中
       try {
		sPara.put("sign", URLEncoder.encode(mysign,HdbpayConfig.input_charset ));
	 } catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       sPara.put("sign_type", HdbpayConfig.sign_type);

       return sPara;
   }

 
}

