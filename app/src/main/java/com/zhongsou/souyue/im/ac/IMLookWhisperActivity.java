package com.zhongsou.souyue.im.ac;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.emoji.EmojiPattern;
import com.zhongsou.souyue.im.view.RoundProgressBar;
import com.zhongsou.souyue.module.ChatMsgEntity;
/**
 * 
 * @ClassName: IMLookWhisperActivity 
 * @Description: 查看密信 
 * @author gengsong@zhongsou.com
 * @date 2014年7月19日 下午1:23:15 
 * @version 3.9
 */
public class IMLookWhisperActivity extends IMBaseActivity implements OnClickListener {

    private TextView chat_pop_text;
    private ChatMsgEntity intentData;
    private RoundProgressBar mRoundProgressBar;
    private long timeProgress;
    public static long maxProgress = 10;
    private Handler handler;
    private int position;
    private boolean isReceive;
    private LinearLayout im_chat_text_pop;

    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.im_chat_whisper_pop_layout);
        handler=new Handler();
        intentData = (ChatMsgEntity) getIntent().getSerializableExtra("chatMsgEntity");
        position = getIntent().getIntExtra("position", 0);
        timeProgress = intentData.getTimerLength();
        isReceive=intentData.isComMsg();
//        PushService.setIsInChat(MainApplication.getInstance(), true);
        initView();
        if(timeProgress>0){
            handler.postDelayed(runnable, 1000);
        }
    }
    
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if(timeProgress > 0){
                --timeProgress;
                mRoundProgressBar.setProgress(timeProgress);
                
            }else{
                handler.removeCallbacks(runnable);
                Intent data = new Intent(IMLookWhisperActivity.this,IMChatActivity.class);
                data.putExtra("chatMsgEntity", intentData);
                setResult(RESULT_OK, data);
                IMLookWhisperActivity.this.finish();
            }
            //要做的事情
            handler.postDelayed(this, 1000);
        }
    };
    
    private void initView() {
        chat_pop_text = (TextView) this.findViewById(R.id.chat_pop_text);
        chat_pop_text.setMovementMethod(ScrollingMovementMethod.getInstance());
        im_chat_text_pop = (LinearLayout) this.findViewById(R.id.im_chat_text_pop);
        im_chat_text_pop.setOnClickListener(this);
        mRoundProgressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);
        if(intentData.getText().length()>ChatMsgEntity.RECEIVEMAXLENGTH) {
            if(!intentData.isComMsg()) {
                maxProgress = 10;
            } else {
                maxProgress = ChatMsgEntity.RECEIVEMAXLENGTH;
            }
        } else if(intentData.getText().length()>10) {
            if(!intentData.isComMsg()) {
                maxProgress = 10;
            } else {
                maxProgress = intentData.getText().length();
            }
        } else {
        	maxProgress = 10;
        }
        
        if(timeProgress>0) {
        	mRoundProgressBar.setMax((int)maxProgress);
            mRoundProgressBar.setProgress(timeProgress);
        }
        if(!TextUtils.isEmpty(intentData.getText())) {
             SpannableString spannableString = EmojiPattern.getInstace().getExpressionString(this, intentData.getText());
            chat_pop_text.setText(spannableString);
        }
    }

    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键 
            setBackData();
            onBackPressed();
            return true; 
        }     
        return false; 
    }

	@Override
	public void onClick(View arg0) {
		setBackData();
		switch (arg0.getId()) {
		case R.id.im_chat_text_pop:
			this.finish();
			break;

		default:
			break;
		}
	}

	private void setBackData(){
	    Intent data = new Intent(IMLookWhisperActivity.this,IMChatActivity.class);
        data.putExtra("timeLength", timeProgress);
        data.putExtra("isReceive", isReceive);
        data.putExtra("chatMsgEntity", intentData);
        setResult(RESULT_OK,data);
	}

    @Override
    protected void onDestroy() {
//    	PushService.setIsInChat(MainApplication.getInstance(), false);
    	super.onDestroy();
    }

    public void deleteWhisperEvent(View view) {
        Intent data = new Intent(IMLookWhisperActivity.this,IMChatActivity.class);
        data.putExtra("chatMsgEntity", intentData);
        data.putExtra("delete", 1);
        setResult(RESULT_OK,data);
        finish();
    }
    
}
