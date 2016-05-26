package com.zhongsou.souyue.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tuita.sdk.im.db.module.Contact;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.view.AlphaSideBar.AlphaIndexer;
import com.zhongsou.souyue.module.MobiContactEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PhoneContactAdapter extends BaseAdapter implements AlphaIndexer{
    private Context mContext;
    private Map<String, Integer> alphaIndex;
    /**
     * 定义字母表的排序规则
     */
    private List<Contact> infolist = new ArrayList<Contact>();

    public void setData(MobiContactEntity mce) {
        this.infolist = mce.getContasts();
        this.alphaIndex = mce.getAlphaIndex();
        notifyDataSetChanged();
    }
    
    
    public PhoneContactAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return infolist.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Contact getItem(int position) {
        return infolist.get(position);
    }

    public List<Contact> getInfolist() {
        return infolist;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       
        if (convertView == null) {
            convertView = newView(mContext, parent);
        }
        setViewData(position, (ViewHolder) convertView.getTag());
        setSections((ViewHolder) convertView.getTag(), position);
        return convertView;
    }

    private void setViewData(int position, ViewHolder holder) {
        Contact c = getItem(position);
        if (c != null) {
            holder.name.setText(c.getNick_name());
            holder.phoneno.setText(c.getPhone());
            if (c.getStatus() == Contact.STATUS_IS_SOUYUE_USER_NOT_FRIEND) {
                holder.operation.setText(mContext.getResources().getString(R.string.add_friends_status));
                holder.operation.setEnabled(false);
            } else if (c.getStatus() == Contact.STATUS_IS_SOUYUE_USER_AND_FRIEND) {
                holder.operation.setText(mContext.getResources().getString(R.string.already_friends_status));
                holder.operation.setEnabled(false);
            } else {
                holder.operation.setText(mContext.getResources().getString(R.string.request_friends_status));
                holder.operation.setEnabled(true);
            }
        }else{
            holder.name.setText("");
            holder.phoneno.setText("");
            holder.operation.setText("");
            holder.operation.setEnabled(false);
        }
    }

    private class ViewHolder {
        private TextView name, phoneno, operation, header;
    }


    private void setSections(ViewHolder holder, int position) {
        String catalog = null;
        catalog =infolist.get(position).getCatalog();
        if (position == 0) {
            holder.header.setVisibility(View.VISIBLE);
            holder.header.setText(catalog);
        } else {
            String lastCatalog = infolist.get(position - 1).getCatalog();
            if (lastCatalog != null && catalog.equals(lastCatalog)) {
                holder.header.setVisibility(View.GONE);
            } else {
                holder.header.setVisibility(View.VISIBLE);
                holder.header.setText(catalog);
            }
        }

    }

    public View newView(final Context paramContext, ViewGroup parent) {
        View v = LayoutInflater.from(paramContext).inflate(R.layout.im_phonecontact_item, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.name = (TextView) v.findViewById(R.id.phonename);
        holder.phoneno = (TextView) v.findViewById(R.id.phoneno);
        holder.operation = (TextView) v.findViewById(R.id.operation_text);
        holder.header = (TextView) v.findViewById(R.id.header);
        v.setTag(holder);
        return v;
    }

    @Override
    public int getPositionForAlpha(char c) {
        String key = new String(new char[]{c});
        if(alphaIndex!=null&&alphaIndex.containsKey(key)){
            int index=alphaIndex.get(key);
            return index;
        }
        return -1;
    }

    @Override
    public boolean isEnabled(int position) {
        Contact c = infolist.get(position);
        if (c != null) {
            if (c.getStatus() == Contact.STATUS_IS_SOUYUE_USER_NOT_FRIEND) {
                return true;
            } else if (c.getStatus() == Contact.STATUS_IS_SOUYUE_USER_AND_FRIEND) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

}
