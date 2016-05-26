package com.zhongsou.souyue.im.search;

import java.io.UnsupportedEncodingException;

import android.os.AsyncTask;
import android.util.Log;
import com.zhongsou.souyue.DontObfuscateInterface;

/**
 * 搜索类
 * 
 * @author zhaomeng
 * 
 */
public class IMSearch implements DontObfuscateInterface {
	/**
	 * 搜索接口监听
	 * 
	 * @author zhaomeng
	 * 
	 */
	public interface IMSearchListener {
		public void onSearchStart();

		public void onSearchEnd(int searchType, Result result);
	}

	public final static int SEARCH_LIST_TYPE = 1;
	public final static int SEARCH_MSG_TYPE = 2;

	private IMSearchListener mSearchListener;
	private String mSearchKey;
	private long mSessionId;
	private long mSessionType;
	private SearchTask mSearchTask;
	private int mSearchType;

	public IMSearch() {
//		IMQuery.init();
	}

	public void setIMSearchListener(IMSearchListener listener) {
		mSearchListener = listener;
	}

	public void searchList(String searchKey) {
		mSearchKey = searchKey;
		mSearchType = SEARCH_LIST_TYPE;
		if (mSearchTask != null) {
			mSearchTask.cancel(true);
		}
		mSearchTask = new SearchTask();
		mSearchTask.execute();
	}

	public void searchMsg(String searchKey, long sessionid, long sessionType) {
		mSearchKey = searchKey;
		mSessionId = sessionid;
		mSessionType = sessionType;
		mSearchType = SEARCH_MSG_TYPE;
		if (mSearchTask != null) {
			mSearchTask.cancel(true);
		}
		mSearchTask = new SearchTask();
		mSearchTask.execute();
	}

	public void cancelSearch() {
		if (mSearchTask != null) {
			mSearchTask.cancel(true);
		}
	}

	class SearchTask extends AsyncTask<Void, Void, Result> {

		@Override
		protected void onPreExecute() {
			if (mSearchListener != null) {
				mSearchListener.onSearchStart();
			}
		}

		@Override
		protected Result doInBackground(Void... params) {
			Result result = null;
			byte[] searchBuffer;
			int bufferLen = 0;
			if (mSearchKey != null) {
				try {
					if (mSearchType == SEARCH_LIST_TYPE) {
						bufferLen = 4 + 20 * 1000;
						searchBuffer = new byte[bufferLen];
						int code = IMQuery.queryList(
								mSearchKey.getBytes("utf-8"),
								mSearchKey.getBytes("utf-8").length,
								searchBuffer, bufferLen);
						if (code != -1) {
							result = ParseUtils.parseList(searchBuffer);
						}
					} else {
						bufferLen = 4 + 16 * 1000;
						searchBuffer = new byte[bufferLen];
						int code = IMQuery.queryMessage(
								mSearchKey.getBytes("utf-8"),
								mSearchKey.getBytes("utf-8").length,
								(short) mSessionType, mSessionId, searchBuffer,
								bufferLen);
						if (code != -1) {
							result = ParseUtils.parseMessage(searchBuffer);
						}
					}
				} catch (UnsupportedEncodingException e) {
					Log.e("log", "search error enocding", e);
				} catch (ParserException e) {
					Log.e("log", "search error ParserException", e);
				} catch (Exception e) {
					Log.e("log", "search error", e);
				}
			}

			searchBuffer = null;
			//search db
			//return format
			
			return result;
		}

		@Override
		protected void onPostExecute(Result result) {
			if (mSearchListener != null) {
				mSearchListener.onSearchEnd(mSearchType, result);
//				mSearchListener.onSearchEnd(format);
			}
		}

	}

}
