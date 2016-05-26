package com.zhongsou.souyue.circle.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.tuita.sdk.im.db.helper.ContactDaoHelper;
import com.tuita.sdk.im.db.helper.PingYinUtil;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.MessageHistory;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.circle.adapter.HorizontalListViewAdapter;
import com.zhongsou.souyue.circle.adapter.ImFriendAdapter;
import com.zhongsou.souyue.circle.model.InviteNoticeItem;
import com.zhongsou.souyue.circle.model.Posts;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.countUtils.ZSSdkUtil;
import com.zhongsou.souyue.im.dialog.ImInviteDialog;
import com.zhongsou.souyue.im.dialog.ImInviteDialog.Builder.ImInviteDialogInterface;
import com.zhongsou.souyue.im.dialog.ImProgressMsgDialog;
import com.zhongsou.souyue.im.dialog.ImShareDialog;
import com.zhongsou.souyue.im.dialog.ImShareDialog.Builder.ImShareDialogInterface;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.ContactModelUtil;
import com.zhongsou.souyue.im.util.IMIntentUtil;
import com.zhongsou.souyue.im.view.AlphaSideBar;
import com.zhongsou.souyue.im.view.SwipeListView;
import com.zhongsou.souyue.module.SharePointInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CircleGetMeberRoleRequest;
import com.zhongsou.souyue.net.circle.InvitationReq;
import com.zhongsou.souyue.net.share.ShareResultRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.SYInputMethodManager;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.view.HorizontalListView;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

//import com.alibaba.fastjson.JSONObject;

/**
 * Desc: 搜悦好友列表
 * User: tiansj
 * DateTime: 14-4-17 下午3:13
 */
public class ImFriendActivity extends RightSwipeActivity implements OnClickListener{

//    private static final int GETMEBERROLE_REQUESTID = 12455;
    private ImFriendAdapter adapter;
    private Map<String, Integer> alphaIndex;
    private SwipeListView swipeListView;
    private TextView txtOverlay;
    private EditText search_edit;
    private SYInputMethodManager syInputMng;
    private AlphaSideBar alphaBar;

    private LoadContactsTask loadContactsTask;
    private ImProgressMsgDialog dialog;
    private HorizontalListView horizontalListView;
    private RelativeLayout rl_horizon;
    private HorizontalListViewAdapter adapter2;
    private ArrayList<String> vecStr;
    private TextView tvConfirmInvite;
    private int friendCount = 0;
//    private Http http;
    private Vector<Long> vecIds;
    private Vector<Long> contactIdTmpVec;

    private long interest_id;
    //是否是搜悦好友
    private boolean isSYFriend = false;
    private String instrest_logo;
    private String instest_name;
    private Posts mainPosts;
    private int type;   //圈子类型
    private boolean isFromBlog;
    private String shareUrl;
    private String srpId;


    private static int NOTIFY = 1;
    private int from_type = -1;
    private ArrayList<Contact> selMembers;

    private ImInviteDialog.Builder sharebuilder;
    private boolean isAdmin;
    private String SHARE_CALLBACK = "10";
    public final static int FROM_TYPE_WEB = 0;

    @SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                    break;
                case 3:
                    adapter.clearData();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_im_friend_list_view);
        findView(R.id.title_bar_ac_chat_txtbtn).setVisibility(View.GONE);
        selMembers = new ArrayList<Contact>();
//        http = new Http(this);
        sharebuilder = new ImInviteDialog.Builder(ImFriendActivity.this);

        from_type = this.getIntent().getIntExtra("fromType", from_type);
        TextView title = (TextView) findViewById(R.id.activity_bar_title);

        interest_id = getIntent().getLongExtra("interest_id", 1001l);
        isSYFriend = this.getIntent().getBooleanExtra("isSYFriend", isSYFriend);
        if(isSYFriend){
            title.setText("您要分享给？");
        }else{
            title.setText("邀请好友");
        }
        instrest_logo = getIntent().getStringExtra("interest_logo");
        if(null == instrest_logo){
            instrest_logo = "http://souyue-image.b0.upaiyun.com/user/0001/91733511.jpg";
        }
        instest_name = getIntent().getStringExtra("interest_name");
        if(null == instest_name){
            instest_name = "圈子名称";
        }
        mainPosts = (Posts) getIntent().getSerializableExtra("Posts");
        type = getIntent().getIntExtra("type", -1);
        isFromBlog = getIntent().getBooleanExtra("isFromBlog", false);
        shareUrl = getIntent().getStringExtra("shareUrl");
        srpId = getIntent().getStringExtra("srpId");


        vecStr = new ArrayList<String>();
        vecIds = new Vector<Long>();
        contactIdTmpVec = new Vector<Long>();
        findView(R.id.im_find_friend).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ImFriendAdapter.selected.clear();
		        vecIds.clear();
		        vecStr.clear();
		        contactIdTmpVec.clear();
				ImFriendActivity.this.finish();
			}
		});
        
        horizontalListView =(HorizontalListView) findView(R.id.horizon_listview);
        rl_horizon = (RelativeLayout) findViewById(R.id.rl_horizon);
        horizontalListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				vecStr.remove(position);
    			friendCount --;
    			vecIds.remove(position);
    			tvConfirmInvite.setText("发送(" +friendCount+")");
    			selMembers.remove(position);
        		adapter2.notifyDataSetChanged();
        		ImFriendAdapter.selected.put(contactIdTmpVec.get(position), false);
        		contactIdTmpVec.remove(position);
        		adapter.notifyDataSetChanged();
        		
			}
        	
		});
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
        adapter = new ImFriendAdapter(swipeListView, this, alphaIndex, swipeListView.getRightViewWidth());
        swipeListView.setAdapter(adapter);
        tvConfirmInvite.setText("发送(" +friendCount+")");
        swipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                syInputMng.hideSoftInput();
                if (parent instanceof SwipeListView) {
                        SwipeListView sl = ((SwipeListView) parent);
                        Contact itemContact = adapter.getItem(position - sl.getHeaderViewsCount());
                    if (rl_horizon.isShown()) {
                        ImFriendAdapter.ViewHolder holder = (ImFriendAdapter.ViewHolder) view.getTag();
                        holder.checkBox.toggle();
                        ImFriendAdapter.selected.put(itemContact.getChat_id(), holder.checkBox.isChecked());
                        //添加东西
                        if (ImFriendAdapter.selected.get(itemContact.getChat_id())) {
                            friendCount++;
                            vecStr.add(itemContact.getAvatar());
                            vecIds.add(itemContact.getChat_id());
                            contactIdTmpVec.add(itemContact.getChat_id());
                            selMembers.add(itemContact);
                        } else {
                            vecStr.remove(itemContact.getAvatar());
                            friendCount--;
                            vecIds.remove(itemContact.getChat_id());
                            contactIdTmpVec.remove(itemContact.getChat_id());
                            selMembers.remove(itemContact);
                        }
                        tvConfirmInvite.setText("发送(" + friendCount + ")");
                        adapter2.notifyDataSetChanged();
                    }else {
                        //搜索结果分享区分私聊群聊   by zhangwb
                        searchResultShare(itemContact);
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

        if (dialog == null)
            dialog = new ImProgressMsgDialog.Builder(this).create();
        dialog.show();
        loadContactsTask = new LoadContactsTask();
        loadContactsTask.execute("", "");// 首次获取

        setSlideBar();
        syInputMng = new SYInputMethodManager(this);
        getIsAdmin();
    }

    /**
     * 搜索结果分享区分群聊和私聊
     * by  zhangwb
     * @param itemContact
     */
    private void searchResultShare(Contact itemContact) {

        selMembers.add(itemContact);
        vecIds.add(itemContact.getChat_id());
        contactIdTmpVec.add(itemContact.getChat_id());
        if(!isSYFriend) { //邀请好友
            ArrayList<InviteNoticeItem> mItem = new ArrayList<InviteNoticeItem>();
                InviteNoticeItem item= new InviteNoticeItem();
                item.setApplicant_pic(itemContact.getAvatar());
                item.setApplicant_nickname(itemContact.getNick_name());
                item.setUser_id(itemContact.getChat_id());
                mItem.add(item);
            if(type == Constant.INTEREST_TYPE_PRIVATE){  //type == 1
                if(isAdmin){
                    UIHelper.showInviteNotice(this, mItem, true, interest_id);
                }else{
                    UIHelper.showInviteNotice(this, mItem,false,interest_id);
                }

            }else{
                openInviteDialog();
            }

        } else { //打开对话框
            openShareDialog();
        }
    }

    private void getIsAdmin() {           //判断是不是圈主
		if (IntentUtil.isLogin()) {
//			http.getMemberRole(SYUserManager.getInstance().getToken(),interest_id);
//            CircleGetMeberRoleRequest circleGetMeberRole = new CircleGetMeberRoleRequest(GETMEBERROLE_REQUESTID, this);
//            circleGetMeberRole.setParams();
//            CMainHttp.getInstance().doRequest(circleGetMeberRole);
            CircleGetMeberRoleRequest.send(HttpCommon.CIRCLE_GETMEBERROLE_REQUESTID,this,SYUserManager.getInstance().getToken(), interest_id);
		} else {
			isAdmin = false;
		}
	}

    @Override
    public void onHttpResponse(IRequest _request) {
        super.onHttpResponse(_request);
        switch (_request.getmId()) {
            case HttpCommon.CIRCLE_GETMEBERROLE_REQUESTID:
                getMemberRoleSuccess(_request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRLCE_INVITATION_ID:
                imFriendSuccess(_request.<HttpJsonResponse>getResponse());
                break;
        }
    }

    @Override
    public void onHttpError(IRequest _request) {
        super.onHttpError(_request);
        switch (_request.getmId()) {
            case HttpCommon.CIRCLE_GETMEBERROLE_REQUESTID:
                break;
            case HttpCommon.CIRLCE_INVITATION_ID:
                UIHelper.ToastMessage(this, "邀请失败");
                finish();
                break;
        }

    }

    public void getMemberRoleSuccess(HttpJsonResponse res) {
        int role = res.getBody().get("role").getAsInt();
        isAdmin = role == 1 ? true : false;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        if(ImFriendAdapter.selected.size() != 0){
        	ImFriendAdapter.selected.clear();
        }
    }
    

    private void initListViewHeader(final View convertView) {
        convertView.findViewById(R.id.new_friends).setVisibility(View.GONE);
        convertView.findViewById(R.id.phone_friends).setVisibility(View.GONE);
        convertView.findViewById(R.id.rl_service_message).setVisibility(View.GONE);
        if(!isSYFriend) {
            convertView.findViewById(R.id.group_chat).setVisibility(View.GONE);
        }
        convertView.findViewById(R.id.group_chat).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                IMIntentUtil.showImFriend(ImFriendActivity.this,interest_id,isSYFriend,instrest_logo,instest_name,mainPosts,type
                ,isFromBlog,shareUrl,srpId,IMIntentUtil.CIRCLE,from_type);
            }
        });
        search_edit = (EditText) convertView.findViewById(R.id.search_edit);
        search_edit.setHint(isSYFriend ? R.string.search_has_group : R.string.search_no_group);
        Button clearBtn = (Button)convertView.findViewById(R.id.btn_search_clear);
        clearBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                search_edit.setText("");
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
                    if (isSYFriend) {
                        convertView.findViewById(R.id.group_chat).setVisibility(View.VISIBLE);
                        rl_horizon.setVisibility(View.VISIBLE);
                    }
                    adapter.setKeyWord(null);
                }else{
                    if (isSYFriend) {
                        convertView.findViewById(R.id.group_chat).setVisibility(View.GONE);
                        rl_horizon.setVisibility(View.GONE);
                    }
                }
                if (before != null && before.equals(after)) {// 没有改变
                    return;
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
//                data = ContactDaoHelper.getInstance(
//                        MainApplication.getInstance())
//                        .findLike(muid, keyword_F);
                if (isSYFriend) {
                    data = ImserviceHelp.getInstance().db_findLike(keyword_F);
                }else {
                    data = ContactDaoHelper.getInstance(
                            MainApplication.getInstance())
                            .findLike(muid, keyword_F);
                }
                adapter.setKeyWord(sourceKey.toUpperCase().replace(" ",""));
            }

            return convertResult((List<Contact>) (data == null ? Collections.emptyList() : data));
        }

        protected void onPostExecute(final List<Contact> result) {
            if (needLoad) {
                ImserviceHelp.getInstance().im_info(4, null);
            }


            if(null!=adapter) {
                adapter.addAll(result);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
//            ThreadPoolUtil.getInstance().execute(new Runnable() {

//                @Override
//                public void run() {
//                    boolean isSearch = false;
//                    if (!TextUtils.isEmpty(search_edit.getText().toString())) {
//                        isSearch = true;
//                    }
//
//                    LogDebugUtil.v("fan", result.size() + "总共");
//                    Map<String, List<Contact>> sectionItems = new HashMap<String, List<Contact>>();
//                    List<Contact> finalRes = null;
//                    for (int i = 0; i < result.size(); i++) {
//                        Contact item = result.get(i);
//                        String lhs_p = PingYinUtil.getPingYin(ContactModelUtil
//                                .getShowName(item));
//                        if (TextUtils.isEmpty(lhs_p))
//                            continue;
//
//                        String key = lhs_p.substring(0, 1).toUpperCase();
//                        if ("A".compareTo(key) > 0 || "Z".compareTo(key) < 0) {
//                            key = "#";
//                        }
//
//                        if (!sectionItems.containsKey(key)) {
//                            finalRes = new ArrayList<Contact>();
//                            sectionItems.put(key, finalRes);
//                        } else {
//                            finalRes = sectionItems.get(key);
//                        }
//                        finalRes.add(item);
//                    }
//
//                    List<String> keys = new ArrayList<String>(sectionItems
//                            .keySet());
//                    Collections.sort(keys, new Comparator<String>() {
//
//                        @Override
//                        public int compare(String lhs, String rhs) {
//                            if (lhs.equals(rhs)) {
//                                return 0;
//                            } else if ("#".equals(lhs) && !"#".equals(rhs)) {
//                                return 1;
//                            } else if (!"#".equals(lhs) && "#".equals(rhs)) {
//                                return -1;
//                            }
//                            return lhs.compareTo(rhs);
//                        }
//                    });
//                    adapter.clearData();
//                    int sectionIndex = 0;
//                    if (!isSearch) {
//                        for (String key : keys) {
//                            List<Contact> items = sectionItems.get(key);
//                            Collections.sort(items, new Comparator<Contact>() {
//
//                                @Override
//                                public int compare(Contact lhs, Contact rhs) {
//                                    String lhs_p = PingYinUtil
//                                            .getPingYin(ContactModelUtil
//                                                    .getShowName(lhs));
//                                    String rhs_p = PingYinUtil
//                                            .getPingYin(ContactModelUtil
//                                                    .getShowName(rhs));
//                                    return lhs_p.compareTo(rhs_p);
//                                }
//                            });
//
//                            if (!isSearch) {// 追加索引字母
//                                Contact indexItem = new Contact();
//                                indexItem.setComment_name(key);
//                                items.add(0, indexItem);
//                            }
//
//                            alphaIndex.put(key, sectionIndex);
//                            adapter.addAll(items);
//
//                            sectionIndex += items.size();
//                        }
//                    }else{
//                        adapter.addAll(result);
//                    }
////                        Message message = new Message();
////                        message.what = NOTIFY;
////                        handler.sendMessage(message);
//            adapter.notifyDataSetChanged();
//            dialog.dismiss();
//                }
//            });
        }
    }

    private  List<Contact> convertResult;
    private List<Contact> convertResult(List<Contact> result){
        convertResult = new ArrayList<Contact>();
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
        Message msg = new Message();
        msg.what = 3;
        handler.sendMessage(msg);
//        adapter.clearData();
        int sectionIndex = 0;
        if (!isSearch){
            for (String key : keys) {
                List<Contact> items = sectionItems.get(key);
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
                convertResult.addAll(items);
                sectionIndex += items.size();
            }
        }else{
            convertResult.addAll(result);
        }
//            if (isSearch && convertResult.size() > 0) {
//                Contact friendItem = new Contact();
//                friendItem.setComment_name("好友");
//                convertResult.add(0, friendItem);
//            }
        return convertResult;
    }

//    }

    private String getStrIds(Vector<Long> ids){
    	String str = "";
    	for(int i= 0 ; i< ids.size();i++){
    		if(i != ids.size() - 1){
    			str += ids.get(i)+",";
    		}else{
    			str += ids.get(i);
    		}
    		
    	}
    	return str;
    }
    
    public void imFriendSuccess(HttpJsonResponse res){
    	int statusCode = res.getCode();
        if(statusCode != 200){
        	UIHelper.ToastMessage(this, "邀请失败");
            return;
        }
      //发送文本消息
      		for(int i = 0 ; i< vecIds.size() ; i++){
      			String str = sharebuilder.getDesc_text().toString().replace(" ", "");
      			if(str.length() != 0){
      				ImserviceHelp.getInstance().im_sendMessage(selMembers.get(i).getChat_type(), selMembers.get(i).getChat_id(),MessageHistory.CONTENT_TYPE_TEXT, sharebuilder.getDesc_text(),"" );
      			}
      			//搜悦统计   邀请好友加入圈子成功
      			UpEventAgent.onGroupJoin(ImFriendActivity.this, interest_id+"."+instest_name, "");
      		}
		inviteFrienSendMessage(vecIds);
        UIHelper.ToastMessage(this, "邀请成功");
        Intent intent = new Intent();
        setResult(UIHelper.RESULT_CODE_IMFRIEND, intent);
        finish();
    }

    private void inviteFrienSendMessage(Vector<Long> vec) {
        //发送图片消息
        try {
            for (int i = 0; i < vec.size(); i++) {
                JSONObject json = new JSONObject();
                json.put("interest_logo", instrest_logo);
                json.put("interest_id", interest_id);
                json.put("interest_name", instest_name);
                ImserviceHelp.getInstance().im_sendMessage(selMembers.get(i).getChat_type(), selMembers.get(i).getChat_id(), MessageHistory.CONTENT_TYPE_INTEREST_ADD_FRIEND, json.toString(), "");
            }
        } catch (Exception e) {

        }
    }

    private void invitePrivateFrienSendMessage(Vector<Long> vec) {
        //发送图片消息
        try {
            for (int i = 0; i < vec.size(); i++) {
                JSONObject json = new JSONObject();
                json.put("interest_logo", instrest_logo);
                json.put("interest_id", interest_id);
                json.put("interest_name", instest_name);
                ImserviceHelp.getInstance().im_sendMessage(selMembers.get(i).getChat_type(), selMembers.get(i).getChat_id(), MessageHistory.CONTENT_TYPE_INTEREST_ADD_FRIEND_PRIVATE, json.toString(), "");
            }
        } catch (Exception e) {

        }
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//发送好友邀请 
		if(vecIds.size() == 0){
			UIHelper.ToastMessage(this, "您还没有邀请好友");
			return;
		}
		if(!isSYFriend) { //邀请好友
			ArrayList<InviteNoticeItem> mItem = new ArrayList<InviteNoticeItem>();
				for(int i=0 ; i< selMembers.size(); i++){
				InviteNoticeItem item= new InviteNoticeItem();
				item.setApplicant_pic(selMembers.get(i).getAvatar());
				item.setApplicant_nickname(selMembers.get(i).getNick_name());
				item.setUser_id(selMembers.get(i).getChat_id());
				mItem.add(item);
			}
			if(type == Constant.INTEREST_TYPE_PRIVATE){  //type == 1
				if(isAdmin){
					UIHelper.showInviteNotice(ImFriendActivity.this, mItem,true,interest_id);
				}else{
					UIHelper.showInviteNotice(ImFriendActivity.this, mItem,false,interest_id);
				}
				
			}else{
				openInviteDialog();
			}
			
		} else { //打开对话框 
			openShareDialog();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == UIHelper.RESULT_CODE_INVITE_FRIEND_OK){
			String str = data.getStringExtra("user_ids").substring(1, data.getStringExtra("user_ids").length() - 1);
			if(str.equals("")){
				setResult(UIHelper.RESULT_CODE_IMFRIEND);
				ImFriendActivity.this.finish();
				return;
			}
			String[] ids = str.split(",");
			if(ids.length == 0){
				setResult(UIHelper.RESULT_CODE_IMFRIEND);
				ImFriendActivity.this.finish();
				return;
			}
			Vector<Long> vec = new Vector<Long>();
			for(int i= 0; i< ids.length; i++){
				vec.add(Long.parseLong(ids[i]));
			}
			if(isAdmin){
				 //发送文本消息
	      		for(int i = 0 ; i< vec.size() ; i++){
	      			String str1 = "我已经邀请你加入" + instest_name+"圈，快来看看吧！" ;
	      			if(str.length() != 0){
	      				ImserviceHelp.getInstance().im_sendMessage(selMembers.get(i).getChat_type(), selMembers.get(i).getChat_id(),MessageHistory.CONTENT_TYPE_TEXT, str1,"" );
	      			}
	      		}
				invitePrivateFrienSendMessage(vec);
			}
			setResult(UIHelper.RESULT_CODE_IMFRIEND);
			ImFriendActivity.this.finish();
		}
        if(resultCode == IMIntentUtil.SHAREGROUP){
            finish();
        }
	}

//	@Override
//	public void onHttpError(String methodName, AjaxStatus status) {
//        if("imFriend".equals(methodName)){
//            UIHelper.ToastMessage(this, "邀请失败");
//            finish();
//        }
//	}
	
	/**
	 * 邀请好友对话框
	 */
	
	
	private void openInviteDialog() {
		sharebuilder.setImgHeader(instrest_logo);
		sharebuilder.setContent(instest_name);
		sharebuilder.setDesc("我已经邀请您加入" + instest_name + "兴趣圈，快来看看吧");
		sharebuilder.setPositiveButton(R.string.im_dialog_ok,	new ImInviteDialogInterface() {
			@Override
			public void onClick(DialogInterface dialog, View v) {
//				http.imFriend(interest_id, getStrIds(vecIds));

                InvitationReq req = new InvitationReq(HttpCommon.CIRLCE_INVITATION_ID,ImFriendActivity.this);
                req.setParams(interest_id, getStrIds(vecIds), ZSSdkUtil.OTHER_SUBSCRIBE_MENU);
                CMainHttp.getInstance().doRequest(req);

			}

		}).create().show();		
						
	}



	/**
	 * 分享对话框
	 */
	private void openShareDialog() {
		search_edit.clearFocus();

		final ImShareDialog.Builder sharebuilder = new ImShareDialog.Builder(ImFriendActivity.this);
		sharebuilder.setImgHeader(instrest_logo);
		if (!isFromBlog){
			sharebuilder.setContent(instest_name);
		}else{
			if(!StringUtils.isEmpty(mainPosts.getTitle())){
			    sharebuilder.setContent(mainPosts.getTitle());
			}else{
			    sharebuilder.setContent(mainPosts.getContent());
			}
		}
		
		sharebuilder.setPositiveButton(R.string.im_dialog_ok,	new ImShareDialogInterface() {
			@Override
			public void onClick(DialogInterface dialog, View v) {
                if(!TextUtils.isEmpty(sharebuilder.getDesc_text())) {
                    //发送文本消息
                    for(int i = 0 ; i< vecIds.size() ; i++){
                        ImserviceHelp.getInstance().im_sendMessage(selMembers.get(i).getChat_type(), selMembers.get(i).getChat_id(),MessageHistory.CONTENT_TYPE_TEXT, sharebuilder.getDesc_text(),"" );
                    }
                }
                if (from_type == FROM_TYPE_WEB){
                    shareGreettingCard();
                }else {
                    shareToSYFriends();
                }
            }

		}).create().show();
		
						
	}
	
	/**
	 * 分享到搜悦好友
	 */
	private void shareToSYFriends() {
        try {
            for(int i = 0 ; i< vecIds.size() ; i++){
                JSONObject json = new JSONObject();
                json.put("interest_id", interest_id);
                json.put("blog_logo", instrest_logo);
                if(!isFromBlog){
                    json.put("blog_title", instest_name);
                    ImserviceHelp.getInstance().im_sendMessage(selMembers.get(i).getChat_type(), selMembers.get(i).getChat_id(),MessageHistory.CONTENT_TYPE_INTEREST_CIRCLE_CARD, json.toString(),"" );
                }else{
                    if(type == Constant.INTEREST_TYPE_PRIVATE){  //type == 1
                        json.put("blog_title", mainPosts.getTitle());
                        ImserviceHelp.getInstance().im_sendMessage(selMembers.get(i).getChat_type(), selMembers.get(i).getChat_id(),MessageHistory.CONTENT_TYPE_INTEREST_CIRCLE_CARD, json.toString(),"" );
                    }else{
                        json.put("blog_title", mainPosts.getTitle());
                        json.put("blog_id", mainPosts.getBlog_id());
                        json.put("blog_content", mainPosts.getContent());
                        json.put("user_id", mainPosts.getUser_id());
                        json.put("is_prime", mainPosts.getIs_prime());
                        json.put("top_status", mainPosts.getTop_status());
                        ImserviceHelp.getInstance().im_sendMessage(selMembers.get(i).getChat_type(), selMembers.get(i).getChat_id(),MessageHistory.CONTENT_TYPE_INTEREST_SHARE, json.toString(),"" );
                    }

                }
                SouYueToast.makeText(this, R.string.share_success, Toast.LENGTH_LONG).show();
                SharePointInfo info = new SharePointInfo();
                info.setPlatform(SHARE_CALLBACK);
                info.setSrpId(srpId);
                info.setUrl(shareUrl);
//				http.userSharePoint(info);
                ShareResultRequest.send(HttpCommon.SHARE_RESULT_REQUESTID,null,info);
                this.finish();
            }
        } catch (Exception e) {

        }

	}

    /**
     * 分享贺卡
     * add  by  zhangwb
     */
    private void shareGreettingCard() {
        try {
            for (int i = 0; i < vecIds.size(); i++) {
                JSONObject json = new JSONObject();
//            json.put("srpId", mainPosts.getSrpId());
                json.put("image_url", instrest_logo);
                json.put("title", mainPosts.getTitle());
                json.put("url", mainPosts.getUrl());
                ImserviceHelp.getInstance().im_sendMessage(selMembers.get(i).getChat_type(), selMembers.get(i).getChat_id(), MessageHistory.CONTENT_TYPE_WEB, json.toString(), "");

                SouYueToast.makeText(this, R.string.share_success, Toast.LENGTH_LONG).show();
//            SharePointInfo info = new SharePointInfo();
//            info.setPlatform(SHARE_CALLBACK);
//            info.setSrpId(srpId);
//            info.setUrl(shareUrl);
//            http.userSharePoint(info);
                this.finish();
            }
        }catch(Exception e){

        }
    }

	
}

