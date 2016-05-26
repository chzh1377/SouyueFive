package com.zhongsou.souyue.im.render;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.ChatMsgEntity;

/**
 * Created by zcz on 2015/3/3016:05.
 */
public class MsgUpdateRender extends MsgItemRender {

    private Context mContext;
    private TextView tv_sysmsg;

    public MsgUpdateRender(Context context, BaseTypeAdapter<ChatMsgEntity> adapter, int itemType) {
        super(context, adapter, itemType);
        this.mContext = context;
        tv_sysmsg = mViewHolder.obtainView(mContentView, R.id.tv_sysmsg);
    }

    @Override
    public void fitDatas(int position) {
        super.fitDatas(position);
        String strSys = mContext.getString(R.string.im_sysversiontip);
        tv_sysmsg.setVisibility(View.VISIBLE);
        tv_sysmsg.setText(ToDBC(strSys));
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
                MainApplication.getInstance().checkVersion(1);
            }
        });

    }

    @Override
    protected int getLeftLayoutId() {
        return R.layout.msg_update_left_view;
    }

    @Override
    protected int getRightLayoutId() {
        return R.layout.msg_update_right_view;
    }
}
