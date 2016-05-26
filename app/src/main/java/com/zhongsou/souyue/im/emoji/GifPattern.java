package com.zhongsou.souyue.im.emoji;

import com.zhongsou.souyue.im.module.ExpressionBean;
import com.zhongsou.souyue.im.module.GifBean;
import com.zhongsou.souyue.im.util.Slog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zoulu on 2015/1/8 Description:
 */
public class GifPattern {
	private static GifPattern mFaceConversionUtil;
	// 每页gif图片数量
	public static int number = 10;

	public static GifPattern getInstace() {
		if (mFaceConversionUtil == null) {
			mFaceConversionUtil = new GifPattern();
		}
		return mFaceConversionUtil;
	}

	/** GIF表情分页的结果集合 */
	public List<List<GifBean>> gifLists = new ArrayList<List<GifBean>>();

	// 组装gif数据
	public List<List<GifBean>> makeData(int number, String[] gifnames,
			String[] gifrealNum, String[] gifsendName, String[] gifurl,
			int[] gifdrawableid) {
		gifLists.clear();
		return getData(number, gifnames, gifrealNum, gifsendName, gifurl,
				gifdrawableid);

	}

	/**
	 * 分页 核心算法
	 * 
	 * @param number
	 *            每页多少个表情
	 * @param gifnames
	 *            表情名字数组
	 * @param gifrealNum
	 *            GIF动态图路径数组
	 * @param gifsendName
	 *            表情发送名字数组
	 * @param gifdrawableid
	 *            gif静态图的id
	 * @return
	 */
	private List<List<GifBean>> getData(int number, String[] gifnames,
			String[] gifrealNum, String[] gifsendName, String[] gifurl,
			int[] gifdrawableid) {
		int n = (int) Math.round(gifnames.length / number + 0.5);
		for (int j = 0; j < n; j++) {
			List<GifBean> gif = new ArrayList<GifBean>();
			for (int i = 0; i < gifnames.length; i++) {
				GifBean gifBean = new GifBean();
				if (j > 0 && ((number - 1) * j + i) < gifnames.length) {
					if (i > (j - 1) && ((number - 1) * j + i) >= j * number
							&& ((number - 1) * j + i) < (j + 1) * number) {
						gifBean.setGifRealName(gifnames[(number - 1) * j + i]);
						gifBean.setGifName(gifsendName[(number - 1) * j + i]);
						gifBean.setGifid(gifrealNum[(number - 1) * j + i]);
						gifBean.setGifurl(GifInfo.YOUPAIYUN_URL
								+ (gifurl[(number - 1) * j + i]));
						gifBean.setGifDrawableId(gifdrawableid[(number - 1) * j
								+ i]);
						gif.add(gifBean);
					}
				} else if (j == 0) {
					if (i < number) {
						gifBean.setGifRealName(gifnames[i]);
						gifBean.setGifName(gifsendName[i]);
						gifBean.setGifid(gifrealNum[i]);
						gifBean.setGifurl(GifInfo.YOUPAIYUN_URL
								+ (gifurl[(number - 1) * j + i]));
						gifBean.setGifDrawableId(gifdrawableid[(number - 1) * j
								+ i]);
						gif.add(gifBean);
					}
				}
			}
			gifLists.add(gif);
		}
		return gifLists;
	}

	/**
	 * 分页
	 * 
	 * @param pageSize
	 * @param expressionBeans
	 * @return
	 */
	public List<List<ExpressionBean>> getExpressionByPage(int pageSize,
			List<ExpressionBean> expressionBeans) {
		if (expressionBeans == null) {
			Slog.d("callback", "-------gif package has problem!");
			return null;
		}
		List<List<ExpressionBean>> compoundList = new ArrayList<List<ExpressionBean>>();
		int pageNum = expressionBeans.size() % pageSize == 0 ? expressionBeans
				.size() / pageSize : (expressionBeans.size() / pageSize) + 1;
		for (int i = 0; i < pageNum-1; i++) {
			compoundList.add(expressionBeans.subList(i * pageSize, (i + 1)
					* pageSize));
		}
		compoundList.add(expressionBeans.subList((pageNum-1)*pageSize,
				expressionBeans.size()));
		return compoundList;

	}

}
