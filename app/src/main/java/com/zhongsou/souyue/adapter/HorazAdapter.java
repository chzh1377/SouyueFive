package com.zhongsou.souyue.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.HomeBallBean;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.List;

public class HorazAdapter extends BaseAdapter {
	private List<HomeBallBean> mDatas;
//	private int mCount;
   // private ImageLoader imgloader;
//    private DisplayImageOptions options;
	private Context mcontext;
	
	public HorazAdapter(Context _context) {
		mcontext = _context;
		//this.imgloader = ImageLoader.getInstance();

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
	
	@Override
	public int getCount() {
		return mDatas == null?0:mDatas.size();
	}

	@Override
	public HomeBallBean getItem(int position) {
		if (mDatas==null||mDatas.size()==0||position>=mDatas.size()){
			Log.e("return","null");
			return null;
		}
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mDatas.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder = null;
		if(view == null ){
			view  = View.inflate(mcontext, R.layout.circletitle_item, null);
			view.setDrawingCacheEnabled(false);
			holder = new ViewHolder();
			holder.circle_image=(ImageView) view.findViewById(R.id.circle_image);
			holder.circle_text=(TextView) view.findViewById(R.id.circle_text);
			holder.home_title_circle_grouptips  = (ImageView) view.findViewById(R.id.home_title_circle_grouptips);
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
			holder.circle_image.setImageResource(R.drawable.circle_title_default_icon_c);
		}
		HomeBallBean circletitle=getItem(position);
		String title = circletitle.getTitle();
		title=title==null?"":title;
		int size = title.getBytes().length;
        if(title!=null&&size>15){
            holder.circle_text.setText(title.substring(0, 5));
        }else{
			holder.circle_text.setText(title);
        }
		//imgloader.cancelDisplayTask(holder.circle_image);
		 PhotoUtils.getImageLoader().cancelDisplayTask(holder.circle_image);
		holder.circle_image.setVisibility(View.VISIBLE);
        if(circletitle.isSubscription()){
        	holder.circle_image.setImageDrawable(null);
			holder.circle_image.setVisibility(View.INVISIBLE);
        }else if (circletitle.isSub()) {
        	holder.circle_image.setImageResource(R.drawable.circle_title_subscription);
		}else {
			if(StringUtils.isNotEmpty(circletitle.getImage())){
				//imgloader.displayImage(circletitle.getImage(), holder.circle_image,options);
                                  PhotoUtils.showCard( PhotoUtils.UriType.HTTP,circletitle.getImage(),holder.circle_image, MyDisplayImageOption.homeTitle);
			}else{
				//imgloader.displayImage("drawable://"+R.drawable.circle_title_default_icon_c,holder.circle_image);
                                  PhotoUtils.showCard(PhotoUtils.UriType.DRAWABLE,R.drawable.circle_title_default_icon_c+"",holder.circle_image,MyDisplayImageOption.homeTitle);
			}

		}
		if(HomeBallBean.GROUP_NEWS.equals(circletitle.getCategory())){
			holder.home_title_circle_grouptips.setVisibility(View.VISIBLE);
		}else{
			holder.home_title_circle_grouptips.setVisibility(View.GONE);
		}
		return view;
	}

	class ViewHolder{
    	ImageView circle_image;
        TextView circle_text;
		ImageView home_title_circle_grouptips;
    }
	
	public void setData(List<HomeBallBean> _data){
		mDatas = _data;
	}

	public List<HomeBallBean> getData(){
		return mDatas;
	}


//	@Override
//	public float[] getItemSize() {
//		float den = mcontext.getResources().getDisplayMetrics().density;
//		return new float[]{den*ItemHView.mWidth_dpi,0};
//	}

}
