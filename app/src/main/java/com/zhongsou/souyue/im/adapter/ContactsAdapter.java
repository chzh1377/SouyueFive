package com.zhongsou.souyue.im.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tuita.sdk.im.db.helper.PingYinUtil;
import com.tuita.sdk.im.db.module.Contact;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.ContactModelUtil;
import com.zhongsou.souyue.im.util.ImUtils;
import com.zhongsou.souyue.im.view.AlphaSideBar.AlphaIndexer;
import com.zhongsou.souyue.im.view.SwipeListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContactsAdapter extends BaseAdapter implements AlphaIndexer {
    private static final int VIEW_TYPE_ZM = 0;//字母
    private static final int VIEW_TYPE_VALUE = 1;//联系人
    private static final int VIEW_TYPE_NOVALUE = 2;//无结果
    private boolean isShowNoValue;//true 显示无结果
    private boolean isDelete;
    private String keyWord;
    private Context mContext;
    private DisplayImageOptions options;
    public String getKeyWord(){
        return keyWord;
    }
    public void setKeyWord(String keyWord){
        this.keyWord = keyWord;
    }
    public void setShowNoValue(boolean isShowNoValue){
        this.isShowNoValue = isShowNoValue&&!isDelete;
    }
    private Map<String, Integer> alphaIndex;
    private List<Contact> data;
    private int mRightWidth = 0;

    private ListView listView;
    public ContactsAdapter(ListView listView, Context context, Map<String, Integer> alphaIndex, int rightWidth) {
        this.mContext = context;
        this.listView = listView;
        this.alphaIndex = alphaIndex;
        mRightWidth = rightWidth;
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .displayer(new RoundedBitmapDisplayer(10))
                .showImageOnLoading(R.drawable.default_head).build();
    }

    public interface OnDeleteListener {
        void onDeleteItem(int position);
    }

    private OnDeleteListener deleteListener = new OnDeleteListener() {

        @Override
        public void onDeleteItem(int position) {
            Contact contactDel;
            if (data.get(position - 1).getId() == null ){
                if ((position + 1) == data.size() || data.get(position + 1).getId() == null) {
                    ContactsAdapter.this.data.remove(position - 1);
                    contactDel = ContactsAdapter.this.data.remove(position - 1);
                }else {
                    contactDel = ContactsAdapter.this.data.remove(position);
                }
            }else {
                contactDel = ContactsAdapter.this.data.remove(position);
            }
            ImserviceHelp.getInstance().im_userOp(4, contactDel.getChat_id(), contactDel.getNick_name(), contactDel.getAvatar(), null);
            isDelete=true;
            if (listView instanceof SwipeListView) {
                ((SwipeListView) listView).onChanged();
                ListAdapter tempAdapter = listView.getAdapter();
                if (tempAdapter instanceof HeaderViewListAdapter) {
                    tempAdapter = ((HeaderViewListAdapter) tempAdapter).getWrappedAdapter();
                }
                if (tempAdapter instanceof BaseAdapter) {
                    ((BaseAdapter) tempAdapter).notifyDataSetChanged();
                }
            }
        }
    };
    
    public void addAll(List<Contact> items){
        if (data == null)
            data = new ArrayList<Contact>();
        data.clear();
        data.addAll(items);
        isDelete=false;
    }
    
    public void setData(List<Contact> items) {
        if(items != null){
            this.data = items;
        }
        notifyDataSetChanged();
    }


    // 清除所有引用
    public void clear() {
        listView = null;
        if (data != null)
            data.clear();
        data = null;
        if (alphaIndex != null)
            alphaIndex.clear();
        alphaIndex = null;
    }
    		
    
    public void clearData(){
        if (data != null)
            data.clear();
        isDelete=false;
    }

    @Override
    public int getCount() {
        if(data == null){
            return 0;
        } else if(data.size() == 0 && isShowNoValue){
            return 1;
        } else {
            return data.size();
        }
    }

    @Override
    public Contact getItem(int position) {
        if (data.size() > position)
            return data.get(position);
          else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        // if (TextUtils.isEmpty(getItem(position).getMyid())) {
        if (data != null && data.size() == 0){
            return false;
        } else if (TextUtils.isEmpty(getItem(position).getNick_name())) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (data.size() == 0){
            return VIEW_TYPE_NOVALUE;
        } else if (TextUtils.isEmpty(getItem(position).getNick_name())) {// 字母
            return VIEW_TYPE_ZM;
        } else {// 其他
            return VIEW_TYPE_VALUE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Contact item = getItem(position);
        final int type = getItemViewType(position);
        if (type == VIEW_TYPE_VALUE) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflateView(R.layout.im_swipe_contacts_list_view_row, parent);
                holder = new ViewHolder();
                //填充view
                setView(convertView,holder);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //填充item数据
            setData(holder,item,position);

        } else if (type == VIEW_TYPE_ZM){
            if (convertView == null) {
                convertView = inflateView(R.layout.im_swipe_contacts_list_view_index, parent);
            }
            if(convertView instanceof TextView)
                ((TextView) convertView).setText(item.getComment_name());
        } else if (type == VIEW_TYPE_NOVALUE){
            if (convertView == null) {
                convertView = inflateView(R.layout.im_novalue_local, parent);
            }
        }

        return convertView;
    }

    public void setView(View convertView,ViewHolder holder){
        holder.item_left = (RelativeLayout) convertView.findViewById(R.id.item_left);
        holder.item_right = (RelativeLayout) convertView.findViewById(R.id.item_right);
        holder.ivImage = (ImageView) convertView.findViewById(R.id.row_iv_image);
        holder.tvTitle = (TextView) convertView.findViewById(R.id.row_tv_title);
        holder.row_tv_beizhu = (TextView) convertView.findViewById(R.id.row_tv_beizhu);
        holder.bAction_delete = (Button) convertView.findViewById(R.id.row_btn_delete);
    }

    public void setData(ViewHolder holder , Contact item ,final int position){
        LinearLayout.LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        holder.item_left.setLayoutParams(lp1);
        LinearLayout.LayoutParams lp2 = new LayoutParams(mRightWidth, LayoutParams.MATCH_PARENT);
        holder.item_right.setLayoutParams(lp2);

        if (TextUtils.isEmpty(item.getAvatar())) {
            holder.ivImage.setImageResource(R.drawable.default_head);
        } else {
//            aQuery.id(holder.ivImage).image(item.getAvatar(), true, true, 0, R.drawable.default_head);
            ImageLoader.getInstance().displayImage(
                    item.getAvatar(),
                    holder.ivImage,options);
        }

        /**
         * 正常情况{如果有备注名，则显示备注名，否则显示昵称}
         */
        String username = ContactModelUtil.getShowName(item);
        item.setLocal_order(PingYinUtil.converter2FirstSpell(username));

        String local_order_Nick =  PingYinUtil.converter2FirstSpell(item.getNick_name()).toUpperCase();
        String local_order_comment = PingYinUtil.converter2FirstSpell(item.getComment_name()).toUpperCase();
        if (getKeyWord() == null)
            setKeyWord("");

        /**
         * by1是否修改过备注名（1修改，0未修改），by2类型
         */
        if(getKeyWord().equals("")){
            holder.tvTitle.setText(username);
            holder.row_tv_beizhu.setVisibility(View.GONE);
        } else {
            if (item.getNick_name().toUpperCase().contains(getKeyWord()) || local_order_Nick.contains(getKeyWord())){
                holder.tvTitle.setText(ImUtils.getHighlightText(username, item.getLocal_order(),getKeyWord()));
                if (item.getNick_name().equals(username)){
                    holder.row_tv_beizhu.setVisibility(View.GONE);
                }else {
                    holder.row_tv_beizhu.setVisibility(View.VISIBLE);
                    holder.row_tv_beizhu.setText(ImUtils.getHighlightText("昵称：", item.getNick_name(), local_order_Nick, getKeyWord()));
                }
            }else if (item.getComment_name().toUpperCase().contains(getKeyWord()) || local_order_comment.contains(getKeyWord())){
                holder.tvTitle.setText(ImUtils.getHighlightText(username, item.getLocal_order(), getKeyWord()));
                if (item.getNick_name().toUpperCase().contains(getKeyWord()) || local_order_Nick.contains(getKeyWord())){
                    holder.row_tv_beizhu.setVisibility(View.VISIBLE);
                    holder.row_tv_beizhu.setText(ImUtils.getHighlightText("昵称：",item.getNick_name(), local_order_Nick, getKeyWord()));
                }else {
                    holder.row_tv_beizhu.setVisibility(View.GONE);
                }
            }else {
                holder.tvTitle.setText(username);
            }
        }
        holder.bAction_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteListener.onDeleteItem(position);
            }
        });
    }

    
    private View inflateView(int id, ViewGroup parent) {
        LayoutInflater viewInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return viewInflater.inflate(id, parent, false);
    }

    static class ViewHolder {
        RelativeLayout item_left;
        RelativeLayout item_right;

        ImageView ivImage;
        TextView tvTitle,row_tv_beizhu;
        Button bAction_delete;
    }

    @Override
    public int getPositionForAlpha(char c) {
        String key = new String(new char[] {c});
        if (alphaIndex.containsKey(key)) {
            return alphaIndex.get(key) + listView.getHeaderViewsCount();
        }
        return -1;
    }

}
