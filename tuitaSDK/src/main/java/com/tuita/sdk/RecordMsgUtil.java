package com.tuita.sdk;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

/** 
 * 类说明 :记录推送消息工具类
 */
public class RecordMsgUtil {
	public static final String MSGKEY = "MessageRecord";
	public static final String KEY = "key";
	private static String localFolder = "/souyue/message";
	private static String save_file = "/message.txt";
	private static String old_file = "/message.bak";
	
	
	private static boolean isaWeekAgo(long currentTimeMillis){
		long aweek = 7*24*60*60*1000;
		return (System.currentTimeMillis() - currentTimeMillis) > aweek;
	}
	
	public static void editMsg(Context context , String msgId){
		StringBuffer buffer = new StringBuffer();
		SharedPreferences sp = context.getSharedPreferences(
				MSGKEY, Context.MODE_PRIVATE);
		buffer.append(readMsg(context));
		buffer.append(joinString(msgId));
		Editor editor = sp.edit();
		editor.putString(KEY, buffer.toString());
		editor.commit();
	}
	
	private static void writeMsg(Context context, String msg){
		SharedPreferences sp = context.getSharedPreferences(
				MSGKEY, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(KEY, msg);
		editor.commit();
	}
	
	public static void deleteMsg(Context context,String str){
		StringBuffer buffer = new StringBuffer();
		String[] msg = readMsg(context).split(",");
		for(String oldMsg : msg){
			if(!isaWeekAgo(Long.parseLong(oldMsg.split("/")[1]))){
				buffer.append(oldMsg + ",");
			}
		}
		writeMsg(context , buffer.toString());
//		saveMessage(str);
	}
	
	/**
	 * 
	 * @param context
	 * @param msgId
	 * @return  true  说明有相同的消息存在
	 */
	public static boolean hasMsg(Context context , String msgId){
		String[] msg = readMsg(context).split(",");
		boolean flag = false;
		for(String oldMsg : msg){
			if(oldMsg.split("/")[0].equals(msgId))
				flag = true;
		}
		return flag;
	}
	
	private static String readMsg(Context context){
		SharedPreferences sp = context.getSharedPreferences(
				MSGKEY, Context.MODE_PRIVATE);
		return sp.getString(KEY, "");
	}
	
	private static String joinString(String msgId){
		return msgId + "/" +System.currentTimeMillis() + ",";
	}
	
	private static void saveMessage(String message) {
		if (isHaveSdCard()) {
			File folder = new File(Environment.getExternalStorageDirectory()
					+ localFolder);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			Calendar c = Calendar.getInstance();
			String time = do4Format(c.getTime(), "yyyy-MM-dd HH:mm:ss");
			RandomAccessFile file;
			try {
				file = new RandomAccessFile(
						Environment.getExternalStorageDirectory() + localFolder
								+ save_file, "rw");
				if (file.length() > 1048576*2) {
					file.close();
					File file2 = new File(
							Environment.getExternalStorageDirectory()
									+ localFolder + save_file);
					File file3 = new File(
							Environment.getExternalStorageDirectory()
									+ localFolder + old_file);
					if (!file3.exists()) {
						file2.renameTo(file3);// 不存在
					} else {
						file3.delete();
						file2.renameTo(new File(Environment
								.getExternalStorageDirectory()
								+ localFolder
								+ old_file));// 存在
					}
					file = new RandomAccessFile(
							Environment.getExternalStorageDirectory()
									+ localFolder + save_file, "rw");
					file.seek(file.length());
					file.writeUTF(do4Format(c.getTime(), "\n" + time)
							+ ": " + message + " \n");
				} else {
					file.seek(file.length());
					file.writeUTF(do4Format(c.getTime(), "\n" + time)
							+ ": " + message + " \n");
				}
				file.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private static String do4Format(Date date,String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
	private static boolean isHaveSdCard(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
}
