package com.zhongsou.souyue.adapter;

/**
 * @author YanBin
 * @version V1.0
 * @project trunk
 * @Description Class Description
 * @date 2016/4/20
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.zhongsou.souyue.R;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private ArrayFilter mFilter;
    private ArrayList<String> mOriginalValues;//所有的Item
    private List<String> mObjects;//过滤后的item
    private final Object mLock = new Object();
    private int maxMatch = 10;//最多显示多少个选项,负数表示全部

    public AutoCompleteAdapter(Context context, ArrayList<String> mOriginalValues, int maxMatch) {
        this.context = context;
        this.mOriginalValues = mOriginalValues;
        this.maxMatch = maxMatch;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
                synchronized (mLock) {
                    Log.i("tag", "mOriginalValues.size=" + mOriginalValues.size());
                    ArrayList<String> list = new ArrayList<String>(mOriginalValues);
                    results.values = list;
                    results.count = list.size();
                    return results;
                }
            } else {
                String prefixString = prefix.toString().toLowerCase();

                final int count = mOriginalValues.size();

                final ArrayList<String> newValues = new ArrayList<String>(count);

                for (int i = 0; i < count; i++) {
                    final String value = mOriginalValues.get(i);
                    final String valueText = value.toLowerCase();

                    if (valueText.startsWith(prefixString)) {  //源码 ,匹配开头
                        newValues.add(value);
                    }
                    if (maxMatch > 0) {//有数量限制
                        if (newValues.size() > maxMatch - 1) {//不要太多
                            break;
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mObjects = (List<String>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_for_autocomplete, null);
            holder.text = (TextView) convertView.findViewById(R.id.search_item_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(mObjects.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView text;
    }

    public ArrayList<String> getAllItems() {
        return mOriginalValues;
    }
}
