package com.zhongsou.souyue.im.download;

import com.zhongsou.souyue.im.util.Slog;

/**
 * 
 * 纪录下载器的信息到内存
 * @author wangqiang
 */
public class MemoryPackageDao {

	private static final int INIT = 0;// 定义三种下载的状态：0初始化状态，1正在下载状态，2停止状态
	private static final int DOWNLOADING = 1;
	private static final int STOP = 2;
	
	/**
	 * 初始化下载纪录
	 * @param info
	 */

	public static void saveInfo(LoadInfo info) {
		if (info != null) {
			ListState.completeSizes
			.put(info.getPackageId(), info.getComplete());
			ListState.fileSizes.put(info.getPackageId(), info.getFileSize());
			ListState.downloadUrl.put(info.getPackageId(), info.getUrlstring());
		}
	}

	/**
	 * 
	 * 更新下载纪录 
	 * @param packageId
	 * @param completeSize
	 *            完成度
	 * @param context
	 */

	public static void updateInfo(String packageId, int completeSize) {

		ListState.completeSizes.put(packageId, completeSize);

	}

	/**
	 * 
	 * 根据id查询下载纪录进度
	 * @param packageId
	 * @param context
	 * @return
	 */

	public static LoadInfo getInfos(String packageId) {
		if (!isHasInfo(packageId) || packageId == null)
			return null;
		Slog.d("MapDao", "packageId-------------" + packageId);
		Integer fileSize = ListState.fileSizes.get(packageId);
		Integer complete = ListState.completeSizes.get(packageId);
		String urlstring = null;
		if (fileSize == null || complete == null)
			return null;
		LoadInfo info = new LoadInfo(fileSize, complete, urlstring, packageId);
		return info;
	}

	/**
	 * 
	 * 判断packageid开始下载
	 * @param packageId
	 * @return
	 */

	public static boolean isHasInfo(String packageId) {
		if (ListState.state.containsKey(packageId)) {
			int state = ListState.state.get(packageId);
			return state == INIT || state == DOWNLOADING;
		}
		return false;
	}

	/**
	 * 
	 * 清除下载纪录缓存信息
	 * @param packageId
	 */

	private static void deleteInfo(String packageId) {
		if (ListState.completeSizes.containsKey(packageId))
			ListState.completeSizes.remove(packageId);
		if (ListState.fileSizes.containsKey(packageId))
			ListState.fileSizes.remove(packageId);
		if (ListState.downloadUrl.containsKey(packageId))
			ListState.downloadUrl.remove(packageId);
		if (ListState.state.containsKey(packageId))
			ListState.state.remove(packageId);
	}
	
	/**
	 * 停止线程下载/下载完成
	 * @param packageId
	 */
	public static void stopThread(String packageId) {
		if (!isHasInfo(packageId)) {
			return;
		}
		deleteInfo(packageId);
	}
}
