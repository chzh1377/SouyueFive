package com.zhongsou.souyue.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.CommentaryActivity;
import com.zhongsou.souyue.activity.LoadingDataListener;
import com.zhongsou.souyue.circle.model.PersonPageParam;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.module.Comment;
import com.zhongsou.souyue.module.PopActionItem;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.srp.SrpReportReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.pop.PopActionBar;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.MyImageLoader;
import com.zhongsou.souyue.utils.SYMediaplayer;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CommentaryAdapter extends BaseAdapter implements OnClickListener,IVolleyResponse {
    private Context mContext;
    private SYMediaplayer audio;
    private List<Comment> data = new ArrayList<Comment>();
    private PopActionItem /* action_copy, */action_reply, action_report;
    private PopActionBar actionBar;
//    private AQuery aq;
    public boolean hasMoreItems = true;
    private View getMore, waiting;
    public boolean isMetNetworkError;
    private LoadingDataListener loadListener;
    private CMainHttp mMainHttp;

    public void setLoadingDataListener(LoadingDataListener loadListener) {
        this.loadListener = loadListener;
    }



    public interface ReplyListener {
        public void reply(Comment to);
    }

    ;

    private ReplyListener replyListener;

    public void setReplyListener(ReplyListener replyListener) {
        this.replyListener = replyListener;
    }

    public CommentaryAdapter(Context context) {
        this.mContext = context;
        audio = SYMediaplayer.getInstance(context);
//        aq = new AQuery(mContext);
//        http = new Http(this);
        mMainHttp = CMainHttp.getInstance();
        initQuickBar();
    }

    public void addDatas(List<Comment> data) {
        this.data.addAll(data);
    }

    public int getDataSize() {
        return data.size();
    }

    public void addData(Comment list) {
        this.data.add(list);
    }

    public void insertData(Comment comment) {
        this.data.add(0, comment);
    }

    public void seatData(List<Comment> data) {
        this.data = data;
    }

    public void clearDatas() {
        if (this.data != null) {
            this.data.clear();
        }
    }

    @Override
    public int getCount() {
        if (hasMoreItems && data.size() > 0)
            return data.size() + 1;
        else
            return data.size();
    }

    @Override
    public Comment getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static final int ITEM_GET_MORE = 0;
    public static final int ITEM_WAITING = 1;
    public static final int ITEM = 2;

    @Override
    public int getItemViewType(int position) {

        int len = this.data.size();
        if (position < len)
            return ITEM;
        else {
            if ((position == len + 1) && hasMoreItems) {
                if (isMetNetworkError)
                    return ITEM_GET_MORE;
                else
                    return ITEM_WAITING;
            } else
                return super.getItemViewType(position);
        }
    }

    @Override
    public int getViewTypeCount() {
        return ITEM + 1;
    }

    protected View genItemView(int position) {
        View v = inflaterVeiw(R.layout.commentary_list_item);

        ViewHolder holder = new ViewHolder();
        holder.audio_play_layout = v.findViewById(R.id.audio_play_layout);
        holder.audio_lenth = (TextView) v.findViewById(R.id.audio_length);
        holder.imgBtn = (ImageButton) v.findViewById(R.id.list_audio_play);
        holder.imgBtn.setOnClickListener(this);
        holder.text = (TextView) v.findViewById(R.id.commentary_content);
//        holder.quickBar = (ImageButton) v.findViewById(R.id.quick_bar);
//        holder.quickBar.setOnClickListener(this);
        holder.commentary_user_head = (ImageView) v.findViewById(R.id.commentary_user_head);
        holder.commentary_user_head.setOnClickListener(this);
        holder.commentary_user_head_layout = (RelativeLayout) v.findViewById(R.id.commentary_user_head_layout);
        holder.commentary_user_head_layout.setOnClickListener(this);
        holder.nickname = (TextView) v.findViewById(R.id.nickname);
        holder.commentary_times = (TextView) v.findViewById(R.id.commentary_times);
        v.setTag(holder);

        return v;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (!isMetNetworkError)
            checkGetMore(position);
        if (position == this.data.size() && hasMoreItems)
            return getCurrentFooter(parent);
        if (convertView == null || (convertView == getCurrentFooter(parent)))
            convertView = genItemView(position);

        setViewData((ViewHolder) convertView.getTag(), data.get(position));
        return convertView;
    }

    protected View getCurrentFooter(ViewGroup parent) {
        if (isMetNetworkError) {
            if (getMore == null) {
                getMore = inflaterVeiw(R.layout.get_more);
                getMore.setFocusableInTouchMode(false);
                TextView m = (TextView) getMore.findViewById(R.id.get_more);
                m.setOnClickListener(this);
            }
            return getMore;
        } else {
            if (waiting == null)
                waiting = inflaterVeiw(R.layout.refresh_footer);
            waiting.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            waiting.setMinimumHeight(70);
            return waiting;
        }
    }

    protected void checkGetMore(int currentPositoin) {
        if ((hasMoreItems) && ((this.data.size() - currentPositoin) <= 1 && (this.data.size() - currentPositoin) > 0)) {
            if (loadListener != null)
                loadListener.loadDataMore(this.data.get(this.data.size() - 1).id(), "");
        }
    }

    private void setViewData(ViewHolder holder, Comment comment) {
        if (comment != null && holder != null) {
            String txt = comment.replyTo().user().name();
            holder.nickname.setText(comment.user().name());
//            holder.quickBar.setTag(comment);
            holder.commentary_user_head_layout.setTag(comment);
            holder.commentary_user_head.setTag(comment);
            if (comment.user().image() != null) ;
            MyImageLoader.imageLoader.displayImage(comment.user().image(), holder.commentary_user_head, MyImageLoader.options);
//				aq.id(holder.commentary_user_head).image(comment.user().image(), true, true);
            holder.commentary_times.setText(StringUtils.convertDate("" + comment.date()));
            holder.text.setVisibility(View.GONE);
            holder.audio_play_layout.setVisibility(View.GONE);
            if (comment.voice().length() > 0 && comment.voice().url() != null && !("").equals(comment.voice().url())) {
                holder.imgBtn.setTag(comment);
                holder.audio_lenth.setText(comment.voice().length() + "s");
                holder.audio_play_layout.setVisibility(View.VISIBLE);
            } else
                holder.audio_play_layout.setVisibility(View.GONE);
            if (txt != null && !"".equals(txt)) {
                holder.text.setVisibility(View.VISIBLE);
                String str = getString(R.string.reply_to);
                holder.text.setText(Html.fromHtml("<font color=#ff000000>" + str + "</font> " + "<font color=#2f9dd6>" + txt + "</font> " + "<font color=#ff000000>" + ":" + "</font> "));
            } else
                holder.text.setVisibility(View.GONE);
            if (comment.content() != null && comment.content().length() > 0) {
                if (txt != null && !"".equals(txt)) {
                    setCommentText(holder, txt, comment.content());
                } else {
                    setCommentText(holder, null, comment.content());
                }
            }
        }
    }

    private void setCommentText(ViewHolder holder, String replyTo, String content) {
        if (replyTo == null)
            holder.text.setText(content);
        else {
            String str = getString(R.string.reply_to);
            holder.text.setText(Html.fromHtml("<font color=#ff000000>" + str + "</font> " + "<font color=#2f9dd6>" + replyTo + "</font> " + "<font color=#ff000000>" + ":" + content + "</font>"));

        }
        holder.text.setVisibility(View.VISIBLE);
    }

    static class ViewHolder {
        TextView text, nickname, commentary_times, audio_lenth;
        ImageButton imgBtn;
        ImageView commentary_user_head;
        View audio_play_layout;
        RelativeLayout commentary_user_head_layout;
    }

    protected LayoutInflater viewInflater;
    private Comment currentComment;

    private View inflaterVeiw(int id) {
        return LayoutInflater.from(mContext).inflate(id, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.list_audio_play:
                audio.play((ImageButton) v, SYMediaplayer.SOURCE_TYPE_NET);
                break;
//            case R.id.quick_bar:
//                currentComment = (Comment) v.getTag();
//                onQuickBarClick(v);
            case R.id.commentary_user_head:
                currentComment = (Comment) v.getTag();
                User user =  currentComment.user();
                String userType = user.userType();
                if(StringUtils.isNotEmpty(userType) && !SYUserManager.USER_GUEST.equals(userType)) {
                    PersonPageParam param = new PersonPageParam();
                    param.setViewerUid(user.userId());
                    param.setFrom(PersonPageParam.FROM_OTHER);
                    UIHelper.showPersonPage((CommentaryActivity) mContext, param);
                }
                break;
            case R.id.get_more:
                isMetNetworkError = false;
                notifyDataSetChanged();
                break;
            case R.id.commentary_user_head_layout:
            	currentComment = (Comment) v.getTag();
            	/*User user =  currentComment.user();
    			String userType = user.userType();
    			if(StringUtils.isNotEmpty(userType) && !SYUserManager.USER_GUEST.equals(userType)) {
                	PersonPageParam param = new PersonPageParam();
                	param.setViewerUid(user.userId());
                	param.setFrom(PersonPageParam.FROM_OTHER);
                	UIHelper.showPersonPage((CommentaryActivity)mContext,param);
    			}*/
                onQuickBarClick(v);
            	break;
            default:
                break;
        }
    }

    private void onQuickBarClick(View v) {
        actionBar = new PopActionBar(v);
        actionBar.addActionItem(action_reply);
        actionBar.addActionItem(action_report);
        actionBar.setAnimStyle(PopActionBar.ANIM_AUTO);
        actionBar.show();

    }

    private void initQuickBar() {
        action_report = new PopActionItem();
        action_report.setTitle(getString(R.string.report));
        action_report.setIcon(mContext.getResources().getDrawable(R.drawable.pop_report_icon));
        action_report.setClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                actionBar.dismiss();
                String[] itemnames = mContext.getResources().getStringArray(R.array.report_names);
                final int[] itemtypes = mContext.getResources().getIntArray(R.array.report_types);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                String report = getString(R.string.report) + "“" + currentComment.user().name() + "”的评论";
                Dialog dialog = builder.setTitle(report).setItems(itemnames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SrpReportReq report = new SrpReportReq(HttpCommon.SRP_CHAT_ROOM_REQUEST,CommentaryAdapter.this);
                        report.setParams(currentComment.id(), itemtypes[which]);
                        report.setTag(mContext);
                        mMainHttp.doRequest(report);
//                        http.report(currentComment.id(), itemtypes[which]);
                    }
                }).create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });
        action_reply = new PopActionItem();
        action_reply.setTitle(getString(R.string.reply_));
        action_reply.setIcon(mContext.getResources().getDrawable(R.drawable.pop_reply_icon));
        action_reply.setClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                actionBar.dismiss();
                if (replyListener != null)
                    replyListener.reply(currentComment);
            }
        });
    }

//    public void reportSuccess(AjaxStatus as) {
//        SouYueToast.makeText(mContext, R.string.report_success, SouYueToast.LENGTH_SHORT).show();
//    }

    private String getString(int id) {
        return mContext.getResources().getString(id);
    }

    @Override
    public void onHttpResponse(IRequest request) {
        SouYueToast.makeText(mContext, R.string.report_success, SouYueToast.LENGTH_SHORT).show();
    }

    @Override
    public void onHttpError(IRequest request) {
        SouYueToast.makeText(mContext, R.string.networkerror, SouYueToast.LENGTH_SHORT).show();
    }

    @Override
    public void onHttpStart(IRequest request) {

    }
}
