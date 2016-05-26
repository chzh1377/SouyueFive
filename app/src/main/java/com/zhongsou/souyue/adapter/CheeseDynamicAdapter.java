package com.zhongsou.souyue.adapter;

/**
 * Author: alex askerov
 * Date: 9/9/13
 * Time: 10:52 PM
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.InterestBean;
import com.zhongsou.souyue.module.SubscribeItem;
import com.zhongsou.souyue.ui.dynamicgrid.BaseDynamicGridAdapter;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.List;

/**
 * @author zhongs
 * 
 */
public class CheeseDynamicAdapter extends BaseDynamicGridAdapter {
	private boolean showDelView;
	private Context context;
	private DeleteListener deleteListener;
	private ImageLoader imgloader;
	private DisplayImageOptions options;
	public interface DeleteListener {
		public void removeItem(int position);
	}

	public void setDeleteListener(DeleteListener deleteListener) {
		this.deleteListener = deleteListener;
	}

	public void setShowDelView(boolean showDelView) {
		this.showDelView = showDelView;
	}

	public CheeseDynamicAdapter(Context context, List<?> items, int columnCount) {
		super(context, items, columnCount);
		this.context = context;
		this.imgloader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().displayer(new SimpleBitmapDisplayer())
		        .cacheOnDisk(true)
		        .showImageOnLoading(R.drawable.circle_title_default_icon_c)
//                .postProcessor(new BitmapProcessor() {
//                    @Override
//                    public Bitmap process(Bitmap bitmap) {
//                        return getClipBitmap(bitmap);
////                        return bitmap;
//                    }
//                })
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new RoundedBitmapDisplayer(150)) // 设置成全圆图片
		        .cacheInMemory(true)
				.build();
	}

    public Bitmap getClipBitmap(Bitmap _bitmap){
        int w = _bitmap.getWidth();
        int h = _bitmap.getHeight();
        int bitSize = 0;
        int x = 0;
        int y = 0;
        if(w>h){
            bitSize = h;
            x = (w-h)/2;
        }else {
            bitSize = w;
            y = (h-w)/2;
        }
        return Bitmap.createBitmap(_bitmap,x,y,bitSize,bitSize);
    }
    
    public void pause(){
    	imgloader.pause();
    }
    
    public void resume(){
    	imgloader.resume();
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CheeseViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_grid, null);
			holder = new CheeseViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (CheeseViewHolder) convertView.getTag();
		}
		holder.build(getItem(position), position);
		return convertView;
	}

	private class CheeseViewHolder {
		private TextView titleText;
		private ImageView image;
		private ImageView del;
		private ImageView isSecret;
		private  long mId;

		private CheeseViewHolder(View view) {
			titleText = (TextView) view.findViewById(R.id.item_title);
			image = (ImageView) view.findViewById(R.id.item_img);
			del = (ImageView) view.findViewById(R.id.item_del);
			isSecret = (ImageView) view.findViewById(R.id.issecret);
		}

		void build(Object item, final int position) {
			if (item instanceof SubscribeItem) {
				
				SubscribeItem it = (SubscribeItem) item;
//				if(it.id()!=mId){
					
					mId = it.id();
					titleText.setText(it.keyword());
					if(StringUtils.isNotEmpty(it.image())){
						imgloader.displayImage(it.image(), image, options);
					}else{
						imgloader.displayImage("drawable://"+R.drawable.news_default_img, image, options);
					}
//				}
			} else if (item instanceof InterestBean){
				InterestBean inter = (InterestBean) item;
//				if(inter.getId()!=mId){
					mId = inter.getId();
					titleText.setText(inter.getName());
					if(StringUtils.isNotEmpty(inter.getImage())){
						imgloader.displayImage(inter.getImage(), image, options);
					}else{
						imgloader.displayImage("drawable://"+R.drawable.news_default_img, image, options);
					}
					if(inter.getType() == 0)
						isSecret.setVisibility(View.GONE);
					else if(inter.getType() == 1)
						isSecret.setVisibility(View.VISIBLE);
//				}
			}else{
			    titleText.setText("");
			    image.setImageResource(R.drawable.news_default_img);
			}

			if (showDelView){
				del.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						deleteListener.removeItem(position);
					}
				});
				del.setVisibility(View.VISIBLE);
			}else{
				del.setVisibility(View.INVISIBLE);
			}
		}
	}
}