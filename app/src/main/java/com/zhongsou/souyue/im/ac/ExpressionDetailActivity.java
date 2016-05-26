package com.zhongsou.souyue.im.ac;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.adapter.IMExpressionDetaiAdapter;
import com.zhongsou.souyue.im.download.Dao;
import com.zhongsou.souyue.im.download.DownLoadService;
import com.zhongsou.souyue.im.download.LoadInfo;
import com.zhongsou.souyue.im.download.MemoryPackageDao;
import com.zhongsou.souyue.im.module.ExpressionDetailBean;
import com.zhongsou.souyue.im.module.PackageBean;
import com.zhongsou.souyue.im.module.ThumbnailBean;
import com.zhongsou.souyue.im.util.Constants;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;
import com.zhongsou.souyue.im.util.Slog;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.im.IMExpressionDetailRequest;
import com.zhongsou.souyue.net.volley.CIMExpressionHttp;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.ProgressBarHelper.ProgressBarClickListener;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.List;

/**
 * 表情详情页
 *
 * @author wangqiang 15/4/17
 */
public class ExpressionDetailActivity extends IMBaseActivity implements
        OnClickListener {

    private View mBack, mSetting;
    private ImageView mIvprotarit;
    private String fileName = "expression"; // 文件名
    private GridView mGridView;
    private List<ThumbnailBean> mThumbnails;
    private IMExpressionDetaiAdapter mAdapter;
    private CIMExpressionHttp http;
    private String packageId;
    private String packageName;
    private ExpressionDetailBean mDetail;
    private TextView mTvpacketname;
    private PackageBean packageBean;
    private TextView mTvDes, mTvSize;
    private Dao dao;

    public ImageView ivStatus;
    public TextView tvPrice;
    public TextView tvFree;
    public ImageView ivHasdown;
    public TextView tvPro;// 进度size
    public ProgressBar proDown;// 进度条
    public ImageView ivStop;// 暂停
    public ImageView ivDowned;

    public RelativeLayout rlPay; // 非下载状态
    public RelativeLayout rlFree;
    public RelativeLayout rlHasdown; // 以前下载过
    public LinearLayout llProgress; // 进度条
    public RelativeLayout rlDowned;
    private TextView title;
    protected ProgressBarHelper progress;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.im_expressiondetail_activity, null);
        setContentView(view);
        findViews();
        setListener();
        setStatus();
        doInitProgressBar(view);
        loadData();
        receiver = new UpdateBroadCastReceiver(this);
        receiver.registerAction("updateUI");
        receiver.registerAction(Constants.FAIL_ACTION);
    }

    private void doInitProgressBar(View view) {
        progress = new ProgressBarHelper(this,
                view.findViewById(R.id.ll_data_loading));
        progress.setProgressBarClickListener(new ProgressBarClickListener() {
            @Override
            public void clickRefresh() {

                loadData();

            }
        });
    }

    private void showNetError() {
        if (progress != null)
            progress.showNetError();
    }

    private void showNoData() {
        if (progress != null)
            progress.showNoData();
    }

    private void goneLoad() {
        if (progress != null)
            progress.goneLoading();
    }

    private UpdateBroadCastReceiver receiver;

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    public void setStatus() {
        // 设置状态
        boolean isExist = dao.hasPackage(packageBean.getPackageId());
        boolean isDowning = MemoryPackageDao.isHasInfo(packageBean
                .getPackageId());

        if (!isExist && packageBean.getIsDownloaded() == 1 && !isDowning) { // 云
            showSpecialBtn(rlHasdown);
            return;
        }

        if (isDowning) { // 正在下载中
            if (!llProgress.isShown())
                showSpecialBtn(llProgress);
            LoadInfo info = MemoryPackageDao.getInfos(packageBean
                    .getPackageId());
            if (info != null) {
                int totalsize = info.getFileSize();
                int completeSize = info.getComplete();
                Slog.d("callback", "下载了－－－－－－－－－" + completeSize);
                int result = (completeSize * 100) / totalsize;
                proDown.setMax(100);
                proDown.setProgress(result);
                tvPro.setText(result + "%");
                if (result == 100) {
                    showSpecialBtn(rlDowned);
                }
            }
            return;
        }

        if (isExist) { // 已下载
            showSpecialBtn(rlDowned);
            return;
        }

        if (!isExist && "0".equals(packageBean.getPrice())
                && packageBean.getIsDownloaded() == 0) { // 免费
            showSpecialBtn(rlFree);
            return;
        }
        if (!isExist && !"0".equals(packageBean.getPrice())
                && packageBean.getIsDownloaded() == 0) { // 收费
            showSpecialBtn(rlPay);
            return;
        }

    }

    private void showSpecialBtn(View showBtnArea) {
        rlPay.setVisibility(View.GONE);
        rlFree.setVisibility(View.GONE);
        rlHasdown.setVisibility(View.GONE);
        llProgress.setVisibility(View.GONE);
        rlDowned.setVisibility(View.GONE);
        showBtnArea.setVisibility(View.VISIBLE);
    }

    public void findViews() {
        dao = new Dao(this);
        mBack = this.findViewById(R.id.back);
        mSetting = this.findViewById(R.id.setting);
        this.mGridView = (GridView) this.findViewById(R.id.gv_detail);
        mTvSize = (TextView) this.findViewById(R.id.packagesize);
        this.findViewById(R.id.setting).setVisibility(View.GONE);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mIvprotarit = (ImageView) this.findViewById(R.id.protarit);
        mTvpacketname = (TextView) this.findViewById(R.id.packetname);
        mTvDes = (TextView) this.findViewById(R.id.tv_des);
        title = (TextView) findViewById(R.id.title);
        packageBean = (PackageBean) getIntent().getSerializableExtra(
                "packageBean");
        // title.setText(packageBean.getPackageName());
        title.setText("表情详情");
        packageId = packageBean.getPackageId();
        packageName = packageBean.getPackageName();

        ivStatus = (ImageView) this.findViewById(R.id.iv_status);
        tvPrice = (TextView) this.findViewById(R.id.tv_price);
        tvFree = (TextView) this.findViewById(R.id.tv_free);
        ivHasdown = (ImageView) this.findViewById(R.id.iv_hasdown);
        tvPro = (TextView) this.findViewById(R.id.tv_pro);
        proDown = (ProgressBar) this.findViewById(R.id.pro_down);
        ivStop = (ImageView) this.findViewById(R.id.iv_stop);
        ivDowned = (ImageView) this.findViewById(R.id.iv_downed);

        rlPay = (RelativeLayout) this.findViewById(R.id.rl_pay);
        rlFree = (RelativeLayout) this.findViewById(R.id.rl_free);
        rlHasdown = (RelativeLayout) this.findViewById(R.id.rl_hasDown);
        llProgress = (LinearLayout) this.findViewById(R.id.ll_progress);
        rlDowned = (RelativeLayout) this.findViewById(R.id.rl_downed);

    }

    public void setListener() {
        mBack.setOnClickListener(this);
        rlFree.setOnClickListener(this);
        ivHasdown.setOnClickListener(this);
        rlFree.setOnClickListener(this);
        ivStop.setOnClickListener(this);
    }

    public void loadData() {
        http = new CIMExpressionHttp(this);
        String token = SYUserManager.getInstance().getToken();
        String vc = DeviceInfo.getAppVersion();
        if (!http.isNetworkAvailable(mContext)) {
            showNetError();
            return;
        }
//        http.getExpressionDetail(CIMExpressionHttp.IM_EXPRESSIONDETAIL_METHOD,
//                token, vc, packageId, this);
        IMExpressionDetailRequest.send(CIMExpressionHttp.IM_EXPRESSIONDETAIL_METHOD,
                token, vc, packageId, this);
    }

    @Override
    public void onHttpError(IRequest _request) {
        // TODO Auto-generated method stub
        super.onHttpError(_request);
        switch (_request.getmId()) {
            case CIMExpressionHttp.IM_EXPRESSIONDETAIL_METHOD:
                showNetError();
                break;

            default:
                break;
        }
    }

    @Override
    public void onHttpResponse(IRequest _request) {
        // TODO Auto-generated method stub
        super.onHttpResponse(_request);
        HttpJsonResponse response = null;
        switch (_request.getmId()) {
            case CIMExpressionHttp.IM_EXPRESSIONDETAIL_METHOD:
                response = (HttpJsonResponse) _request.getResponse();
                ExpressionDetailBean bean = new Gson().fromJson(response.getBody(),
                        ExpressionDetailBean.class);
                mDetail = bean;
                goneLoad();
                setData();
                break;

            default:
                break;
        }
    }

    public void getExpressionDetailSuccess(ExpressionDetailBean detail) {
        if (detail == null) {
            return;
        }
        mDetail = detail;
        setData();

    }

    public void setData() {
        mThumbnails = mDetail.getThumbnails();
        mAdapter = new IMExpressionDetaiAdapter(this, mThumbnails);
        mGridView.setAdapter(mAdapter);
        PhotoUtils.showCard( UriType.HTTP, packageBean.getIconUrl(), mIvprotarit);
        mTvpacketname.setText(packageName);
        mTvDes.setText(mDetail.getDesc());
        double size = (double) mDetail.getPackageSize() / (double) 1048576;
        String strd = String.valueOf(size);
        String filesize = strd.substring(0, strd.indexOf(".") + 3);
        mTvSize.setText(filesize + "MB");

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = null;
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.rl_free:
            case R.id.iv_hasdown:
                intent = new Intent(mContext, DownLoadService.class);
                showSpecialBtn(llProgress);
                intent.putExtra("flag", "startDown");
                intent.putExtra("packagebean", packageBean);
                mContext.startService(intent);
                break;

            case R.id.iv_stop:
                intent = new Intent(mContext, DownLoadService.class);
                intent.putExtra("packagebean", packageBean);
                intent.putExtra("flag", "stop");
                mContext.startService(intent);
                break;

            default:
                break;
        }

    }

    class UpdateBroadCastReceiver extends BroadcastReceiver {
        Context context;
        IntentFilter filter = new IntentFilter();

        public UpdateBroadCastReceiver(Context context) {
            this.context = context;
        }

        public void registerAction(String action) {
            filter.addAction(action);
            registerReceiver(this, filter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if ("updateUI".equals(intent.getAction())) {
                setStatus();
            } else if (Constants.FAIL_ACTION.equals(intent.getAction())) {
                setStatus();
            }
        }

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(receiver);
        http.cancelAll();
    }
}
