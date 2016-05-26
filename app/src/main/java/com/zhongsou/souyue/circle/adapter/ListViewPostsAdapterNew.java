package com.zhongsou.souyue.circle.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.TouchGalleryActivity;
import com.zhongsou.souyue.circle.activity.CircleIndexActivity;
import com.zhongsou.souyue.circle.activity.DetailActivity;
import com.zhongsou.souyue.circle.model.CommentsForCircleAndNews;
import com.zhongsou.souyue.circle.model.PersonPageParam;
import com.zhongsou.souyue.circle.model.Reply;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.circle.util.OnChangeListener;
import com.zhongsou.souyue.circle.util.TextViewUtil;
import com.zhongsou.souyue.im.emoji.EmojiPattern;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.TouchGallerySerializable;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CircleDelReplyRequest;
import com.zhongsou.souyue.net.circle.MblogDelete2Req;
import com.zhongsou.souyue.net.detail.AddCommentUpReq;
import com.zhongsou.souyue.net.detail.CommentSetHotRequest;
import com.zhongsou.souyue.net.volley.CDetailHttp;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CSouyueHttpError;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYMediaplayer;
import com.zhongsou.souyue.utils.SYMediaplayer_Mine;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Desc: 帖子详情适配器 User: tiansj DateTime: 14-4-18 下午1:43
 */
public class ListViewPostsAdapterNew extends BaseAdapter implements
        IVolleyResponse {

    //    private static final int CIRCLEDELREPLY_REQUESTID = 654654; // 删除回复
    private Activity context;
    private List<CommentsForCircleAndNews> postsList;
    private List<CommentsForCircleAndNews> postsListHot;
    int height, width;

    private SYMediaplayer_Mine audio;
    private long mblog_userId;
    private long interest_id;
    private int role;
    private String nickName;
    private String image;
    private long uid;
    private long update_posts_id;
    private Reply removingReply;
    private OnChangeListener listener;
    private final HashMap<String, View> m_valueToKey = new HashMap<String, View>();
    private long blogID;
    private android.view.animation.Animation animation;
    private CommentsForCircleAndNews tmpPosts;
    private PopupWindow popupWindow;
    private View popView;
    private LinearLayout ll_delete;
    private LinearLayout ll_huifu;
    private LinearLayout ll_remen;
    private LinearLayout ll_copy;
    private TextView tv_hot;
    private String mUrl;
    private String srp_id;
    private String keyWord;
    private int operType;
    private DisplayImageOptions options;
    private String main_title;
    private String main_images;
    private String main_name;
    private String main_decsription;
    private String main_date = "";
    private String main_source;
    private int mCircleType;
    private float fontSize;
    private boolean isUping;//正在点赞 ，网络状态
    private static final String OPSOURCE_FROM_COMMENT = "comment.srp.view";    //统计使用字段
    private int idImgArr[] = {R.id.image_1, R.id.image_2, R.id.image_3,
            R.id.image_4, R.id.image_5, R.id.image_6, R.id.image_7,
            R.id.image_8, R.id.image_9};

    private DisplayMetrics dm;

    public List<CommentsForCircleAndNews> getPostsList() {
        return postsList;
    }

    public List<CommentsForCircleAndNews> getPostsListHot() {
        return postsListHot;
    }

    public ListViewPostsAdapterNew(Activity context,
                                   List<CommentsForCircleAndNews> postsList,
                                   List<CommentsForCircleAndNews> postsListHot, long mblog_userId,
                                   long interest_id, int operType) {
        this.context = context;
        if (postsList == null) {
            this.postsList = new ArrayList<CommentsForCircleAndNews>();
        } else {
            this.postsList = postsList;
        }
        if (postsListHot == null) {
            this.postsListHot = new ArrayList<CommentsForCircleAndNews>();
        } else {
            this.postsListHot = postsListHot;
        }
        // aquery = new AQuery(context);
        animation = AnimationUtils.loadAnimation(context, R.anim.addone);
        this.mblog_userId = mblog_userId;
        this.interest_id = interest_id;
        this.operType = operType;
        dm = context.getResources().getDisplayMetrics();
        int w = 130, h = 80;
        height = (int) (h * dm.density);
        width = (int) (w * dm.density);

        audio = SYMediaplayer_Mine.getInstance(context);
//        http = new Http(this);
        fontSize = SYSharedPreferences.getInstance().loadResFont(context);

        this.options = new DisplayImageOptions.Builder().cacheOnDisk(true)
                .cacheInMemory(true)
                .showImageForEmptyUri(R.drawable.circle_default_head)
                .showImageOnFail(R.drawable.circle_default_head)
                .showImageOnLoading(R.drawable.circle_default_head)
                .build();

        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popView = layoutInflater.inflate(
                R.layout.circle_follow_more_popuwindow, null);// 自定义的布局文件
        popupWindow = new PopupWindow(popView, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        ll_delete = (LinearLayout) popView
                .findViewById(R.id.ll_circle_follow_more_delete);
        ll_huifu = (LinearLayout) popView
                .findViewById(R.id.ll_circle_follow_more_huifu);
        ll_remen = (LinearLayout) popView
                .findViewById(R.id.ll_circle_follow_more_remen);
        ll_copy = (LinearLayout) popView
                .findViewById(R.id.ll_circle_follow_more_copy);
        tv_hot = (TextView) popView.findViewById(R.id.tv_hot);

    }

    public void setCircleType(int mCircleType) {
        this.mCircleType = mCircleType;
    }

    public void setinterest_id(long interest_id) {
        this.interest_id = interest_id;
    }

    public void setChangeListener(OnChangeListener listener1) {
        this.listener = listener1;
    }

    @Override
    public int getCount() {
        return postsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.detail_posts_item, null);

            viewHolder.mTopLine = convertView
                    .findViewById(R.id.detail_list_line_top);
            viewHolder.head_photo = (ImageView) convertView
                    .findViewById(R.id.head_photo);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.content = (TextView) convertView
                    .findViewById(R.id.content);
//            viewHolder.edit = (TextView) convertView.findViewById(R.id.edit);
//            viewHolder.delete = (TextView) convertView
//                    .findViewById(R.id.delete);
//            viewHolder.reply = (TextView) convertView.findViewById(R.id.reply);
            viewHolder.tv_ding_count = (TextView) convertView
                    .findViewById(R.id.tv_ding_count);
            viewHolder.img = (ImageView) convertView
                    .findViewById(R.id.iv_cricle_good_icon);

            viewHolder.layout_image_all = (LinearLayout) convertView
                    .findViewById(R.id.layout_image_all);
            viewHolder.layout_image_1 = (LinearLayout) convertView
                    .findViewById(R.id.layout_image_1);
            viewHolder.layout_image_2 = (LinearLayout) convertView
                    .findViewById(R.id.layout_image_2);
            viewHolder.layout_image_3 = (LinearLayout) convertView
                    .findViewById(R.id.layout_image_3);
            viewHolder.imgArr = new ImageView[9];
            for (int i = 0; i < idImgArr.length; i++) {
                viewHolder.imgArr[i] = (ImageView) convertView
                        .findViewById(idImgArr[i]);
            }

            viewHolder.ding_layout = (LinearLayout) convertView
                    .findViewById(R.id.ding_layout);
            viewHolder.tv_add_one = (TextView) convertView
                    .findViewById(R.id.tv_add_one);
            viewHolder.imgMore = (ImageView) convertView
                    .findViewById(R.id.iv_circle_more);
            viewHolder.layout_reply = (LinearLayout) convertView
                    .findViewById(R.id.layout_reply);
            viewHolder.layout_reply_1 = (LinearLayout) convertView
                    .findViewById(R.id.layout_reply_1);
            viewHolder.layout_reply_2 = (LinearLayout) convertView
                    .findViewById(R.id.layout_reply_2);
            viewHolder.layout_reply_more = (LinearLayout) convertView
                    .findViewById(R.id.layout_reply_more);
            viewHolder.reply_content_1 = (TextView) convertView
                    .findViewById(R.id.reply_content_1);
            viewHolder.reply_content_2 = (TextView) convertView
                    .findViewById(R.id.reply_content_2);
            viewHolder.reply_time_1 = (TextView) convertView
                    .findViewById(R.id.reply_time_1);
            viewHolder.reply_time_2 = (TextView) convertView
                    .findViewById(R.id.reply_time_2);
            viewHolder.reply_delete_1 = (ImageView) convertView
                    .findViewById(R.id.reply_delete_1);
            viewHolder.reply_delete_2 = (ImageView) convertView
                    .findViewById(R.id.reply_delete_2);

            viewHolder.layout_reply_voice_1 = (LinearLayout) convertView
                    .findViewById(R.id.layout_reply_voice_1);
            viewHolder.layout_reply_voice_2 = (LinearLayout) convertView
                    .findViewById(R.id.layout_reply_voice_2);
            viewHolder.list_audio_play_1 = (ImageView) convertView
                    .findViewById(R.id.detail_voice_animator_r);
            viewHolder.list_audio_play_2 = (ImageView) convertView
                    .findViewById(R.id.detail_voice_animator_2);
            viewHolder.audio_length_1 = (TextView) convertView
                    .findViewById(R.id.detail_voice_second_r);
            viewHolder.audio_length_2 = (TextView) convertView
                    .findViewById(R.id.detail_voice_second_2);

            viewHolder.detail_voice_master = (RelativeLayout) convertView
                    .findViewById(R.id.detail_voice_master);
            viewHolder.detail_voice_second_m = (TextView) convertView
                    .findViewById(R.id.detail_voice_second_m);
            viewHolder.detail_comefrom = (TextView) convertView
                    .findViewById(R.id.comment_comefrom);
            viewHolder.layout_come_from = (LinearLayout) convertView
                    .findViewById(R.id.layout_come_from);
            viewHolder.detail_voice_animator_m = (ImageView) convertView
                    .findViewById(R.id.detail_voice_animator_m);

            viewHolder.remen_tips = (TextView) convertView
                    .findViewById(R.id.remen_tips);
            viewHolder.latest_tips = (TextView) convertView
                    .findViewById(R.id.latest_tips);
            viewHolder.tip_host = (TextView) convertView
                    .findViewById(R.id.tip_host);
            viewHolder.tip_sub_host = (TextView) convertView
                    .findViewById(R.id.tip_sub_host);
            viewHolder.tip_sub_host.setVisibility(View.GONE);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        fontSize = SYSharedPreferences.getInstance().loadResFont(context);
        viewHolder.reply_content_1.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        viewHolder.reply_content_2.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        viewHolder.content.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        if (position == getCount() - 1) {
            convertView.setPadding(0, 0, 0, DeviceUtil.dip2px(context, 10));
        } else {
            convertView.setPadding(0, 0, 0, 0);
        }
        setViewData(viewHolder, position);
        m_valueToKey.put(convertView.toString(), convertView);
        return convertView;
    }


    private void setViewData(final ViewHolder viewHolder, final int position) {
        // 设置第一条不显示分割线
//		if (holder.reply_content_1 != null)
//			holder.reply_content_1.setTextSize(TypedValue.COMPLEX_UNIT_SP,
//					fontSize);
//		if (holder.reply_content_2 != null)
//			holder.reply_content_2.setTextSize(TypedValue.COMPLEX_UNIT_SP,
//					fontSize);

        if (position == 0) {
            viewHolder.mTopLine.setVisibility(View.GONE);
        } else {
            viewHolder.mTopLine.setVisibility(View.VISIBLE);
        }
        // 设置热门和最新提示语的显示
        if (!postsListHot.isEmpty() && position == 0) {
            viewHolder.remen_tips.setVisibility(View.VISIBLE);
        } else {
            viewHolder.remen_tips.setVisibility(View.GONE);
        }
        if (position == postsListHot.size()) {
            viewHolder.latest_tips.setVisibility(View.VISIBLE);
        } else {
            viewHolder.latest_tips.setVisibility(View.GONE);
        }

        //捕捉一下异常
        try {
            uid = Long.valueOf(SYUserManager.getInstance().getUserId());
        } catch (Exception e) {
            uid = 0;
        }
        final CommentsForCircleAndNews posts = postsList.get(position);
        CommentsForCircleAndNews news = posts;
        viewHolder.mData = news;
        if (posts != null) {
            // 设置昵称，头像和赞的显示
            if (posts.getIs_anonymity() == 1 && TextUtils.isEmpty(posts.getImage_url())) {
                viewHolder.head_photo.setImageResource(R.drawable.common_comment_anonymous);
            } else {
                PhotoUtils.showCard(PhotoUtils.UriType.HTTP, posts.getImage_url(), viewHolder.head_photo, options);
            }
            viewHolder.name.setText(posts.getNickname());
            viewHolder.time.setText(StringUtils.convertDate(posts
                    .getCreate_time()));
            viewHolder.tv_ding_count.setText("0".equals(posts.getGood_num())
                    || TextUtils.isEmpty(posts.getGood_num()) ? "赞" : posts
                    .getGood_num());

            if (posts.isHas_praised()) {
                viewHolder.img.setImageDrawable(context.getResources()
                        .getDrawable(
                                R.drawable.cricle_list_item_good_press_icon));
                viewHolder.ding_layout.setEnabled(false);
                viewHolder.tv_add_one.clearAnimation();

            } else {
                viewHolder.img.setImageDrawable(context.getResources()
                        .getDrawable(R.drawable.cricle_list_item_good_icon));
                viewHolder.ding_layout.setEnabled(true);
                viewHolder.tv_add_one.clearAnimation();
            }
            // 赞的操作
            viewHolder.ding_layout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!CMainHttp.getInstance().isNetworkAvailable(context)) {
                        UIHelper.ToastMessage(context,
                                R.string.cricle_manage_networkerror);
                        return;
                    }
                    if (isUping) {
                        return;
                    }
                    isUping = true;
                    blogID = posts.getComment_id();
                    AddCommentUpReq req = new AddCommentUpReq(CDetailHttp.HTTP_COMMMENT_UP, ListViewPostsAdapterNew.this);
                    req.setParams(keyWord, srp_id, mUrl, SYUserManager.getInstance()
                                    .getToken(),
                            DetailActivity.DEVICE_COME_FROM,
                            DetailActivity.UP_TYPE_SUB, posts.getComment_id(),
                            main_title, main_images, main_decsription,
                            main_date, main_source, 0, posts);
                    CMainHttp.getInstance().doRequest(req);

                    tmpPosts = posts;
                }
            });
            // 更多回复的操作
            viewHolder.layout_reply_more
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 跳转到回复页面
                            if (posts != null) {
                                boolean isAdmin = role == Constant.ROLE_ADMIN;
                                UIHelper.showCommentNewPage(context, posts,
                                        interest_id, nickName, image,
                                        mblog_userId,
                                        DetailActivity.is_bantalk, operType,
                                        srp_id, keyWord, mUrl, isAdmin,
                                        mCircleType, main_title, main_name, main_date, 0, false);//从这里跳到回复页，点击回复页的查看正文，只是结束回复页，用不到blog_id
                            }
                        }
                    });
            // 更多按钮的操作 TODO
            viewHolder.imgMore.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    circleFollowMoreOperation(posts, v);
                }
            });
            // 点击评论头像的操作
            viewHolder.head_photo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //（游客）未登录不能进入个人中心
                    if (posts.getUser_id() >= 1000000000) {
                        return;
                    }
                    //如果是匿名的话，就不让进入个人中心
                    if (posts.getIs_anonymity() == 1) {
                        return;
                    }
                    if (posts.getType() - 1 == DetailActivity.DETAIL_TYPE_NEWS
                            || posts.getType() - 1 == DetailActivity.DETAIL_TYPE_RSS) {
                        PersonPageParam param = new PersonPageParam();
                        param.setViewerUid(posts.getUser_id());
                        param.setFrom(PersonPageParam.FROM_OTHER);
                        UIHelper.showPersonPage(context, param);
                    } else {
                        PersonPageParam param = new PersonPageParam();
                        param.setSrp_id(srp_id);
                        param.setInterest_id(interest_id);
                        param.setFrom(PersonPageParam.FROM_INTEREST);
                        param.setViewerUid(posts.getUser_id());
                        param.setCircleName(keyWord);
                        param.setComment_id(posts.getComment_id());
                        UIHelper.showPersonPage(context, param);
                    }

                }
            });

            // 评论的语音处理
            if (posts.getVoice() != null && !posts.getVoice().equals("")) {
                viewHolder.detail_voice_master.setVisibility(View.VISIBLE);
                viewHolder.content.setVisibility(View.GONE);
                viewHolder.layout_image_all.setVisibility(View.GONE);
                viewHolder.detail_voice_second_m.setText(posts
                        .getVoice_length() + "\"");
            } else {
                viewHolder.detail_voice_master.setVisibility(View.GONE);
                viewHolder.content.setVisibility(View.VISIBLE);

                viewHolder.layout_image_all.setVisibility(View.VISIBLE);
            }

            // 评论图片处理
            final List<String> images = posts.getImages();
            int size = images == null ? 0 : images.size();
            viewHolder.layout_image_all.setVisibility(size > 0 ? View.VISIBLE
                    : View.GONE);
            viewHolder.layout_image_1.setVisibility(size > 0 ? View.VISIBLE
                    : View.GONE);
            viewHolder.layout_image_2.setVisibility(size > 3 ? View.VISIBLE
                    : View.GONE);
            viewHolder.layout_image_3.setVisibility(size > 6 ? View.VISIBLE
                    : View.GONE);
            for (int i = 0; i < 9; i++) {
                // viewHolder.imgArr[i].setVisibility(View.GONE);
                if (size > i) {
                    ImageView imageView = viewHolder.imgArr[i];
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setBackgroundResource(R.drawable.default_small);
                    // String imgUrl = images.get(i).replace("!ios",
                    // "").replace("!android", "");
                    String imgUrl = images.get(i);
                    if (!TextUtils.isEmpty(imgUrl)) {
                        // aquery.id(imageView).image(AppRestClient.getImageUrl(imgUrl),
                        // true, true, 0, 0, null, AQuery.FADE_IN);
//						this.imgloader
//								.displayImage(StringUtils.UpaiYun(imgUrl),
//										imageView, options);
                        PhotoUtils.showCard(PhotoUtils.UriType.HTTP, StringUtils.UpaiYun(imgUrl), imageView, MyDisplayImageOption.smalloptions);
                    }
                    final int pos = i;
                    viewHolder.imgArr[i]
                            .setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent();
                                    intent.setClass(context,
                                            TouchGalleryActivity.class);
                                    TouchGallerySerializable tg = new TouchGallerySerializable();
                                    tg.setItems(images);
                                    tg.setClickIndex(pos);
                                    Bundle extras = new Bundle();
                                    extras.putSerializable("touchGalleryItems",
                                            tg);
                                    intent.putExtras(extras);
                                    context.startActivity(intent);
                                }
                            });
                } else {
                    viewHolder.imgArr[i].setVisibility(View.INVISIBLE);
                }
            }
            // 评论内容处理
            if (StringUtils.isNotEmpty(posts.getContent())) {
                viewHolder.content.setText(EmojiPattern.getInstace()
                        .getExpressionString(context, posts.getContent()));
                viewHolder.content.setVisibility(View.VISIBLE);
            } else {
                viewHolder.content.setVisibility(View.GONE);
            }
            // 评论回复处理
            List<Reply> replyList = posts.getReplyList();
            if (replyList == null || replyList.size() == 0) {
                viewHolder.layout_reply.setVisibility(View.GONE);
                viewHolder.layout_reply_2.setVisibility(View.GONE);
                viewHolder.layout_reply_more.setVisibility(View.GONE);
            } else {
                viewHolder.layout_reply.setVisibility(View.VISIBLE);
                viewHolder.layout_reply_1.setVisibility(View.VISIBLE);
                if (replyList.size() > 1) {
                    viewHolder.layout_reply_2.setVisibility(View.VISIBLE);
                    if (replyList.size() > 2) {
                        viewHolder.layout_reply_more
                                .setVisibility(View.VISIBLE);
//						// 保证只显示两条
//						replyList.remove(replyList.size() - 1);
                    } else {
                        viewHolder.layout_reply_more.setVisibility(View.GONE);
                    }
                } else {
                    viewHolder.layout_reply_more.setVisibility(View.GONE);
                    viewHolder.layout_reply_2.setVisibility(View.GONE);
                }

                final Reply first = replyList.get(0);
                String cont = first.getContent() == null ? "" : first
                        .getContent();
                boolean is_host = false;
                TextViewUtil.setTextWithHostAndTime(context,
                        viewHolder.reply_content_1, first.getNickname(), cont,
                        StringUtils.convertDate(first.getReply_time()));
                //
                viewHolder.reply_time_1.setText(StringUtils.convertDate(first
                        .getReply_time()));
                int visible = View.GONE;
                if (uid == first.getUser_id()) {
                    visible = View.VISIBLE;
                } else if (role == Constant.ROLE_ADMIN) {
                    if (first.getIs_current_reply() == 1) {// 如果是当前词或者圈子才能删
                        visible = View.VISIBLE;
                    }
                }
                viewHolder.reply_delete_1.setVisibility(visible);
                viewHolder.reply_delete_1
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                update_posts_id = posts.getBlog_id();
                                deleteReply(first);
                            }
                        });
                if (StringUtils.isNotEmpty(first.getVoice())) {
                    TextViewUtil.setTextWithHost(context,
                            viewHolder.reply_content_1, first.getNickname(),
                            cont, is_host);
                    viewHolder.layout_reply_voice_1.setVisibility(View.VISIBLE);
                    viewHolder.audio_length_1.setText(first.getVoice_length()
                            + "\"");
                    viewHolder.layout_reply_voice_1
                            .setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    audio.play(viewHolder.list_audio_play_1,
                                            SYMediaplayer.SOURCE_TYPE_NET,
                                            first.getVoice());
                                }
                            });
                } else {
                    viewHolder.layout_reply_voice_1.setVisibility(View.GONE);
                }

                if (replyList.size() > 1) {
                    final Reply second = replyList.get(1);
                    cont = second.getContent() == null ? "" : second
                            .getContent();
                    is_host = second.getIs_host() == 1;
                    TextViewUtil.setTextWithHostAndTime(context,
                            viewHolder.reply_content_2, second.getNickname(),
                            cont,
                            StringUtils.convertDate(second.getReply_time()));
                    //
                    viewHolder.reply_time_2.setText(StringUtils
                            .convertDate(second.getReply_time()));
                    int visible2 = View.GONE;
                    if (uid == second.getUser_id()) {
                        visible2 = View.VISIBLE;
                    } else if (role == Constant.ROLE_ADMIN) {
                        if (second.getIs_current_reply() == 1) {// 如果是当前词或者圈子才能删
                            visible2 = View.VISIBLE;
                        }
                    }
                    viewHolder.reply_delete_2.setVisibility(visible2);
                    viewHolder.reply_delete_2
                            .setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    update_posts_id = posts.getBlog_id();
                                    deleteReply(second);
                                }
                            });
                    if (StringUtils.isNotEmpty(second.getVoice())) {
                        TextViewUtil.setTextWithHost(context,
                                viewHolder.reply_content_2,
                                second.getNickname(), cont, is_host);
                        viewHolder.layout_reply_voice_2
                                .setVisibility(View.VISIBLE);
                        viewHolder.audio_length_2.setText(second
                                .getVoice_length() + "\"");
                        viewHolder.layout_reply_voice_2
                                .setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        audio.play(
                                                viewHolder.list_audio_play_2,
                                                SYMediaplayer.SOURCE_TYPE_NET,
                                                second.getVoice());

                                    }
                                });
                    } else {
                        viewHolder.layout_reply_voice_2
                                .setVisibility(View.GONE);
                    }

                }

            }
            String srp_word = posts.getSrp_word();
            if (srp_word == null || srp_word.equals("")
                    || posts.getIs_current_comment() == 1) {
                viewHolder.layout_come_from.setVisibility(View.GONE);
            } else {
                viewHolder.layout_come_from.setVisibility(View.VISIBLE);
                viewHolder.detail_comefrom.setText(posts.getSrp_word());
                viewHolder.detail_comefrom
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (posts.getType() - 1 == DetailActivity.DETAIL_TYPE_NEWS) {
                                    IntentUtil.gotoSouYueSRP(context,
                                            posts.getSrp_word(),
                                            posts.getSrp_id(),
                                            "",
                                            OPSOURCE_FROM_COMMENT);
                                } else {
                                    showCircleIndex(context, posts.getSrp_id(),
                                            posts.getSrp_word(),
                                            posts.getSrp_word(), "", "", "",
                                            0x3);
                                }
                            }
                        });
            }

            viewHolder.detail_voice_master
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            audio.play(viewHolder.detail_voice_animator_m,
                                    SYMediaplayer.SOURCE_TYPE_NET,
                                    posts.getVoice());
                        }
                    });
//            if (posts.getRole() == Constant.ROLE_ADMIN && posts.getIs_anonymity() != 1) {
////                viewHolder.tip_sub_host.setVisibility(View.GONE);
//                viewHolder.tip_host.setHint("圈主");
//                Drawable drawable= context.getResources().getDrawable(R.drawable.img_host);
//                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                viewHolder.tip_host.setCompoundDrawables(drawable,null,null,null);
//            } else if (posts.getRole() == Constant.ROLE_SUB_ADMIN && posts.getIs_anonymity() != 1){
////                viewHolder.tip_sub_host.setVisibility(View.VISIBLE);
//                viewHolder.tip_host.setHint("副圈主");
//                Drawable drawable= context.getResources().getDrawable(R.drawable.img_sub_host);
//                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                viewHolder.tip_host.setCompoundDrawables(drawable,null,null,null);
//            }else{
////                viewHolder.tip_host.setVisibility(View.GONE);
//                viewHolder.tip_host.setHint("");
//                viewHolder.tip_host.setCompoundDrawables(new ColorDrawable(Color.TRANSPARENT),null,null,null);
//            }


            if (posts.getIs_anonymity() == 1) { // 匿名状态
                viewHolder.tip_host.setHint("");
                viewHolder.tip_host.setCompoundDrawables(new ColorDrawable(Color.TRANSPARENT), null, null, null);
            } else {
                if (posts.getIs_private() == 1) { // 隐私保护状态
                    viewHolder.tip_host.setHint("");
                    viewHolder.tip_host.setCompoundDrawables(new ColorDrawable(Color.TRANSPARENT), null, null, null);
                } else {
                    //没有匿名，没有隐私保护
                    if (posts.getRole() == Constant.ROLE_ADMIN) { //圈主
                        viewHolder.tip_host.setHint("圈主");
                        Drawable drawable = context.getResources().getDrawable(R.drawable.img_host);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        viewHolder.tip_host.setCompoundDrawables(drawable, null, null, null);
                    } else if (posts.getRole() == Constant.ROLE_SUB_ADMIN) { // 副圈主
                        viewHolder.tip_host.setHint("副圈主");
                        Drawable drawable = context.getResources().getDrawable(R.drawable.img_sub_host);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        viewHolder.tip_host.setCompoundDrawables(drawable, null, null, null);
                    } else {
                        viewHolder.tip_host.setHint("");
                        viewHolder.tip_host.setCompoundDrawables(new ColorDrawable(Color.TRANSPARENT), null, null, null);
                    }
                }
            }
        }
    }

    public static void showCircleIndex(Activity context, String srp_id,
                                       String keyword, String interest_name, String interest_logo,
                                       String _title, String _md5, int requestcode) {
        Intent intent = new Intent(context, CircleIndexActivity.class);
        intent.putExtra("srp_id", srp_id);
        intent.putExtra("keyword", keyword);
        intent.putExtra("interest_name", interest_name);
        intent.putExtra("interest_logo", interest_logo);
        intent.putExtra("title", _title);
        intent.putExtra("md5", _md5);
        context.startActivityForResult(intent, requestcode);
        context.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public void setRole(int role) {
        this.role = role;
    }

    TextView tv_add_one;

    private void doAnimation() {
        if (tv_add_one != null) {
            try {
                tv_add_one.clearAnimation();
            } catch (Exception e) {

            }
        }
        Collection<View> views = m_valueToKey.values();
        for (View vs : views) {
            ViewHolder hold = (ViewHolder) vs.getTag();
            if (hold.mData.getComment_id() == blogID) {
                tv_add_one = hold.tv_add_one;
                hold.tv_add_one.setVisibility(View.VISIBLE);
                hold.tv_add_one.startAnimation(animation);
                hold.tv_add_one.setVisibility(View.INVISIBLE);
                if (!TextUtils.isEmpty(hold.tv_ding_count.getText().toString())) {
                    if (hold.tv_ding_count.getText().equals("赞")) {
                        hold.tv_ding_count.setText(1 + "");
                    } else {
                        hold.tv_ding_count.setText(Integer.parseInt(tmpPosts
                                .getGood_num()) + 1 + "");
                    }
                } else {
                    hold.tv_ding_count.setText(1 + "");
                }
                hold.img.setImageDrawable(context.getResources().getDrawable(
                        R.drawable.cricle_list_item_good_press_icon));
                hold.ding_layout.setEnabled(false);
            }
        }

    }

    private long del_posts_id;

    private void deletePosts() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确定删除吗？")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        // 确定删除的时候
                        dialog.dismiss();
                        // 2代表是删除回复,1代表是删除评论
//                        http.deleteCommentNew(SYUserManager.getInstance()
//                                        .getToken(), del_posts_id, operType, srp_id,
//                                keyWord, 1);
                        MblogDelete2Req req = new MblogDelete2Req(HttpCommon.DETAIL_BLOG_DELETE_ID, ListViewPostsAdapterNew.this);
                        req.setParams(SYUserManager.getInstance()
                                        .getToken(), del_posts_id, operType, srp_id,
                                keyWord, 1);
                        CMainHttp.getInstance().doRequest(req);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 取消删除的时候
                        dialog.dismiss();
                    }
                }).create().show();
    }

    public void deleteCommentNewSuccess(HttpJsonResponse res) {
        int statusCode = res.getCode();
        if (statusCode != 200) {
            return;
        }
        List<CommentsForCircleAndNews> list = new ArrayList<CommentsForCircleAndNews>();
        for (CommentsForCircleAndNews posts : postsList) {
            if (posts.getComment_id() == del_posts_id) {
                list.add(posts);
            }
        }

        for (CommentsForCircleAndNews posts : postsListHot) {
            if (posts.getComment_id() == del_posts_id) {
                postsListHot.remove(posts);
                break;
            }
        }

        for (CommentsForCircleAndNews posts : list) {
            postsList.remove(posts);
        }
        if (listener != null) {
            listener.onChange(null);
        }
        UIHelper.ToastMessage(context, "删除成功");
        notifyDataSetChanged();
    }

    private void deleteReply(final Reply reply) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("删除提示").setMessage("您确定删除该回复吗？")
                .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removingReply = reply;
//                        http.deleteComment(reply.getReply_id() + "",
//                                SYUserManager.getInstance().getToken());
                        CircleDelReplyRequest.send(HttpCommon.CIRCLE_DELREPLY_REQUESTID, ListViewPostsAdapterNew.this, reply.getReply_id() + "",
                                SYUserManager.getInstance().getToken());
                    }
                }).setNegativeButton("取消", null);
        builder.create().show();
    }


    public void deleteCommentSuccess(HttpJsonResponse res) {
        if (null != audio) {
            audio.stopPlayAudio();
        }
        UIHelper.ToastMessage(context, "删除回复成功");
        for (CommentsForCircleAndNews posts : postsList) {
            if (posts.getBlog_id() == update_posts_id) {
                long del_id = removingReply.getReply_id();
                List<Reply> replyList = posts.getReplyList();
                for (Reply r : replyList) {
                    if (del_id == r.getReply_id()) {
                        replyList.remove(r);
                        notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
        removingReply = null;
    }

    public SYMediaplayer_Mine getAudio() {
        return audio;
    }

    static class ViewHolder {
        View mTopLine;
        ImageView head_photo;
        TextView name;
        TextView time;
        TextView content;
        TextView edit;
        TextView delete;
        TextView tv_ding_count;
        TextView reply;

        LinearLayout layout_image_all;
        LinearLayout layout_image_1;
        LinearLayout layout_image_2;
        LinearLayout layout_image_3;
        ImageView[] imgArr;

        LinearLayout ding_layout;
        ImageView img;
        TextView tv_add_one;
        ImageView imgMore;

        LinearLayout layout_reply;
        LinearLayout layout_reply_1;
        LinearLayout layout_reply_2;
        LinearLayout layout_reply_more;
        TextView reply_content_1;
        TextView reply_content_2;

        LinearLayout layout_reply_voice_1;
        LinearLayout layout_reply_voice_2;
        ImageView list_audio_play_1;
        ImageView list_audio_play_2;
        TextView audio_length_1;
        TextView audio_length_2;

        TextView reply_time_1;
        TextView reply_time_2;
        ImageView reply_delete_1;
        ImageView reply_delete_2;

        RelativeLayout detail_voice_master;
        LinearLayout layout_come_from;
        TextView detail_voice_second_m;
        TextView detail_comefrom;
        ImageView detail_voice_animator_m;

        TextView remen_tips;
        TextView latest_tips;
        TextView tip_sub_host;
        TextView tip_host;
        CommentsForCircleAndNews mData;

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSrp_id() {
        return srp_id;
    }

    public void setSrp_id(String srp_id) {
        this.srp_id = srp_id;
    }

    private void circleFollowMoreOperation(
            final CommentsForCircleAndNews posts, View v) {
        ll_remen.setVisibility(View.GONE);
        int visible = View.GONE;
        //是圈主并且是帖子
        if (role == Constant.ROLE_ADMIN && mCircleType == DetailActivity.CIRCLE_TYPE_CIRCLE) {
            if (posts.getIs_current_comment() == 1) {// 如果是当前词或者圈子才能删除和设为热门评论
                visible = View.VISIBLE;
                ll_remen.setVisibility(visible);
            }
        }
        if (uid == posts.getUser_id()) {   //只要是自己产生的评论都可以删除
            visible = View.VISIBLE;
        }
        ll_delete.setVisibility(visible);
        // 设置是设为、取消热门评论
        if (posts.getIshot() == 1) {
            tv_hot.setText("取消热门");
        } else {
            tv_hot.setText("设为热门");
        }
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        popupWindow.getContentView().measure(w, h);
        int width = popupWindow.getContentView().getMeasuredWidth();
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
                (CircleUtils.getDeviceWidth(context) - width) / 2,
                location[1] - context.getResources().getDimensionPixelOffset(R.dimen.space_25));
        ll_delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                del_posts_id = posts.getComment_id();
                deletePosts();
                popupWindow.dismiss();
            }
        });
        ll_huifu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 跳转到回复页面
                if (posts != null) {
                    boolean isAdmin = role == Constant.ROLE_ADMIN;
                    UIHelper.showCommentNewPage(context, posts, interest_id,
                            nickName, image, mblog_userId,
                            DetailActivity.is_bantalk, operType, srp_id,
                            keyWord, mUrl, isAdmin, mCircleType,
                            main_title, main_name, main_date, 0, false);//从这里跳到回复页，点击回复页的查看正文，只是结束回复页，用不到blog_id
                }
                popupWindow.dismiss();
            }
        });

        ll_remen.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!CMainHttp.getInstance().isNetworkAvailable(context)) {
                    UIHelper.ToastMessage(context,
                            R.string.cricle_manage_networkerror);
                    return;
                }
                blogID = posts.getComment_id();
                tmpPosts = posts;
                int status = posts.getIshot() == 0 ? 1 : 0;
                CommentSetHotRequest request = new CommentSetHotRequest(HttpCommon.DETAIL_COMMENT_SET_HOT_ID, ListViewPostsAdapterNew.this);
                request.setParams(mUrl,
                        posts.getComment_id(), status,
                        DetailActivity.DEVICE_COME_FROM);
                CMainHttp.getInstance().doRequest(request);
                popupWindow.dismiss();
            }
        });
        ll_copy.setOnClickListener(new OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (android.provider.Settings.System.getInt(
                        context.getContentResolver(),
                        android.provider.Settings.System.SYS_PROP_SETTING_VERSION,
                        3) < 11) {
                    android.text.ClipboardManager cmb = (ClipboardManager) context
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText(posts.getContent());
                } else {
                    ClipboardManager cmb = (ClipboardManager) context
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText(posts.getContent());
                }
                popupWindow.dismiss();
            }
        });
    }

    @Override
    public void onHttpResponse(IRequest volleyRequest) {
        int mId = volleyRequest.getmId();
        switch (mId) {
            case HttpCommon.DETAIL_COMMENT_SET_HOT_ID:
                if (tmpPosts.getIshot() == 0) {
                    SouYueToast.makeText(context, "设为热门评论成功", Toast.LENGTH_SHORT)
                            .show();
                    CommentsForCircleAndNews post = tmpPosts.clone();
                    postsList.add(0, post);
                    postsListHot.add(0, post);
                    for (CommentsForCircleAndNews posts : postsList) {
                        if (posts.getComment_id() == tmpPosts.getComment_id()) {
                            posts.setIshot(1);
                        }
                    }
                    notifyDataSetChanged();
                } else {
                    // 在热门评论里面找到相应的数据，有就在热门里面移除，没有就不做操作
                    for (CommentsForCircleAndNews posts : postsListHot) {
                        if (posts.getComment_id() == tmpPosts.getComment_id()) {
                            postsListHot.remove(posts);
                            postsList.remove(posts);
                            break;
                        }
                    }
                    ;
                    // 保证热门评论和最新评论里面的这条数据都更新
                    for (CommentsForCircleAndNews posts : postsList) {
                        if (posts.getComment_id() == tmpPosts.getComment_id()) {
                            posts.setIshot(0);
                        }
                    }
                    ;
                    SouYueToast.makeText(context, "取消热门评论成功", Toast.LENGTH_SHORT)
                            .show();
                    notifyDataSetChanged();
                }
                break;
            case CDetailHttp.HTTP_COMMMENT_UP:
                isUping = false;
                for (int i = 0; i < postsList.size(); i++) {
                    CommentsForCircleAndNews comments = postsList.get(i);
                    if (blogID == comments.getComment_id()) {
                        comments.setHas_praised(true);
//                        String good_num = comments.getGood_num();
//                        int num = Integer.valueOf(good_num) + 1;
//                        comments.setGood_num(num + "");
                    }
                }
                doAnimation();
                break;
            case HttpCommon.CIRCLE_DELREPLY_REQUESTID:
                deleteCommentSuccess(volleyRequest.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.DETAIL_BLOG_DELETE_ID:
                deleteCommentNewSuccess(volleyRequest.<HttpJsonResponse>getResponse());
                break;
        }
    }

    @Override
    public void onHttpError(IRequest _request) {
        if (_request.getmId() == HttpCommon.DETAIL_COMMENT_SET_HOT_ID) {
            IHttpError error = _request.getVolleyError();
//            HttpJsonResponse json = (HttpJsonResponse) _request.getResponse();
//            if (json.getCode()>=700){
//                return;
//            }
            if (error instanceof CSouyueHttpError) {
                HttpJsonResponse json = ((CSouyueHttpError) error).getJson();
                if (json != null && json.getCode() >= 700) {
                    return;
                }
            }
            if (tmpPosts.getIshot() == 0) {
                SouYueToast.makeText(context, "设为热门评论失败", Toast.LENGTH_SHORT)
                        .show();
            } else {
                SouYueToast.makeText(context, "取消热门评论失败", Toast.LENGTH_SHORT)
                        .show();
            }

        }
        switch (_request.getmId()) {
            case HttpCommon.CIRCLE_DELREPLY_REQUESTID:
                SouYueToast.makeText(context, "删除失败", Toast.LENGTH_SHORT)
                        .show();
                break;
            case CDetailHttp.HTTP_COMMMENT_UP:
                isUping = false;
                break;
        }
    }

    @Override
    public void onHttpStart(IRequest _request) {

    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setMain_date(String main_date) {
        this.main_date = main_date;
    }

    public void setMain_decsription(String main_decsription) {
        this.main_decsription = main_decsription;
    }

    public void setMain_images(String main_images) {
        this.main_images = main_images;
    }

    public void setMain_source(String main_source) {
        this.main_source = main_source;
    }

    public void setMain_title(String main_title) {
        this.main_title = main_title;
    }

    public void setMain_name(String main_name) {
        this.main_name = main_name;
    }
}
