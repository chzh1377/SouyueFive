package com.zhongsou.souyue.im.log;

import android.os.AsyncTask;
import android.util.Log;
import com.upyun.api.IUpYunConfig;
import com.upyun.api.Uploader;
import com.upyun.api.utils.UpYunException;
import com.upyun.api.utils.UpYunUtils;
import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.utils.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Random;

/**
 * Created by zcz on 2015/5/14.
 */
public class UploadLogTask extends AsyncTask<Void, Void, String> implements IUpYunConfig,DontObfuscateInterface {
    private SimpleDateFormat formatDate = new SimpleDateFormat(
            "/yy/MM/dd/hh");
    private Random r = new Random();
    private File file;
    private Object mCallbackHandler;

    public UploadLogTask(Object callbackHandler, File file) {
        this.mCallbackHandler = callbackHandler;
        this.file = file;
    }

    public String getSaveKey() {
        return "/im_log/" + file.getName();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String policy = UpYunUtils.makePolicy(getSaveKey(), Uploader.getExpiration(), BUCKET_LOG);
            String signature = UpYunUtils.signature(policy + "&"
                    + API_LOG_KEY);
            return Uploader.upload(policy, signature, UPDATE_HOST
                    + BUCKET_LOG, file);
        } catch (UpYunException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String url) {
        if (StringUtils.isNotEmpty(url)) {
            System.out.println("------------->上传成功 url = " + url);
            file.delete();
//            m.setType(12);
//            String voiceUrl = IUpYunConfig.HOST_LOG + url;
//            m.setText(getJson(voiceUrl, voiceLength));
//            m.setVoiceUrl(voiceUrl);
//            sendVoice(m);
            // Uploader.invokeMethod(callbackHandler,
            // "uploadSuccess",IUpYunConfig.HOST_VOICE + url);
        } else {
            Log.i("UploadLogTask.onPostExecute","日志文件上传失败");
            // m.setText(getJson(file.getPath(), voiceLength));
            // m.setFailed();
            // Uploader.invokeMethod(callbackHandler,
            // "uploadFaild","--uploadFaild--");
        }
    }
}
