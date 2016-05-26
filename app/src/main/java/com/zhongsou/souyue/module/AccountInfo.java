package com.zhongsou.souyue.module;

import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;

public class AccountInfo implements DontObfuscateInterface{
	public static final String LOGIN_PREF = "souyue";
	
	public static enum THIRDTYPE{//第三方类型
		SINA_WEIBO, 
		TECENT_WEIBO; 
		//去掉人人网绑定
		//RENREN;
	}

	public static enum ACTION{
		BOUND, UNBOUND;
	}
	public interface BoundListener{
		void onBoundSuccess();
	}
	
	public int logo;//logo图片资源id
	public int thirdName;//第三方名称id
	public String thirdAccountName;//第三方用户名
	public THIRDTYPE type;
	public ACTION action = ACTION.BOUND;
	public String loginToken;//账号的唯一标识
	
	public AccountInfo(int logo, int thirdName, THIRDTYPE type) {
		super();
		this.logo = logo;
		this.thirdName = thirdName;
		this.type = type;
	}
	
//	public static List<AccountInfo> getAccounts(Context ctx){
//		List<AccountInfo> accounts = new ArrayList<AccountInfo>();
//		//新浪微博
//		AccountInfo sina = new AccountInfo(R.drawable.ic_sina_icon, R.string.loginActivity_sinaweibo, THIRDTYPE.SINA_WEIBO);
//		Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(ctx);
//		if(token.isSessionValid()){
//			sina.loginToken = token.getUid();
//			sina.action = ACTION.UNBOUND;
//			sina.thirdAccountName = getThirdLoginName(THIRDTYPE.SINA_WEIBO.toString(), sina.loginToken);
//		}
//		accounts.add(sina);
//
//		//腾讯微博sso
//		AccountInfo tweibo = new AccountInfo(R.drawable.ic_tencent_icon, R.string.unbound_tweibo, THIRDTYPE.TECENT_WEIBO);
//		ShareByTencentWeiboSSO shareByTencentWeibo = ShareByTencentWeiboSSO.getInstance();
////		OAuthV2 oAuthV2 = shareByTencentWeibo.getOAuth2(ctx);
////		if (oAuthV2 == null || !ShareByTencentWeibo.tokenExpiresTime(oAuthV2)){
////			tweibo.loginToken = oAuthV2.getOpenid();
////			tweibo.action = ACTION.UNBOUND;
////			tweibo.thirdAccountName = getThirdLoginName(THIRDTYPE.TECENT_WEIBO.toString(), tweibo.loginToken);
////		}
//
//
//		String accessToken = Util.getSharePersistent(ctx,
//				"ACCESS_TOKEN");
//		if(!StringUtils.isEmpty(accessToken)&&!shareByTencentWeibo.isAuthorizeExpired(ctx)){
//			tweibo.loginToken = Util.getSharePersistent(ctx, "OPEN_ID");
//			tweibo.action = ACTION.UNBOUND;
//			tweibo.thirdAccountName = getThirdLoginName(THIRDTYPE.TECENT_WEIBO.toString(), tweibo.loginToken);
//		}
//		accounts.add(tweibo);
//
//		//去掉人人网(超A去掉人人网账号绑定功能)
////		if(ConfigApi.isSouyue()){
////		    AccountInfo renrenInfo = new AccountInfo(R.drawable.ic_renren_icon, R.string.loginActivity_renren, THIRDTYPE.RENREN);
////		    RennClient ren = ShareByRenren.getRenren(ctx);
////		    if(ren.isAuthorizeValid()){
////		        renrenInfo.loginToken = ren.getUid()+"";
////		        renrenInfo.action = ACTION.UNBOUND;
////		        renrenInfo.thirdAccountName = getThirdLoginName(THIRDTYPE.RENREN.toString(), renrenInfo.loginToken);
////		    }
////		    accounts.add(renrenInfo);
////		}
//
//		return accounts;
//	}
//
	//清除登录的
	public static String removeLoginToken() {
		SYSharedPreferences sysp = SYSharedPreferences.getInstance();
		String loginType = sysp.getString(SYSharedPreferences.KEY_LOGIN_TYPE, null);
		sysp.remove(SYSharedPreferences.KEY_LOGIN_TOKEN);
		sysp.remove(SYSharedPreferences.KEY_LOGIN_TYPE);
		return loginType;
	}
	
	//---------账号绑定的start-----//
	/**
	 * @param thirdType 第三方类型
	 * @param uid 第三方用户的uid
	 * @param uname 第三方用户的username
	 */
	public static void saveThirdLogin(String thirdType, String uid, String uname) {
		LogDebugUtil.v("TAG_SAVE", thirdType+"="+uid+"="+uname);
		SYSharedPreferences sysp = SYSharedPreferences.getInstance();
		sysp.putString(thirdType, uid+","+uname);
		if(!ConfigApi.isSouyue()){
		    //TAG:移动商城
		    sysp.putBoolean(SYSharedPreferences.ISTHIRDTYPE, true);
		}
	}
	
	public static String[] getThirdLogin(String thirdType) {
		SYSharedPreferences sysp = SYSharedPreferences.getInstance();
		String v = sysp.getString(thirdType, null);
		if(v != null){
			String[] uid_uname = new String[2];
			int dot = v.indexOf(",");
			uid_uname[0] = v.substring(0, dot);
			uid_uname[1] = v.substring(dot+1);
			return uid_uname;
		}
		return null;
	}
	public static String getThirdLoginName(String thirdType, String uid) {
		LogDebugUtil.v("TAG_GET", thirdType+"="+uid);
		String[] uid_uname = getThirdLogin(thirdType);
		if(uid_uname != null && uid_uname[0].equals(uid)){
			LogDebugUtil.v("TAG_GET_RES", uid_uname[1]);
			return uid_uname[1];
		}
		return null;
	}
	//---------账号绑定的end-----//
}
