package com.zhongsou.souyue.im.ac;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.tuita.sdk.im.db.helper.MessageHistoryDaoHelper;
import com.tuita.sdk.im.db.module.MessageHistory;
import com.tuita.sdk.im.db.module.SearchMsgResult;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.adapter.IMSearchAdapter;
import com.zhongsou.souyue.im.search.IMSearch;
import com.zhongsou.souyue.im.search.ListResult;
import com.zhongsou.souyue.im.search.MsgResult;
import com.zhongsou.souyue.im.search.Result;
import com.zhongsou.souyue.im.search.Session;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by x
 *
 * 搜索结果条目
 * 如果条目包含多条信息（如：有5条群聊记录）
 * 点击条目后跳转的页面，即第三级页面
 */
public class IMSearchItemHasMoreActivity extends IMBaseActivity implements IMSearch.IMSearchListener {

    private ListView mListView;                      //联系人列表
    private String pageTittle;
    private TextView myPageTittle;
    private TextView tvMsgNum;
    private IMSearchAdapter mAdapter;
    private int groupType;              //0:联系人  1:群聊  2:聊天记录
    private int historyType;            //历史记录类型 0:联系人历史记录  1:群聊历史记录
    private String keyWord;
    private ListResult listResult;
    private Session session;
    private MsgResult msgResult;
    private String tittle;
    private String image;
    private long chatId;
    private IMSearch mIMSearch;     //搜索类，调用搜索方法
    private long mTestTime;     //测试搜索速度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_msg_search_more_view);
        mIMSearch = new IMSearch(); //初始化搜索对象
        mIMSearch.setIMSearchListener(IMSearchItemHasMoreActivity.this);
        initIntent();
        initView();
        loadData();
    }

    private void initIntent() {
        pageTittle = getIntent().getStringExtra("pageTittle");
        groupType = getIntent().getIntExtra("groupType", 100);
        historyType = getIntent().getIntExtra("historyType", 101);
        keyWord = getIntent().getStringExtra("keyWord");
        listResult = (ListResult) getIntent().getSerializableExtra("listResult");
        session = (Session) getIntent().getSerializableExtra("session");
        tittle = getIntent().getStringExtra("tittle");
        image = getIntent().getStringExtra("image");
        chatId = getIntent().getLongExtra("chatId",0);
    }

    private void initView() {
        tvMsgNum = (TextView)findViewById(R.id.tv_msg_num);
        tvMsgNum.setVisibility(View.VISIBLE);
        myPageTittle = (TextView)findViewById(R.id.activity_bar_title);
        myPageTittle.setText(pageTittle);
        mListView = (ListView) findViewById(R.id.search_result_more_list);
        mAdapter = new IMSearchAdapter(IMSearchItemHasMoreActivity.this, 1);
        mListView.setAdapter(mAdapter);
        mAdapter.setKeyWord(keyWord);
        mAdapter.setPage(3);
    }

    private void loadData() {
        mIMSearch.searchMsg(keyWord,session.sessionId,session.sessionType);
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
     *
     * @param searchType
     * @param result
     */
    @Override
    public void onSearchEnd(int searchType, Result result) {
//        Toast.makeText(IMSearchItemHasMoreActivity.this, "搜索" + result.count + "条数据，一共用了" + (System.currentTimeMillis() - mTestTime) + "毫秒", Toast.LENGTH_LONG).show();
        if (searchType == IMSearch.SEARCH_MSG_TYPE) {
            msgResult = (MsgResult)result;
            ArrayList<Integer> msgIds = msgResult.msgIds;
            List<SearchMsgResult> msgResults = new ArrayList();
            List<MessageHistory> searchList;
            //得到所有消息历史
            searchList = MessageHistoryDaoHelper.getInstance(IMSearchItemHasMoreActivity.this)
                    .findSearchListByMsgIds(Long.parseLong(SYUserManager.getInstance().getUserId()), msgIds);

            for(int i=0 ; i<searchList.size();i++){
                SearchMsgResult msgResult1 = new SearchMsgResult();
                msgResult1.setLayoutType(1);
                msgResult1.setTitle(tittle);
                msgResult1.setContent(searchList.get(i).getContent());
                msgResult1.setUserImage(image);
                msgResult1.setChat_id(chatId);
                msgResult1.setHistoryType(historyType);
                msgResult1.setTime(searchList.get(i).getDate());
                msgResult1.setMsgId(msgIds.get(i));
                msgResult1.setGroupType(groupType);
                msgResults.add(msgResult1);
            }
            mAdapter.addMore(msgResults);
            mAdapter.notifyDataSetChanged();

            tvMsgNum.setText("共"+mAdapter.getCount()+"条与\""+keyWord+"\"相关的聊天记录");

//            Toast.makeText(IMSearchItemHasMoreActivity.this, "组装完成" + result.count + "条数据，一共用了" + (System.currentTimeMillis() - mTestTime) + "毫秒", Toast.LENGTH_LONG).show();
        }
    }

}
