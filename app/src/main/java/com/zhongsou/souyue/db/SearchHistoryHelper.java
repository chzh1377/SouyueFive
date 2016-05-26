package com.zhongsou.souyue.db;

import android.util.Log;
import com.zhongsou.souyue.module.ToolTip;

import java.util.List;
/**
 * 搜索历史数据库接口
 * 1：keyword
 * 2：lastUpdate，时间毫秒。
 * 关键字排重，按照时间重新到旧输出数据，数据库中最多保持30条记录。
 * @author chefb@zhongsou.com
 *
 */
public class SearchHistoryHelper{
	//最大保存数量
	private static final int MAX_RECORD_COUNT = 30;
    public static final String KEYWORD_TYPE_30 = "3.0";
    public static final String KEYWORD_TYPE_2X = "2.X";
    public static final String KEYWORD_TYPE_CZ_VIDEO = "CZVIDEO";
    public static final String KEYWORD_TYPE_CZ_NEWS = "CZNEWS";
    public static final String KEYWORD_TYPE_CZ_BBS = "CZBBS";
    public static final String KEYWORD_TYPE_CZ_WEIBO = "CZWEIBO";

	private static boolean  isEqual(ToolTip history,ToolTip newTip){
		return
//				history.category().equals(newTip.category())&&//
//				history.rssImage().equals(newTip.rssImage())&&//
//				history.url().equals(newTip.url()&&)//
				history.keyword().equals(newTip.keyword());//&&//
//				history.srpId().equals(newTip.srpId())//
//				&&history.srpCate().equals(newTip.srpCate())//
//				&&history.m().equals(newTip.m())//
//				&&history.g().equals(newTip.g());//
	}
	
	public static void addKeyword(ToolTip toolTip) {
		SearchHistoryTableDBHelper searchHistoryTable = new SearchHistoryTableDBHelper();
		searchHistoryTable.openWritable();
		boolean isSave = false;
		List<ToolTip> list = searchHistoryTable.select();
		if(list != null){
			//是否已经存在
			for(int i=0;i<list.size();i++){
				ToolTip tmpTip = list.get(i);
				if(tmpTip.keyword().equals(toolTip.keyword())&&tmpTip.m().equals(toolTip.m())&&tmpTip.g().equals(toolTip.g())){
					isSave=true;
					searchHistoryTable.update(tmpTip);
					break;
				}
			}
		}
		//插入
		if(!isSave)
			searchHistoryTable.insert(toolTip);
		searchHistoryTable.close();
	}
	/**
	 * 插入一条新的搜索词 
	 * 只在点击item的时候调用 所有所有字段都有数据
	 */
	public static void add(ToolTip toolTip) {
		SearchHistoryTableDBHelper searchHistoryTable = new SearchHistoryTableDBHelper();
		boolean isSave = false;
		searchHistoryTable.openWritable();
		List<ToolTip> list = searchHistoryTable.select();
		//有已记录搜索历史
		if(list != null){
			//是否已经存在
			for(int i=0;i<list.size();i++){
				if(isEqual(list.get(i),toolTip)){
					isSave = true;
					break;
				}
			}
		}
		//如果已经存在的搜索词重新被搜索，需要更新该词的lastUpdate为当前时间。
		if(isSave){
			searchHistoryTable.update(toolTip);
		//如果不存在则插入
		}else{
			searchHistoryTable.insert(toolTip);
		}
		//如果存储总量超出设定值
		if(null!= list && list.size()>MAX_RECORD_COUNT){
			for(int i=MAX_RECORD_COUNT;i<list.size();i++){
				ToolTip tip=list.get(i);
				searchHistoryTable.delete(tip);
			}
		}
		searchHistoryTable.close();
		/**
		 * 如果keywrod存在，调用update
		 * 否则插入，然后清除多余30条以外的数据。
		 *
		 */
	}
	/**
	 * 获得全部数据
	 * @return
	 */
	public static List<ToolTip> getAll() {
		List<ToolTip> list= null;
		SearchHistoryTableDBHelper searchTable = new SearchHistoryTableDBHelper();
		searchTable.openReadable();
		list = searchTable.select();
		searchTable.close();
		return list;
	}
	/**
	 * 清除全部记录
	 */
	public static void removeAll() {
		SearchHistoryTableDBHelper searchTable = new SearchHistoryTableDBHelper();
		searchTable.openWritable();
		searchTable.clear();
		searchTable.close();
		Log.d(SouYueDBHelper.TABLE_SEARCH_HISTORY, "removeAll");
		
	}
 
	
	
}
