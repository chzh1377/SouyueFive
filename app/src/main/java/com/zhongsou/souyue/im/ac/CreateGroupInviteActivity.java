package com.zhongsou.souyue.im.ac;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuita.sdk.BroadcastUtil;
import com.tuita.sdk.im.db.helper.ContactDaoHelper;
import com.tuita.sdk.im.db.helper.PingYinUtil;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.IConst;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.adapter.HorizontalListViewAdapter;
import com.zhongsou.souyue.im.adapter.GroupInviteFriendAdapter;
import com.zhongsou.souyue.im.dialog.ImInviteDialog;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.ContactModelUtil;
import com.zhongsou.souyue.im.util.ImUtils;
import com.zhongsou.souyue.im.view.AlphaSideBar;
import com.zhongsou.souyue.im.view.SwipeListView;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.SYInputMethodManager;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.ThreadPoolUtil;
import com.zhongsou.souyue.view.HorizontalListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Desc: 邀请好友加群
 * User: zhangwb
 * DateTime: 14-8-25
 */
public class CreateGroupInviteActivity extends IMBaseActivity implements OnClickListener{

    private GroupInviteFriendAdapter adapter;
    private Map<String, Integer> alphaIndex;
    private SwipeListView swipeListView;
    private TextView txtOverlay;
    private EditText search_edit;
    private Button btnSearchClear;
    private SYInputMethodManager syInputMng;
    private AlphaSideBar alphaBar;

    private LoadContactsTask loadContactsTask;
    private HorizontalListView horizontalListView;
    private HorizontalListViewAdapter adapter2;
    private ArrayList<String> vecStr;
    private TextView tvConfirmInvite;
    private int friendCount = 0;
    private Vector<Long> vecIds;
    private Vector<Long> contactIdTmpVec;

    private static int NOTIFY = 1;
    //是否是搜悦好友
    private boolean isSYFriend = false;
    private ArrayList<Contact> selMembers;
    private ImInviteDialog.Builder sharebuilder;
    
    private long contactId;
    private String type;
    private Contact itemContact;
    private boolean comefromChatDetail;//是否来自私聊详情

    private CreateGroupReceiver createGroupReceiver;
    @SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    adapter.notifyDataSetChanged();
                    dismissProgress();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_invite_friend_list_view);
        setReciever();
        findView(R.id.title_bar_ac_chat_txtbtn).setVisibility(View.GONE);
        selMembers = new ArrayList<Contact>();
        sharebuilder = new ImInviteDialog.Builder(CreateGroupInviteActivity.this);
        isSYFriend = this.getIntent().getBooleanExtra("isSYFriend", isSYFriend);
        comefromChatDetail = getIntent().getBooleanExtra("fromChatDetail",false);
        TextView title = (TextView) findViewById(R.id.activity_bar_title);
        title.setText("选择好友");
       
        type =  getIntent().getStringExtra("type");
        vecStr = new ArrayList<String>();
        vecIds = new Vector<Long>();
            contactId = getIntent().getLongExtra("contactId", 0);
        if(contactId != 0) {
            vecIds.add(contactId);
            friendCount = 0;
            GroupInviteFriendAdapter.selected.put(contactId,true);
        }
        contactIdTmpVec = new Vector<Long>();
        findView(R.id.im_find_friend).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GroupInviteFriendAdapter.selected.clear();
		        vecIds.clear();
		        vecStr.clear();
		        contactIdTmpVec.clear();

                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

				CreateGroupInviteActivity.this.finish();
			}
		});
        
        horizontalListView =(HorizontalListView) findView(R.id.horizon_listview);
//        horizontalListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//					long arg3) {
//				// TODO Auto-generated method stub
//				vecStr.remove(position);
//    			friendCount --;
//    			vecIds.remove(position);
//    			tvConfirmInvite.setText("确定(" +friendCount+")");
//    			selMembers.remove(position);
//        		adapter2.notifyDataSetChanged();
//        		GroupInviteFriendAdapter.selected.put(contactIdTmpVec.get(position), false);
//        		contactIdTmpVec.remove(position);
//        		adapter.notifyDataSetChanged();
//
//			}
//
//		});
        adapter2 = new HorizontalListViewAdapter(this,vecStr);
        horizontalListView.setAdapter(adapter2);
        
        tvConfirmInvite = (TextView)findView(R.id.invite_confirm_tv);
        tvConfirmInvite.setOnClickListener(this);

        swipeListView = (SwipeListView) findViewById(R.id.example_lv_list);
        swipeListView.setSwipeAble(false);
        View convertView = getLayoutInflater().inflate(R.layout.add_titlebar_contacts_swipe_list, swipeListView, false);
        initListViewHeader(convertView);
        swipeListView.addHeaderView(convertView);
        alphaIndex = new HashMap<String, Integer>();
        adapter = new GroupInviteFriendAdapter(swipeListView, this, alphaIndex, swipeListView.getRightViewWidth());
        swipeListView.setAdapter(adapter);
        tvConfirmInvite.setText("确定(" +friendCount+")");
        swipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                syInputMng.hideSoftInput();
                if (parent instanceof SwipeListView) {
                    SwipeListView sl = ((SwipeListView) parent);
                    itemContact = adapter.getItem(position - sl.getHeaderViewsCount());
                    GroupInviteFriendAdapter.ViewHolder holder = (GroupInviteFriendAdapter.ViewHolder) view.getTag();
                    holder.checkBox.toggle();
                    GroupInviteFriendAdapter.getSelected.put(itemContact.getChat_id(), holder.checkBox.isChecked());
                    //添加东西
                		if(GroupInviteFriendAdapter.getSelected.get(itemContact.getChat_id())){
                            if(itemContact.getChat_id() != contactId) {
                                friendCount++;
                                vecStr.add(itemContact.getAvatar());
                                vecIds.add(itemContact.getChat_id());
                                contactIdTmpVec.add(itemContact.getChat_id());
                                selMembers.add(itemContact);
                                GroupInviteFriendAdapter.getSelected.put(itemContact.getChat_id(), true);
                            }
                		}else{
                            if(itemContact.getChat_id() != contactId) {
                                vecStr.remove(itemContact.getAvatar());
                                friendCount--;
                                vecIds.remove(itemContact.getChat_id());
                                contactIdTmpVec.remove(itemContact.getChat_id());
                                selMembers.remove(itemContact);
                                GroupInviteFriendAdapter.getSelected.remove(itemContact.getChat_id());
                            }
                		}
                		tvConfirmInvite.setText("确定(" +friendCount+")");
                		adapter2.notifyDataSetChanged();
                }
                
            }
        });
        swipeListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                syInputMng.hideSoftInput();
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        // listView-end

        showProgress();
        loadContactsTask = new LoadContactsTask();
        loadContactsTask.execute("", "");// 首次获取

        setSlideBar();
        syInputMng = new SYInputMethodManager(this);
    }
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(createGroupReceiver != null){
            unregisterReceiver(createGroupReceiver);
        }
        swipeListView.setAdapter(null);
        swipeListView = null;
        adapter.clear();
        adapter = null;
        alphaBar = null;
        alphaBar = null;
        if (txtOverlay != null) {
            WindowManager mWindowManager = (WindowManager) this
                    .getSystemService(Context.WINDOW_SERVICE);
            mWindowManager.removeView(txtOverlay);
        }
        txtOverlay = null;
        syInputMng.hideSoftInput();
        syInputMng = null;
        if(GroupInviteFriendAdapter.selected.size() != 0){
            GroupInviteFriendAdapter.selected.clear();
        }
        if(GroupInviteFriendAdapter.getSelected.size() != 0){
        	GroupInviteFriendAdapter.getSelected.clear();
        }
    }
    

    private void initListViewHeader(View convertView) {
        convertView.findViewById(R.id.ll_other).setVisibility(View.GONE);
        search_edit = (EditText) convertView.findViewById(R.id.search_edit);
        search_edit.setHint(R.string.search_no_group);
        search_edit.setHintTextColor(getResources().getColor(R.color.im_edit_text_hint_color));

        btnSearchClear = (Button) convertView.findViewById(R.id.btn_search_clear);
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
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                before = s.toString();
               
            }

            @Override
            public void afterTextChanged(Editable s) {
                String after = s.toString();
                if(after.length() == 0){
                	search_edit.setHint("查找好友");
                    adapter.setKeyWord(null);
                }
                if (before != null && before.equals(after)) {// 没有改变
                    return;
                }

                if (!TextUtils.isEmpty(after)) {// 有输入
                    btnSearchClear.setVisibility(View.VISIBLE);
                } else {
                    btnSearchClear.setVisibility(View.GONE);
                }

                adapter.setShowNoValue(true);
                loadContactsTask.cancel(true);
                loadContactsTask = new LoadContactsTask();
                loadContactsTask.execute(after);
                
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setSlideBar() {
        alphaBar = (AlphaSideBar) findViewById(R.id.alphaView);
        alphaBar.setListView(swipeListView);
        txtOverlay = (TextView) LayoutInflater.from(this).inflate(R.layout.alpha_window, null);
        txtOverlay.setVisibility(View.INVISIBLE);
        alphaBar.setTextView(txtOverlay);
        WindowManager mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mWindowManager.addView(txtOverlay, lp);
    }

    public class LoadContactsTask extends
            AsyncTask<String, Void, List<Contact>> {
        private boolean needLoad;

        /**
         * 参数为空表示取全部， 参数个数为1表示本地搜索 参数个数为2表示初次取通讯录，需要通知获取网络通讯录
         * 参数个数为3表示收到更新的广播，从新获取本地通讯录
         *
         */
        protected List<Contact> doInBackground(String... args) {
            List<Contact> data = null;
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
            long muid = SYUserManager.getInstance().getUser().userId();
            if (showAll) {// 显示全部
                data = ImserviceHelp.getInstance().db_getContact();
                if (args.length == 2) {
                    needLoad = true;
                }
            } else {
                data = ContactDaoHelper.getInstance(
                        MainApplication.getInstance())
                        .findLike(muid, keyword_F);
                adapter.setKeyWord(sourceKey.toUpperCase().replace(" ",""));
            }

            return (List<Contact>) (data == null ? Collections.emptyList() : data);
        }

        protected void onPostExecute(final List<Contact> result) {
            if (needLoad) {
                ImserviceHelp.getInstance().im_info(4, null);
            }
            ThreadPoolUtil.getInstance().execute(new Runnable() {

                @Override
                public void run() {
                    boolean isSearch = false;
                    if (!TextUtils.isEmpty(search_edit.getText().toString())) {
                        isSearch = true;
                    }

                    LogDebugUtil.v("fan", result.size() + "总共");
                    Map<String, List<Contact>> sectionItems = new HashMap<String, List<Contact>>();
                    List<Contact> finalRes = null;
                    for (int i = 0; i < result.size(); i++) {
                        Contact item = result.get(i);
                        String lhs_p = PingYinUtil.getPingYin(ContactModelUtil
                                .getShowName(item));
                        if (TextUtils.isEmpty(lhs_p))
                            continue;

                        String key = lhs_p.substring(0, 1).toUpperCase();
                        if ("A".compareTo(key) > 0 || "Z".compareTo(key) < 0) {
                            key = "#";
                        }

                        if (!sectionItems.containsKey(key)) {
                            finalRes = new ArrayList<Contact>();
                            sectionItems.put(key, finalRes);
                        } else {
                            finalRes = sectionItems.get(key);
                        }
                        finalRes.add(item);
                    }

                    List<String> keys = new ArrayList<String>(sectionItems
                            .keySet());
                    Collections.sort(keys, new Comparator<String>() {

                        @Override
                        public int compare(String lhs, String rhs) {
                            if (lhs.equals(rhs)) {
                                return 0;
                            } else if ("#".equals(lhs) && !"#".equals(rhs)) {
                                return 1;
                            } else if (!"#".equals(lhs) && "#".equals(rhs)) {
                                return -1;
                            }
                            return lhs.compareTo(rhs);
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(adapter != null)
                                adapter.clearData();
                        }
                    });
                    int sectionIndex = 0;
                    if (!isSearch) {
                        for (String key : keys) {
                            final List<Contact> items = sectionItems.get(key);
                            Collections.sort(items, new Comparator<Contact>() {

                                @Override
                                public int compare(Contact lhs, Contact rhs) {
                                    String lhs_p = PingYinUtil
                                            .getPingYin(ContactModelUtil
                                                    .getShowName(lhs));
                                    String rhs_p = PingYinUtil
                                            .getPingYin(ContactModelUtil
                                                    .getShowName(rhs));
                                    return lhs_p.compareTo(rhs_p);
                                }
                            });

                            if (!isSearch) {// 追加索引字母
                                Contact indexItem = new Contact();
                                indexItem.setComment_name(key);
                                items.add(0, indexItem);
                            }

                            alphaIndex.put(key, sectionIndex);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (adapter != null)
                                        adapter.addAll(items);
                                }
                            });
                            sectionIndex += items.size();
                        }
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.addAll(result);
                            }
                        });
                    }
                    if (isSearch && result.size() > 0) {
                        Contact friendItem = new Contact();
                        friendItem.setComment_name("好友");
                        result.add(0, friendItem);
                    }
                    Message message = new Message();
                    message.what = NOTIFY ;
                    handler.sendMessage(message);
                }
            });
        }
    }
    
	@Override
	public void onClick(View v) {
        if(!CMainHttp.getInstance().isNetworkAvailable(this)) {
            SouYueToast.makeText(mContext, "网络未连接", 0).show();
            return;
        }
        if(comefromChatDetail) {
            if (vecIds.size() >= 2) {
                showProgressNotBack();
                ImserviceHelp.getInstance().newGroupOp(1, vecIds);
            } else {
                SouYueToast.makeText(CreateGroupInviteActivity.this, "请选择好友", 0).show();
            }
        }else{
            if (vecIds.size() == 1) {
                if (itemContact != null && itemContact.getId() != contactId) {
                    IMChatActivity.invoke(CreateGroupInviteActivity.this, IConst.CHAT_TYPE_PRIVATE, itemContact.getChat_id());
                } else {
                    SouYueToast.makeText(CreateGroupInviteActivity.this, "请选择好友", 0).show();
                }
            } else if (vecIds.size() == 0) {
                SouYueToast.makeText(CreateGroupInviteActivity.this, "请选择好友", 0).show();
            } else {
                showProgressNotBack();
                ImserviceHelp.getInstance().newGroupOp(1, vecIds);
            }
        }

	}
	
	
	 private void setReciever() {
	        IntentFilter inf = new IntentFilter();
	        inf.addAction(BroadcastUtil.ACTION_GROUP_CREATE_SUCCESS);
	        inf.addAction(BroadcastUtil.ACTION_GROUP_CREATE_FAIL);
	        createGroupReceiver = new CreateGroupReceiver();
	        registerReceiver(createGroupReceiver, inf);
	    }
	 
	 private class CreateGroupReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(BroadcastUtil.ACTION_GROUP_CREATE_SUCCESS)){
				dismissProgress();
				String dataExtra = intent.getStringExtra("data");
				Group group =new Gson().fromJson(dataExtra, new TypeToken<Group>() {}.getType());
				if(group != null && group.getSelf_id() == group.getOwner_id()){
                    IMChatActivity.invoke(CreateGroupInviteActivity.this, IConst.CHAT_TYPE_GROUP, group.getGroup_id());
                    CreateGroupInviteActivity.this.finish();
                }
			}else if(intent.getAction().equals(BroadcastUtil.ACTION_GROUP_CREATE_FAIL)){
               dismissProgress();
//                SouYueToast.makeText(CreateGroupInviteActivity.this, "创建群失败", 0).show();

                String json = intent.getStringExtra("data");
                //友好提示
                ImUtils.showImError(json, CreateGroupInviteActivity.this);
			}
		}
		 
	 }

}

