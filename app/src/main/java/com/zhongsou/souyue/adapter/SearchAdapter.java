package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.HotSearchInfo;
import com.zhongsou.souyue.module.PopActionItem;
import com.zhongsou.souyue.module.SuberedItemInfo;

import java.util.List;

/**
 * @author YanBin
 * @version V5.2.0
 * @project trunk
 * @Description 简单的Adapter 用于搜索
 * @date 2016/4/23
 */
public class SearchAdapter extends BaseAdapter {

    private Context context;
    private List<?> list;
    private int itemLayout;

    public SearchAdapter(Context context, List<?> list, int itemLayout) {
        this.context = context;
        this.list = list;
        this.itemLayout = itemLayout;
    }

    @Override
    public int getCount() {
        int result = 0;
        if (list != null) {
            result = list.size();
        }
        return result;
    }

    @Override
    public Object getItem(int position) {
        if (list != null) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(itemLayout, null);
            setTextContent(convertView, position);
        } else {
            setTextContent(convertView, position);
        }
        return convertView;
    }

    private void setTextContent(View convertView, int position) {
        Object item = list.get(position);
        TextView txtContent = (TextView) convertView.findViewById(R.id.search_item_text);
        if (item instanceof HotSearchInfo) {    //热门推荐
            TextView txtNum = (TextView) convertView.findViewById(R.id.search_grid_num);

            int realPosition = changePosition(position);

            switch(realPosition){
                case 0:
                    txtNum.setBackgroundResource(R.drawable.item_grid_1);
                    break;
                case 1:
                    txtNum.setBackgroundResource(R.drawable.item_grid_2);
                    break;
                case 2:
                    txtNum.setBackgroundResource(R.drawable.item_grid_3);
                    break;
                default:
                    txtNum.setBackgroundResource(R.drawable.item_grid_4);
                    break;
            }

            item = list.get(realPosition);
            txtNum.setText(String.valueOf(realPosition + 1));
            txtContent.setText(((HotSearchInfo) item).getTitle());
        } else if (item instanceof SuberedItemInfo) {   //替补词
            txtContent.setText(((SuberedItemInfo) item).getTitle());

            TextView desc = (TextView) convertView.findViewById(R.id.search_item_desc);
            desc.setVisibility(View.VISIBLE);
            desc.setText(((SuberedItemInfo) item).getDesc());
        } else if (item instanceof String) {    //历史纪录
            txtContent.setText(item.toString());
        }
    }

    /**
     * 序号变换
     * @param position
     * @return
     */
    public int changePosition(int position){
        int realPosition = 0;
        switch(position){
            case 0:
                realPosition = 0;
                break;
            case 1:
                realPosition = 3;
                break;
            case 2:
                realPosition = 1;
                break;
            case 3:
                realPosition = 4;
                break;
            case 4:
                realPosition = 2;
                break;
            case 5:
                realPosition = 5;
                break;
        }
        return realPosition;
    }
}