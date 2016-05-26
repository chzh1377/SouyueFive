package com.zhongsou.souyue.im.transfile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.ac.IMBaseActivity;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyLoadResponse;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by xyh0125 on 15/11/10.
 */
public class TestActivity extends IMBaseActivity implements IVolleyResponse,IVolleyLoadResponse {
//public class TestActivity extends IMBaseActivity{
    public static final String BROADCAST_DOWNLOAD_FILE_TAG = "com.zhongsou.souyue.im.transfile";
    public static final String BROADCAST_DOWNLOAD_FILE = "com.zhongsou.souyue.im.transfile";

    private LinearLayout ll_base_view ;
    private Button btn_start;
    private Button btn_stop;
    private ProgressBar my_progressBar;
    private UpdateReceiver receiver;
    private TextView filePercent;

    //test
    private CMainHttp cMainHttp ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_file_download_test_view);
        initView();
        cMainHttp = CMainHttp.getInstance();
        registReceiver();
    }

    private void registReceiver() {
        receiver = new UpdateReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_DOWNLOAD_FILE_TAG);
        registerReceiver(receiver, filter);

    }

    private void initView() {
        ll_base_view = (LinearLayout) findViewById(R.id.ll_im_search_view);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        my_progressBar = (ProgressBar)findViewById(R.id.my_progressBar);
        filePercent = (TextView)findViewById(R.id.im_download_file_percent);


        setClickListener();
    }

    private void setClickListener() {
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //test
//                FileDownloadInfo info = new FileDownloadInfo();
//                info.setUrl("http://192.168.31.52/doujia_3.37m.exe");
//                FileDownloadService.addItemToQueue(TestActivity.this,info);

                cMainHttp.doDownload(101, FileDownloadService.getSavePath(), "http://souyueim.b0.upaiyun.com/file/201511/25/0654301o473vrtl9ykpvi7/aaaaaaaaaaa.JPG", TestActivity.this, TestActivity.this);

            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                FileDownloadService.stopThread(TestActivity.this);
                cMainHttp.cancelDownload(101);

            }
        });
    }

    @Override
    public void onHttpProcess(long _totle_length, long _cur_length) {
        int downloadPercent = 0;
        downloadPercent = (int) (_cur_length*100/_totle_length);
        Log.i("ImDownload","Testactivity==》onHttpProcess——_totle_length："+_totle_length);
        Log.i("ImDownload","Testactivity==》onHttpProcess——_cur_length："+_cur_length);
        Log.i("ImDownload","Testactivity==》onHttpProcess——downloadPercent："+downloadPercent);
        //        filePercent.setText("onHttpProcess:"+downloadPercent+"%");
        my_progressBar.setProgress(downloadPercent);
    }

    private class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            int what = bundle.getInt("what");
            Object obj = bundle.get("obj");

            Message message = Message.obtain();
            message.what = what;
            message.obj = obj;
            mHandler.sendMessage(message);
        }
    }

    /**
     * 利用消息处理机制适时更新进度条,非UI线程执行操作
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                FileDownloadInfo item = (FileDownloadInfo) msg.obj;
                updateProgress(item);
            }
//            else if (msg.what == 2) {
//                FileDownloadInfo item = (FileDownloadInfo) msg.obj;
//                updateStateChange(item);
//            }
        }
    };

    private void updateProgress(final FileDownloadInfo item) {

        if (my_progressBar == null) {
            return;
        }
        int downloadPercent = 0;
        downloadPercent = item.getCurLength()*100/item.getLength();
        filePercent.setText("updateProgress:"+downloadPercent+"%");
        my_progressBar.setProgress(downloadPercent);
        Log.i("ImDownload","Testactivity==》updateProgress——downloadPercent："+downloadPercent);

    }


}
