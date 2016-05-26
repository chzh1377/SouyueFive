//package com.zhongsou.souyue.activity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import com.zhongsou.souyue.R;
//import com.zhongsou.souyue.bases.RightSwipeActivity;
//
///**
// * User: liyc
// * Date: 14-1-13
// * Time: 下午4:40
// */
//public class ZScoinListActivity extends RightSwipeActivity implements OnClickListener {
//
//	public static String PAY_MONEY_NUM = "money";
//	public static String ZSCOINS_TYPE = "t";
//	public static String ZSCOINS_NUM = "b";
//	private LinearLayout zscoins_1, zscoins_2, zscoins_3, zscoins_4, zscoins_5, zscoins_6, zscoins_7,zscoins_8,zscoins_9,zscoins_10,zscoins_11;
//	private int requestCode = 10;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.zscoins_list);
//		((TextView)findViewById(R.id.activity_bar_title)).setText(getString(R.string.pay));
//
//
//		zscoins_1 = (LinearLayout) findViewById(R.id.zscoins_1);
//		zscoins_2 = (LinearLayout) findViewById(R.id.zscoins_2);
//		zscoins_3 = (LinearLayout) findViewById(R.id.zscoins_3);
//		zscoins_4 = (LinearLayout) findViewById(R.id.zscoins_4);
//		zscoins_5 = (LinearLayout) findViewById(R.id.zscoins_5);
//        zscoins_6 = (LinearLayout) findViewById(R.id.zscoins_6);
//        zscoins_7 = (LinearLayout) findViewById(R.id.zscoins_7);
//        zscoins_8 = (LinearLayout) findViewById(R.id.zscoins_8);
//        zscoins_9 = (LinearLayout) findViewById(R.id.zscoins_9);
//        zscoins_10 = (LinearLayout) findViewById(R.id.zscoins_10);
//        zscoins_11 = (LinearLayout) findViewById(R.id.zscoins_11);
//
//		zscoins_1.setOnClickListener(this);
//		zscoins_2.setOnClickListener(this);
//		zscoins_3.setOnClickListener(this);
//		zscoins_4.setOnClickListener(this);
//		zscoins_5.setOnClickListener(this);
//        zscoins_6.setOnClickListener(this);
//        zscoins_7.setOnClickListener(this);
//        zscoins_8.setOnClickListener(this);
//        zscoins_9.setOnClickListener(this);
//        zscoins_10.setOnClickListener(this);
//        zscoins_11.setOnClickListener(this);
//	}
//
//	@Override
//	public void onClick(View v) {
//		double pay_money = 1;
//		int t = 1, b = 10;
//		switch (v.getId()) {
//			case R.id.zscoins_1:
//				t = 1;
//				b = 10;
//				pay_money = 1;
//				break;
//			case R.id.zscoins_2:
//				t = 3;
//				b = 100;
//				pay_money = 10;
//				break;
//			case R.id.zscoins_3:
//				t = 5;
//				b = 200;
//				pay_money = 20;
//				break;
//			case R.id.zscoins_4:
//				t = 6;
//				b = 500;
//				pay_money = 50;
//				break;
//			case R.id.zscoins_5:
//				t = 7;
//				b = 1000;
//				pay_money = 100;
//				break;
//            case R.id.zscoins_6:
//                t = 1004;
//                b = 2000;
//                pay_money = 200;
//                break;
//            case R.id.zscoins_7:
//                t = 1004;
//                b = 5000;
//                pay_money = 500;
//                break;
//            case R.id.zscoins_8:
//                t = 1004;
//                b = 10000;
//                pay_money = 1000;
//                break;
//            case R.id.zscoins_9:
//                t = 1004;
//                b = 100000;
//                pay_money = 10000;
//                break;
//            case R.id.zscoins_10:
//                t = 1004;
//                b = 1000000;
//                pay_money = 100000;
//                break;
//            case R.id.zscoins_11:
//                t = 1004;
//                b = 10000000;
//                pay_money = 1000000;
//                break;
//			default:
//				break;
//		}
//		gotoPayActive(t, b, pay_money);
//	}
//
//	private void gotoPayActive(int t, int b, double pay_money){
//		Intent intent = new Intent(this, PayActivity.class);
//		intent.putExtra(ZSCOINS_TYPE, t);
//		intent.putExtra(ZSCOINS_NUM, b);
//		intent.putExtra(PAY_MONEY_NUM, pay_money);
//		startActivityForResult(intent, requestCode);
//		overridePendingTransition(R.anim.left_in, R.anim.left_out);
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data){
//		//可以根据多个请求代码来作相应的操作
//		if(resultCode == PayActivity.resultCode){
//			finish();
//		}
//		super.onActivityResult(requestCode, resultCode, data);
//	}
//
//}
