package com.zhongsou.souyue.circle.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CircleGetAtMeBlog;
import com.zhongsou.souyue.net.volley.CSouyueHttpError;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.ProgressBarHelper.ProgressBarClickListener;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.utils.ZhongSouActivityMgr;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @ClassName: IncludingMePostsActivity
 * @Description: 提到我的帖子 （@我的）
 */

public class IncludingMePostsActivity extends RightSwipeActivity implements ProgressBarClickListener,OnClickListener  {
//	private static final int CIRCLEGETATMEBLOG_REQUESTID = 84864; // 获取at 我的列表
	private TextView activity_bar_title;
	private ListView at_me_posts_listview;
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
	private long interest_id;
	private String fromEssence;
	private TextView getMore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_include_me_posts);
		//加载提示框
		pbHelper = new ProgressBarHelper(IncludingMePostsActivity.this, findViewById(R.id.ll_data_loading));
		pbHelper.setProgressBarClickListener(this);
		token = getIntent().getStringExtra("token");
		fromEssence = getIntent().getStringExtra("fromEssence");
//		http = new Http(this);
//		aquery = new AQuery(this);
		initView();
		page = 1;
		loadData();
	}

	private void initView() {
		activity_bar_title = (TextView) this
				.findViewById(R.id.activity_bar_title);
		activity_bar_title.setText(R.string.at_my_post);
		at_me_posts_listview = (ListView) this
				.findViewById(R.id.at_me_posts_listview);
		interest_id = this.getIntent().getLongExtra("interest_id", 0);
		footerView = getLayoutInflater().inflate(R.layout.ent_refresh_footer,
				null);
		// 添加底部刷新
		at_me_posts_listview.addFooterView(footerView);
		footerView.setVisibility(View.GONE);

		// 加载失败
		getMore = (TextView) this.findViewById(R.id.pull_to_refresh_text);
		getMore.setFocusableInTouchMode(false);
		getMore.setOnClickListener(this);

		adapter = new CircleListAdapter(this, interest_id);
		adapter.setListView(getListView());
		at_me_posts_listview.setAdapter(adapter);
		at_me_posts_listview.setOnScrollListener(new ScrollListener());
		at_me_posts_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// 点击列表进详情
				CircleResponseResultItem item = (CircleResponseResultItem) adapter
						.getItem(position);
//				UIHelper.showPostsDetail(IncludingMePostsActivity.this,
//						item.getBlog_id(), item.getInterest_id(), null);
				if(item==null)
				{
					return;
				}
                SearchResultItem item1 = new SearchResultItem();
                item1.setBlog_id(item.getBlog_id());
                item1.srpId_$eq(item.getSrp_id());
                item1.keyword_$eq(item.getSrp_word());
                item1.setInterest_id(item.getInterest_id());
                item1.setSign_id(item.getSign_id());
                IntentUtil.skipDetailPage(IncludingMePostsActivity.this, item1, CircleIndexActivity.REQUEST_CODE_POST_DETAIL_ACTIVITY);

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

	@Override
	protected void onDestroy() {
		if(receiver != null){
			unregisterReceiver(receiver);
		}
		super.onDestroy();
	}

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

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				 loadData();
				break;
			default:
				adapter.updateSingleRow((CircleResponseResultItem) msg.obj, msg.arg1);
				break;
			}
		}
	};

	/**
	 * 返回键
	 */
	public void onBackPressClick(View view) {
		if(!TextUtils.isEmpty(fromEssence) && fromEssence.equals("fromEssence")){
			ZhongSouActivityMgr.getInstance().goHome();
		} else {
			this.finish();
		}

	}

	private class ScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				// 判断滚动到底部
				if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
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
				at_me_posts_listview.removeFooterView(footerView);
			}
//			SouYueToast.makeText(IncludingMePostsActivity.this,R.string.no_includingMePosts, Toast.LENGTH_SHORT).show();
			hasMore = false;
			return;
		}
        if (!isLoading) {
            isLoading = true;
//		 http.getAtMePostList(token,interest_id,page,psize);
            CircleGetAtMeBlog.send(HttpCommon.CIRCLE_GETATMEBLOG_REQUESTID, token, interest_id, page, psize, this);
        }
    }

	@Override
	public void onHttpResponse(IRequest request) {
		switch (request.getmId()){
			case HttpCommon.CIRCLE_GETATMEBLOG_REQUESTID:
				getAtMePostListSuccess(request.<HttpJsonResponse>getResponse());
				break;
		}
	}

	@Override
	public void onHttpError(IRequest request) {
		pbHelper.goneLoading();
		switch (request.getmId()) {
			case HttpCommon.CIRCLE_GETATMEBLOG_REQUESTID:
				if (request.getVolleyError().getErrorType() == IHttpError.TYPE_SERVER_ERROR) {
					pbHelper.goneLoading();
					isLoading = false;
					int statusCode = request.<HttpJsonResponse>getResponse().getCode();
					if (statusCode != 200) {
						pbHelper.showNetError();
						return;
					}
				} else {
					SouYueToast.makeText(IncludingMePostsActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					at_me_posts_listview.removeFooterView(footerView);
				}
				break;
		}
	}

	public void getAtMePostListSuccess(HttpJsonResponse res) {
		pbHelper.goneLoading();
		isLoading = false;
		int statusCode = res.getCode();
		if (statusCode != 200) {
			pbHelper.showNetError();
			return;
		}
		CircleResponseResult result = new CircleResponseResult(res);
		List<CircleResponseResultItem> templist = new ArrayList<CircleResponseResultItem>();
		templist = result.getItems();
        if (templist != null && templist.size() > 0) {
            adapter.setKeyWord(templist.get(0).getSrp_word());
        }
		if (templist != null && templist.size() > 0) {
			adapter.addMore(templist);
			page++;
			if (footerView != null) {
				footerView.setVisibility(View.GONE);
			}
		} else {
			hasMore = false;
//			SouYueToast.makeText(IncludingMePostsActivity.this,R.string.cricle_no_more_data, Toast.LENGTH_SHORT).show();
			if (footerView != null) {
				at_me_posts_listview.removeFooterView(footerView);
			}
		}

		if (templist == null || templist.isEmpty() || templist.size() < psize) {
            if(adapter.getCount() != 0) {
            	at_me_posts_listview.removeFooterView(footerView);
            	getMore.setVisibility(View.VISIBLE);
            	getMore.setText(R.string.cricle_no_more_data);
            }else{
//            	SouYueToast.makeText(IncludingMePostsActivity.this,R.string.no_includingMePosts, Toast.LENGTH_SHORT).show();
            	pbHelper.showNoData();
            }

	    }

		if(templist.size() ==0 && adapter.getCount() == 0) {
			getMore.setText(R.string.tem_no_posts);
		}
	}
//
//	@Override
//	public void onHttpError(String methodName, AjaxStatus status) {
//		 pbHelper.goneLoading();
//		 if("getAtMePostList".equals(methodName)) {
//          SouYueToast.makeText(IncludingMePostsActivity.this,"加载失败", Toast.LENGTH_SHORT).show();
//          at_me_posts_listview.removeFooterView(footerView);
//       }
//	}

	@Override
	public void clickRefresh() {
		loadData();
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
		}
	}

	public long getInterest_id() {
 		return interest_id;
 	}

	public ListView getListView() {
        return at_me_posts_listview;
    }

}
