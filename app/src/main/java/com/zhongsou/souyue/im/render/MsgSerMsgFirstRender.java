package com.zhongsou.souyue.im.render;

/**
 * Created by zhangwenbin on 15/3/24.
 */

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.ImJump2SouyueUtil;
import com.zhongsou.souyue.utils.Utils;

import java.util.List;

public class MsgSerMsgFirstRender extends MsgItemRender {

    private LinearLayout rlItem;

    private LinearLayout llImage;
    private TextView tvTitle;
    private TextView tvContent;
    private RelativeLayout rlHead;
    private LinearLayout llContent;
    private ImageView ivHead;
    private TextView tvComeFrom;
    private LinearLayout llBottom;
    private View viewLine;

    private int mScreenWidth;

    public final static String STATISTICS_JUMP_POSITION_MSG_LIST = "msglist";                   //统计类型 服务号列表
    public final static String STATISTICS_JUMP_POSITION_NOTIFICATION_BAR = "notificationbar";   //统计类型 推送

    public final static String CLICK_FROM_ITEM = UmengStatisticEvent.IM_SERVICE_ITEM_CLICK;   //到达detailAc类型
    public final static String CLICK_FROM_NOTICE = UmengStatisticEvent.NOTICE_CLICK;   //通知栏到达

    /**
     * 下面3张图片
     */
    private List<String> mImageList;

    public MsgSerMsgFirstRender(Context context,
                                BaseTypeAdapter<ChatMsgEntity> adapter, int itemType) {
        super(context, adapter, itemType);
        mScreenWidth = Utils.getScreenWidth(context);

    }

    @Override
    public void fitEvents() {
        super.fitEvents();
        // TODO custom event

        mViewHolder.obtainView(mContentView, R.id.rl_sermsgfirst).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!mChatAdapter.getIsEdit()) {
                    if (mChatMsgEntity.getServiceMessage() != null) {
                        if (mChatMsgEntity.getServiceMessage().getIntent_data() != null && !"".equals(mChatMsgEntity.getServiceMessage().getIntent_data())) {
                            ImJump2SouyueUtil.getInstance().jump(mContext,
                                    mChatMsgEntity.getServiceMessage().getIntent_data(), STATISTICS_JUMP_POSITION_MSG_LIST, mChatMsgEntity.getServiceMessage().getBy2(), CLICK_FROM_ITEM, mChatMsgEntity.getMid());
                        }
                        UpEventAgent.onZSIMServiceItemClick(MainApplication.getInstance(),
                                mChatMsgEntity.getMid(),
                                mChatMsgEntity.chatId,
                                mChatMsgEntity.getServiceMessage().getTitle(),
                                mChatMsgEntity.getType()
                        );  //ZSSDK
                    }
                } else {
                    if (cbCheck.isChecked()) {
                        cbCheck.setChecked(false);
                        mChatMsgEntity.setEdit(false);
                        cbCheck.setBackgroundResource(R.drawable.im_chat_checkbox);
                    } else {
                        mChatMsgEntity.setEdit(true);
                        cbCheck.setChecked(true);
                        cbCheck.setBackgroundResource(R.drawable.im_chat_checkbox_selected);
                    }
                }
            }
        });
    }

    @Override
    public void fitDatas(int position) {
        super.fitDatas(position);
        // TODO custom fit data

        //将控件放入缓存

        if (mChatMsgEntity != null && mChatMsgEntity.getServiceMessage() != null) {
            rlItem = mViewHolder.obtainView(mContentView, R.id.rl_sermsgfirst);
            llImage = mViewHolder.obtainView(mContentView, R.id.ll_image);
            tvTitle = mViewHolder.obtainView(mContentView, R.id.tv_title);
            llContent = mViewHolder.obtainView(mContentView, R.id.ll_content);
            tvContent = mViewHolder.obtainView(mContentView, R.id.tv_content);
            rlHead = mViewHolder.obtainView(mContentView, R.id.rl_head);
            ivHead = mViewHolder.obtainView(mContentView, R.id.iv_head);
            mImageList = mChatMsgEntity.getServiceMessage().getImageUrls();
            viewLine = mViewHolder.obtainView(mContentView, R.id.view_line);
            llBottom = mViewHolder.obtainView(mContentView, R.id.ll_bottom_view);
            tvComeFrom = mViewHolder.obtainView(mContentView, R.id.tv_comefrom);

            llImage.removeAllViews();
            int width = (mScreenWidth - DeviceUtil.dip2px(mContext, 50)) / 3;
            int height = (int) ((2 * width) / 3);
            if (mImageList != null) {
                int num = mImageList.size() > 3 ? 3 : mImageList.size();
                for (int i = 0; i < num; i++) {//最多显示三个
                    ImageView image = new ImageView(mContext);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
                    lp.setMargins(5, 10, 5, 20);
                    image.setLayoutParams(lp);
                    image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    ImageLoader.getInstance().displayImage(mImageList.get(i), image, new DisplayImageOptions.Builder().cacheOnDisk(true)
                            .cacheInMemory(true).showImageOnLoading(R.drawable.default_image).build());
                    llImage.addView(image);
                    System.out.println("---------->" + mImageList.get(i) + "   position=  " + mChatMsgEntity.chatId);
                }
            }

            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            rlItem.setLayoutParams(lp1);

            if (mChatMsgEntity.getServiceMessage().getTitle() != null
                    && !"".equals(mChatMsgEntity.getServiceMessage().getTitle().trim())) {
                tvTitle.setText(mChatMsgEntity.getServiceMessage().getTitle());
            } else {
                tvTitle.setText(mChatMsgEntity.getServiceMessage().getDigst());
            }

            if (mChatMsgEntity.getServiceMessage().getAvatar() != null
                    && !"".equals(mChatMsgEntity.getServiceMessage().getAvatar())) {
                rlHead.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(mChatMsgEntity.getServiceMessage().getAvatar(), ivHead, MsgUtils.getHeadOptions());
            } else {
                rlHead.setVisibility(View.GONE);
            }
            tvContent.setText(mChatMsgEntity.getServiceMessage().getDigst());

            if(mChatMsgEntity.getServiceMessage().getSubDigst()!=null
                    && !"".equals(mChatMsgEntity.getServiceMessage().getSubDigst())) {
                tvComeFrom.setVisibility(View.VISIBLE);
                tvComeFrom.setText(mChatMsgEntity.getServiceMessage().getSubDigst());
            } else {
                tvComeFrom.setVisibility(View.GONE);
            }


            if (mChatMsgEntity.getServiceMessage().getIntent_data() != null ) {
                if("".equals(mChatMsgEntity.getServiceMessage().getIntent_data())){
                    viewLine.setVisibility(View.GONE);
                    llBottom.setVisibility(View.GONE);
                }
            } else {
                viewLine.setVisibility(View.VISIBLE);
                llBottom.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected int getLeftLayoutId() {
        return R.layout.msg_sermsgfirst_left_view;
    }

    @Override
    protected int getRightLayoutId() {
        return 0;
    }


}