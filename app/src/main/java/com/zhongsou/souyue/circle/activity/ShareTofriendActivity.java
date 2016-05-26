package com.zhongsou.souyue.circle.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.circle.adapter.CircleFriendAdapter;
import com.zhongsou.souyue.circle.adapter.HorizontalListViewAdapter;
import com.zhongsou.souyue.circle.adapter.ShareToFriendAdapter;
import com.zhongsou.souyue.circle.model.InterestMemberInfo;
import com.zhongsou.souyue.circle.model.ShareContent;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.im.dialog.ImProgressMsgDialog;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CircleGetCircleListRequest;
import com.zhongsou.souyue.net.circle.CircleSyShareReq;
import com.zhongsou.souyue.net.share.SharePostToDigistRequest;
import com.zhongsou.souyue.net.volley.CSouyueHttpError;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.SYInputMethodManager;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.ThreadPoolUtil;
import com.zhongsou.souyue.view.HorizontalListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
/**
 * 
 * @ClassName: ShareTofriendActivity 
 * @Description: 分享帖子到兴趣圈
 * @author gengsong@zhongsou.com
 * @date 2014年5月5日 下午3:02:55 
 * @version 3.5.2
 */
public class ShareTofriendActivity extends RightSwipeActivity implements OnClickListener{

	private static final int SHAREOISTTODIGIST_REQUSTID = 56465; // 分享到精华区
	private static final int CIRCLEGETCIRCLELIST_REQUESTID = 465489; // 请求订阅圈子的列表
	private ShareToFriendAdapter adapter;
    private ListView swipeListView;
    private EditText search_edit;
    private SYInputMethodManager syInputMng;

    private ImProgressMsgDialog dialog;
    private HorizontalListView horizontalListView;
    private HorizontalListViewAdapter adapter2;
    private TextView tvConfirmInvite;
    private int friendCount = 0;
//    private Http http;
    private long posts_id;
    private String interest_name; //统计新增
    private long interest_id;
    private boolean isFromPosts;
    private String token;
    private ArrayList<String> vecStr;
    private Vector<Long> vecIds;
    private ArrayList<InterestMemberInfo> selMembers;
    private ArrayList<Long> memberIdTmpVec;
    private ShareContent shareContent;
    private ArrayList<InterestMemberInfo> allItems;


    private static int NOTIFY = 1;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if(adapter!=null){
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    };
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_share_post_layout);
		allItems = new ArrayList<InterestMemberInfo>();
		initListViewHeader();
//		http = new Http(this);
		posts_id = this.getIntent().getLongExtra("posts_id", 0);
		token = SYUserManager.getInstance().getToken();
        interest_name = this.getIntent().getStringExtra("interest_name");
		interest_id = this.getIntent().getLongExtra("interest_id", 0);
		isFromPosts = this.getIntent().getBooleanExtra("isFromPosts", isFromPosts);
        shareContent = (ShareContent) getIntent().getSerializableExtra("ShareContent");
		initView();
	}
	
	private void  initView() {
		TextView activity_bar_title = (TextView) this.findViewById(R.id.activity_bar_title);
		activity_bar_title.setText(R.string.share_to_interest);
		
	    selMembers = (ArrayList<InterestMemberInfo>) getIntent().getSerializableExtra("selMembers");
	    if(selMembers == null) {
	         selMembers = new ArrayList<InterestMemberInfo>();
	    }

	    tvConfirmInvite = (TextView)findViewById(R.id.invite_confirm_tv);
        vecStr = new ArrayList<String>();
        vecIds = new Vector<Long>();
        memberIdTmpVec = new ArrayList<Long>();
        horizontalListView =(HorizontalListView) findViewById(R.id.horizon_listview);
        adapter2 = new HorizontalListViewAdapter(this,vecStr);
        horizontalListView.setAdapter(adapter2);
        
        
        tvConfirmInvite.setOnClickListener(this);

        findViewById(R.id.goBack).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
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
    			vecIds.remove(position);
    			tvConfirmInvite.setText("发送(" +friendCount+")");
        		adapter2.notifyDataSetChanged();
        		ShareToFriendAdapter.selected.put(memberIdTmpVec.get(position), false);
        		memberIdTmpVec.remove(position);
        		selMembers.remove(position);
        		adapter.notifyDataSetChanged();
			}
		});
       
        swipeListView = (ListView) findViewById(R.id.listView);
        adapter = new ShareToFriendAdapter(this,allItems);
        swipeListView.setAdapter(adapter);
        tvConfirmInvite.setText("发送(" +friendCount+")");
        swipeListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				syInputMng.hideSoftInput();
                InterestMemberInfo itemContact = (InterestMemberInfo) adapter.getItem(position);
                ShareToFriendAdapter.ViewHolder holder = (ShareToFriendAdapter.ViewHolder) view.getTag();
                holder.checkBox.toggle();
                ShareToFriendAdapter.selected.put(itemContact.getInterest_id(), holder.checkBox.isChecked());
                if(ShareToFriendAdapter.selected.get(itemContact.getInterest_id())){
                    if(friendCount >= 3){
                        holder.checkBox.toggle();
                        ShareToFriendAdapter.selected.put(itemContact.getInterest_id(), holder.checkBox.isChecked());
                        Toast.makeText(ShareTofriendActivity.this, "最多可选择3个", Toast.LENGTH_LONG).show();
                        return;
                    }
                    friendCount ++;
                    vecStr.add(itemContact.getInterest_logo());
                	vecIds.add(itemContact.getInterest_id());
                    memberIdTmpVec.add(itemContact.getInterest_id());
                    selMembers.add(itemContact);
                }else{
                    friendCount --;
                    vecStr.remove(itemContact.getInterest_logo());
                	vecIds.remove(itemContact.getInterest_id());
                    memberIdTmpVec.remove(itemContact.getInterest_id());
                    for(int i = 0 ; i< selMembers.size() ; i++){
                    	InterestMemberInfo item = selMembers.get(i);
                    	if(item.getInterest_id() == itemContact.getInterest_id()){
                    		selMembers.remove(item);
                    		break;
                    	}
                    }
                }
                tvConfirmInvite.setText("发送(" +friendCount+")");
                adapter2.notifyDataSetChanged();
                horizontalListView.setSelection(adapter2.getCount()-1);
			}
		});
        
        swipeListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                syInputMng.hideSoftInput();
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            	//上拉加载
            }
        });

		if (dialog == null)
			dialog = new ImProgressMsgDialog.Builder(this).create();
		dialog.show();

		/**
		 * 传interest_id 过滤当前圈子，否者传0
		 */
		if (!isFromPosts) {
			interest_id = 0;
		}
//        http.getInterestListAll(token, interest_id, " ");
		CircleGetCircleListRequest.send(HttpCommon.CIRCLE_GETCIRCLELIST_REQUESTID,this,token, interest_id, " ");
		syInputMng = new SYInputMethodManager(this);
		for (int i = 0; i < selMembers.size(); i++) {
			vecStr.add(selMembers.get(i).getInterest_logo());
			memberIdTmpVec.add(selMembers.get(i).getInterest_id());
			CircleFriendAdapter.selected.put(memberIdTmpVec.get(i), true);
		}
        friendCount = selMembers.size();
		tvConfirmInvite.setText("发送(" + friendCount + ")");
		adapter2.notifyDataSetChanged();
		adapter.notifyDataSetChanged();

	}


	public void onBackPressClick(View view) {
		this.finish();
	}
	
	 public void getInterestListAllSuccess(HttpJsonResponse res){
//	        int statusCode = res.getCode();
//	        if(statusCode != 200) {
//	            return;
//	        }
	        final List<InterestMemberInfo> result = new Gson().fromJson(res.getBodyArray(), new TypeToken<List<InterestMemberInfo>>() {}.getType());
	        if(result.size()<=0) {
	        	SouYueToast.makeText(this, R.string.cricle_no_data, Toast.LENGTH_SHORT).show();
	        }
	        ThreadPoolUtil.getInstance().execute(new Runnable() {

	            @Override
	            public void run() {
	                boolean isSearch = false;
	                if (!TextUtils.isEmpty(search_edit.getText().toString())) {
	                    isSearch = true;
	                }
	                allItems.clear();
	                allItems.addAll(result);
					Message message = new Message();
					message.what = NOTIFY;
					handler.sendMessage(message);
				}
			});
		}

	 
	@Override
	protected void onDestroy() {
		super.onDestroy();
		adapter = null;
		syInputMng.hideSoftInput();
		syInputMng = null;
		ShareToFriendAdapter.selected.clear();
		vecStr.clear();
		memberIdTmpVec.clear();
		selMembers.clear();
	}

	private void initListViewHeader() {
		search_edit = (EditText)findViewById(R.id.search_edit);
		search_edit.addTextChangedListener(new TextWatcher() {
			String before = null;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				before = s.toString();
			}

			@Override
			public void afterTextChanged(Editable s) {
				String after = s.toString();
				if(s.toString().length() == 0 || s.toString().equals("")){
					search_edit.setHint("圈名称");
				}
				if (before != null && before.equals(after)) {// 没有改变
					return;
				}
//				http.getInterestListAll(token, interest_id, after);
				CircleGetCircleListRequest.send(HttpCommon.CIRCLE_GETCIRCLELIST_REQUESTID,ShareTofriendActivity.this,token, interest_id, after);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private String getStrIds(Vector<Long> ids) {
		String str = "";
		for (int i = 0; i < ids.size(); i++) {
			if (i != ids.size() - 1) {
				str += ids.get(i) + ",";
			} else {
				str += ids.get(i);
			}

		}
		return str;
	}



	@Override
	public void onClick(View v) {
		// 发送好友邀请
		if(selMembers.size() == 0){
			UIHelper.ToastMessage(this, "您还没有邀请好友");
			return;
		}
		if (dialog == null)
			dialog = new ImProgressMsgDialog.Builder(ShareTofriendActivity.this).create();
		    dialog.show();
        // 圈内帖子分享到兴趣圈
        if(posts_id > 0) {
//            http.sharePostToDigist(posts_id, token, getStrIds(vecIds));
			SharePostToDigistRequest.send(HttpCommon.SHARE_POSTTODIGIST_REQUESTID,this,posts_id, token, getStrIds(vecIds));
        } else {    // 新闻或远程分享到兴趣圈
        	if(shareContent != null) {
        		shareContent.setInterest_ids(getStrIds(vecIds));
        		//http.newsShare(shareContent);

              CircleSyShareReq req = new CircleSyShareReq(HttpCommon.CIRCLE_SYSHARE_REQUEST,this);
              req.addParams(shareContent);
              mMainHttp.doRequest(req);
        	}
        }
	}

	@Override
	public void onHttpResponse(IRequest request) {
		switch (request.getmId()){
			case HttpCommon.SHARE_POSTTODIGIST_REQUESTID:
				sharePostToDigistSuccess(request.<HttpJsonResponse>getResponse());
				break;
			case HttpCommon.CIRCLE_GETCIRCLELIST_REQUESTID:
				getInterestListAllSuccess(request.<HttpJsonResponse>getResponse());
				break;
                        case HttpCommon.CIRCLE_SYSHARE_REQUEST:
                           newsShareSuccess((HttpJsonResponse)request.getResponse());
                            break;
		}
	}

	@Override
	public void onHttpError(IRequest request) {
		IHttpError volleyError = request.getVolleyError();
		if (dialog != null)
			dialog.dismiss();
		switch (request.getmId()){
			case HttpCommon.SHARE_POSTTODIGIST_REQUESTID:
                              case HttpCommon.CIRCLE_SYSHARE_REQUEST:
				if(volleyError.getErrorType() == IHttpError.TYPE_SERVER_ERROR){
					int statusCode = volleyError.getErrorCode();
					if(statusCode == 500){
//						new HttpContextImpl("sharePostToDigist").onJsonCodeError(request.<HttpJsonResponse>getResponse()); // json状态码错误
						return;
					}
					if(statusCode != 200){
//						UIHelper.ToastMessage(this, volleyError.getmErrorMessage());
						return;
					}
				}else{
					SouYueToast.makeText(ShareTofriendActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
				}
				break;

			case HttpCommon.CIRCLE_GETCIRCLELIST_REQUESTID:
//				getInterestListAllSuccess(request.<HttpJsonResponse>getResponse());
				break;
		}
	}

	public void sharePostToDigistSuccess(HttpJsonResponse res) {//圈贴分享到兴趣圈成功
		dialog.dismiss();
//    	int statusCode = res.getCode();
//    	if(res.getCode() == 500){
//        	new HttpContextImpl("sharePostToDigist").onJsonCodeError(res); // json状态码错误
//        	return;
//        }
//
//        if(res.getCode() != 200){
//        	 UIHelper.ToastMessage(this, res.getBodyString());
//        	return;
//        }
//        if(statusCode != 200) {
//            return;
//        }
        UpEventAgent.onGroupShare(this,interest_id+"."+interest_name,"",posts_id+"","sy_interest");
        SouYueToast.makeText(ShareTofriendActivity.this, getString(R.string.share_success), Toast.LENGTH_SHORT).show();
        finish();
	}

    public void newsShareSuccess(HttpJsonResponse res) {//新闻分享到搜悦兴趣圈成功
         dialog.dismiss();
        UpEventAgent.onNewsShare(this,shareContent.getChannel(),shareContent.getKeyword(),shareContent.getSrpId(),shareContent.getTitle(),shareContent.getNewsUrl(),"sy_interest");
        SouYueToast.makeText(ShareTofriendActivity.this, getString(R.string.share_success), Toast.LENGTH_SHORT).show();
        finish();
    }
	

	
}

