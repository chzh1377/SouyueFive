package com.zhongsou.souyue.im.ac;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tuita.sdk.im.db.helper.ContactDaoHelper;
import com.tuita.sdk.im.db.helper.GroupDaoHelper;
import com.tuita.sdk.im.db.helper.MessageHistoryDaoHelper;
import com.tuita.sdk.im.db.helper.PingYinUtil;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.MessageHistory;
import com.tuita.sdk.im.db.module.SearchMsgResult;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.im.adapter.IMSearchAdapter;
import com.zhongsou.souyue.im.search.IMQuery;
import com.zhongsou.souyue.im.search.IMSearch;
import com.zhongsou.souyue.im.search.ListResult;
import com.zhongsou.souyue.im.search.Result;
import com.zhongsou.souyue.im.search.SearchUtils;
import com.zhongsou.souyue.im.search.Session;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.ImUtils;
import com.zhongsou.souyue.log.Logger;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by x on 15-4-7.
 * 搜索界面 一级界面
 */
public class IMSearchActivity extends IMBaseActivity implements IMSearch.IMSearchListener {

    private LinearLayout ll_im_search_view;
    private RelativeLayout rl_search_head;                  //搜索框布局
    private LinearLayout llBackground;
    private ListView mListView;                      //联系人列表
    private IMSearchAdapter adapter;
    private TextView tv_cancel;
    private TextView tv_no_result;
    private EditText search_edit;
    private ImageButton btnSearchClear;
    private List<SearchMsgResult> convertResult;
    private LoadContactsTask loadContactsTask;
    private String keyWord;
    private String after;
    private ListResult listResult;
    private int convertResultSize;
    private IMSearch mIMSearch;     //搜索类，调用搜索方法
    private long mTestTime;     //测试搜索速度
    private long maxMessageID;
    private long userId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_msg_search_view);
        initView();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSearch();
        updateSearchMsg();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IMQuery.destory();
    }

    private void updateSearchMsg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MessageHistoryDaoHelper.getInstance(IMSearchActivity.this).updateSearchMsg(Long.parseLong(SYUserManager.getInstance().getUserId()),maxMessageID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        SearchUtils.saveIndex(MainActivity.SEARCH_PATH_MEMORY_DIR,userId);
    }

    private void initSearch() {
        IMQuery.init();
        userId = Long.parseLong(SYUserManager.getInstance().getUserId());
        SearchUtils.loadIndex(MainActivity.SEARCH_PATH_MEMORY_DIR,userId);
        mIMSearch = new IMSearch(); //初始化搜索对象
        SharedPreferences sharedPreferences = getSharedPreferences("curMaxMessageID", Context.MODE_PRIVATE);
        maxMessageID = sharedPreferences.getLong("id",-1);
        mIMSearch.setIMSearchListener(IMSearchActivity.this);
    }

    private void initView() {
        ll_im_search_view = (LinearLayout) findViewById(R.id.ll_im_search_view);
        llBackground= (LinearLayout) findViewById(R.id.im_search_ll_background);
        rl_search_head = (RelativeLayout) findViewById(R.id.rl_search_head);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                finish();
            }
        });
        search_edit = (EditText) findViewById(R.id.search_edit);
        search_edit.setHintTextColor(getResources().getColor(R.color.im_edit_text_hint_color));
        tv_no_result= (TextView) findViewById(R.id.tv_no_result);
        btnSearchClear = (ImageButton) findViewById(R.id.btn_search_clear);
        btnSearchClear.setVisibility(View.GONE);
        btnSearchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_edit.setText("");
                btnSearchClear.setVisibility(View.GONE);
            }
        });
        mListView = (ListView) findViewById(R.id.search_result_list);
        //滑动列表时隐藏软键盘
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        adapter = new IMSearchAdapter(IMSearchActivity.this, 1);
        mListView.setAdapter(adapter);
        adapter.setPage(1);
        search_edit.addTextChangedListener(new TextWatcher() {
            String before = null;
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                before = s.toString();
            }
            @Override
            public void afterTextChanged(Editable s) {
                adapter.clearData();
                after = s.toString().trim();
                if (before != null && before.equals(after)) {// 没有改变
                    return;
                }
                if (!TextUtils.isEmpty(after)) {// 有输入
                    mListView.setVisibility(View.VISIBLE);
                    btnSearchClear.setVisibility(View.VISIBLE);
                    adapter.setKeyWord(after);
                    adapter.notifyDataSetChanged();
                } else {
                    mListView.setVisibility(View.GONE);
                    btnSearchClear.setVisibility(View.GONE);
                }
                loadContactsTask.cancel(true);
                loadContactsTask = new LoadContactsTask();
                loadContactsTask.execute(after);

                if (!TextUtils.isEmpty(after)) {
                    if(mIMSearch!=null){//加层判断，双重保险
                        mIMSearch.searchList(after);
                    }
                }

            }
        });
    }

    public class LoadContactsTask extends AsyncTask<String, Void, List<SearchMsgResult>> {
        private boolean needLoad;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * 参数为空表示取全部，
         * 参数个数为1表示本地搜索
         * 参数个数为2表示初次取通讯录，需要通知获取网络通讯录
         * 参数个数为3表示收到更新的广播，从新获取本地通讯录
         */
        protected List<SearchMsgResult> doInBackground(String... args) {
            List<SearchMsgResult> data = null;
            boolean showAll = true;
            needLoad = false;
            String keyword_F = null;
            String sourceKey = null;
            if (args != null && args.length > 0) {
                String searchKey = args[0];// 输入框输入
                if (!TextUtils.isEmpty(searchKey)) {
                    keyword_F = PingYinUtil.conver2SqlReg(searchKey);
                    sourceKey = searchKey;
                    if (!TextUtils.isEmpty(keyword_F)) {// 显示全部
                        showAll = false;
                    }
                }
            }
            //联系人、群聊数据模型转化为搜索模型
            data = ImserviceHelp.getInstance().db_find_search_all(keyword_F);
            return convertResult((List<SearchMsgResult>) (data == null ? Collections.emptyList() : data));
        }

        protected void onPostExecute(final List<SearchMsgResult> result) {
            if (needLoad) {
                ImserviceHelp.getInstance().im_info(4, null);
            }
            if(null!=adapter) {
                adapter.addMore(convertResult);
                convertResultSize = convertResult.size();
                adapter.setConvertResultSize(convertResultSize);
                adapter.notifyDataSetChanged();

                if(!StringUtils.isEmpty(after) && adapter.getCount()==0){
                    llBackground.setVisibility(View.GONE);
                    tv_no_result.setVisibility(View.VISIBLE);
                    ll_im_search_view.setBackgroundResource(R.color.white);
//                    String afterCut = ImUtils.cutText(after);
                    String content = "没有找到与\""+after+"\"相关的结果";
                    Spanned afterContent = ImUtils.getHighlightText(content, "", after);
                    tv_no_result.setText(afterContent);
                }else {
                    tv_no_result.setVisibility(View.GONE);
                    if(adapter.getCount()==0){
                        llBackground.setVisibility(View.VISIBLE);
                    }else {
                        llBackground.setVisibility(View.GONE);
                        ll_im_search_view.setBackgroundResource(R.color.white);
                    }
                }

            }
        }

        private List<SearchMsgResult> convertResult(List<SearchMsgResult> result) {
            convertResult = new ArrayList<SearchMsgResult>();
            convertResult.addAll(result);
            return convertResult;
        }
    }

    private void loadData() {
        //搜索结果数据
        loadContactsTask = new LoadContactsTask();
    }

    /**
     * 搜索开始方法
     */
    @Override
    public void onSearchStart() {
        mTestTime = System.currentTimeMillis();
    }

    /**
     * 搜索结束方法，搜索结果的回调
     * @param searchType
     * @param result
     */
    @Override
    public void onSearchEnd(int searchType, Result result) {
        if (searchType == IMSearch.SEARCH_LIST_TYPE) {

            //打印搜索结果日志
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Logger.i("IM消息搜索————IMSearchActivity","time:"+date.format(new Date()),"搜索出" + result.count + "条关于["+after+"]的数据（result.count）");

            listResult = (ListResult)result;
            adapter.setListResult(listResult);
            // 所有历史记录
//           List<MessageHistory> searchList;
//            searchList = MessageHistoryDaoHelper.getInstance(IMSearchActivity.this).findSearchList(Long.parseLong(SYUserManager.getInstance().getUserId()), listResult.getSessionList());

            ArrayList<Session> sessions = listResult.getSessionList();  //所有session
            if (sessions.size()==0) return;
            tv_no_result.setVisibility(View.GONE);
            ArrayList<Session> cutSessions = new ArrayList();           //新建session，填入前3条session
            if(sessions.size()>3){
                for (int i =0;i<3;i++){
                    cutSessions.add(sessions.get(i));
                }
            }else {
                for (int i =0;i<sessions.size();i++){
                    cutSessions.add(sessions.get(i));
                }
            }

            List<SearchMsgResult> msgResults = new ArrayList();
            SearchMsgResult result0 = new SearchMsgResult();//添加一条top类型的数据，起到填平作用
            result0.setLayoutType(0);
            result0.setGroupType(2);
            result0.setGroupName("聊天记录");
            msgResults.add(result0);

            for(int i =0 ; i<cutSessions.size() ; i++){
                SearchMsgResult result1 = new SearchMsgResult();
                Session mSession = listResult.getSessionList().get(i);
                if (cutSessions.get(i).sessionType == IConst.CHAT_TYPE_PRIVATE){
                    Contact contact ;
                    //查Contact表
                    contact = ContactDaoHelper.getInstance(IMSearchActivity.this).find(Long.parseLong(SYUserManager.getInstance().getUserId()), mSession.sessionId);
                    result1.setLayoutType(1);
                    result1.setGroupType(2);
                    result1.setGroupName("聊天记录");
                    if(contact != null){
                        result1.setChat_id(contact.getChat_id());
                        result1.setChat_type(contact.getChat_type());
                        result1.setTitle(contact.getNick_name());
                        result1.setUserImage(contact.getAvatar());
                    }else {
                        continue;
                    }


                    if (mSession.count > 1){
                        //不用查history
                        result1.setHasMore(true);
                        result1.setHistoryType(0);
                        result1.setContent("有"+mSession.count+"条私聊记录");
                    }else{
                        //查历史记录
                        result1.setHasMore(false);
                        MessageHistory messageHistory = MessageHistoryDaoHelper.getInstance(IMSearchActivity.this).findSingleSearchList(Long.parseLong(SYUserManager.getInstance().getUserId()), mSession);
                        result1.setContent(""+messageHistory.getContent());
                        result1.setMsgId(mSession.msgId);
                        result1.setHistoryType(0);
                    }

                    msgResults.add(result1);

                }else if (cutSessions.get(i).sessionType == IConst.CHAT_TYPE_GROUP){
                    //查Group表
                    Group group ;
                    group = GroupDaoHelper.getInstance(IMSearchActivity.this).find(Long.parseLong(SYUserManager.getInstance().getUserId()), mSession.sessionId);
                    result1.setLayoutType(1);
                    result1.setGroupType(2);
                    result1.setChat_id(mSession.sessionId);
                    if(group!=null && group.getGroup_nick_name()!=null){
                        Logger.i("IM消息搜索————IMSearchActivity——line348","time:"+date.format(new Date()),"group:" + group + "getGroup_nick_name:"+group.getGroup_nick_name());
                        result1.setTitle(group.getGroup_nick_name());
                        result1.setUserImage(group.getGroup_avatar());
                    }else {
                        continue;
                    }
                    result1.setGroupName("聊天记录");


                    if (mSession.count > 1){
                        //不用查history
                        result1.setHasMore(true);
                        result1.setHistoryType(1);
                        result1.setContent("有"+mSession.count+"条群聊记录");
                    }else{
                        //查历史记录
                        result1.setHasMore(false);
                        MessageHistory messageHistory = MessageHistoryDaoHelper.getInstance(IMSearchActivity.this).findSingleSearchList(Long.parseLong(SYUserManager.getInstance().getUserId()), mSession);
                        result1.setContent(""+messageHistory.getContent());
                        result1.setMsgId(mSession.msgId);
                        result1.setHistoryType(1);
                    }

                    msgResults.add(result1);
                }

            }

            if(sessions.size()>3){
                SearchMsgResult result2 = new SearchMsgResult();//添加查看更多聊天记录的bottom，填平数据
                result2.setLayoutType(2);
                result2.setGroupType(2);
                result2.setGroupName("查看更多聊天记录");
                msgResults.add(result2);
            }

            adapter.addMore(msgResults);
            adapter.notifyDataSetChanged();

//            Toast.makeText(IMSearchActivity.this, "搜索" + result.count + "条数据，一共用了" + (System.currentTimeMillis() - mTestTime) + "毫秒", Toast.LENGTH_LONG).show();
        }
    }

}
