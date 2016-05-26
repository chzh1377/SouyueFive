package com.zhongsou.souyue.db;

import com.zhongsou.souyue.module.SelfCreateItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的原创数据库操作
 * @author Administrator
 *
 */
public class SelfCreateHelper {
	private static SelfCreateHelper instance = null;

	private SelfCreateHelper() {
	}

	public synchronized static SelfCreateHelper getInstance() {
		if (instance == null) {
			instance = new SelfCreateHelper();
		}
		return instance;
	}

	/**
	 * 转换成按空格分割的String
	 * 
	 * @param strs
	 * @return
	 */
	private String listToString(List<String> strs) {
		StringBuffer sb = new StringBuffer();
		for (String str : strs) {
			sb.append(str.trim()).append(" ");
		}
		return sb.toString();
	}

	public String addSelfCreateItem(SelfCreateItem sci) {
		long l = 0;
		if(sci.conpics() != null && sci.conpics().size() != 0){
			sci.conpic_$eq(listToString(sci.conpics()));
		}
		SelfCreateTableDBHelper selfCreateTableDBHelper = new SelfCreateTableDBHelper();
		selfCreateTableDBHelper.openWritable();
		l = selfCreateTableDBHelper.insert(sci);
		selfCreateTableDBHelper.close();
		return getKey(sci, l);
	}
	
	private String getKey(SelfCreateItem sci, long rowid){
		String _id = "";
		SelfCreateTableDBHelper selfCreateTableDBHelper = new SelfCreateTableDBHelper();
		selfCreateTableDBHelper.openReadable();
		_id = selfCreateTableDBHelper.selectKeyId(sci, rowid);
		selfCreateTableDBHelper.close();
		return _id;
	}

	/**
	 * 获得所有失败的原创内容
	 * 
	 * @return
	 */
	public synchronized List<SelfCreateItem> getAllSelfCreateItem(String str) {
		List<SelfCreateItem> all = new ArrayList<SelfCreateItem>();
		SelfCreateTableDBHelper selfCreateTableDBHelper = new SelfCreateTableDBHelper();
		selfCreateTableDBHelper.openReadable();
		all = selfCreateTableDBHelper.select(str);
		selfCreateTableDBHelper.close();
		return all;
	}
	
	/**
     * 获得所有失败的超级分享原创内容
     * 
     * @return
     */
    public synchronized List<SelfCreateItem> getAllSelfCreateItem(String str, String Key,String key1) {
        List<SelfCreateItem> all = new ArrayList<SelfCreateItem>();
        SelfCreateTableDBHelper selfCreateTableDBHelper = new SelfCreateTableDBHelper();
        selfCreateTableDBHelper.openReadable();
        all = selfCreateTableDBHelper.select(str, Key,key1);
        selfCreateTableDBHelper.close();
        return all;
    }
	

	/**
	 * 更新
	 * 
	 * @param sci
	 */
	public long updateSelfCreateItem(SelfCreateItem sci) {
		long l = 0;
		if(sci.conpics() != null && sci.conpics().size() != 0){
			sci.conpic_$eq(listToString(sci.conpics()));
		}
		SelfCreateTableDBHelper selfCreateTableDBHelper = new SelfCreateTableDBHelper();
		selfCreateTableDBHelper.openWritable();
		l = selfCreateTableDBHelper.update(sci);
		selfCreateTableDBHelper.close();
		return l;
	}

	/**
	 * 删除
	 * 
	 * @param sci
	 */
	public long delSelfCreateItem(SelfCreateItem sci) {
		long l = 0;
		SelfCreateTableDBHelper selfCreateTableDBHelper = new SelfCreateTableDBHelper();
		selfCreateTableDBHelper.openWritable();
		l = selfCreateTableDBHelper.delete(sci);
		selfCreateTableDBHelper.close();
		return l;
	}
	
	/**
	 * 删除
	 * 
	 * @param sci
	 */
	public void delAllSelfCreate() {
		SelfCreateTableDBHelper selfCreateTableDBHelper = new SelfCreateTableDBHelper();
		selfCreateTableDBHelper.openWritable();
		if(selfCreateTableDBHelper.isTableExist()){
		       selfCreateTableDBHelper.deleteAll();
		       selfCreateTableDBHelper.close(); 
		}
	}

}
