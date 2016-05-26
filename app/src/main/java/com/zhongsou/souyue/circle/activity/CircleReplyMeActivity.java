package com.zhongsou.souyue.circle.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.adapter.CircleReplyMeAdapter;
import com.zhongsou.souyue.circle.model.CircleBlogReply;
import com.zhongsou.souyue.circle.model.MainBlog;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.personal.UserReplyList;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
import com.zhongsou.souyue.utils.IntentUtil;

import java.util.List;

/**
 * 4.1.0新增，圈子回复我的界面
 * 
 * @author Administrator
 * 
 */
public class CircleReplyMeActivity extends BaseActivity implements
		ProgressBarHelper.ProgressBarClickListener, View.OnClickListener, AbsListView.OnScrollListener {
	private LayoutInflater inflater;
	private ProgressBarHelper pbHelp;
	private TextView title;// 界面titie
	private ImageButton btn_option;// 右部选项菜单隐藏
//	private Http http;
	private String interest_id;
	private PullToRefreshListView replyme_list;
	private CircleReplyMeAdapter adapter;
	private TextView getMore;
	private boolean needLoad;
	private int visibleLast = 0;
	private int visibleCount = 0;
	private boolean hasMore=true;
	private boolean ifDownRefresh = true;// 是否下拉刷新默认为true
	private int psize = 10;// 分页大小
	private List<CircleBlogReply> blogReplyArr;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.circle_reply_me);
		interest_id = getIntent().getStringExtra("interest_id");
		findView();
		loadDataDown();
	}

	private void findView() {
//		http = new Http(this);
		inflater = LayoutInflater.from(this);
		// 初始化dialog
		pbHelp = new ProgressBarHelper(this, findViewById(R.id.ll_data_loading));
		pbHelp.setProgressBarClickListener(this);
		pbHelp.showLoading();
		// 初始化头部标题栏
		title = (TextView) findViewById(R.id.activity_bar_title);
//		btn_option = (ImageButton) findViewById(R.id.btn_option);
//		btn_option.setVisibility(View.INVISIBLE);
		title.setText(R.string.replyme_title);

		// list
		replyme_list = (PullToRefreshListView) findViewById(R.id.replyme_list);
		// 添加底部加载
		View footerView = getLayoutInflater().inflate(
				R.layout.cricle_single_list_refresh_footer, null);

		replyme_list.getRefreshableView()
				.addFooterView(footerView, null, false);
		// 加载失败
		getMore = (TextView) footerView.findViewById(R.id.get_more);
		getMore.setFocusableInTouchMode(false);
		getMore.setOnClickListener(this);
		replyme_list.setOnScrollListener(this);
		adapter = new CircleReplyMeAdapter(this);
		replyme_list.setAdapter(adapter);

		replyme_list.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				ifDownRefresh = true;
				loadDataDown();
			}
		});
		replyme_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				position = position-1;

                SearchResultItem item1 = new SearchResultItem();
                MainBlog item = adapter.getBlogList().get(position).getMainBlog();
                item1.setBlog_id(Long.valueOf(item.getMblog_id()));
                item1.setInterest_id(Long.valueOf(item.getInterest_id()));
                item1.keyword_$eq(item.getSrp_word());
                item1.srpId_$eq(item.getSrp_id());
                IntentUtil.startskipDetailPage(CircleReplyMeActivity.this, item1);

//				UIHelper.showPostsDetail(CircleReplyMeActivity.this,Long.valueOf(adapter.getBlogList().get(position).getMainBlog().getMblog_id()),Long.valueOf(adapter.getBlogList().get(position).getMainBlog().getInterest_id()));
			}
		});
	}

	// 请求网络数据
	// srp_id = "56acdb730136078dcb617e27466c9ee1";
	// token = "59d046a6-a5d0-4aad-9d87-722e76551380";
	private void loadDataDown() {
		// int pno = (adapter.getCount()+psize)/psize;
		UserReplyList replyList = new UserReplyList(HttpCommon.USER_GET_REPLY_LIST_REQUEST,this);
		replyList.setParams(interest_id, psize + "", 1 + "");
		mMainHttp.doRequest(replyList);
//		http.getReplyMeList(SYUserManager.getInstance().getToken(), interest_id,
//				psize + "", 1 + "");
	}

	private void LoadDataUp() {
		int pno = (adapter.getCount() + psize) / psize;
		UserReplyList replyList = new UserReplyList(HttpCommon.USER_GET_REPLY_LIST_REQUEST,this);
		replyList.setParams(interest_id, psize + "", pno + "");
		mMainHttp.doRequest(replyList);
//		http.getReplyMeList(SYUserManager.getInstance().getToken(), interest_id,
//				psize + "", pno + "");
	}

	@Override
	public void onHttpResponse(IRequest request) {
		switch (request.getmId()){
			case HttpCommon.USER_GET_REPLY_LIST_REQUEST:
				getReplyMeListSuccess(request.<HttpJsonResponse>getResponse());
		}
	}

	public void getReplyMeListSuccess(HttpJsonResponse res) {
		if (pbHelp.isLoading) {
			pbHelp.goneLoading();
		}
 
		needLoad = true;
		blogReplyArr = obj2SignatureList(res);
		if (ifDownRefresh) {
			adapter.setBlogList(blogReplyArr);
			replyme_list.setAdapter(adapter);
			ifDownRefresh=false;
		} else {
			adapter.addBlogList(blogReplyArr);
			adapter.notifyDataSetChanged();
		}
		hasMore = !(blogReplyArr.size() < psize);
		if (blogReplyArr.size() < psize) {
			getMore.setVisibility(View.VISIBLE);
			getMore.setText("");
		}
		replyme_list.onRefreshComplete();
		if(adapter.getBlogList().size()==0){
			pbHelp.showNoData();
		}
	}

	private List<CircleBlogReply> obj2SignatureList(HttpJsonResponse res) {
		int statusCode = res.getCode();
		if (statusCode != 200) {
			return null;
		}
		return new Gson().fromJson(res.getBodyArray(),
				new TypeToken<List<CircleBlogReply>>() {
				}.getType());
	}

	@Override
	public void clickRefresh() {
		// TODO 加载dialog 点击刷新
		loadDataDown();
	}

	/**
	 * 头部返回按钮被点击
	 */
	public void onBackPressClick(View v) {
		finish();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		int itemsLastIndex = adapter.getCount();
		if (itemsLastIndex < 0) {
			return;
		}
		int lastIndex = itemsLastIndex;
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
				&& visibleLast >= lastIndex && needLoad) {
			needLoad = false;
			if(hasMore&&!ifDownRefresh){//下拉加载没有完成时，不让其上拉加载
				LoadDataUp();
			}
				
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		visibleCount = visibleItemCount;
		visibleLast = firstVisibleItem + visibleItemCount;
	}

	@Override
	public void onHttpError(IRequest request) {
		pbHelp.showNetError();
	}

	@Override
	public void onClick(View v) {
	}

}
