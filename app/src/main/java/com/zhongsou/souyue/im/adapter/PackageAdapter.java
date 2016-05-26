package com.zhongsou.souyue.im.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.download.DownLoadService;
import com.zhongsou.souyue.im.download.LoadInfo;
import com.zhongsou.souyue.im.download.MemoryPackageDao;
import com.zhongsou.souyue.im.download.PackageDao;
import com.zhongsou.souyue.im.module.PackageBean;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;
import com.zhongsou.souyue.im.util.Slog;
import com.zhongsou.souyue.im.view.CustomerDialog;

import java.util.List;

public class PackageAdapter extends BaseAdapter {

    private Context mContext;
    private List<PackageBean> mPackages;
    private LayoutInflater mLayoutInflater;
    private PackageDao dao;
    private int pageType = 0; // 页面类型
    private DisplayImageOptions expressionOptions = new DisplayImageOptions.Builder()
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT).cacheOnDisk(true)
            .cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
            .showImageForEmptyUri(R.drawable.news_default_img_c)
            .showImageOnFail(R.drawable.news_default_img_c)
            .showImageOnLoading(R.drawable.news_default_img_c)
            .displayer(new RoundedBitmapDisplayer(8))
            .build();

    public PackageAdapter(Context context, List<PackageBean> packages, int type) {
        this.mContext = context;
        this.mPackages = packages;
        mLayoutInflater = LayoutInflater.from(context);
        dao = new PackageDao(context);
        this.pageType = type;
    }

    public void updateList(PackageBean bean) {
        for (int i = 0; i < mPackages.size(); i++) {
            if (mPackages.get(i).getPackageId().equals(bean.getPackageId())) {
                mPackages.get(i).setIsDownloaded(1);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void updateDownStatus(PackageBean bean) {
        if (mPackages.contains(bean)) {
            mPackages.get(mPackages.indexOf(bean)).setIsDownloaded(1);
            notifyDataSetChanged();
        }
    }

    public long getMinSortNo() {
        if (mPackages != null && mPackages.size() != 0) {
            return mPackages.get(0).getSortNo();
        } else
            return 0;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mPackages != null ? mPackages.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mPackages.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(
                    R.layout.im_expressionlist_item, null, false);
            viewHolder = new ViewHolder();

            viewHolder.ivProtrait = (ImageView) convertView
                    .findViewById(R.id.protarit);
            viewHolder.tvName = (TextView) convertView
                    .findViewById(R.id.packetname);
            viewHolder.tvSize = (TextView) convertView
                    .findViewById(R.id.packagesize);
            viewHolder.ivStatus = (ImageView) convertView
                    .findViewById(R.id.iv_status);
            viewHolder.tvPrice = (TextView) convertView
                    .findViewById(R.id.tv_price);
            viewHolder.tvFree = (TextView) convertView
                    .findViewById(R.id.tv_free);
            viewHolder.ivHasdown = (ImageView) convertView
                    .findViewById(R.id.iv_hasdown);
            viewHolder.tvPro = (TextView) convertView.findViewById(R.id.tv_pro);
            viewHolder.proDown = (ProgressBar) convertView
                    .findViewById(R.id.pro_down);
            viewHolder.ivStop = (ImageView) convertView
                    .findViewById(R.id.iv_stop);
            viewHolder.ivDowned = (ImageView) convertView
                    .findViewById(R.id.iv_downed);
            viewHolder.tvRemove = (TextView) convertView
                    .findViewById(R.id.tv_remove);
            viewHolder.ivNew = (ImageView) convertView
                    .findViewById(R.id.iv_new);

            viewHolder.rlPay = (RelativeLayout) convertView
                    .findViewById(R.id.rl_pay);
            viewHolder.rlFree = (RelativeLayout) convertView
                    .findViewById(R.id.rl_free);
            viewHolder.rlHasdown = (RelativeLayout) convertView
                    .findViewById(R.id.rl_hasDown);
            viewHolder.llProgress = (LinearLayout) convertView
                    .findViewById(R.id.ll_progress);
            viewHolder.rlDowned = (RelativeLayout) convertView
                    .findViewById(R.id.rl_downed);
            viewHolder.rlRemove = (RelativeLayout) convertView
                    .findViewById(R.id.rl_remove);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final PackageBean packageBean = mPackages.get(position);

        viewHolder.ivHasdown.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(mContext, DownLoadService.class);
                viewHolder.proDown.setProgress(0);
                viewHolder.tvPro.setText("0%");
                showSpecialBtn(viewHolder.llProgress, viewHolder);
                intent.putExtra("flag", "startDown");
                intent.putExtra("packagebean", packageBean);
                Slog.d("callback", "--------------------下载");
                mContext.startService(intent);
            }
        });

        viewHolder.tvFree.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(mContext, DownLoadService.class);
                viewHolder.proDown.setProgress(0);
                viewHolder.tvPro.setText("0%");
                showSpecialBtn(viewHolder.llProgress, viewHolder);
                intent.putExtra("flag", "startDown");
                intent.putExtra("packagebean", packageBean);
                Slog.d("callback", "--------------------下载");
                mContext.startService(intent);
            }
        });

        // 1.设置头像
        PhotoUtils.showCard( UriType.HTTP, packageBean.getIconUrl(),
                viewHolder.ivProtrait,expressionOptions);
        //boolean isNew = packageBean.getIsNew() == 1;
        boolean isDown = packageBean.getIsDownloaded() == 1;
        if (!isDown && pageType == 0) {
            viewHolder.ivNew.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivNew.setVisibility(View.GONE);
        }
        // 2.设置包名
        viewHolder.tvName.setText(packageBean.getPackageName());
        // 3.设置包大小
        long packageSize = packageBean.getPackageSize();
        double size = (double) packageSize / (double) 1048576;
        String strd = String.valueOf(size);
        Slog.d("callback", "fileSize  Mb-------------" + strd);
        String filesize;
        if (strd.length() < 4) {
            strd.concat("0000");
        }
        filesize = strd.substring(0, strd.indexOf(".") + 3);
        viewHolder.tvSize.setText(filesize + "MB");

        // 4.设置状态
        boolean isExist = dao.hasPackage(packageBean.getPackageId());
        boolean isDowning = MemoryPackageDao.isHasInfo(packageBean
                .getPackageId());

        if (!isExist && packageBean.getIsDownloaded() == 1 && !isDowning) { // 云
            showSpecialBtn(viewHolder.rlHasdown, viewHolder);
        }

        if (isDowning) { // 正在下载中
            showSpecialBtn(viewHolder.llProgress, viewHolder);
            LoadInfo info = MemoryPackageDao.getInfos(packageBean
                    .getPackageId());
            if (info == null) {
                Slog.d("PackageAdapter", "为null");
                viewHolder.proDown.setMax(100);
                viewHolder.proDown.setProgress(0);
                viewHolder.tvPro.setText(0 + "%");
            } else {
                int totalsize = info.getFileSize();
                int completeSize = info.getComplete();
                Slog.d("callback", "下载了－－－－－－－－－" + completeSize);
                int result = (completeSize * 100) / totalsize;
                viewHolder.proDown.setMax(100);
                viewHolder.proDown.setProgress(result);
                viewHolder.tvPro.setText(result + "%");
                if (result == 100) {
                    showSpecialBtn(viewHolder.rlDowned, viewHolder);
                }
            }

        }

        if (isExist && pageType == 0) { // 已下载
            showSpecialBtn(viewHolder.rlDowned, viewHolder);
        }

        if (!isExist && "0".equals(packageBean.getPrice())
                && packageBean.getIsDownloaded() == 0 && !isDowning) { // 免费
            showSpecialBtn(viewHolder.rlFree, viewHolder);
        }
        if (!isExist && !"0".equals(packageBean.getPrice())
                && packageBean.getIsDownloaded() == 0) { // 收费
            showSpecialBtn(viewHolder.rlPay, viewHolder);
        }

        if (isExist && pageType == 1) { // 移除
            showSpecialBtn(viewHolder.rlRemove, viewHolder);
            viewHolder.tvRemove.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    showDialog(packageBean);
                }
            });
        }

        viewHolder.ivStop.setOnClickListener(new OnClickListener() { // 停止下载

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // MapDao.stopThread(packageBean.getPackageId());
                Slog.d("callback", "停止下载了－－－－－" + packageBean.getPackageName());
                Intent intent = new Intent(mContext,
                        DownLoadService.class);
                intent.putExtra("packagebean", packageBean);
                intent.putExtra("flag", "stop");
                mContext.startService(intent);
            }
        });

        return convertView;
    }

    private void showSpecialBtn(View showBtnArea,
                                PackageAdapter.ViewHolder holder) {
        holder.rlPay.setVisibility(View.GONE);
        holder.rlFree.setVisibility(View.GONE);
        holder.rlHasdown.setVisibility(View.GONE);
        holder.llProgress.setVisibility(View.GONE);
        holder.rlDowned.setVisibility(View.GONE);
        holder.rlRemove.setVisibility(View.GONE);
        showBtnArea.setVisibility(View.VISIBLE);

    }

    public class ViewHolder {
        public ImageView ivProtrait;// 头像
        public ImageView ivNew; // 是否新
        public TextView tvName;// 包名
        public TextView tvSize; // 包大小

        public ImageView ivStatus;
        public TextView tvPrice;
        public TextView tvFree;
        public ImageView ivHasdown;
        public TextView tvPro;// 进度size
        public ProgressBar proDown;// 进度条
        public ImageView ivStop;// 暂停
        public ImageView ivDowned;
        public TextView tvRemove;// 移除

        public RelativeLayout rlPay; // 非下载状态
        public RelativeLayout rlFree;
        public RelativeLayout rlHasdown; // 以前下载过
        public LinearLayout llProgress; // 进度条
        public RelativeLayout rlDowned;
        public RelativeLayout rlRemove;
    }

    // 确认卸载
    private CustomerDialog customerDialog;
    private View btnCancle, btnConfirm;

    /**
     * 确认卸载dialog
     */
    private void showDialog(final PackageBean packageBean) {
        if (customerDialog == null) {
            customerDialog = new CustomerDialog(mContext, 40,
                    R.layout.im_upload_dialog, R.style.im_dialog_style, false);
            btnCancle = (Button) customerDialog
                    .findViewById(R.id.dialog_cancel);
            btnConfirm = (Button) customerDialog
                    .findViewById(R.id.dialog_confirm);
        }

        btnCancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (customerDialog != null)
                    customerDialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dao.delete(packageBean);
                updateDownStatus(packageBean);
                customerDialog.dismiss();
            }
        });
        customerDialog.show();
    }

}
