package com.zhongsou.souyue.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tuita.sdk.im.db.module.ServiceMessageRecent;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.ImUtils;
import com.zhongsou.souyue.im.view.SwipeListView;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.List;

/**
 * 服务号Adapter
 * @author zhangwb
 *
 */
public class IMSouYueMessageAdapter extends BaseAdapter {
    private Context mContext;
    private SwipeListView listView;
    private List<ServiceMessageRecent> data;
    private static final int ZEROCOUNT= 0;
    private DisplayImageOptions options;
    public interface OnDeleteListener {
        void onDeleteItem(int position);
    }

//    private OnDeleteListener deleteListener = new OnDeleteListener() {
//
//        @Override
//        public void onDeleteItem(int position) {
//            if (listView instanceof SwipeListView) {
//                notifyDataSetChanged();
//                ((SwipeListView) listView).onChanged();
//                ImserviceHelp.getInstance().db_delMessageRecent(data.get(position).getChat_id());
//                ImserviceHelp.getInstance().db_clearMessageRecentBubble(data.get(position).getChat_id());
//                data.remove(data.get(position));
//                notifyDataSetChanged();
//            }
//        }
//    };

    public void setData(List<ServiceMessageRecent> data){
        this.data=data;
        notifyDataSetChanged();
    }
    
    public IMSouYueMessageAdapter(Context mContext,SwipeListView listView){
        this.mContext=mContext;
        this.listView = listView;

        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .displayer(new RoundedBitmapDisplayer(10))
                .showImageOnLoading(R.drawable.default_head).build();
    }

    @Override
    public int getCount() {
        if(data!=null&&data.size()>0){
            return data.size();
        }else{
            return 0;
        }
        
    }

    @Override
    public ServiceMessageRecent getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.im_souyue_message_item, parent, false);
            holder.item_left = (RelativeLayout) convertView.findViewById(R.id.item_left);
            holder.item_right = (RelativeLayout) convertView.findViewById(R.id.item_right);
            holder.ivImage = (ImageView) convertView.findViewById(R.id.row_iv_image);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.row_tv_title);
            holder.bAction_delete = (Button) convertView.findViewById(R.id.row_btn_delete);
            holder.count_text=(TextView) convertView.findViewById(R.id.count_text);
            holder.row_tv_time=(TextView) convertView.findViewById(R.id.row_tv_time);
            holder.row_tv_content=(TextView) convertView.findViewById(R.id.row_tv_content);
            holder.im_notify_icon = (ImageView) convertView.findViewById(R.id.im_notify_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ServiceMessageRecent serviceMsgRe=(ServiceMessageRecent) getItem(position);
        
        LinearLayout.LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        holder.item_left.setLayoutParams(lp1);
        LinearLayout.LayoutParams lp2 = new LayoutParams(listView.getRightViewWidth(), LayoutParams.MATCH_PARENT);
        holder.item_right.setLayoutParams(lp2);
        
        if(serviceMsgRe!=null){
            if(serviceMsgRe.getService_name()!=null&&!"".equals(serviceMsgRe.getService_name().trim())){
                holder.tvTitle.setText(serviceMsgRe.getService_name());
            }
            if(serviceMsgRe.getBubble_num() != null && Integer.parseInt(serviceMsgRe.getBubble_num())>ZEROCOUNT){
                holder.count_text.setBackgroundResource(serviceMsgRe.getBy3() == null || serviceMsgRe.getBy3().equals("0") ? R.drawable.tool_atme_number : R.drawable.im_count_notify_gray);
                holder.count_text.setText(ImUtils.getBubleText(String.valueOf(serviceMsgRe.getBubble_num())));
                holder.count_text.setVisibility(View.VISIBLE);  
            }else{
                holder.count_text.setVisibility(View.INVISIBLE);  
            }
            holder.row_tv_time.setText(StringUtils.convertDate(String.valueOf(serviceMsgRe.getDate())));


            ImageLoader.getInstance().displayImage(serviceMsgRe.getService_avatar(),holder.ivImage,options);

//            aquery.id(holder.ivImage).image(serviceMsgRe.getService_avatar(), true, true);
        	}

        holder.im_notify_icon.setVisibility(serviceMsgRe.getBy3() == null || serviceMsgRe.getBy3().equals("0") ? View.INVISIBLE : View.VISIBLE);

            holder.row_tv_content.setText(serviceMsgRe.getDigst());            
        
        return convertView;
    }

    class ViewHolder {
        RelativeLayout item_left;
        RelativeLayout item_right;

        ImageView ivImage;
        TextView tvTitle,count_text;
        Button bAction_delete;
        TextView row_tv_time;
        TextView row_tv_content;
        ImageView im_notify_icon;
    }
    
}
