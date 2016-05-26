package com.zhongsou.souyue.im.ac;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
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
import com.zhongsou.souyue.im.adapter.IMSearchAdapter;
import com.zhongsou.souyue.im.search.ListResult;
import com.zhongsou.souyue.im.search.Session;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by x on 15-4-7.
 *
 * 搜索结果点击底部栏“查看更多”后跳转的页面,二级页面
 */
public class IMSearchMoreActivity extends IMBaseActivity {

    private ListView mListView;                      //联系人列表
    private TextView myPageTittle;
    private IMSearchAdapter mAdapter ;
    private int groupType;              //0:联系人  1:群聊  2:聊天记录
    private int historyType;            //历史记录类型 0:联系人历史记录  1:群聊历史记录
    private String keyWord;
    private String pageTittle;
    private ListResult listResult;
    private List<SearchMsgResult> convertResult;
    private LoadContactsTask loadContactsTask;
    private TextView tvMsgNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_msg_search_more_view);
        initIntent();
        initView();
        loadData();
    }

    private void initIntent(){
        groupType = getIntent().getIntExtra("groupType", 100);
        historyType = getIntent().getIntExtra("historyType", 101);
        keyWord = getIntent().getStringExtra("keyWord");
        pageTittle = getIntent().getStringExtra("pageTittle");
        if(groupType==2){
            listResult = (ListResult)getIntent().getSerializableExtra("listResult");
        }
    }

    private void initView(){
        tvMsgNum = (TextView)findViewById(R.id.tv_msg_num);
        tvMsgNum.setVisibility(View.VISIBLE);
        myPageTittle = (TextView)findViewById(R.id.activity_bar_title);
        myPageTittle.setText(pageTittle);
        mListView= (ListView)findViewById(R.id.search_result_more_list);
        mAdapter = new IMSearchAdapter(IMSearchMoreActivity.this,1);
        mListView.setAdapter(mAdapter);
        mAdapter.setKeyWord(keyWord);
        mAdapter.setPage(2);    //二级页面
    }

    private void loadData(){
        if(groupType==2){
            tvMsgNum.setText("聊天记录");
            loadMsgHistoryData();
        }else {
            if(groupType==0){
                tvMsgNum.setText("联系人");
            }
            if(groupType==1){
                tvMsgNum.setText("群聊");
            }
            loadContactsTask = new LoadContactsTask();
            loadContactsTask.execute(keyWord);
        }
    }

    private void loadMsgHistoryData(){

        ArrayList<Session> sessions = listResult.getSessionList();  //所有session
        if (sessions.size()==0) return;
        List<SearchMsgResult> msgResults = new ArrayList();

        for(int i =0 ; i<sessions.size() ; i++){
            SearchMsgResult result1 = new SearchMsgResult();
            Session mSession = listResult.getSessionList().get(i);
            if (sessions.get(i).sessionType == IConst.CHAT_TYPE_PRIVATE){
                Contact contact ;
                //查Contact表
                contact = ContactDaoHelper.getInstance(IMSearchMoreActivity.this).find(Long.parseLong(SYUserManager.getInstance().getUserId()), mSession.sessionId);
                result1.setLayoutType(1);
                result1.setGroupType(2);
                result1.setGroupName("聊天记录");
                if(contact!=null){
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
                    MessageHistory messageHistory = MessageHistoryDaoHelper.getInstance(IMSearchMoreActivity.this).findSingleSearchList(Long.parseLong(SYUserManager.getInstance().getUserId()), mSession);
                    if(messageHistory.getContent()==null){
                        result1.setContent("");
                    }else {
                        result1.setContent(""+messageHistory.getContent());
                    }
                    result1.setMsgId(mSession.msgId);
                    result1.setHistoryType(0);
                }

                msgResults.add(result1);

            }else if (sessions.get(i).sessionType == IConst.CHAT_TYPE_GROUP){
                //查Group表
                Group group ;
                group = GroupDaoHelper.getInstance(IMSearchMoreActivity.this).find(Long.parseLong(SYUserManager.getInstance().getUserId()), mSession.sessionId);

                result1.setLayoutType(1);
                result1.setGroupType(2);
                result1.setChat_id(mSession.sessionId);
                result1.setGroupName("聊天记录");
                if(group!=null){
                    result1.setTitle(group.getGroup_nick_name());
                    result1.setUserImage(group.getGroup_avatar());
                }else {
                    continue;
                }

                if (mSession.count > 1){
                    //不用查history
                    result1.setHasMore(true);
                    result1.setHistoryType(1);
                    result1.setContent("有"+mSession.count+"条群聊记录");
                }else{
                    //查历史记录
                    result1.setHasMore(false);
                    MessageHistory messageHistory = MessageHistoryDaoHelper.getInstance(IMSearchMoreActivity.this).findSingleSearchList(Long.parseLong(SYUserManager.getInstance().getUserId()), mSession);
                    result1.setContent(""+messageHistory.getContent());
                    result1.setMsgId(mSession.msgId);
                    result1.setHistoryType(1);
                }

                msgResults.add(result1);
            }

        }
        mAdapter.addMore(msgResults);
        mAdapter.setKeyWord(keyWord);
        mAdapter.setListResult(listResult);
        mAdapter.notifyDataSetChanged();

    }

    public class LoadContactsTask extends AsyncTask<String, Void, List<SearchMsgResult>> {
        private boolean needLoad;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
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

            switch (groupType){
                case 0 :
                    data = ImserviceHelp.getInstance().db_find_search_contact_detail(keyword_F);
                    break;
                case 1 :
                    data = ImserviceHelp.getInstance().db_find_search_group_detail(keyword_F);
                    break;
                case 2 :
                    break;
                default:
                    break;
            }

            return convertResult((List<SearchMsgResult>) (data == null ? Collections.emptyList() : data));
        }

        protected void onPostExecute(final List<SearchMsgResult> result) {
            if (needLoad) {
                ImserviceHelp.getInstance().im_info(4, null);
            }
            if(null!=mAdapter) {
                mAdapter.addMore(convertResult);
                mAdapter.notifyDataSetChanged();
            }
        }

        private List<SearchMsgResult> convertResult(List<SearchMsgResult> result) {
            convertResult = new ArrayList<SearchMsgResult>();
            convertResult.addAll(result);
            return convertResult;
        }
    }


}
