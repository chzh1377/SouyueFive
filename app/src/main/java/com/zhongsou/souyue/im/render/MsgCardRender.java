package com.zhongsou.souyue.im.render;

/**
 * Created by zhangwenbin on 15/3/24.
 */

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.circle.model.PersonPageParam;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.im.util.IMApi;
import com.zhongsou.souyue.im.util.IMIntentUtil;
import com.zhongsou.souyue.module.ChatMsgEntity;
import org.json.JSONException;
import org.json.JSONObject;

public class MsgCardRender extends MsgItemRender {

    /**
     * card头像
     */
    private ImageView ivHead;

    /**
     * card名字
     */
    private TextView tvName;

    /**
     * card 群人数
     */
    private TextView tvCount;


    /**
     * 缓存解析过得group用于群名片用
     */
    private SparseArray<Group> mGroups = new SparseArray<Group>();

	public MsgCardRender(Context context,
                         BaseTypeAdapter<ChatMsgEntity> adapter, int itemType) {
		super(context, adapter, itemType);

	}

	@Override
	public void fitEvents() {
		super.fitEvents();
		// TODO custom event

        mViewHolder.obtainView(mContentView,R.id.ll_msg_card).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!mChatAdapter.getIsEdit()) {
                //跳转个人中心  4.1
                if (mChatMsgEntity.getType() == IMessageConst.CONTENT_TYPE_VCARD){
                    IMApi.IMGotoShowPersonPage((Activity) mContext, mChatMsgEntity.getCard(), PersonPageParam.FROM_IM);
                    if (mChatMsgEntity.getChatType() == IConst.CHAT_TYPE_SERVICE_MESSAGE
                            && mChatMsgEntity.chatId == IConst.SOUXIAOYUE_ID){        //如果是搜小悦 才统计
                        UpEventAgent.onZSIMServiceItemClick(MainApplication.getInstance(),
                                mChatMsgEntity.getMid(),
                                mChatMsgEntity.chatId,
                                mChatMsgEntity.getCardNickName().toString(),
                                mChatMsgEntity.getType()
                        );  //ZSSDK
                    }
                }else if (mChatMsgEntity.getType() == IMessageConst.CONTENT_TYPE_GROUP_CARD)
                    IMIntentUtil.gotoGroupInfoActivity(mContext, obtainGroup(mChatMsgEntity).getGroup_id(), mChatMsgEntity.getSendId());
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
        mViewHolder.obtainView(mContentView,R.id.ll_msg_card).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (!mChatAdapter.getIsEdit()) {
                    showLCDialog(false, false);
                }
                return true;
            }
        });
	}

	@Override
	public void fitDatas(int position) {
		super.fitDatas(position);
		// TODO custom fit data

        //将控件放入缓存
        ivHead = mViewHolder.obtainView(mContentView,R.id.im_card_head);
        tvName = mViewHolder.obtainView(mContentView,R.id.im_card_name);
        tvCount = mViewHolder.obtainView(mContentView,R.id.groupmember_number);

        // 按照不同消息类型显示名片
        switch (mChatMsgEntity.getType()){
            case IMessageConst.CONTENT_TYPE_VCARD:      //好友名片
                tvName.setText(mChatMsgEntity.getCardNickName());
                tvCount.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage(mChatMsgEntity.getCardUrl(), ivHead, MsgUtils.getHeadOptions());
                break;
            case IMessageConst.CONTENT_TYPE_GROUP_CARD: //群名片
                tvName.setText(obtainGroup(mChatMsgEntity).getGroup_nick_name());
                tvCount.setVisibility(View.VISIBLE);
                tvCount.setText(Integer.valueOf(obtainGroup(mChatMsgEntity).getMemberCount()) + "名成员");
                ImageLoader.getInstance().displayImage(obtainGroup(mChatMsgEntity).getGroup_avatar(), ivHead, MsgUtils.getHeadOptions());
                break;
        }

    }

	@Override
	protected int getLeftLayoutId() {
		return R.layout.msg_card_left_view;
	}

	@Override
	protected int getRightLayoutId() {
		return  R.layout.msg_card_right_view;
	}

    /**
     * 获取解析过的 group，减少解析次数
     * @param chatMsgEntity
     * @return
     */
   private Group obtainGroup(ChatMsgEntity chatMsgEntity) {
       Group group =null;
       if ((int)chatMsgEntity.getId() != 0) {
            mGroups.get((int) chatMsgEntity.getId());
           if (null == group) {
               group = parseJson2Group(chatMsgEntity);
               mGroups.put((int) chatMsgEntity.getId(), group);
           }
       }else{
           group = parseJson2Group(chatMsgEntity);
       }
       return group;
    }

    /**
     * 解析传递的json
     * @param entity
     * @return
     */
    private Group parseJson2Group(ChatMsgEntity entity){
        JSONObject json = null;
        final Group group = new Group();
        try {
            json = new JSONObject(entity.getText());
            if (json.getString("groupmember") != null) {
                group.setMemberCount(Integer.valueOf(json.getString("groupmember")));
            }
            group.setGroup_nick_name(json.getString("group_name"));
            if (json.has("group_avatar"))
                group.setGroup_avatar(json.getString("group_avatar"));
            group.setGroup_id(Long.valueOf(json.getString("groupid")));
            entity.setName(json.getString("group_name"));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return group;
    }

}