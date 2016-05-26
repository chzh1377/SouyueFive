package com.zhongsou.souyue.im.render;

/**
 * Created by zhangwenbin on 15/3/24.
 */

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.tuita.sdk.im.db.helper.MessageFileDaoHelper;
import com.tuita.sdk.im.db.helper.MessageHistoryDaoHelper;
import com.tuita.sdk.im.db.module.MessageFile;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.transfile.IMFileDetailActivity;
import com.zhongsou.souyue.module.ChatMsgEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MsgFileRender extends MsgItemRender {

    /**
     * 文件示意图
     */
    private ImageView ivFileImage;

    /**
     * 文件名字
     */
    private TextView tvFileName;

    /**
     * card 群人数
     */
    private TextView tvFileSize;

    /**
     * 是否已下载状态 及进度显示
     */
    private TextView tvFileState;

    /**
     * 更新文件进度的回调
     */
    private ProgressListener listener;

    private DecimalFormat dFormat;

    /**
     * 缓存解析过得group用于群名片用
     */
    public MsgFileRender(Context context, BaseTypeAdapter<ChatMsgEntity> adapter, int itemType) {
        super(context, adapter, itemType);
        dFormat = new DecimalFormat("#.##");
        tvFileState = mViewHolder.obtainView(mContentView, R.id.file_state);
    }

    @Override
    public void fitEvents() {
        super.fitEvents();
//        Toast.makeText(mContext,"event",Toast.LENGTH_LONG).show();
        mViewHolder.obtainView(mContentView, R.id.ll_msg_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mChatAdapter.getIsEdit()) {
                    Intent intent = new Intent(mContext, IMFileDetailActivity.class);
                    intent.putExtra("mChatEntity", mChatMsgEntity);
                    IMFileDetailActivity.setListener(new ProgressListener() {
                        @Override
                        public void setProgress(String progress,long msgId) {
                            if(mChatMsgEntity.getId()==msgId){
                                tvFileState.setVisibility(View.VISIBLE);
                                tvFileState.setText(progress);
                            }
                        }
                        @Override
                        public void changeFileName(String fileName) {
                            try {
                                JSONObject obj = new JSONObject(mChatMsgEntity.getText());
                                obj.put("name",fileName);
                                MessageHistoryDaoHelper.getInstance(mContext).updateMsgText(mChatMsgEntity.UUId,obj.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    mContext.startActivity(intent);
                }else{
                    if(cbCheck.isChecked()){
                        cbCheck.setChecked(false);
                        mChatMsgEntity.setEdit(false);
                        cbCheck.setBackgroundResource(R.drawable.im_chat_checkbox);
                    }else{
                        mChatMsgEntity.setEdit(true);
                        cbCheck.setChecked(true);
                        cbCheck.setBackgroundResource(R.drawable.im_chat_checkbox_selected);
                    }
                }
            }
        });

        mViewHolder.obtainView(mContentView, R.id.ll_msg_file).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (!mChatAdapter.getIsEdit()) {
                    showLCDialog(false, false);
                }
                return true;
            }
        });

    }

    public static interface ProgressListener {
        public void setProgress(String progress,long msgId);
        public void changeFileName(String fileName);
    }

    @Override
    public void fitDatas(int position) {
        super.fitDatas(position);
        JSONObject obj = null;
        try {
            obj = new JSONObject(mChatMsgEntity.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //将控件放入缓存
        ivFileImage = mViewHolder.obtainView(mContentView, R.id.im_file_image);
        tvFileName = mViewHolder.obtainView(mContentView, R.id.file_name);
        tvFileSize = mViewHolder.obtainView(mContentView, R.id.file_size);


        try {
            String fileName = obj.getString("name");
            tvFileName.setText(fileName);


            long fileSize = obj.getLong("size");

            tvFileSize.setText(MsgUtils.getFileSize(fileSize, dFormat));

            //文件状态设置 1.已下载  2.已过期  3，进度百分比  六个状态  需要通过数字判断具体显示文字


            MessageFile msgFile = MessageFileDaoHelper.getInstance(mContext).select(mChatMsgEntity.getFileMsgId());
            if (!mChatMsgEntity.isComMsg()) {
                tvFileState.setText("");
            } else {
                if (msgFile != null && msgFile.getState() != null && msgFile.getState() == MessageFile.DOWNLOAD_STATE_COMPLETE) {
                    tvFileState.setText(getStates(mContext, msgFile.getState()));
                    tvFileState.setVisibility(View.VISIBLE);
                } else if (System.currentTimeMillis() > Long.parseLong(obj.getString("expiry"))) {
                    tvFileState.setText(mContext.getString(R.string.im_filedownload_state_overtime));
                    tvFileState.setVisibility(View.VISIBLE);
                } else {
                    if (mChatMsgEntity.getFileMsgId() != -1) {//假如fileMsgId为空的话，会被默认赋值成-1
                        if (msgFile != null && msgFile.getState() != null) {
                            tvFileState.setText(getStates(mContext, msgFile.getState()));
                            tvFileState.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvFileState.setText("");
                    }
                }
            }

            MsgUtils.setImagePic(ivFileImage, obj.getString("name"));


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据文件状态int值，来获取文件状态的String串
     * public static final int DOWNLOAD_STATE_INIT = 1;            // 初始化等待中
     * public static final int DOWNLOAD_STATE_LOADING = 2;         // 下载中
     * public static final int DOWNLOAD_STATE_PAUSE = 3;           // 暂停中
     * public static final int DOWNLOAD_STATE_FAILED = 4;          // 下载失败
     * public static final int DOWNLOAD_STATE_COMPLETE = 5;        // 下载完成
     *
     * @param state
     * @return
     */
    public static String getStates(Context context, Integer state) {
        switch (state) {
            case MessageFile.DOWNLOAD_STATE_INIT:
                return "";
            case MessageFile.DOWNLOAD_STATE_LOADING:
                return "";
            case MessageFile.DOWNLOAD_STATE_PAUSE:
                return context.getString(R.string.im_filedownload_state_pause);
            case MessageFile.DOWNLOAD_STATE_FAILED:
                return context.getString(R.string.im_filedownload_state_fails);
            case MessageFile.DOWNLOAD_STATE_COMPLETE:
                return context.getString(R.string.im_filedownload_state_success);
            default:
                return "";
        }
    }

    @Override
    protected int getLeftLayoutId() {
        return R.layout.msg_file_left_view;
    }

    @Override
    protected int getRightLayoutId() {
        return R.layout.msg_file_right_view;
    }

}