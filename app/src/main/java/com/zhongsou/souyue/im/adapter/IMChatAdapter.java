package com.zhongsou.souyue.im.adapter;

/**
 * Created by zhangwenbin on 15/3/24.
 */

import android.content.Context;
import com.zhongsou.souyue.im.render.BaseTypeAdapter;
import com.zhongsou.souyue.im.render.ItemTypeRender;
import com.zhongsou.souyue.im.render.MsgUtils;
import com.zhongsou.souyue.module.ChatMsgEntity;

import java.util.List;

public class IMChatAdapter extends BaseTypeAdapter<ChatMsgEntity> {
	private Context mContext;

	public IMChatAdapter(Context context, List<ChatMsgEntity> objects) {
		super(context, objects);
		mContext = context;
	}

	@Override
	public int getItemViewType(int position) {
		return MsgUtils.getItemViewType(getItem(position));
	}

	@Override
	public int getViewTypeCount() {
		return MsgUtils.getItemTypeCount();
	}

	@Override
	public ItemTypeRender getAdapterTypeRender(int position) {
		return MsgUtils.getItemTypeRender(mContext, getItemViewType(position),
				this);
	}
}