package com.zhongsou.souyue.ui;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.Toast;
import com.zhongsou.souyue.utils.LogDebugUtil;

/**
 * @author fan 带边界提示
 */
public class ViewPagerWithTips extends ViewPager {
    public interface OnEndListener {
        void onEndListener();
    }

    public interface OnBeginListener {
        void onBeginListener();
    }

    Toast toast;
    private OnEndListener endListener;
    private OnBeginListener beginListener;
    private float fLastMotionX;
    private float fInitialMotionX;
    private float fInitialMotionY;
    private float fLastMotionY;
    private int fActivePointerId;
    private boolean fIsBeingDragged;
    private int fTouchSlop;
    private int fMinimumVelocity;
    private int fMaximumVelocity;
    private VelocityTracker fVelocityTracker;
    private static final int FMIN_FLING_VELOCITY = 400; // dips
    private static final int FINVALID_POINTER = -1;
    private boolean mIsInEdit;
    public ViewPagerWithTips(Context context) {
        super(context);
        initParam();
    }

    public ViewPagerWithTips(Context context, AttributeSet attrs) {
        super(context, attrs);
        initParam();
    }
    public void setmIsInEdit(boolean isInEdit){
        mIsInEdit=isInEdit;
    }
    public void initParam() {
        final Context context = getContext();
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        final float density = context.getResources().getDisplayMetrics().density;

        fTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        fMinimumVelocity = (int) (FMIN_FLING_VELOCITY * density);
        fMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mIsInEdit){
            return false;
        }
        
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (toast != null)
                toast.cancel();
        }

        if (ev.getPointerCount() > 1) {
            ev.setAction(MotionEvent.ACTION_CANCEL);
        }

        checkOnEdge(ev);
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
        
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(mIsInEdit){
            return false;
        }
        checkOnEdge(ev);
        return super.onTouchEvent(ev);
    }

    private void checkOnEdge(MotionEvent ev) {
        try {
//		LogDebugUtil.v("fan", "checkOnEdge");
            PagerAdapter tempAdapter = getAdapter();
            if (tempAdapter == null || tempAdapter.getCount() == 0)
                return;

            final int action = ev.getAction();
            switch (action & MotionEventCompat.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    // LogDebugUtil.v("fan", "ACTION_DOWN");
                    // Remember where the motion event started
                    fLastMotionX = fInitialMotionX = ev.getX();
                    fLastMotionY = fInitialMotionY = ev.getY();
                    fActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                    break;
                }
                case MotionEvent.ACTION_MOVE:
                    // LogDebugUtil.v("fan", "ACTION_MOVE");
                    if (!fIsBeingDragged) {
                        final int pointerIndex = MotionEventCompat.findPointerIndex(ev, fActivePointerId);
                        final float x = MotionEventCompat.getX(ev, pointerIndex);
                        final float xDiff = Math.abs(x - fLastMotionX);
                        final float y = MotionEventCompat.getY(ev, pointerIndex);
                        final float yDiff = Math.abs(y - fLastMotionY);
                        if (xDiff > fTouchSlop && xDiff > yDiff) {
                            fIsBeingDragged = true;
                            fLastMotionX = x - fInitialMotionX > 0 ? fInitialMotionX + fTouchSlop
                                    : fInitialMotionX - fTouchSlop;
                            fLastMotionY = y;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    // LogDebugUtil.v("fan", "ACTION_UP");
                    if (fIsBeingDragged) {
                        final VelocityTracker velocityTracker = fVelocityTracker;
                        velocityTracker.computeCurrentVelocity(1000, fMaximumVelocity);
                        int initialVelocity = (int) VelocityTrackerCompat.getXVelocity(velocityTracker,
                                fActivePointerId);
                        final int activePointerIndex = MotionEventCompat.findPointerIndex(ev,
                                fActivePointerId);
                        final float x = MotionEventCompat.getX(ev, activePointerIndex);
                        final int totalDelta = (int) (x - fInitialMotionX);
                        fActivePointerId = FINVALID_POINTER;

                        boolean isFling = Math.abs(initialVelocity) > fMinimumVelocity;
                        boolean isScroll = Math.abs(totalDelta) > getWidth() / 3;
                        LogDebugUtil.v("fan", "width-----v=" + getWidth() / 3);
                        LogDebugUtil.v("fan", "start-----v=" + isFling + "----del=" + totalDelta + "---fTouchSlop=" + fTouchSlop);

                        int mc = getCurrentItem();
                        int t = tempAdapter.getCount();

                        boolean isScrollBegin = (isFling && initialVelocity > 0) || (isScroll && totalDelta > 0);// 从左往右滑
                        if (isScrollBegin && mc == 0) {
                            if (beginListener != null) {
                                beginListener.onBeginListener();
                            }
                        }

                        boolean isScrollEnd = (isFling && initialVelocity < 0) || (isScroll && totalDelta < 0);
                        if (isScrollEnd && (mc + 1) == t) {
                            if (endListener != null) {
                                endListener.onEndListener();
                            }
                            //                    else {
                            //						if (toast == null)
                            //							toast = Toast.makeText(getContext(), "已经是最后一页了", 1000);
                            //						toast.show();
                            //					}
                        }
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (fIsBeingDragged) {
                        fActivePointerId = FINVALID_POINTER;
                    }
                    break;
                case MotionEventCompat.ACTION_POINTER_DOWN: {
                    final int index = MotionEventCompat.getActionIndex(ev);
                    final float x = MotionEventCompat.getX(ev, index);
                    fLastMotionX = x;
                    fActivePointerId = MotionEventCompat.getPointerId(ev, index);
                    break;
                }
                case MotionEventCompat.ACTION_POINTER_UP:
                    onSecondaryPointerUp(ev);
                    fLastMotionX = MotionEventCompat.getX(ev,
                            MotionEventCompat.findPointerIndex(ev, fActivePointerId));
                    break;
            }
            if (fVelocityTracker == null) {
                fVelocityTracker = VelocityTracker.obtain();
            }
            fVelocityTracker.addMovement(ev);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == fActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            fLastMotionX = MotionEventCompat.getX(ev, newPointerIndex);
            fActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
            if (fVelocityTracker != null) {
                fVelocityTracker.clear();
            }
        }
    }

    public void setOnEndListener(OnEndListener endListener) {
        this.endListener = endListener;
    }

    public void setOnBeginListener(OnBeginListener beginListener) {
        this.beginListener = beginListener;
    }
}
