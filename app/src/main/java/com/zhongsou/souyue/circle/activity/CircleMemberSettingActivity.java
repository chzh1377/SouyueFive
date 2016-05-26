package com.zhongsou.souyue.circle.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.upyun.api.UploadImageTask;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.model.CircleMemberInfo;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.countUtils.ZSSdkUtil;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CircleExitCircleRequest;
import com.zhongsou.souyue.net.circle.CircleGetCircleMeberInfoRequest;
import com.zhongsou.souyue.net.circle.CircleSetPrivateRequest;
import com.zhongsou.souyue.net.circle.CircleSetUserInfoRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.uikit.MMAlert;
import com.zhongsou.souyue.utils.AnoyomousUtils;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 我的圈身份
 *
 * User: FlameLi Date: 2014/7/31 Time: 17:50
 */
public class CircleMemberSettingActivity extends BaseActivity implements
		View.OnClickListener, OnCheckedChangeListener {

//	private static final int CIRCLESETUSERINFO_REQUESTID = 1654;
//	private static final int CIRCLEEXITCIRCLE_REQUESTID = 65456;
//	private static final int CIRCLESETPRIVATE_REQUESTID = 634521;
	private RelativeLayout rl_circle_my_photo;
	private RelativeLayout rl_circle_my_nickname;
	private RelativeLayout rl_circle_my_signature;

	private ImageView iv_circle_my_photo;
	private TextView tv_circle_my_nickname;
	private TextView tv_circle_my_signature;
	private ToggleButton tb_circle_protect_setting;

	private ImageLoader imgloader;
	private Uri imageFileUri;
	private File profileImgFile;// 通过uid，构建头像的本地存储路径
	private String imageUrl;
	private Drawable drawable;
	private ProgressDialog pd;

	private boolean gIsChecked;
	boolean isadmin = false;
	private DisplayImageOptions head_img_options;
	private String token;
	private long interest_id;
    private int interestType;

	private CircleMemberInfo circleMemberInfo;
    private boolean isFirst;

	public static final int CRICLE_MANAGE_PHOTO_SETTING = 1;
	public static final int CRICLE_MANAGE_NICKNAME_SETTING = 2;
	public static final int CRICLE_MANAGE_SIGNATURE_SETTING = 3;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.circle_member_setting);

        isFirst = true;

		interest_id = getIntent().getLongExtra("interest_id", 1L);
        interestType = getIntent().getIntExtra("interestType", 0);
		token = SYUserManager.getInstance().getToken();
		this.imgloader = ImageLoader.getInstance();
		this.head_img_options = new DisplayImageOptions.Builder()
				.cacheOnDisk(true).cacheInMemory(true)
				.displayer(new RoundedBitmapDisplayer(10)).build();
		profileImgFile = new File(getCacheDir(), "headphoto_");

		pd = new ProgressDialog(this);
		pd.setCanceledOnTouchOutside(false);

		initView();
		getCircleMemberInfo();
	}

	private void initView() {
		((TextView) findViewById(R.id.activity_bar_title))
				.setText(R.string.circle_member_setting_text);

		rl_circle_my_photo = (RelativeLayout) findViewById(R.id.rl_circle_my_photo);
		rl_circle_my_nickname = (RelativeLayout) findViewById(R.id.rl_circle_my_nickname);
		rl_circle_my_signature = (RelativeLayout) findViewById(R.id.rl_circle_my_signature);

		iv_circle_my_photo = (ImageView) findViewById(R.id.iv_circle_my_photo);
		tv_circle_my_nickname = (TextView) findViewById(R.id.tv_circle_my_nickname);
		tv_circle_my_signature = (TextView) findViewById(R.id.tv_circle_my_signature);
		tb_circle_protect_setting = (ToggleButton) findViewById(R.id.tb_circle_protect_setting);

		// 加入监听事件
		rl_circle_my_photo.setOnClickListener(this);
		rl_circle_my_nickname.setOnClickListener(this);
		rl_circle_my_signature.setOnClickListener(this);
		tb_circle_protect_setting.setOnCheckedChangeListener(this);
	}

	private void getCircleMemberInfo() {
		CircleGetCircleMeberInfoRequest.send(HttpCommon.CIRCLE_GETCIRCLEMEBERINFO_REQUESTID,this,token, interest_id);
//		http.getCircelMemberInfo(token, interest_id);
	}


	public void getCircelMemberInfoSuccess(HttpJsonResponse response) {
		//设置当前的匿名图
		CircleMemberInfo memberInfo = new Gson().fromJson(response.getBody(),
				CircleMemberInfo.class);
		if (memberInfo == null) {
			UIHelper.ToastMessage(this, R.string.cricle_manage_networkerror);
		}
		this.circleMemberInfo = memberInfo;
		AnoyomousUtils.setCurrentPrivateHeadIcon(memberInfo.getImage(),interest_id+"");
		//aQuery.id(iv_circle_my_photo).image(circleMemberInfo.getImage(), true,true, 0, R.drawable.default_head);
              PhotoUtils.showCard( PhotoUtils.UriType.HTTP, this.circleMemberInfo.getImage(),iv_circle_my_photo, MyDisplayImageOption.options);
              tv_circle_my_nickname.setText(this.circleMemberInfo.getNickname());
		tv_circle_my_signature.setText(this.circleMemberInfo.getSignature());
		tb_circle_protect_setting
				.setChecked(this.circleMemberInfo.getIs_private() == 1);
        if(isFirst && this.circleMemberInfo.getIs_private() == 0){
            isFirst = false;
        }
		isadmin = this.circleMemberInfo.isIs_admin();
	}

	private void openPrivateSetting() {
//		CircleSetPrivateRequest circleSetPrivate = new CircleSetPrivateRequest(CIRCLESETPRIVATE_REQUESTID, CircleMemberSettingActivity.this);
//		circleSetPrivate.setParams(interest_id,1,token);
//		CMainHttp.getInstance().doRequest(circleSetPrivate);
		CircleSetPrivateRequest.send(HttpCommon.CIRCLE_SETPRIVATE_REQUESTID,this,interest_id, Constant.MEMBER_PRIVATE_YES,token);
//		http.updatePrivateInfoSetting(interest_id, 1, token);
	}

	private void closePrivateSetting() {
//		http.updatePrivateInfoSetting(interest_id, 0, token);
//		CircleSetPrivateRequest circleSetPrivate = new CircleSetPrivateRequest(CIRCLESETPRIVATE_REQUESTID, CircleMemberSettingActivity.this);
//		circleSetPrivate.setParams(interest_id,0,token);
//		CMainHttp.getInstance().doRequest(circleSetPrivate);
		CircleSetPrivateRequest.send(HttpCommon.CIRCLE_SETPRIVATE_REQUESTID,this,interest_id, Constant.MEMBER_PRIVATE_NO,token);
	}

	public void updatePrivateInfoSettingSuccess(HttpJsonResponse res) {
		if (pd != null) {
			pd.dismiss();
		}
//		boolean isSucc = isRequestSuccess(res);
//		if (isSucc) {
			if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
				UIHelper.ToastMessage(this, R.string.cricle_manage_networkerror);
				return;
			}
			getCircleMemberInfo();
//		} else {
//			UIHelper.ToastMessage(this,
//				R.string.cricle_isprivate_edit_setting_failed);
//		}
	}

	@Override
	public void onClick(View v) {

		if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
			UIHelper.ToastMessage(this, R.string.cricle_manage_networkerror);
			return;
		}

		switch (v.getId()) {
		case R.id.btn_circle_quit:
			if (isadmin) {
				UIHelper.ToastMessage(this,
						R.string.cricle_admin_no_quit_setting_text);
				return;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(
					CircleMemberSettingActivity.this);
			builder.setTitle(R.string.cricle_manage_upload_quit_dialog_title);
			builder.setMessage(R.string.cricle_manage_upload_quit_dialog_content);
			builder.setPositiveButton(
					R.string.cricle_manage_edit_quit_dialog_confirm,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							pd.setMessage(getResources().getString(
									R.string.cricle_manage_update_logouting));
							pd.show();
//							CircleExitCircleRequest circleExitCircle = new CircleExitCircleRequest(HttpCommon.CIRCLE_EXITCIRCLE_REQUESTID,CircleMemberSettingActivity.this);
//							circleExitCircle.setParams(interest_id,token);
//							CMainHttp.getInstance().doRequest(circleExitCircle);
							CircleExitCircleRequest.send(HttpCommon.CIRCLE_EXITCIRCLE_REQUESTID,CircleMemberSettingActivity.this,interest_id,token,
									ZSSdkUtil.MANAGE_SUBSCRIBE_GROUP);
//							http.updateQuitCricle(interest_id, token);
						}
					});
			builder.setNegativeButton(
					R.string.cricle_manage_edit_quit_dialog_cancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			builder.create().show();

			break;
		case R.id.rl_circle_my_nickname:
			if (!tb_circle_protect_setting.isChecked()) {
				UIHelper.ToastMessage(this,
						R.string.cricle_isprivate_noopen_setting_text);
				return;
			}
			if(circleMemberInfo == null){
				UIHelper.ToastMessage(this,"服务器还未返回信息");
				return;
			}
			String nickname = circleMemberInfo.getNickname();

			Intent editNickNameIntent = new Intent();
			editNickNameIntent.setClass(CircleMemberSettingActivity.this,
					CircleManageNikeNameSettingActivity.class);
			editNickNameIntent.putExtra("nickname", nickname);
			editNickNameIntent.putExtra("interest_id", interest_id);
			editNickNameIntent.putExtra("oper_type",
					CRICLE_MANAGE_NICKNAME_SETTING);
			editNickNameIntent.putExtra("token", token);
			startActivityForResult(editNickNameIntent, 1);
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
			break;
		case R.id.rl_circle_my_photo:
			if (!tb_circle_protect_setting.isChecked()) {
				UIHelper.ToastMessage(this,
						R.string.cricle_isprivate_noopen_setting_text);
				return;
			}
			if(circleMemberInfo == null){
				UIHelper.ToastMessage(this,"服务器还未返回信息");
				return;
			}
			ShowPickDialog();
			break;
		case R.id.rl_circle_my_signature:
			if (!tb_circle_protect_setting.isChecked()) {
				UIHelper.ToastMessage(this,
						R.string.cricle_isprivate_noopen_setting_text);
				return;
			}
			if(circleMemberInfo == null){
				UIHelper.ToastMessage(this,"服务器还未返回信息");
				return;
			}
			String signature = circleMemberInfo.getSignature();

			Intent editSingatrueIntent = new Intent();
			editSingatrueIntent.setClass(CircleMemberSettingActivity.this,
					CircleManageSignatureSettingActivity.class);
			editSingatrueIntent.putExtra("signature", signature);
			editSingatrueIntent.putExtra("interest_id", interest_id);
			editSingatrueIntent.putExtra("oper_type",
					CRICLE_MANAGE_SIGNATURE_SETTING);
			editSingatrueIntent.putExtra("token", token);
			startActivityForResult(editSingatrueIntent, 0);
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView,
			final boolean isChecked) {
		gIsChecked = isChecked;
		switch (buttonView.getId()) {
		case R.id.tb_circle_protect_setting:
			if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
				tb_circle_protect_setting.setChecked(!isChecked);
				UIHelper.ToastMessage(this, R.string.cricle_manage_networkerror);
				return;
			}

			if (isChecked) {
                if(isFirst){
                    isFirst = false;
                }else{
//                    http.updatePrivateInfoSetting(interest_id, 1, token);
//					CircleSetPrivateRequest circleSetPrivate = new CircleSetPrivateRequest(CIRCLESETPRIVATE_REQUESTID, CircleMemberSettingActivity.this);
//					circleSetPrivate.setParams(interest_id,1,token);
//					CMainHttp.getInstance().doRequest(circleSetPrivate);
					CircleSetPrivateRequest.send(HttpCommon.CIRCLE_SETPRIVATE_REQUESTID,CircleMemberSettingActivity.this,interest_id, Constant.MEMBER_PRIVATE_YES,token);
                }
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						CircleMemberSettingActivity.this);
				builder.setTitle(R.string.cricle_isprivate_edit_setting_dialog_title);
				builder.setMessage(R.string.cricle_isprivate_edit_setting_dialog_msg);
				builder.setPositiveButton(
						R.string.cricle_manage_edit_quit_dialog_confirm,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
//								http.updatePrivateInfoSetting(interest_id, 0,
//										token);
//								CircleSetPrivateRequest circleSetPrivate = new CircleSetPrivateRequest(HttpCommon.CIRCLE_SETPRIVATE_REQUESTID, CircleMemberSettingActivity.this);
//								circleSetPrivate.setParams(interest_id,0,token);
//								CMainHttp.getInstance().doRequest(circleSetPrivate);
								CircleSetPrivateRequest.send(HttpCommon.CIRCLE_SETPRIVATE_REQUESTID,CircleMemberSettingActivity.this,interest_id, Constant.MEMBER_PRIVATE_NO,token);
							}
						});
				builder.setNegativeButton(
						R.string.cricle_manage_edit_quit_dialog_cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
                                isFirst = true;
								tb_circle_protect_setting
										.setChecked(!isChecked);
								dialog.dismiss();
							}
						});

				builder.create().show();
			}

			break;
		default:
			break;
		}
	}


//	public static boolean isRequestSuccess(HttpJsonResponse res) {
//		int statusCode = res.getCode();
//		if (statusCode != 200) {
//			return false;
//		}
//		JsonObject body = res.getBody();
//		JsonElement result = body.get("result");
//		boolean isSucc = result.getAsBoolean();
//		return isSucc;
//	}

	/**
	 * 选择提示对话框
	 */
	public void ShowPickDialog() {
		String shareDialogTitle = getString(R.string.cricle_manage_pick_dialog_title);
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
									if (Utils
											.isIntentSafe(
													CircleMemberSettingActivity.this,
													i)) {
										startActivityForResult(i, 2);
									} else {
										SouYueToast
												.makeText(
														CircleMemberSettingActivity.this,
														getString(R.string.dont_have_camera_app),
														SouYueToast.LENGTH_SHORT)
												.show();
									}
								} else {
									SouYueToast
											.makeText(
													CircleMemberSettingActivity.this,
													getString(R.string.cant_insert_album),
													SouYueToast.LENGTH_SHORT)
											.show();
								}
							} catch (Exception e) {
								SouYueToast.makeText(
										CircleMemberSettingActivity.this,
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
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:// 如果是直接从相册获取
				if (data != null) {
					Uri uri = data.getData();
					startPhotoZoom(uri);
				}
				break;
			case 2:// 如果是调用相机拍照时
				String picPath = null;
				if (imageFileUri != null) {
					picPath = Utils.getPicPathFromUri(imageFileUri, this);
					int degree = 0;
					if (!StringUtils.isEmpty(picPath))
						degree = ImageUtil.readPictureDegree(picPath);
					Matrix matrix = new Matrix();
					if (degree != 0) {// 解决旋转问题
						matrix.preRotate(degree);
					}
					LogDebugUtil.v("Huang", "imageFileUri != null--picPath="
							+ picPath);
					Uri uri = Uri.fromFile(new File(picPath));
					startPhotoZoom(uri);
				} else {
					SouYueToast.makeText(this, "图片获取异常",
							SouYueToast.LENGTH_SHORT).show();
				}
				break;
			case 3:// 取得裁剪后的图片
				if (data != null) {
					setPicToView(data);
				}
				break;
			}
		} else if (resultCode == CircleManageSignatureSettingActivity.RESULT_CODE_EDIT_SIGNATRUE_SUCC) {
			String new_signature = data.getStringExtra("NEW_SIGNATURE");
			tv_circle_my_signature.setText(new_signature);
			circleMemberInfo.setSignature(new_signature);
		} else if (resultCode == CircleManageNikeNameSettingActivity.RESULT_CODE_EDIT_NIKENAME_SUCC) {
			String new_nikename = data.getStringExtra("NEW_NIKENAME");
			tv_circle_my_nickname.setText(new_nikename);
			circleMemberInfo.setNickname(new_nikename);
		}
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 100);
		intent.putExtra("outputY", 100);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		pd.setMessage(getResources().getString(R.string.data_loading));
		pd.show();
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
            int newWidth = 100;
            if (photo.getWidth() >= 100) {
                newWidth = photo.getWidth();
            }
            int newHeight = 100;
            if (photo.getHeight() >= 100) {
                newHeight = photo.getHeight();
            }
            if (photo.getWidth() < 100 || photo.getHeight() < 100) {
//                Bitmap newBitmap = Bitmap.createBitmap(newWidth, newHeight, photo.getConfig());
//                Canvas canvas = new Canvas(newBitmap);
//                canvas.drawBitmap(photo, null, new Rect(0, 0, newBitmap.getWidth(), newBitmap.getHeight()), null);
//                Bitmap.createBitmap(photo, 0, 0, newWidth, newHeight);
                photo = Bitmap.createScaledBitmap(photo, newWidth, newHeight, false);
            }
			drawable = new BitmapDrawable(photo);
			try {
				photo.compress(Bitmap.CompressFormat.JPEG, 100,
						new FileOutputStream(profileImgFile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			boolean exit = profileImgFile.exists();
			LogDebugUtil.v("secret",
					"setPicToView URL: " + profileImgFile.getAbsolutePath());
			if (!exit) {
				UIHelper.ToastMessage(this,
						R.string.cricle_manage_upload_photo_fail);
				return;
			}
			UploadImageTask.executeTask(this, 0l, profileImgFile);
		}
	}

	// ==============================服务回调方法=========================
	/**
	 * 上传头像图片回调方法
	 * 
	 * @param url
	 *            返回服务器存放的图片资源的url
	 */
	public void uploadSuccess(String url) {
		if (profileImgFile.exists()) {
			profileImgFile.delete();
		}
		if (!TextUtils.isEmpty(url)) {
			imageUrl = url;
//			CircleSetUserInfoRequest circleSetUserInfo = new CircleSetUserInfoRequest(CIRCLESETUSERINFO_REQUESTID, this);
//			circleSetUserInfo.setParams(interest_id, CRICLE_MANAGE_PHOTO_SETTING, imageUrl, token);
//			CMainHttp.getInstance().doRequest(circleSetUserInfo);
			CircleSetUserInfoRequest.send(HttpCommon.CIRCLE_SETUSERINFO_REQUESTID_HEAD,this,interest_id, CRICLE_MANAGE_PHOTO_SETTING, imageUrl, token);
//			http.uploadCricleManagePhoto(interest_id,
//					CRICLE_MANAGE_PHOTO_SETTING, imageUrl, token);
		} else {
			UIHelper.ToastMessage(this,
					R.string.cricle_manage_upload_photo_fail);
		}
	}

	@Override
	public void onHttpResponse(IRequest request) {
		switch (request.getmId()) {
			case HttpCommon.CIRCLE_SETUSERINFO_REQUESTID_HEAD:
				uploadCricleManagePhotoSuccess(request.<HttpJsonResponse>getResponse());
				break;
			case HttpCommon.CIRCLE_EXITCIRCLE_REQUESTID:
				updateQuitCricleSuccess(request.<HttpJsonResponse>getResponse());
				break;
			case HttpCommon.CIRCLE_SETPRIVATE_REQUESTID:
				updatePrivateInfoSettingSuccess(request.<HttpJsonResponse>getResponse());
				break;
			case HttpCommon.CIRCLE_GETCIRCLEMEBERINFO_REQUESTID:
				getCircelMemberInfoSuccess(request.<HttpJsonResponse>getResponse());
				break;
		}
	}

	@Override
	public void onHttpError(IRequest request) {
		switch (request.getmId()){
			case HttpCommon.CIRCLE_GETCIRCLEMEBERINFO_REQUESTID:
				Toast.makeText(this,"获取信息失败",Toast.LENGTH_LONG).show();
				break;
			case HttpCommon.CIRCLE_SETUSERINFO_REQUESTID_HEAD:
				UIHelper.ToastMessage(this,
						R.string.cricle_manage_upload_photo_fail);
				break;
			case HttpCommon.CIRCLE_EXITCIRCLE_REQUESTID:
				UIHelper.ToastMessage(this,R.string.cricle_manage_upload_quit_failed);
				break;
			case HttpCommon.CIRCLE_SETPRIVATE_REQUESTID:
				UIHelper.ToastMessage(this,
						R.string.cricle_isprivate_edit_setting_failed);
				break;
		}
	}
	/**
	 * 修改头像回调方法
	 * @param res
	 */
	public void uploadCricleManagePhotoSuccess(HttpJsonResponse res) {
		if (pd != null) {
			pd.dismiss();
		}
//		boolean isSucc = isRequestSuccess(res);
		iv_circle_my_photo.setImageDrawable(drawable);

//		if (isSucc) {
			if (!TextUtils.isEmpty(imageUrl)) {
				circleMemberInfo.setImage(imageUrl);
				//aQuery.id(iv_circle_my_photo).image(circleMemberInfo.getImage(), true,true, 0, R.drawable.default_head);
                                  PhotoUtils.showCard(PhotoUtils.UriType.HTTP,circleMemberInfo.getImage(),iv_circle_my_photo,MyDisplayImageOption.options);
				UIHelper.ToastMessage(this,
						R.string.cricle_manage_upload_photo_success);
			}
//		} else {
//			UIHelper.ToastMessage(this,
//					R.string.cricle_manage_upload_photo_fail);
//		}

	}

	/**
	 * 圈子管理-退出圈子回调方法
	 * 
	 * @param res
	 */
	public void updateQuitCricleSuccess(HttpJsonResponse res) {
		if (pd != null) {
			pd.dismiss();
		}

//		JsonObject body = res.getBody();
//		JsonElement result = body.get("result");
//		int isSucc = result.getAsInt();
//		if (isSucc == 200) {

			// 统计
			UpEventAgent.onGroupQuit(this, interest_id + "."+"", "");
			UIHelper.ToastMessage(this,
					R.string.cricle_manage_upload_quit_success);
			SYSharedPreferences.getInstance().putBoolean(
					SYSharedPreferences.KEY_UPDATE, true);
//			if(!ConfigApi.isSouyue()){
//			    //退出兴趣圈，跳转到搜悦新闻首页
//			    LocalBroadCastUtil.sendLoginToNewsHome(this);
//			}
            Intent intent = new Intent();
            intent.putExtra("isQuit", true);
            intent.putExtra("interestType", interestType);
            setResult(RESULT_OK, intent);

			//ZhongSouActivityMgr.getInstance().goHome();
			finish();
//		} else {
//			UIHelper.ToastMessage(this,
//					R.string.cricle_manage_upload_quit_failed);
//			return;
//		}
	}

//    @Override
//    public void onHttpError(String methodName, AjaxStatus status) {
//       if(methodName.equals("getCircelMemberInfo")){
//           Toast.makeText(this,"获取信息失败",Toast.LENGTH_LONG).show();
//       }
//    }

}
