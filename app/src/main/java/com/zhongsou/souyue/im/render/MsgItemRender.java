package com.zhongsou.souyue.im.render;

/**
 * Created by zhangwenbin on 15/3/24.
 */

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.tuita.sdk.im.db.module.UserBean;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.model.PersonPageParam;
import com.zhongsou.souyue.im.ac.IMChatActivity;
import com.zhongsou.souyue.im.dialog.ImLongClickDialog;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.IMApi;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;

public abstract class MsgItemRender implements ItemTypeRender {

    /**
     * 注意右边的item 类型必需为奇数，左边的item 类型必需为偶数
     */
    public final static int MSG_TEXT_LEFT = 0;
    public final static int MSG_TEXT_RIGHT = 1;
    public final static int MSG_IMAGE_LEFT = 2;
    public final static int MSG_IMAGE_RIGHT = 3;
    public final static int MSG_AUDIO_LEFT = 4;
    public final static int MSG_AUDIO_RIGHT = 5;
    public final static int MSG_GIF_LEFT = 6;
    public final static int MSG_GIF_RIGHT = 7;
    public final static int MSG_CARD_LEFT = 8;
    public final static int MSG_CARD_RIGHT = 9;
    public final static int MSG_SHARE_LEFT = 10;
    public final static int MSG_SHARE_RIGHT = 11;
    public final static int MSG_WHISPER_LEFT = 12;
    public final static int MSG_WHISPER_RIGHT = 13;
    public final static int MSG_TIGER_LEFT = 14;
    public final static int MSG_TIGER_RIGHT = 15;
    public final static int MSG_NOT_FRIEND = 16;
    public final static int MSG_FRIEND = 17;//无用用于占位
    public final static int MSG_ASKCOIN_LEFT = 18;
    public final static int MSG_ASKCOIN_RIGHT = 19;//无用用于占位
    public final static int MSG_GIFTCOIN_LEFT = 20;
    public final static int MSG_GIFTCOIN_RIGHT = 21;
    public final static int MSG_SERMSGFIRST_LEFT = 22;
    public final static int MSG_SERMSGFIRST_RIGHT = 23;//无用用于占位
    public final static int MSG_SERMSGSECOND_LEFT = 24;
    public final static int MSG_SERMSGSECOND_RIGHT = 25;//无用用于占位
    public final static int MSG_FILE_LEFT = 26;//发送文件左边Render
    public final static int MSG_FILE_RIGHT = 27;//发送文件左边Render
    public final static int MSG_RED_PACKET_LEFT = 28;//红包左边Render
    public final static int MSG_RED_PACKET_RIGHT = 29;//红包右边Render
    public final static int MSG_NEW_SYSTEM_MSG_LEFT = 30;//新系统消息左边Render
    public final static int MSG_NEW_SYSTEM_MSG_RIGHT = 31;//无用用于占位

    public final static int MSG_DEFAULT_LEFT = 32;
    public final static int MSG_DEFAULT_RIGHT = 33;

    public DisplayImageOptions option;

    protected Context mContext;
    protected BaseTypeAdapter<ChatMsgEntity> mChatAdapter;
    protected View mContentView;
    protected ViewHolder mViewHolder;
    protected int mPosition;
    protected ChatMsgEntity mChatMsgEntity;
    protected MessageManager mMsgMananger;

    private int mItemType;

    /**
     * 布局中的id设置为R.id.msg_adapter_item_time
     */
    private TextView tvTime;
    /**
     * 布局中的id设置为R.id.msg_adapter_item_checkbox
     */
    protected CheckBox cbCheck;
    /**
     * 布局中的id设置为R.id.msg_adapter_item_head
     */
    private ImageView ivHead;
    /**
     * 布局中的id设置为R.id.msg_adapter_item_user_name
     */
    private TextView tvUserName;

    /**
     * 布局中的id设置为R.id.msg_adapter_item_sending
     */
    private ProgressBar pbMsgSending;

    /**
     * 布局中的id设置为R.id.msg_adapter_item_failed
     */
    private TextView tvMsgFailed;

    /**
     * 布局中的id设置为R.id.msg_adapter_item_unread
     */
    private ImageView ivMsgUnreadline;

    public MsgItemRender(Context context,
                         BaseTypeAdapter<ChatMsgEntity> adapter, int itemType) {
        mContext = context;
        mChatAdapter = adapter;
        mItemType = itemType;

        mContentView = LayoutInflater.from(context)
                .inflate(getLayoutId(), null);
        mViewHolder = new ViewHolder();
        pbMsgSending = mViewHolder.obtainView(mContentView,
                R.id.msg_adapter_item_sending);
        tvMsgFailed = mViewHolder.obtainView(mContentView,
                R.id.msg_adapter_item_failed);
        cbCheck = mViewHolder.obtainView(mContentView,
                R.id.msg_adapter_item_checkbox);
        option = new DisplayImageOptions.Builder().cacheInMemory(true) .displayer(new RoundedBitmapDisplayer(10)).build();
    }

    /**
     * 返回消息item对应的布局文件
     *
     * @return
     */
    protected int getLayoutId() {
        return (mItemType & 1) == 1 ? getRightLayoutId() : getLeftLayoutId();
    }

    /**
     * 返回当前item位置
     *
     * @return
     */
    protected int getPosition() {
        return (Integer) mContentView.getTag(R.id.msg_adapter_item_position);
    }

    /**
     * 返回消息发发送时的左边布局
     *
     * @return
     */
    protected abstract int getLeftLayoutId();

    /**
     * 返回消息发发送时的右边布局
     *
     * @return
     */
    protected abstract int getRightLayoutId();

    @Override
    public void setMesssageManager(MessageManager messageManager) {
        mMsgMananger = messageManager;
    }

    /**
     * 返回文本类型对应的视图
     */
    @Override
    public View getConvertView() {
        return mContentView;
    }

    /**
     * 为视图添加事件处理
     */
    @Override
    public void fitEvents() {
        // 头像点击事件
        mViewHolder.obtainView(mContentView, R.id.msg_adapter_item_head)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mChatAdapter.getIsEdit()) {
                            if (mChatMsgEntity.getChatType() != IConst.CHAT_TYPE_SERVICE_MESSAGE) {
                                Contact c = new Contact();
                                if (mChatMsgEntity.isComMsg()) {
                                    c.setAvatar(mChatMsgEntity.getIconUrl());
                                    c.setChat_id(mChatMsgEntity.getSendId());
                                    // 跳转个人中心 4.1
//                                    IMApi.IMGotoShowPersonPage((Activity) mContext, c,
//                                            PersonPageParam.FROM_SINGLE_CHAT);
                                } else {
                                    c.setChat_id(Long.parseLong(SYUserManager
                                            .getInstance().getUserId()));
                                }
                                //需要判断是从群聊里进入个人中心，还是从私聊里进入个人中心
                                if(mChatMsgEntity.getChatType()==IConst.CHAT_TYPE_PRIVATE){//从私聊进入
                                    // 跳转个人中心 4.1
                                    IMApi.IMGotoShowPersonPage((Activity) mContext, c,
                                            PersonPageParam.FROM_SINGLE_CHAT);
                                }else{
                                    IMApi.IMGotoShowPersonPage((Activity) mContext, c,
                                            PersonPageParam.FROM_IM);
                                }
                            }
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

        mViewHolder.obtainView(mContentView,R.id.msg_adapter_item_head).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mChatAdapter.getIsEdit() && mChatMsgEntity.getChatType() == IConst.CHAT_TYPE_GROUP) {
                    if (mChatMsgEntity.isComMsg()) {
                        String newname = "";
                        Contact contact = ImserviceHelp.getInstance().db_getContactById(mChatMsgEntity.getSendId());
                        ((IMChatActivity) mContext).setEditMsg("@" + mChatMsgEntity.getNickname() + " ");
                        if(contact != null) {
                            newname = contact.getNick_name();
                        }else{
                            newname = mChatMsgEntity.getNickname();
                        }
                        UserBean userBean = new UserBean();
                        userBean.setNick(newname);
                        userBean.setUid(mChatMsgEntity.getSendId());
                        ((IMChatActivity) mContext).setAtFriend(userBean);
                    }
                }
                return true;
            }
        });
        
        mViewHolder.obtainView(mContentView,R.id.msg_adapter_item_head).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.v("log", "head imageview touch:"+event.getAction());
				if(event.getAction() == MotionEvent.ACTION_CANCEL
						||event.getAction()==MotionEvent.ACTION_UP){
					 ((IMChatActivity) mContext).cancelHeadLongClick();
				}
				return false;
			}
		});

        cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    mChatMsgEntity.setEdit(true);
                    cbCheck.setBackgroundResource(R.drawable.im_chat_checkbox_selected);
                } else {
                    mChatMsgEntity.setEdit(false);
                    cbCheck.setBackgroundResource(R.drawable.im_chat_checkbox);
                }
            }
        });
        tvMsgFailed = mViewHolder.obtainView(mContentView, R.id.msg_adapter_item_failed);
        if (tvMsgFailed != null) {
            sendFailed();
        }


    }

    /**
     * 处理共公头像
     */
    @Override
    public void fitDatas(int position) {
        mPosition = position;
        ivHead = mViewHolder.obtainView(mContentView,
                R.id.msg_adapter_item_head);
        tvUserName = mViewHolder.obtainView(mContentView,
                R.id.msg_adapter_item_user_name);
        tvTime = mViewHolder.obtainView(mContentView,
                R.id.msg_adapter_item_time);
        ivMsgUnreadline = mViewHolder.obtainView(mContentView, R.id.msg_adapter_item_unread);

        mChatMsgEntity = mChatAdapter.getItem(position);
        ImageLoader.getInstance().displayImage(mChatMsgEntity.getIconUrl(),ivHead,option);
        // left tvUserName显示
        if (mChatMsgEntity.isComMsg()) {
            if (mChatMsgEntity.getChatType() == IConst.CHAT_TYPE_GROUP) {
                if (tvUserName != null) {
                    tvUserName.setVisibility(View.VISIBLE);
                    tvUserName.setText(mChatMsgEntity.getNickname());
                }
            } else {
                if (tvUserName != null)
                    tvUserName.setVisibility(View.GONE);
            }
        }

        // right 发送状态显示
        if (!mChatMsgEntity.isComMsg()) {
            if (mChatMsgEntity.isSending()) {
                showSendingView();
            } else if (mChatMsgEntity.isSendFailed()) {
                showFailedView();
            } else {
                showSuccessedView();
            }
        }

        // msg 时间显示
        if (position == 0) {
            tvTime.setVisibility(View.VISIBLE);
            tvTime.setText(StringUtils.toYMHS(mChatMsgEntity.getDate()));
        }else if(position > 0){
            if (MsgUtils.isShowTime(mChatAdapter.getItem(position - 1),
                    mChatMsgEntity)) {
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText(StringUtils.toYMHS(mChatMsgEntity.getDate()));
            } else {
                tvTime.setVisibility(View.GONE);
            }
        }

        if (mChatMsgEntity.getType() == IMessageConst.CONTENT_TYPE_SYSMSG || mChatMsgEntity.getType() == IMessageConst.CONTENT_TYPE_SYS_NOTFRIEND || mChatMsgEntity.getType() == IMessageConst.CONTENT_TYPE_NEW_SYSTEM_MSG) {
            cbCheck.setVisibility(View.GONE);
        } else {
            if (mChatAdapter.getIsEdit()) {
                cbCheck.setVisibility(View.VISIBLE);
//				if (cbCheck.isChecked()) {
                if (mChatMsgEntity.isEdit()) {   //更改一个checkBox判断条件
                    cbCheck.setBackgroundResource(R.drawable.im_chat_checkbox_selected);
                } else {
                    cbCheck.setBackgroundResource(R.drawable.im_chat_checkbox);
                }
            } else {
                cbCheck.setVisibility(View.GONE);
            }
        }

        if (pbMsgSending != null && mChatMsgEntity.isSending()) {
            pbMsgSending.setVisibility(View.VISIBLE);
        } else if (tvMsgFailed != null && mChatMsgEntity.isSendFailed()) {
            tvMsgFailed.setVisibility(View.VISIBLE);
        }

        //左边未读消息线显示
        if (ivMsgUnreadline != null && mChatMsgEntity.isComMsg() && mChatMsgEntity.isShowUnreadLine()){
            ivMsgUnreadline.setVisibility(View.VISIBLE);
        }else {
            if (ivMsgUnreadline != null)
                ivMsgUnreadline.setVisibility(View.GONE);
        }

        // if (mChatMsgEntity.isEdit()) {
        // cbCheck.setBackgroundResource(R.drawable.im_chat_checkbox_selected);
        // } else {
        // cbCheck.setBackgroundResource(R.drawable.im_chat_checkbox);
        // }
        // cbCheck.setVisibility(View.VISIBLE);

        // h.im_chat_totallayout.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // setEditBackground(isEdit(), h.im_chat_checkbox, mChatMsgEntity);
        // }
        // });

        // if (mChatMsgEntity.isNotYourFriend()) {
        // setSysTypeData(h, mChatMsgEntity);
        // return;
        // }
        // if (mChatMsgEntity.isVersiontip()) {
        // setVersionTipType(h, mChatMsgEntity);
        // return;
        // }

        if(mChatAdapter.getIsEdit()){//如果编辑状态，则不显示发送中和发送失败
            showSuccessedView();
        }

    }

    private void showSendingView() {
        tvMsgFailed.setVisibility(View.GONE);
        pbMsgSending.setVisibility(View.VISIBLE);
    }

    private void showFailedView() {
        tvMsgFailed.setVisibility(View.VISIBLE);
        pbMsgSending.setVisibility(View.GONE);
    }

    private void showSuccessedView() {
        if(tvMsgFailed!=null){
            tvMsgFailed.setVisibility(View.GONE);
        }
        if(pbMsgSending!=null){
            pbMsgSending.setVisibility(View.GONE);
        }
    }

    class ViewHolder {
        private SparseArray<View> views = new SparseArray<View>();

        /**
         * 指定resId和类型即可获取到相应的view
         *
         * @param convertView
         * @param resId
         * @param <T>
         * @return
         */
        <T extends View> T obtainView(View convertView, int resId) {
            View v = views.get(resId);
            if (null == v) {
                v = convertView.findViewById(resId);
                views.put(resId, v);
            }
            return (T) v;
        }

    }

    // 抽取设置选择框的View背景
//	protected boolean setEditBackground(boolean isEdit, View v, ChatMsgEntity e) {
//		if (isEdit) {
//			if (e.isEdit()) {
//				v.setBackgroundResource(R.drawable.im_chat_checkbox_selected);
//			} else {
//				v.setBackgroundResource(R.drawable.im_chat_checkbox);
//			}
//			e.setEdit(!e.isEdit());
//			return true;
//		}
//		return false;
//	}

    private void sendFailed() {
        mViewHolder.obtainView(mContentView, R.id.msg_adapter_item_failed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mChatAdapter.getIsEdit()) {
                    switch (mChatMsgEntity.getType()) {
                        case IMessageConst.CONTENT_TYPE_TEXT:
                            mMsgMananger.sendText(mChatMsgEntity);
                            break;
                        case IMessageConst.CONTENT_TYPE_VOICE:

                            break;
                        case IMessageConst.CONTENT_TYPE_IMAGE:

                            break;
                        case IMessageConst.CONTENT_TYPE_NEW_VOICE:
                            if (mChatMsgEntity.getUrl().startsWith("http:")) {
                                mMsgMananger.sendVoice(mChatMsgEntity);
                            } else {
                                mMsgMananger.saveVoice(mChatMsgEntity, mChatMsgEntity.getVoiceLength());
                            }
                            break;
                        case IMessageConst.CONTENT_TYPE_NEW_IMAGE:
                            if (mChatMsgEntity.getUrl().startsWith("http:")) {
                                mMsgMananger.sendImage(mChatMsgEntity);
                            } else {
                                mMsgMananger.saveImage(mChatMsgEntity, mChatMsgEntity.getUrl(), mChatMsgEntity.isVertical(), mChatMsgEntity.getMinWidth(), mChatMsgEntity.getMinHeight());
                            }
                            break;
                        case IMessageConst.CONTENT_TYPE_VCARD:
                            mMsgMananger.sendCard(mChatMsgEntity);
                            break;
                        case IMessageConst.CONTENT_TYPE_TIGER:
                            mMsgMananger.sendAskForCoin(mChatMsgEntity);
                            break;
                        case IMessageConst.CONTENT_TYPE_SHARE_TIGER:
                            break;

                        case IMessageConst.CONTENT_TYPE_SYS_NOTFRIEND:

                            break;
                        case IMessageConst.CONTENT_TYPE_INTEREST_SHARE:
                            mMsgMananger.sendShareInterest(mChatMsgEntity);
                            break;
                        case IMessageConst.CONTENT_TYPE_INTEREST_ADD_FRIEND:
                            mMsgMananger.sendAskInterest(mChatMsgEntity);
                            break;
                        case IMessageConst.CONTENT_TYPE_SOUYUE_NEWS_SHARE:
                            mMsgMananger.sendShareNews(mChatMsgEntity);
                            break;
                        case IMessageConst.CONTENT_TYPE_SECRET_MSG:
                            mMsgMananger.sendWhisper(mChatMsgEntity);
                            break;
                        case IMessageConst.CONTENT_TYPE_SENDCOIN:
                            mMsgMananger.sendCoinForNew(mChatMsgEntity);
                            break;
                        case IMessageConst.CONTENT_TYPE_INTEREST_ADD_FRIEND_PRIVATE:
                            mMsgMananger.sendAskPrivateInterest(mChatMsgEntity);
                            break;
                        case IMessageConst.CONTENT_TYPE_INTEREST_CIRCLE_CARD:
                            mMsgMananger.sendInterestCard(mChatMsgEntity);
                            break;
                        case IMessageConst.CONTENT_TYPE_SRP_SHARE:
                            mMsgMananger.sendShareSRP(mChatMsgEntity);
                            break;
                        case IMessageConst.CONTENT_TYPE_GIF:
                            mMsgMananger.sendGif(mChatMsgEntity);
                            break;
                        case IMessageConst.CONTENT_TYPE_WEB:
                            mMsgMananger.sendWebMessage(mChatMsgEntity);
                            break;
                        case IMessageConst.CONTENT_TYPE_GROUP_CARD:
                            mMsgMananger.sendCard(mChatMsgEntity);
                            break;
                        default:
                            break;
                    }

                }
            }
        });
    }

    /**
     * 长按对话框监听
     */
    ImLongClickDialog.UpdateListInterface mUpdateListInterface = new ImLongClickDialog.UpdateListInterface() {

        @Override
        public void updateChatList(ChatMsgEntity forwordcontent) {
            //更新列表
            ImserviceHelp.getInstance().db_deleteSelectedAllTypeMessageHistory(mMsgMananger.getFriendId(), forwordcontent.getRetry(), forwordcontent.getChatType());
            removeItem(mPosition);
        }
    };

    /**
     * 删除一个条目
     *
     * @param position
     */
    protected void removeItem(int position) {
        if (0 <= position && position < mChatAdapter.getCount()) {
            mChatAdapter.remove(mChatAdapter.getItem(position));
        }
    }


    protected void deleteItem() {
        ImserviceHelp.getInstance().db_deleteSelectedAllTypeMessageHistory(mMsgMananger.getFriendId(), mChatMsgEntity.getRetry(), mChatMsgEntity.getChatType());
        removeItem(mPosition);
    }

    /**
     * 所有 Item 长点击弹出的 dialog
     *
     * @param showCopyFlag
     * @param OnlyDeleteFlag
     */
    protected void showLCDialog(boolean showCopyFlag, boolean OnlyDeleteFlag) {
        if (!mChatAdapter.getIsEdit()) {
            ImLongClickDialog.Builder longclickbuilder = new ImLongClickDialog.Builder(
                    mContext);
            longclickbuilder.setText(showCopyFlag);
            longclickbuilder.setOnlyDelete(OnlyDeleteFlag);

            longclickbuilder.setForwordList(mChatMsgEntity, mUpdateListInterface);
            longclickbuilder.create().show();
        }
    }

}