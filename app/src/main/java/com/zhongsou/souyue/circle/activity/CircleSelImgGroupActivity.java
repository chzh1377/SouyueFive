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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.adapter.CircleSelImgGroupAdapter;
import com.zhongsou.souyue.circle.view.CircleImageBean;
import com.zhongsou.souyue.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * @author liuyh
 * 发帖图片选择分组类
 *
 */
public class CircleSelImgGroupActivity extends  BaseActivity {
	private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
	private List<CircleImageBean> list = new ArrayList<CircleImageBean>();
	private final static int SCAN_OK = 1;
	private ProgressDialog mProgressDialog;
	private CircleSelImgGroupAdapter adapter;
	private ListView mGroupGridView;
	private int picLen;
	private int count;
	private List<String> childListLatest = new ArrayList<String>();
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_OK:
				//关闭进度条
				mProgressDialog.dismiss();
				list = subGroupOfImage(mGruopMap);
				if(list==null||list.size()==0){
					ToastUtil.show(CircleSelImgGroupActivity.this, "相册中没有照片！");
					return ;
				}
				Collections.reverse(list);
				adapter = new CircleSelImgGroupAdapter(CircleSelImgGroupActivity.this,list, mGroupGridView);
				mGroupGridView.setAdapter(adapter);
				break;
			}
		}
		
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		getImages();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_sel_img_group);
		
		mGroupGridView = (ListView) findViewById(R.id.main_grid);
		((TextView)findViewById(R.id.circle_title_textview)).setText("相册");
		picLen = getIntent().getIntExtra("piclen", -1);
		mGroupGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				List<String> childList = mGruopMap.get(list.get(position).getFolderName());
				String folderName = list.get(position).getFolderName();
				Intent mIntent = new Intent(CircleSelImgGroupActivity.this, CircleSelectImageActivity.class);
				mIntent.putStringArrayListExtra("data", (ArrayList<String>)childList);
				mIntent.putExtra("filename", folderName);
				mIntent.putExtra("piclen", picLen);
				mIntent.putExtra("intetrtype", 1);  //第一次进入为0，否侧为1
				startActivityForResult(mIntent, 0x100);
				
			}
		});
		Intent mIntent = new Intent(CircleSelImgGroupActivity.this, CircleSelectImageActivity.class);
		mIntent.putExtra("filename", "最近照片");
		mIntent.putExtra("piclen", picLen);
		mIntent.putExtra("intetrtype", 0);  //第一次进入为1，否侧为0
		startActivityForResult(mIntent, 0x100);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == 0x100){
			List<String> list = new ArrayList<String>();
			list = data.getStringArrayListExtra("seldata");
			Intent intent = new Intent();
			intent.putStringArrayListExtra("imgseldata", (ArrayList<String>)list);
			setResult(0x200, intent);
			CircleSelImgGroupActivity.this.finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
	 */
	private void getImages() {
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}
		count = 0;
		childListLatest.clear();
		mGruopMap.clear();
		//显示进度条
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = CircleSelImgGroupActivity.this.getContentResolver();

				//只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png","image/jpg" }, MediaStore.Images.Media.DATE_MODIFIED + " DESC");
				
				while (mCursor.moveToNext()) {
					//获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					count ++;
					if(count <= 100){
						childListLatest.add(path);
						mGruopMap.put("最近照片", childListLatest);
					}
					//获取该图片的父路径名
					String parentName = new File(path).getParentFile().getName();

					
					//根据父路径名将图片放入到mGruopMap中
					if (!mGruopMap.containsKey(parentName)) {
						List<String> chileList = new ArrayList<String>();
						chileList.add(path);
						mGruopMap.put(parentName, chileList);
					} else {
						mGruopMap.get(parentName).add(path);
					}
				}
				
				mCursor.close();
				
				//通知Handler扫描图片完成
				mHandler.sendEmptyMessage(SCAN_OK);
				
			}
		}).start();
		
	}
	
	
	/**
	 * 组装分组界面GridView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中
	 * 所以需要遍历HashMap将数据组装成List
	 * 
	 * @param mGruopMap
	 * @return
	 */
	private List<CircleImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap){
		if(mGruopMap.size() == 0){
			return null;
		}
		List<CircleImageBean> list = new ArrayList<CircleImageBean>();
		
		Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, List<String>> entry = it.next();
			CircleImageBean mImageBean = new CircleImageBean();
			String key = entry.getKey();
			List<String> value = entry.getValue();
			
			mImageBean.setFolderName(key);
			mImageBean.setImageCounts(value.size());
			mImageBean.setTopImagePath(value.get(0));//获取该组的第一张图片
			
			list.add(mImageBean);
		}
		
		return list;
		
	}


}
