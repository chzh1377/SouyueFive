package com.zhongsou.souyue.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.utils.IntentUtil;

import java.util.List;

public class SuberRecomendAdapter extends BaseAdapter {

    private Context context;
    private List<SuberedItemInfo> infos;
    private LayoutInflater mLayoutInflater;
    private ClickBtnAdd mClickBtnAdd;

    public interface ClickBtnAdd {
        void clickBtnAdd(int position);
    }

    public SuberRecomendAdapter(Context context,
                                List<SuberedItemInfo> infos) {
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.infos = infos;
    }

    public void setmClickBtnAdd(ClickBtnAdd mClickBtnAdd) {
        this.mClickBtnAdd = mClickBtnAdd;
    }

    @Override
    public int getCount() {
        return infos != null ? infos.size() : 0;
    }

    @Override
    public SuberedItemInfo getItem(int position) {
        return infos.get(position);
    }

    public void updateStatus(SuberedItemInfo item) {
        if (infos.contains(item)) {
            int index = infos.indexOf(item);
            SuberedItemInfo inf = infos.get(index);
            if ("0".equals(inf.getStatus())) {
                infos.get(index).setStatus("1");
            } else {
                infos.get(index).setStatus("0");
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(
                    R.layout.suberlist_recomend_item, null);
            viewHolder.ivProtrait = (ImageView) convertView
                    .findViewById(R.id.iv_protrait);
            viewHolder.tvTheme = (TextView) convertView
                    .findViewById(R.id.tv_theme);
            viewHolder.tvOther = (TextView) convertView.findViewById(R.id.tv_other);
            viewHolder.ivAdd = (ImageView) convertView
                    .findViewById(R.id.iv_add);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SuberedItemInfo info = infos.get(position);

        // 设置data
        viewHolder.tvTheme.setText(info.getTitle());

        PhotoUtils.showCard(UriType.HTTP, info.getImage(),
                viewHolder.ivProtrait, MyDisplayImageOption.options);

        viewHolder.tvOther.setText(info.getDesc());

        if ("1".equals(info.getType())) {
            viewHolder.ivAdd.setImageResource(R.drawable.secrete_icon);
        } else if (!"1".equals(info.getType())) {
            if ("0".equals(info.getStatus())) {   //已订阅
                viewHolder.ivAdd
                        .setImageResource(R.drawable.subscribe_cancel01);
            } else {// 未订阅
                viewHolder.ivAdd.setImageResource(R.drawable.subscribe_add01);
            }
        }
        initButtonAddListener(viewHolder, position);   //初始化Listener
        return convertView;
    }

    private void initButtonAddListener(ViewHolder holder, int position) {
        if (holder == null) return;
        ButtonAddClickListener listener = new ButtonAddClickListener(position);
        holder.ivAdd.setOnClickListener(listener);
    }

    class ButtonAddClickListener implements View.OnClickListener {
        private int position;

        public ButtonAddClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.iv_add:   //点击订阅按钮 TODO
                    if(mClickBtnAdd != null){
                        mClickBtnAdd.clickBtnAdd(position);
                    }
                    break;
            }
        }
    }

    public void clickBtnOther(int position) {
//        Toast.makeText(context, "-----", Toast.LENGTH_SHORT).show();
        String typeCircle = "interest";
        String typeSrp = "srp";
        String typeGroup = "group";
        SuberedItemInfo current = infos.get(position);
        if (typeCircle.equals(current.getCategory())) {
            UIHelper.showCircleIndex((Activity) context, current.getSrpId(), current.getKeyword(), current.getKeyword(), null, "HotRecommend");
        } else if (typeSrp.equals(current.getCategory())) {
            IntentUtil.gotoSouYueSRPAndFinish(context, current.getKeyword(), current.getSrpId(), null);
        } else if (typeGroup.equals(current.getCategory())) {
            IntentUtil.gotoSubGroupHome(context, String.valueOf(current.getId()), current.getKeyword(), null);
        }
    }

    static class ViewHolder {
        ImageView ivProtrait;
        TextView tvTheme;
        TextView tvOther;
        ImageView ivAdd;

    }
}
