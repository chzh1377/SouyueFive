package com.zhongsou.souyue.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class PairScrollView extends ViewGroup {

    public interface OnScrollListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    private static final int DIRECT_BOTTOM = 1;
    private static final int DIRECT_TOP = -1;

    private int mLastMotionY;
    private boolean mIsBeingDragged;

    private boolean mIsBeingDraggedFirst;

    private boolean mIsTouchIn;


    private boolean mIsDown;

    private boolean mTouchBeginScroll;

    private boolean mInputOut;
    private boolean mTouchEnable;
    private boolean mTouchOuterEnable;
    private boolean mTouchWholeEnable=true;

    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private OnScrollListener mOnScrollListener;

    public PairScrollView(Context context) {
        super(context);
        init();
    }

    public PairScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PairScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void init() {
        setOverScrollMode(OVER_SCROLL_NEVER);

        mScroller = new Scroller(getContext());
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }


    public boolean ismIsDown() {
        return mIsDown;
    }

    public void setmTouchEnable(boolean _enable){
        mTouchOuterEnable = _enable;
    }
    
    public void scrollToFirstView() {
//        fling(-15000);
//        scrollTo(0, 0);
        startScroll(-getScrollY());
        mTouchBeginScroll=false;
    }
    
    public void scrollToSecondView() {
//        fling(15000);
        View second = getChildAt(1);
        startScroll(second.getTop()-getScrollY());
        mTouchBeginScroll=true;
        mTouchEnable=false;
//        View second = getChildAt(1);
//        if (second != null) {
//            if (second.getBottom() > getHeight()) {
//                scrollBy(0, adjustScrollY(second.getTop()));
//            }
//        }
    }

    public void setOnScrollListener(OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        final int parentLeft = getPaddingLeft();
        final int parentTop = getPaddingTop();

        int lastBottom = parentTop;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();

                int childLeft = parentLeft + lp.leftMargin;
                int childTop = lastBottom + lp.topMargin;
                child.layout(childLeft, childTop, childLeft + width, childTop + height);
                lastBottom = childTop + height + lp.bottomMargin;
            }
        }
    }

    public void setmInputOut(boolean _inputout){
        mInputOut=_inputout;
    }
    public boolean getmInputOut(){
        return mInputOut;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(!mInputOut){
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollChanged(l, t, oldl, oldt);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public void computeScroll() {

        if (mScroller.computeScrollOffset()) {
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();

            if (oldX != x || oldY != y) {
                int dy = adjustScrollY(y - oldY);
                if (dy != 0) {
                    scrollBy(x - oldX, dy);
                    onScrollChanged(getScrollX(), getScrollY(), oldX, oldY);
                } else {
                    mScroller.forceFinished(true);
                }
            }

            if (!awakenScrollBars()) {
                ViewCompat.postInvalidateOnAnimation(this);
            }

        }else{
            mTouchEnable=true;
        }
        super.computeScroll();
    }

    public boolean getTouchAble(){
        return mIsTouchIn;
    }

    @Override
    protected int computeVerticalScrollRange() {
        View second = getChildAt(1);
        if (second != null) {
            return second.getBottom();
        }
        return super.computeVerticalScrollRange();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(!mTouchOuterEnable){
            return true;
        }
        final int action = ev.getAction();
        if(!mTouchWholeEnable){
            return true;
        }
        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(ev);


        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {

                if (!mTouchEnable) {
                    mTouchWholeEnable = false;
                    return true;
                }
                if(!mIsBeingDraggedFirst){
                    mIsBeingDragged = !mScroller.isFinished();
                }
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                mLastMotionY = (int) ev.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mIsBeingDragged) {
                    int y = (int) ev.getY(0);
                    int delta = y - mLastMotionY ;
                    int dy = adjustScrollY(-delta);
                    if (dy != 0) {
                        int oldY = getScrollY();
                        scrollBy(0, dy);
                        onScrollChanged(getScrollX(), getScrollY(), getScrollX(), oldY);
                    }else{
//                        View v = getChildAt(0);
//                        v.dispatchTouchEvent(ev);
                        mIsBeingDraggedFirst = true;
                    }

                    mLastMotionY = y;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                    if (mIsBeingDragged) {
                        mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                        int initialVelocity = (int) mVelocityTracker.getYVelocity(0);
                        if ((Math.abs(initialVelocity) > mMinimumVelocity)) {
                            fling(-initialVelocity);
                        }

                        recycleVelocityTracker();
                    }
                View first = getChildAt(1);
                View second = getChildAt(1);

                if (mTouchBeginScroll&&touchInView(second, ev)) {
                    mTouchBeginScroll=false;

                    if(first.getHeight()-getScrollY()<150){
//                                fling(2000);
                        startScroll(2000);
                    }else{
//                                fling(-8000);
                        mTouchEnable=false;
                        startScroll(-getScrollY());
                    }
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
                recycleVelocityTracker();
                break;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int ac = ev.getAction();
        switch (ac&MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                mIsTouchIn=true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsTouchIn=false;
                break;
        }
        requestDisallowInterceptTouchEvent(false);
        if(mIsBeingDraggedFirst){

            ac&=~MotionEvent.ACTION_MASK;
            ev.setAction(ac|MotionEvent.ACTION_DOWN);
        }
        boolean touch = false;
        try {
           touch =  super.dispatchTouchEvent(ev);
            if(mIsBeingDraggedFirst){
                mIsBeingDraggedFirst=false;
            }
        }catch (Exception e){

        }
        return touch;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(!mTouchOuterEnable){
            return true;
        }
        if(!mTouchEnable){
            return true;
        }
        final int action = ev.getAction();
        if(!mTouchWholeEnable){
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    if (mTouchEnable) {
                        mTouchWholeEnable = true;
                    }
                }
                break;
                default:
                    return true;
            }
        }
        final int childCount = getChildCount();
        if (childCount < 2) {
            return false;
        }

        View first = getChildAt(0);
        View second = getChildAt(1);

        if (!touchInView(first, ev) && !touchInView(second, ev)) {
            return false;
        }



        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                if(mIsDown){
                    mTouchBeginScroll=true;
                }
                int y = (int) ev.getY();
                mLastMotionY = y;
                initOrResetVelocityTracker();
                mVelocityTracker.addMovement(ev);

                // 在Fling状态下点击屏幕
                if(!mIsBeingDraggedFirst){
                    mIsBeingDragged = !mScroller.isFinished();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int y = (int) ev.getY();
                int deltaY = y - mLastMotionY;
                int distance = Math.abs(deltaY);

                if (distance > mTouchSlop) {

                    initVelocityTrackerIfNotExists();
                    mVelocityTracker.addMovement(ev);

                    if (deltaY < 0) { // Scroll To Bottom
                        if (touchInView(first, ev)) {
                            // 第一个View不可以继续向下滚动，否则由这个View自己处理View内的滚动
                            if (!first.canScrollVertically(DIRECT_BOTTOM)||getScrollY()!=0) {
                                if (canScrollVertically(DIRECT_BOTTOM)) {
                                    mLastMotionY = (int) ev.getY();
                                    mIsBeingDragged = true;
                                }
                            }
                        } else if (touchInView(second, ev)) { // 触摸点在第二个View
                            if (canScrollVertically(DIRECT_BOTTOM)) {
                                mIsBeingDragged = true;
                            }
                        } else {
                            mIsBeingDragged = false;
                            mLastMotionY = y;
                        }
                    } else if (deltaY > 0) { // Scroll To Top
                        if (touchInView(first, ev)) {
                            if (canScrollVertically(DIRECT_TOP)) {
                                mIsBeingDragged = true;
                            }
                        } else if (touchInView(second, ev)) {
                            if (!second.canScrollVertically(DIRECT_TOP)) {
                                if (canScrollVertically(DIRECT_TOP)) {
                                    mLastMotionY = y;
                                    mIsBeingDragged = true;
                                }
                            }
                        } else {
                            mIsBeingDragged = false;
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
//                mIsBeingDragged = false;
                recycleVelocityTracker();
                break;
            }
        }
        return mIsBeingDragged;
    }

    private int adjustScrollY(int delta) {
        int dy = 0;
        int distance = Math.abs(delta);
        if (delta > 0) { // Scroll To Bottom
//            View first = getChildAt(0);
            View second = getChildAt(1);
            if (second != null) {
//                int middlepadding = second.getTop()-first.getBottom();
                int max = second.getTop() - getScrollY(); // 最多滚动到第二个View的顶部和Container顶部对齐
                max = Math.min(max, second.getBottom() - getScrollY() - getTop()); // 最多滚动到第二个View的底部和Container对齐
                dy = Math.min(max, distance);
                if(dy==0){
                    mIsDown=true;
                }
            }
        } else if (delta < 0) { // Scroll To Top
            dy = -Math.min(distance, getScrollY());
//            dy = -distance;
            mIsDown=false;
        }
        return dy;
    }

    private void fling(int velocity) {
        mScroller.fling(getScrollX(), getScrollY(), 0, velocity, 0, computeHorizontalScrollRange(), 0,
                computeVerticalScrollRange());
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void startScroll(int position){
        mScroller.startScroll(getScrollX(),getScrollY(),0,position,500);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private boolean touchInView(View child, MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        final int scrollY = getScrollY();
        return !(y < child.getTop() - scrollY
                || y >= child.getBottom() - scrollY
                || x < child.getLeft()
                || x >= child.getRight());
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            recycleVelocityTracker();
        }
        // 禁用掉此功能，当ChildView是ListView时，ListView会通过此方法禁止ParentView拦截事件，
        // 而且ListView的onTouchEvent永远返回true，结果就是，如果ListView是第二个ChildView，
        // 当ListView拉到顶后父控件无法拦截事件，这样父控件无法继续往上滚动。
        // 如果这是可接受的，打以打开这条语句。
         super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }
}
