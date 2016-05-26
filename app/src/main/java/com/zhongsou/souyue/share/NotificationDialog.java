package com.zhongsou.souyue.share;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.utils.IntentUtil;

import java.util.Timer;
import java.util.TimerTask;



public class NotificationDialog extends Dialog implements android.view.View.OnClickListener{

    private Context mcontext;
    private TextView notification_layout;
    private LinearLayout linear_layout;
    private int statusHeight;
    private final Timer timer ;
    private TimerTask timerTask;
    
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(NotificationDialog.this!=null&&NotificationDialog.this.isShowing()){
                NotificationDialog.this.dismiss();
            }
        }
    };
    
    public NotificationDialog(Context context,int statusHeight) {
        super(context,R.style.Dialog_Notifition);
        this.mcontext=context;
        this.statusHeight=statusHeight;
        timer= new Timer();
        setOwnerActivity((Activity)context);
    }
    
    @Override
    public void dismiss() {
        super.dismiss();
        if(timerTask!=null){
            timerTask.cancel();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getContentView();
        initView();
    }

    private void getContentView(){
        this.setContentView(R.layout.notification_layout);
    }
    private void initView(){
        linear_layout=(LinearLayout) findViewById(R.id.linear_layout);
        notification_layout=(TextView) findViewById(R.id.notifition_text);
        linear_layout.setOnClickListener(this);
        timerTask=new TimerTask() {
            
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };
    }
  
    public void setTextString(String textString){
        notification_layout.setText(textString);
    }
    public void showTopDialog() {
        Window window = this.getWindow();
        //设置显示动画
        window.setWindowAnimations(R.style.share_menu_up_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0; //设置x坐标
        wl.y =  statusHeight;//设置y坐标
        //控制dialog停放位置
        window.setAttributes(wl); 
        window.setGravity(Gravity.TOP);
        //设置显示位置
        this.onWindowAttributesChanged(wl);
        //设置点击外围解散
        this.setCanceledOnTouchOutside(true);
        //最终决定dialog的大小,实际由contentView确定了
        this.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        this.show();
        timer.schedule(timerTask,2000);
    }
    @Override
    public void onClick(View paramView) {
        NotificationDialog.this.dismiss();
        IntentUtil.openMainActivity(this.getContext(), new int[] { 1 });
    }
    
}


 