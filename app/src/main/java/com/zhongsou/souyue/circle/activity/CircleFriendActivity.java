package com.zhongsou.souyue.circle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.adapter.CircleFriendAdapter;
import com.zhongsou.souyue.circle.adapter.HorizontalListViewAdapter;
import com.zhongsou.souyue.circle.model.CircleMemberItem;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.im.dialog.ImProgressMsgDialog;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.MemberListReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.utils.SYInputMethodManager;
import com.zhongsou.souyue.view.HorizontalListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc: 发帖@圈成员列表
 * User: tiansj
 * DateTime: 14-4-17 下午3:13
 */
public class CircleFriendActivity extends BaseActivity implements OnClickListener{

    private static int NOTIFY = 1;

    private CircleFriendAdapter adapter;
    private ListView swipeListView;
    private EditText search_edit;
    private SYInputMethodManager syInputMng;

    private ImProgressMsgDialog dialog;
    private HorizontalListView horizontalListView;
    private HorizontalListViewAdapter adapter2;
    private TextView tvConfirmInvite;
    private int friendCount = 0;
    private ArrayList<String> vecStr;
    private ArrayList<CircleMemberItem> selMembers;
    private ArrayList<Long> memberIdTmpVec;
    private ArrayList<CircleMemberItem> allItems;

    private long interest_id;
    private int pno = 1;
	private int psize = 15;
	private int visibleLast = 0;
	private boolean isSearch ;
	private boolean isLoadAll;
//	private View footerView;
	private TextView tvNoResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_friend_list_view);
        
        pno = 1;
        isSearch = false;
        isLoadAll = false;
        allItems = new ArrayList<CircleMemberItem>();
        interest_id = getIntent().getLongExtra("interest_id", 0L);
        selMembers = (ArrayList<CircleMemberItem>) getIntent().getSerializableExtra("selMembers");
        if(selMembers == null) {
            selMembers = new ArrayList<CircleMemberItem>();
        }
        
//        footerView = getLayoutInflater().inflate(R.layout.ent_refresh_footer,null);
        tvNoResult = (TextView) findViewById(R.id.circle_noresult_tv);
    	tvNoResult.setVisibility(View.GONE);
        TextView title = (TextView) findViewById(R.id.activity_bar_title);
        title.setText("@圈成员");
        tvConfirmInvite = (TextView)findViewById(R.id.invite_confirm_tv);
        vecStr = new ArrayList<String>();
        memberIdTmpVec = new ArrayList<Long>();
        horizontalListView =(HorizontalListView) findViewById(R.id.horizon_listview);
        adapter2 = new HorizontalListViewAdapter(this,vecStr);
        horizontalListView.setAdapter(adapter2);
        
        
        tvConfirmInvite.setOnClickListener(this);

        findViewById(R.id.goBack).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	CircleFriendAdapter.selected.clear();
                finish();
            }
        });

        horizontalListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				vecStr.remove(position);
    			friendCount --;
    			tvConfirmInvite.setText("发送(" +friendCount+")");
        		adapter2.notifyDataSetChanged();
        		CircleFriendAdapter.selected.put(memberIdTmpVec.get(position), false);
        		memberIdTmpVec.remove(position);
        		selMembers.remove(position);
        		adapter.notifyDataSetChanged();
			}
			
        	
		});
       
        swipeListView = (ListView) findViewById(R.id.listView);
//        swipeListView.addFooterView(footerView);
        initListViewHeader();
        adapter = new CircleFriendAdapter( this, allItems);
        swipeListView.setAdapter(adapter);
        tvConfirmInvite.setText("发送(" +friendCount+")");
        swipeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
//				if(view == footerView){
//					return;
//				}
                CircleMemberItem itemContact = (CircleMemberItem) adapter.getItem(position);
                CircleFriendAdapter.ViewHolder holder = (CircleFriendAdapter.ViewHolder) view.getTag();
                holder.checkBox.toggle();
                CircleFriendAdapter.selected.put(itemContact.getMember_id(), holder.checkBox.isChecked());
                if(CircleFriendAdapter.selected.get(itemContact.getMember_id())){
                    friendCount ++;
                    vecStr.add(itemContact.getImage());
                    memberIdTmpVec.add(itemContact.getMember_id());
                    selMembers.add(itemContact);
                }else{
                    friendCount --;
                    vecStr.remove(itemContact.getImage());
                    memberIdTmpVec.remove(itemContact.getMember_id());
                    for(int i = 0 ; i< selMembers.size() ; i++){
                    	CircleMemberItem item = selMembers.get(i);
                    	if(item.getMember_id() == itemContact.getMember_id()){
                    		selMembers.remove(item);
                    		break;
                    	}
                    }
                }
                if(friendCount == 6){
                	UIHelper.ToastMessage(CircleFriendActivity.this, "最多可以选择5个好友");
                	friendCount -- ;
                	 holder.checkBox.toggle();
                     CircleFriendAdapter.selected.put(itemContact.getMember_id(), holder.checkBox.isChecked());
                     vecStr.remove(itemContact.getImage());
                     selMembers.remove(itemContact);
                     memberIdTmpVec.remove(itemContact.getMember_id());
                }
                tvConfirmInvite.setText("发送(" +friendCount+")");
                adapter2.notifyDataSetChanged();
			}
        	
		});
        
        swipeListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                syInputMng.hideSoftInput();
                int itemsLastIndex = 0;
//                if(isLoadAll  && !isSearch){
                	  itemsLastIndex = adapter.getCount();
//                }else{
//                	  itemsLastIndex = adapter.getCount() + 1;
//                }
               
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLast == itemsLastIndex ) {
                	pno += 1;
                	isSearch = false;
                    getMemberList();
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            	visibleLast = firstVisibleItem + visibleItemCount ;
            }
        });

        if (dialog == null)
            dialog = new ImProgressMsgDialog.Builder(this).create();
        dialog.show();

        getMemberList();
        syInputMng = new SYInputMethodManager(this);
       for(int i = 0 ; i < selMembers.size() ; i++ ){
        	vecStr.add(selMembers.get(i).getImage());
            memberIdTmpVec.add(selMembers.get(i).getMember_id());
            CircleFriendAdapter.selected.put(memberIdTmpVec.get(i), true);
        }
        friendCount = selMembers.size();
		tvConfirmInvite.setText("发送(" + friendCount + ")");
		adapter2.notifyDataSetChanged();
		adapter.notifyDataSetChanged();
    }

	public void getMemberListSuccess(HttpJsonResponse res) {
		int statusCode = res.getCode();
		if (statusCode != 200) {
			return;
		}
		swipeListView.setVisibility(View.VISIBLE);
		isLoadAll = false;
		tvNoResult.setVisibility(View.GONE);
		final List<CircleMemberItem> result = new Gson().fromJson(res.getBodyArray(), new TypeToken<List<CircleMemberItem>>() {}.getType());
		
		 if((result == null ||result.size() == 0) && isSearch){
	    	  tvNoResult.setVisibility(View.VISIBLE);
	    	  tvNoResult.setText("无结果");
	    	  isLoadAll = false;
	    	  swipeListView.setVisibility(View.GONE);
	    	  return;
	      }
		
		if (result == null || result.size() < psize) {
			isLoadAll = true;
//			swipeListView.removeFooterView(footerView);
		}
		if (isSearch) {
			allItems.clear();
			adapter.notifyDataSetChanged();
		}
		allItems.addAll(result);
		adapter.notifyDataSetChanged();
		dialog.dismiss();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		swipeListView.setAdapter(null);
		swipeListView = null;
		adapter = null;
		syInputMng.hideSoftInput();
		syInputMng = null;
		if(CircleFriendAdapter.selected.size() != 0){
			CircleFriendAdapter.selected.clear();
		}
		vecStr.clear();
		memberIdTmpVec.clear();
		selMembers.clear();
	}

	private void initListViewHeader() {
		search_edit = (EditText)findViewById(R.id.search_edit);
		search_edit.setHint("圈成员昵称");
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
				if(s.toString().length() == 0 || s.toString().equals("")){
					search_edit.setHint("圈成员昵称");
				}
				
				// TODO 搜索请求
				pno = 1;
				isSearch = true;
				getMemberList();
//				http.getMemberListAll(interest_id, after);
				// loadContactsTask.cancel(true);
				// loadContactsTask = new LoadContactsTask();
				// loadContactsTask.execute(after);

			}
		});
	}
	
	
	
	private void getMemberList() {
        if(isLoadAll && !isSearch) {
            UIHelper.ToastMessage(this, "已全部加载");
            return;
        }
//        http.getMemberList(interest_id,search_edit.getText().toString(), pno, psize);
		MemberListReq req = new MemberListReq(HttpCommon.CIRLCE_MEMBERLIST_ID,this);
		req.setParams(interest_id,search_edit.getText().toString(), pno, psize);
		CMainHttp.getInstance().doRequest(req);
    }

	@Override
	public void onHttpResponse(IRequest request) {
		super.onHttpResponse(request);
		switch (request.getmId())
		{
			case HttpCommon.CIRLCE_MEMBERLIST_ID:
				getMemberListSuccess(request.<HttpJsonResponse>getResponse());
				break;
		}
	}

	@Override
	public void onHttpError(IRequest request) {
		super.onHttpError(request);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.invite_confirm_tv:
			if(selMembers.size() == 0){
				UIHelper.ToastMessage(this, "您还没有邀请好友");
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

}
