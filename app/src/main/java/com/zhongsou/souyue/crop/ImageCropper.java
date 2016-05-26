package com.zhongsou.souyue.crop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.service.ZSAsyncTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * 切图工具类
 * @author wanglong@zhongsou.com
 *
 */
public class ImageCropper {
	public static void main(String[] args) {
		Activity activity = null;
		ImageView imageView = null;
		ImageCropper i = new ImageCropper(activity, imageView);
		i.setCubeSize(150);//真实的裁剪图片的大小，方形
		i.setThumbnailSize(100);//缩略图大小，
		i.setCropListener(new ICropListener() {
			@Override
			public void onFinish(File file) {
				//切完图片,完成
			}
			@Override
			public void onCleanImage(ImageView imageView) {
			}
			@Override
			public void onActivityResult(int requestCode, int resultCode, Intent data) {
			}
		});
	}
	private static class CompressBitmapTask extends ZSAsyncTask<File, CompressBitmapTask.ProcessingState, Bitmap> {
		public enum ProcessingState {
			STARTING, PROCESSING_LARGE, FINISHED
		}
		private int thumbnailSize;
		public CompressBitmapTask() {
			super();
		}
		@Override
		protected Bitmap doInBackground(File... files) {
			if (files == null || files.length == 0 || !files[0].exists()) {
				return null;
			}
			ProcessingState[] s = new ProcessingState[1];
			s[0] = ProcessingState.PROCESSING_LARGE;
			publishProgress(s);
			return Images.decodeFile(files[0], thumbnailSize);
		}
	}
	private static class CropOption {
		public CharSequence title;
		public Drawable icon;
		public Intent appIntent;
	}
	private static class CropOptionAdapter extends ArrayAdapter<CropOption> {
		private ArrayList<CropOption> mOptions;
		private LayoutInflater mInflater;
		public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
			super(context, R.layout.crop_item_selector, options);
			this.mOptions = options;
			this.mInflater = LayoutInflater.from(context);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup group) {
			if (convertView == null) {
				convertView = this.mInflater.inflate(R.layout.crop_item_selector, null);
			}
			CropOption item = this.mOptions.get(position);
			if (item != null) {
				((ImageView) convertView.findViewById(R.id.iv_icon)).setImageDrawable(item.icon);
				((TextView) convertView.findViewById(R.id.tv_name)).setText(item.title);
				return convertView;
			}
			return null;
		}
	}
	public static interface ICropListener {
		/**
		 * @param requestCode
		 * @param resultCode
		 * @param data
		 */
		public void onActivityResult(int requestCode, int resultCode, Intent data);
		/**
		 * 当外部应用
		 * @param imageView
		 */
		public void onCleanImage(ImageView imageView);
		/**
		 * 拍照，或从相册选取，或截图完毕后，回调。
		 * @param file
		 */
		public void onFinish(File file);
	}
	private static File getTempFile() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File f = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE);
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return f;
		} else {
			return null;
		}
	}
	private static File getImageFile(Uri uri) {
		if ("file".equalsIgnoreCase(uri.getScheme())) {
			return new File(uri.getPath());
		} else {
			return new File(getFilePathFromContentUri(uri));
		}
	}
	private static String getFilePathFromContentUri(Uri uri) {
		String[] filePathColumn = { MediaColumns.DATA };
		Cursor cursor = MainApplication.getInstance().getContentResolver().query(uri, filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String filePath = cursor.getString(columnIndex);
		cursor.close();
		return filePath;
	}
	private static Uri getTempUri() {
		return Uri.fromFile(getTempFile());
	}
	private ICropListener cropListener;
	private Activity activity;
	private ImageView imageView;
	private int cubeSize;
	private int thumbnailSize = 150;
	private int width = 800;
	private int heigth = 800;
	private int MaxSize = 1024; //切图之前先进行一次压缩，最大边为1024
	private Uri mImageCaptureUri;
	private Uri uriFromCameraOrTakeFile;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_IMAGE = 2;
	private static final int PICK_FROM_FILE = 3;
	private static final String TEMP_PHOTO_FILE = "souyue_crop_temp.jpg";
	/**
	 * @param activity
	 * @param imageView
	 */
	public ImageCropper(Activity activity, ImageView imageView) {
		this.activity = activity;
		this.imageView = imageView;
	}
	private void doCrop() {
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");
		List<ResolveInfo> list = this.activity.getPackageManager().queryIntentActivities(intent, 0);
		int size = list.size();
		if (size == 0) {
			if (uriFromCameraOrTakeFile != null) {
				processPhotoUpdate(getImageFile(uriFromCameraOrTakeFile));
			}
			return;
		} else {
			if (mImageCaptureUri != null) {
				Images.decodeImageFile(getImageFile(this.mImageCaptureUri), MaxSize);
			}
			intent.setData(this.mImageCaptureUri);
			if (this.cubeSize > 0) {
				this.width = this.heigth = this.cubeSize;
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
			}
			intent.putExtra("outputX", this.width);
			intent.putExtra("outputY", this.heigth);
			intent.putExtra("scale", false);
			intent.putExtra("noFaceDetection", true);
			intent.putExtra("return-data", false);
			intent.putExtra("setWallpaper", false);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
			if (size == 1) {
				Intent i = new Intent(intent);
				ResolveInfo res = list.get(0);
				i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
				this.activity.startActivityForResult(i, CROP_IMAGE);
			} else {
				for (ResolveInfo res : list) {
					final CropOption co = new CropOption();
					co.title = this.activity.getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
					co.icon = this.activity.getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
					co.appIntent = new Intent(intent);
					co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
					cropOptions.add(co);
				}
				CropOptionAdapter adapter = new CropOptionAdapter(this.activity.getApplicationContext(), cropOptions);
				AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
				builder.setTitle("请选择切图应用");
				builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {
						ImageCropper.this.activity.startActivityForResult(cropOptions.get(item).appIntent, CROP_IMAGE);
					}
				});
				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						if (uriFromCameraOrTakeFile != null) {
							processPhotoUpdate(getImageFile(uriFromCameraOrTakeFile));
						}
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
			case PICK_FROM_CAMERA:
				doCrop();
				break;
			case PICK_FROM_FILE:
				mImageCaptureUri = data.getData();
				uriFromCameraOrTakeFile = mImageCaptureUri;
				doCrop();
				break;
			case CROP_IMAGE:
				if (data != null && data.getExtras() != null) {
					processPhotoUpdate(getTempFile());
				} else {
					processPhotoUpdate(getTempFile());
				}
				break;
		}
	}
	private void onCleanImage() {
		if (this.cropListener != null) {
			this.cropListener.onCleanImage(this.imageView);
		}
	}
	private void onFinish(Bitmap bitmap) {
		if (this.imageView != null && bitmap != null) {
			this.imageView.setImageBitmap(bitmap);
			this.cropListener.onFinish(getTempFile());
		}
	}
	private void processPhotoUpdate(File tempFile) {
		CompressBitmapTask task = new CompressBitmapTask() {
			@Override
			protected void onPostExecute(Bitmap result) {
				onFinish(result);
			}
		};
		task.thumbnailSize = this.thumbnailSize;
		task.execute(tempFile);
	}
	public void setCropListener(ICropListener cropListener) {
		this.cropListener = cropListener;
	}
	public void setCubeSize(int cubeSize) {
		this.cubeSize = cubeSize;
	}
	public void setHeigth(int heigth) {
		this.heigth = heigth;
	}
	public void setThumbnailSize(int thumbnailSize) {
		this.thumbnailSize = thumbnailSize;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public void show(boolean ifShowCleanImage) {
		final String[] items = ifShowCleanImage ? new String[] { "手机拍照", "从相册选取", "清除图片" } : new String[] { "手机拍照", "从相册选取" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.activity, android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
		builder.setTitle("选择图片");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					//					cleanCache();
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE));
					uriFromCameraOrTakeFile = mImageCaptureUri;
					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
					intent.putExtra("return-data", true);
					try {
						ImageCropper.this.activity.startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else if (item == 1) {
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					ImageCropper.this.activity.startActivityForResult(Intent.createChooser(intent, "选择相册应用"), PICK_FROM_FILE);
				} else if (item == 2) {
					onCleanImage();
				}
			}
		}).create().show();
	}
}