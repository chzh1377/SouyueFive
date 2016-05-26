package com.zhongsou.souyue.im.render;

/**
 * Created by zhangwenbin on 15/3/24.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.ac.IMChatActivity;
import com.zhongsou.souyue.im.ac.IMLookWhisperActivity;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.module.ChatMsgEntity;

public class MsgWhisperRender extends MsgItemRender {

    /**
     * 密信几秒后删除布局
     */
    private LinearLayout llDelete;

    /**
     * 密信存在剩余时间
     */
    private TextView tvWhisperTime;



	public MsgWhisperRender(Context context,
                            BaseTypeAdapter<ChatMsgEntity> adapter, int itemType) {
		super(context, adapter, itemType);
	}

	@Override
	public void fitEvents() {
		super.fitEvents();
		// TODO custom event

        //密信的点击事件
        mViewHolder.obtainView(mContentView,R.id.ll_msg_whisper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!setEditBackground(isEdit(), viewHolder.im_chat_checkbox, entity)) {
                if (!mChatAdapter.getIsEdit()) {
                    if (mChatMsgEntity.isComMsg()) {
                            //接收方
                            long timeLength = 0;
                            if (mChatMsgEntity.getIsReceiveDetailOpen() == ChatMsgEntity.BACK) {
                                timeLength = mChatMsgEntity.getTimerLength();
                            } else {
                                if (mChatMsgEntity.getText().length() < 10) {
                                    timeLength = ChatMsgEntity.TOTALLENGTH;
                                } else if (mChatMsgEntity.getText().length() > ChatMsgEntity.RECEIVEMAXLENGTH) {
                                    timeLength = ChatMsgEntity.RECEIVEMAXLENGTH;
                                } else {
                                    timeLength = mChatMsgEntity.getText().length();
                                }
                                ((IMChatActivity) mContext).updateWhisper(mChatMsgEntity, timeLength);
                            }
                            mChatMsgEntity.setIsReceiveDetailOpen(ChatMsgEntity.OPEN);
                        }
    //                    isDetailOpen = ChatMsgEntity.OPEN;
    //                    if (mChatMsgEntity.getTimerLength() > 0) {
                            Intent intent = new Intent(mContext, IMLookWhisperActivity.class);
                            intent.putExtra("chatMsgEntity", mChatMsgEntity);
                            ((Activity) mContext).startActivityForResult(intent, IMChatActivity.WHISPERTYPEDELETE);
    //                    }
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
//            }
        });
	}

	@Override
	public void fitDatas(int position) {
		super.fitDatas(position);
		// TODO custom fit data

        //初始化控件
        llDelete = mViewHolder.obtainView(mContentView,R.id.whisper_deletelayout);
        tvWhisperTime = mViewHolder.obtainView(mContentView,R.id.im_whisper_time_tv);

        //密信的显示
        if (mChatMsgEntity.getTimerLength() > 0) {
            llDelete.setVisibility(View.VISIBLE);
            if (mChatMsgEntity.isComMsg()) {
                if (mChatMsgEntity.getIsReceiveDetailOpen() != ChatMsgEntity.INIT) {
                    tvWhisperTime.setText(mChatMsgEntity.getTimerLength() + "");
//                    isDetailOpen = ChatMsgEntity.INIT;
                } else {
                    tvWhisperTime.setVisibility(View.INVISIBLE);
                }
            } else {
                tvWhisperTime.setText(mChatMsgEntity.getTimerLength() + "");
//                isDetailOpen = ChatMsgEntity.INIT;
            }

        } else {
//            if (mChatMsgEntity.isWhisperDelete() &&
//                    (isDetailOpen == ChatMsgEntity.BACK
//                            || isDetailOpen == ChatMsgEntity.INIT)) {
            if (mChatMsgEntity.isWhisperDelete() ) {
                removeListItem(mContentView, mChatMsgEntity);
            } else {
//                viewHolder.im_chat_whisper.setVisibility(View.VISIBLE);
//	                                             发送失败
                llDelete.setVisibility(View.INVISIBLE);
            }

        }
	}

	@Override
	protected int getLeftLayoutId() {
		return R.layout.msg_whisper_left_view;
	}

	@Override
	protected int getRightLayoutId() {
		return  R.layout.msg_whisper_right_view;
	}


    //抽取删除list中item的方法，，，，目前仅仅私聊中的密信使用   没用到的写了代码，写了代码注释了
    private void removeListItem(final View rowView, final ChatMsgEntity entity) {
        Animation animation;
        if (entity.isComMsg()) {
            animation = (Animation) AnimationUtils.loadAnimation(mContext, R.anim.im_list_item_remove_anim_left);
        } else {
            animation = (Animation) AnimationUtils.loadAnimation(mContext, R.anim.im_list_item_remove_anim_right);
        }

        rowView.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                rowView.setClickable(false);
//                if (coll != null && coll.size() > 0) {
//                    coll.remove(entity);
//                }
                removeItem(mPosition);
//                notifyDataSetChanged();
//                isDetailOpen = ChatMsgEntity.INIT;
                ImserviceHelp.getInstance().db_deleteSelectedMessageHistory(entity.chatId, entity.getRetry());
            }
        });
    }


}