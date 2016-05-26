package com.zhongsou.souyue.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.upyun.api.UploadVoiceTask;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.LoadingDataListener;
import com.zhongsou.souyue.adapter.ChatCommentaryAdapter;
import com.zhongsou.souyue.adapter.ChatCommentaryAdapter.ReplyListener;
import com.zhongsou.souyue.dialog.SYProgressDialog;
import com.zhongsou.souyue.module.Comment;
import com.zhongsou.souyue.module.CommentList;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.net.other.ChatRoomAddRequest;
import com.zhongsou.souyue.net.other.ChatRoomListRequest;
import com.zhongsou.souyue.net.other.ChatRoomLoadMoreRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
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

@SuppressLint("ValidFragment")
public class ChatRoomFragment extends SRPFragment implements OnCancelListener, OnClickListener,
        OnItemClickListener, onRepeatListener, IVolleyResponse, ReplyListener, LoadingDataListener {
    private PullToRefreshListView pullToRefreshListView;
    private ChatCommentaryAdapter commentAD;
    private RelativeLayout commentary_say_layout;
    private LinearLayout commentary_text_layout;
    private ImageButton say_text;
    private LongPressedButon sayBt;
    private EditText comment_replay_text;

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
//    private Http h;
    private CMainHttp mainHttp;
    private String url, keyword, title;
    private SYProgressDialog sydialog;
    private String temp;
    private String time;
    private KeyboardListenRelativeLayout keystatus;
    private String srpId;
    private View rootView;
    private SYSharedPreferences sysp;
    private ProgressBarHelper pbHelp;
    protected String curLastId = "0";

    public ChatRoomFragment(SearchResultItem sri) {
        this.url = sri.url();
        this.keyword = sri.keyword();
        this.title = sri.title();
        this.srpId = sri.srpId();
    }

    public ChatRoomFragment() {
        super();
    }

    public void setType(String type) {
        super.type = type;
    }

    public void setKeyWord(String keyWord) {
        super.keyWord = keyWord;
    }

    public void setSrpid(String srpId) {
        super.srpId = srpId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.chat_commentary, null);
        if (sysp == null) sysp = SYSharedPreferences.getInstance();
        View v = findViewById(R.id.ll_data_loading);
        initFromIntent();
//        h = new Http(this);
        mainHttp = CMainHttp.getInstance();
        v.setVisibility(View.GONE);
        au = SYMediaplayer.getInstance(getActivity());
        pbHelp = new ProgressBarHelper(getActivity(), v);
        pbHelp.setProgressBarClickListener(new ProgressBarClickListener() {
            @Override
            public void clickRefresh() {
//                h.chatRoomList(srpId, curLastId);
                pullToRefreshListView.startRefresh();
            }
        });
        sydialog = new SYProgressDialog(getActivity(), 0, getResources().getString(R.string.chat_sending));
        sydialog.setOnCancelListener(this);

        initView();
        initCommentModel();
        commentAD = new ChatCommentaryAdapter(getActivity());
        commentAD.setLoadingDataListener(this);
        commentAD.setReplyListener(this);
        pullToRefreshListView.setAdapter(commentAD);
        pullToRefreshListView.setOnItemClickListener(this);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                curLastId = "0";
//                h.chatRoomRefreshList(srpId, curLastId);
                ChatRoomListRequest listRequest = new ChatRoomListRequest(HttpCommon.CHATROOMLIST_REQUEST,ChatRoomFragment.this);
                listRequest.setParams(srpId, curLastId);
                mainHttp.doRequest(listRequest);
            }
        });
        pullToRefreshListView.setOnTimeRefreshListener(new PullToRefreshBase.OnTimeRefreshListener() {
            @Override
            public void onTimeRefresh() {
                if (time != null) pullToRefreshListView.onUpdateTime(StringUtils.convertDate(time));

            }
        });
        ((TextView) findViewById(R.id.activity_bar_title)).setText(getString(R.string.commentary_title));
        rootView.clearFocus();
        return rootView;
    }

    public void initFromIntent() {
        Intent intent = getActivity().getIntent();
        SearchResultItem sri = null;
        if (intent != null)
            sri = (SearchResultItem) intent
                    .getSerializableExtra("searchResultItem");
        if (sri != null) {
            this.url = sri.url();
            this.keyword = sri.keyword();
            this.title = sri.title();
            this.srpId = sri.srpId();
        }
    }

    private View findViewById(int llDataLoading) {
        return rootView.findViewById(llDataLoading);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        pullToRefreshListView.startRefresh();
    }

    @Override
    public void onPause() {
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
            case R.id.say_text:
                onButtonClick(v);
                break;
            case R.id.send_audio:
                onSendAudioButtonClick(v);
                break;
            case R.id.commentary_onCanelReplyClick:
                onCanelReplyClick(v);
                break;
            case R.id.send:
                onSendButtonClick(v);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
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
                /*if(showTitle){
                    SYInputMethodManager.status = state;
                    Log.w("", "jianpan : " + state);
                }*/
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
        say_text.setOnClickListener(this);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.commentary_list);
        comment_replay_text = (EditText) findViewById(R.id.comment_replay_text);
        comment_replay_text.addTextChangedListener(new MaxLengthWatcher());
        comment_replay_text.setText(MainApplication.inputContents);
        comment_replay_text.clearFocus();
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
        recordPopupWindow = new PopupWindow(contentView, Utils.getScreenWidth(getActivity()) / 2, Utils.getScreenHeight(getActivity()) / 3);
        findView(R.id.send_audio).setOnClickListener(this);
        findView(R.id.commentary_onCanelReplyClick).setOnClickListener(this);
        findView(R.id.send).setOnClickListener(this);
        findView(R.id.commentary_title_layout).setVisibility(View.GONE);
    }

    private LayoutInflater getLayoutInflater() {
        return getActivity().getLayoutInflater();
    }

    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

    public void loadDataMore(long start, String type) {
//        h.chatRoomListLoadMore(this.srpId, curLastId);
        ChatRoomLoadMoreRequest loadMoreRequest = new ChatRoomLoadMoreRequest(HttpCommon.CHATROOMLOADMORE_REQUEST,this);
        loadMoreRequest.setParams(this.srpId, curLastId);
        mainHttp.doRequest(loadMoreRequest);
    }

    public void commentListToLoadMoreSuccess(CommentList comment) {
        commentAD.hasMoreItems = comment.hasMore();
        updateLastId(comment);
        pullToRefreshListView.onRefreshComplete();
        commentAD.addDatas(comment.comments());
        commentAD.notifyDataSetChanged();
    }

//    public void commentListToPullDownRefreshSuccess(CommentList comment, AjaxStatus as) {
//        pbHelp.goneLoading();
//        pullToRefreshListView.onRefreshComplete();
//        updateLastId(comment);
//        time = as.getTime().getTime() + "";
//        commentAD.hasMoreItems = comment.hasMore();
//        pullToRefreshListView.onRefreshComplete();
//        if (comment.comments().size() > 0) {
//            no_commentary.setVisibility(View.GONE);
//        } else {
//            no_commentary.setVisibility(View.VISIBLE);
//        }
//        commentAD.seatData(comment.comments());
//        commentAD.notifyDataSetChanged();
//    }

    /*public void commentListSuccess(CommentList comment, AjaxStatus as) {
        time = as.getTime().getTime() + "";
        pbHelp.goneLoading();
        if (comment.comments().size() == 0) {
            no_commentary.setVisibility(View.VISIBLE);
            return;
        }
        updateLastId(comment);
        commentAD.addDatas(comment.comments());
        commentAD.hasMoreItems = comment.hasMore();
        commentAD.notifyDataSetChanged();
    }*/

    private void updateLastId(CommentList comment) {
        if (comment.comments() != null && comment.comments().size() > 0) {
            curLastId = comment.comments().get(comment.comments().size() - 1).id() + "";
        }
    }

    public void commentAddSuccess(Comment comment) {
        findViewById(R.id.send).setClickable(true);
        if (comment != null) {
            commentAD.insertData(comment);
            commentAD.notifyDataSetChanged();
        }
        count++;
//        SouYueToast.makeText(getActivity(), R.string.commentary_success, SouYueToast.LENGTH_SHORT).show();
        pullToRefreshListView.setVisibility(View.VISIBLE);
        no_commentary.setVisibility(View.GONE);
        if (sydialog != null && sydialog.isShowing()) sydialog.dismiss();
        MainApplication.inputContents = "";
        comment_replay_text.setText("");
    }

    public void uploadSuccess(String voiceUrl) {
        findViewById(R.id.send_audio).setClickable(true);
        if (playDelLayout != null) playDelLayout.setVisibility(View.GONE);
        if (sayBt != null) sayBt.setVisibility(View.VISIBLE);
//        h.chatRoomAdd(SYUserManager.getInstance().getToken(), keyword, url, voiceUrl, voice_length, replyCom == null ? 0 : replyCom.id(), title, srpId);
        ChatRoomAddRequest addRequest = new ChatRoomAddRequest(HttpCommon.CHATROOMADD_REQUEST,this);
        addRequest.setParams(SYUserManager.getInstance().getToken(), keyword, url, voiceUrl, voice_length, null, replyCom == null ? 0 : replyCom.id(), title, srpId);
        mainHttp.doRequest(addRequest);
    }

    public void uploadFaild(String str) {
        findViewById(R.id.send_audio).setClickable(true);
        if (sydialog != null && sydialog.isShowing()) sydialog.dismiss();
        SouYueToast.makeText(getActivity(), R.string.chat_fail, SouYueToast.LENGTH_SHORT).show();
    }


//    @Override
//    public void onHttpError(String methodName, AjaxStatus as) {
//        findViewById(R.id.send).setClickable(true);
//        findViewById(R.id.send_audio).setClickable(true);
//        pullToRefreshListView.onRefreshComplete();
//        if ("commentList".equals(methodName)) {
//            pbHelp.showNetError();
//            commentAD.isMetNetworkError = true;
//        } else if ("commentAdd".equals(methodName)) {
//            temp = null;
//            if (sydialog != null && sydialog.isShowing()) sydialog.dismiss();
//            SouYueToast.makeText(getActivity(), R.string.chat_fail, SouYueToast.LENGTH_SHORT).show();
//        } else if ("commentListToPullDownRefresh".equals(methodName)) {
//            pbHelp.showNetError();
//        }
//
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
            new SYInputMethodManager(getActivity()).showSoftInput();
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
        new SYInputMethodManager(getActivity()).hideSoftInput();
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

    public void onSendButtonClick(View view) {
        String text = comment_replay_text.getText().toString();
        if (!StringUtils.isEmpty(text) && text.length() <= TEXT_MAX) {
            /*if (null != temp && temp.equals(text)) {
                // 提示用户不能连续发送相同内容
                SouYueToast.makeText(getActivity(), R.string.commentart_repeat_send, SouYueToast.LENGTH_SHORT).show();
                return;
            }*/
//            h.chatRoomAdd(SYUserManager.getInstance().getToken(), keyword, url, temp = text, replyCom == null ? 0 : replyCom.id(), title, srpId);
            ChatRoomAddRequest addRequest = new ChatRoomAddRequest(HttpCommon.CHATROOMADD_REQUEST,this);
            addRequest.setParams(SYUserManager.getInstance().getToken(), keyword, url,null,0, temp = text, replyCom == null ? 0 : replyCom.id(), title, srpId);
            mainHttp.doRequest(addRequest);
            view.setClickable(false);
            // newComment(false, text);
            if (sydialog != null) sydialog.show();
        } else if (StringUtils.isEmpty(text)) {
            SouYueToast.makeText(getActivity(), R.string.content_no_null, SouYueToast.LENGTH_SHORT).show();
        } else {
            SouYueToast.makeText(getActivity(), R.string.content_more_than_1000, SouYueToast.LENGTH_SHORT).show();
        }
//        new SYInputMethodManager(getActivity()).hideSoftInput();
    }

    public void onSendAudioButtonClick(View view) {
        File file = new File(genAudioFileName());
        if (file.length() <= 0) return;
        UploadVoiceTask t = new UploadVoiceTask(this, SYUserManager.getInstance().getToken(), file);
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
            new SYInputMethodManager(getActivity()).showSoftInput();
        }
        reply_layout.setVisibility(View.VISIBLE);
        replay_to_nick.setText(getString(R.string.reply_to) + ":" + to.user().name());
        replyCom = to;
    }

    private void startRecording() {
        if (!Utils.isSDCardExist()) {
            getActivity().showDialog(DIALOG_SDCARD_NOT_EXIST);
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
        } catch (IOException e) {
        }

        try {
            mRecorder.start();
        } catch (RuntimeException e) {
        }
    }

    private void stopRecording() {

        if (mRecorder == null) {
            return;
        }
        task.isStop = true;
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public void cancelRecord() {
        if (recordPopupWindow.isShowing()) {
            recordPopupWindow.dismiss();
            progress.setProgress(0);
            tempFilename = null;
            stopRecording();
        }
    }

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

    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()){
            case HttpCommon.CHATROOMLIST_REQUEST:
                CommentList comment = request.getResponse();
                commentAD.hasMoreItems = comment.hasMore();
                updateLastId(comment);
                pullToRefreshListView.onRefreshComplete();
                commentAD.addDatas(comment.comments());
                commentAD.notifyDataSetChanged();
                break;
            case HttpCommon.CHATROOMLOADMORE_REQUEST:
                CommentList commentLoadMore = request.getResponse();
                commentAD.hasMoreItems = commentLoadMore.hasMore();
                updateLastId(commentLoadMore);
                pullToRefreshListView.onRefreshComplete();
                commentAD.addDatas(commentLoadMore.comments());
                commentAD.notifyDataSetChanged();
                break;
            case HttpCommon.CHATROOMADD_REQUEST:
                Comment commentAdd=request.getResponse();
                findViewById(R.id.send).setClickable(true);
                if (commentAdd != null) {
                    commentAD.insertData(commentAdd);
                    commentAD.notifyDataSetChanged();
                }
                count++;
//        SouYueToast.makeText(getActivity(), R.string.commentary_success, SouYueToast.LENGTH_SHORT).show();
                pullToRefreshListView.setVisibility(View.VISIBLE);
                no_commentary.setVisibility(View.GONE);
                if (sydialog != null && sydialog.isShowing()) sydialog.dismiss();
                MainApplication.inputContents = "";
                comment_replay_text.setText("");
                break;
        }
    }

    @Override
    public void onHttpError(IRequest request) {

    }

    @Override
    public void onHttpStart(IRequest request) {

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
        if (au != null) au.stopPlayAudio();
        if (!isEnd) {
            if (!recordPopupWindow.isShowing()) {
                recordPopupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                startRecording();
            }
            int temC = (int) count / 1000;
            Log.d("temC ---> count : ", temC + "/" + count);
            progress.setProgress(count);
            progressTitle.setText(count + "秒");
        } else {
            if (recordPopupWindow.isShowing()) {
                recordPopupWindow.dismiss();
                progress.setProgress(0);
                voice_length = (count >= 60000 ? 60000 : count);
                stopRecording();
                if (count <= 1) {
                    handler.sendEmptyMessage(RETURN_AUDIO_TOO_SHORT);
                } else {
                    sendAudio();
                }
            }
        }
    }

    public void finishAnimation(Activity activity) {
        Intent i = new Intent();
        i.putExtra("comment_count", count);
        getActivity().setResult(ConstantsUtils.START_FOR_RESULT, i);
        activity.finish(); //
        activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        findViewById(R.id.send).setClickable(true);
        findViewById(R.id.send_audio).setClickable(true);
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
            MainApplication.inputContents = s.toString();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (sydialog != null && sydialog.isShowing()) sydialog.dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (comment_replay_text != null) {
            comment_replay_text.clearFocus();
        }
    }


}
