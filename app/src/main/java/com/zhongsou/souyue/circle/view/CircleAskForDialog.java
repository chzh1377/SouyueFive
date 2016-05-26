package com.zhongsou.souyue.circle.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.net.circle.CircleApplyRequest;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.ChangeSelector;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.Utility;

/**
 * @author : zoulu 2014年7月15日 下午1:54:57 类说明 :兴趣圈名片dialog
 */
@SuppressLint("InflateParams")
public class CircleAskForDialog extends Dialog implements OnClickListener {
//	public static final int CIRCLEAPPLY_REQUESTID = 63415465;//请求加入圈子
	private final IVolleyResponse mResponse; //请求回来
	private TextView tv_title, leave_title, name_title,leave_content_tv,name_content_tv;
	private RelativeLayout circlename;
	private EditText leave_content, name_content;
	private Button btn1, btn2;
//	private Http http;
	private Context cx;
	private int type;
	private long interest_id;
	private String nickname;
	private String apply_content;
	private String refuse_content;

	/**
	 * 
	 * @param context
	 * @param type
	 *            根据type弹出三种不同样式的dialog. 1.审核中 2.申请圈子 3.申请被拒绝
	 */
	public CircleAskForDialog(Context context, IVolleyResponse response, int type, long interest_id, String nickname, String apply_content, String refuse_content) {
		super(context, R.style.Dialog_Fullscreen);
		this.mResponse = response;
		this.type = type;
		this.interest_id = interest_id;
		this.cx = context;
		this.nickname = nickname;
		this.apply_content = apply_content;
		this.refuse_content = refuse_content;
//		http = new Http(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getContentView(type);
	}

	private void getContentView(int type) {
		setContentView(R.layout.circle_ask_for_dialog);
		initView();
		setViewType(type);
	}

	@SuppressWarnings("deprecation")
	private void setViewType(int type) {
		switch (type) {
		case 1:
			btn1.setVisibility(View.GONE);
			btn2.setVisibility(View.VISIBLE);
			btn2.setTextColor(Color.GRAY);
			btn2.setText("关闭");
			btn2.setBackgroundDrawable(ChangeSelector.addStateDrawable(cx, R.drawable.verify, R.drawable.verify, R.drawable.verify));
			tv_title.setText("您已经提交申请，请等待圈主审批");
			leave_title.setText("给圈主留言:");
			name_title.setText("我将在圈里用的名称（圈昵称）：");
			leave_content.setMovementMethod(ScrollingMovementMethod.getInstance());
			name_content.setMovementMethod(ScrollingMovementMethod.getInstance());
			leave_content_tv.setText(apply_content);
            name_content_tv.setText(nickname);
			leave_content_tv.setVisibility(View.VISIBLE);
            name_content_tv.setVisibility(View.VISIBLE);
			leave_content.setVisibility(View.GONE);
            name_content.setVisibility(View.GONE);
			break;
		case 2:
			btn1.setVisibility(View.VISIBLE);
			btn2.setVisibility(View.VISIBLE);
			btn1.setTextColor(Color.WHITE);
			btn1.setText("申请加入");
            leave_content.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
			btn1.setBackgroundDrawable(ChangeSelector.addStateDrawable(cx, R.drawable.circleshow, R.drawable.verify, R.drawable.verify));
			btn2.setTextColor(Color.GRAY);
			btn2.setText("取消");
			btn2.setBackgroundDrawable(ChangeSelector.addStateDrawable(cx, R.drawable.verify, R.drawable.verify, R.drawable.verify));
			tv_title.setText("本圈为私密圈，请申请加入");
			leave_title.setText("给圈主留言:");
			name_title.setText("我将在圈里用的名称（圈昵称）：");
			leave_content_tv.setVisibility(View.GONE);
			leave_content.setVisibility(View.VISIBLE);
			break;
		case 3:
			btn1.setVisibility(View.VISIBLE);
			btn2.setVisibility(View.VISIBLE);
			btn1.setTextColor(Color.WHITE);
			btn1.setText("重新申请");
			btn1.setBackgroundDrawable(ChangeSelector.addStateDrawable(cx, R.drawable.circleshow, R.drawable.verify, R.drawable.verify));
			btn1.setTextColor(Color.WHITE);
			btn2.setBackgroundDrawable(ChangeSelector.addStateDrawable(cx, R.drawable.verify, R.drawable.verify, R.drawable.verify));
			btn2.setTextColor(Color.GRAY);
			btn2.setText("关闭");
			circlename.setVisibility(View.GONE);
			tv_title.setText("很遗憾！您的申请已被圈主拒绝");
			leave_title.setText("拒绝原因：");
			leave_content_tv.setText(refuse_content);
			leave_content_tv.setVisibility(View.VISIBLE);
			leave_content.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		tv_title = (TextView) findViewById(R.id.tv_title);
		leave_title = (TextView) findViewById(R.id.leave_title);
		name_title = (TextView) findViewById(R.id.name_title);
		circlename = (RelativeLayout) findViewById(R.id.circlename);
		leave_content = (EditText) findViewById(R.id.leave_content);
		name_content = (EditText) findViewById(R.id.name_content);
		leave_content_tv = (TextView) findViewById(R.id.leave_content_tv);
        name_content_tv = (TextView)findViewById(R.id.name_content_tv);
		leave_content_tv.setVisibility(View.GONE);
        name_content_tv.setVisibility(View.GONE);
		btn1 = (Button) findViewById(R.id.btn1);
		btn2 = (Button) findViewById(R.id.btn2);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
	}

	public void showDialog() {
		Window window = this.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;// 改变显示位置
        this.onWindowAttributesChanged(lp);
        this.show();
        if(type == 1 || type == 3){
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn1:
			switch (type) {
			case 2:
				if(Utility.getStrLength(leave_content.getText().toString()) > 140){
					UIHelper.ToastMessage(cx, "留言最多可输入140个字符(70个汉字)");
					return;
				}
				if(name_content.getText().toString().length() != 0){
					if(Utility.getStrLength(name_content.getText().toString()) > 20 || Utility.getStrLength(name_content.getText().toString()) < 4){
						UIHelper.ToastMessage(cx, "昵称限制4到20个字符(4到10个汉字)");
						return;
					}
				}
//				http.applyForSecretCircle(interest_id, SYUserManager.getInstance().getToken(), leave_content.getText().toString(), name_content.getText().toString());
				CircleApplyRequest.sendRequest(HttpCommon.CIRCLE_APPLY_REQUEST,mResponse,interest_id, SYUserManager.getInstance().getToken(), leave_content.getText().toString(), name_content.getText().toString());
				dismiss();
				SYSharedPreferences.getInstance().setReplay(true);
				break;
			case 3:
				getContentView(2);
				type = 2;
				showDialog();
				break;

			default:
				break;
			}
			break;
		case R.id.btn2:
			dismiss();
			break;

		default:
			break;
		}
	}

}
