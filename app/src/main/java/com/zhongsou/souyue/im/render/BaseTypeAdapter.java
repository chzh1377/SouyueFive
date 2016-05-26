package com.zhongsou.souyue.im.render;

/**
 * Created by zhangwenbin on 15/3/24.
 */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.zhongsou.souyue.R;

import java.util.List;

public abstract class BaseTypeAdapter<T> extends ArrayAdapter<T> {

	private MessageManager mMsgManager;
	private boolean isEdit=false;
	public BaseTypeAdapter(Context context, List<T> objects) {
		super(context, 0, objects);
	}

	public boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	public void setMsgManager(MessageManager msgManager) {
		mMsgManager = msgManager;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemTypeRender typeRender;
		if (null == convertView) {
			typeRender = getAdapterTypeRender(position);
			typeRender.setMesssageManager(mMsgManager);
			convertView = typeRender.getConvertView();
			convertView.setTag(R.id.msg_adapter_item_render, typeRender);

			typeRender.fitEvents();
		} else {
			typeRender = (ItemTypeRender) convertView
					.getTag(R.id.msg_adapter_item_render);
		}
		convertView.setTag(R.id.msg_adapter_item_position, position);

		if (null != typeRender) {
			typeRender.fitDatas(position);
		}

		return convertView;
	}

	/**
	 * 根据指定position的item获取对应的type，然后通过type实例化一个AdapterTypeRender的实现
	 * 
	 * @param position
	 * @return
	 */
	public abstract ItemTypeRender getAdapterTypeRender(int position);
}
