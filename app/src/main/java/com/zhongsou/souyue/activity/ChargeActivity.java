package com.zhongsou.souyue.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unionpay.UPPayAssistEx;
import com.zhongsou.cn.sdk.pay.HdbpayConfig;
import com.zhongsou.cn.sdk.pay.HdbpayCore;
import com.zhongsou.cn.sdk.pay.ZSPay;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.view.RadioGroup;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.JiFen;
import com.zhongsou.souyue.module.MyPoints;
import com.zhongsou.souyue.module.OrderInfo;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.pay.EntGetMobileNoRequest;
import com.zhongsou.souyue.net.pay.EntZSCoinsPayRequest;
import com.zhongsou.souyue.net.personal.UserIntegral;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.pay.AliPayment;
import com.zhongsou.souyue.pay.Constant;
import com.zhongsou.souyue.pay.PayResult;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.ToastUtil;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.zhongsou.cn.sdk.pay.ZSPay;

//import com.unionpay.UPPayAssistEx;

/**
 * 中搜币充值界面
 * <p/>
 * Created by bob zhou on 14-10-10.
 */
public class ChargeActivity extends BaseActivity implements View.OnClickListener {

    private ProgressBarHelper progress;

    private User user;

    private double payMoney;

    private int chargeCount = 100;                  //充值的中搜币的个数,默认100

    private int type = 3;                               //chargeCount =100 ：type = 3， 1000 : 7, 1万 : 1004, 10万 ：1004 其他1004

    public static final int PAY_TYPE_ALIPAY = 1;    //支付宝

    public static final int PAY_TYPE_YINLIAN = 2;   //银联支付

//    public static final int PAY_TYPE_ZZ = 3;

    public static final int PAY_TYPE_HDB = 4;  //惠多宝支付

    private int payType = PAY_TYPE_HDB;         //默认惠多宝支付

    private RadioButton radioBtn_100;
    private RadioButton radioBtn_1000;
    private RadioButton radioBtn_10000;
    private RadioButton radioBtn_100000;

    private RadioGroup typeRadioGroup;

    private EditText chargeCountText;

    private ImageView avatarIv;

    private TextView nicknameTv;

    private TextView zsbTv;

    private TextView phoneTipsTv;

    private TextView phoneTv;

    private TextView payMoneyTV;

    private Button payBtn;

    private boolean canPress = true;

    private String paymentVouchers; //支付凭证:支付宝为payUrl,银联为tn

    private Dialog paySuccessDialog;

    public static int resultCode = 1;

    private DecimalFormat df;

    private boolean isSelected;

    private RadioButton checkedButton;

    private LinearLayout rootLayout;

    private LinearLayout rootLayout1;

    public static final String ACCOUNT_STATE_NORMAL = "1";

    public static final String TAG = "ChargeActivity";

    private static final String HDB_RECEIVER_ACTION = "com.zhongsou.cn.pay";//惠多宝支付成功广播action

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charge);
        findViews();
        initData();
        bindListener();
    }

    private void findViews() {
        progress = new ProgressBarHelper(this, findViewById(R.id.ll_data_loading));
        ((TextView) findViewById(R.id.activity_bar_title)).setText("中搜币充值");
        avatarIv = (ImageView) findViewById(R.id.charge_avatar);
        nicknameTv = (TextView) findViewById(R.id.charge_nickname_tv);
        zsbTv = (TextView) findViewById(R.id.charge_zsb_tv);
        phoneTipsTv = (TextView) findViewById(R.id.charge_phone_tips_tv);
        phoneTv = (TextView) findViewById(R.id.charge_phone_tv);
        payMoneyTV = (TextView) findViewById(R.id.pay_count_yuan);
        radioBtn_100 = (RadioButton) findViewById(R.id.radio_count_100);
        radioBtn_1000 = (RadioButton) findViewById(R.id.radio_count_1000);
        radioBtn_10000 = (RadioButton) findViewById(R.id.radio_count_10000);
        radioBtn_100000 = (RadioButton) findViewById(R.id.radio_count_100000);
        checkedButton = radioBtn_100;
        typeRadioGroup = (RadioGroup) findViewById(R.id.pay_type_radio_group);
        chargeCountText = (EditText) findViewById(R.id.charge_count_text);
        chargeCountText.clearFocus();
        payBtn = (Button) findViewById(R.id.pay_btn);
        rootLayout = (LinearLayout) findViewById(R.id.layout_root);
        rootLayout1 = (LinearLayout) findViewById(R.id.layout_root1);

        df = new DecimalFormat("###,##0.0");
    }

    private void initData() {
        user = SYUserManager.getInstance().getUser();
//        http = new Http(this);
//        AQuery aq = new AQuery(this);
        if (user != null) {
            // aq.id(avatarIv).image(user.image(), true, true);
            PhotoUtils.showCard(PhotoUtils.UriType.HTTP, user.image(), avatarIv, MyDisplayImageOption.homeTitle);

            nicknameTv.setText(user.name());
            getMobileNo();
            UserIntegral inte = new UserIntegral(HttpCommon.USER_INTERNAL_REQUEST, this);
            inte.setParams(user.userName());
            mMainHttp.doRequest(inte);
//            http.integral(user.userName());
        }
    }

    private void getMobileNo() {
//        HttpHelper.getMobileNo(user.userId(), new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, JSONObject response) {
//                super.onSuccess(statusCode, response);
////                if (response != null && response.size() != 0) {
////                    phone = response.getString("mobile");
////                    if (StringUtils.isNotEmpty(phone)) {
////                        phoneTv.setText(phone);
////                    } else {
////                        phoneTipsTv.setText("您尚未绑定手机号!");
////                    }
////                }
//            }
//
//            @Override
//            public void onFailure(Throwable e, JSONObject errorResponse) {
//                super.onFailure(e, errorResponse);
//                Toast.makeText(ChargeActivity.this, "获取绑定手机号失败", Toast.LENGTH_SHORT).show();
//            }
//        });
        EntGetMobileNoRequest.send(HttpCommon.ENT_GETMIBLIENO_REQUESTID, this, user.userId());
    }

    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()) {
            case HttpCommon.USER_INTERNAL_REQUEST:
                HttpJsonResponse response = request.getResponse();
                MyPoints points = new Gson().fromJson(response.getBody(),
                        new TypeToken<MyPoints>() {
                        }.getType());
                integralSuccess(points);
                User user = SYUserManager.getInstance().getUser();
                if (user == null) {
                    user = new User();
                }
                user.user_level_$eq(response.getBody().get("userlevel")
                        .getAsString());
                user.user_level_title_$eq(response.getBody().get("userleveltitle")
                        .getAsString());
                user.user_level_time_$eq(String.valueOf(System.currentTimeMillis()));
                SYUserManager.getInstance().setUser(user);
                break;
            case HttpCommon.ENT_GETMIBLIENO_REQUESTID: // 获取用户手机号的接口
                HttpJsonResponse json = request.<HttpJsonResponse>getResponse();
                if (json != null && !json.getBody().isJsonNull()) {
//                    phone = json.getString("mobile");
                    String phone = json.getBody().get("mobile").getAsString();
                    if (StringUtils.isNotEmpty(phone)) {
                        phoneTv.setText(phone);
                    } else {
                        phoneTipsTv.setText("您尚未绑定手机号!");
                    }
                }
                break;
            case HttpCommon.ENT_ENTZSCOINSPAY_REQUESTID: // 充值中搜币
                HttpJsonResponse response1 = request.<HttpJsonResponse>getResponse();
                OrderInfo orderInfo = new Gson().fromJson(response1.getBody(), new TypeToken<OrderInfo>() {
                }.getType());
                if (orderInfo != null) {
                    if (payType == PAY_TYPE_ALIPAY || payType == PAY_TYPE_YINLIAN) {//支付宝、银联
                        paymentVouchers = (payType == PAY_TYPE_ALIPAY) ? orderInfo.getPayUrl() : orderInfo.getTn();
                        if (StringUtils.isNotEmpty(paymentVouchers)) {
                            canPress = false;
                            doPay(paymentVouchers, orderInfo.getMode());
                        } else {
                            ToastUtil.show(ChargeActivity.this, "支付信息有误，请重试！");
                            canPress = true;
                        }
                    } else {//惠多宝
                        if (!StringUtils.isEmpty(orderInfo.getSeller_id()) && !StringUtils.isEmpty(orderInfo.getOut_trade_no())) {
                            canPress = false;
                            doPayOnlyHdb(orderInfo);
                            canPress = true;
                        } else {
                            ToastUtil.show(ChargeActivity.this, "支付信息有误，请重试！");
                            canPress = true;
                        }
                    }
                }
                break;
        }
    }

    public void integralSuccess(MyPoints points) {
        progress.goneLoading();
        if (points != null) {
            String state = points.getState();
            if (ACCOUNT_STATE_NORMAL.equals(state)) {
                List<JiFen> list = points.getScore();
                for (JiFen jf : list) {
                    if (jf.isZSB()) {
                        zsbTv.setText(jf.getNum() + "");
                        break;
                    }
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChargeActivity.this);
                builder.setMessage("该账号被冻结，请电话联系客服(4006506913)");
                builder.setTitle("提示消息");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.create().show();
            }
        }
    }

    private void bindListener() {
        radioBtn_100.setOnClickListener(this);
        radioBtn_1000.setOnClickListener(this);
        radioBtn_10000.setOnClickListener(this);
        radioBtn_100000.setOnClickListener(this);
        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(chargeCountText.getWindowToken(), 0);
                chargeCountText.clearFocus();
            }
        });

        rootLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chargeCountText.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                View focusView = ChargeActivity.this.getCurrentFocus();
                if (focusView != null) {
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                }
            }
        });

        typeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.pay_type_yinlian:
                        payType = PAY_TYPE_YINLIAN;
                        break;
                    case R.id.pay_type_alipay:
                        payType = PAY_TYPE_ALIPAY;
                        break;
                    case R.id.pay_type_hdb://惠多宝
                        payType = PAY_TYPE_HDB;
                        break;
                    default:
                        payType = PAY_TYPE_HDB;
                }

                Log.d(TAG, "payType=" + payType);
            }
        });

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCount();
                setType();
                if (chargeCount == 0 || !canPress) {
                    return;
                }
                canPress = false;
                doPay();
            }
        });

        chargeCountText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String countStr = charSequence.toString();
                if (StringUtils.isNotEmpty(countStr)) {
                    int count = Integer.parseInt(countStr);
                    if (count > 0) {
                        payMoney = count * 0.1;
                        payMoneyTV.setText(df.format(payMoney) + "元");
                        payBtn.setBackgroundResource(R.drawable.my_button_selector);
                    } else {
                        payMoneyTV.setText("0元");
                        payBtn.setBackgroundResource(R.drawable.charge_btn_gray);
                    }
                } else {
                    chargeCountText.removeTextChangedListener(this);
                    payMoneyTV.setText("0元");
                    payBtn.setBackgroundResource(R.drawable.charge_btn_gray);
                    chargeCountText.setText("0");
                    chargeCountText.setSelection(1);
                    chargeCountText.addTextChangedListener(this);
                }

                if (StringUtils.isNotEmpty(countStr) && !"0".equals(countStr)) {
                    subStr(countStr, this);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!isSelected && checkedButton != null) {
                    checkedButton.setChecked(false);
                }
                isSelected = false;
            }
        });


        progress.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
            @Override
            public void clickRefresh() {
                UserIntegral inte = new UserIntegral(HttpCommon.USER_INTERNAL_REQUEST, ChargeActivity.this);
                inte.setParams(user.userName());
                mMainHttp.doRequest(inte);
//                http.integral(user.userName());
            }
        });
    }

    private String subStr(String str, TextWatcher watcher) {
        boolean is = false;
        while ((str.length() - 1) > 0) {
            String startStr = str.substring(0, 1);
            if (startStr.equals("0")) {
                str = str.substring(1);
                is = true;
            } else {
                break;
            }
        }
        if (is) {
            chargeCountText.removeTextChangedListener(watcher);
            chargeCountText.setText(str);
            if (str.length() == 1) {
                chargeCountText.setSelection(1);
            }
            chargeCountText.addTextChangedListener(watcher);
        }
        return str;
    }


    private void setCount() {
        String countStr = chargeCountText.getText().toString();
        if (StringUtils.isNotEmpty(countStr)) {
            chargeCount = Integer.parseInt(countStr);
        } else {
            chargeCount = 0;
        }
    }

    private void setType() {
        switch (chargeCount) {
            case 10:
                type = 1;
                break;
            case 100:
                type = 3;
                break;
            case 200:
                type = 5;
                break;
            case 500:
                type = 6;
                break;
            case 1000:
                type = 7;
                break;
            default:
                type = 1004;
        }
    }

    private void doPay() {
        EntZSCoinsPayRequest.send(HttpCommon.ENT_ENTZSCOINSPAY_REQUESTID, this, user, type, chargeCount, payType);
//        HttpHelper.zscoinsPay(user, type, chargeCount, payType, new ObjectHttpResponseHandler<OrderInfo>(OrderInfo.class) {
//            @Override
//            public void onSuccess(OrderInfo orderInfo) {
//                if (orderInfo != null) {
//                    if (payType == PAY_TYPE_ALIPAY || payType == PAY_TYPE_YINLIAN) {//支付宝、银联
//
//                        paymentVouchers = (payType == PAY_TYPE_ALIPAY) ? orderInfo.getPayUrl() : orderInfo.getTn();
//
//                        if (StringUtils.isNotEmpty(paymentVouchers)) {
//                            canPress = false;
//                            doPay(ChargeActivity.this, paymentVouchers, orderInfo.getMode());
//                        } else {
//                            ToastUtil.show(ChargeActivity.this, "支付信息有误，请重试！");
//                            canPress = true;
//                        }
//                    } else {//惠多宝
//                        if (!StringUtils.isEmpty(orderInfo.getSeller_id()) && !StringUtils.isEmpty(orderInfo.getOut_trade_no())) {
//                            canPress = false;
//                            doPayOnlyHdb(ChargeActivity.this, orderInfo);
//                            canPress = true;
//                        } else {
//                            ToastUtil.show(ChargeActivity.this, "支付信息有误，请重试！");
//                            canPress = true;
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onSuccess(List<OrderInfo> response) {
//
//            }
//
//            @Override
//            public void onFailure(Throwable e, String msg) {
//                ToastUtil.show(ChargeActivity.this, "服务器内部错误，请重试！");
//                canPress = true;
//            }
//        });
    }

    private void doPay(final String paymentVouchers, String mode) {
        if (payType == PAY_TYPE_ALIPAY) {
            AliPayment payment = new AliPayment();
//            boolean result = payment.pay(paymentVouchers, new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    super.handleMessage(msg);
//                    if (msg.obj != null && msg.obj.toString().contains("9000")) {
//                        IPayCallBack payCallBack = (IPayCallBack) activity;
//                        payCallBack.paySuccess();
//                    } else {
//                        canPress = true;
//                    }
//                }
//            }, activity);
//            if (!result) {
//                canPress = true;
//            }
            payment.payByAli(this, paymentVouchers, mHandler);  //调用支付宝支付 - YanBin
        } else if (payType == PAY_TYPE_YINLIAN) {
            UPPayAssistEx.startPayByJAR(this, com.unionpay.uppay.PayActivity.class, null, null, paymentVouchers.trim(), mode);
            canPress = true;
        }
    }

    /**
     * 惠多宝支付
     *
     * @param orderInfo 订单信息
     */
    private void doPayOnlyHdb(OrderInfo orderInfo) {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("partner", "2068000006");
        paramMap.put("seller_id", orderInfo.getSeller_id());
        paramMap.put("out_trade_no", orderInfo.getOut_trade_no());
        paramMap.put("subject", orderInfo.getSubject());
        paramMap.put("body", orderInfo.getBody());
        paramMap.put("total_fee", orderInfo.getTotal_fee());
        paramMap.put("notify_url", orderInfo.getNotify_url());
        paramMap.put("return_url", orderInfo.getReturn_url());
        orderInfo.setAppScheme(HDB_RECEIVER_ACTION);
        paramMap.put("appScheme", orderInfo.getAppScheme());
        paramMap.put("orderSpec", orderInfo.getOrderSpec());
        paramMap.put("_input_charset", HdbpayConfig.input_charset);
        paramMap.put("it_b_pay", orderInfo.getIt_b_pay());
        paramMap.put("payment_type", orderInfo.getPayment_type());
        paramMap.put("app_id", getPackageName());
        paramMap.put("mer_cust_id", user.userName() == null ? "" : user.userName());

        //去重
        Map<String, String> map = HdbpayCore.paraFilter(paramMap);
        //签名
        map = HdbpayCore.buildRequestPara(map, HdbpayConfig.private_key);
        //拼接字符串
        String parameters = HdbpayCore.createLinkString(map);

        //使用的是  import com.zhongsou.cn.sdk.pay.ZSPay;
        ZSPay.pay(this, parameters, orderInfo.getAppScheme());
    }

    /**
     * AliPay success
     */
    private void showPaySuccessDialog() {
        paySuccessDialog = new Dialog(ChargeActivity.this, R.style.pay_success_dialog);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.pay_success_dialog, null);
        paySuccessDialog.setContentView(dialogView);
        Button paySuccessDialogBtn = (Button) dialogView.findViewById(R.id.pay_success_dialog_btn);
        TextView paySuccessMsg = (TextView) dialogView.findViewById(R.id.pay_success_msg);
        paySuccessDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidePaySuccessDialog();
            }
        });
        paySuccessMsg.setText("中搜币充值可能会有延迟，请耐心等待！");

        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        paySuccessDialog.getWindow().getAttributes().width = (int) (d.getWidth() * 0.65);
        paySuccessDialog.show();
        canPress = true;
    }

    private void hidePaySuccessDialog() {
        if (paySuccessDialog != null) {
            paySuccessDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*************************************************
         * 步骤3：处理银联手机支付控件返回的支付结果
         ************************************************/
        if (data == null) {
            return;
        }
//        String msg = "";
//        String str = data.getExtras().getString("pay_result");
//        if (str.equalsIgnoreCase("success")) {
//            showPaySuccessDialog();
//            return;
//        }
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            showPaySuccessDialog();
            return;
        }
//        else if( str.equalsIgnoreCase(R_FAIL) ){
//            showResultDialog(" 支付失败！ ");
//        }else if( str.equalsIgnoreCase(R_CANCEL) ){
//            showResultDialog(" 你已取消了本次订单的支付！ ");
//        }

        canPress = true;
    }

    @Override
    public void onClick(View view) {
        radioBtn_100.setChecked(false);
        radioBtn_1000.setChecked(false);
        radioBtn_10000.setChecked(false);
        radioBtn_100000.setChecked(false);
        String enumCount;
        checkedButton = (RadioButton) view;
        checkedButton.setChecked(true);
        switch (view.getId()) {
            case R.id.radio_count_100:
                enumCount = "100";
                break;
            case R.id.radio_count_1000:
                enumCount = "1000";
                break;
            case R.id.radio_count_10000:
                enumCount = "10000";
                break;
            case R.id.radio_count_100000:
                enumCount = "100000";
                break;
            default:
                enumCount = "100";
        }
        isSelected = true;
        chargeCountText.setText(enumCount);
        chargeCountText.setSelection(enumCount.length());
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootLayout.getWindowToken(), 0);
        chargeCountText.clearFocus();
    }

    @Override
    public void onHttpError(IRequest request) {
        switch (request.getmId()) {
            case HttpCommon.USER_INTERNAL_REQUEST:
                progress.showNetError();
                break;
            case HttpCommon.ENT_GETMIBLIENO_REQUESTID: // 获取用户手机号的接口
                Toast.makeText(ChargeActivity.this, "获取绑定手机号失败", Toast.LENGTH_SHORT).show();
                break;
            case HttpCommon.ENT_ENTZSCOINSPAY_REQUESTID:
                ToastUtil.show(ChargeActivity.this, "服务器内部错误，请重试！");
                canPress = true;
                break;
        }
    }

    /**
     * 支付宝支付回调
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        canPress = false;   //成功后，设置支付按钮不可点击
//                        Toast.makeText(ChargeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        showPaySuccessDialog();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(ChargeActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            canPress = true;   //成功后，设置支付按钮可以点击
                            Toast.makeText(ChargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                case Constant.SDK_CHECK_FLAG: {  //查询终端设备是否存在支付宝认证账户
                    Toast.makeText(ChargeActivity.this, "检查结果为：" + msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
            return false;
        }
    });

//    private static class AliPayHandler extends Handler{
//        WeakReference<Activity> mWeakReferenceActivity;       //弱引用
//
//        public AliPayHandler(Activity activity){
//            this.mWeakReferenceActivity = new WeakReference<Activity>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//
//        }
//    }
}