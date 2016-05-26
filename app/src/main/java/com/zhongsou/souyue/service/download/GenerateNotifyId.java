package com.zhongsou.souyue.service.download;

import java.util.ArrayList;

public class GenerateNotifyId {
	private static ArrayList<String> notifyIds = new ArrayList<String>();
	public static int generateNotifyId(String url) {
		int index = -1;
		if((index = notifyIds.indexOf(url))==-1) {
			notifyIds.add(url);
			return notifyIds.size();
		}
		return index+1*1000;
	}
}
