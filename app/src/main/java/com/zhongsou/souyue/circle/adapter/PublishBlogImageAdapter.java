package com.zhongsou.souyue.circle.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.zhongsou.souyue.BuildConfig;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.utils.ImageUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

public class PublishBlogImageAdapter extends BaseAdapter {

	private List<String> bolgImageList;
	private Context context;
	//private AQuery query;
    public static boolean isAdd;

	public PublishBlogImageAdapter(Context context, List<String> bolgImageList) {
		this.context = context;
		this.bolgImageList = bolgImageList;
		//query = new AQuery(context);
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
                                                      PhotoUtils.showCard( PhotoUtils.UriType.HTTP,bolg_Path,holder.imgBtn_bolg_image, MyDisplayImageOption.defaultOption);
					}else{
                        if(!isAdd){       //删除和左右滑动时候，防止重刷
                            bm = BitmapFactory.decodeFile(bolg_Path, options);
                            if(BuildConfig.DEBUG) {
                                Log.e("SOU_YUE", bolg_Path+";;"+options.inSampleSize+"");
                            }
                            holder.imgBtn_bolg_image.setImageBitmap(bm);
                        }else{
//                            new BitmapWorkerTask(holder.imgBtn_bolg_image).execute(bolg_Path);
							new BitmapWorker(holder.imgBtn_bolg_image).showImage(bolg_Path);
                        }
					}
				}
				
			}
			holder.bolgImagePath = bolg_Path;
		}
		return view;
	}

    private Bitmap extractThumbNail(String filePath) {
        Bitmap bm = ImageUtil.getSmallBitmap(filePath, 480 * 480);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(bm != null) {
            bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            byte[] b = baos.toByteArray();
            ByteArrayInputStream bas = new ByteArrayInputStream(b);
            return BitmapFactory.decodeStream(bas);
        }else{
            return null;
        }
    }

	/**
	 * 逻辑跟上面的哥们一样一样的。压缩图片.显示.
	 */
	class BitmapWorker {
		private final WeakReference<ImageView> imageViewReference;
		private String data = "";
		private String smallImagePath = "";

		public BitmapWorker(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		public void showImage(final String imagePath) {
			new Thread() {
				@Override
				public void run() {
					data = imagePath;
					final Bitmap bitmap = extractThumbNail(data);
					if (imageViewReference != null) {
						final ImageView imageView = imageViewReference.get();
						if (imageView != null) {
							if (bitmap != null) {
								imageView.post(new Runnable() {
									@Override
									public void run() {
										imageView.setImageBitmap(bitmap);
									}
								});
							}
						}
					}
				}
			}.start();
		}
	}

	/**
	 * async task 不知道为啥不好使了。干掉重写
	 */

//    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
//        private final WeakReference<ImageView> imageViewReference;
//        private String data = "";
//        private String smallImagePath = "";
//
//        public BitmapWorkerTask(ImageView imageView) {
//            // Use a WeakReference to ensure the ImageView can be garbage collected
//            imageViewReference = new WeakReference<ImageView>(imageView);
//        }
//
//        // Decode image in background.
//        @Override
//        protected Bitmap doInBackground(String... params) {
//            data = params[0];
//            smallImagePath = new File(ImageUtil.getSelfDir(), System.currentTimeMillis() + "blog_image").getAbsolutePath();
//            Bitmap bitmap = extractThumbNail(data);
//            return bitmap;
//        }
//
//        // Once complete, see if ImageView is still around and set bitmap.
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            if (imageViewReference != null) {
//                final ImageView imageView = imageViewReference.get();
//                if (imageView != null) {
//                    if(bitmap != null) {
//                        imageView.setImageBitmap(bitmap);
//                    }
//                }
//            }
//        }
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
