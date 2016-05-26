package com.zhongsou.souyue.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.SendNextGridAdapter;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.db.SelfCreateHelper;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.GroupKeywordItem;
import com.zhongsou.souyue.module.SelfCreateItem;
import com.zhongsou.souyue.module.ToolTip;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.other.ToolTipRequest;
import com.zhongsou.souyue.net.srp.SrpGetSubscibedRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.service.SendUtils;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.ProgressBarHelper.ProgressBarClickListener;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 选择原创关键词 （选择发布范围）
 * 
 * @author wcy
 * 
 */

public class SendNextActivity extends RightSwipeActivity implements OnClickListener, ProgressBarClickListener {

    private static final int SELECT_COUNT = 3;
//	private Http http;
	private CMainHttp mainHttp;
	private User user;
	private String token, url;
	private SYUserManager sym;
	private SelfCreateItem sci;
	private boolean haschange;
	private boolean weibochecked;
	private boolean isSame = false;  //在listview列表中搜索出的词是否和gridview中订阅的词相同
	private TextView title, tv_send;

//	private LinkedList<String> srpIds;
//	private LinkedList<String> keywords;   //要发出去的关键词


    private LinkedList<String> mSelectKeys;
    private LinkedList<String> mSelectKeyIds;

	private ProgressBarHelper pbHelper;
	private GridView select_gridview;
	private SendNextGridAdapter gridadapter;
	private TextView gridtext;

	private int clickcount;
	private String maxSelect;
	private String listSelectKeyword;
	// private SubscribeKeywordList subscribekeywordlist;
	// 存放列表条目数据的集合
	private List<GroupKeywordItem> mykeywordlist;
	private List<String> groupKeywords;

//	private GroupKeywordItem groupkeyworditem;

	// -------------搜索相关--------------------------
	private EditText select_search;
	private ArrayList<ToolTip> listItems;
//	private AQuery aq;
	private ListItemAdapter adapter;
	private ListView searchList;
	protected boolean isDetect = true;
	private String keyword = "";
	private static final int LOADING = 0;
	private static final int DEFAULT = 1;
	private static final int HISTORY = 2;
	private static final int SEARCH = 3;
	protected boolean keybordShowing;
	private View sou;
	private ImageButton search_clear_it;
	private Button bt_search_down;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sendblognext_new);
		user = SYUserManager.getInstance().getUser();
		token = SYUserManager.getInstance().getToken();
		url = user.url();
		Log.i("SendNextActivity", "user.url()=" + user.url());
//		http = new Http(this);
		mainHttp = CMainHttp.getInstance();
		getDataFromIntent();
		initView();
		initInputEditView();
		loaddata();
	}

	private void loaddata() {
		// http.subscribeKeywordList(user != null ? user.token() : "");
//		http.getSubscibe(UrlConfig.subscibe, user.token());
		SrpGetSubscibedRequest getSubscibedRequest = new SrpGetSubscibedRequest(HttpCommon.SRP_GET_SUBSCRIBED_REQUEST, this);
		mMainHttp.doRequest(getSubscibedRequest);
	}

	/**
	 * 回调——得到订阅关键词的模型
	 * 
	 * @param items
	 */
/*	public void getSubscibeSuccess(List<GroupKeywordItem> items, AjaxStatus as) {
		pbHelper.goneLoading();
		Log.i("SendNextActivity", "items.toString()=" + items.toString());

		mykeywordlist = items;
		gridadapter.setdata(mykeywordlist);
		gridadapter.notifyDataSetChanged();
	}*/

	public void getSubscibeSuccess(List<GroupKeywordItem> items) {
		pbHelper.goneLoading();
		Log.i("SendNextActivity", "items.toString()=" + items.toString());

		mykeywordlist = items;
		gridadapter.setdata(mykeywordlist);
		gridadapter.notifyDataSetChanged();
	}
//	/**
//	 * 关键词搜索成功回调
//	 *
//	 * @param tips
//	 */
//	public void tooltipSuccess(List<ToolTip> tips) {
//		listItems.clear();
//		if (tips != null && tips.size() != 0) {
//			for(ToolTip t:tips){
//                if(isInKeywords(t.keyword(),t.srpId())){
//                    t.ischeck_$eq(true);
//                }
//                listItems.add(t);
//            }
//		}
//		if (TextUtils.isEmpty(select_search.getText().toString())) {
//			listItems.clear();
//		}
//		runOnUiThread(new Runnable() {
//			public void run() {
//				adapter.notifyDataSetChanged();
//			}
//		});
//		showView(SEARCH);
//	}

	private void getDataFromIntent() {
		Intent intent = getIntent();
		sci = (SelfCreateItem) intent.getSerializableExtra("selfCreateItem");
		haschange = intent.getBooleanExtra("haschange", false);
		weibochecked = intent.getBooleanExtra("weibochecked", false);
	}

	private void initView() {

//		srpIds = new LinkedList<String>();
		tv_send = findView(R.id.text_btn);
		tv_send.setText(getString(R.string.title_bar_send));
		title = findView(R.id.activity_bar_title);
		search_clear_it = findView(R.id.search_clear_it);
		bt_search_down = findView(R.id.bt_search_down);
		searchList = (ListView) findViewById(R.id.search_result_list);
		select_search = findView(R.id.select_search);
		tv_send.setOnClickListener(this);
		select_search.setOnClickListener(this);
		bt_search_down.setOnClickListener(this);

//		keywords = new LinkedList<String>();
        mSelectKeys = new LinkedList<String>();
        mSelectKeyIds = new LinkedList<String>();
		groupKeywords = new ArrayList<String>();
			
		title.setText(getString(R.string.selectnextscope));
		pbHelper = new ProgressBarHelper(this, findViewById(R.id.ll_data_loading));
		pbHelper.setProgressBarClickListener(this);
		select_gridview = (GridView) findViewById(R.id.select_gridview);
		gridadapter = new SendNextGridAdapter(SendNextActivity.this);
		select_gridview.setAdapter(gridadapter);

		select_gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View selectview, int position, long arg3) {
                GroupKeywordItem gk = mykeywordlist.get(position);
				if (gk.ischeck()) {
                    removeKeyword(gk.keyword(),gk.srpId());
				}else {
                    addKeyword(gk.keyword(),gk.srpId());
				}
                checkMyListKeyword();
				gridadapter.setSelected(position);
			}
		});
	}

	private void initInputEditView() {
		listItems = new ArrayList<ToolTip>();
//		aq = new AQuery(this);
		findViewById(R.id.search_clear_it).setOnClickListener(this);
		// loading = (LinearLayout) findViewById(R.id.search_loading);
		initSearchList();
		select_search.setText(keyword);
		select_search.setSelection(keyword.length());
		select_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (isDetect) {
					searchKeyword();
				} else {
					isDetect = true;
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
			}

		});
		showView(DEFAULT);
	}

	/**
	 * 记录最后一次搜索信息
	 * 
	 * @param v
	 */
	private void writeSharedPreferences(String v) {
		sysp.putString(SYSharedPreferences.KEY_LAST_SEARCH_KEYWORD, StringUtils.splitKeyWord(v));
	}

	protected void hideKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
		}
		showView(DEFAULT);
	}

	/**
	 * 搜索下拉选择列表页面
	 */
	private void initSearchList() {
		adapter = new ListItemAdapter(this, SEARCH);
		searchList.setAdapter(adapter);
		searchList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ToolTip tip = null;
				if (listItems.size() > 0) {
					tip = listItems.get(position);
					isDetect = false;
					adapter.setSelected(position);
					if (tip.ischeck()) {
						removeKeyword(tip.keyword(),tip.srpId());
					}else{
                        addKeyword(tip.keyword(),tip.srpId());
                    }
                    checkListKeyword();
					gridadapter.notifyDataSetChanged();

				}
			}
		});
		searchList.setOnTouchListener(hideKeyTouch);
	}

	View.OnTouchListener hideKeyTouch = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent ev) {
			if (ev.getAction() == MotionEvent.ACTION_DOWN) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(getWindow().getDecorView().getApplicationWindowToken(), 0);
				}
				if (keybordShowing) {
					showView(DEFAULT);
				}
			}
			return false;
		}
	};

	/**
	 * 根据输入的字进行搜索
	 */
	private void searchKeyword() {
		String keyword = select_search.getText().toString();
		if (StringUtils.isNotEmpty(keyword)) {
			keyword = keyword.trim();
			if (StringUtils.isNotEmpty(keyword)) {
				showView(LOADING);
//				http.tooltip(keyword, "srpPure");
				ToolTipRequest request = new ToolTipRequest(HttpCommon.TOOLTIP_REQUEST,this);
				request.setParams(keyword, "srpPure");
				mainHttp.doRequest(request);
			}
		} else {
			if (StringUtils.isEmpty(keyword)) {
				showView(DEFAULT);
			}
		}
	}

	// 显示View
	private void showView(int view) {
		switch (view) {
		case LOADING:
			searchList.setVisibility(View.GONE);
			break;
		case DEFAULT:
			searchList.setVisibility(View.GONE);
			break;
		case HISTORY:
			searchList.setVisibility(View.GONE);
			break;
		case SEARCH:
			searchList.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.bt_search_down:
			showView(DEFAULT);
            checkMyListKeyword();
			select_gridview.setVisibility(View.VISIBLE);
			bt_search_down.setVisibility(View.GONE);
			return;

		case R.id.search_clear_it:
			showView(DEFAULT);
			if (StringUtils.isNotEmpty(select_search.getText())) {
				select_search.setText("");
				writeSharedPreferences("");
			}
			return;

		case R.id.select_search:
			Log.i("showToast", "点击搜索响应了");
			bt_search_down.setVisibility(View.VISIBLE);
			select_gridview.setVisibility(View.GONE);
			return;

		case R.id.text_btn:
			ConnectivityManager mConnectivityManager = (ConnectivityManager) SendNextActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);  
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if(mNetworkInfo != null){
				if (mSelectKeys == null || mSelectKeys.size() == 0) {
					showToast(R.string.keywordstring);
					return;
				}
				sci.keyword_$eq(SendUtils.preK(mSelectKeys));
				sci.srpId_$eq(SendUtils.preI(mSelectKeyIds));
				Log.i("SendNextActivity", "keywords = " + mSelectKeys);
				Log.i("SendNextActivity", "srpIds = " + mSelectKeyIds);
				v.setEnabled(SendUtils.sendOrNext(sci, SendNextActivity.this, weibochecked));

			}else{
				showToast(R.string.self_msg_neterror);
			}

			break;
		}
	}

    public void deleteSave(){
        /**
         * 点击发送，删除原来草稿
         */

        if (StringUtils.isEmpty(sci.id()) && sci.status() == ConstantsUtils.STATUS_SEND_ING) {
            SelfCreateHelper.getInstance().delSelfCreateItem(sci);
        }
//        else {
//            http.delSelfCreate(SYUserManager.getInstance().getToken(), sci.id());
//        }
    }

	@Override
	public void clickRefresh() {
		loaddata();
	}

//	@Override
//	public void onHttpError(String methodName, AjaxStatus status) {
//		pbHelper.showNetError();
//	}

	public void showToast(int resId) {
		SouYueToast.makeText(SendNextActivity.this, getResources().getString(resId), 0).show();
	}

	// 适配器
	protected class ListItemAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private int hORs;
                    private  Context context;
		List<ToolTip> items;
		private int selected = 100;
		protected static final int VIEW_TYPE_CONTENT = 1;
		protected static final int VIEW_TYPE_NOSEARCHRESULT = 2;

		public ListItemAdapter(Context context, int hORs) {
			this.mInflater = LayoutInflater.from(context);
			this.hORs = hORs;
			items = listItems;
                        this.context = context;
		}

		public void setSelected(int position) {
			selected = position;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return listItems == null || listItems.size() == 0 ? 1 : listItems.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public int getItemViewType(int position) {
			if (items.size() == 0)
				return VIEW_TYPE_NOSEARCHRESULT;
			else
				return VIEW_TYPE_CONTENT;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			int type = getItemViewType(position);
			if (items.size() == 0 || type == VIEW_TYPE_NOSEARCHRESULT) {
				if (this.hORs == SEARCH)
					return getCurrentFooter(parent);
			}
			if (convertView == null || (convertView == getCurrentFooter(parent))) {
				convertView = mInflater.inflate(R.layout.list_item_search, null);
				holder = new ViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.tv_search_keyword);
				holder.imageView = (ImageView) convertView.findViewById(R.id.btn_search_subscribe);
				holder.rssimg = (ImageView) convertView.findViewById(R.id.iv_search_rss);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (selected == position) {
				if (!listItems.get(position).ischeck()) {
					holder.imageView.setBackgroundResource(R.drawable.subscribe_nomal);
				} else {
					holder.imageView.setBackgroundResource(R.drawable.subscribe_select);
				}
			} else {
				if (!listItems.get(position).ischeck()) {
					holder.imageView.setBackgroundResource(R.drawable.subscribe_nomal);
				} else {
					holder.imageView.setBackgroundResource(R.drawable.subscribe_select);
				}
			}

			ToolTip tip = items.get(position);

			String str = StringUtils.splitKeyWord(tip.keyword()) + " " + (StringUtils.isNotEmpty(tip.m()) ? " (" + tip.m() + ")" : "");
			String str1 = (StringUtils.isNotEmpty(tip.g()) ? tip.g() : "");
			holder.title.setText(Html.fromHtml("<font color=#ff000000>" + str + "</font> " + "<font color=#888888>" + str1 + "</font>"));
			if ("rss".equals(tip.category())) {
				holder.rssimg.setVisibility(View.VISIBLE);
				//aq.id(holder.rssimg).image(tip.rssImage(), true, true, 0, 0, null, AQuery.FADE_IN);
                                  PhotoUtils.showCard( PhotoUtils.UriType.HTTP,tip.rssImage(),holder.rssimg);
			} else {
				holder.rssimg.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}

	}

	/**
	 * 获得无搜索结果view
	 * 
	 * @param parent
	 * @return View
	 */
	private View getCurrentFooter(ViewGroup parent) {
		if (sou == null) {
			sou = inflateView(R.layout.list_item_search_no_res, parent);
		}
		TextView mtv = (TextView) sou.findViewById(R.id.search_sou_keyword);
		String keyword = select_search.getText().toString();
		if (TextUtils.isEmpty(keyword))
			showView(DEFAULT);
		// tv.setText(keyword);
		mtv.setText("无搜索结果");
		return sou;
	}

	/**
	 * 根据id 引入item view
	 * 
	 * @param resId
	 * @param parent
	 * @return View
	 */
	protected View inflateView(int resId, ViewGroup parent) {
		LayoutInflater viewInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return viewInflater.inflate(resId, parent, false);
	}

	private final class ViewHolder {
		public TextView title;
		public ImageView rssimg;
		public ImageView imageView;
	}

    /**---------add by lv.-------------*/

    public void addKeyword(String _key,String _id){
        mSelectKeys.addFirst(_key);
        mSelectKeyIds.addFirst(_id);
        if(mSelectKeyIds.size()>SELECT_COUNT){
            mSelectKeys.removeLast();
            mSelectKeyIds.removeLast();
        }
    }

    public boolean isInKeywords(String _key,String _id){
        if(mSelectKeyIds.contains(_id)&&mSelectKeys.contains(_key)){
            return true;
        }
        return false;
    }

    public void removeKeyword(String _key,String _id){
        mSelectKeys.remove(_key);
        mSelectKeyIds.remove(_id);
    }

    public void checkListKeyword(){
        for (ToolTip t:listItems){
            t.ischeck_$eq(false);
            for (int i=0;i<mSelectKeyIds.size();i++){
                String s=mSelectKeyIds.get(i);
                String key = mSelectKeys.get(i);
                if(t.srpId().contains(s)&&t.keyword().contains(key)){
                    t.ischeck_$eq(true);
                }
            }
        }
    }

    public void checkMyListKeyword(){
        for (GroupKeywordItem t:mykeywordlist){
            t.ischeck_$eq(false);
        }
        boolean isContain = false;
        for (int i =0;i<mSelectKeyIds.size();i++){
            String s = mSelectKeys.get(i);
            String s_id = mSelectKeyIds.get(i);
            isContain = false;
            for (GroupKeywordItem t:mykeywordlist){
                if(t.srpId().contains(s_id)&&t.keyword().contains(s)){
                    isContain = true;
                    t.ischeck_$eq(true);
                }
            }
            if(!isContain){
                GroupKeywordItem gki = new GroupKeywordItem();
                gki.ischeck_$eq(true);
                gki.keyword_$eq(s);
                gki.srpId_$eq(s_id);
                mykeywordlist.add(0,gki);
            }
        }
    }

	@Override
	public void onHttpResponse(IRequest request) {
		super.onHttpResponse(request);
		switch (request.getmId()){
			case HttpCommon.TOOLTIP_REQUEST:
				List<ToolTip> tips = request.getResponse();
				listItems.clear();
				if (tips != null && tips.size() != 0) {
					for(ToolTip t:tips){
						if(isInKeywords(t.keyword(),t.srpId())){
							t.ischeck_$eq(true);
						}
						listItems.add(t);
					}
				}
				if (TextUtils.isEmpty(select_search.getText().toString())) {
					listItems.clear();
				}
				runOnUiThread(new Runnable() {
					public void run() {
						adapter.notifyDataSetChanged();
					}
				});
				showView(SEARCH);
				break;
			case HttpCommon.SRP_GET_SUBSCRIBED_REQUEST:
				List<GroupKeywordItem> items = request.getResponse();
				getSubscibeSuccess(items);
				break;
		}
	}

	@Override
	public void onHttpError(IRequest request) {
		super.onHttpError(request);
		pbHelper.showNetError();
	}

	/**------end------------*/
}
