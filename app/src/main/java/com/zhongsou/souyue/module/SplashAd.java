package com.zhongsou.souyue.module;


/**
 * User: zhangliang01@zhongsou.com
 * Date: 11/27/13
 * Time: 6:36 PM
 */
public class SplashAd extends ResponseObject {
	
	public static final String LAST_SPLASH_IMAGE_URL = "last_splash_image_url";
	public static final String SPLASH_DISPLAY_TIME = "splash_display_time";
	public static final String SPLASH_IS_DISPLAY_JUMP = "splash_is_display_jump";//是否显示跳转按钮
	public static final String SPLASH_JUMP_TYPE = "splash_jump_type";//跳转类型
	public static final String SPLASH_JUMP_URL = "splash_jump_url";//跳转url
	public static final String SPLASH_LASTID = "splash_last_id";//上次的id
	public static final String SPLASH_ID = "splash_id";//当前的id

    private String ADID;//
    private String AdName;//广告名称
    private String AdImage;//图片地址
    private String expiredStartTime = "";//有效开始时间
    private String expiredEndTime = "";//有效结束时间
	private String url = "";
    private long offsetTime;
    private int exhibitionTime;//启动图展示时间
    private int isjump ;// 0是不显示，1是显示
	private int jumpType;// 0是不跳，1是h5，2是srp ，3是圈子
    private String jumpUrl;//跳转Url
	private String id; // 广告id

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getADID() {
        return ADID;
    }

    public void setADID(String ADID) {
        this.ADID = ADID;
    }

    public String getAdName() {
        return AdName;
    }

    public void setAdName(String adName) {
        AdName = adName;
    }

    public String getAdImage() {
        return AdImage;
    }

    public void setAdImage(String adImage) {
        AdImage = adImage;
    }
    
    public String getExpiredStartTime() {
		return expiredStartTime;
	}

	public void setExpiredStartTime(String expiredStartTime) {
		this.expiredStartTime = expiredStartTime;
	}

	public String getExpiredEndTime() {
		return expiredEndTime;
	}

	public void setExpiredEndTime(String expiredEndTime) {
		this.expiredEndTime = expiredEndTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getOffsetTime() {
		return offsetTime;
	}

	public void setOffsetTime(long offsetTime) {
		this.offsetTime = offsetTime;
	}

	public int getExhibitionTime() {
		return exhibitionTime;
	}

	public void setExhibitionTime(int exhibitionTime) {
		this.exhibitionTime = exhibitionTime;
	}

	public int getIsJump() {
		return isjump;
	}

	public void setIsJump(int isJump) {
		this.isjump = isJump;
	}

	public int getJumpType() {
		return jumpType;
	}

	public void setJumpType(int jumpType) {
		this.jumpType = jumpType;
	}

	public String getJumpUrl() {
		return jumpUrl;
	}

	public void setJumpUrl(String jumpUrl) {
		this.jumpUrl = jumpUrl;
	}
}
