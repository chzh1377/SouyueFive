 package com.zhongsou.souyue.circle.fragment;

 import android.content.BroadcastReceiver;
 import android.content.Context;
 import android.content.Intent;
 import android.content.IntentFilter;
 import android.graphics.Matrix;
 import android.os.Bundle;
 import android.os.Handler;
 import android.os.Message;
 import android.util.Log;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.AbsListView;
 import android.widget.AbsListView.OnScrollListener;
 import android.widget.AdapterView;
 import android.widget.ImageView;
 import android.widget.ListView;
 import android.widget.RelativeLayout;
 import android.widget.TextView;
 import android.widget.Toast;
 import com.zhongsou.souyue.R;
 import com.zhongsou.souyue.activity.LoadingDataListener;
 import com.zhongsou.souyue.adapter.baselistadapter.CircleListManager;
 import com.zhongsou.souyue.adapter.baselistadapter.HomeListManager;
 import com.zhongsou.souyue.adapter.baselistadapter.ListViewAdapter;
 import com.zhongsou.souyue.circle.activity.CircleIndexActivity;
 import com.zhongsou.souyue.circle.activity.PublishActivity;
 import com.zhongsou.souyue.circle.adapter.CircleListAdapter;
 import com.zhongsou.souyue.circle.model.CircleResponseResultItem;
 import com.zhongsou.souyue.circle.ui.UIHelper;
 import com.zhongsou.souyue.circle.util.OnChangeListener;
 import com.zhongsou.souyue.fragment.SRPFragment;
 import com.zhongsou.souyue.im.emoji.EmojiPattern;
 import com.zhongsou.souyue.module.Ad;
 import com.zhongsou.souyue.module.listmodule.BaseListData;
 import com.zhongsou.souyue.module.listmodule.CrouselItemBean;
 import com.zhongsou.souyue.net.HttpJsonResponse;
 import com.zhongsou.souyue.net.circle.CircleEssenceListReq;
 import com.zhongsou.souyue.net.circle.CirclePrimeHeadRequest;
 import com.zhongsou.souyue.net.news.CommonReq;
 import com.zhongsou.souyue.net.volley.CMainHttp;
 import com.zhongsou.souyue.net.volley.HttpCommon;
 import com.zhongsou.souyue.net.volley.IRequest;
 import com.zhongsou.souyue.net.volley.IVolleyResponse;
 import com.zhongsou.souyue.ui.ProgressBarHelper;
 import com.zhongsou.souyue.ui.ProgressBarHelper.ProgressBarClickListener;
 import com.zhongsou.souyue.ui.SouYueToast;
 import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
 import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
 import com.zhongsou.souyue.utils.ImageUtil;
 import com.zhongsou.souyue.utils.SYUserManager;
 import com.zhongsou.souyue.utils.StringUtils;
 import com.zhongsou.souyue.utils.Utils;

 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
/**
 * @ClassName: EssencePostFragment 
 * @Description: 兴趣圈_精华帖fragment
 * @author gengsong@zhongsou.com
 * @date 2014年4月22日 下午2:35:13 
 * @version V4.0
 */
public class EssencePostFragment extends SRPFragment implements ProgressBarClickListener,
        LoadingDataListener,View.OnClickListener, OnScrollListener ,IVolleyResponse {

    public static final int HTTP_PRIMELIST=5901;
	private View view;
	private ProgressBarHelper pbHelper;
//	private Http http;
	private PullToRefreshListView pullToRefreshListView;
    private ListViewAdapter listViewAdapter;
    private CircleListManager listManager;
	private CircleListAdapter adapter;
	private int page = 1;//页码
	private int psize = 10;//分页大小
	private long interest_id;//圈子ID
    private String srp_id;
    private String tag_id;   // 标签ID
	private PullToRefreshBase.OnRefreshListener<ListView> onRefreshListener;
	private List<CircleResponseResultItem> items = new ArrayList<CircleResponseResultItem>();
	private boolean isLoading = false;
//	private AQuery aquery;
	public static final String INTERESTID = "interest_id";
	private String  title;
	private int visibleLast = 0;
	private boolean needLoad;
	private boolean isLoadAll = false;
	private View footerView;
	private TextView getMore;
    private boolean pullToRefresh = false;

	private RelativeLayout rl_cricle_good_btn;
    private ImageView iv_cricle_good_icon;
    private TextView tv_cricle_reply_num;
    private OnChangeListener onRoleChangeListener;
    private TextView groupCountTv;
    private View headerView;
    private boolean isIMGroupNone;
    private boolean isPostNone;
    private boolean isGetPostListSuccess;
    private boolean isGetHeaderSuccess;
    private String mUpdateTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//	    aquery = new AQuery(getActivity());
        new Thread(new Runnable() {
            @Override
            public void run() {
            	EmojiPattern.getInstace().getFileText(getActivity());
            }
        }).start();
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
    	if(page != 1) {//重置page
    		page = 1;
    	}
    	if(isLoadAll = true) { //重置isLoadAll
    	   isLoadAll = false;
           isLoading = false;
    	}
//    	http = new Http(this);
		view = inflater.inflate(R.layout.circle_essencepost_list, null);
		interest_id = getArguments().getLong(INTERESTID, 0);
        title = getArguments().getString("title");
        tag_id = getArguments().getString("tag_id");
        srp_id = getArguments().getString("srp_id");
        keyWord =getArguments().getString("keyword");
//        http.getPrimeHeader(srp_id, SYUserManager.getInstance().getToken());
        CirclePrimeHeadRequest.send(HttpCommon.CIRCLE_PRIME_HEAD_REQUESTID,this,srp_id, SYUserManager.getInstance().getToken());
        return view;
    }
    
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
        loadSelfData();
			
    }
    
    @Override
	public void onResume() {
        super.onResume();
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
//			adapter.updateSingleRow((CircleResponseResultItem) msg.obj, msg.arg1);
            listManager.updateSingleRow((CircleResponseResultItem) msg.obj, msg.arg1);
		}
	};
    
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			int type = intent.getIntExtra("resultType", 0);
			if (action != null && PublishActivity.ACTION_NEW_POST.equals(action)) {
				CircleResponseResultItem item = (CircleResponseResultItem) intent.getSerializableExtra(PublishActivity.ACTION_KEY_RESPONSEITEM);
				update(item, type);
			}
		}
	};
	
	private void initView() {
		// 加载提示框
		pbHelper = new ProgressBarHelper(getActivity(),view.findViewById(R.id.ll_data_loading));
		pbHelper.setProgressBarClickListener(this);
		pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.essence_post_list);
        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(view.getId() == R.id.prime_header_layout && isGetHeaderSuccess && isGetPostListSuccess){
                    UIHelper.showCircleIMGroup(getActivity(), srp_id, title);
                    return;
                }
            	if (!CMainHttp.getInstance().isNetworkAvailable(context)) {
            		SouYueToast.makeText(getActivity(), R.string.cricle_manage_networkerror, Toast.LENGTH_SHORT).show();
            		return;
            	}
                //   || position<=1 去掉 小于 1 的判断
                if (position == 0 || position > listViewAdapter.getCount()+1)
                    return;

                BaseListData item = (BaseListData) parent.getAdapter().getItem(position);
//              BaseListData item = listViewAdapter.getDatas().get(position-1);
                item.setHasRead(true);
//                item.getInvoke().setChan(mTitle);
                listManager.clickItem(item);
//                setHasRead(item);
                getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
                listViewAdapter.notifyDataSetChanged();


//                CircleResponseResultItem item = (CircleResponseResultItem) parent.getAdapter().getItem(position);
//                if(item != null){
////                    Intent intent = new Intent(getActivity(), PostsActivity.class);
////                    intent.putExtra("blog_id",  item.getBlog_id());
////                    intent.putExtra("interest_id", interest_id);
////                    intent.putExtra("title", title);
////                    intent.putExtra("srp_id", srp_id);
////                    intent.putExtra("is_from_list", true);
////                    getActivity().startActivityForResult(intent, CircleIndexActivity.REQUEST_CODE_POST_DETAIL_ACTIVITY);
////                    getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
//                    SearchResultItem item1 = new SearchResultItem();
//                    item1.setBlog_id(item.getBlog_id());
//                    item1.keyword_$eq(keyWord);
//                    item1.srpId_$eq(srp_id);
//                    item1.setInterest_id(interest_id);
//                    item1.setSign_id(item.getSign_id());
//                    IntentUtil.skipDetailPage(getActivity(), item1, CircleIndexActivity.REQUEST_CODE_POST_DETAIL_ACTIVITY);
//                }
            }
        });

    	//添加底部加载
		footerView = getActivity().getLayoutInflater().inflate(R.layout.cricle_single_list_refresh_footer,null);
        headerView = getActivity().getLayoutInflater().inflate(R.layout.circle_prime_header, null);
        groupCountTv = (TextView)headerView.findViewById(R.id.group_count_tv);
        pullToRefreshListView.getRefreshableView().addHeaderView(headerView);
		pullToRefreshListView.setOnRefreshListener(onRefreshListener);
		//加载失败
		getMore = (TextView) footerView.findViewById(R.id.get_more);
		getMore.setFocusableInTouchMode(false);
        getMore.setOnClickListener(this);

        listViewAdapter = new ListViewAdapter(context,null);
        listManager = new CircleListManager(getActivity(), interest_id);
        listManager.setView(listViewAdapter,getPullToRefreshListView().getRefreshableView());
        listManager.setChangeListener(new OnChangeListener() {
            @Override
            public void onChange(Object obj) {
                pbHelper.goneLoading();
                pullToRefreshListView.startRefresh();
            }
        });
        listManager.setOnRoleChangeListener(new OnChangeListener() {
            @Override
            public void onChange(Object obj) {
                if(onRoleChangeListener != null){
                    onRoleChangeListener.onChange(obj);
                }
            }
        });
        listViewAdapter.setManager(listManager);


//		adapter = new CircleListAdapter(getActivity(), interest_id);
//        adapter.setKeyWord(keyWord);
//        adapter.setChangeListener(new OnChangeListener() {
//            @Override
//            public void onChange(Object obj) {
//                pbHelper.goneLoading();
//                pullToRefreshListView.startRefresh();
//            }
//        });
//		adapter.setListView(getPullToRefreshListView().getRefreshableView());
//        adapter.setOnRoleChangeListener(new OnChangeListener() {
//            @Override
//            public void onChange(Object obj) {
//                if(onRoleChangeListener != null){
//                    onRoleChangeListener.onChange(obj);
//                }
//            }
//        });
//        adapter.setLoadingDataListener(this);
        pullToRefreshListView.getRefreshableView().addFooterView(footerView);
		pullToRefreshListView.setAdapter(listViewAdapter);
		pullToRefreshListView.setOnScrollListener(this);
		// 下拉刷新监听
//		pullToRefreshListView.onUpdateTime(adapter.getChannelTime());
		
		pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
			
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				visibleLast = 0;
				isLoadAll = false;
				page = 1;//默认加载第一页数据
				isPostNone = false;
                isIMGroupNone = false;
				//下拉刷新，重新请求一次接口
                pullToRefresh = true;
//                http.getNewEssencePostList(SYUserManager.getInstance().getToken(),interest_id, page, psize,0);
                loadMblogList(SYUserManager.getInstance().getToken(),interest_id, page, psize,0);
                CirclePrimeHeadRequest.send(HttpCommon.CIRCLE_PRIME_HEAD_REQUESTID,EssencePostFragment.this,srp_id, SYUserManager.getInstance().getToken());
//                http.getPrimeHeader(srp_id, SYUserManager.getInstance().getToken());

			}
		});
		
		pullToRefreshListView.setOnTimeRefreshListener(new PullToRefreshBase.OnTimeRefreshListener() {
			@Override
			public void onTimeRefresh() {
                if (null != mUpdateTime) {
                    String time = StringUtils
                            .convertDate(mUpdateTime);
                    pullToRefreshListView.onUpdateTime(time);
                }
			}
		});
		
		// 注册接受发帖后刷新列表的广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(PublishActivity.ACTION_NEW_POST);
		getActivity().registerReceiver(receiver, filter);
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_more:
                loadSelfData();
                break;
            default:
                break;
        }
    }

    private void loadMblogList(String token,long interest_id, int page, int psize, long last_sort_num)
    {
//        PrimeListReq req =new PrimeListReq(HttpCommon.CIRLCE_BLOG_PRIME_LIST_ID,this);
//        req.setParams(token,interest_id,page,psize,last_sort_num);
//        CMainHttp.getInstance().doRequest(req);
        CircleEssenceListReq req =new CircleEssenceListReq(HttpCommon.CIRLCE_BLOG_PRIME_LIST_ID,this);
        req.setParams(token,interest_id,page,psize,last_sort_num);
        CMainHttp.getInstance().doRequest(req);
    }

    @Override
    public void onHttpResponse(IRequest request) {
        mUpdateTime = System.currentTimeMillis()+"";
        switch (request.getmId())
        {
            case HttpCommon.CIRLCE_BLOG_PRIME_LIST_ID:
                getNewEssencePostListDataTo(request,new Date().getTime()+"");
                break;
            case HttpCommon.CIRCLE_PRIME_HEAD_REQUESTID:
                getPrimeHeaderSuccess(request.<HttpJsonResponse>getResponse());
                break;
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        switch (request.getmId())
        {
            case HttpCommon.CIRLCE_BLOG_PRIME_LIST_ID:
                break;
            case HttpCommon.CIRCLE_PRIME_HEAD_REQUESTID:
                break;
        }
    }

    @Override
    public void onHttpStart(IRequest request) {

    }
	private void loadSelfData()  {
        if (isLoadAll) {
            needLoad = false;
            return;
        }
        if (!isLoading) {
            if (!CMainHttp.getInstance().isNetworkAvailable(context)) {
                if (listViewAdapter.getCount() <= 0) {
                    pbHelper.showNetError();
                } else {
                    UIHelper.ToastMessage(getActivity(), R.string.cricle_manage_networkerror);
                    if (getMore.getVisibility() == View.GONE) {
//                        if (getMore.getText() != null && getResources().getString(R.string.cricle_no_more_data).equals(getMore.getText())) {
                            getMore.setText(R.string.mores);
//                        }
                        getMore.setVisibility(View.VISIBLE);
                    }
                }
                return;
            }

            if (getMore.getVisibility() == View.VISIBLE) {
                getMore.setVisibility(View.GONE);
            }
            isLoading = true;
            if(listViewAdapter != null) {
//                http.getNewEssencePostList(SYUserManager.getInstance().getToken(),interest_id, page, psize,adapter.getLastId());
                loadMblogList(SYUserManager.getInstance().getToken(),interest_id, page, psize,Long.parseLong(getLastId()));

                Log.v("test", "【ESS】"+"page:"+page + "     psize:"+psize+"      lastid:"+getLastId());
            }
        }
    }
    public String getLastId(){
        String lastId = "";
        if (listViewAdapter == null){
            return "0";
        }
        List<BaseListData> datas = listViewAdapter.getDatas();
        if (datas == null){
            return "0";
        }
        int count = datas.size();
        if (count == 0){
            return "0";
        }
        BaseListData data = datas.get(datas.size()-1);
        lastId = data.getId()+"";
        return lastId;
    }

	@Override
	public void loadData()  {

	}
	
	public Handler getmHandler() {
		return mHandler;
	}

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}
	
	public PullToRefreshListView getPullToRefreshListView() {
        return pullToRefreshListView;
    }
	
	/**
	 * 加载成功
	 * @param
	 */
//	public void getNewEssencePostListSuccess(HttpJsonResponse res, AjaxStatus status) {
//        getNewEssencePostListDataTo(res,status.getTime().getTime()+"");
//    }
	public void getNewEssencePostListDataTo(IRequest request,String data)
    {
        if(pullToRefresh) {
            if(pbHelper.isLoading) {
                pbHelper.goneLoading();
            }
            pullToRefreshListView.onRefreshComplete();
//            listManager.setChannelTime(data);
//            adapter.setChannelTime(data);
            updateDataToAdapter(request,0);
            pullToRefresh = false;
        } else {
            pbHelper.goneLoading();
            updateDataToAdapter(request,1);
        }
        isGetPostListSuccess = true;
    }

    public void getPrimeHeaderSuccess(HttpJsonResponse res){
        int count =  res.getBody().get("group_count").getAsInt();
        if(count > 0){
            groupCountTv.setText(getString(R.string.prime_group_count, count));
        }else {
            isIMGroupNone = true;
            if(isPostNone){
                pbHelper.showNoData();
            }
            pullToRefreshListView.getRefreshableView().removeHeaderView(headerView);
        }
        isGetHeaderSuccess = true;
    }



	/**
     * 更新Adapter数据
     * @param
     * @param type 0下拉刷新，1上拉加载
     */
	private void updateDataToAdapter(IRequest request, int type) {
        List datas = request.getResponse();
        boolean hasmore = (Boolean)datas.get(CommonReq.YAOWEN_DATA_INDEX_HASMORE);
        List<BaseListData> topList = (List<BaseListData>) datas.get(CommonReq.YAOWEN_DATA_INDEX_TOPLIST);
        List<BaseListData> focusList = (List<BaseListData>) datas.get(CommonReq.YAOWEN_DATA_INDEX_FOCUS);
        List<BaseListData> newslist = (List<BaseListData>) datas.get(CommonReq.YAOWEN_DATA_INDEX_NEWSLIST);
        List<Ad> adlist = (List<Ad>) datas.get(CommonReq.YAOWEN_DATA_INDEX_ADLIST);



		isLoading = false;
//		int statusCode = res.getCode();
//		if (statusCode != 200) {
//			pbHelper.showNetError();
//			return;
//		}
//		CircleResponseResult result = new CircleResponseResult(res);
//		items = result.getItems();

        if(page == 1 && newslist.isEmpty()) {
//            adapter.clearDatas();
            listViewAdapter.clear();
            isPostNone = true;
            if(isIMGroupNone){
                pbHelper.showNoData();
            }
            pullToRefreshListView.onRefreshComplete();
            pullToRefreshListView.getRefreshableView().removeFooterView(footerView);
            return;
        }

		if(!newslist.isEmpty()) {
			if(type == 0) {
//                adapter.clearDatas();
                listViewAdapter.clear();
                setDatas(topList, focusList, newslist);
            }else
            {
                listViewAdapter.addLast(newslist);
            }
//            adapter.addMore(items);
            page++;
            pullToRefreshListView.onRefreshComplete();
	    }

		if (newslist == null || newslist.isEmpty() || newslist.size()< psize) {
            isLoadAll = true;
            if(listViewAdapter.getCount() != 0) {
            	getMore.setVisibility(View.VISIBLE);
//            	getMore.setText(R.string.cricle_no_more_data);  暂时去掉已加载全部的提示
                getMore.setText("");
            }
	    }
	    needLoad = true;
        listViewAdapter.notifyDataSetChanged();
	}
    private void setDatas(List<BaseListData> toplist,List<BaseListData> focusList,List<BaseListData> newslist){
        if (focusList!=null&&focusList.size()>0) {
            CrouselItemBean bean = new CrouselItemBean();
            bean.setViewType(BaseListData.VIEW_TYPE_IMG_CAROUSEL);
            bean.setFocus(focusList);
            toplist.add(0, bean);
        }
        listViewAdapter.setData(newslist);
        listViewAdapter.addFirst(toplist);
    }


    /** 取到@我的帖子个数后清空  */
	private void clearPostTipsCount() {
//		http.clearCircleMainPostCount(interest_id+"",SYUserManager.getInstance().getToken());
	}
//
//	public void clearCircleMainPostCountSuccess(HttpJsonResponse response, AjaxStatus status) {
//		 int statusCode = response.getCode();
//	     if(statusCode != 200){
//	          return;
//	     }
//	}

//	@Override
//	public void onHttpError(String methodName, AjaxStatus status) {
//        if(methodName.equals("getNewEssencePostList")){
//            pbHelper.showNetError();
//            pullToRefreshListView.onRefreshComplete();
//        }
//    }

	@Override
	public void clickRefresh() {
        if(!isFastDoubleClick()){   //防止过快点击
            isLoadAll = false;
            isLoading = false;
//            http.getPrimeHeader(srp_id, SYUserManager.getInstance().getToken());
            CirclePrimeHeadRequest.send(HttpCommon.CIRCLE_PRIME_HEAD_REQUESTID, this, srp_id, SYUserManager.getInstance().getToken());
            loadSelfData();
        }
	}

    private static long lastClickTime;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
	
	public void showToast(int resId) {
		SouYueToast.makeText(getActivity(),getResources().getString(resId), 0).show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        try{
            getActivity().unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		pullToRefreshListView.setAdapter(null);
		pullToRefreshListView = null;
		pbHelper.context = null;
		pbHelper = null;
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		  int itemsLastIndex = listViewAdapter.getCount();
          if(itemsLastIndex < 0) {
              return;
          }
          int lastIndex = itemsLastIndex;
          if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLast == lastIndex && needLoad) {
              needLoad = false;
              loadSelfData();
          }
	}

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
        int count = isIMGroupNone ? 0 : 1;
        visibleLast = firstVisibleItem + visibleItemCount - count -2;
        if (isLoadAll) {
            ++visibleLast;
        }
    }

    public long getInterest_id() {
 		return interest_id;
 	}
	
//	public void getPraisePostCountSuccess(HttpJsonResponse res, AjaxStatus status) {
//	   	 int statusCode = res.getCode();
//	        if(statusCode != 200) {
//	            return;
//	        }
//        JsonObject body = res.getBody();
//        String responseResult =  body.toString();
//			if (!TextUtils.isEmpty(responseResult)) {
//	            String state = body.get("state").getAsString();
//	            switch (Integer.parseInt(state)) {
//	            	case 0: ////0-失败 1-成功 3-帖子不存在 4-已经点过赞
////	            		SouYueToast.makeText(this.getActivity(), "点赞失败", Toast.LENGTH_SHORT).show();
//	            		rl_cricle_good_btn.setEnabled(true);
//	            		iv_cricle_good_icon.setImageDrawable(getResources().getDrawable(R.drawable.cricle_list_item_good_icon));
//					break;
//					case 1:
//						rl_cricle_good_btn.setEnabled(false);
//			    		iv_cricle_good_icon.setImageDrawable(getResources().getDrawable(R.drawable.cricle_list_item_good_press_icon));
//			    		tv_cricle_reply_num.setText(Integer.parseInt(tv_cricle_reply_num.getText().toString()) + 1 + "");
//						break;
//					case 4:
//						SouYueToast.makeText(this.getActivity(), "亲，您已经点过赞哦", Toast.LENGTH_SHORT).show();
//						break;
//					default:
//						break;
//				}
//			}
//	   }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	//强制执行下拉刷新
        if(requestCode ==CircleIndexActivity.REQUEST_CODE_LOGIN_ACTIVITY){
        	if(pullToRefreshListView != null){
       		 pullToRefreshListView.startRefresh();
       	}
        }

        if(requestCode == CircleIndexActivity.REQUEST_CODE_POST_DETAIL_ACTIVITY && data != null){
            boolean isSubscribeSuccess = data.getBooleanExtra("isSubscribeSuccess", false);
            boolean isLogin = data.getBooleanExtra("isLogin", false);
            if(isSubscribeSuccess || isLogin){
            	if(pullToRefreshListView != null){
           		 pullToRefreshListView.startRefresh();
           	}
            }
        }
        
    	 if (requestCode == 200 && resultCode!=getActivity().RESULT_CANCELED) {    //精华去相机
			String picPath = null;
			if (listManager != null && listManager.getImageFileUri() != null) {
				picPath = Utils.getPicPathFromUri(listManager.getImageFileUri(), getActivity());
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
                listManager.getCircleFollowDialog().addImagePath(list);

			} else {
				Toast.makeText(getActivity(), R.string.self_get_image_error, Toast.LENGTH_LONG).show();
			}
		} else if (resultCode == 0x200 && requestCode == 100  && data != null) {  //精华区相册
			List<String> list = new ArrayList<String>();
			list = data.getStringArrayListExtra("imgseldata");
//             if (adapter != null && adapter.getImageFileUri() != null) {
             listManager.getCircleFollowDialog().addImagePath(list);
//             }
		}
        if(requestCode == HomeListManager.SHARE_TO_SSO_REQUEST_CODE)
        {
            listManager.doSsoHandler(requestCode, resultCode, data);
        }

    }

    public void setOnRoleChangeListener(OnChangeListener onRoleChangeListener) {
        this.onRoleChangeListener = onRoleChangeListener;
    }

    public String getSrp_id() {
        return srp_id;
    }

	@Override
	public void loadDataMore(long start, String type) {
		loadSelfData();
	}

    public void setIsBanTalk(int isBanTalk){
        listManager.setmIsBanTalk(isBanTalk);
    }
}
