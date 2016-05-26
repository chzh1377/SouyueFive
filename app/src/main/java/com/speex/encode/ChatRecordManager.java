package com.speex.encode;

import android.app.Activity;
import android.app.Service;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;

import java.io.IOException;

/**
 * @author Administrator
 * 
 */
public class ChatRecordManager implements OnTouchListener {

	private View recordBtn;
	private Activity context;
	private ImageView amplitudeIv;
	private ImageView im_record_mic;
	private ImageView iv_im_cancel_record;
	private TextView progressTitle;
	private TextView tv_im_record;
	private RelativeLayout rl_im_record;
	private PopupWindow recordPopupWindow;
	private long startTime;
	private String fileName;
	protected boolean isRecording;
	protected int count;
//	private SpeexRecorder recorderInstance;
	private boolean cancelSend;
	private boolean isShowNum;
	private OnSendListener onSendListener;
	private Handler micRefreshHandler;
	private Handler handlerRefreshPg;
    private Handler handlerDelay;
	private Runnable refreshPgTask;
	private MediaRecorder mRecorder;
	private static boolean SPEEX = false;

	private int mCurrentMotionY = 0;
	private int mOldMotionY = 0;
	private int mCurrentMotionX = 0;
	private int mOldMotionX = 0;

	private Vibrator mVibrator;

	public ChatRecordManager(View startBtn, Activity activity) {
		this.recordBtn = startBtn;
		this.context = activity;
		init();
	}

	private void init() {
		Display dp = context.getWindowManager().getDefaultDisplay();
		recordBtn.setOnTouchListener(this);
		View contentView = LayoutInflater.from(context).inflate(
				R.layout.im_chat_record, null);
		amplitudeIv = (ImageView) contentView
				.findViewById(R.id.im_chat_record_img);
		im_record_mic = (ImageView) contentView
				.findViewById(R.id.im_record_mic);
		progressTitle = (TextView) contentView
				.findViewById(R.id.record_seconds);
		tv_im_record = (TextView) contentView.findViewById(R.id.tv_im_record);
		rl_im_record = (RelativeLayout) contentView
				.findViewById(R.id.rl_im_record);
		iv_im_cancel_record = (ImageView) contentView
				.findViewById(R.id.iv_im_cancel_record);
		recordPopupWindow = new PopupWindow(contentView, dp.getWidth() / 2,
				dp.getWidth() / 2);
		micRefreshHandler = new Handler();
		handlerRefreshPg = new Handler();
        handlerDelay = new Handler();
	}

	/**
	 * 是否使用speex 压缩录音文件；默认为 amr
	 * 
	 * @param b
	 */
	public void setSpeexRecord(boolean b) {
		SPEEX = b;
	}

	public void onRecording() {
		AudioLoader.getInstance().stopCurrentPlaying();
//		if (SPEEX) {
//			startSpxRecord();
//		} else {
			startMediaRecord();
//		}
		startRefreshMic();
		count = 0;
		isRecording = true;
		if (!recordPopupWindow.isShowing()) {
			recordPopupWindow.showAtLocation(
					context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
		}
        im_record_mic.setImageResource(R.drawable.im_record_mic);
		refreshPgTask = new Runnable() {
			@Override
			public void run() {
				count++;
				if (count > 60 || !isRecording) {
					isRecording = false;
					return;
				}
				progressTitle.setText(60 - count + "\"");
				if (count == 50) {
					mVibrator = (Vibrator) context
							.getSystemService(Service.VIBRATOR_SERVICE);
					mVibrator.vibrate(new long[] { 100, 10, 100, 1000 }, -1);
					if (!isShowNum) {
						tv_im_record.setText("后结束录音");
						progressTitle.setVisibility(View.VISIBLE);
					}
				} else if (count == 60) {
					if (isSend) {
						isSend = false;
						int l = stopRecord();
						if (onSendListener != null&&AudioLoader.getFilePath(fileName).length()>20) {
							onSendListener.onSend(
									AudioLoader.getFilePath(fileName)
											.getAbsolutePath(), l);
						}
					}
				}
				handlerRefreshPg.postDelayed(this, 1000);
			}
		};
		handlerRefreshPg.postDelayed(refreshPgTask, 1000);
	}

	// private boolean isStart = true;
	private void startMediaRecord() {
		// isStart = true;
		fileName = FileNameGenerate.generateId("amr");
		try {
			if (mRecorder == null) {
				mRecorder = new MediaRecorder();
			} else {
				mRecorder.stop();
				mRecorder.release();
				mRecorder= null;
				mRecorder = new MediaRecorder();
			}
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			mRecorder.setOutputFile(AudioLoader.getFilePath(fileName)
					.getAbsolutePath());
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// isStart = false;
			if (recordPopupWindow.isShowing()) {
				recordPopupWindow.dismiss();
			}
			e.printStackTrace();
		}

		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					mRecorder.start();
				}
			}).start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

//	private void startSpxRecord() {
//		fileName = FileNameGenerate.generateId();
//		recorderInstance = new SpeexRecorder(context, fileName);
//		Thread th = new Thread(recorderInstance);
//		th.start();
//		recorderInstance.setRecording(true);
//	}

	private void startRefreshMic() {
		micRefreshHandler.postDelayed(micLoopAni, 500);
	}

	private Runnable micLoopAni = new Runnable() {

		private int aniCount;

		@Override
		public void run() {
			switch (aniCount % 3) {
			case 0:
				amplitudeIv.setImageResource(R.drawable.recording_1);
				break;
			case 1:
				amplitudeIv.setImageResource(R.drawable.recording_2);
				break;
			case 2:
				amplitudeIv.setImageResource(R.drawable.recording_3);
				break;
			}

			aniCount++;
			if (isRecording) {
				micRefreshHandler.postDelayed(this, 300);
			}
		}
	};

	private Runnable micAmpRefresh = new Runnable() {
		@Override
		public void run() {
			if (mRecorder != null) {
				int amplitude;
				int level;
//				if (SPEEX) {
//					amplitude = recorderInstance.getMaxAMP();
//					level = amplitude / 2000;
//				} else {
					amplitude = mRecorder.getMaxAmplitude();
					level = amplitude / 1000;
//				}
				switch (level % 3) {
				case 0:
					amplitudeIv.setImageResource(R.drawable.recording_1);
					break;
				case 1:
					amplitudeIv.setImageResource(R.drawable.recording_2);
					break;
				case 2:
					amplitudeIv.setImageResource(R.drawable.recording_3);
					break;
				default:
					amplitudeIv.setImageResource(R.drawable.recording_1);
					break;
				}

			}

			if (isRecording) {
				micRefreshHandler.postDelayed(this, 100);
			}
		}
	};

	private int stopRecord() {
		int s = 0;
		isRecording = false;
		if (recordPopupWindow.isShowing()) {
			recordPopupWindow.dismiss();
		}
		if (micAmpRefresh != null && micRefreshHandler != null) {
			micRefreshHandler.removeCallbacks(micAmpRefresh);
		}
		if (handlerRefreshPg != null && refreshPgTask != null) {
			handlerRefreshPg.removeCallbacks(refreshPgTask);
		}
		progressTitle.setText("60秒");
		s = Math.min(count, 60);
//		if (SPEEX) {
//			stopSpxRecord();
//		} else {
			stopMediaRecord();
//		}
		return s;
	}

	private void stopMediaRecord() {

		if (mRecorder == null) {
			return;
		}
		try {
            mRecorder.setOnErrorListener(null);
			mRecorder.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mRecorder.release();
		mRecorder = null;
	}

	private void showRecordTooShort() {
        if (!recordPopupWindow.isShowing()) {
            recordPopupWindow.showAtLocation(
                    context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        }
        im_record_mic.setImageResource(R.drawable.record_too_short_icon);
        amplitudeIv.setVisibility(View.GONE);
        tv_im_record.setText(R.string.record_too_short);

        Handler handler = new Handler();
        handlerDelay.postDelayed(mhandlerDelay, 500);
	}

    private Runnable mhandlerDelay = new Runnable() {

        private int aniCount;

        @Override
        public void run() {
            if (recordPopupWindow.isShowing()) {
                recordPopupWindow.dismiss();
            }
        }
    };

//	private void stopSpxRecord() {
//		if (recorderInstance != null) {
//			recorderInstance.setRecording(false);
//		}
//	}

	private boolean isSend = true;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startTime = System.currentTimeMillis();
			cancelSend = false;
			mOldMotionY = (int) event.getY();
			mOldMotionX = (int) event.getX();
			iv_im_cancel_record.setVisibility(View.GONE);
			tv_im_record.setText("手指上滑取消发送");
			progressTitle.setVisibility(View.GONE);
			amplitudeIv.setVisibility(View.VISIBLE);
			im_record_mic.setVisibility(View.VISIBLE);
			isSend = true;
			cancelSend = false;
			onRecording();
			break;
		case MotionEvent.ACTION_UP:
			if (isSend) {
				isSend = false;
				int l = stopRecord();
				if (!cancelSend) {
					if (l <= 1||AudioLoader.getFilePath(fileName).length()<20) {
						showRecordTooShort();
					} else {
						if (this.onSendListener != null) {
							onSendListener.onSend(
									AudioLoader.getFilePath(fileName)
											.getAbsolutePath(), l);
						}
					}
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			mCurrentMotionY = (int) event.getY();
			mCurrentMotionX = (int) event.getX();
			if (mCurrentMotionY < mOldMotionY
					&& Math.abs(mCurrentMotionX - mOldMotionX) < Math
							.abs(mCurrentMotionY - mOldMotionY)) {
				if (Math.abs(mOldMotionY) > 350) {
					iv_im_cancel_record.setVisibility(View.VISIBLE);
					progressTitle.setVisibility(View.GONE);
					tv_im_record.setText("松开手指，取消发送");
					amplitudeIv.setVisibility(View.INVISIBLE);
					im_record_mic.setVisibility(View.INVISIBLE);
					isShowNum = true;
					cancelSend = true;
				}
			} else if (mCurrentMotionY > mOldMotionY
					&& Math.abs(mCurrentMotionX - mOldMotionX) < Math
							.abs(mCurrentMotionY - mOldMotionY)) {
				if (Math.abs(mOldMotionY) < 350) {
					iv_im_cancel_record.setVisibility(View.GONE);
					amplitudeIv.setVisibility(View.VISIBLE);
					im_record_mic.setVisibility(View.VISIBLE);
					if (count >= 50 && count < 60) {
						progressTitle.setVisibility(View.VISIBLE);
						tv_im_record.setText("后结束录音");
					} else {
						progressTitle.setVisibility(View.GONE);
						tv_im_record.setText("手指上滑取消发送");
					}
					isShowNum = false;
					cancelSend = false;
				}
			}
			mOldMotionY = (int) event.getY();
			mOldMotionX = (int) event.getX();
			break;
			case MotionEvent.ACTION_CANCEL:
				stopRecord();
				break;
		default:
			break;
		}
		return false;
	}

	public void setOnSendListener(OnSendListener l) {
		this.onSendListener = l;
	}

	public interface OnSendListener {
		public void onSend(String file, int voiceLength);
	}
}
