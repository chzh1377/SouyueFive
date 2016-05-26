package com.zhongsou.souyue.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.db.WendaAnswerHelper;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.module.Wenda;
import com.zhongsou.souyue.module.WendaDetail;
import com.zhongsou.souyue.module.WendaSameAsk;
import com.zhongsou.souyue.module.WendaUpDown;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.srp.SrpAnswerRequest;
import com.zhongsou.souyue.net.srp.SrpQADetailRequest;
import com.zhongsou.souyue.net.srp.SrpQADownRequest;
import com.zhongsou.souyue.net.srp.SrpQASameRequest;
import com.zhongsou.souyue.net.srp.SrpQAUpRequest;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.SYInputMethodManager;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 有问必答
 * @author  qubian 加注释
 * @time 2015-12-11
 */
public class QADetailsActivity extends RightSwipeActivity implements  OnClickListener {

	private TextView author, tv_date, tv_title, sameAsk_count, answer_count;
	private Button sameAsk, send_answer;
	private EditText myanswer;
//	private AQuery aq;
//	private Http http;
	List<ViewHolder> holders;
	private WendaAnswerHelper helper;
	private String token;
	private View qa_question;
	private ListView lv_qa;
	private List<View> views;
	private MyAdapter adapter;
	private String md5;
	private String kid;
	private String id;
	private boolean hasMore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qa_main);
		views = new ArrayList<View>();
		initAllViews();
		setCanRightSwipe(true);
//		http = new Http(this);
		token = SYUserManager.getInstance().getToken();
		holders = new ArrayList<QADetailsActivity.ViewHolder>();
		helper = new WendaAnswerHelper();
//		aq = new AQuery(this);
		initFromIntent();
	}

	private void initFromIntent() {
		Intent intent = getIntent();
		id = intent.getStringExtra("id");
		id=id==null?"":id;
		md5 = intent.getStringExtra("md5");
		md5=md5==null?"":md5;
		kid = intent.getStringExtra("kid");
		kid=kid==null?"":kid;
		long sameaskcount = intent.getLongExtra("sameaskcount", 0);
		long answercount = intent.getLongExtra("answercount", 0);
		String description = intent.getStringExtra("description");
		description=description==null?"":description;
		String name = intent.getStringExtra("name");
		name=name==null?"":name;
		String date = intent.getStringExtra("date");
		date=date==null?"":date;
		sameAsk_count.setText(sameaskcount + "");
		answer_count.setText(answercount + "");
		author.setText(name);
		tv_title.setText(description);
		tv_date.setText(StringUtils.convertDate(date));
		sameAsk.setTag(id);
//		http.wendaDetail(token, md5, id, "");
		SrpQADetailRequest srpQADetail = new SrpQADetailRequest(HttpCommon.SRP_QA_DETAIL_REQUEST, this);
		srpQADetail.addParams(md5, id, "");
		mMainHttp.doRequest(srpQADetail);

		int state = helper.getState(id, "");
		if (state != -1)
			sameAsk.setTextColor(Color.parseColor("#FF8000"));
	}

	private void initAllViews() {
		qa_question = View.inflate(this, R.layout.qa_question, null);
		myanswer = (EditText) findViewById(R.id.et_qa_myanswer);
		send_answer = (Button) findViewById(R.id.btn_send_answer);
		sameAsk = (Button) qa_question.findViewById(R.id.btn_qa_sameAsk);
		sameAsk_count = (TextView) qa_question.findViewById(R.id.tv_qa_sameAsk_count);
		answer_count = (TextView) qa_question.findViewById(R.id.tv_qa_answer_count);
		tv_title = (TextView) qa_question.findViewById(R.id.tv_qa_title);
		tv_date = (TextView) qa_question.findViewById(R.id.tv_qa_date);
		author = (TextView) qa_question.findViewById(R.id.tv_qa_author);
		sameAsk.setOnClickListener(this);
		send_answer.setOnClickListener(this);

		// views.add(qa_question);
		lv_qa = (ListView) findViewById(R.id.lv_qa);
		lv_qa.addHeaderView(qa_question);
		lv_qa.setOnScrollListener(new MyOnScrollListener());
		adapter = new MyAdapter();
		lv_qa.setAdapter(adapter);
		((TextView)findViewById(R.id.activity_bar_title)).setText("有问必答");

	}

	public void wendaDetailSuccess(WendaDetail wendadetail) {
		List<Wenda> wendaList = wendadetail.wendaList();
		views.clear();
		// views.add(qa_question);
		// adapter.notifyDataSetChanged();
		holders.clear();
		if (wendaList.size() == 0) {
			findViewById(R.id.tv_no_answer).setVisibility(View.VISIBLE);
			// findViewById(R.id.lv_qa).setVisibility(View.GONE);
		} else {
			findViewById(R.id.tv_no_answer).setVisibility(View.GONE);
			findViewById(R.id.lv_qa).setVisibility(View.VISIBLE);
		}
		hasMore = wendadetail.hasMore();
		for (Wenda wenda : wendaList) {
			String content = wenda.content();
			String answerId = wenda.id();
			long date = wenda.date();
			int downCount = wenda.downCount();
			int upCount = wenda.upCount();
			User user = wenda.user();
			String imageUrl = user.image();
			String name = user.name();

			ViewHolder holder = createAnswerItem();
			//aq.id(holder.iv_face).image(imageUrl, true, true);
			PhotoUtils.showCard(PhotoUtils.UriType.HTTP,imageUrl,holder.iv_face);
			holder.tv_author.setText(name);
			holder.tv_date.setText(StringUtils.convertDate(date + ""));
			holder.content.setText(content);
			int upOrDown = helper.selectUpOrDown(this.id, answerId);
			if (upOrDown == 0)
				holder.ib_up.setImageResource(R.drawable.up_selected_1);
			else if (upOrDown == 1)
				holder.ib_down.setImageResource(R.drawable.down_selected_1);
			holder.ib_up.setTag(wenda);
			holder.ib_down.setTag(wenda);
			holder.tv_up.setText(upCount + "");
			holder.tv_down.setText(downCount + "");
			holder.answerId = answerId;// answerId
			// holder.answerItem.setTag(wenda);
			views.add(holder.answerItem);
			adapter.notifyDataSetChanged();
			holders.add(holder);
		}
	}

	private ViewHolder createAnswerItem() {
		ViewHolder viewHolder = new ViewHolder();
		View answer_item = View.inflate(this, R.layout.answers_item, null);
		TextView tv_author = (TextView) answer_item.findViewById(R.id.tv_qa_item_author);
		TextView tv_date = (TextView) answer_item.findViewById(R.id.tv_qa_item_date);
		TextView content = (TextView) answer_item.findViewById(R.id.tv_answer_content);
		ImageButton ib_up = (ImageButton) answer_item.findViewById(R.id.ib_up);
		TextView tv_up = (TextView) answer_item.findViewById(R.id.tv_up);
		ImageButton ib_down = (ImageButton) answer_item.findViewById(R.id.ib_down);
		TextView tv_down = (TextView) answer_item.findViewById(R.id.tv_down);
		ib_up.setOnClickListener(this);
		ib_down.setOnClickListener(this);
		viewHolder.answerItem = answer_item;
		viewHolder.tv_author = tv_author;
		viewHolder.tv_date = tv_date;
		viewHolder.content = content;
		viewHolder.ib_up = ib_up;
		viewHolder.tv_up = tv_up;
		viewHolder.ib_down = ib_down;
		viewHolder.tv_down = tv_down;
		return viewHolder;
	}

	private class ViewHolder {
		View answerItem;
		ImageView iv_face;
		TextView tv_author, tv_date, content, tv_up, tv_down;
		ImageButton ib_up, ib_down;
		String answerId;
	}

	@Override
	public void onHttpError(IRequest request) {
		changeButtonState(true);
		switch (request.getmId()) {
			case HttpCommon.SRP_QUESTION_REQUEST:
				SouYueToast.makeText(getApplicationContext(), R.string.answerFail, 0).show();
				break;
		}

	}
//	@Override
//	public void onHttpError(String methodName, AjaxStatus as) {
//		changeButtonState(true);
//		if ("wendaAnswer".equals(methodName)) {
//			SouYueToast.makeText(getApplicationContext(), R.string.answerFail, 0).show();
//		}
//		// views.clear();
//		// views.add(qa_question);
//		// adapter.notifyDataSetChanged();
//	}

	private class MyAdapter extends BaseAdapter {
		public int getCount() {
			return views.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			return views.get(position);
		}
	}

	// 禁用或启动所有按钮
	private void changeButtonState(boolean state) {
		for (int i = 0; i < holders.size(); i++) {
			ViewHolder viewHolder = holders.get(i);
			viewHolder.ib_up.setEnabled(state);
			viewHolder.ib_down.setEnabled(state);
		}
		sameAsk.setClickable(state);
	}

	@Override
	public void onClick(View v) {
		changeButtonState(false);
		Wenda wenda;
		boolean state = false;
		switch (v.getId()) {
			case R.id.ib_up:
				wenda = (Wenda) v.getTag();
				state = helper.setState(id, wenda.id(), 0, 0);
				if (state) {
//				http.wendaUp(token, md5, id, wenda.id());
					SrpQAUpRequest srpUpRequest = new SrpQAUpRequest(HttpCommon.SRP_QA_UP_REQUEST, this);
					srpUpRequest.addParams(md5, id, wenda.id());
					mMainHttp.doRequest(srpUpRequest);
				} else {
					changeButtonState(true);
					SouYueToast.makeText(getApplicationContext(), R.string.hasUpOrDown, 0).show();
				}
				break;
			case R.id.ib_down:
				wenda = (Wenda) v.getTag();
				state = helper.setState(id, wenda.id(), 0, 1);
				if (state) {
//				http.wendaDown(token, md5, id, wenda.id());
					SrpQADownRequest srpQADownRequest = new SrpQADownRequest(HttpCommon.SRP_QA_DOWN_REQUEST, this);
					srpQADownRequest.addParams(md5, id, wenda.id());
					mMainHttp.doRequest(srpQADownRequest);
				} else {
					changeButtonState(true);
					SouYueToast.makeText(getApplicationContext(), R.string.hasUpOrDown, 0).show();
				}
				break;
			case R.id.btn_qa_sameAsk:
				state = helper.setState(id, "", 0, -1);
				if (state) {
//				http.wendaSameAsk(token, md5, id, kid);
					SrpQASameRequest qaSameRequest = new SrpQASameRequest(HttpCommon.SRP_QA_SAME_REQUEST, this);
					qaSameRequest.addParams(md5, id, kid);
					mMainHttp.doRequest(qaSameRequest);
				} else {
					changeButtonState(true);
					SouYueToast.makeText(getApplicationContext(), R.string.hasAsked, 0).show();
				}
				break;
			case R.id.btn_send_answer:
				String answerString = myanswer.getText().toString();
				if (TextUtils.isEmpty(answerString)) {
					SouYueToast.makeText(getApplicationContext(), R.string.contentisnull, 0).show();
					return;
				}
				new SYInputMethodManager(this).hideSoftInput();
				send_answer.setClickable(false);
//			http.wendaAnswer(token, SYUserManager.getInstance().getName(), md5, id, kid, answerString);
				SrpAnswerRequest questionRequest = new SrpAnswerRequest(HttpCommon.SRP_QUESTION_REQUEST, this);
				questionRequest.addParams(SYUserManager.getInstance().getName(), md5, id, kid, answerString);
				mMainHttp.doRequest(questionRequest);
				break;
			default:
				break;
		}
	}

	public void wendaUpSuccess(WendaUpDown wendaupdown) {
		updownSuccess(wendaupdown);
		SouYueToast.makeText(getApplicationContext(), R.string.hasUp, 0).show();
	}

	public void wendaDownSuccess(WendaUpDown wendaupdown) {
		updownSuccess(wendaupdown);
		SouYueToast.makeText(getApplicationContext(), R.string.hasDown, 0).show();
	}

	private void updownSuccess(WendaUpDown wendaupdown) {
		changeButtonState(true);
		String questionId = wendaupdown.questionId();
		String answerId = wendaupdown.answerId();
		int upCount = wendaupdown.upCount();
		int downCount = wendaupdown.downCount();
		helper.setState(questionId, answerId, 1, -1);
		for (ViewHolder holder : holders) {
			if (answerId.equals(holder.answerId)) {
				holder.tv_up.setText("" + upCount);
				holder.tv_down.setText("" + downCount);
				int upOrDown = helper.selectUpOrDown(questionId, answerId);
				if (upOrDown == 0) {
					holder.ib_up.setImageResource(R.drawable.up_selected_1);
					holder.ib_down.setImageResource(R.drawable.down_normal_1);
				} else if (upOrDown == 1) {
					holder.ib_up.setImageResource(R.drawable.up_normal_1);
					holder.ib_down.setImageResource(R.drawable.down_selected_1);
				}
			}
		}
	}

	public void wendaSameAskSuccess(WendaSameAsk sameask) {
		changeButtonState(true);
		sameAsk.setTextColor(Color.parseColor("#FF8000"));
		String questionId = sameask.questionId();
		int sameAskCount = sameask.sameAskCount();
		sameAsk_count.setText("" + sameAskCount);
		helper.setState(questionId, "", 1, -1);
		SouYueToast.makeText(getApplicationContext(), "成功", 0).show();
	}

	/*
	 * souyue-5.0.9
	 * public void wendaAnswerSuccess(AjaxStatus as) {
		changeButtonState(true);
		http.wendaDetail(token, md5, id, "");
		send_answer.setClickable(true);
		SouYueToast.makeText(getApplicationContext(), R.string.answerSuccess, 0).show();
		myanswer.setText("");
	}*/

	@Override
	public void onHttpResponse(IRequest request) {
		HttpJsonResponse response = request.getResponse();
		switch (request.getmId()){
			case HttpCommon.SRP_QUESTION_REQUEST:
				changeButtonState(true);
//		      		http.wendaDetail(token, md5, id, "");
				SrpQADetailRequest srpQADetail = new SrpQADetailRequest(HttpCommon.SRP_QA_DETAIL_REQUEST, this);
				srpQADetail.addParams(md5, id, "");
				mMainHttp.doRequest(srpQADetail);

				send_answer.setClickable(true);
				SouYueToast.makeText(getApplicationContext(), R.string.answerSuccess, 0).show();
				myanswer.setText("");
				break;
			case HttpCommon.SRP_QA_DETAIL_REQUEST:
				wendaDetailSuccess(new WendaDetail(response));
				break;
			case HttpCommon.SRP_QA_SAME_REQUEST:
				wendaSameAskSuccess(new Gson().fromJson(response.getBody(), WendaSameAsk.class));
				break;
			case HttpCommon.SRP_QA_DOWN_REQUEST:
				wendaDownSuccess(new Gson().fromJson(response.getBody(), WendaUpDown.class));
				break;
			case HttpCommon.SRP_QA_UP_REQUEST:
				wendaUpSuccess(new Gson().fromJson(response.getBody(), WendaUpDown.class));
				break;
		}
	}

	private class MyOnScrollListener implements OnScrollListener {
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (!hasMore)
				return;
			switch (scrollState) {
				// 当不滚动时
				case OnScrollListener.SCROLL_STATE_IDLE:
					// 判断滚动到底部
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						ViewHolder viewHolder = holders.get(holders.size() - 1);
						String tmpId = viewHolder.answerId;
//					http.wendaDetail(token, md5, id, tmpId);
						SrpQADetailRequest srpQADetail = new SrpQADetailRequest(HttpCommon.SRP_QA_DETAIL_REQUEST, QADetailsActivity.this);
						srpQADetail.addParams(md5, id, tmpId);
						mMainHttp.doRequest(srpQADetail);
					}
					break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		}
	}

	@Override
	public void onBackPressed() {
		finish();
		this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}

}
