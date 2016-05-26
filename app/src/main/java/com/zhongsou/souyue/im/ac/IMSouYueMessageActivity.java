package com.zhongsou.souyue.im.ac;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import com.tuita.sdk.BroadcastUtil;
import com.tuita.sdk.im.db.module.Cate;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.ServiceMessageRecent;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.im.adapter.IMSouYueMessageAdapter;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.view.SwipeListView;
import com.zhongsou.souyue.utils.ThreadPoolUtil;

import java.util.List;

/**
 * 服务号列表页
 * @author zhangwb
 *
 */
public class IMSouYueMessageActivity extends IMBaseActivity implements OnClickListener,OnItemClickListener{
    private static final String EXTRA_CATE_ID = "cate_id";
    private TextView tvTitle;
    private SwipeListView swipelistview;
    private IMSouYueMessageAdapter souYueMessageAdapter;
    private List<ServiceMessageRecent> serviceMsgReList;
    private SystemReceiver mSystemReceiver;
    private Cate mCate;
    private long mCateId;
    private MessageRecentReceiver messageRecentReceiver;
    
    private Handler handler = new Handler(){
    	
    	public void handleMessage(android.os.Message msg) {
    		
    	}

    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_souyue_message_layout);
        initView();
        loadData();

        IntentFilter inf = new IntentFilter();
        inf.addAction(BroadcastUtil.ACTION_MSG_ADD);
        inf.addAction(BroadcastUtil.ACTION_CONTACT_AND_MSG);
        inf.addAction(BroadcastUtil.ACTION_CLRAR_MESSAGE_BUBBLE);
        inf.addAction(BroadcastUtil.ACTION_MSG_SEND_SUCCESS);
        messageRecentReceiver = new MessageRecentReceiver();
       registerReceiver(messageRecentReceiver, inf);

        ImserviceHelp.getInstance().db_clearMessageRecentBubble(
                mCateId);
        ImserviceHelp.getInstance().clearCateBubble(mCateId);

    }

    private void loadData() {
    	//1、通过cateId查找serviceMsg
        mCateId = getIntent().getLongExtra(EXTRA_CATE_ID,0l);
        mCate = ImserviceHelp.getInstance().getCate(mCateId);
    	serviceMsgReList = ImserviceHelp.getInstance().db_getServiceMsgRe(mCateId);

        tvTitle.setText(mCate.getCate_name());
        swipelistview.setAdapter(souYueMessageAdapter);
        swipelistview.setSwipeAble(false);
        souYueMessageAdapter.setData(serviceMsgReList);
    }
    private BroadcastReceiver netReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(manager != null){
                NetworkInfo netInfo = manager.getActiveNetworkInfo();
                if(netInfo != null && netInfo.isConnected() && netInfo.isAvailable()){
                    loadData();
                }
            }
        }
    };
    private void initView() {
        tvTitle = findView(R.id.activity_bar_title);
        swipelistview=(SwipeListView) findViewById(R.id.delete_lv_list);
        swipelistview.setOnItemClickListener(this);
        setReciever();
        souYueMessageAdapter = new IMSouYueMessageAdapter(this,swipelistview);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_btn:
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if(mSystemReceiver !=null){
            unregisterReceiver(mSystemReceiver);
        }
        if(netReceiver!=null){
            unregisterReceiver(netReceiver);
        }
        if (messageRecentReceiver != null) {
            unregisterReceiver(messageRecentReceiver);
        }

        super.onDestroy();
    }
    private void setReciever() {
        IntentFilter inf = new IntentFilter();
        inf.addAction(BroadcastUtil.ACTION_SYS_MSG);
        mSystemReceiver = new SystemReceiver();
        registerReceiver(mSystemReceiver, inf);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netReceiver, filter);
    }
    private class SystemReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null){
                loadData();
            }
            
        }
        
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UmengStatisticUtil.onEvent(this, UmengStatisticEvent.IM_LIST_SERVICE_CLICK);
    	ServiceMessageRecent item = souYueMessageAdapter.getItem(position);
        IMChatActivity.invoke(IMSouYueMessageActivity.this, IConst.CHAT_TYPE_SERVICE_MESSAGE, item.getService_id());

    }
    @Override
    public void finish() {
    	 ThreadPoolUtil.getInstance().execute(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ImserviceHelp.getInstance().db_clearFriendBubble();
				}
			});
        super.finish();
    }

	private class MessageRecentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadData();
        }

    }

    public static void invoke(Activity activity,long cateId){
        Intent intent = new Intent(activity, IMSouYueMessageActivity.class);
        intent.putExtra(EXTRA_CATE_ID, cateId);
        activity.startActivity(intent);
        if (activity instanceof Activity){
            ((Activity)activity).overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        loadData();
    }
}
