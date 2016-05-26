package com.zhongsou.souyue.im.render;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.dialog.ImDialog;
import com.zhongsou.souyue.im.module.MsgContent;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * @author zhangwenbin zhangwb@zhongsou.com
 * @version version_code (e.g, V5.1)
 * @Copyright (c) 2016 zhongsou
 * @Description class description
 * 新的系统消息render  老的是MsgNoFriendRender
 * 从5.1开始 以后显示在界面的系统消息一律采用此render
 * @date 16/1/11
 */
public class MsgSysMsgRender extends MsgItemRender {

    private Context mContext;
    private TextView tv_sysmsg;
    private ImageView ivIcon;
    private DisplayImageOptions mIconOption;

    /**
     * 缓存解析的MsgContent
     */
    private SparseArray<MsgContent> mMsgContent = new SparseArray<MsgContent>();


    public MsgSysMsgRender(Context context, BaseTypeAdapter<ChatMsgEntity> adapter, int itemType) {
        super(context, adapter, itemType);
        this.mContext = context;
        tv_sysmsg = mViewHolder.obtainView(mContentView, R.id.tv_sysmsg);
        ivIcon = mViewHolder.obtainView(mContentView, R.id.iv_icon);
        mIconOption = new DisplayImageOptions.Builder().cacheInMemory(true).build();
    }

    @Override
    public void fitDatas(int position) {
        super.fitDatas(position);
        MsgContent msgContent = obtainMsgContent(mChatMsgEntity);
        if (msgContent != null) {
            sysMsgShow(msgContent);
        }
    }

    /**
     * 对android TextView 诡异换行的处理  其一
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }


    @Override
    public void fitEvents() {
        super.fitEvents();

        mViewHolder.obtainView(mContentView, R.id.tv_sysmsg).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!mChatAdapter.getIsEdit()) {
                    MsgUtils.msgJump(mContext, obtainMsgContent(mChatMsgEntity));
                }
            }
        });

    }

    @Override
    protected int getLeftLayoutId() {
        return R.layout.msg_sysmsg_left_view;
    }

    @Override
    protected int getRightLayoutId() {
        return R.layout.msg_sysmsg_right_view;
    }

    /**
     * 获取消息对象
     *
     * @param msgEntity
     * @return
     */
    private MsgContent obtainMsgContent(ChatMsgEntity msgEntity) {
        MsgContent msgContent = mMsgContent.get((int) msgEntity.getId());

        if (null == msgContent) {
            msgContent = parseJson(msgEntity);
            mMsgContent.put((int) msgEntity.getId(), msgContent);
        }

        return msgContent;
    }

    /**
     * 解析json得到MsgContent
     *
     * @param msgEntity
     * @return
     */
    private MsgContent parseJson(ChatMsgEntity msgEntity) {
        try {
            MsgContent msgContent = new Gson().fromJson(msgEntity.getText().toString(), MsgContent.class);
            return msgContent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 系统消息显示逻辑
     *
     * @param msgContent
     */
    private void sysMsgShow(MsgContent msgContent) {
        //判断icon是否显示
        if (msgContent.getIconUrl() != null && !"".equals(msgContent.getIconUrl())) {
            ivIcon.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(msgContent.getIconUrl(), ivIcon, mIconOption);
        } else {
            ivIcon.setVisibility(View.GONE);
        }

        //判断文本显示样式
        if (msgContent.getJumpType() != null) {
            if ("".equals(msgContent.getJumpType())) {      //传的空代表默认
                tv_sysmsg.setText(ToDBC(msgContent.getText()));
            } else if ("redPacketUrl".equals(msgContent.getJumpType())) {        //红包类型系统消息
                tv_sysmsg.setText(Html.fromHtml(ToDBC(msgContent.getText()) + "&nbsp;&nbsp;&nbsp;<font color='#da4644'>查看</font>"));
            } else {         //不识别 按默认处理
                tv_sysmsg.setText(ToDBC(msgContent.getText()) + "&nbsp;&nbsp;&nbsp;<font color='#2B91EC'>查看</font>");
            }
        }

    }
}
