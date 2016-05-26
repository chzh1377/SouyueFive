package com.zhongsou.souyue.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Scroller;
import com.zhongsou.souyue.utils.DeviceUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *首页球球类
 *
 */
public class CirlTitleView extends ViewGroup{
	
	public static final int POINT_DEFAULT=1;
	public static final int POINT_MOVE_X=2;
	public static final int POINT_MOVE_Y=3;
	public static final int POINT_AUTO=4;
	public static final int POINT_RECOVER=5;
	public static final int POINT_UP=6;
	public static final int POINT_PRESS=7;

	public static final int STATE_DEFAULT=0;
	public static final int STATE_LEFT=1;
	public static final int STATE_RIGHT=2;

	public static final long LONGPRESS_TIME = 500; // 长按时间的范围，大于500认为是长按，否则认为是短按

	public static final int POSITION_TAG=11;




	public static float SPEED_ACCELARATE=3f;//dp为单位
	public static float SPEED_RECOVER=5f;//dp为单位

	public static float BALL_CELL_WIDTH = 62.F;
	

	public static final float SCALE_BASE=0.5F;
    private final int mMoveOffset;


	protected int mState;//当前手指按下状态
	private float mLastTouchLeft;//手指按下x坐标
	private float mLastTouchUp;//手指按下y坐标
	protected float mCurrentMoveX;
	protected float mMoveX;
	protected float mPreMoveX;
	protected float mCurVelocity;//当前速度每帧

	
	protected float mSingleViewWidth;

	protected float mFloatA=4;
	
	
	protected int mCurmiddleIndex;
	protected int mPremiddleIndex;
	
	protected float mMaxViewWidth;
	protected int mFirstIndex;
	protected float mFirstIndexOffset;
	protected int mPreViewIndex;
	
	protected int mScrollState;
	protected float mDeterminePos;
	protected int mScreenWidth;

	protected float mScreenPos;
	
	protected int mLeftCount;
	
	
	private OnItemClickListener mOnItemClickListener;
	private OnItemChanged mItemChanged;
	private OnItemChangedStop mItemChangedStop;
	private OnScrollListener mScrollListener;
	private Map<View,Integer> viewpositions;
	
	private int mCurrentStopIndex;
	
	
	private int mTotalCount;
//	private int mPostCount=0;//引用计数
	
	private int mShowCount;
	private BaseAdapter mAdapter;
	private VelocityTracker mVelocityTracker;

	private int mMiddleStopIndex;
	private int mMiddleStopState;
	private float mMiddleStopOffset;


	
	//modify
	protected int mWidth;
	protected int mHeight;
	protected int mLeft;
	protected int mRight;
	protected int mTop;
	protected int mBottom;
    
    private Scroller mScroller;
	private boolean mTouchMoved;

	private boolean mSelected;

	private boolean mTouchEnable = true;



	public CirlTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		final String xmlns="http://schemas.android.com/apk/res/android";

		mScroller = new Scroller(context);
		mMoveOffset = DeviceUtil.dip2px(context, 15);
//		mShowView = new ArrayList<View>();
		DisplayMetrics me = getContext().getResources().getDisplayMetrics();
		mWidth = (int) (attrs.getAttributeIntValue(xmlns, "layout_width", (int) BALL_CELL_WIDTH)*me.density);
		mHeight = (int) (attrs.getAttributeIntValue(xmlns, "layout_width", (int) BALL_CELL_WIDTH)*me.density);
		viewpositions = new HashMap<View, Integer>();
		mScreenWidth=me.widthPixels;
		SPEED_RECOVER = me.density*SPEED_RECOVER;
		SPEED_ACCELARATE = me.density*SPEED_ACCELARATE;
		mSingleViewWidth = me.density*BALL_CELL_WIDTH;
		mScrollState = OnScrollListener.SCROLL_STATE_IDLE;
	}

	public interface OnScrollListener{
		/**
		 * The view is not scrolling. Note navigating the list using the trackball counts as
		 * being in the idle state since these transitions are not animated.
		 */
		public static int SCROLL_STATE_IDLE = 0;

		/**
		 * The user is scrolling using touch, and their finger is still on the screen
		 */
		public static int SCROLL_STATE_TOUCH_SCROLL = 1;

		/**
		 * The user had previously been scrolling using touch and had performed a fling. The
		 * animation is now coasting to a stop
		 */
		public static int SCROLL_STATE_FLING = 2;

		void onScrollChange(int state);

	}

	protected void recoverMiddle(){
//		float left = (-mMoveX+mScreenWidth/2.f)/mSingleViewWidth;

		int pos = (int)((-mMoveX+mScreenWidth/2.f)/mSingleViewWidth);
		if (pos>=mTotalCount-mLeftCount){
			pos = mTotalCount-mLeftCount-1;

		}else if (pos <mLeftCount){
			pos = mLeftCount;
		}
		smoothScrollTo(pos * mSingleViewWidth + mSingleViewWidth / 2.f - mScreenWidth / 2.f);
	}
	
	private void showView(){

		if(getWidth()-mMaxViewWidth>0){//暂时不做橡皮筋效果
			mMoveX=0;
			mScroller.forceFinished(true);
		}else if(mMoveX>0){
			mScroller.forceFinished(true);
			mMoveX=0;
		}else if(mMoveX<getWidth()-mMaxViewWidth){
			mMoveX = getWidth()-mMaxViewWidth;
			mScroller.forceFinished(true);
		}
		if((mMoveX-mScreenWidth/2)%mSingleViewWidth<0.01){
			mCurmiddleIndex = Math.abs((int)((mMoveX-mScreenWidth/2)/mSingleViewWidth));
			if (mCurmiddleIndex>=mTotalCount-1){
				mCurmiddleIndex=mTotalCount-2;
			}
			if(mCurmiddleIndex!=mPremiddleIndex){
				if(mItemChanged!=null){
					mItemChanged.onChangedMiddlePosition(mCurmiddleIndex);
				}
				mPremiddleIndex = mCurmiddleIndex;
			}
		}

		mScreenPos = -mMoveX+mScreenWidth/2.0f;
		mFirstIndex = (int) (-mMoveX/mSingleViewWidth);//当前移动了多少个
		mFirstIndexOffset = mMoveX%mSingleViewWidth;
		if(mPreViewIndex>mFirstIndex){//向左移动
			removeLastView();
		}else if(mPreViewIndex<mFirstIndex){
			removeFrontView();
		}
		mPreViewIndex=mFirstIndex;
		//从index开始往后循环一次找view来作为当前这个绘制的开始view
		scrollTo((int) -mFirstIndexOffset,0);

		for(int i = 0;i<mShowCount;i++){
			View v = getChildAt(i);
			int left = (int) (mFirstIndexOffset+i*mSingleViewWidth);
//			float Scale =(1-Math.abs(Math.abs(left+mSingleViewWidth/2.0f)-mScreenWidth/2.0f)/(mScreenWidth/2.0f))*(1-SCALE_BASE)+SCALE_BASE;
			float point = Math.abs(Math.abs(left+mSingleViewWidth/2.0f)-mScreenWidth/2.0f);
			float Scale = mFloatA/(mFloatA+point);//不做线性哦，用反函
//			MarginLayoutParams st = (MarginLayoutParams) v.getLayoutParams();
//			st.width = (int) (mSingleViewWidth*Scale);
			((ItemHView)v).setmScale(Scale);
//			//在这里设置缩放
		}
	}

	private void removeFrontView(){
		for(int i =mPreViewIndex;i<mFirstIndex;i++){
			View view = getChildAt(0);
			removeViewAt(0);
			if (i+mShowCount>=mTotalCount){
				addView(view, mShowCount - 1);
			}else {
				View v = mAdapter.getView(i + mShowCount, view, this);
				viewpositions.put(v,i + mShowCount);
				addView(v, mShowCount - 1);
			}
		}

		int count = getChildCount();
		for (int i=0;i<count;i++){
			View v = getChildAt(i);
			MarginLayoutParams params = (MarginLayoutParams) v.getLayoutParams();
			params.leftMargin = (int) (i*mSingleViewWidth);
		}
	}

	private void removeLastView(){
		for(int i =mPreViewIndex-1;i>=mFirstIndex;i--){
			View view = getChildAt(mShowCount-1);
			removeViewAt(mShowCount-1);
			if (i<0){
				addView(view, 0);
			}else {
				View v = mAdapter.getView(i, view, this);
				viewpositions.put(v,i);
				addView(v, 0);
			}
		}

		int count = getChildCount();
		for (int i=0;i<count;i++){
			View v = getChildAt(i);
			MarginLayoutParams params = (MarginLayoutParams) v.getLayoutParams();
			params.leftMargin = (int) (i*mSingleViewWidth);
		}
	}

	/**
	 * 设置球球在快速滑动时要突然停止的位置
	 * @param _index
	 */
	public void setMiddleStopIndex(int _index){
		mMiddleStopIndex = _index;
		mMiddleStopOffset = -(_index*mSingleViewWidth+mSingleViewWidth/2)+mScreenWidth/2.0f;
	}

	/**
	 * 获得当前球球快速滑动时突然停止的位置
	 * @return
	 */
	public int getmMiddleStopIndex(){
		return mMiddleStopIndex;
	}
	private void initView() {
		int count =mAdapter.getCount();
		View v = mAdapter.getView(0, null, this);
		viewpositions.put(v,0);
		mWidth=getScreenWidth();
		if(mWidth/mSingleViewWidth+2>count){
			mShowCount = count;
		}else{
			mShowCount = (int) (mWidth/mSingleViewWidth+2);
		}

		MarginLayoutParams params0 = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		addView(v,params0);
		for(int i=1;i<mShowCount;i++){
			View vi = mAdapter.getView(i, null, this);
			viewpositions.put(vi,i);
			MarginLayoutParams params = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.leftMargin = (int) (i*mSingleViewWidth);
			float point = Math.abs(Math.abs(params.leftMargin+mSingleViewWidth/2.0f)-mScreenWidth/2.0f);
			float Scale = mFloatA/(mFloatA+point);
//            float Scale =(1-point/(mScreenWidth/2.0f))*(1-SiCALE_BASE)+SCALE_BASE;
//			MarginLayoutParams st = (MarginLayoutParams) v.getLayoutParams();
//			st.width = (int) (mSingleViewWidth*Scale);
            ((ItemHView)vi).setmScale(Scale);
			addView(vi,params);
		}
	}
	
	/** DataSetObserver used to capture adapter data change events */
    private DataSetObserver mAdapterDataObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
//
//            // Clear so we can notify again as we run out of data
//            mHasNotifiedRunningLowOnData = false;
//

            // Invalidate and request layout to force this view to completely redraw itself
//        	refreshData();
//			showView();
			mTotalCount =mAdapter.getCount();
			mMaxViewWidth = mSingleViewWidth*mTotalCount;
//            invalidate();
//            requestLayout();
			refreshView();
        }



        @Override
        public void onInvalidated() {
            // Clear so we can notify again as we run out of data
//            mHasNotifiedRunningLowOnData = false;
//
//            unpressTouchedChild();
//            reset();

            // Invalidate and request layout to force this view to completely redraw itself
//        	refreshData();
            invalidate();
            requestLayout();
        }
    };

	void refreshView(){
		int count = getChildCount();
		for (int i =0;i< count; i++){
			View v = getChildAt(i);
			int pos = viewpositions.get(v);
			v = mAdapter.getView(pos,v,this);
			viewpositions.put(v,pos);
//			addView(v,i);
			v.invalidate();
		}
	}
    
    protected void refreshData(){
		clearView();
		initView();
    }

	private void clearView(){
		removeAllViews();
	}

	@Override
	public void computeScroll() {
		if (mSelected){
			return;
		}
		if (mScroller.computeScrollOffset()){
			mPreMoveX = mMoveX;
			mMoveX = mScroller.getCurrX();
			mCurVelocity=mMoveX-mPreMoveX;
			if (mState==POINT_AUTO) {
				if (mCurVelocity > 0&&mMiddleStopState==STATE_LEFT) {
					if (mMoveX > mMiddleStopOffset) {
						mMiddleStopState=STATE_DEFAULT;
						mMoveX = mMiddleStopOffset;
						mScroller.abortAnimation();
					}
				}
				if (mCurVelocity < 0&&mMiddleStopState==STATE_RIGHT) {
					if (mMoveX < mMiddleStopOffset) {
						mMiddleStopState=STATE_DEFAULT;
						mMoveX = mMiddleStopOffset;
						mScroller.abortAnimation();
					}
				}
			}
			if (Build.VERSION.SDK_INT> Build.VERSION_CODES.JELLY_BEAN) {
				postOnAnimation(new Runnable() {
					@Override
					public void run() {
						showView();
					}
				});
			}else {
				post(new Runnable() {
					@Override
					public void run() {
						showView();
					}
				});
			}
			ViewCompat.postInvalidateOnAnimation(this);
		}else{
			if (mState==POINT_AUTO) {
				recoverMiddle();
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int curAction = ev.getActionMasked();
		if (!mTouchEnable){
			return false;
		}
		if(mState==POINT_MOVE_X){
			return true;
		}
		float x = ev.getX();
		float y = ev.getY();
		switch (curAction&MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				mState=POINT_PRESS;
				mSelected=false;
				break;
			case MotionEvent.ACTION_MOVE:
				float xDiff = Math.abs(x - mLastTouchLeft);
				float yDiff = Math.abs(y - mLastTouchUp);
				if (xDiff>mMoveOffset||yDiff>mMoveOffset){
					mState=POINT_MOVE_X;
					mTouchMoved = true;
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				if (mState==POINT_PRESS){
					mTouchMoved=true;
				}else if (mState==POINT_MOVE_X){
					mTouchMoved=true;
				}
				break;
		}
		return mTouchMoved;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int curAction = event.getActionMasked();
		if (!mTouchEnable||mAdapter == null){
			return false;
		}
		if(mVelocityTracker == null) {
			// Retrieve a new VelocityTracker object to watch the velocity of a motion.
			mVelocityTracker = VelocityTracker.obtain();
		}

		float x=event.getX();
		float y=event.getY();
		mVelocityTracker.addMovement(event);
		switch (curAction&MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mIsLongClick = false; //一开始没法判断是否是长按，所以是false
			if(!mScroller.isFinished()){
				mScroller.abortAnimation();
			}
			mSelected=false;
			mState = POINT_PRESS;
			mMiddleStopState = STATE_DEFAULT;
			mCurrentMoveX = mMoveX;
			mLastTouchLeft = event.getX();
			mLastTouchUp = event.getY();
			//记录开始时间
            mLongClickStartTime=-1;
			if(mLongClickStartTime == -1){
				mLongClickStartTime = SystemClock.uptimeMillis();
                float curpos = x-mMoveX;
                final int position = (int) (curpos/mSingleViewWidth);
                final View view = getChildAt((int) (x/mSingleViewWidth));
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //触发长按事件
                        if(mOnItemLongClickListener!=null&&mState == POINT_PRESS&&!mIsLongClick){
                            Log.v(this.getClass().getName(),"长按---------------------");
                            mOnItemLongClickListener.onItemLongClick(mAdapter, view, position, mAdapter.getItemId(position));
                            mLongClickStartTime = -1; //开始事件归零
                            mIsLongClick = true;//此时可以确定是长按事件了。
                        }
                    }
                },LONGPRESS_TIME);
            }

            break;
		case MotionEvent.ACTION_MOVE:
			float xDiff = Math.abs(x - mLastTouchLeft);
			float yDiff = Math.abs(y - mLastTouchUp);
			if (xDiff>mMoveOffset||yDiff>mMoveOffset){
				mState=POINT_MOVE_X;
				if (mScrollListener !=null&&mScrollState!=OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					mScrollListener.onScrollChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
				}
				mScrollState = OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
			}
			if (mState == POINT_MOVE_X && !mIsLongClick) {
				float dx = event.getX(0) - mLastTouchLeft;
				scrollBy(dx);
			}
			//每次用完之后，都将长按的开始时间置为-1

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mLongClickStartTime = -1; //不到时间抬起，时间要归零
			mSelected=false;
			if(mState == POINT_PRESS){
				if(mOnItemClickListener!=null && !mIsLongClick){ //增加判断条件，如果不是长按才会执行这里
					Log.v(VIEW_LOG_TAG, "点击事件");
					float curpos = x-mMoveX;
					int position = (int) (curpos/mSingleViewWidth);
					View view = getChildAt((int) (x/mSingleViewWidth));
					if (mOnItemClickListener!=null) {
						boolean isfling = mOnItemClickListener.onItemClick(mAdapter, view, position, mAdapter.getItemId(position));
						if (!isfling) {
							mState = POINT_AUTO;
							ViewCompat.postInvalidateOnAnimation(this);
						}
					}
				}else{
					mState = POINT_AUTO;
					ViewCompat.postInvalidateOnAnimation(this);
				}
			}else{
				final VelocityTracker velocityTracker = mVelocityTracker  ;
				velocityTracker.computeCurrentVelocity(1000);
				int velocityX = (int) velocityTracker.getXVelocity() ;
				if(mMoveX<mMiddleStopOffset){
					mMiddleStopState=STATE_LEFT;
				}else if (mMoveX>mMiddleStopOffset){
					mMiddleStopState=STATE_RIGHT;
				}
				if (mScrollListener !=null&&mScrollState != OnScrollListener.SCROLL_STATE_FLING) {
					mScrollListener.onScrollChange(OnScrollListener.SCROLL_STATE_FLING);
				}
				mScrollState = OnScrollListener.SCROLL_STATE_FLING;
				mScroller.fling((int) mMoveX, 0, velocityX, 0, -((int) getMaxWidth() + 2 * mWidth), (int) (2 * mWidth), 0, 0);
				mState=POINT_AUTO;
				ViewCompat.postInvalidateOnAnimation(this);
			}

			mVelocityTracker.recycle();
			mVelocityTracker=null;
			break;
		default:
			break;
		}
		return true;
	}
	
	
//	private float getDistance(float[] p1,float[] p2){
//		return (float) Math.sqrt((p1[0]-p2[0])*(p1[0]-p2[0])+(p1[1]-p2[1])*(p1[1]-p2[1]));
//	}

	/**
	 * 获得当前中间的球球
	 * @return
	 */
	public int getCurMiddleIndex(){
		int curmiddle = (int) ((mScreenWidth/2.f-mMoveX)/mSingleViewWidth);

		return curmiddle;
	}

	private float getMaxWidth(){
		return mTotalCount*mSingleViewWidth-mWidth;
	}

	/**
	 * 滑动到某个位置
	 * @param xOffset
	 */
	public void scrollBy(float xOffset){
		mMoveX=mCurrentMoveX+xOffset;
		showView();
	}


	/**
	 * 设置球球点击事件
	 * @param listener
	 */
	public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

	/**
	 * 设置球球在滑动过程中的回调
	 * @param listener
	 */
	public void setOnItemChange(OnItemChanged listener){
		mItemChanged = listener;
	}

	/**
	 * 设置球球在滑动后最后停止位置的回调
	 * @param listener
	 */
	public void setOnItemChangedStop(OnItemChangedStop listener){
		mItemChangedStop = listener;
	}

	/**
	 * 设置球球滑动状态改变回调
	 * @param listener
	 */
	public void setOnScrollListener(OnScrollListener listener){
		mScrollListener = listener;
	}

	/**
	 * 设置球球的适配器
	 * @param _adapter
	 */
	public void setAdapter(BaseAdapter _adapter){

		if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mAdapterDataObserver);
        }

        if (_adapter != null) {
//            // Clear so we can notify again as we run out of data
            mAdapter = _adapter;
            mAdapter.registerDataSetObserver(mAdapterDataObserver);
        }
        removeAllViews();
        mTotalCount =mAdapter.getCount();
//        mSingleViewWidth = mAdapter.getItemSize()[0];
		mMaxViewWidth = mSingleViewWidth*mTotalCount;
		mFirstIndex =0;
		mPreViewIndex=0;
		mMoveX=0;
		mLeftCount = getLeftcount();
		initView();
	}

	/**
	 * 获得左边
	 * @return
	 */
	public int getLeftcount(){
		float left = mScreenWidth/2/mSingleViewWidth;
		mLeftCount = (int)(left+0.5f);
		mFloatA = mScreenWidth/mLeftCount*1.5f;
		return mLeftCount;
	}
	
	public int getScreenWidth(){
		return mScreenWidth;
	}
	/**
	 * 
	 * @param index 这里的index是第几个，特殊处理时按照中间来的！
	 */
	public void setCurrentMiddleItem(int index){
		if(index<mLeftCount){
			Log.e("出错", "你设定的中间位置超出界限");
		}else if(index>mTotalCount-mLeftCount){
			Log.e("出错", "你设定的中间位置超出界限");
		}
//		float pos = index*mSingleViewWidth-mScreenWidth/2.f+mSingleViewWidth/2.f;
		mMoveX = index*mSingleViewWidth-mScreenWidth/2.0f+mSingleViewWidth/2.0f;
		mMoveX = -mMoveX;
		mDeterminePos = mMoveX;
		showView();
		if (mItemChangedStop != null) {
			mItemChangedStop.onChangedMiddlePositionStop(index);
		}

		mState=POINT_AUTO;
//		post(new Runnable() {
//			@Override
//			public void run() {
//				requestLayout();
//			}
//		});
	}
	/**
	 * 
	 * @param pos 位置，即需要滚动的位置
	 */
	public void smoothScrollTo(float pos){
		mDeterminePos = pos;
		if(mDeterminePos<0){
			mDeterminePos=0;
		}else if(mDeterminePos>mMaxViewWidth-mScreenWidth){
			mDeterminePos = mMaxViewWidth-mScreenWidth;
		}
		mDeterminePos = -mDeterminePos;
//
		mState=POINT_AUTO;
		if (!mSelected&&Math.abs(mMoveX-mDeterminePos)<1){
				if(Math.abs(mCurrentStopIndex+(mDeterminePos+mScreenWidth/2.f)/mSingleViewWidth)>0.1) {
					mCurrentStopIndex = (int) ((-mDeterminePos + mScreenWidth / 2.f) / mSingleViewWidth);
					if (mItemChangedStop != null) {
						mItemChangedStop.onChangedMiddlePositionStop(mCurrentStopIndex);
					}
				}
			if (mScrollListener!=null&&mScrollState != OnScrollListener.SCROLL_STATE_IDLE){
				mScrollListener.onScrollChange(OnScrollListener.SCROLL_STATE_IDLE);
			}
			mScrollState = OnScrollListener.SCROLL_STATE_IDLE;
			mState=POINT_DEFAULT;
			return;
		}
		mScroller.startScroll((int)mMoveX,0, (int) (mDeterminePos-mMoveX),0,500);
		ViewCompat.postInvalidateOnAnimation(this);
	}

	/**
	 * 将某个位置移动到中间
	 * @param index
	 */
	public void smoothScrollToMiddleIndex(int index){
		float pos = index*mSingleViewWidth-mScreenWidth/2.f+mSingleViewWidth/2.f;
		mSelected=false;
		smoothScrollTo(pos);
	}

	/**
	 * 平滑滑动到某个位置上
	 * @param select 滑动过程中是否可以点击
	 * @param index 平滑滑动位置
	 * @param offset 平滑滑动百分比
	 */
	public void smoothScrollTo(boolean select,int index,float offset){
		mTouchEnable = select;
		mSelected=true;
		int ind = mLeftCount+index;
		mMoveX=-(ind*mSingleViewWidth+mSingleViewWidth/2-mWidth/2+offset*mSingleViewWidth);

		this.post(new Runnable() {
			@Override
			public void run() {
				showView();
			}
		});
	}
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int measureWidth = measureWidth(widthMeasureSpec);  
        int measureHeight = measureHeight(heightMeasureSpec);  
        mWidth = measureWidth;
        mHeight = measureHeight;

		measureChildren(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(measureWidth, measureHeight);

	}

    private int measureWidth(int pWidthMeasureSpec) {  
        int result = 0;  
        int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式  
        int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸  
  
        switch (widthMode) {  
        /** 
         * mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY, 
         * MeasureSpec.AT_MOST。 
         *  
         *  
         * MeasureSpec.EXACTLY是精确尺寸， 
         * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid 
         * :layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。 
         *  
         *  
         * MeasureSpec.AT_MOST是最大尺寸， 
         * 当控件的layout_width或layout_height指定为WRAP_CONTENT时 
         * ，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可 
         * 。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。 
         *  
         *  
         * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView， 
         * 通过measure方法传入的模式。 
         */
        case MeasureSpec.AT_MOST:  
        case MeasureSpec.EXACTLY:  
            result = widthSize;  
            break;  
        }  
        return result;  
    }  
  
    private int measureHeight(int pHeightMeasureSpec) {  
        int result = 0;  
  
        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);  
        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);  
  
        switch (heightMode) {  
        case MeasureSpec.AT_MOST:  
        case MeasureSpec.EXACTLY:  
            result = heightSize;  
            break;  
        }  
        return result;  
    }  
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (mLeft != l || mRight != r || mTop != t || mBottom != b) {
			mLeft = l;
			mRight = r;
			mTop = t;
			mBottom = b;
		}
		int count = getChildCount();

		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				MarginLayoutParams st =
						(MarginLayoutParams) child.getLayoutParams();
				child.layout(l+st.leftMargin, t+st.topMargin, l+st.leftMargin+(int) mSingleViewWidth, b);
			}
		}
	}
	//长按事件的开始事件
	long mLongClickStartTime = 0;
	//长按的响应事件
	private boolean mIsLongClick = false; //此次事件是否是长按事件。
	//会将这个标记置为true.表示这次的事件不是长按事件.
	private onItemLongClickListener mOnItemLongClickListener;

	/**
	 * 设置长按点击事件
	 * @param mOnItemLongClickListener
	 */
	public void setOnItemLongClickListener(onItemLongClickListener mOnItemLongClickListener){
		this.mOnItemLongClickListener = mOnItemLongClickListener;
	}

    @Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}
	
	
	public interface OnItemChangedStop{
		void onChangedMiddlePositionStop(int _position);
	}
	
	public interface OnItemChanged{
		void onChangedMiddlePosition(int _position);
	}
	
	public interface OnItemClickListener{
		boolean onItemClick(BaseAdapter mAdapter, View view, int position, long id);
	}

	//长按的接口
	public interface  onItemLongClickListener{
		void onItemLongClick(BaseAdapter mAdapter, View view, int position, long id);
	}
	
}
