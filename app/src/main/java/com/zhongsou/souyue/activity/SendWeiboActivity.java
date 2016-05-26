package com.zhongsou.souyue.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.db.SelfCreateHelper;
import com.zhongsou.souyue.dialog.SYProgressDialog;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.SelfCreateItem;
import com.zhongsou.souyue.net.selfCreate.SelfCreateDel;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.service.SelfCreateTask;
import com.zhongsou.souyue.service.SendUtils;
import com.zhongsou.souyue.share.ShareByWeibo;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.uikit.MMAlert;
import com.zhongsou.souyue.utils.AccessTokenKeeper;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class SendWeiboActivity extends RightSwipeActivity implements
		OnClickListener,OnCancelListener {

	public static final String TAG = "selfCreateItem";
	private TextView tv_remaincount;
	private TextView tv_save;
	private EditText et_content;
	private CheckBox cbx;
	private Uri imageFileUri;
//	public Http http;
	private ImageButton ib_photo;
	private String picPath = null;

	private SelfCreateItem sci;
//	private AQuery query;
	private int degree;
	private SsoHandler mSsoHandler;
	private SYProgressDialog sydialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_weibo);
		if (sysp == null)
			sysp = SYSharedPreferences.getInstance();
		sydialog = new SYProgressDialog(this, 0, "保存中");
		sydialog.setOnCancelListener(this);
//		http = new Http(this);
//		query = new AQuery(this);
		getDataFromIntent();
		initViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void getDataFromIntent() {
		Intent intent = getIntent();
		sci = (SelfCreateItem) intent.getSerializableExtra("selfCreateItem");
		if (sci.conpics() != null && sci.conpics().size() != 0) {
			sci.conpic_$eq(sci.conpics().get(0));
		}
	}

	private void initViews() {
		tv_remaincount = findView(R.id.tv_remaincount);
		ib_photo = findView(R.id.ib_photo);
		ib_photo.setOnClickListener(this);
		tv_save = findView(R.id.tv_save);
		tv_save.setOnClickListener(this);
		TextView tv_cancel = findView(R.id.tv_cancel);
		tv_cancel.setOnClickListener(this);
		TextView tv_send = findView(R.id.tv_send);
		if (StringUtils.isEmpty(sci.keyword())) {
			tv_send.setText(R.string.next);
		} else {
			tv_send.setText(R.string.send);
		}
		tv_send.setOnClickListener(this);
		et_content = findView(R.id.et_content);
		if (sci != null && !StringUtils.isEmpty(sci.content().trim())) {
			et_content.setText(sci.content());
			int remainSize = 140 - ShareWeiboActivity.getStrLen(sci.content());
			tv_remaincount.setText("" + remainSize);
			if (!TextUtils.isEmpty(sci.conpic())) {
				if (sci.conpic().toLowerCase().contains("http")) {
					// 加载网络图片
					//query.id(ib_photo).image(sci.conpic(), true, true);
                                            PhotoUtils.showCard(PhotoUtils.UriType.HTTP,sci.conpic(),ib_photo);
				} else {
					Uri uri = Uri.fromFile(new File(sci.conpic()));
					picPath = Utils.getPicPathFromUri(uri, this);
					setImage(uri);
				}
			}
			picPath = sci.conpic();
			Log.v("Huang", "picPath-->sci.conpic():" + picPath);
		}
		cbx = findView(R.id.cbx);
		// 设置复选框初始状态
		if (ShareByWeibo.isAuthorised(this))
			cbx.setChecked(true);
		else
			cbx.setChecked(false);

		cbx.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					if (!ShareByWeibo.isAuthorised(SendWeiboActivity.this)) {
						mSsoHandler = ShareByWeibo.getInstance().share(
								SendWeiboActivity.this, null);
					}
				}
			}
		});
		et_content.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				int remainSize = 140 - ShareWeiboActivity.getStrLen(s
						.toString());
				tv_remaincount.setText("" + remainSize);
				if (remainSize < 0)
					tv_remaincount.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.remain_count_bg_negative));
				else
					tv_remaincount.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.remain_count_bg));
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.tv_save:
			
			//草稿修改后再次保存，删除上次保存的草稿
			if (StringUtils.isEmpty(sci.id())
					&& sci.status() == ConstantsUtils.STATUS_SEND_ING) {
				SelfCreateHelper.getInstance().delSelfCreateItem(sci);
			} else {
				SelfCreateDel selfcreatedel=new SelfCreateDel(HttpCommon.SELFCREATEDEL_REQUEST_ID,this);
				selfcreatedel.setParams(sci.id());
				mMainHttp.doRequest(selfcreatedel);
			}

			try {
				List<String> weiboImages = new ArrayList<String>();
				if (!TextUtils.isEmpty(picPath)) {
					if (picPath.toLowerCase().contains("http")) {
						weiboImages.add(picPath);
					} else {
						String weibo_image_path = new File(
								ImageUtil.getSelfDir(),
								System.currentTimeMillis() + "weibo_image")
								.getAbsolutePath();
						Bitmap bm = ImageUtil.extractThumbNail(picPath);
						ImageUtil.saveBitmapToFile(bm, weibo_image_path);
						weiboImages.add(weibo_image_path);
						picPath = weibo_image_path;
					}
				}
				String contentValue = et_content.getText().toString();
				sci.conpics_$eq(weiboImages);
				if (!SendUtils.checkUser(this))
					return;
				if (!SendUtils.checkData(sci, null, contentValue))
					return;
				sci.content_$eq(contentValue);
				sci.status_$eq(ConstantsUtils.STATUS_SEND_FAIL);
				sci.pubtime_$eq(System.currentTimeMillis() + "");
				SelfCreateTask.getInstance().save2draftBox(
						SendWeiboActivity.this, sci);
			} catch (Exception ex) {

			}
			
			sydialog.show();

			break;

		case R.id.tv_cancel:
			finishAnimation(this);
			break;

		case R.id.tv_send:
			// 判断用户是否登录

			List<String> weiboImages = new ArrayList<String>();
			if (!TextUtils.isEmpty(picPath)) {
				if (picPath.toLowerCase().contains("http")) {
					weiboImages.add(picPath);
				} else {
					String weibo_image_path = new File(ImageUtil.getSelfDir(),
							System.currentTimeMillis() + "weibo_image")
							.getAbsolutePath();
					Bitmap bm = ImageUtil.extractThumbNail(picPath);
					ImageUtil.saveBitmapToFile(bm, weibo_image_path);
					weiboImages.add(weibo_image_path);
					picPath = weibo_image_path;
				}
			}
			String contentValue = et_content.getText().toString();
			sci.conpics_$eq(weiboImages);
			if (!SendUtils.checkUser(this))
				return;
			if (!SendUtils.checkData(sci, null, contentValue))
				return;
			sci.content_$eq(contentValue);

			v.setEnabled(SendUtils.sendOrNext(sci, SendWeiboActivity.this,
					cbx.isChecked()));
			break;

		case R.id.ib_photo:
			if (!TextUtils.isEmpty(picPath)) {
				// 删除照片
				showDeleteAlert();
			} else
				ShowPickDialog();
			break;

		default:
			break;
		}
	}

	// 数据库保存后 回调方法
	public void save2BoxSuccess(String result) {
		// gone dialog
		sydialog.cancel();
		if (result == null) {
			// 保存失败, 失败继续留在原页面
		} else {
			// 保存成功， 跳转到列表中
			Intent intent = new Intent();
			intent.setClass(SendWeiboActivity.this, SelfCreateActivity.class);
			startActivity(intent);
			
			Intent tofresh = new Intent();
			tofresh.putExtra("ismodify", true);
			tofresh.setAction(ConstantsUtils.INTENTFILTER_ACTION_REFRESH_SELFCREATE_LV);
			sendBroadcast(tofresh);
		}
	}

	private boolean hasChangedPics() {// 图片是否改变
		if (TextUtils.isEmpty(sci.conpic()) && TextUtils.isEmpty(picPath)) // 前后都是空
			return false;
		if (!TextUtils.isEmpty(sci.conpic()) && !TextUtils.isEmpty(picPath)
				&& sci.conpic().equals(picPath))
			return false;

		return true;
	}

	public class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			Oauth2AccessToken accessToken = Oauth2AccessToken
					.parseAccessToken(values);
			if (accessToken.isSessionValid()) {
				AccessTokenKeeper.keepAccessToken(SendWeiboActivity.this,
						accessToken);
			}
			cbx.setChecked(true);
		}

		@Override
		public void onWeiboException(WeiboException paramWeiboException) {
			cbx.setChecked(false);
			SouYueToast.makeText(getApplicationContext(), R.string.bound_fail,
					0).show();
		}

		@Override
		public void onCancel() {
			cbx.setChecked(false);
		}

	}

	private void showDeleteAlert() {
		Dialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(getString(R.string.dialog_del_sure))
				.setMessage(getString(R.string.dialog_del_sure_des))
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(getString(R.string.dialog_del),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								picPath = null;
								if (sci != null
										&& TextUtils.isEmpty(sci.conpic())) {
									sci.conpic_$eq("");
									sci.conpics_$eq(null);
								}
								ib_photo.setImageDrawable(getResources()
										.getDrawable(R.drawable.add_pic));
								ib_photo.setBackgroundDrawable(null);
							}
						})
				.setNegativeButton(getString(R.string.dialog_cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
		alertDialog.show();

	}

	/**
	 * 选择提示对话框
	 */
	public void ShowPickDialog() {
		String shareDialogTitle = getString(R.string.pick_dialog_title);
		MMAlert.showAlert(this, shareDialogTitle, getResources()
				.getStringArray(R.array.picks_item), null,
				new MMAlert.OnAlertSelectId() {

					@Override
					public void onClick(int whichButton) {
						switch (whichButton) {
						case 0: // 拍照
							try {
								imageFileUri = getContentResolver()
										.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
												new ContentValues());
								if (imageFileUri != null) {
									Intent i = new Intent(
											android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
									i.putExtra(
											android.provider.MediaStore.EXTRA_OUTPUT,
											imageFileUri);
									if (Utils.isIntentSafe(
											SendWeiboActivity.this, i)) {
										startActivityForResult(i, 2);
									} else {
										SouYueToast
												.makeText(
														SendWeiboActivity.this,
														getString(R.string.dont_have_camera_app),
														SouYueToast.LENGTH_SHORT)
												.show();
									}
								} else {
									SouYueToast
											.makeText(
													SendWeiboActivity.this,
													getString(R.string.cant_insert_album),
													SouYueToast.LENGTH_SHORT)
											.show();
								}
							} catch (Exception e) {
								SouYueToast.makeText(SendWeiboActivity.this,
										getString(R.string.cant_insert_album),
										SouYueToast.LENGTH_SHORT).show();
							}
							break;
						case 1: // 相册
							Intent intent = new Intent(Intent.ACTION_PICK);
							intent.setDataAndType(
									android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
									"image/*");
							startActivityForResult(intent, 1);
							break;
						default:
							break;
						}
					}

				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/**
		 * 新浪微博sso认证
		 */
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:// 如果是直接从相册获取
				if (data != null) {
					Uri uri = data.getData();
					picPath = Utils.getPicPathFromUri(uri, this);
					imageFileUri = null;
					degree = 0;
					if (!StringUtils.isEmpty(picPath))
						degree = ImageUtil.readPictureDegree(picPath);
					Matrix matrix = new Matrix();
					if (degree != 0) {// 解决旋转问题
						matrix.preRotate(degree);
					}
					setImage(uri);
				}
				break;
			case 2:// 如果是调用相机拍照时
				if (imageFileUri != null) {
					Log.v("Huang", "调用相机拍照");
					picPath = Utils.getPicPathFromUri(imageFileUri, this);
					degree = 0;
					if (!StringUtils.isEmpty(picPath))
						degree = ImageUtil.readPictureDegree(picPath);
					Matrix matrix = new Matrix();
					if (degree != 0) {// 解决旋转问题
						matrix.preRotate(degree);
					}
					Uri uri = Uri.fromFile(new File(picPath));
					setImage(uri);
				} else {
					// 图片获取异常
					showToast(R.string.self_get_image_error);
				}
				break;
			}
		}
	}

	public void setImage(Uri uri) {

		Bitmap bm = null;
		try {
			BitmapFactory.Options options = ImageUtil.getCaculateSize(picPath,
					ib_photo);
			Log.v("Huang", "setImage-->picPath:" + picPath);
			bm = BitmapFactory.decodeFile(picPath, options);
			if (bm != null) {
				bm = ImageUtil.rotaingImageView(degree, bm);
			} else {
				// 图片获取异常
				picPath = null;
				showToast(R.string.self_get_image_error);
				return;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		ib_photo.setImageBitmap(bm);
		ib_photo.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.ad_item_rectangle));
	}

//	@Override
//	public void onHttpError(String methodName, AjaxStatus as) {
//
//	}

	public void showToast(int resId) {
		SouYueToast.makeText(SendWeiboActivity.this,
				getResources().getString(resId), 0).show();
	}

	// 获取资源String
	public String getResString(int id) {
		return this.getResources().getString(id);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		String self_weibo = String.format(getString(R.string.self_weibo_login_no),CommonStringsApi.APP_NAME_SHORT);
		AlertDialog gotoLogin = new AlertDialog.Builder(this)
				.setMessage(self_weibo)
				.setPositiveButton(R.string.go_login,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// 登陆
								Intent intent = new Intent();
								intent.setClass(SendWeiboActivity.this,
										LoginActivity.class);
								intent.putExtra(LoginActivity.Only_Login, true);
								startActivityForResult(intent, 0);
								overridePendingTransition(R.anim.left_in,
										R.anim.left_out);
							}
						})
				.setNegativeButton(R.string.go_cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).create();
		return gotoLogin;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		
	}

}
