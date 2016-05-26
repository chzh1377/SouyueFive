package com.zhongsou.souyue.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.tuita.sdk.im.db.module.MessageFile;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.render.MsgUtils;
import com.zhongsou.souyue.utils.StringUtils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhou on 2015/11/12.
 */
public class FileListAdapter extends BaseAdapter {
    private List<MessageFile> mMessageFileList;
    private LayoutInflater mInflater;
    private boolean mIsEdit;        //判断是否是编辑状态
    private DecimalFormat mDFormat;

    private Map<Long, Boolean> mSelectedMap = new HashMap<Long, Boolean>(); //记录勾选文件

    public void setmMessageFileList(List<MessageFile> mMessageFileList) {
        this.mMessageFileList = mMessageFileList;
    }

    public boolean ismIsEdit() {
        return mIsEdit;
    }

    public void setmIsEdit(boolean mIsEdit) {
        this.mIsEdit = mIsEdit;
    }

    public Map<Long, Boolean> getmSelectedMap() {
        return mSelectedMap;
    }

    public void setmSelectedMap(Map<Long, Boolean> mSelectedMap) {
        this.mSelectedMap = mSelectedMap;
    }

    public FileListAdapter(Context context, List<MessageFile> messageFileList) {
        mInflater = LayoutInflater.from(context);
        this.mMessageFileList = messageFileList;
        mDFormat = new DecimalFormat("#.##");
    }

    @Override
    public int getCount() {
        return mMessageFileList.size();
    }

    @Override
    public MessageFile getItem(int position) {
        return mMessageFileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mMessageFileList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageFile msgFile = mMessageFileList.get(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.im_filelist_item, null);
            viewHolder.ivFileHead = (ImageView) convertView.findViewById(R.id.iv_file_head);
            viewHolder.tvfileName = (TextView) convertView.findViewById(R.id.tv_file_name);
            viewHolder.tvfileSize = (TextView) convertView.findViewById(R.id.tv_file_size);
            viewHolder.cbEdit = (CheckBox) convertView.findViewById(R.id.cb_edit);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MsgUtils.setImagePic(viewHolder.ivFileHead, msgFile.getName());
        viewHolder.tvfileName.setText(msgFile.getName());
        viewHolder.tvfileSize.setText(MsgUtils.getFileSize(msgFile.getSize(),mDFormat));
        viewHolder.tvTime.setText(StringUtils.convertDate(String.valueOf(msgFile.getUpdateTime())));
        if (ismIsEdit()) {
            viewHolder.cbEdit.setVisibility(View.VISIBLE);
        } else {
            viewHolder.cbEdit.setVisibility(View.INVISIBLE);
        }

        if (mSelectedMap.get(msgFile.getId()) != null && mSelectedMap.get(msgFile.getId())){
            viewHolder.cbEdit.setChecked(true);
        }else {
            viewHolder.cbEdit.setChecked(false);
        }
        viewHolder.cbEdit.setBackgroundResource(R.drawable.radiobutton);
        return convertView;
    }

   public static class ViewHolder {
        private ImageView ivFileHead;
        private TextView tvfileName;
        private TextView tvfileSize;
        public CheckBox cbEdit;
        private TextView tvTime;
    }
}
