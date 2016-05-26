package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.ZSImageListener;
import com.facebook.drawee.view.ZSImageOptions;
import com.facebook.drawee.view.ZSImageView;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.ui.AccountConflicDialog;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.ToastUtil;

/**
 * @description: 公共渲染器， 处理公共视图，事件，管理等
 *
 * @auther: qubian
 * @data: 2015/12/22.
 */

public abstract class ListTypeRender implements BaseListTypeRender ,View.OnClickListener{

    // 此处排序一定要从 0 开始
    public static final int TYPE_DEFAULT =0;
    public static final int TYPE_FOCUS =1;
    public static final int TYPE_ONE_IMAGE =2;
    public static final int TYPE_THREE_IMAGE =3;
    public static final int TYPE_SPECIAL=4;
    public static final int TYPE_BIGIMAGE=5;
    public static final int TYPE_JOKE=6;
    public static final int TYPE_VIDEO=7;
    public static final int TYPE_BANNER=8;
    public static final int TYPE_FRESH=9;
    public static final int TYPE_SEARCH_RESULT=10;
    protected View mConvertView;
    protected View mBottomView;
    protected TextView mTitleTv;
    protected Context mContext;
    protected int mBottomType;
    protected BaseBottomViewRender bottomViewRender =null;
    protected BaseListViewAdapter mAdaper =null;
    protected BaseListManager mListManager;
    private boolean mHasRead;
    private float mTextSize;
    private static boolean isWifi = CMainHttp.getInstance().isWifi(MainApplication.getInstance());
    public ListTypeRender(Context context,int itemType,int bottomType,BaseListViewAdapter adapter) {
        mContext =context;
        mBottomType =bottomType;
        mAdaper = adapter;
    }

    /**
     * 网络状态 改变 需要重新设置 isWifi
     */
    public static void resetNet()
    {
        isWifi = CMainHttp.getInstance().isWifi(MainApplication.getInstance());
    }
    @Override
    public View getConvertView() {
        if(mConvertView!=null)
        {
            mTitleTv= findView(mConvertView, R.id.title);//将findview移至这里以提高性能
            if(mTitleTv!=null)
            {
                mTitleTv.setTextColor(mContext.getResources().getColor(R.color.list_has_no_read));
            }
//            LinearLayout view = (LinearLayout) mConvertView.findViewById(R.id.bottomView);
//            if(view != null&& mBottomType != 0)
            {
//                    bottomViewRender=getBottomView(mBottomType);
//                    if(bottomViewRender!=null)
//                    {
//                        view.addView(bottomViewRender.getConvertView());
//                    }
//                    view.setVisibility(View.VISIBLE);
            }
        }
        return mConvertView;
    }

    @Override
    public void fitEvents() {
        if(bottomViewRender!=null)
        {
            bottomViewRender.fitEvents();
        }

    }

    @Override
    public void fitDatas(int position) {
        BaseListData bean = (BaseListData) mAdaper.getItem(position);
        if (mTitleTv != null) {
            if (bean.isHasRead() != mHasRead) {//先判定当前状态是否是已读状态，如果不是再做处理
                mHasRead = bean.isHasRead();
                int color ;
                if (mHasRead) {
                    color = mContext.getResources().getColor(R.color.list_has_read);
                }else {
                    color = mContext.getResources().getColor(R.color.list_has_no_read);
                }
                mTitleTv.setTextColor(color);
            }
            float size = mListManager.getFontSize();
            if (mTextSize != size){
                mTextSize = size;
                mTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP,mTextSize);
            }
            ListUtils.setViewString(mTitleTv, getTitle(bean));
        }
        if(bottomViewRender!=null)
        {
            bottomViewRender.fitDatas(position);
        }
    }

    @Override
    public void setListManager(BaseListManager manager) {
        mListManager =manager;
    }

    @Override
    public BaseBottomViewRender getBottomView(int bottomType)
    {
        // 底部可以没有数据 default
        switch (bottomType)
        {
            case BottomViewRender.BottomViewDEFAULT:
                return null;
            case  BottomViewRender.BottomViewTYPE1:
                bottomViewRender = new BottomViewRender1(mContext,bottomType,mAdaper);
                break;
            case BottomViewRender.BottomViewTYPE2:
                bottomViewRender = new BottomViewRender2(mContext,bottomType,mAdaper);
                break;
            case BottomViewRender.BottomViewTYPE3:
                bottomViewRender = new BottomViewRender3(mContext,bottomType,mAdaper);
                break;
            case BottomViewRender.BottomViewTYPE4:
                bottomViewRender = new BottomViewRender4(mContext,bottomType,mAdaper);
                break;
            case  BottomViewRender.BottomViewTYPE5:
                bottomViewRender = new BottomViewRender5(mContext,bottomType,mAdaper);
                break;
            default:
                bottomViewRender = new BottomViewRender1(mContext,bottomType,mAdaper);
                break;
        }
        bottomViewRender.setListManager(mListManager);
        return bottomViewRender;
    }

    public <T extends View> T findView(View view, int id) {
        return (T) view.findViewById(id);
    }

    /**
     * 设置wifi 加载图片的控制
     * ScaleType.CENTER_CROP
     * @param imageView
     * @param url
     * @param degfaultId
     * @param listener
     */
    public void showImage(ZSImageView imageView, String url,int degfaultId ,ZSImageListener listener)
    {
        if(SYSharedPreferences.getInstance().getLoadWifi(MainApplication.getInstance())){
            if(isWifi) {
                imageView.setImageURL(url, ZSImageOptions.getDefaultConfigList(mContext,degfaultId),listener);
            }else{
                imageView.setImageResource(degfaultId);
            }
        }else{
            imageView.setImageURL(url, ZSImageOptions.getDefaultConfigList(mContext,degfaultId),listener);
        }
    }

    /**
     * 无论wifi 强制加载
     * ScaleType.FIT_XY
     * @param imageView
     * @param url
     * @param degfaultId
     * @param listener
     */
    public void showImageForce(ZSImageView imageView, String url,int degfaultId ,ZSImageListener listener)
    {
        imageView.setImageURL(url, ZSImageOptions.getDefaultConfig(mContext,degfaultId),listener);
    }

    /**
     * 对加载的drawable 的控制
     * ScaleType.FIT_XY
     * @param imageView
     * @param url
     * @param drawable
     * @param listener
     */
    public void showImageForce(ZSImageView imageView, String url, Drawable  drawable, ZSImageListener listener)
    {
        imageView.setImageURL(url, drawable==null?ZSImageOptions.getDefaultConfigForNone(mContext):ZSImageOptions.getDefaultConfig(mContext,drawable),listener);
    }

    /**
     * WIFI下 加载的 drawable 的控制
     * ScaleType.FIT_XY
     * @param imageView
     * @param url
     * @param drawable
     * @param listener
     */
    public void showImage(ZSImageView imageView, String url, Drawable  drawable, ZSImageListener listener)
    {
        if(SYSharedPreferences.getInstance().getLoadWifi(MainApplication.getInstance())){
            if(isWifi) {
                imageView.setImageURL(url, drawable==null?ZSImageOptions.getDefaultConfigForNone(mContext):ZSImageOptions.getDefaultConfig(mContext,drawable),listener);
            }else{
                imageView.setImageDrawable(drawable);
            }
        }else{
            imageView.setImageURL(url, drawable==null?ZSImageOptions.getDefaultConfigForNone(mContext):ZSImageOptions.getDefaultConfig(mContext,drawable),listener);
        }
    }

    @Override
    public void onClick(View v) {

    }
    protected String getTitle(BaseListData bean)
    {
       return StringUtils.isNotEmpty(bean.getTitle())?bean.getTitle():bean.getDesc();
    }

}
