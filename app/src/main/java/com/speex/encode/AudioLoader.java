package com.speex.encode;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
//import com.speex.encode.SpeexDecoder.OnCompeleteListener;
import com.zhongsou.souyue.utils.SYSharedPreferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioLoader {

    public static final int LOAD_ONLY = 12;
    private static final int LOAD_ONLY_ADD_PLAY = -121;

    public static final int LOAD_AND_PLAY = 11;
    private static final int LOAD_AND_NOT_PLAY = 110;
    private static ExecutorService mAudioLoaderES;
    private static AudioLoader audioLoader;
    private static AudioLoaderConfigrition audioLoderConfig;
    private static AtomicBoolean pause = new AtomicBoolean(false);
    private static File cacheDir;
    private static Map<String, LoadLock> loadingTask = Collections.synchronizedMap(new HashMap<String, LoadLock>());
    private Handler handler = new Handler();
    private static Context mContext;

    public static void init(Context context, AudioLoaderConfigrition config) {

        if (config != null) {
            audioLoderConfig = config;
        } else {
            init();
        }
        mContext = context;
        cacheDir = getCacheDirectory(context.getApplicationContext());
    }

    private static void init() {
        audioLoderConfig = new AudioLoaderConfigrition();
    }

    private AudioLoaderListener audioLoaderListener;
//    private static SpeexPlayer splayer;
    private boolean curPlaying;
    private View curPlayingView;
    private String curPlayingFile;
    private AudioPlayListener curPlayingListener;
    private MediaPlayer mediaPlayer;

    private void checkThreadPool() {
        if (mAudioLoaderES == null || mAudioLoaderES.isShutdown()) {
            mAudioLoaderES = Executors.newFixedThreadPool(audioLoderConfig.threadNums);
        }
    }

    public static AudioLoader getInstance() {
        if (audioLoader == null) {
            audioLoader = new AudioLoader();
        }
        return audioLoader;
    }

    public void display(final String url, final View view, final AudioPlayListener l) {

        if (curPlaying) { // 播放器停止当前的播放
            stopCurrentPlaying();
            if (checkCurPlaying(url)) { // 如果当前的url和准备播放的url相同则停止操作，否则将开启新的播放
                curPlayingFile = null;
                return;
            }
        }

        curPlayingListener = l;
        curPlayingFile = url;

        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (!isExist(url)) {
            if (!loadingTask.containsKey(url)) {
                loadAndPlay(url, l, view);
            } else {
                LoadLock lock = loadingTask.get(url);
                if (lock.refrence == LOAD_AND_PLAY) { // 当时加载并播放的状态时，点击切换状态
                    l.onDisplayingEnd(0, 0, view);
                    lock.refrence = LOAD_AND_NOT_PLAY;

                } else if (lock.refrence == LOAD_AND_NOT_PLAY) {
                    l.onDisplayingEnd(0, 0, view);
                    lock.refrence = LOAD_AND_PLAY;
                } else if (lock.refrence == LOAD_ONLY) { // 当是加载状态是，点击切换状态
                    lock.refrence = LOAD_ONLY_ADD_PLAY;
                    l.onDisplayPreparing(view);
                    startPlayTask(lock, url, view, l);
                } else if (lock.refrence == LOAD_ONLY_ADD_PLAY) {
                    lock.refrence = LOAD_ONLY;
                    l.onDisplayingEnd(0, 0, view);
                }
            }
        } else {
            play(getFilePath(url), l, view);
        }
    }


    private void startPlayTask(final LoadLock lock, final String url, final View view, final AudioPlayListener l) {
        new Thread() {
            public void run() {
                synchronized (lock) {
                    if (lock.refrence == LOAD_ONLY_ADD_PLAY) {
                        handler.post(new Runnable() {
                            public void run() {
                                play(getFilePath(url), l, view);
                            }

                            ;
                        });

                        loadingTask.remove(url);
                    }
                }
            }
        }.start();

    }

    private void loadAndPlay(final String url, final AudioPlayListener l, final View view) {
        l.onDisplayPreparing(view);
        loadAudio(url, view, new SimpleAudioLoaderListener() {
            @Override
            public void onLoadingFaild() {
                l.onDisplayingEnd(0, 0, view);
            }

            @Override
            public void onLoadingComplete(long totalSize) {
                LoadLock lock = loadingTask.get(url);
                if (lock.refrence == LOAD_AND_PLAY) {
                    play(getFilePath(url), l, view);
                }
            }
        }, LOAD_AND_PLAY);
    }

    protected void play(File filePath, final AudioPlayListener l, final View view) {
        if (filePath == null) {
            return;
        }
        curPlayingView = view;

//        if (isSpeexFile(filePath)) {
//            speexPlay(l, filePath, view);
//        } else {
            meadiaPlay(l, filePath, view);
//        }
        l.onDisplayingStart(0, 0, view);
    }

    private void meadiaPlay(final AudioPlayListener l, File filePath, final View view) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(filePath.getAbsolutePath());
            AudioManager am = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
            if(SYSharedPreferences.getInstance().getBoolean("showIcon",false)){
                am.setMode(AudioManager.MODE_IN_CALL);
                am.setSpeakerphoneOn(false);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            }else{
                am.setMode(AudioManager.MODE_NORMAL);
                am.setSpeakerphoneOn(true);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            curPlaying = true;
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    curPlaying = false;
                    l.onDisplayingEnd(0, 0, view);
                }
            });
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        } catch (Exception e){
            curPlaying = false;
            l.onDisplayingEnd(0, 0, view);
        }

    }

//    private void speexPlay(final AudioPlayListener l, File filePath, final View view) {
//        splayer = new SpeexPlayer(filePath.getAbsolutePath());
//        splayer.startPlay();
//        curPlaying = true;
//        splayer.getDecoder().setOnComPeleteListener(new OnCompeleteListener() {
//
//            @Override
//            public void onCompelete() {
//                curPlaying = false;
//                l.onDisplayingEnd(0, 0, view);
//            }
//        });
//    }

//    private boolean isSpeexFile(File src) {
//        RandomAccessFile file = null;
//        try {
//            file = new RandomAccessFile(src, "r");
//            byte[] header = new byte[2048];
//            file.read(header, 0, 27);
//            /* how many segments are there? */
//            int segments = header[26] & 0xFF;
//            file.readFully(header, 27, segments);
//            byte[] bodybytes = new byte[8];
//            file.readFully(bodybytes, 0, 8);
//            String s = new String(bodybytes, 0, 8);
//            return "Speex   ".equals(s);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (file != null) {
//                try {
//                    file.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return false;
//    }

    public void stopCurrentPlaying() {
        curPlaying = false;
        if (curPlayingListener != null) {
            curPlayingListener.onDisplayingEnd(0, 0, curPlayingView);
        }
//        if (splayer != null) {
//            splayer.stopPlay();
//        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        curPlayingListener = null;
    }

    private boolean checkCurPlaying(String filePath) {

        return (curPlayingFile != null && curPlayingFile.equals(filePath));
    }

    protected long caculateTime(long totalSize) {
        return totalSize / 2000;
    }

    public void loadAudio(String url, View view) {
        loadAudio(url, view, null);
    }

    public void loadAudio(String url, View view, AudioLoaderListener l) {
        loadAudio(url, view, l, LOAD_ONLY);
    }

    private void loadAudio(String url, View view, AudioLoaderListener l, int state) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (isExist(url)) {
            return;
        }
        audioLoaderListener = l;
        if (audioLoaderListener == null) {
            audioLoaderListener = new SimpleAudioLoaderListener();
        }
        LoadLock lock = new LoadLock();
        lock.refrence = state;
        loadingTask.put(url, lock);
        DownLoadInfo info = new DownLoadInfo(new Handler(), url, view, audioLoaderListener, cacheDir, lock);
        checkThreadPool();
        mAudioLoaderES.execute(new DownloadTask(info, loadingTask));
    }

    public void loadAudio(String url) {
        loadAudio(url, null, null);
    }

    /**
     * �жϱ����Ƿ����
     *
     * @param name url �ļ��� ���ļ�·��
     * @return
     */
    public boolean isExist(String name) {
        if (FileNameGenerate.isHttpUrl(name)) {
            name = FileNameGenerate.getName(name);
        }
        if (new File(name).exists()) {
            return true;
        }
        return new File(cacheDir, name).exists();
    }

    /**
     * @param url http url
     * @return
     */
    public static File getFilePath(String url) {
        if (FileNameGenerate.isHttpUrl(url)) {
            url = FileNameGenerate.getName(url);
        }
        File file = new File(url);
        if (file.exists()) {
            return file;
        }
        return new File(cacheDir, url);
    }

    private static final String INDIVIDUAL_DIR_NAME = "uil-audio";

    public static File getCacheDirectory(Context context) {
//        File appCacheDir = null;
        final String cachePath = context.getExternalCacheDir() != null ? context
                .getExternalCacheDir().getAbsolutePath() : context
                .getCacheDir().getPath();

        try {
            new File(cachePath, ".nomedia").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new File(cachePath + File.separator );
//        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
//            appCacheDir = getExternalCacheDir(context);
//        }
//        if (appCacheDir == null) {
//            appCacheDir = context.getCacheDir();
//        }
//        return appCacheDir;
    }

    public static String generateFilePath(Context context, String name) {
        return getCacheDirectory(context).getAbsolutePath() + File.separator + name;
    }

    public static File getIndividualCacheDirectory(Context context) {
        File cacheDir = getCacheDirectory(context);
        File individualCacheDir = new File(cacheDir, INDIVIDUAL_DIR_NAME);
        if (!individualCacheDir.exists()) {
            if (!individualCacheDir.mkdir()) {
                individualCacheDir = cacheDir;
            }
        }
        return individualCacheDir;
    }

    public static File getOwnCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            try {
                new File(dataDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!appCacheDir.mkdirs()) {
                return null;
            }
        }
        return appCacheDir;
    }

}
