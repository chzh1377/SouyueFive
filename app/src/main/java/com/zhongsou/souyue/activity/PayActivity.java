//package com.zhongsou.souyue.activity;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.Display;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.zhongsou.souyue.R;
//import com.zhongsou.souyue.bases.RightSwipeActivity;
//import com.zhongsou.souyue.module.OrderInfo;
//import com.zhongsou.souyue.module.User;
//import com.zhongsou.souyue.net.HttpJsonResponse;
//import com.zhongsou.souyue.net.pay.EntZSCoinsPayRequest;
//import com.zhongsou.souyue.net.volley.HttpCommon;
//import com.zhongsou.souyue.net.volley.IRequest;
//import com.zhongsou.souyue.pay.AliPayment;
//import com.zhongsou.souyue.pay.IPayCallBack;
//import com.zhongsou.souyue.utils.SYUserManager;
//import com.zhongsou.souyue.utils.StringUtils;
//import com.zhongsou.souyue.utils.ToastUtil;
//
//import java.text.DecimalFormat;
//
///**
// * User: liyc
// * Date: 14-1-13
// * Time: 下午4:51
// */
//public class PayActivity extends RightSwipeActivity implements OnClickListener, IPayCallBack {
//
//    private TextView payMoneyTV;
//    private ImageView payTypeRadio_alipay_yes;
//    private ImageView payTypeRadio_alipay_no;
//    private Button payBtn;
//    private TextView titleBarTV;
//    private TextView paySuccessMsg;
//    private Button paySuccessDialogBtn;
//    private Dialog paySuccessDialog;
//
//    private boolean hasSelectedPayType = true, canPress = true;
//    private int t, b;
//    private double payMoney;
//    private User user;
//    private String payUrl;
//
//    public static int resultCode = 1;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay);
//        user = SYUserManager.getInstance().getUser();
//
//        initFromIntent();
//        initView();
//    }
//
//    /**
//     * 初始数据
//     */
//    private void initFromIntent() {
//        Intent i = this.getIntent();
//        t = i.getIntExtra(ZScoinListActivity.ZSCOINS_TYPE, 1);
//        b = i.getIntExtra(ZScoinListActivity.ZSCOINS_NUM, 1);
//        payMoney = i.getDoubleExtra(ZScoinListActivity.PAY_MONEY_NUM, 1);
//    }
//
//    private void initView() {
//        DecimalFormat df1 = new DecimalFormat("###,###.0");
//        titleBarTV = ((TextView) findViewById(R.id.activity_bar_title));
//        payMoneyTV = (TextView) findViewById(R.id.tv_pay_money);
//        titleBarTV.setText(getString(R.string.pay));
//        payMoneyTV.setText(df1.format(payMoney) + "元");
//
//        payTypeRadio_alipay_yes = (ImageView) findViewById(R.id.pay_type_radio_alipay_yes);
//        payTypeRadio_alipay_no = (ImageView) findViewById(R.id.pay_type_radio_alipay_no);
//        payBtn = (Button) findViewById(R.id.btn_pay);
//
//        payTypeRadio_alipay_yes.setOnClickListener(this);
//        payTypeRadio_alipay_no.setOnClickListener(this);
//        payBtn.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_pay:
//                if (hasSelectedPayType) {
//                    if (user != null && canPress) {
//                        canPress = false;
//                        this.getPayUrl();
////						this.showPaySuccessDialog();
//                    }
//                } else {
//                    ToastUtil.show(this, "请选择充值方式！");
//                }
//                break;
//            case R.id.pay_type_radio_alipay_yes:
//                payTypeRadio_alipay_yes.setVisibility(View.GONE);
//                payTypeRadio_alipay_no.setVisibility(View.VISIBLE);
//                hasSelectedPayType = false;
//                break;
//            case R.id.pay_type_radio_alipay_no:
//                payTypeRadio_alipay_yes.setVisibility(View.VISIBLE);
//                payTypeRadio_alipay_no.setVisibility(View.GONE);
//                hasSelectedPayType = true;
//                break;
//            case R.id.pay_success_dialog_btn:
//                hidePaySuccessDialog();
//                setResult(resultCode, new Intent());
//                finish();
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public void onHttpResponse(IRequest request) {
//        int id = request.getmId();
//        switch (id) {
//            case HttpCommon.ENT_ENTZSCOINSPAY_REQUESTID:
//                HttpJsonResponse response1 = request.<HttpJsonResponse>getResponse();
//                OrderInfo orderInfo = new Gson().fromJson(response1.getBody(), new TypeToken<OrderInfo>() {}.getType());
//                payUrl = orderInfo.getPayUrl();
//                if (StringUtils.isNotEmpty(payUrl)) {
//                    canPress = false;
//                    //启动支付宝app 把支付信息带给支付宝
//                    doPay(PayActivity.this, payUrl);
//                } else {
//                    ToastUtil.show(PayActivity.this, "支付信息有误，请重试！");
//                    canPress = true;
//                }
//                break;
//        }
//    }
//
//    @Override
//    public void onHttpError(IRequest request) {
//        int id = request.getmId();
//        switch (id) {
//            case HttpCommon.ENT_ENTZSCOINSPAY_REQUESTID:
//                ToastUtil.show(PayActivity.this, "网络错误，请重试！");
//                canPress = true;
//                break;
//        }
//    }
//
//    private void getPayUrl() {
//        EntZSCoinsPayRequest.send(HttpCommon.ENT_ENTZSCOINSPAY_REQUESTID, this, user, t, b, ChargeActivity.PAY_TYPE_ALIPAY);
////        HttpHelper.zscoinsPay(user, t, b, ChargeActivity.PAY_TYPE_ALIPAY, new ObjectHttpResponseHandler<OrderInfo>(OrderInfo.class) {
////            @Override
////            public void onSuccess(OrderInfo response) {
////
////                payUrl = response.getPayUrl();
////                if (StringUtils.isNotEmpty(payUrl)) {
////                    canPress = false;
////                    //启动支付宝app 把支付信息带给支付宝
////                    doPay(PayActivity.this, payUrl);
////                } else {
////                    ToastUtil.show(PayActivity.this, "支付信息有误，请重试！");
////                    canPress = true;
////                }
////            }
////
////            @Override
////            public void onSuccess(List<OrderInfo> response) {
////
////            }
////
////            @Override
////            public void onFailure(Throwable e, String msg) {
////                ToastUtil.show(PayActivity.this, "网络错误，请重试！");
////                canPress = true;
////            }
////        });
//    }
//
//    private void doPay(final Activity activity, String payUrl) {
//        AliPayment payment = new AliPayment();
//        payment.pay(payUrl, new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (msg.obj != null && msg.obj.toString().contains("9000")) {
//                    IPayCallBack payCallBack = (IPayCallBack) activity;
//                    payCallBack.paySuccess();
//                } else {
//                    canPress = true;
//                }
//            }
//        }, activity);
////        if (!result) {
////            canPress = true;
////        }
//    }
//
//    @Override
//    public void paySuccess() {
//        showPaySuccessDialog();
//    }
//
//    private void showPaySuccessDialog() {
//        paySuccessDialog = new Dialog(PayActivity.this, R.style.pay_success_dialog);
//
//        View dialogView = LayoutInflater.from(this).inflate(R.layout.pay_success_dialog, null);
//        paySuccessDialog.setContentView(dialogView);
//        paySuccessDialogBtn = (Button) dialogView.findViewById(R.id.pay_success_dialog_btn);
//        paySuccessMsg = (TextView) dialogView.findViewById(R.id.pay_success_msg);
//        paySuccessDialogBtn.setOnClickListener(this);
//        paySuccessMsg.setText("中搜币充值可能会有延迟，请耐心等待！");
//
//        WindowManager m = getWindowManager();
//        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
//        paySuccessDialog.getWindow().getAttributes().width = (int) (d.getWidth() * 0.65);
//        paySuccessDialog.show();
//        canPress = true;
//    }
//
//    private void hidePaySuccessDialog() {
//        if (paySuccessDialog != null) {
//            paySuccessDialog.dismiss();
//        }
//    }
//
//}
