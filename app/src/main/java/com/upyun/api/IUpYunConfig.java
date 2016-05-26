package com.upyun.api;
/**
 * @author wanglong@zhongsou.com
 *
 */
public interface IUpYunConfig {
	public static final String API_IMAGE_KEY = "PrvwIpMbUK3XV7f7JXpvBzaAUBc="; //上传图片所使用的密钥
	public static final String API_VOICE_KEY = "g4D9TWsLhhnt2EtTroH3g4AasIA=";//上传语音所使用的密钥
	public static final String API_LOG_KEY = "o5OUvlx6zJlSMq1AX7dUkGRx7FA=";//上传IMlog所使用的密钥

	public static final String BUCKET_VOICE = "souyue-voice"; //语音上传空间名
	public static final String BUCKET_IMAGE = "souyue-image"; //图片上传空间名
	public static final String BUCKET_LOG = "souyue-log-im";//log上传空间名

	public static final String UPDATE_HOST = "http://v0.api.upyun.com/";

	public static final String HOST_IMAGE = "http://souyue-image.b0.upaiyun.com"; //host
	public static final String HOST_VOICE = "http://souyue-voice.b0.upaiyun.com"; //host
	public static final String HOST_LOG = "souyue-log-im.b0.upaiyun.com";//log host
	public String getSaveKey();
}
