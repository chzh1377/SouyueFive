package com.zhongsou.cn.sdk.pay;

import com.zhongsou.souyue.net.UrlConfig;


public class HdbpayConfig {
	
	// 商户的私钥(正式环境)
	private static final String PRIVATE_KEY_RELEASE = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAg1pPG3qKnbqeqIp7mwFop67K7MGwiPr7RWCyRkCUTEtJbonJKY9XtC8G4A7ylAe2OWgDWJOpSoXaAT8rrY4rwQIDAQABAkAoWohPItiPGJRBpznOJyKl0VdaYS8BBkpHyCs81f45ab3fiKOYS5WQf4ZN7LgI5ARU748/9pR+Rbs3GBLQR7+ZAiEA1Ja3BHHPY2SNF9ny/DPPlqp9752LcGX+J/VQQh4lGF8CIQCeLOeBMLQbHZlAgeM6vFMdLmKI6pZxvhPevcwMrIuv3wIhAJejRbqy36ZbA9VejlbdMhabWCdB7kNnTQn9jpkK3ZTrAiEAjZPb00LosqyXk6mu3c/bBO+TusFez4g2+NQhY+a9UTkCIHZ4x3Yl+crcP9KNzsp99cHjOU24CIVGhqiroaXaBkhm";
	private static final String URL_RELEASE="https://www.zshdb.cn/m_pay/pay_view?";
	
	//私钥(测试环境)
	private static final String PRIVATE_KEY_DEBUG = "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBALhtZxe0Z7iCHBzkckAl37alEq2NHk3oKYsTyUa9BEgBIXYPd8GOnSxNxKNGC8baitumfQPIQu4WqHwfhpjz1F79JexoMoYfw5KyFAfBkr18ClS/tpVuOWkUPYANhvmAn8MuAe72X+7zvaoHRO73CRBgB22hJUpd85gqhImmQvKJAgMBAAECgYEApo7PQx4/aee/YeyCriJGRxoPo0NGMg/QrPTimwsLecfjo07W1axpj+BwDRCuhBlMvJBAaREMOR+na+nA0hOrH9BcchwD9jrQB0qLq99qhNN7AoWMhqtOeywSHXGHQogwct9nmlFnvRVh7Hd+/sSETrGs4MJAs5wGcceGl44v0TECQQDsYJVU7sZw00ryDUWpvBAoMkPvftrsA2Aes77tWG2gCJY7lmO2V488MJQRjXVvi9ix7alS3UrzPAe7Dz7XuwtdAkEAx7zLshRqjeWOtl9fYZ8qWpiyMhBXdwRA20lp6ZSyNdQrmjir2GlwiHvDgne/02JnepACYJIiLiZmczg0vXu9HQJBAMlbAPpnqE0mAFqu6Z/MHutO3kYehrizq2ab8VFXmZLQncFGu8vxTIeWThuhp98Mftwaurlm3tjSUzeKphq5L9UCQQCLq5W1HVm1iATXurF0Dl0LpSNWpt7CtxKzRQ7u67ACvC8RVPe2CWVLy1/X5+0X4AQETJec+BM2uluBzpfyuIhxAkEAmr1vlVNVyEvi5ulsoqIShUfdRQflWDGaaT8PA+tyVjzOA8CNAcFlk10jj8aU6KPCZEVVrZFB967cykvklGL17A==";
	private static final String URL_DEBUG="http://zzpaytest.zhongsou.com/m_pay/pay_view?";
	
	// 惠多宝的公钥，无需修改该值
	public static String hdb_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCT9b6PDbOjpinmTpwSdCv8mM7MsfEfo29xmwaBD6MgrvEDQd8DvPVEcLDIvGoQj3fv2tyzQcXZhwSSl+LzDNbhh8KuZne+CIjPzrJqtewcVi8DumtQ8jN8qsfQys7Yg8oAGrI4MTtnQxv4JhSGa8KxUDq1hkOjrsiKTLS1KhoIKQIDAQAB";
	
	public static final String private_key = getHdbPrivateKey();
	public static final String url=getHdbPayUrl();
	
	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String input_charset = "utf-8";

	// 签名方式 不需修改
	public static String sign_type = "RSA";
	// 云商城卖场ID
	public static String zs_mall_id = "";
	// 云商城店铺ID
	public static String zs_shop_id = "";
	
	private static  String getHdbPrivateKey() {
        switch (UrlConfig.SOUYUE_SERVICE) {
            case UrlConfig.SOUYUE_TEST:
                return PRIVATE_KEY_DEBUG;
            case UrlConfig.SOUYUE_PRE_ONLINE:
                return PRIVATE_KEY_RELEASE;
            case UrlConfig.SOUYUE_ONLINE:
                return PRIVATE_KEY_RELEASE;
            default:
                return PRIVATE_KEY_RELEASE;
        }
    }
	
	private static  String getHdbPayUrl() {
        switch (UrlConfig.SOUYUE_SERVICE) {
            case UrlConfig.SOUYUE_TEST:
                return URL_DEBUG;
            case UrlConfig.SOUYUE_PRE_ONLINE:
                return URL_RELEASE;
            case UrlConfig.SOUYUE_ONLINE:
                return URL_RELEASE;
            default:
                return URL_RELEASE;
        }
    }
}
