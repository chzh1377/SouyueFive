package com.zhongsou.souyue.im.render;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.tuita.sdk.im.db.module.MessageHistory;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.TouchGalleryActivity;
import com.zhongsou.souyue.im.ac.IMChatActivity;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.module.TouchGallerySerializable;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.ImageUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zcz on 2015/3/26.
 */
public class MsgImageRender extends MsgItemRender {

	protected ImageView ivChatImage = null;
	protected int mBigSize;// 竖图大于两倍图片的高度
	protected int mLowSize;// 横图大于两倍图片的宽度
	private ImageLoadingListener imagerListner;
	public MsgImageRender(final Context context,
			BaseTypeAdapter<ChatMsgEntity> adapter, int itemType) {
		super(context, adapter, itemType);
		mBigSize = DeviceUtil.dip2px(context, 120);
		mLowSize = DeviceUtil.dip2px(context, 60);
		imagerListner=new ImageLoadingListener(){
			@Override
			public void onLoadingStarted(String s, View view) {
			}

			@Override
			public void onLoadingFailed(String s, View view, FailReason failReason) {
			}

			@Override
			public void onLoadingComplete(String s, View view, Bitmap bitmap) {
				String fileName = ImageUtil.getFileName(s);
				File file = new File(MsgUtils.getThumbPathFile(context),fileName+".jpg");
				if(!file.exists()&&file.getParentFile().canWrite()){
					MsgUtils.saveImage(context,fileName,bitmap);
				}
			}

			@Override
			public void onLoadingCancelled(String s, View view) {
			}
		};
	}


	@Override
	public void fitDatas(int position) {
		super.fitDatas(position);
        if (mChatMsgEntity.getType() == IMessageConst.CONTENT_TYPE_NEW_IMAGE) {
            ivChatImage = mViewHolder.obtainView(mContentView, R.id.iv_image);
            JSONObject json = null;
            try {
                json = new JSONObject(mChatMsgEntity.getText());
                if (mChatMsgEntity.isComMsg()) {
                    mChatMsgEntity.setUrl(json.getString("url"));
                } else {
                    String localPath = null;
                    if (json.has("localPath")) {
                        localPath = json.getString("localPath");
                    }
                    if (localPath != null && !"".equals(localPath) && !mChatMsgEntity.isComMsg() && new File(localPath).exists()) {
                        mChatMsgEntity.setUrl(localPath);
                    } else {
                        mChatMsgEntity.setUrl(json.getString("url"));
                    }
                }
                if (json.has("image-height") || json.has("image-width")) {
                    mChatMsgEntity.setMinHeight(json.getInt("image-height"));
                    mChatMsgEntity.setMinWidth(json.getInt("image-width"));
                }
                if (json.has("isVertical")) {
                    mChatMsgEntity.setVertical(json.getBoolean("isVertical"));
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            ViewGroup.LayoutParams lp = ivChatImage.getLayoutParams();
            if (mChatMsgEntity.getMinWidth() >= (mChatMsgEntity.getMinHeight() * 2)) {// 宽是高的两倍或更多
                lp.width = mBigSize;
                lp.height = mLowSize;
            }
            if (mChatMsgEntity.getMinHeight() >= (mChatMsgEntity.getMinWidth() * 2)) {// 高是宽的两倍或更多
                lp.height = mBigSize;
                lp.width = mLowSize;
            }
            if (mChatMsgEntity.getMinWidth() > mChatMsgEntity.getMinHeight()
                    && mChatMsgEntity.getMinWidth() < (mChatMsgEntity.getMinHeight() * 2)) {// 宽图但小于二倍
                lp.width = mBigSize;
                lp.height = (int) (mChatMsgEntity.getMinHeight() * mBigSize / mChatMsgEntity.getMinWidth());
            }
            if (mChatMsgEntity.getMinHeight() > mChatMsgEntity.getMinWidth()
                    && mChatMsgEntity.getMinHeight() < (mChatMsgEntity.getMinWidth() * 2)) {// 竖图但小于二倍
                lp.height = mBigSize;
                lp.width = (int) (mBigSize * mChatMsgEntity.getMinWidth() / mChatMsgEntity.getMinHeight());
            }

            if (mChatMsgEntity.getMinWidth() == mChatMsgEntity.getMinHeight()) {// 正方形
                lp.width = mBigSize;
                lp.height = mBigSize;
            }
            ivChatImage.setLayoutParams(lp);
            String imagePath = null;
            String imageLocPath = null;
            try {
                imagePath = json.getString("url").replace("!ios", "").replace("!android", "");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            if (json.has("localPath")) {
                try {
                    imageLocPath = json.getString("localPath");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            File file = null;
            if (!TextUtils.isEmpty(imagePath)) {
                file = new File(MsgUtils.getThumbPathFile(mContext), ImageUtil.getFileName(imagePath) + ".jpg");
            }
            File locFile = null;
            if (imageLocPath != null) {
                locFile = new File(MsgUtils.getThumbPathFile(mContext), ImageUtil.getFileName(imageLocPath) + ".jpg");
            }
            if (file != null && file.exists()) {//如果存在网络上的文件
                ImageLoader.getInstance().displayImage("file:/" + file.getPath(), ivChatImage, MsgUtils.getImgOptions(mContext));
            } else if (locFile != null && locFile.exists()) {//如果文件不在thumb但是在temp
                ImageLoader.getInstance().displayImage("file:/" + locFile.getPath(), ivChatImage, MsgUtils.getImgOptions(mContext));
            } else {//如果本地不存在
                //在联想手机上偶然发现有空的情况，先判断下  应该是效率问题
                if (mChatMsgEntity.getUrl() != null) {
                    ImageLoader.getInstance().displayImage(mChatMsgEntity.getUrl().replace("!android", "").replace("!ios", ""),
                            ivChatImage, MsgUtils.getImgOptions(mContext), imagerListner);
                }
            }
        }
	}

	@Override
	public void fitEvents() {
		super.fitEvents();
		mViewHolder.obtainView(mContentView, R.id.iv_image).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				cbCheck = mViewHolder.obtainView(mContentView, R.id.msg_adapter_item_checkbox);
//				if (!setEditBackground(e.isEdit(), cbCheck, e)) {
				if (!mChatAdapter.getIsEdit()) {
//					设置为查看大图
					IMChatActivity.isDetailOpen = ChatMsgEntity.BIGIMAGE;
					//下面是检索所有消息，并从中筛选出所有带图片的消息
					List<MessageHistory> contentList = ImserviceHelp.getInstance().db_getMessage(mMsgMananger.getFriendId(), -1, IConst.QUERY_MSG_ALL);
					List<String> urlList = convertUrlList(contentList);
					List<String> uuiDList = convertUUidList(contentList);
					int mIndex = getPosition(uuiDList, mChatMsgEntity.UUId);

					/*此处有设置是否详情页open的界面，暂时注释
					* */
//					setIsDetailOpen(ChatMsgEntity.BIGIMAGE);

					Intent intent = new Intent(mContext, TouchGalleryActivity.class);
					TouchGallerySerializable s = new TouchGallerySerializable();

					if (mIndex != -1) {//处理异常情况，假如出现异常走原逻辑
						s.setItems(urlList);
						s.setClickIndex(mIndex);
					} else {
						List<String> list = new ArrayList<String>();
						list.add(mChatMsgEntity.getUrl());
						s.setItems(list);
					}
					intent.putExtra("isIM", true);
					intent.putExtra("touchGalleryItems", s);
					mContext.startActivity(intent);
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

		mViewHolder.obtainView(mContentView, R.id.iv_image).setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (!mChatAdapter.getIsEdit()) {
					showLCDialog(false, false);
				}
				return true;
			}
		});
	}

	@Override
	protected int getLeftLayoutId() {
		return R.layout.msg_image_left_view;
	}

	@Override
	protected int getRightLayoutId() {
		return R.layout.msg_image_right_view;
	}
	/**
	 * 抽取URL转换方法  主要用于把一致list实体类中的URL转换为  假如类型是图片类型 15 则把其json中的URL字段拿出放到list<String>中 并返回
	 * @param contentList
	 * @return
	 */
	protected List<String> convertUrlList(List<MessageHistory> contentList){
		if(contentList!=null){
			ArrayList<String> msgImageUrl = new ArrayList<String>();
			for(MessageHistory messageHistory:contentList){
				if (messageHistory.getContent_type()==15){
					try {
						msgImageUrl.add(new JSONObject(messageHistory.getContent()).getString("url"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			return msgImageUrl;
		}else{
			return new ArrayList<String>();
		}
	}

	/**
	 * 抽取  主要用于把一致list实体类中的URL转换为  假如类型是图片类型 15 则把其json中的UUID字段拿出放到list<String>中 并返回
	 * @param contentList
	 * @return
	 */
	protected List<String> convertUUidList(List<MessageHistory> contentList){
		if(contentList!=null){
			ArrayList<String> msgImageUrl = new ArrayList<String>();
			for(MessageHistory messageHistory:contentList){
				if (messageHistory.getContent_type()==15){
					msgImageUrl.add(messageHistory.getUuid());
				}
			}

			return msgImageUrl;
		}else{
			return new ArrayList<String>();
		}
	}

	/**
	 * 找到传入的url在整个urlList中的位置
	 * @param uuid  要找的url
	 * @return
	 */
	protected int getPosition(List<String> uuiDList,String uuid){
		if(uuiDList!=null && uuid!=null){
			for(int i=0;i<uuiDList.size();i++){
				if(uuid.equals(uuiDList.get(i))){
					return i;
				}
			}
		}
		return -1;
	}
}
