package com.zhongsou.souyue.im.ac;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.tuita.sdk.im.db.helper.PingYinUtil;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.GroupMembers;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.adapter.HorizontalListViewAdapter;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.view.HorizontalListView;
import com.zhongsou.souyue.im.adapter.InviteGroupFriendAdapter;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.utils.SYInputMethodManager;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zoulu
 * on 14-8-21
 * Description:@群成员
 */
public class InviteGroupFriendActivity extends IMBaseActivity implements View.OnClickListener{
    private TextView title;
    private static int NOTIFY = 1;

    private InviteGroupFriendAdapter adapter;
    private ListView swipeListView;
    private EditText search_edit;
    private Button btnSearchClear;
    private SYInputMethodManager syInputMng;

//    private ImProgressMsgDialog dialog;
    private HorizontalListView horizontalListView;
    private HorizontalListViewAdapter adapter2;
    private TextView tvConfirmInvite;
    private int friendCount = 0;
    private ArrayList<String> vecStr;
    private ArrayList<GroupMembers> selMembers;
    private ArrayList<Long> memberIdTmpVec;
    private ArrayList<GroupMembers> allItems;

    private long interest_id;
    private int pno = 1;
    private int psize = 15;
    private int visibleLast = 0;
    private boolean isSearch ;
    private boolean isLoadAll;
    //	private View footerView;
    private TextView tvNoResult;
    private long groupid;
    private ImserviceHelp service = ImserviceHelp.getInstance();
    private ArrayList<GroupMembers> alllist = new ArrayList<GroupMembers>();
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.invitegroupfrinendactivity);
        initUI();
    }

    private void initUI(){
        title = (TextView) findViewById(R.id.title_name);
        title.setText("选择群成员");
        pno = 1;
        isSearch = false;
        isLoadAll = false;
        allItems = new ArrayList<GroupMembers>();
        groupid = getIntent().getLongExtra("groupId", 0L);
        if(selMembers == null) {
            selMembers = new ArrayList<GroupMembers>();
        }

//        footerView = getLayoutInflater().inflate(R.layout.ent_refresh_footer,null);
        tvNoResult = (TextView) findViewById(R.id.circle_noresult_tv);
        tvNoResult.setVisibility(View.GONE);
        tvConfirmInvite = (TextView)findViewById(R.id.invite_confirm_tv);
        vecStr = new ArrayList<String>();
        memberIdTmpVec = new ArrayList<Long>();
        horizontalListView =(HorizontalListView) findViewById(R.id.horizon_listview);
        adapter2 = new HorizontalListViewAdapter(this,vecStr);
        horizontalListView.setAdapter(adapter2);


        tvConfirmInvite.setOnClickListener(this);

//        horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//                                    long arg3) {
//                // TODO Auto-generated method stub
//                vecStr.remove(position);
//                friendCount --;
//                tvConfirmInvite.setText("发送(" +friendCount+")");
//                adapter2.notifyDataSetChanged();
//                InviteGroupFriendAdapter.selected.put(memberIdTmpVec.get(position), false);
//                memberIdTmpVec.remove(position);
//                selMembers.remove(position);
//                adapter.notifyDataSetChanged();
//            }
//
//
//        });

        swipeListView = (ListView) findViewById(R.id.listView);
        swipeListView.setOnTouchListener(mTouchListener);
//        swipeListView.addFooterView(footerView);
        initListViewHeader();
        adapter = new InviteGroupFriendAdapter(InviteGroupFriendActivity.this, allItems);
        swipeListView.setAdapter(adapter);
        tvConfirmInvite.setText("发送(" +friendCount+")");
        swipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
//				if(view == footerView){
//					return;
//				}
                GroupMembers itemContact = (GroupMembers) adapter.getItem(position);
                InviteGroupFriendAdapter.ViewHolder holder = (InviteGroupFriendAdapter.ViewHolder) view.getTag();
                holder.checkBox.toggle();
                InviteGroupFriendAdapter.selected.put(itemContact.getMember_id(), holder.checkBox.isChecked());
                if(InviteGroupFriendAdapter.selected.get(itemContact.getMember_id())){
                    friendCount ++;
                    vecStr.add(itemContact.getMember_avatar());
                    memberIdTmpVec.add(itemContact.getMember_id());
                    selMembers.add(itemContact);
                }else{
                    friendCount --;
                    vecStr.remove(itemContact.getMember_avatar());
                    memberIdTmpVec.remove(itemContact.getMember_id());
                    for(int i = 0 ; i< selMembers.size() ; i++){
                        GroupMembers item = selMembers.get(i);
                        if(item.getMember_id() == itemContact.getMember_id()){
                            selMembers.remove(item);
                            break;
                        }
                    }
                }
                tvConfirmInvite.setText("发送(" +friendCount+")");
                adapter2.notifyDataSetChanged();
            }

        });

//        swipeListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                syInputMng.hideSoftInput();
//                int itemsLastIndex = 0;
////                if(isLoadAll  && !isSearch){
//                itemsLastIndex = adapter.getCount();
////                }else{
////                	  itemsLastIndex = adapter.getCount() + 1;
////                }
//
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLast == itemsLastIndex ) {
//                    pno += 1;
//                    isSearch = false;
//                    getMemberList();
//                }
//            }
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                visibleLast = firstVisibleItem + visibleItemCount ;
//            }
//        });

//        if (dialog == null)
//            dialog = new ImProgressMsgDialog.Builder(this).create();
//        dialog.show();
        showProgress();

        getMemberList();
        syInputMng = new SYInputMethodManager(this);
        for(int i = 0 ; i < selMembers.size() ; i++ ){
            vecStr.add(selMembers.get(i).getMember_avatar());
            memberIdTmpVec.add(selMembers.get(i).getMember_id());
            InviteGroupFriendAdapter.selected.put(memberIdTmpVec.get(i), true);
        }
        friendCount = selMembers.size();
        tvConfirmInvite.setText("发送(" + friendCount + ")");
        adapter2.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.invite_confirm_tv:
                if(selMembers.size() == 0){
                    UIHelper.ToastMessage(this, "请选择群成员");
                    return;
                }
                Intent data = new Intent();
                data.putExtra("selMembers", selMembers);
                setResult(UIHelper.RESULT_OK, data);
                finish();
                break;
            default:
                break;
        }
    }

    private void initListViewHeader() {
        search_edit = (EditText)findViewById(R.id.search_edit);
        search_edit.setHint(R.string.search_at);
        search_edit.setHintTextColor(getResources().getColor(R.color.im_edit_text_hint_color));
        btnSearchClear = (Button) findViewById(R.id.btn_search_clear);
        btnSearchClear.setVisibility(View.GONE);
        btnSearchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_edit.setText("");
                btnSearchClear.setVisibility(View.GONE);
            }
        });

        search_edit.addTextChangedListener(new TextWatcher() {
            String before = null;

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                isLoadAll = false;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                before = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                String after = s.toString();
                if (before != null && before.equals(after)) {// 没有改变
                    return;
                }

                if (!TextUtils.isEmpty(after)) {// 有输入
                    btnSearchClear.setVisibility(View.VISIBLE);
                } else {
                    btnSearchClear.setVisibility(View.GONE);
                }

                if(s.toString().length() == 0 || s.toString().equals("")){
                    search_edit.setHint("群成员昵称");
                }

                // TODO 搜索请求
                pno = 1;
                isSearch = true;
                searchMembers(after);
//				http.getMemberListAll(interest_id, after);
//                loadContactsTask.cancel(true);
//                loadContactsTask = new LoadContactsTask();
//                loadContactsTask.execute(after);

            }
        });
    }

    private void searchMembers(String s){
        String keyword_F = PingYinUtil.converReg(s);
        ArrayList<GroupMembers> list = new ArrayList<GroupMembers>();
        for(int i = 0 ;i < alllist.size(); i++){
            if (alllist.get(i).getLocalOrder()!=null && alllist.get(i).getLocalOrder().contains(keyword_F)){
                list.add(alllist.get(i));
            }
//            String search_name = TextUtils.isEmpty(alllist.get(i).getMember_name())?alllist.get(i).getNick_name():alllist.get(i).getMember_name();
//            if(search_name.contains(keyword_F))
//                list.add(alllist.get(i));
        }
        allItems.clear();
        allItems.addAll(list);
        adapter.setKeyWord(keyword_F.replace(" ",""));
        adapter.notifyDataSetChanged();
    }

    private void getMemberList() {
        if(isLoadAll && !isSearch) {
            UIHelper.ToastMessage(this, "已全部加载");
            return;
        }
        //根据groupid 查询成员表
        final List<GroupMembers> result = service.db_findMemberListByGroupid(groupid);
        swipeListView.setVisibility(View.VISIBLE);
        isLoadAll = false;
        tvNoResult.setVisibility(View.GONE);

        if((result == null ||result.size() == 0) && isSearch){
            tvNoResult.setVisibility(View.VISIBLE);
            tvNoResult.setText("无结果");
            isLoadAll = false;
            swipeListView.setVisibility(View.GONE);
            return;
        }

        if (isSearch) {
            allItems.clear();
            adapter.notifyDataSetChanged();
        }
        if(result != null){
            for(int i = 0; i<result.size(); i++){
                Contact c = ImserviceHelp.getInstance().db_getContactById(result.get(i).getMember_id());
                if(c != null && !TextUtils.isEmpty(c.getComment_name())){
                    result.get(i).setConmmentName(c.getComment_name());
                }
                result.get(i).setLocalOrder(genLocal_order(result.get(i)));
            }
            for (int i = 0; i<result.size(); i++){
                if(result.get(i).getNick_name().equals(SYUserManager.getInstance().getName()))
                    result.remove(i);
            }
            allItems.addAll(result);
            alllist.addAll(allItems);
            adapter.notifyDataSetChanged();
        }
//        dialog.dismiss();
        dismissProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        swipeListView.setAdapter(null);
        swipeListView = null;
        adapter = null;
        syInputMng.hideSoftInput();
        syInputMng = null;
        if(InviteGroupFriendAdapter.selected.size() != 0){
            InviteGroupFriendAdapter.selected.clear();
        }
        vecStr.clear();
        memberIdTmpVec.clear();
        selMembers.clear();
    }

    private String genLocal_order(GroupMembers groupMembers) {
        if (groupMembers.getConmmentName() == null || groupMembers.getConmmentName().length() == 0) {   //没有有备注名
            if (groupMembers.getMember_name() == null || groupMembers.getMember_name().length() == 0){  //没有群昵称
            return PingYinUtil.conver2SqlRow(groupMembers.getNick_name());
            }else { //有群昵称
                return PingYinUtil.conver2SqlRow(groupMembers.getMember_name() + " " + groupMembers.getNick_name());
            }
        }else {     //有备注名
            if (groupMembers.getMember_name() == null || groupMembers.getMember_name().length() == 0){  //没有群昵称
                return PingYinUtil.conver2SqlRow(groupMembers.getConmmentName() + " " + groupMembers.getNick_name());
            }else {     //啥都有
                return PingYinUtil.conver2SqlRow(groupMembers.getConmmentName() + " " +groupMembers.getMember_name() + " " + groupMembers.getNick_name());
            }
        }
    }


    /**
     * listview的touch时间监听
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            syInputMng.hideSoftInput();

            return false;
        }
    };
}
