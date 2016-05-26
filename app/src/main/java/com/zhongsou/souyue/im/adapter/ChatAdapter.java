package com.zhongsou.souyue.im.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tuita.sdk.im.db.module.AtFriend;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.GroupMembers;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.tuita.sdk.im.db.module.MessageHistory;
import com.tuita.sdk.im.db.module.MessageRecent;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.fragment.MsgTabFragment;
import com.zhongsou.souyue.im.module.MsgContent;
import com.zhongsou.souyue.im.search.SearchUtils;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.ImUtils;
import com.zhongsou.souyue.im.view.SwipeListView;
import com.zhongsou.souyue.module.SRP;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class ChatAdapter extends BaseAdapter {
    private Context mContext;
    private SwipeListView listView;
    private List<MessageRecent> data;
    private static final int ZEROCOUNT = 0;
    private static final int ONECOUNT = 1;
    private static final int MORECOUNT = 99;
    private Fragment fragment;
    private boolean deleted = false;
    private DisplayImageOptions options;

    public interface OnDeleteListener {
        void onDeleteItem(int position);
    }

    private OnDeleteListener deleteListener = new OnDeleteListener() {

        @Override
        public void onDeleteItem(int position) {
            if (listView instanceof SwipeListView) {
                notifyDataSetChanged();
                ((SwipeListView) listView).onChanged();
                ImserviceHelp.getInstance().db_delMessageRecent(data.get(position).getChat_id());
                ImserviceHelp.getInstance().db_clearMessageRecentBubble(data.get(position).getChat_id());
                SearchUtils.deleteSession(MainActivity.SEARCH_PATH_MEMORY_DIR, data.get(position).getMyid(), (short) data.get(position).getChat_type(), data.get(position).getChat_id());
                deleted = true;
                data.remove(data.get(position));
                notifyDataSetChanged();
                if (fragment instanceof MsgTabFragment) {
                    ((MsgTabFragment) fragment).exitSwitchPage(IntentUtil.isLogin());
                }

            }
        }
    };

    public interface OnTopListener {
        void onTopItem(int position);
    }

    private OnTopListener topListener = new OnTopListener() {

        @Override
        public void onTopItem(int position) {
            if (listView instanceof SwipeListView) {
                ((SwipeListView) listView).onChanged();
                String by3 = data.get(position).getBy3();
                if (!by3.equals("0")) {
                    ImserviceHelp.getInstance().db_ToTopMessageRecent(data.get(position).getChat_id(), "0");
                } else {
                    ImserviceHelp.getInstance().db_ToTopMessageRecent(data.get(position).getChat_id(), Long.toString(new Date().getTime()));
                }
                if (fragment instanceof MsgTabFragment) {
                    ((MsgTabFragment) fragment).exitSwitchPage(IntentUtil.isLogin());
                }

            }
        }
    };

    public void setData(List<MessageRecent> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public ChatAdapter(Context mContext, SwipeListView listView, Fragment fragment) {
        this.mContext = mContext;
        this.listView = listView;
        this.fragment = fragment;
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .displayer(new RoundedBitmapDisplayer(10))
                .showImageOnLoading(R.drawable.default_head).build();
    }

    @Override
    public int getCount() {
        if (data != null && data.size() > 0) {
            return data.size();
        } else {
            return 0;
        }

    }

    @Override
    public MessageRecent getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.swipe_chat_list_view_row, parent, false);
            holder.item_left = (RelativeLayout) convertView.findViewById(R.id.item_left);
            holder.item_right = (RelativeLayout) convertView.findViewById(R.id.item_right);
            holder.ivImage = (ImageView) convertView.findViewById(R.id.row_iv_image);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.row_tv_title);
            holder.bAction_delete = (Button) convertView.findViewById(R.id.row_btn_delete);
            holder.btnTop = (Button) convertView.findViewById(R.id.row_btn_top);
            holder.llTopView = (LinearLayout) convertView.findViewById(R.id.ll_top_view);

            holder.count_text = (TextView) convertView.findViewById(R.id.count_text);
            holder.no_count_text = (TextView) convertView.findViewById(R.id.no_count_text);
            holder.im_send_faild_icon = (ImageView) convertView.findViewById(R.id.im_send_faild_icon);
            holder.im_sending = (ProgressBar) convertView.findViewById(R.id.im_sending);
            holder.row_tv_time = (TextView) convertView.findViewById(R.id.row_tv_time);
            holder.row_tv_content = (TextView) convertView.findViewById(R.id.row_tv_content);
            holder.im_notify_icon = (ImageView) convertView.findViewById(R.id.im_notify_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MessageRecent messageRecent = (MessageRecent) getItem(position);

        LinearLayout.LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        holder.item_left.setLayoutParams(lp1);
        LinearLayout.LayoutParams lp2 = new LayoutParams(listView.getRightViewWidth(), LayoutParams.MATCH_PARENT);
        holder.item_right.setLayoutParams(lp2);

        /**
         * 加载MessageRecent数据
         *
         */

        //通过判断By3来设置左拉条目的显示
        if (messageRecent.getBy3() != null && !messageRecent.getBy3().equals("0")) {
            holder.llTopView.setVisibility(View.VISIBLE);
            holder.btnTop.setText("取消置顶");
        } else {
            holder.llTopView.setVisibility(View.GONE);
            holder.btnTop.setText("置顶消息");
        }

        holder.tvTitle.setText(messageRecent.getChatName());    //会话名字
        holder.row_tv_time.setText(StringUtils.convertDate(String.valueOf(messageRecent.getDate())));   //会话时间
        //会话头像
        if (holder.ivImage.getTag() == null || !holder.ivImage.getTag().toString().equals(messageRecent.getChatAvatar())) {
            ImageLoader.getInstance().displayImage(
                    messageRecent.getChatAvatar(),
                    holder.ivImage, options, imageLoadingListener
            );
        }
        if (messageRecent.getChatAvatar() == null) {
            holder.ivImage.setBackgroundResource(R.drawable.default_head);
        }

        holder.im_notify_icon.setVisibility(messageRecent.isNotify() ? View.INVISIBLE : View.VISIBLE); //会话消息打扰提醒右下小图标

        //状态显示
        if (messageRecent.getStatus() == IMessageConst.STATUS_SENT_FAIL || messageRecent.getStatus() == IMessageConst.STATUS_SENTING) {
            holder.im_sending.setVisibility(View.GONE);
            holder.im_send_faild_icon.setVisibility(View.VISIBLE);
        } else if (messageRecent.getStatus() == IMessageConst.STATUS_HAS_SENT) {
            holder.im_sending.setVisibility(View.GONE);
            holder.im_send_faild_icon.setVisibility(View.INVISIBLE);
        } else if (messageRecent.getStatus() == IMessageConst.STATE_HAS_READ) {
            holder.im_sending.setVisibility(View.GONE);
            holder.im_send_faild_icon.setVisibility(View.INVISIBLE);
        } else if (messageRecent.getStatus() == IMessageConst.STATE_RED_PACKET_READ) {      //红包已拆
            holder.im_sending.setVisibility(View.GONE);
            holder.im_send_faild_icon.setVisibility(View.INVISIBLE);
        } else {
            holder.im_sending.setVisibility(View.VISIBLE);
            holder.im_send_faild_icon.setVisibility(View.INVISIBLE);
        }

        //气泡显示
        if (messageRecent.getBubble_num() > ZEROCOUNT) {
            if (messageRecent.getJumpType() == MessageRecent.ITEM_JUMP_IMCHAT) {
                if (messageRecent.isNotify()) {
                    holder.count_text.setBackgroundResource(R.drawable.tool_atme_number);
                    holder.count_text.setText(ImUtils.getBubleText(String.valueOf(messageRecent.getBubble_num())));
                    holder.count_text.setVisibility(View.VISIBLE);
                    holder.no_count_text.setVisibility(View.GONE);
                } else {
                    holder.no_count_text.setVisibility(View.VISIBLE);
                    holder.count_text.setVisibility(View.INVISIBLE);
                }

            } else if (messageRecent.getJumpType() == MessageRecent.ITEM_JUMP_SERVICE_LIST) {
                holder.no_count_text.setVisibility(View.VISIBLE);
                holder.count_text.setVisibility(View.INVISIBLE);
            }
        } else {
            holder.count_text.setVisibility(View.INVISIBLE);
            holder.no_count_text.setVisibility(View.GONE);
        }

        //显示最近的聊天内容
        showRecentText(messageRecent, holder);

        holder.bAction_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteListener.onDeleteItem(position);
            }
        });

        holder.btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topListener.onTopItem(position);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout item_left;
        RelativeLayout item_right;

        LinearLayout llTopView;
        ImageView ivImage;
        TextView tvTitle, count_text, no_count_text;
        Button bAction_delete;
        Button btnTop;
        ImageView im_send_faild_icon;
        ProgressBar im_sending;
        TextView row_tv_time;
        TextView row_tv_content;
        ImageView im_notify_icon;
    }

    private void showText(MessageRecent messageRecent, TextView view) {
        String stringBuffer = "";
        if (messageRecent.getChat_type() == 1) {
            if (messageRecent.getSender() != 0) {
                Contact contact = ImserviceHelp.getInstance().db_getContactById(messageRecent.getSender());//查找是否是好友
                GroupMembers groupMembers = ImserviceHelp.getInstance().db_findMemberListByGroupidandUid(messageRecent.getChat_id(), messageRecent.getSender());//查找群成员
                if (contact == null) {
                    if (!TextUtils.isEmpty(messageRecent.getContent()) && groupMembers != null)
                        stringBuffer = (TextUtils.isEmpty(groupMembers.getMember_name()) ? groupMembers.getNick_name() : groupMembers.getMember_name()) + ":" + messageRecent.getContent();
                    else
                        stringBuffer = "";
                } else {
                    if (!TextUtils.isEmpty(messageRecent.getContent()) && groupMembers != null)
                        stringBuffer = (TextUtils.isEmpty(contact.getComment_name()) ? TextUtils.isEmpty(groupMembers.getMember_name()) ? groupMembers.getNick_name()
                                : groupMembers.getMember_name() : contact.getComment_name()) + ":" + messageRecent.getContent();
                    else
                        stringBuffer = "";
                }
            } else {
                stringBuffer = messageRecent.getContent();
            }

            if (messageRecent.getBubble_num() > ONECOUNT && !messageRecent.isNotify()) {
                String beforeText = stringBuffer;
                String bubbleNum = messageRecent.getBubble_num() > MORECOUNT ? mContext.getString(R.string.im_notify_text) : ImUtils.getBubleText(String.valueOf(messageRecent.getBubble_num()));
                stringBuffer = "[" + bubbleNum + "条]" + beforeText;
            }

            if (messageRecent.getBy1() != null && messageRecent.getBy1().equals("1")) {
                view.setText(Html.fromHtml("<font color='#AB0404'>[有人@你]</font>" + "<font>" + stringBuffer + "</font>"));
            } else {
                view.setText(stringBuffer);
            }

        } else {
            if (messageRecent.getBubble_num() > ONECOUNT && !messageRecent.isNotify()) {
                String bubbleNum = messageRecent.getBubble_num() > MORECOUNT ? mContext.getString(R.string.im_notify_text) : ImUtils.getBubleText(String.valueOf(messageRecent.getBubble_num()));
                messageRecent.setContent("[" + bubbleNum + "条]" + messageRecent.getContent());
            }
            view.setText(messageRecent.getContent());
        }

        if (messageRecent.getBy1() != null && messageRecent.getBy1().equals("1")) {
            view.setText(Html.fromHtml("<font color='#AB0404'>[有人@你]</font>" + "<font>" + stringBuffer + "</font>"));
        } else if (!TextUtils.isEmpty(messageRecent.getDrafttext())) {
            if (messageRecent.getBubble_num() > ONECOUNT && !messageRecent.isNotify()) {
                String bubbleNum = messageRecent.getBubble_num() > MORECOUNT ? mContext.getString(R.string.im_notify_text) : ImUtils.getBubleText(String.valueOf(messageRecent.getBubble_num()));
                view.setText(Html.fromHtml("<font color='#AB0404'>[草稿]</font>" + "<font>" + "[" + bubbleNum + "条]" + messageRecent.getDrafttext() + "</font>"));
            } else {
                view.setText(Html.fromHtml("<font color='#AB0404'>[草稿]</font>" + "<font>" + messageRecent.getDrafttext() + "</font>"));
            }
        }

        if (messageRecent.getBy1() != null && messageRecent.getBy1().equals("1") && !TextUtils.isEmpty(messageRecent.getDrafttext())) {
            view.setText(Html.fromHtml("<font color='#AB0404'>[有人@你]</font>" + "<font>" + "<font color='#AB0404'>[草稿]</font>" + "<font>" + stringBuffer + messageRecent.getDrafttext() + "</font>"));
        }

    }

    /**
     * 显示文件聊天
     *
     * @param messageRecent
     * @param view
     * @param string
     * @throws JSONException
     */
    private void showFileTips(MessageRecent messageRecent, TextView view, String string) throws JSONException {
        String stringBuffer = "";
        JSONObject obj = new JSONObject(messageRecent.getContent());
        if (messageRecent.getChat_type() == IConst.CHAT_TYPE_GROUP) {
            if (messageRecent.getSender() != 0) {
                Contact contact = ImserviceHelp.getInstance().db_getContactById(messageRecent.getSender());//查找是否是好友
                GroupMembers groupMembers = ImserviceHelp.getInstance().db_findMemberListByGroupidandUid(messageRecent.getChat_id(), messageRecent.getSender());//查找群成员
                if (contact == null) {
                    stringBuffer = (TextUtils.isEmpty(groupMembers.getMember_name()) ? groupMembers.getNick_name() : groupMembers.getMember_name()) + ":[" + string + "]" + obj.get("name");
                } else {
                    stringBuffer = (TextUtils.isEmpty(contact.getComment_name()) ? TextUtils.isEmpty(groupMembers.getMember_name()) ? groupMembers.getNick_name()
                            : groupMembers.getMember_name() : contact.getComment_name()) + ":[" + string + "]" + obj.get("name");
                }
            } else {
                stringBuffer = "[" + string + "]" + obj.get("name");
            }

            if (messageRecent.getBubble_num() > ONECOUNT && !messageRecent.isNotify()) {
                String beforeText = stringBuffer;
                String bubbleNum = messageRecent.getBubble_num() > MORECOUNT ? mContext.getString(R.string.im_notify_text) : ImUtils.getBubleText(String.valueOf(messageRecent.getBubble_num()));
                stringBuffer = "[" + bubbleNum + "条]" + beforeText;
            }

            if (messageRecent.getBy1() != null && messageRecent.getBy1().equals("1")) {
                view.setText(Html.fromHtml("<font color='#AB0404'>[有人@你]</font>" + "<font>" + stringBuffer + "</font>"));
            } else {
                view.setText(stringBuffer);
            }


        } else {
            if (messageRecent.getBubble_num() > ONECOUNT && !messageRecent.isNotify()) {
                String bubbleNum = messageRecent.getBubble_num() > MORECOUNT ? mContext.getString(R.string.im_notify_text) : ImUtils.getBubleText(String.valueOf(messageRecent.getBubble_num()));
                view.setText("[" + bubbleNum + "条]" + "[" + string + "]" + obj.get("name"));
            } else {
                view.setText("[" + string + "]" + obj.get("name"));
            }
        }

        if (messageRecent.getBy1() != null && messageRecent.getBy1().equals("1")) {
            view.setText(Html.fromHtml("<font color='#AB0404'>[有人@你]</font>" + "<font>" + stringBuffer + "</font>"));
        } else if (!TextUtils.isEmpty(messageRecent.getDrafttext())) {
            if (messageRecent.getBubble_num() > ONECOUNT && !messageRecent.isNotify()) {
                String bubbleNum = messageRecent.getBubble_num() > MORECOUNT ? mContext.getString(R.string.im_notify_text) : ImUtils.getBubleText(String.valueOf(messageRecent.getBubble_num()));
                view.setText(Html.fromHtml("<font color='#AB0404'>[草稿]</font>" + "<font>" + "[" + bubbleNum + "条]" + messageRecent.getDrafttext() + "</font>"));
            } else {
                view.setText(Html.fromHtml("<font color='#AB0404'>[草稿]</font>" + "<font>" + messageRecent.getDrafttext() + "</font>"));
            }
        }

        if (messageRecent.getBy1() != null && messageRecent.getBy1().equals("1") && !TextUtils.isEmpty(messageRecent.getDrafttext())) {
            view.setText(Html.fromHtml("<font color='#AB0404'>[有人@你]</font>" + "<font>" + "<font color='#AB0404'>[草稿]</font>" + "<font>" + stringBuffer + messageRecent.getDrafttext() + "</font>"));
        }
    }

    private void showTips(MessageRecent messageRecent, TextView view, int string) {
        String stringBuffer = "";
        if (messageRecent.getChat_type() == 1) {
            if (messageRecent.getSender() != 0) {
                Contact contact = ImserviceHelp.getInstance().db_getContactById(messageRecent.getSender());//查找是否是好友
                GroupMembers groupMembers = ImserviceHelp.getInstance().db_findMemberListByGroupidandUid(messageRecent.getChat_id(), messageRecent.getSender());//查找群成员
                if (contact == null) {
                    if (!TextUtils.isEmpty(messageRecent.getContent()) && groupMembers != null)
                        stringBuffer = (TextUtils.isEmpty(groupMembers.getMember_name()) ? groupMembers.getNick_name() : groupMembers.getMember_name()) + ":" + mContext.getString(string);
                    else
                        stringBuffer = "";
                } else {
                    if (!TextUtils.isEmpty(messageRecent.getContent()) && groupMembers != null)
                        stringBuffer = (TextUtils.isEmpty(contact.getComment_name()) ? TextUtils.isEmpty(groupMembers.getMember_name()) ? groupMembers.getNick_name() : groupMembers.getMember_name()
                                : contact.getComment_name()) + ":" + mContext.getString(string);
                    else
                        stringBuffer = "";
                }
            } else {
                stringBuffer = messageRecent.getContent();
            }

            if (messageRecent.getBubble_num() > ONECOUNT && !messageRecent.isNotify()) {
                String beforeText = stringBuffer;
                String bubbleNum = messageRecent.getBubble_num() > MORECOUNT ? mContext.getString(R.string.im_notify_text) : ImUtils.getBubleText(String.valueOf(messageRecent.getBubble_num()));
                stringBuffer = "[" + bubbleNum + "条]" + beforeText;
            }

            if (messageRecent.getBy1() != null && messageRecent.getBy1().equals("1")) {
                view.setText(Html.fromHtml("<font color='#AB0404'>[有人@你]</font>" + "<font>" + stringBuffer + "</font>"));
            } else {
                view.setText(stringBuffer);
            }

        } else {
            if (messageRecent.getBubble_num() > ONECOUNT && !messageRecent.isNotify()) {
                String bubbleNum = messageRecent.getBubble_num() > MORECOUNT ? mContext.getString(R.string.im_notify_text) : ImUtils.getBubleText(String.valueOf(messageRecent.getBubble_num()));
                view.setText("[" + bubbleNum + "条]" + mContext.getString(string));
            } else {
                view.setText(string);
            }
        }

        if (messageRecent.getBy1() != null && messageRecent.getBy1().equals("1")) {
            view.setText(Html.fromHtml("<font color='#AB0404'>[有人@你]</font>" + "<font>" + stringBuffer + "</font>"));
        } else if (!TextUtils.isEmpty(messageRecent.getDrafttext())) {
            if (messageRecent.getBubble_num() > ONECOUNT && !messageRecent.isNotify()) {
                String bubbleNum = messageRecent.getBubble_num() > MORECOUNT ? mContext.getString(R.string.im_notify_text) : ImUtils.getBubleText(String.valueOf(messageRecent.getBubble_num()));
                view.setText(Html.fromHtml("<font color='#AB0404'>[草稿]</font>" + "<font>" + "[" + bubbleNum + "条]" + messageRecent.getDrafttext() + "</font>"));
            } else {
                view.setText(Html.fromHtml("<font color='#AB0404'>[草稿]</font>" + "<font>" + messageRecent.getDrafttext() + "</font>"));
            }
        }

        if (messageRecent.getBy1() != null && messageRecent.getBy1().equals("1") && !TextUtils.isEmpty(messageRecent.getDrafttext())) {
            view.setText(Html.fromHtml("<font color='#AB0404'>[有人@你]</font>" + "<font>" + "<font color='#AB0404'>[草稿]</font>" + "<font>" + stringBuffer + messageRecent.getDrafttext() + "</font>"));
        }
    }

    private void showTips(MessageRecent messageRecent, TextView view, String string) {
        String stringBuffer = "";
        if (messageRecent.getChat_type() == 1) {
            if (messageRecent.getSender() != 0) {
                Contact contact = ImserviceHelp.getInstance().db_getContactById(messageRecent.getSender());//查找是否是好友
                GroupMembers groupMembers = ImserviceHelp.getInstance().db_findMemberListByGroupidandUid(messageRecent.getChat_id(), messageRecent.getSender());//查找群成员
                if (contact == null) {
                    if (!TextUtils.isEmpty(messageRecent.getContent()) && groupMembers != null)
                        stringBuffer = (TextUtils.isEmpty(groupMembers.getMember_name()) ? groupMembers.getNick_name() : groupMembers.getMember_name()) + ":" + string;
                    else
                        stringBuffer = "";
                } else {
                    if (!TextUtils.isEmpty(messageRecent.getContent()) && groupMembers != null)
                        stringBuffer = (TextUtils.isEmpty(contact.getComment_name()) ? TextUtils.isEmpty(groupMembers.getMember_name()) ? groupMembers.getNick_name() : groupMembers.getMember_name()
                                : contact.getComment_name()) + ":" + string;
                    else
                        stringBuffer = "";
                }
            } else {
                stringBuffer = messageRecent.getContent();
            }

            if (messageRecent.getBubble_num() > ONECOUNT && !messageRecent.isNotify()) {
                String beforeText = stringBuffer;
                String bubbleNum = messageRecent.getBubble_num() > MORECOUNT ? mContext.getString(R.string.im_notify_text) : ImUtils.getBubleText(String.valueOf(messageRecent.getBubble_num()));
                stringBuffer = "[" + bubbleNum + "条]" + beforeText;
            }

            if (messageRecent.getBy1() != null && messageRecent.getBy1().equals("1")) {
                view.setText(Html.fromHtml("<font color='#AB0404'>[有人@你]</font>" + "<font>" + stringBuffer + "</font>"));
            } else {
                view.setText(stringBuffer);
            }

        } else {
            if (messageRecent.getBubble_num() > ONECOUNT && !messageRecent.isNotify()) {
                String bubbleNum = messageRecent.getBubble_num() > MORECOUNT ? mContext.getString(R.string.im_notify_text) : ImUtils.getBubleText(String.valueOf(messageRecent.getBubble_num()));
                view.setText("[" + bubbleNum + "条]" + string);
            } else {
                view.setText(string);
            }
        }

        if (messageRecent.getBy1() != null && messageRecent.getBy1().equals("1")) {
            view.setText(Html.fromHtml("<font color='#AB0404'>[有人@你]</font>" + "<font>" + stringBuffer + "</font>"));
        } else if (!TextUtils.isEmpty(messageRecent.getDrafttext())) {
            if (messageRecent.getBubble_num() > ONECOUNT && !messageRecent.isNotify()) {
                String bubbleNum = messageRecent.getBubble_num() > MORECOUNT ? mContext.getString(R.string.im_notify_text) : ImUtils.getBubleText(String.valueOf(messageRecent.getBubble_num()));
                view.setText(Html.fromHtml("<font color='#AB0404'>[草稿]</font>" + "<font>" + "[" + bubbleNum + "条]" + messageRecent.getDrafttext() + "</font>"));
            } else {
                view.setText(Html.fromHtml("<font color='#AB0404'>[草稿]</font>" + "<font>" + messageRecent.getDrafttext() + "</font>"));
            }
        }

        if (messageRecent.getBy1() != null && messageRecent.getBy1().equals("1") && !TextUtils.isEmpty(messageRecent.getDrafttext())) {
            view.setText(Html.fromHtml("<font color='#AB0404'>[有人@你]</font>" + "<font>" + "<font color='#AB0404'>[草稿]</font>" + "<font>" + stringBuffer + messageRecent.getDrafttext() + "</font>"));
        }

    }


    /**
     * 加载图片完成后将view的Tag设置成url
     */
    public ImageLoadingListener imageLoadingListener = new ImageLoadingListener() {

        @Override
        public void onLoadingStarted(String imageUri, View view) {

        }

        @Override
        public void onLoadingFailed(String imageUri, View view,
                                    FailReason failReason) {

        }

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            view.setTag(imageUri);
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

        }
    };

    /**
     * 显示最近聊天的文本内容
     *
     * @param messageRecent
     * @param holder
     */
    private void showRecentText(MessageRecent messageRecent, ViewHolder holder) {
        MsgContent msgContent = null;
        switch (messageRecent.getContent_type()) {
            case MessageHistory.CONTENT_TYPE_TEXT:
                showText(messageRecent, holder.row_tv_content);
                break;
            case MessageHistory.CONTENT_TYPE_VOICE:
                if (messageRecent.getBubble_num() > ONECOUNT && !messageRecent.isNotify()) {
                    String bubbleNum = messageRecent.getBubble_num() > MORECOUNT ? mContext.getString(R.string.im_notify_text) : ImUtils.getBubleText(String.valueOf(messageRecent.getBubble_num()));
                    holder.row_tv_content.setText("[" + bubbleNum + "条]语音");
                } else {
                    holder.row_tv_content.setText(R.string.im_chat_voice);
                }
                break;
            case MessageHistory.CONTENT_TYPE_IMAGE:
                if (messageRecent.getBubble_num() > ONECOUNT && !messageRecent.isNotify()) {
                    String bubbleNum = messageRecent.getBubble_num() > MORECOUNT ? mContext.getString(R.string.im_notify_text) : ImUtils.getBubleText(String.valueOf(messageRecent.getBubble_num()));
                    holder.row_tv_content.setText("[" + bubbleNum + "条]图片");
                } else {
                    holder.row_tv_content.setText(R.string.im_chat_image);
                }
                break;
            case MessageHistory.CONTENT_TYPE_VCARD:
                showTips(messageRecent, holder.row_tv_content, R.string.im_chat_card);
                break;
            case MessageHistory.CONTENT_TYPE_TIGER:
                showTips(messageRecent, holder.row_tv_content, R.string.im_chat_tiger);
                break;
            case MessageHistory.CONTENT_TYPE_SHARE_TIGER:
            case MessageHistory.CONTENT_TYPE_INTEREST_SHARE:
            case MessageHistory.CONTENT_TYPE_SOUYUE_NEWS_SHARE:
                showTips(messageRecent, holder.row_tv_content, R.string.im_chat_share);
                break;
            case MessageHistory.CONTENT_TYPE_NEW_VOICE:
                showTips(messageRecent, holder.row_tv_content, R.string.im_chat_voice);
                break;
            case MessageHistory.CONTENT_TYPE_NEW_IMAGE:
                showTips(messageRecent, holder.row_tv_content, R.string.im_chat_image);
                break;
            case MessageHistory.CONTENT_TYPE_SECRET_MSG:
                holder.row_tv_content.setText(R.string.message);//消息
                showTips(messageRecent, holder.row_tv_content, R.string.message);
                break;
            case MessageHistory.CONTENT_TYPE_SENDCOIN:
                holder.row_tv_content.setText(R.string.send_coin);//赠币
                showTips(messageRecent, holder.row_tv_content, R.string.send_coin);
                break;
            case MessageHistory.CONTENT_TYPE_INTEREST_ADD_FRIEND_PRIVATE:
            case MessageHistory.CONTENT_TYPE_INTEREST_CIRCLE_CARD:
                showTips(messageRecent, holder.row_tv_content, R.string.interest_circle_card);
                break;
            case MessageHistory.CONTENT_TYPE_SERVICE_MESSAGE_FIRST:  //服务号类型1
                showText(messageRecent, holder.row_tv_content);
                break;
            case MessageHistory.CONTENT_TYPE_SERVICE_MESSAGE_SECOND://服务号类型2
                showText(messageRecent, holder.row_tv_content);
                break;
            case MessageHistory.CONTENT_TYPE_GROUP_CARD:
                showTips(messageRecent, holder.row_tv_content, R.string.im_chat_card);
                break;
            case MessageHistory.CONTENT_TYPE_AT_FRIEND:
                if (!TextUtils.isEmpty(messageRecent.getContent()) && messageRecent.getContent().startsWith("{")) {
                    AtFriend atFriend = new Gson().fromJson(messageRecent.getContent(), AtFriend.class);
                    String newc = "";
                    for (int i = 0; i < atFriend.getUsers().size(); i++) {
                        long uid = atFriend.getUsers().get(i).getUid();
                        String nickname = atFriend.getUsers().get(i).getNick();
                        Contact contact = ImserviceHelp.getInstance().db_getContactById(uid);

                        GroupMembers groupMembers = ImserviceHelp.getInstance().db_findMemberListByGroupidandUid(messageRecent.getChat_id(), uid);
                        String newname = "";
                        if (contact != null && groupMembers != null) {
                            newname = TextUtils.isEmpty(contact.getComment_name()) ? TextUtils.isEmpty(groupMembers.getMember_name()) ?
                                    groupMembers.getNick_name() : groupMembers.getMember_name() : contact.getComment_name();
                        } else if (groupMembers != null) {
                            newname = TextUtils.isEmpty(groupMembers.getMember_name()) ?
                                    groupMembers.getNick_name() : groupMembers.getMember_name();
                        }

                        newc = atFriend.getC().replace(nickname, newname);
                    }
                    messageRecent.setContent(newc);
                    showText(messageRecent, holder.row_tv_content);
                } else {
                    showText(messageRecent, holder.row_tv_content);
                }
                break;
            case MessageHistory.CONTENT_TYPE_SRP_SHARE:
                String str = mContext.getResources().getString(R.string.im_chat_srp_share);
                String content = messageRecent.getContent();
                if (content != null) {
                    SRP srp = new Gson().fromJson(content, SRP.class);
                    showTips(messageRecent, holder.row_tv_content, String.format(str, srp.getKeyword()));
                } else {
                    showTips(messageRecent, holder.row_tv_content, R.string.im_chat_share);
                }
                break;
            case MessageHistory.CONTENT_TYPE_GIF:
                showTips(messageRecent, holder.row_tv_content, R.string.im_chat_gif);
                break;
            case MessageHistory.CONTENT_TYPE_WEB:
                showTips(messageRecent, holder.row_tv_content, R.string.im_chat_share);
                break;
            case MessageHistory.CONTENT_TYPE_SYS_NOTFRIEND:
                showTips(messageRecent, holder.row_tv_content, String.format(mContext.getString(R.string.im_sysnofriend), messageRecent.getChatName()));
                break;
            case MessageHistory.CONTENT_TYPE_FILE:
                try {
                    showFileTips(messageRecent, holder.row_tv_content, mContext.getString(R.string.im_chat_file));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case MessageHistory.CONTENT_TYPE_RED_PAKETS:
                try {
                    msgContent = new Gson().fromJson(messageRecent.getContent(), MsgContent.class);
                    messageRecent.setContent("[搜悦红包]"+msgContent.getText());
                    messageRecent.setContent_type(IMessageConst.CONTENT_TYPE_TEXT);
                    showText(messageRecent, holder.row_tv_content);
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
            case MessageHistory.CONTENT_TYPE_NEW_SYSTEM_MSG:
                try {
                    msgContent = new Gson().fromJson(messageRecent.getContent(), MsgContent.class);
                    messageRecent.setContent(msgContent.getText());
                    messageRecent.setSender(0);
                    messageRecent.setContent_type(IMessageConst.CONTENT_TYPE_TEXT);
                    showText(messageRecent, holder.row_tv_content);
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
            default:
                showTips(messageRecent, holder.row_tv_content, R.string.im_chat_tip);
                break;
        }

    }

}
