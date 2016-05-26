package com.zhongsou.souyue.im.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

/**
 * Created by zoulu
 * on 2015/1/14
 * Description:发送gif用
 */
public class SendGifBean implements Serializable,DontObfuscateInterface {

    private String gif_name;
    
    private String local_url; //本地图片url


	public String getGif_name() {
        return gif_name;
    }

    public void setGif_name(String gif_name) {
        this.gif_name = gif_name;
    }



    public String getLocal_url() {
		return local_url;
	}

	public void setLocal_url(String local_url) {
		this.local_url = local_url;
	}


	public String getGif_url() {
		return gif_url;
	}

	public void setGif_url(String gif_url) {
		this.gif_url = gif_url;
	}

	public String getGif_w() {
        return gif_w;
    }

    public void setGif_w(String gif_w) {
        this.gif_w = gif_w;
    }

    public String getGif_h() {
        return gif_h;
    }

    public void setGif_h(String gif_h) {
        this.gif_h = gif_h;
    }

    private String gif_url;
    private String gif_w;
    private String gif_h;
}
