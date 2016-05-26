package com.zhongsou.souyue.circle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.GridView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.util.CircleMemberListEmptyViewMethodAccessor;

public class CircleMemberListPullToRefreshGridView extends CircleMemberListPullToRefreshAdapterViewBase<GridView> {

	class InternalGridView extends GridView implements CircleMemberListEmptyViewMethodAccessor {

		public InternalGridView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public void setEmptyView(View emptyView) {
			CircleMemberListPullToRefreshGridView.this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}

		@Override
		public ContextMenuInfo getContextMenuInfo() {
			return super.getContextMenuInfo();
		}

	}

	public CircleMemberListPullToRefreshGridView(Context context) {
		super(context);
	}

	public CircleMemberListPullToRefreshGridView(Context context, int mode) {
		super(context, mode);
	}

	public CircleMemberListPullToRefreshGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected final GridView createRefreshableView(Context context, AttributeSet attrs) {
		GridView gv = new InternalGridView(context, attrs);
		// Use Generated ID (from res/values/ids.xml)
		gv.setId(R.id.gridview);
		return gv;
	}

	@Override
	public ContextMenuInfo getContextMenuInfo() {
		return ((InternalGridView) getRefreshableView()).getContextMenuInfo();
	}
}
