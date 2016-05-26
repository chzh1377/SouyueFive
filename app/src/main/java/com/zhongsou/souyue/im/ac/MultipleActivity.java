package com.zhongsou.souyue.im.ac;

/**
 * 此类已经不再使用 已确认 暂时不删除 下个版本验证无bug可以删除 - YanBin 20151229（v5.1）
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.fragment.MsgTabFragment;
import com.zhongsou.souyue.fragment.MsgTabFragment.NotifyMainListener;
import com.zhongsou.souyue.fragment.MyFragmentTabHost.OnTabClickListener;
import com.zhongsou.souyue.im.emoji.EmojiPattern;
import com.zhongsou.souyue.im.fragment.ChatFragment;
import com.zhongsou.souyue.im.fragment.ContactsListFragment;

public class MultipleActivity extends IMBaseActivity implements OnTabClickListener,NotifyMainListener{
//    private RelativeLayout chat_layout,contact_relayout;
//    private LinearLayout self_layout;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private ChatFragment chatfragment;
    private MsgTabFragment msgTabFragment;
    private ContactsListFragment contactslistfragment;
    //升级到4.0后，修改IFragment-->MyTabFragment
//    private MyTabFragment iFragment;
    private SharedPreferences bubbleSp;
//    public TextView chatcount_text,contactCount_text;
    
    public static final String CHATFRAGMENTTYPE="chatfragment";
    public static final String CONTACTSLISTFRAGMENT="contactslistfragment";
    public static final String ACTION_SHARE="action_share";
    public static final String IFRAGMENT="iFragment";
    private String fragmentType;
//    private ImserviceHelp service = ImserviceHelp.getInstance();
//    private GetBubbleReceiver getbubblereceiver;
//    private UnreadBubleReceiver unreadBubleReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                EmojiPattern.getInstace().getFileText(getApplication());
            }
        }).start();
        bubbleSp = getSharedPreferences("BUBBLESP", MODE_PRIVATE);
        loadBubleData(bubbleSp.getInt("bubblenum", 0));
        init();
    }
    
    private void init(){
        setContentView(R.layout.im_multiple_layout);
        fragmentType=getIntent().getStringExtra("fragmentType");
//        setReciever();
//        setFriendReceiver();
        initView();
    }
    @Override
    public void onResume() {
        super.onResume();
//        loadData();
//        loadFriendData();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("fragmentType", fragmentType);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fragmentType=savedInstanceState.getString("fragmentType");
    }
    public static String getHostIp(Context context) {
        SharedPreferences sp = context.getSharedPreferences("TuitaSDK", Context.MODE_PRIVATE);
        return sp.getString("http://push.souyue.mobi/api/get.mid", "none");
    }

    private void initView() {
//        chat_layout=(RelativeLayout) findViewById(R.id.chat_layout);
//        contact_relayout=(RelativeLayout) findViewById(R.id.conttact_relayout);
//        chat_layout.setOnClickListener(this);
//        contact_relayout.setOnClickListener(this);
//        contact_layout=(LinearLayout) findViewById(R.id.contact_layout);
//        contact_layout.setOnClickListener(this);
//        self_layout=(LinearLayout) findViewById(R.id.self_layout);
//        chatcount_text=(TextView) findViewById(R.id.chatcount_text);
//        contactCount_text=(TextView) findViewById(R.id.contactcount_text);
//        self_layout.setOnClickListener(this);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentType != null) {
            if (fragmentType.equals(CHATFRAGMENTTYPE)) {
//                chatfragment = new ChatFragment();
//                fragmentTransaction.add(R.id.myframe, chatfragment);
//                fragmentTransaction.show(chatfragment);
                msgTabFragment = new MsgTabFragment();
                fragmentTransaction.add(R.id.myframe, msgTabFragment);
                fragmentTransaction.show(msgTabFragment);
            } else if (fragmentType.equals(CONTACTSLISTFRAGMENT)) {
                contactslistfragment = new ContactsListFragment();
                fragmentTransaction.add(R.id.myframe, contactslistfragment);
                fragmentTransaction.show(contactslistfragment);
//                setBackground(contact_relayout,chat_layout,self_layout);
            } else {
//                iFragment = new MyTabFragment();
//                fragmentTransaction.add(R.id.myframe, iFragment);
//                fragmentTransaction.show(iFragment);
////                setBackground(self_layout,chat_layout,contact_relayout);
            }
        } else {
            chatfragment = new ChatFragment();
            fragmentTransaction.add(R.id.myframe, chatfragment);
            fragmentTransaction.show(chatfragment);
//            setBackground(chat_layout,contact_relayout,self_layout);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.chat_layout:
//                setBackground(chat_layout,contact_relayout,self_layout);
//                fragmentTransaction = fragmentManager.beginTransaction();
//                if(chatfragment==null){
//                    chatfragment=new ChatFragment();
//                    fragmentTransaction.add(R.id.myframe, chatfragment);
//                }
//                showFragment(chatfragment,contactslistfragment,iFragment);
//                break;
//            case R.id.conttact_relayout:
//                setBackground(contact_relayout,chat_layout,self_layout);
//                fragmentTransaction = fragmentManager.beginTransaction();
//                if(contactslistfragment==null){
//                    contactslistfragment=new ContactsListFragment();
//                    fragmentTransaction.add(R.id.myframe, contactslistfragment);
//                }
//                showFragment(contactslistfragment,chatfragment,iFragment);
//                break;
//            case R.id.self_layout:
//                setBackground(self_layout,chat_layout,contact_relayout);
//                fragmentTransaction = fragmentManager.beginTransaction();
//                if(iFragment==null){
//                    iFragment=new MyTabFragment();
//                    fragmentTransaction.add(R.id.myframe, iFragment);
//                }
//                showFragment(iFragment,contactslistfragment,chatfragment);
//                break;
//            default:
//                break;
//        }
//    }
    
//    private void showFragment(Fragment show,Fragment hide1,Fragment hide2){
//        
//        if(show!=null){
//            fragmentTransaction.show(show);
//        }
//        if(hide1!=null){
//            fragmentTransaction.hide(hide1);
//        }
//        
//        if(hide2!=null){
//            fragmentTransaction.hide(hide2);
//        }
//        fragmentTransaction.commitAllowingStateLoss();
//    }
// 
//    private void setBackground(ViewGroup layout1,ViewGroup layout2,ViewGroup layout3){
//        layout1.setSelected(true);
//        layout2.setSelected(false);
//        layout3.setSelected(false);
//    }
//    private void setReciever() {
//        IntentFilter inf = new IntentFilter();
//        inf.addAction(BroadcastUtil.ACTION_MSG_ADD);
//        inf.addAction(BroadcastUtil.ACTION_CONTACT_AND_MSG);
//        inf.addAction(BroadcastUtil.ACTION_CLRAR_MESSAGE_BUBBLE);
//        getbubblereceiver = new GetBubbleReceiver();
//        registerReceiver(getbubblereceiver, inf);
//        
//    }
    
//    private void setFriendReceiver(){
//        IntentFilter unfilter = new IntentFilter(BroadcastUtil.ACTION_SYS_MSG);
//        unreadBubleReceiver = new UnreadBubleReceiver();
//        registerReceiver(unreadBubleReceiver, unfilter);
//    }
    
    // 读取未读的新朋友数量的监听
//    public class UnreadBubleReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            loadFriendData();
//        }
//    }
    
//    private void loadData(){
//        int totalcount = 0;
//        if (service != null){
//            Config c = service.db_getConfig();
//            if (c != null){
//                totalcount = c.getTotal_message_bubble(); 
//            }
//        }
//        if(totalcount==0){
//            if(chatcount_text!=null){
//                chatcount_text.setVisibility(View.INVISIBLE);
//            }
//            
//        }else{
//            if(chatcount_text!=null){
//               chatcount_text.setText(ImUtils.getBubleText(String.valueOf(totalcount)));
//               chatcount_text.setVisibility(View.VISIBLE);
//            }
//        }
//
//    }
//    
//    private void loadFriendData(){
//        int contactCount = 0;
//        Config c = service.db_getConfig();
//        if(service!=null){
//            if(c!=null){
//                contactCount = c.getFriend_bubble();
//            }   
//        }
//        if(contactCount == 0){
//            if(contactCount_text != null){
//                contactCount_text.setVisibility(View.INVISIBLE);
//            }
//        }else{
//              if(contactCount_text != null){
//                  contactCount_text.setVisibility(View.VISIBLE);
//                  contactCount_text.setText(ImUtils.getBubleText(String.valueOf(contactCount)));
//               }
//        }
//    }
//    private class GetBubbleReceiver extends BroadcastReceiver{
//        @Override
//        public void onReceive(Context context, Intent intent) {
//                loadData();
//        }
//    }
//    
//    @Override
//    public void onDestroy() {
//        if(getbubblereceiver!=null){
//            unregisterReceiver(getbubblereceiver);
//        }
//        if(unreadBubleReceiver!=null){
//            unregisterReceiver(unreadBubleReceiver);
//        }
//        super.onDestroy();
//    }

    @Override
    public boolean checkLogin(String tabId) {
          
            // TODO Auto-generated method stub  
            return false;
            
    }

    @Override
    public void setCurrentTabByTag(String tabId) {
          
            // TODO Auto-generated method stub  
            
    }

    @Override
    public void setTabViewBageTips(int pos, int newMsgCount) {
          
            // TODO Auto-generated method stub  
            
    }

    @Override
    public void setCurrentTabByIndex(int pos) {
          
            // TODO Auto-generated method stub  
            
    }

    @Override
    public void showRedNum(int num) {
        SharedPreferences.Editor edit = bubbleSp.edit();
        edit.putInt("bubblenum", num);
        edit.commit();
        loadBubleData(num);  
    }
    
 // 显示未读消息气泡数
    public void loadBubleData(int num) {
        if (num != 0) {
            setTabViewBageTips(1, num);
        } else {
            setTabViewBageTips(1, 0);
        }
    }
//    /** 我的询报价 */
//    public void onTradeInquiryClick(View v){
//    	Intent i = new Intent();
//		i.setClass(this, InquiryActivity.class);
//		startActivity(i);
//		overridePendingTransition(R.anim.left_in, R.anim.left_out);
//    }
}
