package com.zhongsou.souyue.im.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.zhongsou.souyue.R;

public class AlphaSideBar extends View {	
	public interface AlphaIndexer{
		int getPositionForAlpha(char c);
	}
	private char[] l;
	private ListView list;
	private TextView mDialogText;
	private int width = 0;
	private int hight = 0;

	public AlphaSideBar(Context context)
	{
		super(context);
		init();
	}

	public AlphaSideBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	private void init() {
		l = new char[] {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '#'};
	}

	public AlphaSideBar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public void setListView(ListView _list) {
		list = _list;
	}

	public void setTextView(TextView mDialogText) {
		this.mDialogText = mDialogText;
	}

	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		int i = (int) event.getY();
		if (hight <= 0)
		{
			hight = getHeight();
		}
		int idx = i / (hight / l.length);
		if (idx >= l.length)
		{
			idx = l.length - 1;
		} else if (idx < 0)
		{
			idx = 0;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE)
		{
            list.clearFocus();
			mDialogText.setVisibility(View.VISIBLE);
			mDialogText.setText("" + l[idx]);
			ListAdapter listAdpater = list.getAdapter();
			if (listAdpater != null) {
				if(listAdpater instanceof HeaderViewListAdapter){
					listAdpater = ((HeaderViewListAdapter)listAdpater).getWrappedAdapter();
				}
				int position = -1;
				if(listAdpater instanceof SectionIndexer){
					SectionIndexer sectionIndexter = (SectionIndexer) listAdpater;
					position = sectionIndexter.getPositionForSection(idx);
				}else if(listAdpater instanceof AlphaIndexer){
					AlphaIndexer alphaIndexter = (AlphaIndexer) listAdpater;
					position = alphaIndexter.getPositionForAlpha(l[idx]);
				}
				if (position == -1) {
					return true;
				}
				list.setSelection(position);
			}
		} else
		{
			mDialogText.setVisibility(View.INVISIBLE);
		}
		return true;
	}

	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setTextSize(getContext().getResources().getDimension(R.dimen.im_alphasidebar_text));
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(0xff4b4b4b);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setAntiAlias(true);
		hight = getHeight();
		// float widthCenter = getMeasuredWidth() / 2;
		for (int i = 0; i < l.length; i++)
		{
			float xPos = getWidth() / 2 /*- paint.measureText(String.valueOf(l[i])) / 2*/;
			float yPos = (getHeight() / l.length) * i + (getHeight() / l.length);
			canvas.drawText(String.valueOf(l[i]), xPos, yPos, paint);
			// canvas.drawText(String.valueOf(l[i]), widthCenter, m_nItemHeight
			// + (i * m_nItemHeight), paint);
		}
		super.onDraw(canvas);
	}
}
