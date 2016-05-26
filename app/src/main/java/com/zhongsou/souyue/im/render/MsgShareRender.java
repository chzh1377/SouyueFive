package com.zhongsou.souyue.im.render;

/**
 * Created by zhangwenbin on 15/3/24.
 */

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.im.util.IMIntentUtil;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.StringUtils;

public class MsgShareRender extends MsgItemRender {

    /**
     * 分享类型的icon
     */
    private ImageView ivIcon;

    /**
     * 分享摘要
     */
    private TextView tvShareText;

    /**
     * 兴趣圈分享8添加
     */
    private TextView tvLogoText;

    /**
     * 标题
     */
    private String title;

    public MsgShareRender(Context context,
                          BaseTypeAdapter<ChatMsgEntity> adapter, int itemType) {
        super(context, adapter, itemType);

    }

    @Override
    public void fitEvents() {
        super.fitEvents();
        // TODO custom event

        //分享内容点击事件
        mViewHolder.obtainView(mContentView, R.id.ll_msg_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mChatAdapter.getIsEdit()) {
                    //根据不同消息类型显示
                    int interestInt = 0;        //7,13
                    long interestLong = 0;     //8,14
                    switch (mChatMsgEntity.getType()) {
                        case IMessageConst.CONTENT_TYPE_INTEREST_SHARE:                //  圈吧分享帖 7
                            try {
                                interestInt = Integer.parseInt(mChatMsgEntity
                                        .getImshareinterest().getInterest_id());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            //个人感觉这段代码是没有用的，如果发现bug及时更改
//                    CircleResponseResultItem item = new CircleResponseResultItem();
//                    item.setBlog_id(Long.valueOf(e.getImshareinterest().getBlog_id()));
//                    item.setInterest_id(interest_id);
//                    item.setIs_prime(e.getImshareinterest().getIs_prime());
//                    item.setTop_status(e.getImshareinterest().getTop_status());
//                    item.setUser_id(e.getImshareinterest().getUser_id());
//                            UIHelper.showPostsDetail(mContext, Long.valueOf(mChatMsgEntity.getImshareinterest().getBlog_id()), interestInt);
                            SearchResultItem item1 = new SearchResultItem();
                            item1.setBlog_id(Long.valueOf(mChatMsgEntity.getImshareinterest().getBlog_id()));
                            item1.setInterest_id(interestInt);
                            IntentUtil.startskipDetailPage(mContext, item1);
                            break;
                        case IMessageConst.CONTENT_TYPE_INTEREST_ADD_FRIEND:           // 添加好友入公开圈吧 8
                            try {
                                interestLong = Long.parseLong(mChatMsgEntity.getImaskinterest()
                                        .getInterest_id());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            IntentUtil.gotoSecretCricleCard(mContext, interestLong);
                            break;
                        case IMessageConst.CONTENT_TYPE_SOUYUE_NEWS_SHARE:             //搜悦新闻分享 9 - souxiaoyue
//                            IMIntentUtil.souyueNewsClick(mContext, mChatMsgEntity);
//                            break;
                        case IMessageConst.CONTENT_TYPE_SRP_SHARE:                     // SRP词分享类型 20 - souxiaoyue
                            //跟9一模一样
                            IMIntentUtil.souyueNewsClick(mContext, mChatMsgEntity);
                            invokeZSIMServiceItemClick();
                            break;
                        case IMessageConst.CONTENT_TYPE_INTEREST_CIRCLE_CARD:          //圈名片分享 13 - souxiaoyue
                            try {
                                interestInt = Integer.parseInt(mChatMsgEntity
                                        .getImshareinterest().getInterest_id());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            IntentUtil.gotoSecretCricleCard(mContext, interestInt);
                            invokeZSIMServiceItemClick();
                            break;
                        case IMessageConst.CONTENT_TYPE_INTEREST_ADD_FRIEND_PRIVATE:   //添加好友入私密圈吧 14
                            try {
                                interestLong = Long.parseLong(mChatMsgEntity.getImaskinterest()
                                        .getInterest_id());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            IntentUtil.gotoSecretCricleCard(mContext, interestLong);
                            break;
                        case IMessageConst.CONTENT_TYPE_WEB:                           // WEB跳转类型（贺卡等）23 - souxiaoyue
                            IntentUtil.gotoWeb(mContext, mChatMsgEntity.getmPosts().getUrl(), "interactWeb");
                            invokeZSIMServiceItemClick();
                            break;
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

        //分享内容的长按事件
        mViewHolder.obtainView(mContentView, R.id.ll_msg_share).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mChatAdapter.getIsEdit()) {
                    showLCDialog(false, false);
                }
                return true;
            }
        });
    }

    /**
     * 调用ZSSDK 统计
     */
    private void invokeZSIMServiceItemClick(){
        //如果是搜小悦 才统计
        if (mChatMsgEntity.getChatType() == IConst.CHAT_TYPE_SERVICE_MESSAGE
                && mChatMsgEntity.chatId == IConst.SOUXIAOYUE_ID){
            UpEventAgent.onZSIMServiceItemClick(MainApplication.getInstance(),
                    mChatMsgEntity.getMid(),
                    mChatMsgEntity.chatId,
                    title,
                    mChatMsgEntity.getType()
            );  //ZSSDK
        }
    }


    @Override
    public void fitDatas(int position) {
        super.fitDatas(position);
        // TODO custom fit data

        //将控件放入缓存
        ivIcon = mViewHolder.obtainView(mContentView, R.id.im_share_news_icon);
        tvShareText = mViewHolder.obtainView(mContentView, R.id.im_share_news_context);
        tvLogoText = mViewHolder.obtainView(mContentView, R.id.im_logo_icon);

        //根据不同消息类型显示
        switch (mChatMsgEntity.getType()) {
            case IMessageConst.CONTENT_TYPE_INTEREST_SHARE:                //  圈吧分享帖 7
                if (mChatMsgEntity.getImshareinterest() != null) {
                    if (!StringUtils.isEmpty(mChatMsgEntity.getImshareinterest().getBlog_logo())) {
                        ImageLoader.getInstance().displayImage(mChatMsgEntity.getImshareinterest().getBlog_logo(), ivIcon, MsgUtils.getShareIconOptions());
                    } else {
                        ivIcon.setImageResource(R.drawable.im_souyue_share_news_default);
                    }
                    ivIcon.setVisibility(View.VISIBLE);
                    String content = "";
                    if (!StringUtils.isEmpty(mChatMsgEntity.getImshareinterest().getBlog_title())) {
                        content = mChatMsgEntity.getImshareinterest().getBlog_title();
                    } else {
                        content = mChatMsgEntity.getImshareinterest().getBlog_content();
                    }
                    tvShareText.setText(content);
                }
                break;
            case IMessageConst.CONTENT_TYPE_INTEREST_ADD_FRIEND:           // 添加好友入公开圈吧 8
                if (mChatMsgEntity.getImaskinterest() != null) {
                    if (!StringUtils.isEmpty(mChatMsgEntity.getImaskinterest().getInterest_logo())) {
//                        aquery.id(h.im_share_news_icon).image(
//                                mChatMsgEntity.getImaskinterest().getInterest_logo(), true, true);
                        ImageLoader.getInstance().displayImage(mChatMsgEntity.getImaskinterest().getInterest_logo(), ivIcon, MsgUtils.getShareIconOptions());
                    } else {
                        ivIcon.setImageResource(R.drawable.im_souyue_share_news_default);
                    }
                    ivIcon.setVisibility(View.VISIBLE);
                    if (!StringUtils.isEmpty(mChatMsgEntity.getImaskinterest().getInterest_name())) {
                        tvShareText.setText(mChatMsgEntity.getImaskinterest()
                                .getInterest_name());
                    }
                    tvLogoText.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.im_chat_interest_icon, 0, 0, 0);
                    tvLogoText.setText(R.string.im_interest_text);
                }
                break;
            case IMessageConst.CONTENT_TYPE_SOUYUE_NEWS_SHARE:             //搜悦新闻分享 9
                if (mChatMsgEntity.getImsharenews() != null) {
                    if (!StringUtils.isEmpty(mChatMsgEntity.getImsharenews().getImgurl())) {
                        ImageLoader.getInstance().displayImage(mChatMsgEntity.getImsharenews().getImgurl(), ivIcon, MsgUtils.getShareIconOptions());
                    } else {
                        ivIcon.setImageResource(R.drawable.im_souyue_share_news_default);
                    }
                    tvShareText.setText(mChatMsgEntity.getImsharenews().getTitle());
                    title = mChatMsgEntity.getImsharenews().getTitle();
                }
                break;
            case IMessageConst.CONTENT_TYPE_INTEREST_CIRCLE_CARD:          //圈名片分享 13
                if (mChatMsgEntity.getImshareinterest() != null) {
                    if (!StringUtils.isEmpty(mChatMsgEntity.getImshareinterest().getBlog_logo())) {
//                        aquery.id(h.im_share_news_icon).image(
//                                mChatMsgEntity.getImshareinterest().getBlog_logo(), true, true);
                        ImageLoader.getInstance().displayImage(mChatMsgEntity.getImshareinterest().getBlog_logo(), ivIcon, MsgUtils.getShareIconOptions());
                    } else {
                        ivIcon.setVisibility(View.GONE);
                    }
                    String content = "";
                    if (!StringUtils.isEmpty(mChatMsgEntity.getImshareinterest().getBlog_title())) {
                        content = mChatMsgEntity.getImshareinterest().getBlog_title();
                    } else {
                        content = mChatMsgEntity.getImshareinterest().getBlog_content();
                    }
                    tvShareText.setText(content);
                    title = content;
                }
                break;
            case IMessageConst.CONTENT_TYPE_INTEREST_ADD_FRIEND_PRIVATE:   //添加好友入私密圈吧 14
                if (mChatMsgEntity.getImaskinterest() != null) {
                    if (!StringUtils.isEmpty(mChatMsgEntity.getImaskinterest().getInterest_logo())) {
//                        aquery.id(h.im_share_news_icon).image(
//                                mChatMsgEntity.getImaskinterest().getInterest_logo(), true, true);
                        ImageLoader.getInstance().displayImage(mChatMsgEntity.getImaskinterest().getInterest_logo(), ivIcon, MsgUtils.getShareIconOptions());
                    } else {
                        ivIcon.setVisibility(View.GONE);
                    }
                    if (!StringUtils.isEmpty(mChatMsgEntity.getImaskinterest().getInterest_name())) {
                        tvShareText.setText(mChatMsgEntity.getImaskinterest()
                                .getInterest_name());
                    }
                    tvLogoText.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.im_chat_interest_icon, 0, 0, 0);
                    tvLogoText.setText(R.string.im_interest_text);
                }
                break;
            case IMessageConst.CONTENT_TYPE_SRP_SHARE:                     // SRP词分享类型 20
                if (mChatMsgEntity.getImsharenews() != null) {
                    if (!StringUtils.isEmpty(mChatMsgEntity.getImsharenews().getImgurl())) {
                        ImageLoader.getInstance().displayImage(mChatMsgEntity.getImsharenews().getImgurl(), ivIcon, MsgUtils.getShareIconOptions());
                    } else {
                        ivIcon.setImageResource(R.drawable.im_souyue_share_news_default);
                    }
                    ivIcon.setVisibility(View.VISIBLE);
                    tvShareText.setText(mChatMsgEntity.getImsharenews().getTitle());
                    title = mChatMsgEntity.getImsharenews().getTitle();
                }
                break;
            case IMessageConst.CONTENT_TYPE_WEB:                           // WEB跳转类型（贺卡等）23
                if (mChatMsgEntity.getmPosts() != null) {
                    if (!StringUtils.isEmpty(mChatMsgEntity.getmPosts().getImage_url())) {
//                        aquery.id(h.im_share_news_icon).image(
//                                mChatMsgEntity.getmPosts().getImage_url(), true, true);
                        ImageLoader.getInstance().displayImage(mChatMsgEntity.getmPosts().getImage_url(), ivIcon, MsgUtils.getShareIconOptions());
                    } else {
                        ivIcon.setVisibility(View.GONE);
                    }
                    String content = "";
                    if (!StringUtils.isEmpty(mChatMsgEntity.getmPosts().getTitle())) {
                        content = mChatMsgEntity.getmPosts().getTitle();
                    } else {
                        content = mChatMsgEntity.getmPosts().getContent();
                    }
                    tvShareText.setText(content);
                    title = content;
                }
                break;

        }

    }

    @Override
    protected int getLeftLayoutId() {
        return R.layout.msg_share_left_view;
    }

    @Override
    protected int getRightLayoutId() {
        return R.layout.msg_share_right_view;
    }

}