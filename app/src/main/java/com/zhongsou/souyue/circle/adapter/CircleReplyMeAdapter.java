package com.zhongsou.souyue.circle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.model.CircleBlogReply;
import com.zhongsou.souyue.im.emoji.EmojiPattern;
import com.zhongsou.souyue.ui.RoundRectImageView;
import com.zhongsou.souyue.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CircleReplyMeAdapter extends BaseAdapter{
	
	private Context context;
	private LayoutInflater inflater;
	private ImageLoader imgloader;
    private DisplayImageOptions options;
	private List<CircleBlogReply> blogList;
	private SimpleDateFormat format;
	public CircleReplyMeAdapter(Context context){
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.blogList = new ArrayList<CircleBlogReply>();
		format = new SimpleDateFormat("MM-dd hh:mm");
		//初始化图片加载类和其配置选项
		this.imgloader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .showImageOnFail(R.drawable.circle_default_head)
                .showImageOnLoading(R.drawable.circle_default_head)
                .showImageForEmptyUri(R.drawable.circle_default_head)
                .build();
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return blogList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return blogList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder vh = new ViewHolder();
		if(convertView == null){
			convertView = inflater.inflate(R.layout.circle_replyme_item, null);
			vh.subImage = (RoundRectImageView)convertView.findViewById(R.id.subImage);
			vh.subName = (TextView)convertView.findViewById(R.id.subName);
			vh.subTime = (TextView)convertView.findViewById(R.id.subTime);
			vh.subText = (TextView)convertView.findViewById(R.id.subText);
			vh.mainImage = (ImageView)convertView.findViewById(R.id.mainImage);
			vh.mainName = (TextView)convertView.findViewById(R.id.mainName);
			vh.mainText =(TextView)convertView.findViewById(R.id.mainText);
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder) convertView.getTag();
		}
		
		imgloader.displayImage(blogList.get(position).getSubBlog().getImage_url(),vh.subImage,options);//vh.subImage.setImageBitmap();
		vh.subName.setText(blogList.get(position).getSubBlog().getNickname());
		vh.subTime.setText(StringUtils.convertDate(Long.valueOf(blogList.get(position).getSubBlog().getCreate_time())+""));
//		format.format(new Date(Long.valueOf(blogList.get(position).getSubBlog().getCreate_time()));
		if("".equals(blogList.get(position).getSubBlog().getContent())){
			vh.subText.setText(R.string.clickto_detail);
		}else{
			vh.subText.setText(EmojiPattern.getInstace().getExpressionString(context,  blogList.get(position).getSubBlog().getContent()));
		}
		if(blogList.get(position).getMainBlog().getImages()!=null && blogList.get(position).getMainBlog().getImages().size()>0){
			imgloader.displayImage(blogList.get(position).getMainBlog().getImages().get(0),vh.mainImage,options);
		}else{
			imgloader.displayImage(blogList.get(position).getMainBlog().getImage_url(),vh.mainImage,options);//vh.mainImage.setImageBitmap(null);
		}
		vh.mainName.setText("@"+blogList.get(position).getMainBlog().getNickname());
		vh.mainText.setText(EmojiPattern.getInstace().getExpressionString(context,  blogList.get(position).getMainBlog().getBrief()));
		return convertView;
	}
	
	
	
	public List<CircleBlogReply> getBlogList() {
		return blogList;
	}

	public void setBlogList(List<CircleBlogReply> blogList) {
		this.blogList = blogList;
	}
	
	public void addBlogList(List<CircleBlogReply> blogList){
		this.blogList.addAll(blogList);
	}
	
	class ViewHolder{
		ImageView mainImage;
		RoundRectImageView subImage;
		TextView subName,subTime,subText,mainName,mainText;
	}

}
