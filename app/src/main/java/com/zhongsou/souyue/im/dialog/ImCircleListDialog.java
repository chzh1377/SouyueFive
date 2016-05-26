package com.zhongsou.souyue.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tuita.sdk.im.db.module.ImToCricle;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.MyImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: Im 跳转到列表的选择
 * @auther: qubian
 * @data: 2016/4/19.
 */
public class ImCircleListDialog extends Dialog implements ListView.OnItemClickListener {
    private static final int MAX_HEIGHT = 320;// 最大高度 350 dip
    private Context mContext;
    private List<ImToCricle> imToCricleList = new ArrayList<>();
    private ImCircleListDialogListner imCircleListDialogListner;
    private ListView im_circle_list;
    private CircliListAdapter circliListAdapter;

    public ImCircleListDialog(Context context, List list, ImCircleListDialogListner listner) {
        super(context, R.style.dialog_alert);
        mContext = context;
        imToCricleList = list;
        imCircleListDialogListner = listner;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            setContentView(R.layout.im_circle_dialog);
        } catch (Exception e) {
            try {
                dismiss();
            } catch (Exception ex) {

            }
        }
        im_circle_list = (ListView) findViewById(R.id.im_circle_list);
        circliListAdapter = new CircliListAdapter();
        im_circle_list.setAdapter(circliListAdapter);
        im_circle_list.setOnItemClickListener(this);
        if(imToCricleList.size()>5)
        {
            setMaxHeight();
        }
    }
    private void setMaxHeight()
    {
        WindowManager.LayoutParams params =getWindow().getAttributes();
        params.height = DeviceUtil.dip2px(mContext,MAX_HEIGHT);
        getWindow().setAttributes(params);
    }

    class CircliListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return imToCricleList.size();
        }

        @Override
        public Object getItem(int position) {
            return imToCricleList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CircleViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new CircleViewHolder();
            } else {
                viewHolder = (CircleViewHolder) convertView.getTag();
            }
            viewHolder.bindDate(imToCricleList.get(position));
            return viewHolder.bgView;
        }
    }

    class CircleViewHolder {
        private View bgView;
        private ImageView iconIv;
        private TextView titleTv;

        public CircleViewHolder() {
            bgView = LayoutInflater.from(mContext).inflate(R.layout.im_list_cirlce_item, null);
            iconIv = (ImageView) bgView.findViewById(R.id.im_circle_img);
            titleTv = (TextView) bgView.findViewById(R.id.im_circle_name);
            bgView.setTag(this);
        }

        public void bindDate(ImToCricle circle) {
            PhotoUtils.getImageLoader().displayImage(circle.getInterestLogo(), iconIv, MyImageLoader.subrecommendOptions);
            titleTv.setText(circle.getInterestName());
        }
    }

    public interface ImCircleListDialogListner {
        void itemClick(ImToCricle imToCricle);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
        if (imCircleListDialogListner != null) {

            imCircleListDialogListner.itemClick((ImToCricle) circliListAdapter.getItem(position));
        }
    }
}
