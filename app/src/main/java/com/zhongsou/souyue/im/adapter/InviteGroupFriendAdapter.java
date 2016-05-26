package com.zhongsou.souyue.im.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tuita.sdk.im.db.helper.PingYinUtil;
import com.tuita.sdk.im.db.module.GroupMembers;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.ContactModelUtil;
import com.zhongsou.souyue.im.util.ImUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zoulu
 * on 14-8-30
 * Description:@群好友adapter
 */
public class InviteGroupFriendAdapter extends BaseAdapter{
    public static Map<Long, Boolean> selected = new HashMap<Long, Boolean>();
    private List<GroupMembers> data;
    private ImageLoader imageLoader;
    private Context mContext;
    private String keyWord;

    public InviteGroupFriendAdapter(Context context,List<GroupMembers> data){
        mContext = context;
        this.data = data;
        imageLoader =ImageLoader.getInstance();
    }
    public String getKeyWord(){
        return keyWord;
    }
    public void setKeyWord(String keyWord){
        this.keyWord = keyWord;
    }
    @Override
    public int getCount() {
        if(data == null){
            return 0;
        }  else {
            return data.size();
        }
    }

    @Override
    public Object getItem(int i) {
        if (data.size() > i)
            return data.get(i);
        else
            return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        GroupMembers item = (GroupMembers) getItem(i);
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.circle_friend_list_view_row, null);
            holder = new ViewHolder();

            holder.ivImage = (ImageView) convertView.findViewById(R.id.row_iv_image);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.row_tv_title);
            holder.row_tv_beizhu = (TextView) convertView.findViewById(R.id.row_tv_beizhu);
            holder.row_tv_souyuename = (TextView) convertView.findViewById(R.id.row_tv_souyuename);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (TextUtils.isEmpty(item.getMember_avatar())) {
            holder.ivImage.setImageResource(R.drawable.default_head);
        } else {
            imageLoader.displayImage(item.getMember_avatar(),holder.ivImage);
        }

        String username = ContactModelUtil.getShowName(item);
        item.setLocalOrderHighLight(PingYinUtil.converter2FirstSpell(username));
        if (getKeyWord() == null)
            setKeyWord("");

        /**
         * 正常情况{如果有备注名，则显示备注名，否则显示昵称}
         */
        String local_order_Nick =  PingYinUtil.converter2FirstSpell(item.getNick_name()).toUpperCase();
        String local_order_comment = PingYinUtil.converter2FirstSpell(item.getConmmentName()).toUpperCase();
        String local_order_memberName = "";
        if(null != item.getMember_name()) {
            local_order_memberName = PingYinUtil.converter2FirstSpell(item.getMember_name().toUpperCase());
        }else {
            item.setMember_name("");
        }

        /**
         * by1是否修改过备注名（1修改，0未修改），by2类型
         */

        if(getKeyWord().equals("")){
            holder.tvTitle.setText(username);
            holder.row_tv_beizhu.setVisibility(View.GONE);
            holder.row_tv_souyuename.setVisibility(View.GONE);
        } else {
            if (item.getNick_name().toUpperCase().contains(getKeyWord()) || local_order_Nick.contains(getKeyWord())){
                if (item.getNick_name().equals(username)){
                    holder.tvTitle.setText(ImUtils.getHighlightText(username, item.getLocalOrderHighLight(), getKeyWord()));
                    holder.row_tv_beizhu.setVisibility(View.GONE);
                    holder.row_tv_souyuename.setVisibility(View.GONE);
                }else {
                    holder.tvTitle.setText(ImUtils.getHighlightText(username, item.getLocalOrderHighLight(), getKeyWord()));
                    holder.row_tv_souyuename.setVisibility(View.VISIBLE);
                    holder.row_tv_souyuename.setText(ImUtils.getHighlightText("昵称：", item.getNick_name(), local_order_Nick, getKeyWord()));
                }
            }

            if (item.getConmmentName().toUpperCase().contains(getKeyWord()) || local_order_comment.contains(getKeyWord())){
                holder.tvTitle.setText(ImUtils.getHighlightText(username, item.getLocalOrderHighLight(), getKeyWord()));
                if (item.getNick_name().toUpperCase().contains(getKeyWord()) || local_order_Nick.contains(getKeyWord())){
                    holder.row_tv_souyuename.setVisibility(View.VISIBLE);
                    holder.row_tv_souyuename.setText(ImUtils.getHighlightText("昵称：",item.getNick_name(), local_order_Nick, getKeyWord()));
                }else {
                    holder.row_tv_beizhu.setVisibility(View.GONE);
                    holder.row_tv_souyuename.setVisibility(View.GONE);
                }
            }
            if (item.getMember_name().toUpperCase().contains(getKeyWord()) || local_order_memberName.contains(getKeyWord())){
                holder.tvTitle.setText(ImUtils.getHighlightText(username, item.getLocalOrderHighLight(), getKeyWord()));
                if (item.getMember_name().equals(username)){
                    holder.row_tv_beizhu.setVisibility(View.GONE);
                }else {
                    holder.row_tv_beizhu.setVisibility(View.VISIBLE);
                    holder.row_tv_beizhu.setText(ImUtils.getHighlightText("群昵称：", item.getMember_name(), local_order_memberName, getKeyWord()));
                }
            }
//            else {
//                holder.tvTitle.setText(username);
//            }

        }
        //按照备注名，我的群昵称，搜悦昵称的优先级顺序显示
//        Contact c = ImserviceHelp.getInstance().db_getContactById(item.getMember_id());
//        if(c != null && !TextUtils.isEmpty(c.getComment_name())){
//            holder.tvTitle.setText(c.getComment_name());
//        }
//        else{
//            holder.tvTitle.setText(TextUtils.isEmpty(item.getMember_name())?item.getNick_name():item.getMember_name());
//        }
        if(selected.get(item.getMember_id())!=null && selected.get(item.getMember_id())) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
        return convertView;
    }

    public static class ViewHolder {
        ImageView ivImage;
        TextView tvTitle,row_tv_beizhu,row_tv_souyuename;
        public CheckBox checkBox;
    }
}
