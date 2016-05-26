package com.zhongsou.souyue.im.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.module.ExpressionBean;
import com.zhongsou.souyue.im.util.BitmapUtil;
import com.zhongsou.souyue.im.util.Constants;
import com.zhongsou.souyue.im.view.CustomerViewPager;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.view.ExpressionView;

import java.io.File;
import java.util.List;

/**
 * 表情
 * 
 * @author wangqiang
 * 
 */
public class ExpressionAdapter extends BaseAdapter {

	private Context mContext;
	private List<ExpressionBean> beans;
	private LayoutInflater inflater;

	private LinearLayout mFloatLayout;
	private SimpleDraweeView mFloatView;
	private PopupWindow popUpWindow;

	private int popWidth;
	private int popHeight;
	private int gifWidth;
	private int gifHeight;

	private ExpressionView.OnExpressionListener mExpressionListener;

	public ExpressionAdapter(Context context, List<ExpressionBean> beans) {
		this.inflater = LayoutInflater.from(context);
		this.mContext = context;
		mExpressionListener = (ExpressionView.OnExpressionListener)mContext;
		this.beans = beans;

		this.popWidth = DeviceUtil.dip2px(context, 75);
		this.popHeight = DeviceUtil.dip2px(context,75);
		this.gifWidth = DeviceUtil.dip2px(context,50);
		this.gifHeight = DeviceUtil.dip2px(context,50);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return beans != null ? beans.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return beans.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ExpressionBean expressionBean = (ExpressionBean) beans.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = this.inflater.inflate(R.layout.gif_item, null);
			viewHolder.iv_face = ((ImageView) convertView
					.findViewById(R.id.imageview));
			viewHolder.iv_name = ((TextView) convertView
					.findViewById(R.id.name));
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		String thumbnailUrl = expressionBean.geteThumbnailUrl();
//		Slog.d("callback", "picPath:--------");
		final File iconFile = new File(mContext.getFilesDir()+Constants.PACKAGE_DOWNURL + File.separator
				+ SYUserManager.getInstance().getUserId() + File.separator
				+ expressionBean.geteThumbnailUrl());
		if (iconFile.exists()) {
			Bitmap bm = BitmapUtil.convertToBitmap(iconFile.getAbsolutePath(), 100,100);
			viewHolder.iv_face.setImageBitmap(bm);
			viewHolder.iv_name.setText(expressionBean.geteRealName());

			final View finalConvertView = convertView;
			viewHolder.iv_face.setOnTouchListener(new MTouchListener(expressionBean));
			viewHolder.iv_face.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
//					System.out.println("长按");
//					int i[]=new int[2];
//					finalConvertView.getLocationOnScreen(i);
					popUpWindow=createFloatView(finalConvertView,expressionBean);
					CustomerViewPager.isCanScroll = false;
					return false;
				}
			});

			viewHolder.iv_face.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mExpressionListener!=null){
						mExpressionListener.expressionItemClick(expressionBean);
					}
//					System.out.println("发送表情");
				}
			});
		}
		return convertView;

	}

	public static class ViewHolder {
		private ImageView iv_face;
		private TextView iv_name;
	}



	/**
	 *	显示GIF预览片
	 * @param expressionBean
	 * @param gifImageView
	 */
	private void showGif(ExpressionBean expressionBean, SimpleDraweeView gifImageView){
		if (expressionBean != null) {
			final String local_url = expressionBean.geteSendUrl();
			File gifFile = new File(mContext.getFilesDir()+Constants.PACKAGE_DOWNURL + File.separator
					+ SYUserManager.getInstance().getUserId() + File.separator
					+ local_url);
//			GifDrawable gifFromFile=null;
//			try {
//				gifFromFile = new GifDrawable(gifFile);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			System.out.println("-------->"+gifFile.getAbsolutePath());
			if (expressionBean != null) {
				if (gifFile.exists()) { // 如果下载了
//					gifImageView.setImageDrawable(gifFromFile);
					DraweeController controller = Fresco.newDraweeControllerBuilder()
							.setAutoPlayAnimations(true)
							.setUri(Uri.fromFile(gifFile))
							.build();
					gifImageView.setController(controller);
					return;
				} else {
					gifImageView.setImageResource(R.drawable.default_head);
				}
			}
		}
	}

	private class MTouchListener implements View.OnTouchListener{
		private ExpressionBean expressionBean;
		public MTouchListener(ExpressionBean expressionBean){
			this.expressionBean = expressionBean;
		}
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()){
				case MotionEvent.ACTION_DOWN:
//					System.out.println("ActionDown   down");
					break;
				case MotionEvent.ACTION_UP:
					if(popUpWindow!=null&&popUpWindow.isShowing()){
						popUpWindow.dismiss();
						CustomerViewPager.isCanScroll=true;
//						System.out.println("ActionUp    dismiss");
					}
					break;
				case MotionEvent.ACTION_MOVE:
//					System.out.println("ActionMove   move  expressionBean.name = "+expressionBean.geteName());
					;break;
				case MotionEvent.ACTION_CANCEL:
					if(popUpWindow!=null&&popUpWindow.isShowing()){
//						System.out.println("Action    Cancel   dismiss");
						popUpWindow.dismiss();
						CustomerViewPager.isCanScroll=true;
					}
					break;
			}
			return false;
		}
	}

	private PopupWindow createFloatView(View convertView,ExpressionBean expressionBean)
	{
		mFloatLayout = (LinearLayout) inflater.inflate(R.layout.im_gif_preview, null);
		PopupWindow popWindow= new PopupWindow(mFloatLayout,popWidth ,popHeight,true);

		int h[]=new int[2];
		convertView.getLocationInWindow(h);
//		int offsetX=-40;
		int offsetX=-(popWidth-gifWidth)/2;
//		int u=0;
		while(h[0] + offsetX + popWidth - convertView.getRootView().getWidth() > 0){
			offsetX-=5;
//			u++;
//			System.out.println("符合条件了第"+u+"次");
		}
		int offsetY=-2*popHeight;
		mFloatView = (SimpleDraweeView)mFloatLayout.findViewById(R.id.giv_gif);
		showGif(expressionBean,mFloatView);
//		popWindow.setContentView(mFloatLayout);
//		popWindow.showAtLocation(convertView,Gravity.NO_GRAVITY,offsetX,offsetY);

//		System.out.println("hX ="+h[0]+ " hY =" + h[1]);
//		System.out.println("convertView.height ="+convertView.getHeight());
		popWindow.showAsDropDown(convertView,offsetX,offsetY);
		return popWindow;
	}
}
