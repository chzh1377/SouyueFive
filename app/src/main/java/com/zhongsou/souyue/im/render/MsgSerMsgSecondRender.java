package com.zhongsou.souyue.im.render;

/**
 * Created by zhangwenbin on 15/3/24.
 */

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.ImJump2SouyueUtil;

import java.util.List;

public class MsgSerMsgSecondRender extends MsgItemRender {

    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvComeFrom;
    private View viewLine;
    private ImageView ivTotalImage;
    //    private LinearLayout llChild;
    private TextView tvBottom;
    protected int height, width;
    private int deviceWidth;
    private DisplayImageOptions options;

    /**
     * 下面3张图片
     */
    private List<String> mImageList;

    public MsgSerMsgSecondRender(Context context,
                                 BaseTypeAdapter<ChatMsgEntity> adapter, int itemType) {
        super(context, adapter, itemType);

        deviceWidth = CircleUtils.getDeviceWidth(context);
        width = deviceWidth - DeviceUtil.dip2px(context, 40);
        height = (int) (width) / 2;
        options = new DisplayImageOptions.Builder().
                cacheOnDisk(true).
                cacheInMemory(true).
                resetViewBeforeLoading(true).
                build();

    }
    @Override

    public void fitEvents() {
        super.fitEvents();
        // TODO custom event

        mViewHolder.obtainView(mContentView, R.id.second_ll).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!mChatAdapter.getIsEdit()) {
                    if (mChatMsgEntity.getServiceMessage().getIntent_data() != null && !"".equals(mChatMsgEntity.getServiceMessage().getIntent_data())) {
                        ImJump2SouyueUtil.getInstance().jump(mContext,
                                mChatMsgEntity.getServiceMessage().getIntent_data(), MsgSerMsgFirstRender.STATISTICS_JUMP_POSITION_MSG_LIST, mChatMsgEntity.getServiceMessage().getBy2(),MsgSerMsgFirstRender.CLICK_FROM_ITEM,mChatMsgEntity.getMid());
                    }
                    UpEventAgent.onZSIMServiceItemClick(MainApplication.getInstance(),
                            mChatMsgEntity.getMid(),
                            mChatMsgEntity.chatId,
                            mChatMsgEntity.getServiceMessage().getTitle(),
                            mChatMsgEntity.getType()
                    );  //ZSSDK
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

        if (mChatMsgEntity != null && mChatMsgEntity.getServiceMessage() != null) {
            //将控件放入缓存
            tvTitle = mViewHolder.obtainView(mContentView, R.id.total_tv);
            tvContent = mViewHolder.obtainView(mContentView, R.id.total_tv_content);
            ivTotalImage = mViewHolder.obtainView(mContentView, R.id.total_pic);
//            llChild = mViewHolder.obtainView(mContentView, R.id.ll_add_item);
            viewLine = mViewHolder.obtainView(mContentView, R.id.view_line);
            tvBottom = mViewHolder.obtainView(mContentView, R.id.tv_bottom);
            tvComeFrom = mViewHolder.obtainView(mContentView, R.id.tv_comefrom);

            if (mChatMsgEntity.getServiceMessage().getImage_url() != null && !"".equals( mChatMsgEntity.getServiceMessage().getImage_url())) {
                ivTotalImage.setVisibility(View.VISIBLE);
//                ivTotalImage.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
                lp.setMargins(5, 0, 5, 20);
                ivTotalImage.setLayoutParams(lp);
                ImageLoader.getInstance().displayImage(mChatMsgEntity.getServiceMessage().getImage_url(), ivTotalImage, options);
            } else {
                ivTotalImage.setVisibility(View.GONE);
            }
            tvTitle.setText(mChatMsgEntity.getServiceMessage().getTitle());
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(mChatMsgEntity.getServiceMessage().getDigst());

            if(mChatMsgEntity.getServiceMessage().getSubDigst()!=null && !"".equals(mChatMsgEntity.getServiceMessage().getSubDigst())){
                tvComeFrom.setVisibility(View.VISIBLE);
                tvComeFrom.setText(mChatMsgEntity.getServiceMessage().getSubDigst());
            }else {
                tvComeFrom.setVisibility(View.GONE);
            }


            if (mChatMsgEntity.getServiceMessage().getIntent_data() != null) {
                if("".equals(mChatMsgEntity.getServiceMessage().getIntent_data())){
                    viewLine.setVisibility(View.GONE);
                    tvBottom.setVisibility(View.GONE);
                }else {
                    viewLine.setVisibility(View.VISIBLE);
                    tvBottom.setVisibility(View.VISIBLE);
                }
            }

//多条的情况暂时去掉
//            final List<ServiceMessageChild> serviceMessageChildList = mChatMsgEntity.getServiceMessage()
//                    .getChilds();
//
//            if (serviceMessageChildList != null && serviceMessageChildList.size() >= 0) {
//                if (serviceMessageChildList.size() > 0) {
//                    tvContent.setVisibility(View.GONE);
//                    viewLine.setVisibility(View.VISIBLE);
//                }
//                llChild.removeAllViews();
//                for (int i = 0; i < serviceMessageChildList.size(); i++) {
//                    View view = LayoutInflater.from(mContext).inflate(
//                            R.layout.im_souyue_message_additem, null);
//                    ImageView ivChild = (ImageView) view
//                            .findViewById(R.id.iv_list_head);
//                    TextView tvChild = (TextView) view.findViewById(R.id.tv_list_content);
//                    ImageLoader.getInstance().displayImage(serviceMessageChildList.get(i).getPic(), ivChild, options);
//                    tvChild.setText(serviceMessageChildList.get(i).getDigst());
//                    final String c = serviceMessageChildList.get(i).getC();
//                    if (i == serviceMessageChildList.size() - 1) {
//                        view.findViewById(R.id.view_item_line).setVisibility(View.GONE);
//                    }
//                    view.setOnClickListener(new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View arg0) {
//                            if (c != null && !"".equals(c)) {
//                                ImJump2SouyueUtil.getInstance().jump(mContext,
//                                        c, MsgSerMsgFirstRender.STATISTICS_JUMP_POSITION_MSG_LIST, mChatMsgEntity.getServiceMessage().getBy2());
//                            }
//                        }
//                    });
//                    llChild.addView(view);
//                }
//                llChild.setVisibility(View.VISIBLE);
//            } else {
//                llChild.setVisibility(View.GONE);
//            }

        }
    }

    @Override
    protected int getLeftLayoutId() {
        return R.layout.msg_sermsgsecond_left_view;
    }

    @Override
    protected int getRightLayoutId() {
        return 0;
    }


}