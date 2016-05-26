package com.zhongsou.souyue.pay;

public class Constant
{
//	public final static String server_url = "https://msp.alipay.com/x.htm";
	//	// 商户（RSA）私钥
	//	public static final String RSA_PRIVATE = "123123123";
	//	// 支付宝（RSA）公钥 用签约支付宝账号登录ms.alipay.com后，在密钥管理页面获取。
	//	public static final String RSA_ALIPAY_PUBLIC = "123123";
		// 支付宝安全支付服务apk的名称，必须与assets目录下的apk名称一致
//	public static final String ALIPAY_PLUGIN_NAME = "alipay_plugin.apk";

	public static final int SDK_PAY_FLAG = 1;	//支付宝发送支付Handler的what属性
	public static final int SDK_CHECK_FLAG = 2;	//支付宝发送检查账号Handler的what属性
	
	final class AlixDefine
	{
//		public static final String IMEI 			= "imei";
//		public static final String IMSI 			= "imsi";
//		public static final String KEY 			= "key";
//		public static final String USER_AGENT	= "user_agent";
		public static final String VERSION		= "version";
//		public static final String DEVICE		= "device";
//		public static final String SID			= "sid";
//		public static final String partner		= "partner";
		public static final String charset		= "charset";
//		public static final String sign_type		= "sign_type";
		public static final String sign			= "sign";
		
		
		public static final String URL		= "URL";
		public static final String split		= "&";
		
//		public static final String AlixPay	=  "AlixPay";

		public static final String action		=  "action";
//		public static final String actionUpdate=  "update";
		public static final String data		=  "data";
		public static final String platform	=  "platform";
	}
}