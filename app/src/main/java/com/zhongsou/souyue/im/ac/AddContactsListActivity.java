package com.zhongsou.souyue.im.ac;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.tuita.sdk.BroadcastUtil;
import com.tuita.sdk.im.db.module.NewFriend;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.model.PersonPageParam;
import com.zhongsou.souyue.im.dialog.ImDialog;
import com.zhongsou.souyue.im.dialog.ImDialog.Builder.ImDialogInterface;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.IMApi;
import com.zhongsou.souyue.im.util.ImUtils;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.ui.ImRequestDialog;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.SYInputMethodManager;
import com.zhongsou.souyue.utils.SYUserManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 添加好友<br>
 * 从网络查找获取要添加的好友
 * 
 * @author "jianxing.fan@iftek.cn"
 * 
 */
public class AddContactsListActivity extends IMBaseActivity {

	private AddContactsAdapter adapter;
	private List<NewFriend> data;
	private ListView listView;
    private LinearLayout addContactView;
	private EditText search_edit;
    private Button btnSearchClear;
    private String after;
	private SYInputMethodManager syInputMng;
	private View ll_other;
    private LinearLayout llBackground;

	private MsgBroadCastReceiver br;
	private User u;
//	private ImProgressMsgDialog dialog;
	private View im_noresul;
    private String keyWord = "";
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (adapter != null)
				adapter.notifyDataSetChanged();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					dismissProgress();
					if (adapter.getCount() == 0) {
						im_noresul.setVisibility(View.VISIBLE);
						TextView noresul = (TextView) findViewById(R.id.im_noresult_text);
						noresul.setText(R.string.im_no_result);
					} else {
						im_noresul.setVisibility(View.GONE);
					}
				}
			}, 500);
		};
	};

	protected void onDestroy() {
		super.onDestroy();
		if (br != null)
			unregisterReceiver(br);

		// instance = null;
	};

	public class MsgBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context mContext, Intent intent) {
			String string = intent.getStringExtra("data");
			try {
				JSONObject json = new JSONObject(string);
				JSONArray ja = null;
				if (json != null)
					ja = json.getJSONArray("users");
				if (ja != null) {
					data.clear();
					for (int i = 0; i < ja.length(); i++) {
						JSONObject jo = ja.getJSONObject(i);
						// {"uid":124,"nick":"ios2","avatar":"http://usc.zhongsou.com/images/public/HeadImg/default_2.jpg","s":1}
						NewFriend nf = new NewFriend();
						nf.setAvatar(jo.getString("avatar"));
						nf.setChat_id(jo.getLong("uid"));
						nf.setNick_name(jo.getString("nick"));
						nf.setStatus(jo.getInt("s"));
						data.add(nf);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			handler.sendEmptyMessage(0);
		}

	}

	@Override
	protected void onPause() {
		dismissProgress();
		super.onPause();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		u = SYUserManager.getInstance().getUser();
		setContentView(R.layout.im_add_contacts_list_view_activity);
        llBackground= (LinearLayout) findViewById(R.id.ll_background);
        addContactView = (LinearLayout)findViewById(R.id.ll_add_contacts_view);
		br = new MsgBroadCastReceiver();
		IntentFilter intentFilter = new IntentFilter(BroadcastUtil.ACTION_SEARCH);
		registerReceiver(br, intentFilter);
		((TextView) findViewById(R.id.activity_bar_title)).setText("添加好友");
		ImageView addFriend = (ImageView) findViewById(R.id.text_btn);
		addFriend.setVisibility(View.GONE);
		syInputMng = new SYInputMethodManager(this);
		listView = (ListView) findViewById(R.id.example_lv_list);
		im_noresul = findViewById(R.id.im_noresult);
		ll_other = findViewById(R.id.ll_other);
		ll_other.setVisibility(View.GONE);
		search_edit = (EditText) findViewById(R.id.search_edit);
        search_edit.setHint(R.string.search_net);
		search_edit.setHintTextColor(getResources().getColor(R.color.im_edit_text_hint_color));
		btnSearchClear = (Button) findViewById(R.id.btn_search_clear);
        btnSearchClear.setVisibility(View.GONE);
        btnSearchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_edit.setText("");
                btnSearchClear.setVisibility(View.GONE);
            }
        });

        search_edit.addTextChangedListener(new TextWatcher() {
            String before = null;
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                before = s.toString();
            }
            @Override
            public void afterTextChanged(Editable s) {
                after = s.toString();
                if (before != null && before.equals(after)) {// 没有改变
                    return;
                }
                if (!TextUtils.isEmpty(after)) {// 有输入
                    btnSearchClear.setVisibility(View.VISIBLE);
                } else {
                    btnSearchClear.setVisibility(View.GONE);
                }

            }
        });

		search_edit.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (TextUtils.isEmpty(v.getText().toString()))
					return false;

                llBackground.setVisibility(View.GONE);
				boolean status = ImserviceHelp.getInstance().im_search(v.getText().toString());
				syInputMng.hideSoftInput();
				showProgress();
                keyWord = v.getText().toString();
                if (!status) {
                    handler.sendEmptyMessage(0);
                }
                im_noresul.setVisibility(View.GONE);
                if ((event == null && actionId == EditorInfo.IME_ACTION_SEARCH)) {
                    return true;
                }
				return false;
			}
		});
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				search_edit.requestFocus();
				InputMethodManager imm = (InputMethodManager) search_edit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
			}
		}, 500);
		data = new ArrayList<NewFriend>();
		// alphaIndex = new HashMap<String, Integer>();
		adapter = new AddContactsAdapter();
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (parent instanceof ListView) {
					ListView lv = (ListView) parent;
					int hc = lv.getHeaderViewsCount();
					if (position < hc) {
						return;
					}
                    //跳转个人中心  4.1
                    IMApi.IMGotoShowPersonPage(AddContactsListActivity.this, adapter.getItem(position - hc), PersonPageParam.FROM_IM);
//					ImFriendInfoActivity.startImFriendInfo(AddContactsListActivity.this, adapter.getItem(position - hc));
				}
			}
		});

		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				syInputMng.hideSoftInput();
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});

	}

	public int convertDpToPixel(float dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}

	public class AddContactsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public NewFriend getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final NewFriend item = (NewFriend) getItem(position);
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				convertView = inflater.inflate(R.layout.im_add_contacts_list_view_row, parent, false);
				holder = new ViewHolder();

				holder.ivImage = (ImageView) convertView.findViewById(R.id.row_iv_image);
				holder.tvTitle = (TextView) convertView.findViewById(R.id.row_tv_title);
				holder.bAction_add = (Button) convertView.findViewById(R.id.row_btn_add);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			int status = item.getStatus();
            if(status == 2){
                holder.bAction_add.setTextColor(getResources().getColor(R.color.im_already_isfriend));
				updateBackgroundResourceWithRetainedPadding(holder.bAction_add,R.drawable.has_add);
                holder.bAction_add.setText("已添加");
            }else{
                holder.bAction_add.setTextColor(Color.WHITE);
				updateBackgroundResourceWithRetainedPadding(holder.bAction_add, R.drawable.im_add_friend);
                holder.bAction_add.setText("添加");
            }
			holder.bAction_add.setTag(status);
			holder.bAction_add.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int s = (Integer) v.getTag();
					if (s != 2) {
						ImDialog.Builder builder = new ImDialog.Builder(v.getContext());
						builder.setTitle(R.string.im_dialog_title);
                        builder.setEditShowMsg("我是"+SYUserManager.getInstance().getName());
						builder.setSelection(true);
						builder.setPositiveButton("发送", new OKClickListener(item, v.getContext()));
						builder.create().show();
					}
				}
			});
			if (TextUtils.isEmpty(item.getAvatar())) {
				holder.ivImage.setImageResource(R.drawable.default_head);
			} else {
				//aQuery.id(holder.ivImage).image(item.getAvatar(), true, true, 0, R.drawable.default_head);
                                  PhotoUtils.showCard( PhotoUtils.UriType.HTTP,item.getAvatar(),holder.ivImage, MyDisplayImageOption.options);
			}

//			if (item.getNick_name() != null && item.getNick_name().contains(search_edit.getText().toString())) {
//
//				int index = item.getNick_name().indexOf(search_edit.getText().toString());
//
//				int len = search_edit.getText().toString().length();
//
//				Spanned temp = Html.fromHtml(item.getNick_name().substring(0, index)
//						+ "<font color=#418ec9>"
//						+ item.getNick_name().substring(index, index + len) + "</font>"
//						+ item.getNick_name().substring(index + len, item.getNick_name().length()));
//
//				holder.tvTitle.setText(temp);
//			} else {
//				holder.tvTitle.setText(item.getNick_name());
//			}
			holder.tvTitle.setText(ImUtils.getHighlightText(item.getNick_name(),item.getNick_name().toUpperCase(),keyWord.toUpperCase()));

			return convertView;
		}

		public void updateBackgroundResourceWithRetainedPadding(View view, int resourceID)
		{
			int bottom = view.getPaddingBottom();
			int top = view.getPaddingTop();
			int right = view.getPaddingRight();
			int left = view.getPaddingLeft();
			view.setBackgroundResource(resourceID);
			view.setPadding(left, top, right, bottom);
		}

		public class OKClickListener implements ImDialogInterface {
			NewFriend item;
			WeakReference<Context> weakReference;

			public OKClickListener(NewFriend item, Context ctx) {
				this.item = item;
				weakReference = new WeakReference<Context>(ctx);
			}

			@Override
			public void onClick(DialogInterface dialog, final View v) {
				if (item != null && weakReference != null && weakReference.get() != null) {
					item.setMyid(u.userId());
					item.setStatus(0);
					item.setAllow_text(v.getTag().toString());
					ImRequestDialog mDialog = new ImRequestDialog(AddContactsListActivity.this);
					mDialog.setCancelable(true);

					mDialog.show();
					if(CMainHttp.getInstance().isNetworkAvailable(AddContactsListActivity.this)){
						if(ImserviceHelp.getInstance().im_userOp(1, item.getChat_id(), item.getNick_name(), item.getAvatar(),v.getTag().toString(),1)){
							mDialog.mDismissDialog();
							SouYueToast.makeText(AddContactsListActivity.this, getString(R.string.im_send_success), Toast.LENGTH_SHORT).show(); //发送成功不提示
						}else{
							mDialog.mDismissDialog();
							SouYueToast.makeText(AddContactsListActivity.this, getString(R.string.im_server_busy), Toast.LENGTH_SHORT).show();
						}
					}else{
						mDialog.mDismissDialog();
						SouYueToast.makeText(AddContactsListActivity.this, getString(R.string.im_net_unvisiable), Toast.LENGTH_SHORT).show();
					}
				}
			}

		}

		public class ViewHolder {
			public Button bAction_add;
			public TextView tvTitle;
			public ImageView ivImage;
		}
	}

    @Override
    public void onBackPressed() {
        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        finish();
    }
}
