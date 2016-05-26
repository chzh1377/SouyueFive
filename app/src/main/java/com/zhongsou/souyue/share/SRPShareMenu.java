package com.zhongsou.souyue.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.im.util.BitmapUtil;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.pop.MyPopupWindow;
import com.zhongsou.souyue.utils.StringUtils;

public class SRPShareMenu implements OnClickListener {
    private  View ll_weixin;
    private  View ll_friend;
    public MyPopupWindow popupWindow;
	private View view;
	private final Context context;
	private String keyword, url, imageUrl, srpId;

    public View getLl_weixin() {
        return ll_weixin;
    }

    public void setLl_weixin(View ll_weixin) {
        this.ll_weixin = ll_weixin;
    }

    public View getLl_friend() {
        return ll_friend;
    }

    public void setLl_friend(View ll_friend) {
        this.ll_friend = ll_friend;
    }

    public SRPShareMenu(final Context context) {
		this.context = context;
		if (popupWindow == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.srp_share, null);
			view.measure(0, 0);
			 ll_weixin = view.findViewById(R.id.ll_weixin);
			ll_weixin.setOnClickListener(this);
			 ll_friend = view.findViewById(R.id.ll_friend);
			ll_friend.setOnClickListener(this);

		}
		popupWindow = new MyPopupWindow(view, view.getMeasuredWidth(),
				view.getMeasuredHeight(), true);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.update();
	}

	// public void showAtLocation(View parent) {
	// popupWindow.showAtLocation(parent, Gravity.RIGHT , 0,
	// 0);
	// }

	public void showAsDropDown(View parent) {
		popupWindow.showAsDropDown(parent, parent.getWidth(), 0);
	}

	public void dismiss() {
		if (popupWindow != null)
			popupWindow.dismiss();
	}

	public void setParams(String keyword, String srpId, String shareImageUrl) {
		this.imageUrl = shareImageUrl;
		this.keyword = keyword;
		this.srpId=srpId;
		this.url = UrlConfig.srp + StringUtils.enCodeRUL(keyword)+"&srpId="+srpId+CommonStringsApi.getUrlAppendIgId();
	}

	@Override
	public void onClick(View v) {
		String contentStr = "";
	    int type = StringUtils.isSuperSrp(keyword, null);
	    switch (type) {
		case 1:
			contentStr = context.getString(R.string.srp_cj_share_title);
			contentStr = String.format(contentStr,CommonStringsApi.APP_NAME);
			break;
		case 2:
			contentStr = context.getString(R.string.srp_cm_share_title);
			contentStr = String.format(contentStr,CommonStringsApi.APP_NAME);
			break;
		default:
			 contentStr = context.getString(R.string.srp_share_content);
			 contentStr=String.format(contentStr,CommonStringsApi.APP_NAME,keyword);
			break;
		}
		contentStr=String.format(contentStr, keyword);
//		AQuery a = new AQuery(context);
		Bitmap b = BitmapUtil.decodeFile(PhotoUtils.getImageLoader().getDiskCache().get(imageUrl).getAbsolutePath());
		ShareContent content = new ShareContent(keyword, url, b, contentStr,imageUrl);
		content.setSharePointUrl(this.url);
		content.setKeyword(keyword);
		content.setSrpId(srpId);
		switch (v.getId()) {
		case R.id.ll_weixin:
			ShareByWeixin.getInstance().share(content, false);
			popupWindow.dismiss();
			break;
		case R.id.ll_friend:
			ShareByWeixin.getInstance().share(content, true);
			popupWindow.dismiss();
			break;

		default:
			break;
		}

	}
}
