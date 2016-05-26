
package com.zhongsou.souyue.circle.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;
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
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.circle.util.OnChangeListener;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.countUtils.ZSSdkUtil;
import com.zhongsou.souyue.fragment.SRPFragment;
import com.zhongsou.souyue.module.Ad;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.CrouselItemBean;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CircleBarListReq;
import com.zhongsou.souyue.net.circle.InterestSubscriberReq;
import com.zhongsou.souyue.net.circle.MblogList4Req;
import com.zhongsou.souyue.net.news.CommonReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zoulu on 14-7-29 Description:圈吧Fragment
 */
public class CircleBarFragment extends SRPFragment implements ProgressBarHelper.ProgressBarClickListener,
		View.OnClickListener, LoadingDataListener, AbsListView.OnScrollListener,IVolleyResponse {

	private View view;
	private List<CircleResponseResultItem> items = new ArrayList<CircleResponseResultItem>();
	private CircleListAdapter adapter;
	private ProgressBarHelper pbHelper;
	public PullToRefreshListView pullToRefreshListView;
	private ListViewAdapter listViewAdapter;
	private CircleListManager listManager;
	private TextView activity_bar_title;
	private View footerView;
	private TextView getMore;
	private RelativeLayout rl_cricle_good_btn;
	private ImageView iv_cricle_good_icon;
	private TextView tv_cricle_reply_num;

	private int pno = 1; // 页码
	private int psize = 10; // 分页大小
	private long interest_id;// 圈子ID
	private boolean isLoading = false;

	private int visibleLast = 0;
	private boolean needLoad;
	private boolean isLoadAll = false;
//	private Http http;
//	private AQuery aquery;

	private String token;
	private int role = -1;
	private String title;
	private String srp_id;
    private String tag_id;
    private String onlyjing = "";
    private String nickName;
    private boolean pullToRefresh = false;
    private OnChangeListener onRoleChangeListener;

    private ImageButton btn_new;
	private String mUpdateTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		interest_id = getArguments().getLong("interest_id", 0);
		title = getArguments().getString("title");
        srp_id = getArguments().getString("srp_id");
        keyWord =getArguments().getString("keyword");
        tag_id = getArguments().getString("tag_id");
        onlyjing = getArguments().getString("onlyjing");
        nickName = getArguments().getString("nickName");
		if (title == null)
			title = "";
//		http = new Http(this);
//		aquery = new AQuery(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.circle_list_pager, null);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init();
	}
	
	private void init() {
        pno = 1;
        visibleLast = 0;
        needLoad = false;
        isLoadAll = false;

		initView();
		initData();
	}
	

	private void initView() {
		token = SYUserManager.getInstance().getToken();
		activity_bar_title = (TextView) view.findViewById(R.id.activity_bar_title);
		activity_bar_title.setText(title + "吧");
		// 加载提示框
		pbHelper = new ProgressBarHelper(getActivity(), view.findViewById(R.id.ll_data_loading));
		pbHelper.setProgressBarClickListener(this);

        btn_new = (ImageButton) view.findViewById(R.id.btn_new);
        btn_new.setOnClickListener(this);
        if("1".equals(onlyjing)){
            btn_new.setVisibility(View.GONE);
        }else{
            btn_new.setVisibility(View.VISIBLE);
        }
        isLoadAll = false;
        isLoading = false;
		// 下拉刷新相关
		pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_cricle_list);
		this.setPullToRefreshListView(pullToRefreshListView);

		// 添加底部加载
		footerView = getActivity().getLayoutInflater().inflate(R.layout.cricle_single_list_refresh_footer, null);

		pullToRefreshListView.getRefreshableView().addFooterView(footerView, null, false);
		// 加载失败
		getMore = (TextView) view.findViewById(R.id.get_more);
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


//		adapter = new CircleListAdapter(getActivity(), interest_id, tag_id, onlyjing);
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
//
//            }
//        });
//		adapter.setLoadingDataListener(this);
//		pullToRefreshListView.setAdapter(adapter);

		pullToRefreshListView.setAdapter(listViewAdapter);
		pullToRefreshListView.setOnScrollListener(this);
		pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (position == 0 || position > listViewAdapter.getCount())
					return;
				BaseListData item = listViewAdapter.getDatas().get(position-1);
				item.setHasRead(true);
//                item.getInvoke().setChan(mTitle);
				listManager.clickItem(item);
//                setHasRead(item);
				getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
				listViewAdapter.notifyDataSetChanged();

//				CircleResponseResultItem item = (CircleResponseResultItem) parent.getAdapter().getItem(position);
//                SearchResultItem item1 = new SearchResultItem();
//                item1.setBlog_id(item.getBlog_id());
//                item1.keyword_$eq(keyWord);
//                item1.srpId_$eq(srp_id);
//                item1.setInterest_id(interest_id);
//                item1.setSign_id(item.getSign_id());
//                IntentUtil.skipDetailPage(getActivity(),item1,CircleIndexActivity.REQUEST_CODE_POST_DETAIL_ACTIVITY);
//
                /*Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("blog_id",  item.getBlog_id());
                intent.putExtra("interest_id", interest_id);
                intent.putExtra("title", title);
                intent.putExtra("srp_id", srp_id);
                intent.putExtra("is_from_list", true);
                intent.putExtra("keyword",keyWord);
                getActivity().startActivityForResult(intent, CircleIndexActivity.REQUEST_CODE_POST_DETAIL_ACTIVITY);
                getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);*/
			}
		});

		loadSelfData();

		// 下拉刷新监听
//		pullToRefreshListView.onUpdateTime(adapter.getChannelTime());
		pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				if (!CMainHttp.getInstance().isNetworkAvailable(context)) {
					UIHelper.ToastMessage(getActivity(), R.string.cricle_manage_networkerror);
					pullToRefreshListView.onRefreshComplete();
					if (getMore.getVisibility() == View.GONE) {
						getMore.setVisibility(View.VISIBLE);
					}

					return;
				} else {
					visibleLast = 0;
					pno = 1;
					isLoadAll = false;
                    pullToRefresh = true;
//					http.getNewSingleCricleList(interest_id, 0l, pno, psize, SYUserManager.getInstance().getToken(), tag_id,onlyjing);
					loadMblogList4(interest_id, 0l, pno, psize, SYUserManager.getInstance().getToken(), tag_id,onlyjing);
				}
			}
		});
		pullToRefreshListView.setOnTimeRefreshListener(new PullToRefreshBase.OnTimeRefreshListener() {
			@Override
			public void onTimeRefresh() {
//				if (null != adapter.getChannelTime())
//					pullToRefreshListView.onUpdateTime(StringUtils.convertDate(adapter.getChannelTime()));
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
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
//			adapter.updateSingleRow((CircleResponseResultItem) msg.obj, msg.arg1);
			listManager.updateSingleRow((CircleResponseResultItem)msg.obj, msg.arg1);
		}
	};

	private void initData() {
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
						if (getMore.getText() != null && getResources().getString(R.string.cricle_no_more_data).equals(getMore.getText())) {
							getMore.setText(R.string.mores);
						}
						getMore.setVisibility(View.VISIBLE);
					}
				}
				return;
			} else {
				if (getMore.getVisibility() == View.VISIBLE) {
					getMore.setVisibility(View.GONE);
				}
				isLoading = true;
			}

		}
	}

	public PullToRefreshListView getPullToRefreshListView() {
		return pullToRefreshListView;
	}

	public void setPullToRefreshListView(PullToRefreshListView pullToRefreshListView) {
		this.pullToRefreshListView = pullToRefreshListView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(null != pbHelper){
			if(pbHelper.isLoading){
				pbHelper.goneLoading();
			}
		}

        try{
            getActivity().unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
	}

	@Override
	public void clickRefresh() {
		isLoadAll = false;
		listViewAdapter.clear();
		loadSelfData();
	}

	public long getInterest_id() {
		return interest_id;
	}

	public void setInterest_id(long interest_id) {
		this.interest_id = interest_id;
	}

//	@Override
//	public void onHttpError(String methodName, AjaxStatus status) {
//		pbHelper.goneLoading();
//	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.get_more:
			loadSelfData();
			break;
        case R.id.btn_new:
            if (!IntentUtil.isLogin()) {
                IntentUtil.goLoginForResult(getActivity(), CircleIndexActivity.REQUEST_CODE_LOGIN_ACTIVITY);
                return;
            }else if(CircleListAdapter.role == Constant.ROLE_NONE) { // 非圈成员
                showJoinInterest();
                return;
            } else if(CircleListAdapter.is_bantalk == Constant.MEMBER_BAN_TALK_YES){
                SouYueToast.makeText(getActivity(), "您已被禁言", Toast.LENGTH_SHORT).show();
                return;
            }
			UmengStatisticUtil.onEvent(getActivity(), UmengStatisticEvent.CIRCLE_SEND_POST);
            UIHelper.showPublish(getActivity(),null,interest_id,srp_id,PublishActivity.PUBLISH_TYPE_M_NEW,tag_id,nickName);
            break;
		default:
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void loadDataMore(long start, String type) {
		loadSelfData();
	}

	@Override
	public void onScrollStateChanged(AbsListView absListView, int scrollState) {
		int itemsLastIndex = listViewAdapter.getCount();
		if (itemsLastIndex < 0) {
			return;
		}
		int lastIndex = itemsLastIndex;
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLast == lastIndex && needLoad) {
			needLoad = false;
			loadSelfData();
		}
	}

	@Override
	public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		visibleLast = firstVisibleItem + visibleItemCount - 2;
		if (isLoadAll) {
			++visibleLast;
		}
	}

	/**
	 * ============================服务返回方法开始
	 */
	/**
	 * 返回圈吧列表(4.0)
	 * 
	 * @param res
	 */
//	public void getNewSingleCricleListSuccess(HttpJsonResponse res) {
//		getNewSingleCricleListSuccessFormData(res,status.getTime().getTime() + "");
//	}

	public void getNewSingleCricleListSuccessFormData(IRequest res,String data)
	{
		if(pullToRefresh) {
			if (pbHelper.isLoading) {
				pbHelper.goneLoading();
			}
			pullToRefreshListView.onRefreshComplete();
//			listManager.setChannelTime(data);
//			listViewAdapter.setChannelTime(data);
			updateDataToAdapter(res, 0);
			pullToRefresh = false;
		} else {
			// 关闭提示框
			pbHelper.goneLoading();
			updateDataToAdapter(res, 1);
		}
	}
//	public void getNewSingleCricleListToPullDownRefreshSuccess(HttpJsonResponse res, AjaxStatus status) {
//		if (pbHelper.isLoading) {
//			pbHelper.goneLoading();
//		}
//		pullToRefreshListView.onRefreshComplete();
//		adapter.setChannelTime(status.getTime().getTime() + "");
//		updateDataToAdapter(res, TYPE_PULL_TO_REFREASH);
//	}

	public void getPraisePostCountSuccess(HttpJsonResponse res) {
		int statusCode = res.getCode();
		if (statusCode != 200) {
			return;
		}
		JsonObject body = res.getBody();
		String responseResult = body.toString();
		if (!TextUtils.isEmpty(responseResult)) {
//			String state = JSON.parseObject(responseResult).getString("state");
			String state = body.get("state").getAsString();
			switch (Integer.parseInt(state)) {
			case 0: // //0-失败 1-成功 3-帖子不存在 4-已经点过赞
				// SouYueToast.makeText(this.getActivity(), "点赞失败",
				// Toast.LENGTH_SHORT).show();
				rl_cricle_good_btn.setEnabled(true);
				iv_cricle_good_icon.setImageDrawable(getResources().getDrawable(R.drawable.cricle_list_item_good_icon));
				break;
			case 1:
				rl_cricle_good_btn.setEnabled(false);
				iv_cricle_good_icon.setImageDrawable(getResources().getDrawable(R.drawable.cricle_list_item_good_press_icon));
				tv_cricle_reply_num.setText(Integer.parseInt(tv_cricle_reply_num.getText().toString()) + 1 + "");
				break;
			case 4:
				SouYueToast.makeText(this.getActivity(), "亲，您已经点过赞哦", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}

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

//		CircleResponseResult result = new CircleResponseResult(res);// true:带置顶区域
//		items = result.getItems();
		
		if (!newslist.isEmpty()) {
            if(type == 0) {
                listViewAdapter.clear();
				setDatas(topList, focusList, newslist);
            }else
			{
				listViewAdapter.addLast(newslist);
			}
//            adapter.addMore(items);

            pno++;
			pullToRefreshListView.onRefreshComplete();
		}

		if (newslist == null || newslist.isEmpty() || newslist.size() < psize) {
			isLoadAll = true;
			if (listViewAdapter.getCount() != 0) {
				getMore.setVisibility(View.VISIBLE);
				//getMore.setText(R.string.cricle_no_more_data);    暂时去掉已加载全部问题提示
                getMore.setText("");
			}
		}

		if (newslist.size() == 0 && listViewAdapter.getCount() == 0) {
			pbHelper.showNoData();
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
	public void showToast(int resId) {
		SouYueToast.makeText(getActivity(), getResources().getString(resId), 0).show();
	}

	private void loadSelfData() {
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
//						if (getMore.getText() != null && getResources().getString(R.string.cricle_no_more_data).equals(getMore.getText())) {
							getMore.setText(R.string.mores);
//						}
						getMore.setVisibility(View.VISIBLE);
					}
				}
				return;
			} else {
				if (getMore.getVisibility() == View.VISIBLE) {
					getMore.setVisibility(View.GONE);
				}
				isLoading = true;
//				http.getNewSingleCricleList(interest_id, adapter.getLastId(), pno, psize, SYUserManager.getInstance().getToken(), tag_id,onlyjing);
				loadMblogList4(interest_id, Long.parseLong(getLastId()), pno, psize, SYUserManager.getInstance().getToken(), tag_id,onlyjing);
				Log.v("test", "【Circle】"+"page:"+pno + "    psize:"+psize+"   lastid:"+getLastId());
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
		
		if (requestCode == 2 && resultCode!=getActivity().RESULT_CANCELED) {  //圈吧相机
			String picPath = null;
			if (listViewAdapter != null && listManager.getImageFileUri() != null) {
				picPath = Utils.getPicPathFromUri(listManager
						.getImageFileUri(), getActivity());
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

                if(listViewAdapter != null && listManager.getCircleFollowDialog() != null) {
					listManager.getCircleFollowDialog().addImagePath(list);
                }
            }
		} else if (resultCode == 0x200 && requestCode == 1 && data != null) {  //圈吧相册
			List<String> list = new ArrayList<String>();
			list = data.getStringArrayListExtra("imgseldata");
            if(listViewAdapter != null && listManager.getCircleFollowDialog() != null) {
				listManager.getCircleFollowDialog().addImagePath(list);
            }
		}
		if(requestCode == HomeListManager.SHARE_TO_SSO_REQUEST_CODE)
		{
			listManager.doSsoHandler(requestCode, resultCode, data);
		}

	}

	@Override
	public void loadData() {

	}

    public void setOnRoleChangeListener(OnChangeListener onRoleChangeListener) {
        this.onRoleChangeListener = onRoleChangeListener;
    }

    public String getSrp_id() {
        return srp_id;
    }

    private void showJoinInterest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("您还不是该圈的成员，是否立即加入？").setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if(CircleListAdapter.is_private){//判断是私密圈还是公开圈，进行跳转,私密圈，进行申请
                            IntentUtil.gotoSecretCricleCard(getActivity(), id);
                        }else{//公开圈，直接加入圈子
//                            Map<String, Object> params = new HashMap<String, Object>();
//                            params.put("token", SYUserManager.getInstance().getToken() +"");
//                            params.put("interest_ids", interest_id);
//                            http.saveRecomentCircles(params);
							loadSaveRecomentCircles(SYUserManager.getInstance().getToken() +"",interest_id+"");
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        }).create().show();
    }

	/**
	 * 加载数据-----圈子主贴列表（4.0）
	 * @param interest_id
	 * @param last_sort_num
	 * @param pno
	 * @param psize
	 * @param token
	 * @param tag_id
     * @param onlyjing
     */
	private void loadMblogList4( long interest_id,long last_sort_num, int pno, int psize,String token, String tag_id,String onlyjing)
	{
		CircleBarListReq req =new CircleBarListReq(HttpCommon.CIRLCE_BAR_LIST_REQUEST,this);
		req.setParams(interest_id,last_sort_num,pno,psize,token,tag_id,onlyjing);
		CMainHttp.getInstance().doRequest(req);
	}
	/**
	 *  加载数据-----订阅兴趣圈
	 * @param token
	 * @param interest_id
     */
	private void loadSaveRecomentCircles(String token,String interest_id)
	{
		InterestSubscriberReq req =  new InterestSubscriberReq(HttpCommon.CIRLCE_INTEREST_SUB_ID,this);
		req.setParams(token,interest_id,ZSSdkUtil.CIRCLEBAR_SUBSCRIBE_GROUP);
		CMainHttp.getInstance().doRequest(req);

	}
	@Override
	public void onHttpResponse(IRequest request) {
		mUpdateTime = System.currentTimeMillis()+"";
		switch (request.getmId())
		{
			case HttpCommon.CIRLCE_BAR_LIST_REQUEST:
				getNewSingleCricleListSuccessFormData(request,new Date().getTime()+"");
				break;
			case HttpCommon.CIRLCE_INTEREST_SUB_ID:
				saveRecomentCirclesSuccess((HttpJsonResponse)request.getResponse());
				break;
		}
	}

	@Override
	public void onHttpError(IRequest request) {
			pbHelper.goneLoading();
	}

	@Override
	public void onHttpStart(IRequest request) {

	}
    public void saveRecomentCirclesSuccess(HttpJsonResponse res) {
        if(res.getBody().get("state").getAsInt() == 1){
            Toast.makeText(getActivity(), "订阅成功", Toast.LENGTH_SHORT).show();
            UpEventAgent.onGroupJoin(getActivity(), interest_id + "." + "", "");// 统计
            CircleListAdapter.role = Constant.ROLE_NORMAL;
            if(onRoleChangeListener != null){
                onRoleChangeListener.onChange(null);
            }
        } else {
            Toast.makeText(getActivity(), "订阅失败", Toast.LENGTH_SHORT).show();
        }
    }

	public void setIsBanTalk(int isBanTalk){
		listManager.setmIsBanTalk(isBanTalk);
	}
}
