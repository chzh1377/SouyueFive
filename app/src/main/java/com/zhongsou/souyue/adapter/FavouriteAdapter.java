package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.fragment.FavoriteFragment;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;
import com.zhongsou.souyue.module.Favorite;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * common adapter
 *
 * @author zhangliang01@zhongsou.com
 */
public class FavouriteAdapter extends BaseAdapter {
    private Context mContext;
    private FavoriteFragment favoriteFragment;
    private List<Favorite> data = new ArrayList<Favorite>();
    private boolean favouriteEdit = false;
    private static final int TYPE_MAX_COUNT = 5;
    private int count;
    protected int height, width;
    private int deviceWidth;
    
    public FavouriteAdapter(FavoriteFragment favoriteFragment) {
        this.favoriteFragment = favoriteFragment;
        this.mContext = favoriteFragment.getActivity();

        viewInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        deviceWidth = CircleUtils.getDeviceWidth(mContext);
        width = (deviceWidth - DeviceUtil.dip2px(mContext, 48)) / 3;
        height = (int) ((2 * width) / 3);
    }

    public void addData(List<Favorite> dataInfo) {
        this.data.addAll(dataInfo);
    }

    public void setData(List<Favorite> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    public List<Favorite> getDatas() {
        return data;
    }

    @Override
    public int getCount() {
        int count = data.size();
        if (hasMore && !favouriteEdit) {//加载更多
            count += 1;
        }
        return count;
    }

    @Override
    public Favorite getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
    	 int type = super.getItemViewType(position);
    	 int len = this.data.size();
    	 if (position < len) {
           type = this.data.get(position).getFavoriteLayoutType();
    	 } 
         return type;
    }
   
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (hasMore && (position == getCount() - 1) && !favouriteEdit) {//加载更多
            convertView = viewInflater.inflate(R.layout.load_more, null);
            favoriteFragment.loadingMore(convertView);
            return convertView;
        }
        
        ViewHolder holder = null;
        Favorite currFav = data.get(position);
        
        int layoutType = getItemViewType(position);  
        
        if (convertView == null || convertView.getTag() == null) {
        	holder = new ViewHolder();
    		switch (layoutType) {
    		case Favorite.LAYOUT_TYPE_NO_PIC:
    		    convertView = viewInflater.inflate(R.layout.favorite_list_item_nopic, null);
    		    break;
    		case Favorite.LAYOUT_TYPE_ONE_PIC:
    		    convertView = viewInflater.inflate(R.layout.favorite_list_item_pic1, null);
    		    holder.image = (ImageView) convertView.findViewById(R.id.favourite_list_item_image);
    		    RelativeLayout.LayoutParams params01 = (RelativeLayout.LayoutParams) holder.image
                        .getLayoutParams();
                params01.width = width;
                params01.height = height;
                holder.image.setLayoutParams(params01);
    		    break;
    		default:
    		    break;
    		}
    		//holder.favorite = currFav;
            holder.text = (TextView) convertView.findViewById(R.id.favourite_list_item_title);
            holder.source = (TextView) convertView.findViewById(R.id.favourite_list_item_source);
            holder.imageButton = (ImageButton) convertView.findViewById(R.id.item_button);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();
        if (currFav != null){
        	
			if(layoutType == Favorite.LAYOUT_TYPE_ONE_PIC) {
				PhotoUtils.showCard(UriType.HTTP, currFav != null ? currFav.image():"", holder.image, MyDisplayImageOption.getOptions(R.drawable.default_small));
			}
			holder.text.setText(currFav.title());
			if(!StringUtils.isEmpty(currFav.source())) {
                                        holder.source.setVisibility(View.VISIBLE);
				holder.source.setText(currFav.source());
			}else {
				holder.source.setVisibility(View.INVISIBLE);
			}
		    DelHelper delHelper = new DelHelper();
	        delHelper.url = currFav.url();
	        delHelper.position = position;
	        delHelper.dataType = currFav.getDataType();
	        delHelper.blogId = currFav.getBlogId();
	        holder.imageButton.setTag(delHelper);
	        
	        if (favouriteEdit) {
	            holder.imageButton.setVisibility(View.VISIBLE);
	        } else {
	            holder.imageButton.setVisibility(View.GONE);
	        }

	        holder.imageButton.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                favoriteFragment.onClick(v);
	            }
	        });
        }
        return convertView;
    }

    /**
     * 获取ItemView
     * @param position
     * @param convertView
     * @return
     */
	private View getInflaterView(final int position, View convertView,ViewHolder holder) {
		int layoutType = getItemViewType(position);
		switch (layoutType) {
		case Favorite.LAYOUT_TYPE_NO_PIC:
		    convertView = viewInflater.inflate(R.layout.favorite_list_item_nopic, null);
		    break;
		case Favorite.LAYOUT_TYPE_ONE_PIC:
		    convertView = viewInflater.inflate(R.layout.favorite_list_item_pic1, null);
            holder.image = (ImageView) convertView.findViewById(R.id.favourite_list_item_image);
		    break;
		default:
			convertView = viewInflater.inflate(R.layout.favorite_list_item_nopic, null);
		    break;
		}
        holder.text = (TextView) convertView.findViewById(R.id.favourite_list_item_title);
        holder.source = (TextView) convertView.findViewById(R.id.favourite_list_item_source);
        holder.imageButton = (ImageButton) convertView.findViewById(R.id.item_button);
		return convertView;
	}

    protected LayoutInflater viewInflater;
    private boolean hasMore;

    public static class ViewHolder {
        public Favorite favorite;
        TextView text, source, time;
        ImageButton imageButton;
        ImageView image;
    }

    public class DelHelper {
        public int position;
        public String url;
        public int dataType;
        public long blogId;

    }

    public void setFavouriteEdit(boolean favouriteEdit) {
        this.favouriteEdit = !favouriteEdit;
        notifyDataSetChanged();
    }

    public void deleteData(int position) {
        data.remove(position);
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

    public static String string2Date(String time) {
        String[] strings = time.split(" ");
        if (strings.length == 2) {
            String[] temp1 = strings[0].split("-");
            String[] temp2 = strings[1].split(":");
            if (temp1.length == 3 && temp2.length == 3) {
                time = temp1[1] + "-" + temp1[2] + " " + temp2[0] + ":" + temp2[1];
                return time;
            }
        }
        return time;
    }

}
