package com.zhongsou.souyue.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.tuita.sdk.im.db.module.Contact;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.model.PersonPageParam;
import com.zhongsou.souyue.im.ac.WebImLoginActivity;
import com.zhongsou.souyue.im.util.IMApi;
import com.zhongsou.souyue.im.util.IMIntentUtil;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.discover.QrcodeRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.qrdecoding.CameraManager;
import com.zhongsou.souyue.qrdecoding.CaptureActivityHandler;
import com.zhongsou.souyue.qrdecoding.InactivityTimer;
import com.zhongsou.souyue.qrdecoding.ViewfinderView;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.ZSEncode;

import java.io.IOException;
import java.util.Vector;

public class ScaningActivity extends BaseActivity implements Callback {

     public static final String UID = "uid";
    private static final int REQ_CHOOSE = 1;

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 2.0f;
    private static final long DURATION = 3;
    private boolean vibrate;
    private Context myContext;

    // 外在的view
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private CameraManager cameraManager;
    private TextView line;
    private boolean isNeedInitResume = true;
    private String scaningResult;
    private TextView scan_qr_nonet;
//    private Http http;
    private CMainHttp mainHttp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_scaning);
        myContext = this;
        CameraManager.init(this);
//        http = new Http(this);
        mainHttp = CMainHttp.getInstance();
        cameraManager = CameraManager.get();
        initView();
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    /**
     * 本地选择二维码图片解析事件
     */
    public void btnScaning_SelectImg(View view) {
//        handler.sendEmptyMessage(1000);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        mContext.startActivityForResult(intent, REQ_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQ_CHOOSE) {
//                if (handler == null) {
//                    handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
//                }
                Uri uri = data == null || resultCode != RESULT_OK ? null : data
                        .getData();
                if (uri != null) {
//                    String[] proj = {MediaStore.Images.Media.DATA};
//                    Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
//                    int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                    actualimagecursor.moveToFirst();
//                    String img_path = actualimagecursor.getString(actual_image_column_index);
                    new CaptureActivityHandler(this, decodeFormats, characterSet).obtainMessage(R.id.selectimg_to_decoding, uri).sendToTarget();
                } else return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(mContext, "请选择有效的二维码照片", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        surfaceHolder = surfaceView.getHolder();
        scan_qr_nonet=(TextView) findViewById(R.id.scan_qr_nonet);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        if(!CMainHttp.getInstance().isNetworkAvailable(mContext)){
            scan_qr_nonet.setVisibility(View.VISIBLE);
        }else{
            scan_qr_nonet.setVisibility(View.GONE);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(ScaningActivity.this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;
        new Thread(new Runnable() {

            @Override
            public void run() {
                playBeep = true;
                AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
                if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                    playBeep = false;
                }
                initBeepSound();
                vibrate = true;
            }
        }).start();
    }


    @Override
    protected void onPause() {
//        isNeedInitResume = true;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (handler != null) {
                    handler.quitSynchronously();
                    handler = null;
                }
//                hasSurface = false;
                cameraManager.closeDriver();
//                if (!hasSurface) {
//                    surfaceHolder.removeCallback(ScaningActivity.this);
//                }
            }
        }, 200);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        viewfinderView.recycle();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
            }
        } catch (IOException ioe) {
            SouYueToast.makeText(this, "相机打开失败", Toast.LENGTH_SHORT).show();
            finish();
            ioe.printStackTrace();
            return;
        } catch (RuntimeException e) {
            SouYueToast.makeText(this, "相机打开失败", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    public void handleDecode(Result obj, Bitmap barcode) {
        inactivityTimer.onActivity();
        // viewfinderView.drawResultBitmap(barcode);
        playBeepSoundAndVibrate();
        getScaningResult(obj.getText());
    }

    public void decodeImgFailed() {
        Toast.makeText(this, "未发现二维码", Toast.LENGTH_LONG).show();
    }

    public void decodeNoNet() {

        if(CMainHttp.getInstance().isNetworkAvailable(mContext)){
            scan_qr_nonet.setVisibility(View.GONE);
        }else{
            scan_qr_nonet.setVisibility(View.VISIBLE);
        }
        viewfinderView.invalidate();
    }

    /**
     * 获取结果并且结束
     *
     * @param result
     */
    public void getScaningResult(final String result) {
        String s = StringUtils.decodeURL(result);
        if (!StringUtils.isEmpty(result)) {
            if (s.startsWith("http://souyue.mobi/")) {
                String iid = getParam("iid", s);
                String uid = getParam("uid", s);
                String key = getParam("k", s);
                String i = getParam("i", s);
                String t = getParam("t", s);
                if (t != null) {
                    if ("i".equals(t) && i != null) {
                        try {
                            IntentUtil.gotoSecretCricleCard(this, Long.valueOf(i));
                            finish();
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if ("userim".equals(t) && uid != null) {
                        try {
                            if (IntentUtil.isLogin()) {
//                                IMIntentUtil.gotoIMFriendInfoSca(this, Long.valueOf(uid));
                                Contact c = new Contact();
                                c.setChat_id(Long.valueOf(uid));
                                IMApi.IMGotoShowPersonPage(mContext, c, PersonPageParam.FROM_IM);
                                finish();
                                return;
                            } else {
                                IntentUtil.goLogin(this, true);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if ("group".equals(t) && uid != null && iid != null) {
                        try {
                            if (IntentUtil.isLogin()) {
                                IMIntentUtil.gotoGroupInfoActivity(this, Long.valueOf(uid), Long.valueOf(iid));
                                finish();
                                return;
                            } else {
                                IntentUtil.goLogin(this, true);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if("webim".equals(t)){//webIM登录
                        String uuid = getParam("uuid", s);
                        String url = getParam("url",s);
                        if (IntentUtil.isLogin()) {
                            if(uuid!=null){
                                //跳转到扫描结果界面
                                WebImLoginActivity.invoke(this,uuid,url);
                            }else{
                                SouYueToast.makeText(this,getString(R.string.webim_qrcode_infoerror),SouYueToast.LENGTH_LONG).show();
                            }
                        } else {
                            IntentUtil.goLogin(this, true);
                        }
                        finish();
                        return;
                    }
                }
                if (key != null) {
                    Intent intent = new Intent(ScaningActivity.this, SRPActivity.class);
                    intent.putExtra("keyword", (String) key);
                    if (i != null) {
                        intent.putExtra("srpId", (String) i);
                    }
                    intent.putExtra("is_scan", true);
                    ScaningActivity.this.startActivity(intent);
                    finish();
                    return;
                }
            }
            if (s.startsWith(UrlConfig.HOST_YOUBAO_SCANING)) {
                IntentUtil.gotoWeb(this, UrlConfig.youbao + "?&qr=" + ZSEncode.encodeURI(s), "youbao");
                return;
            }
            /*
             * 暂时注释，没有找到使用地方
             * if (!s.startsWith(UrlConfig.HOST_YOUBAO_SCANING))  {
            	IntentUtil.toOpenNoTitleForUrl(mContext, ZSEncode.encodeURI(s), null);
            	return;
            }*/

//         请求接口
            scaningResult = result;

            if (CMainHttp.getInstance().isNetworkAvailable(mContext)) {
//                http.Qrcode_Web(result);
                QrcodeRequest request = new QrcodeRequest(HttpCommon.QRCODE_REQUEST,this);
                request.setParams(result);
                mainHttp.doRequest(request);
            } else {
                startScanResult(result);
            }
        } else {
            Toast.makeText(myContext, "扫描失败！", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String getParam(String string, String result) {
        if (result.indexOf(string + "=") <= 0) {
            return null;
        }
        int start = result.indexOf(string + "=") + string.length() + 1;
        int end = result.indexOf("&", start);
        if (end > 0) {
            return result.subSequence(start, end).toString();
        } else {
            return result.subSequence(start, result.length()).toString();
        }
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    public void onGoBackClick(View view) {
        this.finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

//    public void Qrcode_WebSuccess(HttpJsonResponse bs, AjaxStatus status) {
//        JsonObject js = bs.getBody();
//        String jumpType = js.get("type").getAsString();
//        String jumpUrl = js.get("jump").getAsString();
//        if (StringUtils.isNotEmpty(jumpType) && jumpType.equals("blank")) {
//            startScanResult(jumpUrl);
//        } else {
//            IntentUtil.gotoWeb(ScaningActivity.this, jumpUrl, "interactWeb");
//        }
//    }

    private void startScanResult(String result) {
        Intent intent = new Intent(ScaningActivity.this, ScanResultActivity.class);
        intent.putExtra("content", result);
        ScaningActivity.this.startActivity(intent);
    }

    //  服务器返回失败，跳转扫描失败结果页
//    @Override
//    public void onHttpError(String methodName) {
//
//    }

    @Override
    public void onHttpResponse(IRequest _request) {
        HttpJsonResponse response = (HttpJsonResponse) _request.getResponse();

        switch (_request.getmId()) {
            case HttpCommon.QRCODE_REQUEST: // 删除
                JsonObject js = response.getBody();
                String jumpType = js.get("type").getAsString();
                String jumpUrl = js.get("jump").getAsString();
                if (StringUtils.isNotEmpty(jumpType) && jumpType.equals("blank")) {
                    startScanResult(jumpUrl);
                } else {
                    IntentUtil.gotoWeb(ScaningActivity.this, jumpUrl, "interactWeb");
                }
                break;
     }
    }

    @Override
    public void onHttpError(IRequest request) {
        if (StringUtils.isNotEmpty(scaningResult)) {
            startScanResult(scaningResult);
            scaningResult = null;
        }
    }
}
