package com.zhongsou.souyue.im.render;

import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.speex.encode.AudioLoader;
import com.speex.encode.AudioPlayListener;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.ImChangeView;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zcz on 2015/3/26.
 */
public class MsgAudioRender extends MsgItemRender {

	private TextView tv_audio_isRead;
	private TextView im_chat_voice_length;
	private ImageView im_chat_voice_img;//语音播放动态指示的Image
	private TextView im_chat_tv_voice;
	private String length;
	private float parseInt;
	public Handler handler = new Handler();
	private int lenInt;
	private int count;
	private Context mContext;
	private AlertDialog dialog;
	private int mDialogArr[] ={R.string.dialog_inner_play,R.string.dialog_delete };
	AudioManager audioManager;
	public MsgAudioRender(Context context,
			BaseTypeAdapter<ChatMsgEntity> adapter, int itemType) {
		super(context, adapter, itemType);
		this.mContext = context;
		audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);//获取语音播放服务
	}

	@Override
	public void fitDatas(int position) {
		super.fitDatas(position);
		tv_audio_isRead = mViewHolder.obtainView(mContentView,
				R.id.tv_audio_isRead);
		im_chat_voice_length = mViewHolder.obtainView(mContentView,R.id.im_chat_voice_length);
		im_chat_voice_img = mViewHolder.obtainView(mContentView,R.id.im_chat_voice_img);

		if (mChatMsgEntity.isVoice()) {
			if (mChatMsgEntity.isComMsg()) {// 判断语音读未读
				if (mChatMsgEntity.status == 2) {
					tv_audio_isRead.setVisibility(View.GONE);
				} else {
					tv_audio_isRead.setVisibility(View.VISIBLE);
				}
			}
		}
		JSONObject json = null;
		try {
			json = new JSONObject(mChatMsgEntity.getText());
			mChatMsgEntity.setUrl(json.getString("url"));
			length = json.getString("length");
			// parseInt = Integer.parseInt(length);
			parseInt = Float.parseFloat(length);
			lenInt = (int) Math.round(parseInt);
            mChatMsgEntity.setVoiceLength(lenInt);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		AudioLoader.getInstance().loadAudio(mChatMsgEntity.getUrl());
		im_chat_voice_length.setText(lenInt + " \"");

	}

	@Override
	public void fitEvents() {
		super.fitEvents();
		im_chat_tv_voice = mViewHolder.obtainView(mContentView,R.id.tv_voice);
		im_chat_tv_voice.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						if (mChatMsgEntity.isComMsg()) {
							im_chat_tv_voice.setBackgroundResource(R.drawable.chatfrom_bg_pressed);
						} else {
							im_chat_tv_voice.setBackgroundResource(R.drawable.chatto_bg_pressed);
						}

						break;
					case MotionEvent.ACTION_UP:
						if (mChatMsgEntity.isComMsg()) {
							im_chat_tv_voice.setBackgroundResource(R.drawable.chatfrom_bg_normal);
						} else {
							im_chat_tv_voice.setBackgroundResource(R.drawable.chatto_bg_normal);
						}
						break;
				}
				return false;
			}
		});

		mViewHolder.obtainView(mContentView,	R.id.tv_voice).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mChatAdapter.getIsEdit()) {
					Log.d("COOL", "onclick");
					resetVoiceViewStates(im_chat_voice_img);
					ImserviceHelp.getInstance().updateStatus(mChatMsgEntity.getRetry(), mChatMsgEntity.getType(), mMsgMananger.getFriendId(), 2);
					tv_audio_isRead = mViewHolder.obtainView(mContentView,R.id.tv_audio_isRead);
					if(tv_audio_isRead != null){
						tv_audio_isRead.setVisibility(View.GONE);//是否已读的图标
					}
					mChatMsgEntity.status = 2;
					new AudioLoader().getInstance().display(mChatMsgEntity.getUrl(), mViewHolder.obtainView(mContentView,R.id.tv_voice), new AudioPlayListener() {
						@Override
						public void onDisplayingStart(long curTime, long totalTime, View view) {
							startRefreshVoice();
						}

						@Override
						public void onDisplayingPause(long curTime, long totalTime, View view) {
							stopRefreshVoice();
						}

						@Override
						public void onDisplayingEnd(long curTime, long totalTime, View view) {
							stopRefreshVoice();
						}

						@Override
						public void onDisplayPreparing(View view) {
							startRefreshVoice();
						}
					});
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
		mViewHolder.obtainView(mContentView,	R.id.tv_voice).setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (!mChatAdapter.getIsEdit()) {
					if(SYSharedPreferences.getInstance().getBoolean("showIcon",false)){//判断是显示听筒模式还是外放模式
						mDialogArr[0] = R.string.dialog_out_play;//如果是听筒模式则dialog显示切换成外放模式
					}else{
						mDialogArr[0] = R.string.dialog_inner_play;
					}
					dialog = MsgUtils.showDialog(mContext,mDialogArr , new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							if(mDialogArr[0]==id){//切换播放模式
								if(SYSharedPreferences.getInstance().getBoolean("showIcon",false)){//判断切换标识，并  进行图标隐现   现在是听筒模式
									SYSharedPreferences.getInstance().putBoolean("showIcon", false);
									((ImChangeView)mContext).hideTitleIcon();
									if(mContext instanceof ImChangeView){
										((ImChangeView)mContext).showTipsText(R.string.tips_out);
									}
								}else{//外放模式 切换成听筒模式
									SYSharedPreferences.getInstance().putBoolean("showIcon", true);
									((ImChangeView)mContext).showTitleIcon();
									if(mContext instanceof ImChangeView){
										((ImChangeView)mContext).showTipsText(R.string.tips_inner);
									}
								}

							}else if(mDialogArr[1]==id){//执行删除
								deleteItem();
							}else{//其他  理论上说不存在

							}
							if(dialog!=null){
								dialog.dismiss();
							}
						}
					});
				}
				return false;
			}
		});
	}

	@Override
	protected int getLeftLayoutId() {
		return R.layout.msg_audio_left_view;
	}

	@Override
	protected int getRightLayoutId() {
		return R.layout.msg_audio_right_view;
	}

	private void resetVoiceViewStates(ImageView im_chat_voice_img) {
		if (im_chat_voice_img != null) {
			if (mChatMsgEntity.isComMsg()) {
				im_chat_voice_img.setImageResource(R.drawable.voice_left_3);
			} else {
				im_chat_voice_img.setImageResource(R.drawable.voice_right_3);
			}
		}
	}

	public void stopRefreshVoice() {
		handler.removeCallbacks(refresh);
		if (mChatMsgEntity.isComMsg()) {
			im_chat_voice_img.setImageResource(R.drawable.voice_left_3);
		} else {
			im_chat_voice_img.setImageResource(R.drawable.voice_right_3);
		}
	}

	public void startRefreshVoice() {
		stopRefreshVoice();
		handler.postDelayed(refresh, 500);
	}


	private Runnable refresh = new Runnable() {

		@Override
		public void run() {
			switch (count % 4) {
				case 0:
					/*
					 * voiceImg.setImageResource(R.drawable.empty); break;
					 */
				case 1:
					if (mChatMsgEntity.isComMsg()) {
						im_chat_voice_img.setImageResource(R.drawable.voice_left_1);
					} else {
						im_chat_voice_img.setImageResource(R.drawable.voice_right_1);
					}
					break;
				case 2:
					if (mChatMsgEntity.isComMsg()) {
						im_chat_voice_img.setImageResource(R.drawable.voice_left_2);
					} else {
						im_chat_voice_img.setImageResource(R.drawable.voice_right_2);
					}
					break;
				case 3:
					if (mChatMsgEntity.isComMsg()) {
						im_chat_voice_img.setImageResource(R.drawable.voice_left_3);
					} else {
						im_chat_voice_img.setImageResource(R.drawable.voice_right_3);
					}
					break;
				default:
					break;
			}
			count++;
			handler.postDelayed(this, 200);
		}
	};
}
