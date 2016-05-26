package com.zhongsou.souyue.circle.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.circle.adapter.CircleListAdapter;
import com.zhongsou.souyue.circle.model.CircleResponseResult;
import com.zhongsou.souyue.circle.model.CircleResponseResultItem;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.personal.UserCircleList;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.ProgressBarHelper.ProgressBarClickListener;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @ClassName: PostsForMeActivity
 * @Description: 我发布的帖子(从个人中心进入)
 */

public class PostsForMeActivity extends RightSwipeActivity implements ProgressBarClickListener,OnClickListener  {
	private TextView activity_bar_title;
	private ListView my_posts_listview;
	private CircleListAdapter adapter;
	private List<CircleResponseResultItem> list = new ArrayList<CircleResponseResultItem>();
//	private Http http;
	private int page = 1;//页码
	private static int psize = 10;//分页大小
//	private AQuery aquery;
	private View footerView;
	private ProgressBarHelper pbHelper;
	private boolean isLoading = false;
	private boolean hasMore = true;//更多
	private String token = "";//
	private String new_srpid;
//	private TextView getMore;
	private long interest_id;
    private int imStatus;
	private long user_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_posts_for_me);
        //加载提示框
        pbHelper = new ProgressBarHelper(PostsForMeActivity.this, null);
        pbHelper.setProgressBarClickListener(this);
        token = SYUserManager.getInstance().getToken();
//        http = new Http(this);
//        aquery = new AQuery(this);
        initView();
        page = 1;
		loadData();
	}

	private void initView() {
		activity_bar_title = (TextView) this.findViewById(R.id.activity_bar_title);
		my_posts_listview = (ListView) this.findViewById(R.id.at_me_posts_listview);
        interest_id = getIntent().getLongExtra("interest_id", 0);
        imStatus = getIntent().getIntExtra("imStatus", 0);
        user_id = getIntent().getLongExtra("user_id", 0);
        new_srpid = getIntent().getStringExtra("new_srpid");
        footerView = getLayoutInflater().inflate(R.layout.ent_refresh_footer,null);

        if(String.valueOf(user_id).equals(SYUserManager.getInstance().getUserId())){
            activity_bar_title.setText(R.string.my_send_post);
        }else{
            activity_bar_title.setText("TA的帖子");
        }

		//添加底部刷新
		my_posts_listview.addFooterView(footerView);
		footerView.setVisibility(View.GONE);

		//加载失败
//		getMore = (TextView) this.findViewById(R.id.pull_to_refresh_text);
//		getMore.setFocusableInTouchMode(false);
//		getMore.setOnClickListener(this);

//		adapter = new PostForMeAdapter(this,aquery,false,PostsForMeActivity.this);
		adapter = new CircleListAdapter(this, interest_id);
		adapter.setListView(getListView());
		my_posts_listview.setAdapter(adapter);
		my_posts_listview.setOnScrollListener(new ScrollListener());
		my_posts_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				  //点击列表进详情
				  CircleResponseResultItem item = (CircleResponseResultItem) adapter.getItem(position);
				if(item ==null)
				{
					return;
				}
//	              UIHelper.showPostsDetail(PostsForMeActivity.this, item.getBlog_id(), item.getInterest_id(), null);
                SearchResultItem item1 = new SearchResultItem();
                item1.setBlog_id(item.getBlog_id());
                item1.setInterest_id(item.getInterest_id());
                IntentUtil.skipDetailPage(PostsForMeActivity.this, item1, CircleIndexActivity.REQUEST_CODE_POST_DETAIL_ACTIVITY);
			}

		});
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(PublishActivity.ACTION_NEW_POST);
		registerReceiver(receiver, filter);
	}
	
	/**
	 * 针对发帖操作，接受广播更新圈吧列表
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			int type = intent.getIntExtra("resultType", 0);
			if (PublishActivity.ACTION_NEW_POST.equals(action)) {
				CircleResponseResultItem item = (CircleResponseResultItem) intent.getSerializableExtra(PublishActivity.ACTION_KEY_RESPONSEITEM);
				update(item, type);
			}
		}
	};

	/**
	 * 更新listview中对应的item
	 * 
	 * @param item
	 * @param type
	 */
	public void update(CircleResponseResultItem item, int type) {
		Message msg = mHandler.obtainMessage();
		msg.obj = item;
		msg.arg1 = type;
		msg.sendToTarget();
	}
	
	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
				switch (msg.what) {
					case 0:
						loadData();
						break;
				}
			return false;
		}
	}) ;

	/**
	 * 返回键
	 */
	public void onBackPressClick(View view) {
		this.finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}

	private class ScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				// 判断滚动到底部
				if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                    if (!CMainHttp.getInstance().isNetworkAvailable(PostsForMeActivity.this)) {
                        UIHelper.ToastMessage(PostsForMeActivity.this, R.string.cricle_manage_networkerror);
                        return;
                    }
					footerView.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessage(0);
				}
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
		}

	}

	private void loadData() {

		if(!hasMore) {
			if (footerView != null) {
				my_posts_listview.removeFooterView(footerView);
			}
			hasMore = false;
			return;
		}
		if(!isLoading) {
		    isLoading = true;
            if(adapter.getDatas().size() == 0){
				UserCircleList list = new UserCircleList(HttpCommon.USER_CIRCLE_LIST_REQUEST,this);
				list.addParams(user_id, new_srpid, interest_id, psize, 0);
				mMainHttp.doRequest(list);
            }else{
				UserCircleList list = new UserCircleList(HttpCommon.USER_CIRCLE_LIST_REQUEST,this);
				list.addParams(user_id, new_srpid, interest_id, psize, adapter.getDatas().get(adapter.getDatas().size() - 1).getBlog_id());
				mMainHttp.doRequest(list);
            }

        }
	}

	@Override
	public void onHttpResponse(IRequest _request) {
		int id = _request.getmId();
		switch (id){
			case HttpCommon.USER_CIRCLE_LIST_REQUEST:
				getPostListForPersonalSuccess(_request.<HttpJsonResponse>getResponse());
		}
	}



	//	public void getPostListForPersonalSuccess(HttpJsonResponse res, AjaxStatus status) {
//	后面的网络状态不用了就去掉了
	public void getPostListForPersonalSuccess(HttpJsonResponse res) {
		pbHelper.goneLoading();
		isLoading = false;
		int statusCode = res.getCode();
		if (statusCode != 200) {
			pbHelper.showNetError();
			return;
		}
		CircleResponseResult result = new CircleResponseResult(res,1);
		List<CircleResponseResultItem> templist = new ArrayList<CircleResponseResultItem>();
		templist = result.getItems();
		if (templist != null && templist.size() > 0) {
			adapter.addMore(templist);
			page++;
			if (footerView != null) {
				footerView.setVisibility(View.GONE);
			}
		} else {
			hasMore = false;
			if (footerView != null) {
				my_posts_listview.removeFooterView(footerView);
			}
		}

		if (templist == null || templist.isEmpty() || templist.size() < psize) {
            isLoading = true;
            if(adapter.getCount() != 0) {
            	my_posts_listview.removeFooterView(footerView);
                Toast.makeText(PostsForMeActivity.this,"已加载全部", Toast.LENGTH_LONG).show();
//            	getMore.setVisibility(View.VISIBLE);
//            	getMore.setText(R.string.cricle_no_more_data);
            }else{
            	pbHelper.showNoData();
            }

	    }

//		if(templist.size() ==0 && adapter.getCount() == 0) {
//			getMore.setText(R.string.tem_no_posts);
//		}
	}
	@Override
	public void onHttpError(IRequest _request) {
		pbHelper.goneLoading();
		int id = _request.getmId();
		if(id == HttpCommon.USER_CIRCLE_LIST_REQUEST) {
			my_posts_listview.removeFooterView(footerView);
			pbHelper.showNetError();
		}
	}
//	错误处理换成上面的新网络框架错误处理
//	@Override
//	public void onHttpError(String methodName, AjaxStatus status) {
//		 pbHelper.goneLoading();
//		 if("getPostListForPersonal".equals(methodName)) {
//          my_posts_listview.removeFooterView(footerView);
//          pbHelper.showNetError();
//       }
//	}

	@Override
	public void clickRefresh() {
		UserCircleList list = new UserCircleList(HttpCommon.USER_CIRCLE_LIST_REQUEST,this);
		list.addParams(user_id, new_srpid, interest_id, psize, 0);
		mMainHttp.doRequest(list);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 200 && resultCode != RESULT_CANCELED) {  //圈吧相机
			String picPath = null;
			if (adapter.getImageFileUri() != null) {
				picPath = Utils.getPicPathFromUri(adapter.getImageFileUri(), this);
				int degree = 0;
				if (!StringUtils.isEmpty(picPath))
					degree = ImageUtil.readPictureDegree(picPath);
				Matrix matrix = new Matrix();
				if (degree != 0) {// 解决旋转问题
					matrix.preRotate(degree);
				}
				Log.v("Huang", "相机拍照imageFileUri != null:" + picPath);
                ArrayList<String> list = new ArrayList<String>();
                list.add(picPath);
				adapter.getCircleFollowDialog().addImagePath(list);

			} else {
				// showToast(R.string.self_get_image_error);
			}
		} else if (resultCode == 0x200) {  //圈吧相册
			List<String> list = new ArrayList<String>();
			list = data.getStringArrayListExtra("imgseldata");
            adapter.getCircleFollowDialog().addImagePath(list);
//            for (int i = 0; i < list.size(); i++) {
//			}
		}
	}

	public long getInterest_id() {
 		return interest_id;
 	}

	public ListView getListView() {
        return my_posts_listview;
    }

}
