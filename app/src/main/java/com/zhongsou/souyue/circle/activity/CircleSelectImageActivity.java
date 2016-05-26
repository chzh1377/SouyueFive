package com.zhongsou.souyue.circle.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.adapter.CircleSelectImgAdapter;
import com.zhongsou.souyue.circle.util.OnChangeListener;

import java.util.ArrayList;
import java.util.List;
/**
 * @author liuyh
 * 发帖选择图片类
 */
public class CircleSelectImageActivity extends  BaseActivity {
	private GridView mGridView;
	private List<String> list;
	private CircleSelectImgAdapter adapter;
	private TextView tvNum;
	private TextView tvFinish;
	private int picLen;
	public static int  TOTAL_IMG_NUM = 9;
	private ProgressDialog mProgressDialog;
	private final static int SCAN_OK_IMG = 0x500;
	private int count;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_OK_IMG:
				// 关闭进度条
				mProgressDialog.dismiss();
				if(list==null||list.size()==0){
					return ;
				}
				adapter = new CircleSelectImgAdapter(CircleSelectImageActivity.this, list, mGridView,TOTAL_IMG_NUM - picLen);
				adapter.setListener(new OnChangeListener() {
					@Override
					public void onChange(Object obj) {
						tvNum.setText(obj.toString() + "/" + (TOTAL_IMG_NUM - picLen));
					}
				});
				mGridView.setAdapter(adapter);
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_sel_img_show_image);
		int type = getIntent().getIntExtra("intetrtype", -1);
		mGridView = (GridView) findViewById(R.id.child_grid);
		String filename = getIntent().getStringExtra("filename");
		picLen = getIntent().getIntExtra("piclen", -1);
		((TextView)findViewById(R.id.circle_title_textview)).setText(filename);
		tvNum = (TextView) findViewById(R.id.tv_num);
		tvFinish = (TextView) findViewById(R.id.tv_finish);
		tvNum.setText(0 + "/" + (TOTAL_IMG_NUM - picLen));
		((TextView)findView(R.id.tv_xiangce)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tvFinish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				List<String> list = new ArrayList<String>();
				list = adapter.getPath();
				intent.putStringArrayListExtra("seldata", (ArrayList<String>)list);
				setResult(0x100, intent);
				CircleSelectImageActivity.this.finish();
			}
		});
		if (type == 1) {
			list = getIntent().getStringArrayListExtra("data");
			adapter = new CircleSelectImgAdapter(this, list, mGridView,TOTAL_IMG_NUM-picLen);
			mGridView.setAdapter(adapter);
			adapter.setListener(new OnChangeListener() {
				@Override
				public void onChange(Object obj) {
					tvNum.setText(obj.toString() + "/" + (TOTAL_IMG_NUM - picLen));
				}
			});
		} else if (type == 0) {
			list = new ArrayList<String>();
			getImages();
		}
	}
	
	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}

		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

		new Thread(new Runnable() {

			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = CircleSelectImageActivity.this
						.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,MediaStore.Images.Media.MIME_TYPE + "=? or "+ MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",new String[] { "image/jpeg", "image/png","image/jpg" },MediaStore.Images.Media.DATE_MODIFIED + " DESC");
				while (mCursor.moveToNext()) {
					// 获取图片的路径
					String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
					count++;
					if (count <= 100) {
						list.add(path);
					}
				}

				mCursor.close();

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(SCAN_OK_IMG);

			}
		}).start();

	}
}
