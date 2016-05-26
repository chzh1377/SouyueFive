package com.zhongsou.souyue.content;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import com.umeng.analytics.MobclickAgent;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author wanglong@zhongsou.com
 * 
 */
public class ShareContent implements Serializable{
	public static int SHARE_REC = 0;
	public static int SHARE_NEWS = 1;
	private static String APP_URL = MobclickAgent.getConfigParams(MainApplication.getInstance(), "DOWNLOAD_URL");// "http://itunes.apple.com/cn/app/id552858812?mt=8";
	public int dimensionalcode;
	private File dimenImg;
	
    public static final String SRPID = "srpId";
    public static final String KEYWORD = "keyword";
    public static final String SHAREURL = "shareUrl";
	
	public void setDimenImg(File f){
	    this.dimenImg = f;
	}
	
	public int getDimensionalcode() {
        return dimensionalcode;
    }

    public void setDimensionalcode(int dimensionalcode) {
        this.dimensionalcode = dimensionalcode;
    }
    static{
		if(StringUtils.isEmpty(APP_URL)){
			APP_URL="http://souyue.mobi";
		}
	}

	public ShareContent() {
	}

	public ShareContent(String title, String url, Bitmap image, String content,String picUrl) {
		this.title = title;
		if(url!=null){
		    this.url = addToken(url);
		}
		this.image = image;
		this.content = content;
		this.picUrl=picUrl;
	}

	private String url;// 分享到短信
	private String content;// 分享到新浪的消息体
	private Bitmap image;// 图片
	private File tempFile;// 图片临时文件
	private String title;// 标题
	private int from = SHARE_NEWS;//分享的来源，主要针对微博
	private String picUrl; //分享图片路径
	private String keyword;
	private String callback;
	private String srpId;
	private String sharePointUrl;//统计积分用的url

    private List<String> toInterestImages;  // 新闻分享到兴趣圈的图片列表
	
    public String getSharePointUrl() {
        return sharePointUrl;
    }

    public void setSharePointUrl(String sharePointUrl) {
        if(SYUserManager.getInstance().getUser().userType().equals(SYUserManager.USER_ADMIN)){
           this.sharePointUrl=sharePointUrl;
        }
        
    }
    private String addToken(String shareurl){
        String finalurl=null;
        String token = SYUserManager.getInstance().getUser().token();
        long userid = SYUserManager.getInstance().getUser().userId();
        
        if(shareurl.contains("?")){
            String[] url=shareurl.split("\\?");
            if(!StringUtils.isEmpty(url[1])){
//                finalurl =url[0]+"?token="+SYUserManager.getInstance().getUser().token()+"&"+url[1];
            	//优先使用userid,userid=0,再使用token
            	if(userid != 0) {
            		finalurl =url[0]+"?userid="+userid+"&"+url[1];
            	}else{
            		finalurl =url[0]+"?token="+token+"&"+url[1];
            	}
            }else{
//                finalurl =url[0]+"?token="+SYUserManager.getInstance().getUser().token();
            	if(userid != 0) {
            		finalurl =url[0]+"?userid="+userid;
            	}else{
            		finalurl =url[0]+"?token="+token;
            	}
            	
            } 
        }else{
//            finalurl=shareurl+"?token="+SYUserManager.getInstance().getUser().token();
        	if(userid != 0) {
        		finalurl=shareurl+"?userid="+userid;
        	}else{
        		finalurl=shareurl+"?token="+token;
        	}
        }
        return finalurl;
    }
	
	public String getSrpId() {
        return srpId;
    }

    public void setSrpId(String srpId) {
        this.srpId = srpId;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public void setKeyword(String keyword){
	    this.keyword = keyword;
	}
	public String getKeyword(){
	    return this.keyword;
	}

  public String getPicUrl() {
    return picUrl;
  }

  public void setPicUrl(String picUrl) {
    this.picUrl = picUrl;
  }

  public String getContent() {
		return content;
	}
	/**
	 * 
	 * @return 临时分享图片文件的完全路径
	 */
	public String getTempImageFilePath(){
		File file = getTempImageFile();
		if(null!=file){
			return file.getAbsolutePath();
		}else{
			return null;
		}
	}
	public File getTempImageFile() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (!sdCardExist) {
			return null;
		} 
		if (image == null) {
			return null;
		}
		if (null != tempFile) {
			return tempFile;
		}
		String filename = ImageUtil.getfilename();
		ImageUtil.saveBitmapToFile(this.image, filename);
		File file = new File(filename);
		if (null != file && file.exists()) {
			tempFile = file;
			return file;
		} else {
			return null;
		}

	}

	public Bitmap getImage() {
		return image;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUrl(String url) {
		this.url = url;
	}
    public void setQQZONEUrl(String url){
        if(url!=null){
            this.url = addToken(url);
        }
    }
	public String getOtherContent(){
		return this.title + ":" + this.url + "。更多精彩来自" + CommonStringsApi.APP_NAME + APP_URL;
	}

	public String getEmailContent() {
	    if (StringUtils.isSuperSrp(keyword, null) != 0)
	        return content;
		if (StringUtils.isNotEmpty(this.url)) {//新闻分享
			return this.content + "<br>" + this.url + "<br><br>分享自<a href=\"" + APP_URL + "\" target=\"_blank\">"+CommonStringsApi.APP_NAME+"</a></br>";
		} else {
			return this.content;
		}
	}

	
	/**
	 * 此方法返回的内容，用户可以编辑，在发之前，系统在尾部追加url地址，url地址为10个长度，微博允许发140个字符，在发微博文本框中，
	 * 限制字符数为130.
	 * 
	 * @return
	 */
	public String getWeiboContent() {
//		return "分享自@中搜搜悦:【" + StringUtils.truncate(this.title, 200) + "】";
		//文章配图（如果有） + 标题 + url + 分享自@中搜搜悦 
//		return StringUtils.truncate(this.content, StringUtils.LENGTH_200);
		String shareTitle = StringUtils.isNotEmpty(title)?title:"";
		String shareContent = StringUtils.isNotEmpty(content)?content:"";
		return StringUtils.truncate(StringUtils.isNotEmpty(shareTitle)?shareTitle:shareContent, StringUtils.LENGTH_200);
	}
	
	public String getTWeiboContent(){
		return StringUtils.truncate(this.content, StringUtils.LENGTH_200);
	}
	
	public String getRenrenContent(){
		return StringUtils.truncate(this.content, StringUtils.LENGTH_200);
	}
	
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	public String getSmsContent() {
		if (StringUtils.isNotEmpty(this.url)) {//新闻分享
			return this.content + this.url + " 更多精彩来自"+CommonStringsApi.APP_NAME;
		} else {
			return this.content;
		}
	}
	
	public String getWeiXinContent() {
		if (StringUtils.isNotEmpty(content)) {//新闻分享
			return content;
		} else {
			return "";
		}
	}

	public String getWeiXinTitle() {
		return "分享";
	}

	public void setFrom(int sHARE_REC2) {
		from = sHARE_REC2;
	}
	
	public int getFrom() {
		return from;
	}
	
	//二维码分享
	/**
	 * code=0 mail title
	 * code=1 mail content
	 * default other
	 * @param code
	 * @return
	 */
	public String getCodeContent(){
	    return getCodeContent(-1);
	}
	public String getCodeContent(int code){
	    switch (code) {
            case 0:
                return "推荐你在"+CommonStringsApi.APP_NAME_SHORT+"中订阅 "+'"'+this.keyword + '"';
            default:
                return "推荐你在"+CommonStringsApi.APP_NAME_SHORT+"中订阅 "+'"'+this.keyword+'"'+",内容很精彩,值得关注,使用"+CommonStringsApi.APP_NAME_SHORT+"扫描二维码,可以直接订阅。";
        }
	    
	}
	//获取二维码图片
	public File getDimensionalCodeFile() {
        if (null != dimenImg && dimenImg.exists()) {
            return dimenImg;
        } else {
            return null;
        }
    }

    public List<String> getToInterestImages() {
        return toInterestImages;
    }

    public void setToInterestImages(List<String> toInterestImages) {
        this.toInterestImages = toInterestImages;
    }
}
