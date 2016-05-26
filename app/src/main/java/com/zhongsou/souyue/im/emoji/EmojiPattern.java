package com.zhongsou.souyue.im.emoji;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * emoji 匹配
 * 
 * @author zwb
 */
public class EmojiPattern{

	/** 每一页表情的个数 */
	private int pageSize = 20;

	private static EmojiPattern mFaceConversionUtil;

	/** 保存于内存中的表情HashMap */
	private HashMap<String, String> emojiMap = new HashMap<String, String>();

	/** 保存于内存中的表情集合 */
	private List<Emoji> emojis = new ArrayList<Emoji>();

	/** 表情分页的结果集合 */
	public List<List<Emoji>> emojiLists = new ArrayList<List<Emoji>>();

    private final int mEmojiCount = 156;

    private final int mPageCount = 8;

	private EmojiPattern() {

	}

	public static EmojiPattern getInstace() {
		if (mFaceConversionUtil == null) {
			mFaceConversionUtil = new EmojiPattern();
		}
		return mFaceConversionUtil;
	}

	/**
	 * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
	 * 
	 * @param context
	 * @param str
	 * @return
	 */
	public SpannableString getExpressionString(Context context, String str) {
        SpannableString spannableString = null;
        if(str != null) {
            spannableString = new SpannableString(str);
        }
		// 正则表达式比配字符串里是否含有表情
		String zhengze = "\\[[^\\]]+\\]";
		// 通过传入的正则表达式来生成一个pattern
		Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);
		try {
			dealExpression(context, spannableString, sinaPatten, 0);
		} catch (Exception e) {
//			Log.e("dealExpression", e.getMessage());
		}
		return spannableString;
	}

	/**
	 * 添加表情
	 * 
	 * @param context
	 * @param imgId
	 * @param spannableString
	 * @return
	 */
	public SpannableString addFace(Context context, int imgId,
			String spannableString) {
		if (TextUtils.isEmpty(spannableString)) {
			return null;
		}
		Drawable drawable = context.getResources().getDrawable(imgId);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth() * 2 / 3 , drawable.getIntrinsicHeight() * 2 / 3);
		ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
		SpannableString spannable = new SpannableString(spannableString);
		spannable.setSpan(span, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
//				imgId);
//		bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, true);
//		ImageSpan imageSpan = new ImageSpan(context, bitmap);
//		SpannableString spannable = new SpannableString(spannableString);
//		spannable.setSpan(imageSpan, 0, spannableString.length(),
//				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannable;
	}

	/**
	 * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
	 * 
	 * @param context
	 * @param spannableString
	 * @param patten
	 * @param start
	 * @throws Exception
	 */
	private void dealExpression(Context context,
			SpannableString spannableString, Pattern patten, int start)
			throws Exception {
		Matcher matcher = patten.matcher(spannableString);
		for(int i = 0;i<spannableString.length();i++){
		while (matcher.find()) {
			String key = matcher.group();
			// 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
			if (matcher.start() < start) {
				continue;
			}
			String value = emojiMap.get(key);
			if (TextUtils.isEmpty(value)) {
				continue;
			}
			int resId = context.getResources().getIdentifier(value, "drawable",
					context.getPackageName());
			// 通过上面匹配得到的字符串来生成图片资源id
			if (resId != 0) {
//				Bitmap bitmap = BitmapFactory.decodeResource(
//						context.getResources(), resId);
//				int width = Utils.getScreenWidth(context);
//				bitmap = Bitmap.createScaledBitmap(bitmap, width/6, width/6, true);
//				ImageSpan imageSpan = new ImageSpan(bitmap);
				int end = matcher.start() + key.length();
//				spannableString.setSpan(imageSpan, matcher.start(), end,
//						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//				if (end < spannableString.length()) {
//					// 如果整个字符串还未验证完，则继续。。
////					dealExpression(context, spannableString, patten, end);
//				}
				
				
				Drawable drawable = context.getResources().getDrawable(resId);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth() * 2 / 3, drawable.getIntrinsicWidth() * 2 / 3);
				ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
				spannableString.setSpan(span, matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				break;
			}
		}
	}
	}

	public void getFileText(Context context) {
		ParseData(FileUtils.getEmojiFile(context), context);
	}

	/**
	 * 解析字符
	 * 
	 * @param data
	 */
	private void ParseData(List<String> data, Context context) {
		emojiLists.clear();
		if (data == null) {
			return;
		}
		Emoji emojEentry;
		try {
			for (String str : data) {
				String[] text = str.split(",");
				String fileName = text[0]
						.substring(0, text[0].lastIndexOf("."));
				emojiMap.put(text[1], fileName);
				int resID = context.getResources().getIdentifier(fileName,
						"drawable", context.getPackageName());

				if (resID != 0) {
					emojEentry = new Emoji();
					emojEentry.setId(resID);
					emojEentry.setCharacter(text[1]);
					emojEentry.setFaceName(fileName);
					emojis.add(emojEentry);
				}
			}
			int pageCount = mPageCount;

			for (int i = 0; i < pageCount; i++) {
				emojiLists.add(getData(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取分页数据
	 * 
	 * @param page
	 * @return
	 */
	private List<Emoji> getData(int page) {
		int startIndex = page * pageSize;
		int endIndex = 0;
		if(page == mPageCount - 1){
			endIndex = mEmojiCount;
		}else{
			endIndex = startIndex + pageSize;
			
		}

		if (endIndex > emojis.size()) {
			endIndex = emojis.size();
		}
		List<Emoji> list = new ArrayList<Emoji>();
		list.addAll(emojis.subList(startIndex, endIndex));
		if (list.size() < pageSize) {
			for (int i = list.size(); i < pageSize; i++) {
				Emoji object = new Emoji();
				list.add(object);
			}
		}
		if (list.size() == pageSize) {
			Emoji object = new Emoji();
			object.setId(R.drawable.btn_msg_facedelete_selector);
			list.add(object);
		}
		return list;
	}
}