package com.zhongsou.souyue.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.upyun.api.UploadVoiceTask;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.CommentaryAdapter;
import com.zhongsou.souyue.adapter.CommentaryAdapter.ReplyListener;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.dialog.SYProgressDialog;
import com.zhongsou.souyue.module.Comment;
import com.zhongsou.souyue.module.CommentList;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.SelfCreateItem;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.detail.AddCommentReq;
import com.zhongsou.souyue.net.detail.CommentListReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.LongPressedButon;
import com.zhongsou.souyue.ui.LongPressedButon.onRepeatListener;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.ProgressBarHelper.ProgressBarClickListener;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.ui.keystatus.KeyboardListenRelativeLayout;
import com.zhongsou.souyue.ui.keystatus.KeyboardListenRelativeLayout.IOnKeyboardStateChangedListener;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.FileUtils;
import com.zhongsou.souyue.utils.SYInputMethodManager;
import com.zhongsou.souyue.utils.SYMediaplayer;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @description: 添加的注释，这个类在 我的原创 评论详情中
 * @auther: qubian 加注释
 * @data: 2015/12/12.
 */

public class CommentaryActivity extends RightSwipeActivity implements OnCancelListener, OnClickListener, OnItemClickListener, onRepeatListener, ReplyListener, LoadingDataListener {

    private PullToRefreshListView pullToRefreshListView;
    private CommentaryAdapter commentAD;
    private RelativeLayout commentary_say_layout;
    private LinearLayout commentary_text_layout;
    private ImageButton say_text;
    private LongPressedButon sayBt;
    private EditText comment_replay_text;
    private String token;
    //
    private List<String> images;
    //
    private ImageView amplitudeIv;
    private ProgressBar progress;
    private TextView progressTitle;
    private PopupWindow recordPopupWindow;
    private int voice_length;
    private String tempFilename;
    private SYMediaplayer au;
    private MediaRecorder mRecorder;
    private Task task;
    private Button delAudio;
    private ImageButton imgPlay;
    private RelativeLayout playDelLayout, reply_layout;
    private TextView no_commentary;
    private TextView replay_to_nick;
    private static final int RETURN_AUDIO_TOO_SHORT = -1;
    private static final int DIALOG_SDCARD_NOT_EXIST = 11;
    private static final int TEXT_MAX = 4000;
    private int count = 0;

    private Handler handler = new Handler();
    private String url, keyword, title;
    private SYProgressDialog sydialog;
    private String temp;
    private String time;
    private KeyboardListenRelativeLayout keystatus;
    private String srpId;
    private String callback;
    // add by trade
    private boolean isH5Widget = false;  //H5微件评论成功后回调接口
    private String h5CallBackUrl;      //H5微件评论回调接口

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commentary);
        if (sysp == null) sysp = SYSharedPreferences.getInstance();
        View v = findViewById(R.id.ll_data_loading);
        this.setCanRightSwipe(true);
        au = SYMediaplayer.getInstance(this);
        pbHelp = new ProgressBarHelper(this, v);
        pbHelp.setProgressBarClickListener(new ProgressBarClickListener() {
            @Override
            public void clickRefresh() {
//                h.commentList(url, 0);
                getCommentList(HttpCommon.DETAIL_COMMENT_LSIST_ID, url, 0);
            }
        });
        sydialog = new SYProgressDialog(this, 0, getResources().getString(R.string.comment_sending));
        sydialog.setOnCancelListener(this);
        token = SYUserManager.getInstance().getToken();
        initFromIntent();
        initView();
        initCommentModel();
        commentAD = new CommentaryAdapter(this);
        commentAD.setLoadingDataListener(this);
        commentAD.setReplyListener(this);
        pullToRefreshListView.setAdapter(commentAD);
//        h.commentList(this.url, 0);
        getCommentList(HttpCommon.DETAIL_COMMENT_LSIST_ID, this.url, 0);
        pullToRefreshListView.setOnItemClickListener(this);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//                h.commentListToPullDownRefresh(url, 0);
                getCommentList(HttpCommon.DETAIL_COMMENT_LSIST_PULLDOWN_ID, url, 0);
            }

        });
        pullToRefreshListView.setOnTimeRefreshListener(new PullToRefreshBase.OnTimeRefreshListener() {
            @Override
            public void onTimeRefresh() {
                if (time != null && pullToRefreshListView != null)
                    pullToRefreshListView.onUpdateTime(StringUtils.convertDate(time));

            }
        });
        ((TextView) findViewById(R.id.activity_bar_title)).setText(getString(R.string.commentary_title));

    }

    /**
     * 获取列表数据
     *
     * @param url
     * @param commentLastId
     */
    public void getCommentList(int id, String url, long commentLastId) {
        CommentListReq req = new CommentListReq(id, this);
        req.setParams(url, commentLastId);
        CMainHttp.getInstance().doRequest(req);
    }

    public void initFromIntent() {
        Intent intent = this.getIntent();
        SearchResultItem sri = null;
        SelfCreateItem sci = null;
        if (intent != null) {
            sri = (SearchResultItem) intent.getSerializableExtra("searchResultItem");
            sci = (SelfCreateItem) intent.getSerializableExtra("selfCreateItem");
        }
        if (sri != null) {
            this.url = exchange(sri.url());
            this.keyword = sri.keyword();
            this.title = sri.title();
            this.srpId = sri.srpId();
            this.callback = sri.callback();
        }
        if (sci != null) {
            this.url = exchange(sci.url());
            this.keyword = sci.keyword();
            this.title = sci.title();
            this.srpId = sci.srpId();
        }
        isH5Widget = intent.getBooleanExtra("isH5Widget", false);
        h5CallBackUrl = intent.getStringExtra("h5CallBackUrl");
    }

    private String exchange(String sourceUrl) {
        if (sourceUrl.endsWith("#extractnone"))
            sourceUrl = sourceUrl.replaceAll("#extractnone", "");
        return sourceUrl;
    }

    @Override
    protected void onPause() {
        au.stopPlayAudio();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.audio_play:
                au.play((ImageButton) v, SYMediaplayer.SOURCE_TYPE_LOC);
                break;
            case R.id.audio_del:
                au.stopPlayAudio();
                if (tempFilename != null) FileUtils.retireFile(tempFilename);
                playDelLayout.setVisibility(View.GONE);
                sayBt.setVisibility(View.VISIBLE);
            default:
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    private void initView() {
        keystatus = findView(R.id.keystatus);
        keystatus.setOnKeyboardStateChangedListener(new IOnKeyboardStateChangedListener() {
            @Override
            public void onKeyboardStateChanged(int state) {
//                SYInputMethodManager.status = state;
//                Log.w("", "jianpan : " + state);
            }
        });
        no_commentary = (TextView) findViewById(R.id.commentary_null);
        reply_layout = (RelativeLayout) findViewById(R.id.reply_layout);
        replay_to_nick = (TextView) findViewById(R.id.replay_to_nick);
        commentary_say_layout = (RelativeLayout) findViewById(R.id.commentary_say_layout);
        commentary_text_layout = (LinearLayout) findViewById(R.id.commentary_text_layout);
        playDelLayout = (RelativeLayout) findViewById(R.id.audio_play_del_layout);
        delAudio = (Button) findViewById(R.id.audio_del);
        say_text = (ImageButton) findViewById(R.id.say_text);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.commentary_list);
        comment_replay_text = (EditText) findViewById(R.id.comment_replay_text);
        comment_replay_text.addTextChangedListener(new MaxLengthWatcher());
//        comment_replay_text.setText(MainApplication.inputContents);
        imgPlay = (ImageButton) findViewById(R.id.audio_play);
        imgPlay.setOnClickListener(this);
        sayBt = (LongPressedButon) findViewById(R.id.longClickToSay);
        sayBt.setLongClickable(true);
        sayBt.setOnRepeatListener(this);
        delAudio.setOnClickListener(this);
        View contentView = getLayoutInflater().inflate(R.layout.chat_progressview, null);
        amplitudeIv = (ImageView) contentView.findViewById(R.id.amplitude);
        progress = (ProgressBar) contentView.findViewById(R.id.progress);
        progressTitle = (TextView) contentView.findViewById(R.id.progresstitle);
        recordPopupWindow = new PopupWindow(contentView, Utils.getScreenWidth(this) / 2, Utils.getScreenHeight(this) / 3);
    }

    public void loadDataMore(long start, String type) {
//        h.commentListToLoadMore(this.url, start);
        getCommentList(HttpCommon.DETAIL_COMMENT_LSIST_LOADMORE_ID, this.url, start);
    }

    public void commentListToLoadMoreSuccess(CommentList comment) {
        commentAD.hasMoreItems = comment.hasMore();
        pullToRefreshListView.onRefreshComplete();
        commentAD.addDatas(comment.comments());
        commentAD.notifyDataSetChanged();
    }

    public void commentListToPullDownRefreshSuccess(CommentList comment) {
        time = new Date().getTime() + "";
        commentAD.hasMoreItems = comment.hasMore();
        pullToRefreshListView.onRefreshComplete();
        if (comment.comments().size() > 0) {
            no_commentary.setVisibility(View.GONE);
        }
        commentAD.seatData(comment.comments());
        commentAD.notifyDataSetChanged();
    }


    public void commentAddSuccess() {
        if (sydialog != null && sydialog.isShowing())
            sydialog.dismiss();
        SouYueToast.makeText(this, R.string.commentary_success, SouYueToast.LENGTH_SHORT).show();

    }

    public void commentAddSuccess(Comment comment) {

        findViewById(R.id.send).setClickable(true);
        if (comment != null) {
            commentAD.insertData(comment);
            commentAD.notifyDataSetChanged();
        }
        count++;
        SouYueToast.makeText(this, R.string.commentary_success, SouYueToast.LENGTH_SHORT).show();

//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        if (pullToRefreshListView != null)
            pullToRefreshListView.setVisibility(View.VISIBLE);
        no_commentary.setVisibility(View.GONE);
        if (sydialog != null && sydialog.isShowing()) sydialog.dismiss();
//        MainApplication.inputContents = "";
        comment_replay_text.setText("");
    }

    public void commentListSuccess(CommentList comment) {
        time = new Date().getTime() + "";
        pbHelp.goneLoading();
        if (comment.comments().size() == 0) {
            no_commentary.setVisibility(View.VISIBLE);
            return;
        }
        commentAD.addDatas(comment.comments());
        commentAD.hasMoreItems = comment.hasMore();
        commentAD.notifyDataSetChanged();
    }


    public void uploadSuccess(String voiceUrl) {
        findViewById(R.id.send_audio).setClickable(true);
        if (playDelLayout != null) playDelLayout.setVisibility(View.GONE);
        if (sayBt != null) sayBt.setVisibility(View.VISIBLE);
//        h.commentAdd(token, keyword, url, voiceUrl, voice_length,
//                      replyCom == null ? 0 : replyCom.id(), title, srpId);
        loadaddComment(token, keyword, url, voiceUrl, voice_length, null,
                replyCom == null ? 0 : replyCom.id(), title, srpId);
    }

    /**
     * 加载网络 ---详情页--添加评论
     *
     * @param token
     * @param keyword
     * @param url
     * @param voiceUrl
     * @param voiceLength
     * @param content
     * @param replyToId
     * @param title
     * @param srpId
     */
    private void loadaddComment(String token, String keyword, String url, String voiceUrl,
                                int voiceLength, String content, long replyToId, String title, String srpId) {
        AddCommentReq req = new AddCommentReq(HttpCommon.DETAIL_COMMENT_ADD_ID, this);
        req.setParams(token, keyword, url, voiceUrl, voiceLength, content,
                replyToId, title, srpId);
        CMainHttp.getInstance().doRequest(req);
    }

    public void onHttpResponse(IRequest _request) {
        switch (_request.getmId()) {
            case HttpCommon.DETAIL_COMMENT_ADD_ID:
                Comment comment = new Gson().fromJson(_request.<HttpJsonResponse>getResponse().getBody(),
                        Comment.class);
                // 统计 评论
                UpEventAgent.onNewsComment(MainApplication.getInstance(), "",
                        comment.keyword(), comment.srpId(), comment.title(),
                        comment.url());
                commentAddSuccess(comment);
                break;
            case HttpCommon.DETAIL_COMMENT_LSIST_ID:
                commentListSuccess(new CommentList(_request.<HttpJsonResponse>getResponse()));
                break;
            case HttpCommon.DETAIL_COMMENT_LSIST_PULLDOWN_ID:
                commentListToPullDownRefreshSuccess(new CommentList(_request.<HttpJsonResponse>getResponse()));
                break;
            case HttpCommon.DETAIL_COMMENT_LSIST_LOADMORE_ID:
                commentListToLoadMoreSuccess(new CommentList(_request.<HttpJsonResponse>getResponse()));
                break;
        }

    }

    public void onHttpError(IRequest _request) {
        findViewById(R.id.send).setClickable(true);
        findViewById(R.id.send_audio).setClickable(true);
        pullToRefreshListView.onRefreshComplete();
        switch (_request.getmId()) {
            case HttpCommon.DETAIL_COMMENT_ADD_ID:
                temp = null;
                if (sydialog != null && sydialog.isShowing()) sydialog.dismiss();
                if (_request.getVolleyError().getErrorCode() != 200) {
                    SouYueToast.makeText(this, R.string.commentart_fail, SouYueToast.LENGTH_SHORT).show();
                }
                break;
            case HttpCommon.DETAIL_COMMENT_LSIST_ID:
                pbHelp.showNetError();
                commentAD.isMetNetworkError = true;
                break;
        }

    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus as) {
//        findViewById(R.id.send).setClickable(true);
//        findViewById(R.id.send_audio).setClickable(true);
//        pullToRefreshListView.onRefreshComplete();
//        if ("commentList".equals(methodName)) {
//            pbHelp.showNetError();
//            commentAD.isMetNetworkError = true;
//        }
//        if ("commentAdd".equals(methodName)) {
//            temp = null;
//            if (sydialog != null && sydialog.isShowing()) sydialog.dismiss();
//            if (as.getCode() != 200) {
//                SouYueToast.makeText(this, R.string.commentart_fail, SouYueToast.LENGTH_SHORT).show();
//            }
//        }
//    }

    public void onButtonClick(View view) {
        if (commentary_text_layout == null || commentary_say_layout == null) return;
        if (commentary_say_layout.isShown()) {
            showSayBtn();
        } else {
            showTextBtn();
        }

    }

    /**
     * 显示 切换为语音的按钮
     */
    private void showSayBtn() {
        if (comment_replay_text != null) {
            sysp.putBoolean(SYSharedPreferences.KEY_INPUT_MODEL, false);
            new SYInputMethodManager(this).showSoftInput();
            comment_replay_text.requestFocus();
        }
        commentary_text_layout.setVisibility(View.VISIBLE);
        commentary_say_layout.setVisibility(View.GONE);
        say_text.setImageDrawable(getResources().getDrawable(R.drawable.say_button_selector));
    }

    /**
     * 显示 切换为文字输入按钮
     */
    private void showTextBtn() {
        sysp.putBoolean(SYSharedPreferences.KEY_INPUT_MODEL, true);
        new SYInputMethodManager(this).hideSoftInput();
        commentary_text_layout.setVisibility(View.GONE);
        commentary_say_layout.setVisibility(View.VISIBLE);
        say_text.setImageDrawable(getResources().getDrawable(R.drawable.text_button_selector));
        if (au != null) au.stopPlayAudio();
    }

    /**
     * 文本是true
     */
    private void initCommentModel() {
        if (sysp == null) return;
        if (sysp.getBoolean(SYSharedPreferences.KEY_INPUT_MODEL, true)) {
            showTextBtn();
        } else {
            showSayBtn();
        }
    }

    private Comment replyCom;

    /**
     * 发送按钮
     * @param view
     */
    public void onSendButtonClick(View view) {
        String text = comment_replay_text.getText().toString();
        if (!StringUtils.isEmpty(text) && text.length() <= TEXT_MAX) {
            if (!CMainHttp.getInstance().isNetworkAvailable(this)) { //无网状态
                SouYueToast.makeText(this, R.string.nonetworkerror, SouYueToast.LENGTH_SHORT).show();
                return;
            }
            if (null != temp && temp.equals(text)) {
                // 提示用户不能连续发送相同内容
                SouYueToast.makeText(this, R.string.commentart_repeat_send, SouYueToast.LENGTH_SHORT).show();
                return;
            }
//            h.commentAdd(token, keyword, url, temp = text,
//            		replyCom == null ? 0 : replyCom.id(), title, srpId);
            loadaddComment(token, keyword, url, null, 0, temp = text,
                    replyCom == null ? 0 : replyCom.id(), title, srpId);

            view.setClickable(false);
            // newComment(false, text);
            if (sydialog != null) sydialog.show();
        } else if (StringUtils.isEmpty(text)) {
            SouYueToast.makeText(this, R.string.content_no_null, SouYueToast.LENGTH_SHORT).show();
        } else {
            SouYueToast.makeText(this, R.string.content_more_than_1000, SouYueToast.LENGTH_SHORT).show();
        }
//        new SYInputMethodManager(this).hideSoftInput();
    }

    public void onSendAudioButtonClick(View view) {
        String audioFileName = genAudioFileName();
        File file = null;
        if(audioFileName != null){
            file = new File(audioFileName);
        }
        if (file == null || file.length() <= 0) return;
//        h.uploadVoice(this, token, file);
        UploadVoiceTask t = new UploadVoiceTask(this, token, file);
		t.execute();
        view.setClickable(false);
        if (sydialog != null) sydialog.show();

    }

    public void onCanelReplyClick(View view) {
        if (reply_layout != null) {
            replyCom = null;
            reply_layout.setVisibility(View.GONE);
        }
    }

    @Override
    public void reply(Comment to) {
        if (null != comment_replay_text && comment_replay_text.isShown()) {
            comment_replay_text.requestFocus();
            new SYInputMethodManager(this).showSoftInput();
        }
        reply_layout.setVisibility(View.VISIBLE);
        replay_to_nick.setText(getString(R.string.reply_to) + ":" + to.user().name());
        replyCom = to;
    }

    private void startRecording() {
        if (!Utils.isSDCardExist()) {
            showDialog(DIALOG_SDCARD_NOT_EXIST);
            return;
        }
        tempFilename = genAudioFileName();
        if (tempFilename == null) {
            return;
        }
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        mRecorder.setOutputFile(tempFilename);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        if (task == null) {
            task = new Task();
        }
        task.isStop = false;
        handler.post(task);
        try {
            mRecorder.prepare();
            Log.i("", "mRecorder prepare");
        } catch (IOException e) {
            Log.i("", "mRecorder prepare " + e.getMessage());
        }

        try {
            mRecorder.start();
            Log.i("", "mRecorder start");
            isStart = true;
        } catch (RuntimeException e) {
            isStart = false;
            Log.i("", "mRecorder start" + e.getMessage());
        }
    }

    private boolean isStart = false;

    private void stopRecording() {
        try {
            if (mRecorder == null) {
                return;
            }
            task.isStop = true;
            if (isStart) {
                mRecorder.stop();
            }
            Log.i("", "mRecorder stop");
            mRecorder.reset();
            Log.i("", "mRecorder reset");
        } catch (Exception e) {
            Log.i("", "mRecorder stop + reset " + e.getMessage());
        } finally {
            isStart = false;
            mRecorder.release();
            Log.i("", "mRecorder release");
            mRecorder = null;
        }
    }

//    public void cancelRecord() {
//        if (recordPopupWindow.isShowing()) {
//            recordPopupWindow.dismiss();
//            progress.setProgress(0);
//            tempFilename = null;
//            stopRecording();
//        }
//    }

    private String genAudioFileName() {
        if (Utils.isSDCardExist()) {
            StringBuffer filename = new StringBuffer();
            File fileDir = FileUtils.createDir(Environment.getExternalStorageDirectory() + "/souyue/file/");
            filename.append(fileDir.toString() + "/");
            filename.append("sytemp__");
            return filename.toString();
        }
        return null;
    }

    class Task implements Runnable {

        public boolean isStop;

        @Override
        public void run() {
            if (mRecorder != null) {
                int amplitude = mRecorder.getMaxAmplitude();
                int level = amplitude / 1000;
                switch (level) {
                    case 0:
                        amplitudeIv.setImageResource(R.drawable.chat_voice01);
                        break;
                    case 1:
                        amplitudeIv.setImageResource(R.drawable.chat_voice02);
                        break;
                    case 2:
                        amplitudeIv.setImageResource(R.drawable.chat_voice03);
                        break;
                    case 3:
                        amplitudeIv.setImageResource(R.drawable.chat_voice04);
                        break;
                    case 4:
                        amplitudeIv.setImageResource(R.drawable.chat_voice05);
                        break;
                    default:
                        amplitudeIv.setImageResource(R.drawable.chat_voice05);
                        break;
                }

            }

            if (!isStop) handler.postDelayed(this, 100);
        }
    }

    private void sendAudio() {
        if (tempFilename == null) {
            return;
        }
        playDelLayout.setVisibility(View.VISIBLE);
        sayBt.setVisibility(View.GONE);
    }

    @Override
    public void onRepeat(boolean isEnd, int count) {
        Log.i("", "voice_length count = " + count);
        if (au != null) au.stopPlayAudio();
        if (!isEnd) {
            if (!recordPopupWindow.isShowing()) {
                startRecording();
                recordPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
            }
            progress.setProgress(count);
            progressTitle.setText(count + "秒");
        } else {
            if (recordPopupWindow.isShowing()) {
                recordPopupWindow.dismiss();
                progress.setProgress(0);
                voice_length = (count >= 60000 ? 60000 : count);
                Log.i("", "mRecorder = " + voice_length);
                Log.i("", "mRecorder end count = " + count);
                stopRecording();
                if (count <= 1) {
                    handler.sendEmptyMessage(RETURN_AUDIO_TOO_SHORT);
                } else {
                    sendAudio();
                }
            }
        }
    }

    @Override
    public void finishAnimation(Activity activity) {
        Intent i = new Intent();
        i.putExtra("comment_count", count);
        i.putExtra("jsCallback", callback);
        this.setResult(ConstantsUtils.START_FOR_RESULT, i);
        super.finishAnimation(activity);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        finishAnimation(this);
    }

    class MaxLengthWatcher implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            MainApplication.inputContents = s.toString();
        }

    }
}
