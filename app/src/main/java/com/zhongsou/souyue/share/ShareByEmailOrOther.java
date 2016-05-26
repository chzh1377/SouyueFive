package com.zhongsou.souyue.share;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.im.util.SMSUtils;
import com.zhongsou.souyue.net.share.SharePvRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

import java.io.File;

public class ShareByEmailOrOther {

    public static void shareByEmail(Activity activity, ShareContent content,IVolleyResponse response) {
//        new Http(activity).pv("mail", content.getUrl());
        SharePvRequest.send("mail", content.getUrl(),response);
        Intent i = new Intent(Intent.ACTION_SEND);
//		i.setType("message/rfc882");//htc手机不通
//		i.setType("image/*");
		i.setType("application/emma+xml");

		if(content.getDimensionalcode()==1){
		    i.putExtra(Intent.EXTRA_SUBJECT, content.getCodeContent(0));
            i.putExtra(Intent.EXTRA_TEXT, content.getCodeContent());
            File file = content.getDimensionalCodeFile();
            if (null != file) {
                i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            }
		}else{
		    i.putExtra(Intent.EXTRA_SUBJECT, content.getTitle());
		    i.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(content.getEmailContent()));
		    File file = content.getTempImageFile();
		    if (null != file) {
		        i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		    }
		}
		String emailDescription = MainApplication.getInstance().getString(R.string.choose_email_description);
		activity.startActivity(Intent.createChooser(i,
				emailDescription));
	}

    public static void shareBySMS(Activity activity, ShareContent content,IVolleyResponse response) {
//        new Http(activity).pv("sms", content.getUrl());
        SharePvRequest.send("sms", content.getUrl(), response);
        new SMSUtils(activity).sendShareMessage(content);
    }


//	public static void shareBySystem(Activity activity, ShareContent content) {
//        Intent send = new Intent(Intent.ACTION_SEND);
//        send.setType("text/plain");
//        send.putExtra(Intent.EXTRA_TEXT, content.getOtherContent());
//        send.putExtra(Intent.EXTRA_SUBJECT, content.getTitle());
//        String systemDescription = MainApplication.getInstance().getString(R.string.choose_system_description);
//        try {
//        	activity.startActivity(Intent.createChooser(send, systemDescription));
//        } catch(android.content.ActivityNotFoundException ex) {
//        }
//    
//	}


}
