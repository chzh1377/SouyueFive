package com.zhongsou.souyue.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;
import com.zhongsou.souyue.module.BoZhu;
import com.zhongsou.souyue.module.HotTopic;
import com.zhongsou.souyue.module.ImageUrlInfo;
import com.zhongsou.souyue.module.ReplyTo;
import com.zhongsou.souyue.module.Weibo;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.utils.MyImageLoader;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.List;

/**
 * 微博搜索适配器
 * @author qubian
 * 加注释
 */
public class WeiboAdapter extends SouyueAdapter {


    private static final int TYPE_BoZhu = 1;
    private static final int TYPE_HotTopics = 2;
    private static final int TYPE_Reply = 3;
    private static final int TYPE_NORMAL = 4;

    public WeiboAdapter(Context context) {
        super(context);
        setMaxCount(4);
    }

    public BoZhu getBoZhu() {
        if (datas != null && datas.size() > 0) {
            BoZhu boZhu = datas.get(0).boZhu();
            if (boZhu != null && boZhu.user() != null && !TextUtils.isEmpty(boZhu.user().name()))
                return boZhu;
        }
        return null;
    }

    public List<HotTopic> getHotTopics() {
        //有博主的时候
        if (getBoZhu() != null) {
            if (datas != null && datas.size() > 1)
                return datas.get(1).hotTopics();
        } else {
            // 没有博主的时候
            if (datas != null && datas.size() > 0)
                return datas.get(0).hotTopics();
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        int len = this.datas.size();
        if (position < len) {
            if (position == 0 && getBoZhu() != null)
                return TYPE_BoZhu;
            if (((getBoZhu() == null && position == 0) || (getBoZhu() != null && position == 1)) && getHotTopics() != null && getHotTopics().size() > 0)
                return TYPE_HotTopics;
            Weibo weibo = datas.get(position).weibo();
            ReplyTo replyTo = weibo.replyTo();
            if (replyTo != null && !TextUtils.isEmpty(replyTo.content())) {
                return TYPE_Reply;
            } else
                return TYPE_NORMAL;
        } else {
            if ((position == len + 1) && hasMoreItems) {
                if (!CMainHttp.getInstance().isNetworkAvailable(MainApplication.getInstance()))
                    return TYPE_ITEM_GET_MORE;
                else
                    return TYPE_ITEM_WAITING;
            }

        }

        return 0;
    }

    @Override
    View getCurrentView(int position, View convertView, ViewHolder holder) {
        int type = getItemViewType(position);
        if (type == TYPE_BoZhu) {
            convertView = inflateView(R.layout.list_item_weibo_summary);
            holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_face);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.source = (TextView) convertView.findViewById(R.id.tv_source);
            holder.follow = (TextView) convertView.findViewById(R.id.tv_follow);
            holder.fans = (TextView) convertView.findViewById(R.id.tv_fans);
            holder.weiboCount = (TextView) convertView.findViewById(R.id.tv_weiboCount);
            holder.desc = (TextView) convertView.findViewById(R.id.tv_newWeibo);
            holder.container = (ViewGroup) convertView;
        } else if (type == TYPE_HotTopics) {
            convertView = inflateView(R.layout.list_item_weibo_hot_topic);
            holder.container = (ViewGroup) convertView.findViewById(R.id.container);

        } else {
            if (type == TYPE_Reply) {
                convertView = inflateView(R.layout.list_item_weibo_reply);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_face);
                holder.iv_item = (ImageView) convertView.findViewById(R.id.iv_image);
                holder.iv_marked = (ImageView) convertView.findViewById(R.id.iv_reply_image);//replyTo的图片
                holder.marked = (TextView) convertView.findViewById(R.id.tv_replyName);
                holder.desc = (TextView) convertView.findViewById(R.id.tv_content);
                holder.replyContent = (TextView) convertView.findViewById(R.id.tv_replyContent);
                holder.source = (TextView) convertView.findViewById(R.id.tv_source);
                holder.date = (TextView) convertView.findViewById(R.id.tv_date);
            } else {

                convertView = inflateView(R.layout.list_item_weibo_normal);
                holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_face);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.desc = (TextView) convertView.findViewById(R.id.tv_content);
                holder.source = (TextView) convertView.findViewById(R.id.tv_source);
                holder.date = (TextView) convertView.findViewById(R.id.tv_date);
                holder.iv_item = (ImageView) convertView.findViewById(R.id.iv_image);
                holder.marked = (TextView) convertView.findViewById(R.id.tv_original);//原创
            }

        }

        convertView.setTag(holder);
        //super.setFontSize(holder.title);
        return convertView;
    }

    @Override
    void setViewData(int position, ViewHolder holder) {
        holder.searchResultItem = datas.get(position);
        //float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
		//holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        int type = getItemViewType(position);
        if (type == TYPE_BoZhu) {
            try {

                MyImageLoader.imageLoader.displayImage(getBoZhu().user().image(), holder.iv_pic,  MyImageLoader.options);
//                holder.iv_pic.setBackgroundResource(R.drawable.default_head);
//                aq.id(holder.iv_pic).image(getBoZhu().user().image(), true, true, 0, 0, null, AQuery.FADE_IN);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            holder.tv_name.setText(getBoZhu().user().name());
            holder.source.setText(getBoZhu().source());
            holder.follow.setText(getBoZhu().follow() + "");
            holder.fans.setText(getBoZhu().fans() + "");
            holder.weiboCount.setText(getBoZhu().weibo() + "");
            Weibo newWeibo = getBoZhu().newWeibo();
            if (newWeibo != null && !TextUtils.isEmpty(newWeibo.content()))
                holder.desc.setText("最新微博：" + newWeibo.content());
            holder.container.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    String url = getBoZhu().user().url();
                    if (TextUtils.isEmpty(url))
                        return;
                    Intent intent = new Intent();
                    intent.setClass(mContext, WebSrcViewActivity.class);
                    intent.putExtra(WebSrcViewActivity.PAGE_URL, url);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.left_in, R.anim.left_out);
                }
            });
        } else if (type == TYPE_HotTopics) {
            holder.container.removeAllViews();
            for (HotTopic hotTopic : getHotTopics()) {
                LinearLayout ll_root = (LinearLayout) inflateView(R.layout.hot_topic_item);
                TextView tv_item = (TextView) ll_root.findViewById(R.id.tv_item);
                tv_item.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        String url = (String) v.getTag();
                        if (TextUtils.isEmpty(url))
                            return;
                        Intent intent = new Intent();
                        intent.setClass(mContext, WebSrcViewActivity.class);
                        intent.putExtra(WebSrcViewActivity.PAGE_URL, url);
                        mContext.startActivity(intent);
                        ((Activity) mContext).overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }
                });
                tv_item.setText(hotTopic.title());
                tv_item.setTag(hotTopic.url());
                holder.container.addView(ll_root);
            }
        } else {
            Weibo weibo = datas.get(position).weibo();
            ReplyTo replyTo = weibo.replyTo();
            if (type == TYPE_Reply) {
                List<ImageUrlInfo> replyImages = weibo.replyTo().image();
                if (replyImages != null && replyImages.size() > 0) {
                    if (TextUtils.isEmpty(replyImages.get(0).small()) || !isImgAble())
                        holder.iv_marked.setVisibility(View.GONE);
                    else {
                        holder.iv_marked.setVisibility(View.VISIBLE);
                       // aq.id(holder.iv_marked).image(replyImages.get(0).small(), true, true, 0, 0, null, AQuery.FADE_IN);
                        PhotoUtils.showCard(UriType.HTTP, replyImages.get(0).small(), holder.iv_marked, MyDisplayImageOption.smalloptions);
                    }
                } else {
                    holder.iv_marked.setVisibility(View.GONE);
                }
                holder.tv_name.setText(weibo.user().name());
                holder.marked.setText(replyTo.user().name());
                holder.desc.setText(weibo.content());
                holder.replyContent.setText(replyTo.content());

                MyImageLoader.imageLoader.displayImage(weibo.user().image(),holder.iv_pic, MyImageLoader.options);
//                holder.iv_pic.setBackgroundResource(R.drawable.default_head);
//                aq.id(holder.iv_pic).image(weibo.user().image(), true, true, 0, 0, null, AQuery.FADE_IN);

                List<ImageUrlInfo> images = weibo.image();
                if (images != null && images.size() > 0) {
                    if (TextUtils.isEmpty(images.get(0).small()) || !isImgAble())
                        holder.iv_item.setVisibility(View.GONE);
                    else {
                        holder.iv_item.setVisibility(View.VISIBLE);
                       // aq.id(holder.iv_item).image(images.get(0).small(), true, true, 0, 0, null, AQuery.FADE_IN);
                        PhotoUtils.showCard(UriType.HTTP, images.get(0).small(), holder.iv_item, MyDisplayImageOption.smalloptions);
                    }
                } else {
                    holder.iv_item.setVisibility(View.GONE);
                }

                holder.source.setText(weibo.source());
                holder.date.setText(StringUtils.convertDate(weibo.date()));

            } else {//一般的

                try {
                    MyImageLoader.imageLoader.displayImage(weibo.user().image(),holder.iv_pic, MyImageLoader.options);
//                    holder.iv_pic.setBackgroundResource(R.drawable.default_head);
//                    aq.id(holder.iv_pic).image(weibo.user().image(), true, true, 0, 0, null, AQuery.FADE_IN);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                List<ImageUrlInfo> images = weibo.image();
                if (images != null && images.size() > 0) {
                    if (TextUtils.isEmpty(images.get(0).small()) || !isImgAble())
                        holder.iv_item.setVisibility(View.GONE);
                    else {
                        holder.iv_item.setVisibility(View.VISIBLE);
                        //aq.id(holder.iv_item).image(images.get(0).small(), true, true, 0, 0, null, AQuery.FADE_IN);
                        PhotoUtils.showCard(UriType.HTTP,images.get(0).small(),holder.iv_item);
                    }
                } else {
                    holder.iv_item.setVisibility(View.GONE);
                }
                holder.tv_name.setText(weibo.user().name());
                holder.desc.setText(weibo.content());
                holder.date.setText(StringUtils.convertDate(weibo.date()));
                if (weibo.category() == 1) {//原创
                    holder.source.setVisibility(View.GONE);
                    holder.marked.setVisibility(View.VISIBLE);
                } else {
                    holder.source.setVisibility(View.VISIBLE);
                    holder.marked.setVisibility(View.GONE);
                    holder.source.setText(weibo.source());

                }
            }
        }
    }
}