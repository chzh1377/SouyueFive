package com.zhongsou.souyue.im.render;

/**
 * Created by zhangwenbin on 15/3/30.
 */

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;
import org.json.JSONException;
import org.json.JSONObject;

public class MsgGiftCoinRender extends MsgItemRender {

    /**
     * 显示的文本上行
     */
    private TextView tvGiftCoin;

    private TextView tvGiftCount;
    /**
     * 缓存解析过得中搜币数量
     */
    private SparseArray<String> mCoins = new SparseArray<String>();

    private User mUser;


    public MsgGiftCoinRender(Context context,
                             BaseTypeAdapter<ChatMsgEntity> adapter, int itemType) {
        super(context, adapter, itemType);


    }

    @Override
    public void fitEvents() {
        super.fitEvents();
        // TODO custom event
        mUser = SYUserManager.getInstance().getUser();
        mViewHolder.obtainView(mContentView,R.id.ll_msg_giftcoin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mChatAdapter.getIsEdit()) {
                    IntentUtil
                            .gotoWeb(
                                    mContext,
                                    createAccessUrl(UrlConfig.HOST_ZHONGSOU_COINS_BLANCE),
                                    null);
                }else{
                    if(cbCheck.isChecked()){
                        cbCheck.setChecked(false);
                        mChatMsgEntity.setEdit(false);
                        cbCheck.setBackgroundResource(R.drawable.im_chat_checkbox);
                    }else{
                        mChatMsgEntity.setEdit(true);
                        cbCheck.setChecked(true);
                        cbCheck.setBackgroundResource(R.drawable.im_chat_checkbox_selected);
                    }
                }
            }
        });

        mViewHolder.obtainView(mContentView,R.id.ll_msg_giftcoin).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mChatAdapter.getIsEdit()) {
                    showLCDialog(false, true);
                }
                return true;
            }
        });

    }

    @Override
    public void fitDatas(int position) {
        super.fitDatas(position);
        // TODO custom fit data

        //将控件放入缓存
        tvGiftCoin = mViewHolder.obtainView(mContentView,
                R.id.gift_coin_text);
        tvGiftCount = mViewHolder.obtainView(mContentView,R.id.gift_coin_count);

        //按接收双方来显示文字
        if (mChatMsgEntity.isComMsg()){
            mMsgMananger.getFriendName();
            tvGiftCoin.setText("收到"+mMsgMananger.getFriendName()+"的转账");
            tvGiftCount.setText(obtainCoin(mChatMsgEntity)+"中搜币");
        }else {
            tvGiftCoin.setText("转账给"+mMsgMananger.getFriendName());
            tvGiftCount.setText(obtainCoin(mChatMsgEntity)+"中搜币");
        }
    }

    @Override
    protected int getLeftLayoutId() {
        return R.layout.msg_giftcoin_left_view;
    }

    @Override
    protected int getRightLayoutId() {
        return R.layout.msg_giftcoin_right_view;
    }

    /**
     * 获取缓存中的中搜币数量
     * @param msgEntity
     * @return
     */
    private String obtainCoin(ChatMsgEntity msgEntity){
        String coinText = mCoins.get((int)msgEntity.getId());

        if (null == coinText){
            coinText = parseJson2String(msgEntity);
            mCoins.put((int)msgEntity.getId(),coinText);
        }

        return coinText;
    }

    /**
     * 解析json得到中搜币数量
     * @param msgEntity
     * @return
     */
    private String parseJson2String(ChatMsgEntity msgEntity){
        try {
            JSONObject jsonObject = new JSONObject(msgEntity.getText().toString());
            return jsonObject.getString("count");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    /**
     *
     * @param url
     * @return
     */
    private String createAccessUrl(String url) {
        StringBuilder sb = new StringBuilder(url);
        sb.append("?username=").append(mUser.userName()).append("&token=")
                .append(mUser.token()).append("&r=")
                .append(System.currentTimeMillis());
        return sb.toString();
    }
}