package com.zhongsou.souyue.im.render;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.dialog.ImDialog;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * Created by zcz on 2015/3/3016:05.
 */
public class MsgNoFriendRender extends MsgItemRender {

    private Context mContext;
    private TextView tv_sysmsg;

    public MsgNoFriendRender(Context context, BaseTypeAdapter<ChatMsgEntity> adapter, int itemType) {
        super(context, adapter, itemType);
        this.mContext = context;
        tv_sysmsg = mViewHolder.obtainView(mContentView, R.id.tv_sysmsg);
    }

    @Override
    public void fitDatas(int position) {
        super.fitDatas(position);

        switch (mChatMsgEntity.getType()) {
            case IMessageConst.CONTENT_TYPE_SYS_NOTFRIEND:
                String str = String.format(mContext.getString(R.string.im_sysnofriend),
                        mMsgMananger.getFriendName());
//                SpannableString spanString = new SpannableString(str);
//                ForegroundColorSpan span = new ForegroundColorSpan(R.color.im_validator);
//                spanString.setSpan(span, str.length() - 6, str.length(),
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                tv_sysmsg.setText(Html.fromHtml(str + "<font color=#2B91EC>发送验证申请</font>"));
                tv_sysmsg.setVisibility(View.VISIBLE);
                break;
            case IMessageConst.CONTENT_TYPE_SYSMSG:
                String strSys = mChatMsgEntity.getText();
                if (!TextUtils.isEmpty(strSys)) {
                    tv_sysmsg.setVisibility(View.VISIBLE);
                    tv_sysmsg.setText(ToDBC(strSys));
                } else {
                    tv_sysmsg.setVisibility(View.GONE);
                }
                break;
        }

    }

    /**
     * 对android TextView 诡异换行的处理  其一
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i< c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }if (c[i]> 65280&& c[i]< 65375)
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
                switch (mChatMsgEntity.getType()) {
                    case IMessageConst.CONTENT_TYPE_SYS_NOTFRIEND:
                        if (!mChatAdapter.getIsEdit()) {
                            ImDialog.Builder builder = new ImDialog.Builder(v.getContext());
                            builder.setTitle(R.string.im_dialog_title);
                            builder.setEditShowMsg("我是" + SYUserManager.getInstance().getName());
                            builder.setPositiveButton("发送", new ImDialog.Builder.ImDialogInterface() {
                                @Override
                                public void onClick(DialogInterface dialog, View v) {
                                    mMsgMananger.sendAddFriend(v.getTag().toString());
                                }
                            });
                            builder.create().show();
                        }
                        break;
                }
            }
        });

    }

    @Override
    protected int getLeftLayoutId() {
        return R.layout.msg_notfriend_left_view;
    }

    @Override
    protected int getRightLayoutId() {
        return R.layout.msg_notfriend_right_view;
    }
}
