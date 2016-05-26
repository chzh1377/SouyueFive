package com.zhongsou.souyue.im.render;

/**
 * Created by zhangwenbin on 15/3/30.
 */

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.ac.IMChatActivity;
import com.zhongsou.souyue.module.ChatMsgEntity;

public class MsgAskCoinRender extends MsgItemRender {

    /**
     * 显示的文本
     */
    private TextView tvCoinText,tvText,tvOk,tvCancel;

    private LinearLayout llAskCoin;


    public MsgAskCoinRender(Context context,
                            BaseTypeAdapter<ChatMsgEntity> adapter, int itemType) {
        super(context, adapter, itemType);


    }

    @Override
    public void fitEvents() {
        super.fitEvents();
        // TODO custom event
        tvOk =  mViewHolder.obtainView(mContentView, R.id.im_ask_iocn_ok);
        tvCancel = mViewHolder.obtainView(mContentView, R.id.im_ask_iocn_cancel);
        llAskCoin = mViewHolder.obtainView(mContentView,R.id.ll_msg_askcoin);
        tvText = mViewHolder.obtainView(mContentView,R.id.tv_text);
        if (tvOk != null) {
            //同意按钮
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mChatAdapter.getIsEdit()) {
                            ((IMChatActivity) mContext).sendCoinNew();
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
        }

        if (tvCancel != null) {
            //拒绝按钮
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mChatAdapter.getIsEdit()) {
                        mMsgMananger.rejestSendCoin();
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
        }

        if (llAskCoin != null) {
            llAskCoin.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!mChatAdapter.getIsEdit()) {
                        showLCDialog(false, true);
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
                    return true;
                }
            });
        }

        if (tvText != null){
            tvText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!mChatAdapter.getIsEdit()) {
                        showLCDialog(false, true);
                    }
                    return true;
                }
            });

            tvText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mChatAdapter.getIsEdit()) {
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
        }

    }

    @Override
    public void fitDatas(int position) {
        super.fitDatas(position);
        // TODO custom fit data

        //将控件放入缓存
        tvCoinText =  mViewHolder.obtainView(mContentView,R.id.im_ask_for_cion_text);
        tvText = mViewHolder.obtainView(mContentView,R.id.tv_text);
        String s = String.format(
                mContext.getResources().getString(R.string.ask_for_coin),
                mChatMsgEntity.getCoinString());
        if (mChatMsgEntity.isComMsg()) {
            tvCoinText.setLineSpacing(1f, 1.05f);
            tvCoinText.setText(s);
        }else {
            tvText.setText(s);
        }
    }

    @Override
    protected int getLeftLayoutId() {
        return R.layout.msg_askcoin_left_view;
    }

    @Override
    protected int getRightLayoutId() {
        return R.layout.msg_askcoin_right_view;
    }

}