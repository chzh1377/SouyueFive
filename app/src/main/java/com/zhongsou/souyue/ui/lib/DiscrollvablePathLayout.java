package com.zhongsou.souyue.ui.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.zhongsou.souyue.R;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DiscrollvablePathLayout extends LinearLayout implements
		Discrollvable {

	private Bitmap bm,bm1,bm2,bm3,bm4,bm5,bm6,bm7,bm8,bm9,bm10;
	public DiscrollvableLayout  mDiscrollLayoutContent1,mDiscrollLayoutContent2,mDiscrollLayoutContent3,
								mDiscrollLayoutContent4,mDiscrollLayoutContent5,mDiscrollLayoutContent6,
								mDiscrollLayoutContent7,mDiscrollLayoutContent8,mDiscrollLayoutContent9,
								mDiscrollLayoutContent10;
    
	private Context context;
	
	private ImageView mDayOfChosenView;
	private List<DiscrollvableLayout> mLayouts ;
	
//	private List<MyPoint> myPoints;
	private List<Bitmap> mBitmaps;
	
	private static final int ControlPointCount = 9;
	
	public DiscrollvablePathLayout(Context context) {
		this(context, null);
	}

	public DiscrollvablePathLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		inflaterView();
	}

	private void inflaterView() {
		mDayOfChosenView = (ImageView) findViewById(R.id.iv_day_of_chosen);
		
		if(mLayouts == null) {
			mLayouts = new ArrayList<DiscrollvableLayout>();
		}
		
		mDiscrollLayoutContent1 = (DiscrollvableLayout) findViewById(R.id.discroll_layout_content_1);
		mDiscrollLayoutContent2 = (DiscrollvableLayout) findViewById(R.id.discroll_layout_content_2);
		mDiscrollLayoutContent3 = (DiscrollvableLayout) findViewById(R.id.discroll_layout_content_3);
		mDiscrollLayoutContent4 = (DiscrollvableLayout) findViewById(R.id.discroll_layout_content_4);
		mDiscrollLayoutContent5 = (DiscrollvableLayout) findViewById(R.id.discroll_layout_content_5);
		mDiscrollLayoutContent6 = (DiscrollvableLayout) findViewById(R.id.discroll_layout_content_6);
		mDiscrollLayoutContent7 = (DiscrollvableLayout) findViewById(R.id.discroll_layout_content_7);
		mDiscrollLayoutContent8 = (DiscrollvableLayout) findViewById(R.id.discroll_layout_content_8);
		mDiscrollLayoutContent9 = (DiscrollvableLayout) findViewById(R.id.discroll_layout_content_9);
		mDiscrollLayoutContent10 = (DiscrollvableLayout) findViewById(R.id.discroll_layout_content_10);
		
		mLayouts.add(mDiscrollLayoutContent1);	
		mLayouts.add(mDiscrollLayoutContent2);	
		mLayouts.add(mDiscrollLayoutContent3);	
		mLayouts.add(mDiscrollLayoutContent4);	
		mLayouts.add(mDiscrollLayoutContent5);	
		mLayouts.add(mDiscrollLayoutContent6);	
		mLayouts.add(mDiscrollLayoutContent7);	
		mLayouts.add(mDiscrollLayoutContent8);	
		mLayouts.add(mDiscrollLayoutContent9);	
		mLayouts.add(mDiscrollLayoutContent10);	
		this.setLayouts(mLayouts);
	}

	@Override
	public void onDiscrollve(float ratio) {
	}

	@Override
	public void onResetDiscrollve() {
	}

	public List<DiscrollvableLayout> getLayouts() {
		return mLayouts;
	}

	public void setLayouts(List<DiscrollvableLayout> mLayouts) {
		this.mLayouts = mLayouts;
	}
}
