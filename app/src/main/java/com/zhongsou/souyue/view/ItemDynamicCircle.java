package com.zhongsou.souyue.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.GridDynamicAdapter;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.view.CustomerDialog;
import com.zhongsou.souyue.module.AGridDynamic;
import com.zhongsou.souyue.module.HomeBallBean;
import com.zhongsou.souyue.module.InterestBean;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.module.SubscribeItem;
import com.zhongsou.souyue.utils.StringUtils;

/**
 * 我的订阅中每个球的Holder类
 */
public class ItemDynamicCircle extends LinearLayout {
    private GridDynamicAdapter.DeleteListener deleteListener;
    private ItemDynamicCircle mItemDynamicCircle;

    private DisplayImageOptions options;
    private Animation mAnimationDispear;
    private AGridDynamic mDataItem;
    private GridDynamicAdapter mAdapter;

    private long mId;
    public TextView titleText;
    public ImageView image;
    private ImageView del;
    private ImageView isSecret;
    private ImageView isGroup;
    private int mPostion;
    private Context context;
    private Handler fHandler = new Handler();

    public ItemDynamicCircle(Context context,
                             GridDynamicAdapter.DeleteListener _deleteListener) {
        super(context);
        this.context = context;
        inflate(context, R.layout.item_dynamic_grid, this);
        setGravity(Gravity.CENTER);
        mItemDynamicCircle = this;
        titleText = (TextView) findViewById(R.id.item_title);
        image = (ImageView) findViewById(R.id.item_img);
        del = (ImageView) findViewById(R.id.item_del);
        isSecret = (ImageView) findViewById(R.id.issecret);
        isGroup = (ImageView) findViewById(R.id.item_group);
        deleteListener = _deleteListener;
        mAnimationDispear = AnimationUtils.loadAnimation(context,
                R.anim.anim_dispear);
        mAnimationDispear
                .setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        setVisibility(INVISIBLE);
                        fHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mDataItem.setmState(AGridDynamic.STATE_DELETE);
                                    deleteListener.removeItem(mPostion);
                                    mAdapter.setmDoingAnim(false);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

        del.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mAdapter != null && !mAdapter.getmDoingAnim()) {
                    SuberedItemInfo item = (SuberedItemInfo) mAdapter.getItem(mPostion);
                    if (item != null && item.getCategory().equalsIgnoreCase(HomeBallBean.GROUP_NEWS)) {
                        showDeleteDialog();
                        return;
                    }
                    mAdapter.setmDoingAnim(true);
                    mItemDynamicCircle.startAnimation(mAnimationDispear);
                }
            }
        });

        options = MyDisplayImageOption.newoptions;
    }

    private CustomerDialog customerDialog;
    private View btnCancle, btnConfirm;

    private void showDeleteDialog() {
        if (customerDialog == null) {
            customerDialog = new CustomerDialog(context, 40,
                    R.layout.subgroup_delete_dialog, R.style.im_dialog_style, false);
            btnCancle = (Button) customerDialog
                    .findViewById(R.id.dialog_cancel);
            btnConfirm = (Button) customerDialog
                    .findViewById(R.id.dialog_confirm);
            customerDialog.setCanceledOnTouchOutside(false);
        }

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (customerDialog != null)
                    customerDialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (customerDialog != null)
                    customerDialog.dismiss();
                mAdapter.setmDoingAnim(true);
                mItemDynamicCircle.startAnimation(mAnimationDispear);
            }
        });
        customerDialog.show();
    }

    public Bitmap getClipBitmap(Bitmap _bitmap) {
        int w = _bitmap.getWidth();
        int h = _bitmap.getHeight();
        Bitmap bmp = null;
        int bitSize = 0;
        int x = 0;
        int y = 0;
        if (w > h) {
            bitSize = h;
            x = (w - h) / 2;
        } else {
            bitSize = w;
            y = (h - w) / 2;
        }
        try {
            bmp = Bitmap.createBitmap(_bitmap, x, y, bitSize, bitSize);
        } catch (OutOfMemoryError e) {
            while (bmp == null) {
                System.gc();
                System.runFinalization();
                getClipBitmap(_bitmap);
            }
        }
        return bmp;
    }

    public void build(GridDynamicAdapter _adapter, AGridDynamic item,
                      final int position, boolean showDelView) {
        mAdapter = _adapter;
        build(item, position, showDelView);
    }

    public void build(AGridDynamic item, final int position, boolean showDelView) {

        SuberedItemInfo info = (SuberedItemInfo) item;
        titleText.setText(info.getTitle());
        if (!"1".equals(info.getType())) {
            isSecret.setVisibility(View.GONE);
            if (StringUtils.isNotEmpty(info.getImage())) {
                // imgloader.displayImage(info.getImage(), image, options);
                PhotoUtils.showCard(PhotoUtils.UriType.HTTP, info.getImage(), image, options);
            } else {
                // imgloader.displayImage("drawable://"
                //       + R.drawable.news_default_img_c, image, options);
                PhotoUtils.showCard(PhotoUtils.UriType.DRAWABLE, R.drawable.news_default_img_c + "", image, options);
            }
        }
        if ("1".equals(info.getType())) {
            PhotoUtils.showCard(PhotoUtils.UriType.HTTP, info.getImage(),
                    image, options);
            isSecret.setVisibility(View.VISIBLE);
        }

        mPostion = position;
        mDataItem = item;
        if (showDelView) {
            del.setVisibility(View.VISIBLE);
        } else {
            del.setVisibility(View.INVISIBLE);
        }
        if (item.getmState() == AGridDynamic.STATE_INIT) {
            setVisibility(VISIBLE);
        } else {
            setVisibility(INVISIBLE);
        }
        if (HomeBallBean.GROUP_NEWS.equals(info.getCategory())) {
            isGroup.setVisibility(View.VISIBLE);
        } else {
            isGroup.setVisibility(View.GONE);
        }

    }

    public int getPosition() {
        return mPostion;
    }

    public AGridDynamic getData() {
        return mDataItem;
    }

    public ItemDynamicCircle clone() {
        ItemDynamicCircle item = new ItemDynamicCircle(getContext(),
                deleteListener);
        item.titleText.setText(titleText.getText());
        item.image.invalidateDrawable(image.getDrawable());
        item.build(mDataItem, mPostion, true);
        return item;
    }

    public void exchangeView(ItemDynamicCircle item2) {
        // Drawable able = image.getDrawable();
        // CharSequence text = titleText.getText();
        AGridDynamic data = mDataItem;
        int position = mPostion;
        int visible = getVisibility();
        titleText.setText(item2.titleText.getText());
        image.setImageDrawable(item2.image.getDrawable());
        // this.destroyDrawingCache();
        build(item2.getData(), position, true);
        // item2.image.setImageDrawable(able);
        // titleText.setText(text);
        invalidate();
        item2.build(data, item2.getPosition(), true);
        item2.setVisibility(visible);
    }

    private void buildHightEffect(AGridDynamic item, final int position) {
        if (item instanceof SubscribeItem) {

            SubscribeItem it = (SubscribeItem) item;
            mId = it.id();
            titleText.setText(it.keyword());
        } else if (item instanceof InterestBean) {
            InterestBean inter = (InterestBean) item;
            mId = inter.getId();
            if (inter.getType() == 0)
                isSecret.setVisibility(View.GONE);
            else if (inter.getType() == 1) {
                isSecret.setVisibility(View.VISIBLE);
            }
        } else {
            titleText.setText("");
            image.setImageResource(R.drawable.news_default_img);
        }

        mPostion = position;
        mDataItem = item;
        del.setVisibility(View.VISIBLE);
        if (item.getmState() == AGridDynamic.STATE_INIT) {
            setVisibility(VISIBLE);
        } else {
            setVisibility(INVISIBLE);
        }
    }
}
