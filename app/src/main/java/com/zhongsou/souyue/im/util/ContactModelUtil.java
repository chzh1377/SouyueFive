package com.zhongsou.souyue.im.util;

import android.text.TextUtils;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.GroupMembers;

public class ContactModelUtil {

	//如果有备注名，则显示备注名，否则显示昵称
	public static String getShowName(Contact contact){
    	String showName = contact.getComment_name();
    	if(showName==null || "".equals(showName.trim())){
    		showName = contact.getNick_name();
    	}
    	return showName;
	}


	//@列表   如果有备注名，则显示备注名，否则显示昵称
	public static String getShowName(GroupMembers groupMembers){
		String showName = groupMembers.getConmmentName();
		if(showName==null || "".equals(showName.trim())){
		showName = TextUtils.isEmpty(groupMembers.getMember_name())?groupMembers.getNick_name():groupMembers.getMember_name();
		}
		return showName;
	}
}
