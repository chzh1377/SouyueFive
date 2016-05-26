package com.zhongsou.souyue.ui.dynamicgrid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.GridDynamicAdapter;
import com.zhongsou.souyue.module.AGridDynamic;
import com.zhongsou.souyue.view.GridViewWithHeaderAndFooter;
import com.zhongsou.souyue.view.ItemDynamicCircle;

import java.util.LinkedList;

/**
 * Created by on 14-12-23.
 * 可以拖拽编辑的GridView
 */
public class DynamicGridEdit extends FrameLayout implements Runnable {
    private static final int SCROLL_UP = 1;
    private static final int SCROLL_DOWN = -1;
    private static final int SCROLL_NONE = 0;

    private static final int INVALID_ID = -1;
    private static final int SWEET_SPACE = 10;
    private static final int SMOOTH_SCROLL_AMOUNT_AT_EDGE = 8;

    private View footView;
    private LayoutInflater inflater;

    GridViewWithHeaderAndFooter mGridView;
    int mDownX;
    int mDownY;
    int mPointMoveX;
    int mPointMoveY;
    int mActivePointerId;
    int mScollDierection;
    boolean mIsEditMode;
    boolean mIsCellMove;
    int mInitItemPosition;
    int mMoveItemPosition;
    Rect mHoverCellCurrentBounds;
    Rect mHoverCellOriginalBounds;
    private int mSmoothScrollAmountAtEdge = 0;
    View mSelectView;
    private TextView tvTotal;

    int mRemoveCount = 0;

    public GridDynamicAdapter getmAdapter() {
        return mAdapter;
    }

    public void setmAdapter(GridDynamicAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    //    int mMoveCount;
    GridDynamicAdapter mAdapter;
    EditEnable mEditEnable;
    ItemDynamicCircle mHoverCell;
    //    AGridDynamic mSelectData;
    boolean mIsRuning = false;

    LinkedList<ItemDynamicCircle> mCaches;
    LinkedList<AGridDynamic> mCacheDatas;
    private Context mContext;

    public interface EditEnable {
        void setEditEnable(boolean _isEdit);
    }

    public DynamicGridEdit(Context context) {
        super(context);
        this.mContext = context;
    }

    public DynamicGridEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public DynamicGridEdit(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    /**
     * gridView 初始化
     */
    public void init() {
        mGridView = (GridViewWithHeaderAndFooter) findViewById(R.id.dynamic_grid);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mSmoothScrollAmountAtEdge = (int) (SMOOTH_SCROLL_AMOUNT_AT_EDGE * metrics.density + 0.5f);
//        mTranslateAnimationCache = new HashMap<Animation, View>();
        mCaches = new LinkedList<ItemDynamicCircle>();
        mCacheDatas = new LinkedList<AGridDynamic>();
        mRemoveCount = 0;

        //添加footview
        inflater = LayoutInflater.from(mContext);
        footView = inflater.inflate(R.layout.sub_footer_view, null);
        tvTotal = (TextView) footView.findViewById(R.id.tv_total);
        mGridView.addFooterView(footView);

        //  visiableFootView();
    }

    public void setAdapter(ListAdapter adapter) {
        mGridView.setAdapter(adapter);
        mAdapter = (GridDynamicAdapter) adapter;
        setText();

    }

    public void setEditEnable(EditEnable editEnable) {
        this.mEditEnable = editEnable;
    }

    public boolean isEditMode() {
        return mIsEditMode;
    }

    public EditEnable getEditEnable() {
        return mEditEnable;
    }

    public void startEditMode() {
        goneFootView();
        mIsEditMode = true;
        mEditEnable.setEditEnable(mIsEditMode);
        mGridView.setEdited(mIsEditMode);
        doRuning();
    }

    public void doRuning() {
        mIsRuning = true;
        post(this);
    }

    public void goneFootView() {
        tvTotal.setVisibility(View.INVISIBLE);
    }

    public void visiableFootView() {
        setText();
        tvTotal.setVisibility(View.VISIBLE);
    }

    public void setText() {
        if (tvTotal != null && mAdapter != null) {
            tvTotal.setText(mAdapter.getCount() + "个订阅");
        }
    }

    public void stopEditMode() {
        visiableFootView();
        mIsEditMode = false;
        mEditEnable.setEditEnable(mIsEditMode);
        mGridView.setEdited(mIsEditMode);
        mIsRuning = false;

    }

    @SuppressLint("NewApi")
    @Override
    public void run() {
        if (mIsRuning) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                postOnAnimation(this);
            } else {
                postDelayed(this, SWEET_SPACE);
            }
        }
        switch (mScollDierection) {
            case SCROLL_UP:
                doSwitch();
                mGridView.smoothScrollBy(-mSmoothScrollAmountAtEdge / 5, 0);
                break;
            case SCROLL_DOWN:
                doSwitch();
                mGridView.smoothScrollBy(mSmoothScrollAmountAtEdge / 5, 0);
                break;
            case SCROLL_NONE:
                break;
        }
    }

    private AdapterView.OnItemLongClickListener mUserLongClickListener;
    private AdapterView.OnItemLongClickListener mLocalLongClickListener = new AdapterView.OnItemLongClickListener() {
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
            if (!isEnabled() || mIsEditMode)
                return false;
            if (mUserLongClickListener != null)
                mUserLongClickListener.onItemLongClick(arg0, arg1, pos, id);


            return true;
        }
    };

    private AdapterView.OnItemClickListener mUserItemClickListener;
    private AdapterView.OnItemClickListener mLocalItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!mIsEditMode && isEnabled() && mUserItemClickListener != null) {
                mUserItemClickListener.onItemClick(parent, view, position, id);
            }
        }
    };

    public void setOnItemLongClickListener(final AdapterView.OnItemLongClickListener listener) {
        mUserLongClickListener = listener;
        mGridView.setOnItemLongClickListener(mLocalLongClickListener);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.mUserItemClickListener = listener;
        mGridView.setOnItemClickListener(mLocalItemClickListener);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
//            Log.v(this.getClass().getName(), "响应事件：dispatchTouchEvent" + (ev.getAction() & MotionEvent.ACTION_MASK));
            final int action = ev.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = (int) ev.getX();
                    mDownY = (int) ev.getY();
                    mActivePointerId = ev.getPointerId(0);

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!mIsEditMode) {
                        break;
                    }
                    if (mIsCellMove) {
                        return true;
                    }
                    if (mActivePointerId == INVALID_ID) {
                        break;
                    }
                    int pointerIndex = ev.findPointerIndex(mActivePointerId);

                    mPointMoveX = (int) ev.getX(pointerIndex);
                    mPointMoveY = (int) ev.getY(pointerIndex);
                    int deltaX = mPointMoveX - mDownX;
                    int deltaY = mPointMoveY - mDownY;
                    if (Math.abs(deltaX) > 10 || Math.abs(deltaY) > 10) {
                        doInitMove();
                        mIsCellMove = true;
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    break;
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean doInitMove() {
        if (mIsEditMode && isEnabled()) {

            mInitItemPosition = mGridView.pointToPosition(mDownX, mDownY);
            int itemNum = mInitItemPosition - mGridView.getFirstVisiblePosition();
            mSelectView = mGridView.getChildAt(itemNum);
            if (mSelectView == null) {
                return false;
            } else {
                AGridDynamic mSelectData = mAdapter.getItem(mInitItemPosition);
                mSelectData.setmState(AGridDynamic.STATE_SPACE);
                mCacheDatas.addLast(mSelectData);
                mHoverCell = getAndAddHoverView(mSelectView);
                mSelectView.setVisibility(INVISIBLE);
                mMoveItemPosition = mInitItemPosition;
            }
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        try {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = (int) event.getX();
                    mDownY = (int) event.getY();
                    mActivePointerId = event.getPointerId(0);

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mActivePointerId == INVALID_ID) {
                        break;
                    }

                    int pointerIndex = event.findPointerIndex(mActivePointerId);

                    mPointMoveX = (int) event.getX(pointerIndex);
                    mPointMoveY = (int) event.getY(pointerIndex);
                    int deltaX = mPointMoveX - mDownX;
                    int deltaY = mPointMoveY - mDownY;

                    if (mHoverCell != null && mIsCellMove) {
                        mHoverCellCurrentBounds.offsetTo(mHoverCellOriginalBounds.left + deltaX,
                                mHoverCellOriginalBounds.top + deltaY);
                        doSwitch();
                        mScollDierection = handleMobileCellScroll(mHoverCellCurrentBounds);
                        return false;
                    }

                    break;
                case MotionEvent.ACTION_UP: {
                    int pointerId = event.getPointerId(event.getActionIndex());
                    if (pointerId == mActivePointerId) {
                        onTouchEnd();
                    }

                }
                break;
                case MotionEvent.ACTION_CANCEL: {
                    int pointerId = event.getPointerId(event.getActionIndex());
                    if (pointerId == mActivePointerId) {
                        onTouchEnd();
                    }
                }
                break;
                case MotionEvent.ACTION_POINTER_UP: {
                    pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                            MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    int pointerId = event.getPointerId(pointerIndex);
                    if (pointerId == mActivePointerId) {
                        onTouchEnd();

                    }
                }
                break;
                default:
                    int pointerId = event.getPointerId(event.getActionIndex());
                    if (pointerId == mActivePointerId) {
                        onTouchEnd();
                    }
                    break;
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return super.onTouchEvent(event);
    }


    public void setOnScrollListener(AbsListView.OnScrollListener _scolllistener) {
        mGridView.setOnScrollListener(_scolllistener);
    }

    private void doSwitch() {

        mHoverCell.layout(mHoverCellCurrentBounds.left, mHoverCellCurrentBounds.top, mHoverCellCurrentBounds.right, mHoverCellCurrentBounds.bottom);
//                    invalidate();
        FrameLayout.LayoutParams params = (LayoutParams) mHoverCell.getLayoutParams();
        params.leftMargin = mHoverCellCurrentBounds.left;
        params.topMargin = mHoverCellCurrentBounds.top;
        handleCellSwitch();
//                    mIsMobileScrolling = false;

    }

    private void onTouchEnd() {
        mScollDierection = SCROLL_NONE;
        mIsCellMove = false;
        if (mHoverCell != null && mInitItemPosition < mAdapter.getCount()) {
            toTranlate(mHoverCell, mInitItemPosition);
        }
    }

    private void handleCellSwitch() {
        mMoveItemPosition = mGridView.pointToPosition(mPointMoveX, mPointMoveY);
        if (mMoveItemPosition != mInitItemPosition) {
            doMoveSwitch();
        }

    }

    protected void doMoveSwitch() {

        if (mMoveItemPosition >= mAdapter.getCount()) {
            return;
        }
        int itemNum = getViewPosition(mMoveItemPosition);
        View targetView = mGridView.getChildAt(itemNum);

        if (mMoveItemPosition >= 0 && mMoveItemPosition != mInitItemPosition && mInitItemPosition != -1) {
            targetView.setVisibility(INVISIBLE);
            insertView();
            AGridDynamic data = mAdapter.removeIndexData(mInitItemPosition);
            mAdapter.addIndexData(mMoveItemPosition, data);
            mInitItemPosition = mMoveItemPosition;
        }
    }

    private int getViewPosition(int _position) {
        int itemNum = _position - mGridView.getFirstVisiblePosition();
        int totalcount = mGridView.getChildCount();
        if (itemNum < 0) {
            itemNum = -1;
        } else if (itemNum > totalcount) {
            itemNum = -1;
        }
        return itemNum;
    }

    public int handleMobileCellScroll(Rect r) {
        int offset = mGridView.computeVerticalScrollOffset();
        int height = mGridView.getHeight();
        int extent = mGridView.computeVerticalScrollExtent();
        int range = mGridView.computeVerticalScrollRange();
        int hoverViewTop = r.top;
        int hoverHeight = r.height();

        if (hoverViewTop <= 0 && offset > 0) {
//            mGridView.smoothScrollBy(-mSmoothScrollAmountAtEdge, 0);
            mSmoothScrollAmountAtEdge = -hoverViewTop;
            return SCROLL_UP;
        }

        if (hoverViewTop + hoverHeight >= height && (offset + extent) < range) {
//            mGridView.smoothScrollBy(mSmoothScrollAmountAtEdge, 0);
            mSmoothScrollAmountAtEdge = hoverViewTop + hoverHeight - height;
            return SCROLL_DOWN;
        }

        return SCROLL_NONE;
    }

    private ItemDynamicCircle getAndAddHoverView(View _view) {
        if (_view instanceof ItemDynamicCircle) {
            int w = _view.getWidth();
            int h = _view.getHeight();
            int top = _view.getTop();
            int left = _view.getLeft();
            if (_view != null)
                _view.findViewById(R.id.item_del).setVisibility(View.VISIBLE);

            mHoverCellOriginalBounds = new Rect(left, top, left + w, top + h);
            mHoverCellCurrentBounds = new Rect(mHoverCellOriginalBounds);

            ItemDynamicCircle item = ((ItemDynamicCircle) _view);
            ItemDynamicCircle cache = item.clone();
            FrameLayout.LayoutParams params = new LayoutParams(w, h);
            params.leftMargin = left;
            params.topMargin = top;
            addView(cache, params);
//            cache.layout(mHoverCellCurrentBounds.left,mHoverCellCurrentBounds.top,mHoverCellCurrentBounds.right,mHoverCellCurrentBounds.bottom);
            mCaches.addLast(cache);
            cache.setVisibility(VISIBLE);
            return cache;
        }
        return null;
    }

    private void insertView() {
        if (mMoveItemPosition > mInitItemPosition) {
            for (int i = mInitItemPosition; i < mMoveItemPosition; i++) {
                int itemNum = getViewPosition(i);
                if (itemNum >= 0) {
                    toTranlate(itemNum, itemNum + 1);
                }
            }
        } else if (mMoveItemPosition < mInitItemPosition) {
            for (int i = mInitItemPosition - 1; i >= mMoveItemPosition; i--) {
                int itemNum = getViewPosition(i);
                if (itemNum >= 0) {
                    toTranlate(itemNum + 1, itemNum);
                }
            }
        }
    }


    public void removeData(AGridDynamic _dynamic) {
        int count = mGridView.getChildCount();
        if (_dynamic.getmPosition() - mGridView.getFirstVisiblePosition() >= count - 1) {
            mAdapter.notifyDataSetChanged();
        }
        for (int i = 0; i < count; i++) {
            ItemDynamicCircle view = (ItemDynamicCircle) mGridView.getChildAt(i);
            if (_dynamic.getmPosition() == view.getPosition()) {
                updatePosition(count, i);
            }
        }
    }

    private void updatePosition(int _total, int _index) {
        for (int i = _index; i < _total - 1; i++) {
            toTranlate(i, i + 1);
        }
    }

    private void toTranlate(int tran1, int tran2) {
        try {
            ItemDynamicCircle curview = (ItemDynamicCircle) mGridView.getChildAt(tran1);
            ItemDynamicCircle nextview = (ItemDynamicCircle) mGridView.getChildAt(tran2);
            if (curview != null && nextview != null) {
                toTranlate(nextview, curview);
            }
        } catch (Exception e) {

        }
    }

    private void toTranlate(ItemDynamicCircle paramnext, ItemDynamicCircle paramcur) {
        mRemoveCount++;
        TranslateAnimation trans = new TranslateAnimation(paramnext.getLeft() - paramcur.getLeft(), 0, paramnext.getTop() - paramcur.getTop(), 0);
        trans.setDuration(300);
        trans.setFillAfter(false);
        paramcur.startAnimation(trans);
        paramcur.exchangeView(paramnext);
//        mTranslateAnimationCache.put(trans,paramcur);
        trans.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                paramnext.exchangeView(paramcur);
//                postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        ((GridDynamicAdapter)getAdapter()).notifyDataSetChanged();
//                    }
//                },200);
//                paramcur.clearAnimation();
//                int left = paramnext.getLeft();
//                int top = paramnext.getTop();
//                int width = paramnext.getWidth();
//                int height = paramnext.getHeight();
//                mTranslateAnimationCache.remove(animation).clearAnimation();
                mRemoveCount--;
                if (mRemoveCount == 0) {
                    mAdapter.notifyDataSetChanged();
                }
//                paramcur.setVisibility(VISIBLE);
//                paramcur.layout(left, top, left+width, top+height);
//                paramcur.build(paramPre.getData(),paramPre.getPosition(),true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void toTranlate(final View _paramnext, int _position) {
        int position = getViewPosition(_position);
        if (position < 0) {
        } else {
            final View v2 = mGridView.getChildAt(position);
            TranslateAnimation trans = new TranslateAnimation(0, v2.getLeft() - mHoverCellCurrentBounds.left, 0, v2.getTop() - mHoverCellCurrentBounds.top);
            trans.setDuration(300);
            trans.setFillAfter(true);
            _paramnext.startAnimation(trans);
            trans.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    _paramnext.clearAnimation();
                    removeView(mCaches.removeFirst());
                    AGridDynamic mSelectData = mCacheDatas.removeFirst();
                    if (mSelectData != null) {
                        mSelectData.setmState(AGridDynamic.STATE_INIT);
                    }
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }
}
