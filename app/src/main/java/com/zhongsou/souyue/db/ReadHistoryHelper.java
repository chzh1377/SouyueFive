package com.zhongsou.souyue.db;
import java.util.List;
import java.util.Set;
public class ReadHistoryHelper {
	private static ReadHistoryHelper instance = null;
	private ReadHistoryHelper() {
	}
	public synchronized static ReadHistoryHelper getInstance() {
		if (instance == null) {
			instance = new ReadHistoryHelper();
		}
		return instance;
	}
	/**
	 * 插入一条新纪录
	 * @param url
	 * @return
	 */
	public long insert(String url) {
		ReadHistoryTableDBHelper table = new ReadHistoryTableDBHelper();
		try {
			table.openWritable();
			return table.insert(url);
		} finally {
			table.close();
		}
	}
	/**
	 * 清库
	 * @return
	 */
	public long deleteAll() {
		ReadHistoryTableDBHelper table = new ReadHistoryTableDBHelper();
		try {
			table.openWritable();
			return table.deleteAll();
		} finally {
			table.close();
		}
	}
	/**
	 * 查询数据库中全部阅读的历史记录，返回命中的数据
	 * @param url
	 * @return
	 */
	public Set<String> select(List<String> url) {
		ReadHistoryTableDBHelper table = new ReadHistoryTableDBHelper();
		try {
            table.openReadable();
            return table.select(url);
        }catch(Exception e){
            e.printStackTrace();
		} finally {
			table.close();
		}
        return null;
	}
	

}
