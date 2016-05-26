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
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuita.sdk.BroadcastUtil;
import com.tuita.sdk.TuitaIMManager;
import com.tuita.sdk.im.db.helper.ContactDaoHelper;
import com.tuita.sdk.im.db.helper.PingYinUtil;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.GroupMembers;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.AddGroupMemberAdapter;
import com.zhongsou.souyue.circle.adapter.HorizontalListViewAdapter;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.view.HorizontalListView;
import com.zhongsou.souyue.im.dialog.ImInviteDialog;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.ContactModelUtil;
import com.zhongsou.souyue.im.util.ImUtils;
import com.zhongsou.souyue.im.view.AlphaSideBar;
import com.zhongsou.souyue.im.view.SwipeListView;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.SYInputMethodManager;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.ThreadPoolUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
/**
 * 
 * @ClassName: AddGroupMemberActivity 
 * @Description: 群详情_添加群成员
 * @author gengsong@zhongsou.com
 * @date 2014年9月2日 上午11:30:11 
 * @version 4.0
 */
public class AddGroupMemberActivity extends IMBaseActivity implements OnClickListener{

    private AddGroupMemberAdapter adapter;
    private Map<String, Integer> alphaIndex;
    private SwipeListView swipeListView;
    private TextView txtOverlay;
    private EditText search_edit;
    private Button btnSearchClear;
    private SYInputMethodManager syInputMng;
    private AlphaSideBar alphaBar;

    private LoadContactsTask loadContactsTask;
//    private ImProgressMsgDialog dialog;
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
    
    private boolean isAdmin;
    private String type;

    private Group mgroup;
    private GroupMembers mGroupMember;
    private int currentGroupCount;
    private int mGroupMax_number;

    private CreateGroupReceiver createGroupReceiver;
    private ArrayList<Long> memberIdTmpVec = new ArrayList<Long>();
    private Map<Long, Boolean> hasSelect = new HashMap<Long, Boolean>();
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
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.im_invite_friend_list_view);
		initView();
	}
	
	private void initView() {
		 setReciever();
		 findView(R.id.title_bar_ac_chat_txtbtn).setVisibility(View.GONE);
         mgroup = (Group) getIntent().getSerializableExtra("group");
         mGroupMember = (GroupMembers) getIntent().getSerializableExtra("member");
         currentGroupCount = getIntent().getIntExtra("count",0);
         mGroupMax_number = getIntent().getIntExtra("maxCount", 40); 

         selMembers = (ArrayList<Contact>) getIntent().getSerializableExtra("contact");
         if(selMembers == null) {
             selMembers = new ArrayList<Contact>();
         }
	     sharebuilder = new ImInviteDialog.Builder(AddGroupMemberActivity.this);
	     isSYFriend = this.getIntent().getBooleanExtra("isSYFriend", isSYFriend);
	     TextView title = (TextView) findViewById(R.id.activity_bar_title);
	     title.setText("添加群成员");
	       
	     type =  getIntent().getStringExtra("type");
	     vecStr = new ArrayList<String>();
	     vecIds = new Vector<Long>();
	     contactIdTmpVec = new Vector<Long>();
	     findView(R.id.im_find_friend).setOnClickListener(new OnClickListener() {
				
	    	 @Override
		 public void onClick(View v) {
				// TODO Auto-generated method stub
				AddGroupMemberAdapter.selected.clear();
			    vecIds.clear();
			    vecStr.clear();
			    contactIdTmpVec.clear();

                 InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                 im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                 AddGroupMemberActivity.this.finish();
				}
		  });
	        
	     horizontalListView =(HorizontalListView) findView(R.id.horizon_listview);
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
	     adapter = new AddGroupMemberAdapter(swipeListView, this, alphaIndex, swipeListView.getRightViewWidth());
	     swipeListView.setAdapter(adapter);

	     swipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	          @Override
	         public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
	             syInputMng.hideSoftInput();
	             if (parent instanceof SwipeListView) {
	                 SwipeListView sl = ((SwipeListView) parent);
	                 Contact itemContact = adapter.getItem(position - sl.getHeaderViewsCount());
                     AddGroupMemberAdapter.ViewHolder holder = (AddGroupMemberAdapter.ViewHolder) view.getTag();
                     if(hasSelect.get(itemContact.getChat_id())==null || !hasSelect.get(itemContact.getChat_id())){
	                 holder.checkBox.toggle();
                     AddGroupMemberAdapter.getSelected.put(itemContact.getChat_id(), holder.checkBox.isChecked());
	                    //添加东西
	                		if(AddGroupMemberAdapter.getSelected.get(itemContact.getChat_id())){
	                			friendCount ++;
	                			vecStr.add(itemContact.getAvatar());
	                			vecIds.add(itemContact.getChat_id());
	                			contactIdTmpVec.add(itemContact.getChat_id());
	                			selMembers.add(itemContact);
                                AddGroupMemberAdapter.getSelected.put(itemContact.getChat_id(), true);
	                		}else{
	                			vecStr.remove(itemContact.getAvatar());
	                			friendCount --;
	                			vecIds.remove(itemContact.getChat_id());
	                			contactIdTmpVec.remove(itemContact.getChat_id());
	                			selMembers.remove(itemContact);
                                AddGroupMemberAdapter.getSelected.remove(itemContact.getChat_id());

	                			 for(int i = 0 ; i< selMembers.size() ; i++){
	                                 Contact item = selMembers.get(i);
	                                 if(item.getId() == itemContact.getId()){
	                                     selMembers.remove(item);
	                                     break;
	                                 }
	                             }
	                		}
                            tvConfirmInvite.setText("发送(" +friendCount+")");

	                		adapter2.notifyDataSetChanged();
	                }
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


        for(int i = 0 ; i < selMembers.size() ; i++ ){
//            vecStr.add(selMembers.get(i).getAvatar()); 底部暂不显示了。
            memberIdTmpVec.add(selMembers.get(i).getChat_id());
            AddGroupMemberAdapter.selected.put(memberIdTmpVec.get(i), true);
            hasSelect.put(memberIdTmpVec.get(i), true);
        }
        friendCount = selMembers.size();

        //清0
        friendCount = 0;
        tvConfirmInvite.setText("发送(" +friendCount+")");
        adapter2.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
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
	        if(AddGroupMemberAdapter.selected.size() != 0){
	        	AddGroupMemberAdapter.selected.clear();
	        }
	        if(AddGroupMemberAdapter.getSelected.size() != 0){
	        	AddGroupMemberAdapter.getSelected.clear();
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
								if(null != adapter)
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
						}else{
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
                                    if (adapter != null)
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
            if(friendCount - hasSelect.size() + currentGroupCount>mGroupMax_number) {
                SouYueToast.makeText(AddGroupMemberActivity.this,"群人数超限",Toast.LENGTH_SHORT).show();
                return;
            }
            if(vecIds.size() >= 1){
                if(null != mgroup && null != mGroupMember ) {
                    showProgress();
                    ImserviceHelp.getInstance().addGroupMemberOp(2,Long.toString(mgroup.getGroup_id()),Long.toString(mGroupMember.getMember_id()),1,vecIds,"");
                }

            } else {
                  Toast.makeText(this,"请选择好友",Toast.LENGTH_SHORT).show();
            }
		}
		
		
		private void setReciever() {
			 IntentFilter inf = new IntentFilter();
		     inf.addAction(BroadcastUtil.ACTION_ADD_GROUP);
		     inf.addAction(BroadcastUtil.ACTION_GROUP_CREATE_FAIL);
		     createGroupReceiver = new CreateGroupReceiver();
		     registerReceiver(createGroupReceiver, inf);       
		 }
		 
		 private class CreateGroupReceiver extends BroadcastReceiver{

			@Override
			public void onReceive(Context context, Intent intent) {
                dismissProgress();
				if(intent.getAction().equals(BroadcastUtil.ACTION_ADD_GROUP)){
                    String dataExtra = intent.getStringExtra("data");
                    ArrayList<GroupMembers> list = new Gson().fromJson(dataExtra, new TypeToken<ArrayList<GroupMembers>>() { }.getType());
                    if(list != null && list.get(0).getBy1().equals(TuitaIMManager.ISINVITED)) {
                        Intent intentJump = new Intent(AddGroupMemberActivity.this,NewGroupDetailsActivity.class);
                        intentJump.putExtra("groupMembers", list);
                        intentJump.putExtra("group", mgroup);
                        AddGroupMemberActivity.this.setResult(RESULT_OK, intentJump);
                        finish();
                    }
				}else if(intent.getAction().equals(BroadcastUtil.ACTION_GROUP_CREATE_FAIL)){
                    String json = intent.getStringExtra("data");
                    //友好提示
                    ImUtils.showImError(json, AddGroupMemberActivity.this);
				}
			}
			 
		 }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        finish();
    }
}
