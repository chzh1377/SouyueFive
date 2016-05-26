package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import com.zhongsou.souyue.BuildConfig;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.utils.ImageUtil;

import java.util.List;

public class BolgImageAdapter extends BaseAdapter {

	private List<String> bolgImageList;
	private Context context;

	public BolgImageAdapter(Context context, List<String> bolgImageList) {
		this.context = context;
		this.bolgImageList = bolgImageList;
		Log.v("Huang", "bolgImageList.size()-->Adapter:" + bolgImageList.size());
	}

	public int getCount() {
		return bolgImageList.size();
	}

	public Object getItem(int position) {
		return bolgImageList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View view;
		ViewHolder holder;
		if (convertView == null) {
			view = View.inflate(context, R.layout.bolg_image_item, null);
			holder = new ViewHolder();
			holder.imgBtn_bolg_image = (ImageButton) view
					.findViewById(R.id.imgBtn_bolg_image);
			view.setTag(holder);

		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		String bolg_Path = null;
		Bitmap bm = null;
		if (bolgImageList != null && bolgImageList.size() > 0) {
			bolg_Path = bolgImageList.get(position);
			if (!TextUtils.isEmpty(bolg_Path)) {
				
				if(bolg_Path.equals("add_pic")){
					holder.imgBtn_bolg_image
					.setImageResource(R.drawable.add_pic);
				}else{
					BitmapFactory.Options options = ImageUtil.getCaculateSize(bolg_Path,holder.imgBtn_bolg_image);
					if(bolg_Path.toLowerCase().contains("http")){
						//query.id(holder.imgBtn_bolg_image).image(bolg_Path, true, true);
                                                      PhotoUtils.showCard(PhotoUtils.UriType.HTTP,bolg_Path,holder.imgBtn_bolg_image);
					}else{
						bm = BitmapFactory.decodeFile(bolg_Path, options);
						if(BuildConfig.DEBUG) {
							Log.e("SOU_YUE", bolg_Path+";;"+options.inSampleSize+"");
						}
						holder.imgBtn_bolg_image.setImageBitmap(bm);
					}
				}
				
			}
			holder.bolgImagePath = bolg_Path;
		}
		return view;
	}

//	/**
//	 * 根据image需要显示的大小区计算sampsize的值
//	 * @param imgBtn_bolg_image
//	 * @return
//	 */
//	private Options getCaculateSize(String file, ImageButton img) {
//		Options options = new BitmapFactory.Options();
//		options.inJustDecodeBounds = true;
//		options.inPurgeable = true;
//		options.inInputShareable = true;
//		BitmapFactory.decodeFile(file, options);
//		LayoutParams lp = img.getLayoutParams();
//		int with = lp.width;
//		int height = lp.height;
//		int size = calculateInSampleSize(options, with, height);
//		options = new BitmapFactory.Options();
//		options.inSampleSize = size;
//		Log.e("SOU_YUE", "options.inSampleSize="+options.inSampleSize);
//		options.inJustDecodeBounds = false;
//		options.inPurgeable = true;
//		options.inInputShareable = true;
//		return options;
//	}
//	public static int calculateInSampleSize(BitmapFactory.Options options,
//            int reqWidth, int reqHeight) {
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//        if (height > reqHeight || width > reqWidth) {
//            final int heightRatio = Math.round((float) height / (float) reqHeight);
//            final int widthRatio = Math.round((float) width / (float) reqWidth);
//            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
//
//            final float totalPixels = width * height;
//
//            final float totalReqPixelsCap = reqWidth * reqHeight * 2;
//            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
//                inSampleSize++;
//            }
//        }
//        return inSampleSize;
//    }


	public static class ViewHolder {
		ImageButton imgBtn_bolg_image;
		public String bolgImagePath;
	}

	public void addItemPaht(String item) {
		bolgImageList.add(item);
	}
	
	public boolean seletePaht(String item) {
		return bolgImageList.contains(item);
	}

	public void clearBolgImageItem() {
		if (this.bolgImageList != null) {
			this.bolgImageList.clear();
		}
	}
	public List<String> getBolgImageList() {
		return bolgImageList;
	}
	
	public void clearBolgImageItem(String str) {
		if (this.bolgImageList != null && str != null) {
			this.bolgImageList.remove(str);
		}
	}

}
