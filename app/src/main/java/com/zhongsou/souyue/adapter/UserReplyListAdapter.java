package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.model.CircleBlogReply;
import com.zhongsou.souyue.utils.MyImageLoader;
import com.zhongsou.souyue.utils.SYMediaplayer;
import com.zhongsou.souyue.utils.SYMediaplayer_Mine;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class UserReplyListAdapter extends BaseAdapter {

    //    private List<Comment> comments = new ArrayList<Comment>();
    private List<CircleBlogReply> circlrList = new ArrayList<CircleBlogReply>();
    private Context context;
    private SYMediaplayer_Mine audio;

    public UserReplyListAdapter(Context context) {
        this.context = context;
        audio = SYMediaplayer_Mine.getInstance(context);
    }

    public int getCount() {
        return circlrList.size();
    }

    public Object getItem(int position) {
        return circlrList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = View.inflate(context, R.layout.user_reply_list_item, null);
            holder = new ViewHolder();
            holder.tv_reply_title = (TextView) view.findViewById(R.id.tv_reply_title);

            holder.ll_audio = (RelativeLayout) view.findViewById(R.id.ll_user_reply_audio);

            holder.imgbtn_audio_play = (ImageButton) view.findViewById(R.id.imgbtn_user_reply_audio_play);

            holder.imgbtn_audio_play.setTag(circlrList.get(position));

            holder.tv_audio_times = (TextView) view.findViewById(R.id.tv_reply_audio_times);

            holder.tv_time = (TextView) view.findViewById(R.id.tv_reply_time);

            holder.tv_tag = (TextView) view.findViewById(R.id.tv_reply_tagstring);

            holder.tv_nickname = (TextView) view.findViewById(R.id.my_comment_nickname);

            holder.iv_user_head = (ImageView) view.findViewById(R.id.my_comment_user_head);

            holder.iv_user_reply_item = (ImageView) view.findViewById(R.id.iv_user_reply_item);

            holder.tv_come_from = (TextView) view.findViewById(R.id.tv_reply_by_nickname);

            holder.tv_me_audio_times = (TextView) view.findViewById(R.id.tv_me_reply_audio_times);

            holder.ll_me_audio = (RelativeLayout) view.findViewById(R.id.ll_me_reply_audio);

            holder.imgbtn_me_audio_play = (ImageButton) view.findViewById(R.id.imgbtn_me_reply_audio_play);

            holder.imgbtn_reply_audio_anmi = (ImageView) view.findViewById(R.id.imgbtn_reply_audio_anmi);

            holder.imgbtn_me_reply_audio_anmi = (ImageView) view.findViewById(R.id.imgbtn_me_reply_audio_anmi);

            holder.imgbtn_reply_audio_anmi.setTag(circlrList.get(position));
            holder.imgbtn_me_reply_audio_anmi.setTag(circlrList.get(position));
            holder.imgbtn_me_audio_play.setTag(circlrList.get(position));
            view.setTag(holder);

        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        try {
            setViewData(holder, position);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.getClass().getSimpleName(), "setViewData Exception in UserReplyListAdapter");
        }
        return view;
    }

    private void setViewData(final ViewHolder holder, int position) throws Exception{
//            final Comment n = comments.get(position);
        final CircleBlogReply circleBlog = circlrList.get(position);
        switch (circleBlog.getCommentType()) {
            case 1:// 1、我的评论(评论***)
                if (!TextUtils.isEmpty(circleBlog.getSubBlog().getContent())) {
                    holder.tv_tag.setVisibility(View.VISIBLE);
                    holder.tv_tag.setText(Html.fromHtml("<font color='#2b2b2b' size='28px'>" + circleBlog.getSubBlog().getContent() + "</font>"));
                } else {
                    holder.tv_tag.setVisibility(View.GONE);
                }

                if (circleBlog.getMainBlog().getType() == 1) {//圈子
                    if (circleBlog.getMainBlog().getNickname() != null && "".equals(circleBlog.getMainBlog().getNickname())) {
                        holder.tv_come_from.setVisibility(View.GONE);
                    } else {
                        holder.tv_come_from.setVisibility(View.VISIBLE);
                        holder.tv_come_from.setText("@" + circleBlog.getMainBlog().getNickname());
                    }
                    if (circleBlog.getMainBlog().getBrief() != null && "".equals(circleBlog.getMainBlog().getBrief())) {
                        holder.tv_reply_title.setVisibility(View.GONE);
                    } else {
                        holder.tv_reply_title.setVisibility(View.VISIBLE);
                        holder.tv_reply_title.setText(circleBlog.getMainBlog().getBrief());
                    }
                    if (circleBlog.getMainBlog().getImages() != null
                            && circleBlog.getMainBlog().getImages().size() > 0
                            && !"".equals(circleBlog.getMainBlog().getImages().get(0))) {
                        MyImageLoader.imageLoader.displayImage(circleBlog.getMainBlog().getImages().get(0), holder.iv_user_reply_item, MyImageLoader.userReplyListOptions);
                    } else {
                        MyImageLoader.imageLoader.displayImage(circleBlog.getMainBlog().getImage_url(), holder.iv_user_reply_item, MyImageLoader.userReplyListOptions);
                    }
                } else {//新闻
                    if (circleBlog.getMainBlog().getTitle() != null && "".equals(circleBlog.getMainBlog().getTitle())) {
                        holder.tv_come_from.setVisibility(View.GONE);
                    } else {
                        holder.tv_come_from.setVisibility(View.VISIBLE);
                        holder.tv_come_from.setText(circleBlog.getMainBlog().getTitle());
                    }
                    if (circleBlog.getMainBlog().getBrief() != null && "".equals(circleBlog.getMainBlog().getBrief())) {
                        holder.tv_reply_title.setVisibility(View.GONE);
                    } else {
                        holder.tv_reply_title.setVisibility(View.VISIBLE);
                        holder.tv_reply_title.setText(circleBlog.getMainBlog().getBrief());
                    }
                    if (circleBlog.getMainBlog().getImages() != null
                            && circleBlog.getMainBlog().getImages().size() > 0
                            && !"".endsWith(circleBlog.getMainBlog().getImages().get(0))) {
                        MyImageLoader.imageLoader.displayImage(circleBlog.getMainBlog().getImages().get(0), holder.iv_user_reply_item, MyImageLoader.userReplyListOptions);
                    } else {
                        MyImageLoader.imageLoader.displayImage(circleBlog.getMainBlog().getSrp_logo(), holder.iv_user_reply_item, MyImageLoader.userReplyListOptions);
                    }
                }

                break;
                /*case 2:// 2、我的回复 (回复***)
                    holder.tv_tag.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(circleBlog.getSubBlog().getContent())) {
                        holder.tv_tag.setText(Html.fromHtml("<font color='#7e7e7e' size='28px'>回复" + "</font><font color='#3e83d8' size='28px'>" + n.replyTo().user().name() + ":" + "</font>"
                                + "<font color='#2b2b2b' size='24px'>" + circleBlog.getSubBlog().getContent() + "</font>"));
                    } else {
                        holder.tv_tag.setText(Html.fromHtml("<font color='#7e7e7e' size='28px'>回复" + "</font><font color='#3e83d8' size='28px'>" + n.replyTo().user().name() + ":" + "</font>"));
                    }

                    break;*/
            case 3:// 3、回复我的(***回复)
                if (!TextUtils.isEmpty(circleBlog.getSubBlog().getContent())) {
                    holder.tv_tag.setVisibility(View.VISIBLE);
                    holder.tv_tag.setText(circleBlog.getSubBlog().getContent());
                } else {
                    holder.tv_tag.setVisibility(View.GONE);
                }


                holder.tv_come_from.setText("@" + circleBlog.getMainBlog().getNickname());
                MyImageLoader.imageLoader.displayImage(circleBlog.getMainBlog().getImage_url(), holder.iv_user_reply_item, MyImageLoader.userReplyListOptions);

                if (TextUtils.isEmpty(circleBlog.getMainBlog().getBrief())) {
                    holder.tv_reply_title.setVisibility(View.GONE);
                    holder.ll_me_audio.setVisibility(View.VISIBLE);
                    holder.imgbtn_me_audio_play.setFocusable(false);
                    holder.imgbtn_me_audio_play.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(circleBlog.getMainBlog().getVoice())) {
                                audio.play(holder.imgbtn_me_reply_audio_anmi, SYMediaplayer.SOURCE_TYPE_NET, circleBlog.getMainBlog().getVoice());
                            }
                        }
                    });
                    holder.tv_me_audio_times.setText(circleBlog.getMainBlog().getVoice_length() + "\"");

                } else {
                    holder.tv_reply_title.setVisibility(View.VISIBLE);
                    holder.tv_reply_title.setText(circleBlog.getMainBlog().getBrief());
                    holder.ll_me_audio.setVisibility(View.GONE);

                }
                break;
            default:
                holder.tv_tag.setVisibility(View.GONE);
        }
        /*holder.tv_reply_title.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (circleBlog != null) {
                    //去往srp词页面
//						gotoSRP(n.keyword(), n.srpId());
                    //去往评论所在新闻详情页面
                    Intent intent = new Intent();
                    SearchResultItem resultItem = new SearchResultItem();
                    intent.setClass(context, ReadabilityActivity.class);
                    Bundle bundle = new Bundle();
                    resultItem.title_$eq(circleBlog.getMainBlog().getTitle());
                    resultItem.keyword_$eq(circleBlog.getMainBlog().getSrp_word());
                    resultItem.date_$eq(circleBlog.getSubBlog().getCreate_time() + "");
                    resultItem.url_$eq(circleBlog.getMainBlog().getUrl());
                    resultItem.srpId_$eq(circleBlog.getMainBlog().getSrp_id());
                    bundle.putSerializable("searchResultItem", (Serializable) resultItem);
                    intent.putExtras(bundle);
                    intent.putExtra("from", "mycomments");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);

                }
            }
        });*/

        if (TextUtils.isEmpty(circleBlog.getSubBlog().getContent())) {
            // content为空
            holder.ll_audio.setVisibility(View.VISIBLE);
            holder.imgbtn_audio_play.setFocusable(false);
            holder.imgbtn_audio_play.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    audio.play(holder.imgbtn_reply_audio_anmi, SYMediaplayer.SOURCE_TYPE_NET, circleBlog.getSubBlog().getVoice());
                }
            });
            holder.tv_audio_times.setText(circleBlog.getSubBlog().getVoice_length() + "\"");

        } else {
            holder.ll_audio.setVisibility(View.GONE);

        }
//            holder.tv_time.setText("asdfasdfa");
        holder.tv_time.setText(StringUtils.convertDate(circleBlog.getSubBlog().getCreate_time() + ""));
        holder.tv_nickname.setText(circleBlog.getSubBlog().getNickname());
//            holder.tv_nickname.setText("asdfasdf");
        holder.iv_user_head.setTag(circleBlog);
        MyImageLoader.imageLoader.displayImage(circleBlog.getSubBlog().getImage_url(), holder.iv_user_head, MyImageLoader.options);
//            holder.comment = n;
        holder.circleBlogReply = circleBlog;
    }

//	public void gotoSRP(String keyword, String srpId) {
//        if (!StringUtils.isEmpty(keyword) && !StringUtils.isEmpty(srpId)) {
//            Intent intent = new Intent();
//            intent.setClass(context, SRPActivity.class);
//            intent.putExtra("keyword", keyword);
//            intent.putExtra("srpId", srpId);
//            context.startActivity(intent);
//        }
//    }

    public class ViewHolder {
        TextView tv_reply_title, tv_tag, tv_audio_times, tv_me_audio_times, tv_time, tv_nickname, tv_come_from;
        RelativeLayout ll_audio, ll_me_audio;
        ImageButton imgbtn_audio_play, imgbtn_me_audio_play;
        ImageView imgbtn_reply_audio_anmi, imgbtn_me_reply_audio_anmi;
        ImageView iv_user_head, iv_user_reply_item;

        //        public Comment comment;
        public CircleBlogReply circleBlogReply;
    }

    public void addItem(CircleBlogReply item) {
        circlrList.add(item);
    }

    public void clearNotices() {
        if (this.circlrList != null) {
            this.circlrList.clear();
        }
    }
}
