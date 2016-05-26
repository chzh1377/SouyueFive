package com.zhongsou.souyue.circle.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.upyun.api.IUpYunConfig;
import com.upyun.api.Uploader;
import com.upyun.api.utils.UpYunException;
import com.upyun.api.utils.UpYunUtils;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.circle.adapter.PublishBlogImageAdapter;
import com.zhongsou.souyue.circle.adapter.SelectedFriendAdapter;
import com.zhongsou.souyue.circle.model.CircleMemberItem;
import com.zhongsou.souyue.circle.model.CircleResponseResultItem;
import com.zhongsou.souyue.circle.model.InterestTag;
import com.zhongsou.souyue.circle.model.Posts;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.im.emoji.EmojiPattern;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CircleGetMeberRoleRequest;
import com.zhongsou.souyue.net.circle.CircleGetTagsRequest;
import com.zhongsou.souyue.net.circle.CircleSaveBlogRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.service.ZSAsyncTask;
import com.zhongsou.souyue.ui.HorizontalListView;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.uikit.MMAlert;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Desc: 帖子发布/编辑
 * User: tiansj
 * DateTime: 14-4-17 下午3:13
 */
public class PublishActivity extends RightSwipeActivity implements View.OnClickListener {

    public static final String ACTION_NEW_POST = "ACTION_NEW_POST";
    public static final String NEW_POST_MIMI_TYPE = "post/new";

    public static final String ACTION_KEY_RESPONSEITEM = "ACTION_KEY_RESPONSEITEM";

    public static final int MESSAGE_WHAT_DISMISS_PROGRESS_DIALOG = 0;
    private static final int TEXT_MAX = 10000;

    // publish_type  1发布主贴，2编辑主题，3发布跟帖，4编辑跟帖
    public static final int PUBLISH_TYPE_M_NEW = 1;
    public static final int PUBLISH_TYPE_M_EDIT = 2;
    public static final int PUBLISH_TYPE_C_NEW = 3;
    public static final int PUBLISH_TYPE_C_EDIT = 4;

    private TextView tv_nickname;
    private TextView title_textview;
    private EditText et_title;
    private EditText et_content;    //编辑内容
    private Uri imageFileUri;
    private TextView tv_childcount;
    // 改进后的属性
    private List<String> bolgImageList;
    private HorizontalListView bolgImageHorList;
    private HorizontalListView friendHorListView;
    private PublishBlogImageAdapter bolgImageAdapter;
    private SelectedFriendAdapter selectedFriendAdapter;
    private ImageButton sel_friend, sel_photo;
    private Button btn_send_niming;
    private ArrayList<CircleMemberItem> selMembers;

    private String title;
    private String content;                    // 评论内容
    private int totalImgCount;                // 上传图片的总数量
    private static int uploadedImgCount;    // 当前已经上传图片的数量
    private List<String> uploadedImgUrls;    // 上传成功图片的URL
    private String user_ids;
    private ProgressDialog progressDialog;
    private String uid;
    private boolean is_sending;

    private Posts posts;
    private long interest_id;
    private int publish_type; //是否主贴
    private Posts publishPosts;
    private boolean is_from_list_publish;
    private boolean hasChange;

    private String srp_id;
    private String srpWord;
    private List<InterestTag> interestTags;
    private InterestTag selectedTag;

    private String tag_id; //从圈吧微件中带过来的tag_id;
    private String nickName; //5.0匿名发帖新加

    private List<File> imgeList = new ArrayList<File>();

    static final String TAG = "PublishActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_posts_publish);

        Intent intent = getIntent();
        posts = (Posts) intent.getSerializableExtra("posts");
        srp_id = intent.getStringExtra("srp_id");
        srpWord = intent.getStringExtra("srpWord");
        interestTags = new ArrayList<InterestTag>();
        interest_id = intent.getLongExtra("interest_id", 0);
        publish_type = intent.getIntExtra("publish_type", 0);
        nickName = getIntent().getStringExtra("nickName");

        is_from_list_publish = intent.getBooleanExtra("is_from_list_publish", false);

        tag_id = intent.getStringExtra("tag_id");

        uploadedImgUrls = new ArrayList<String>();
        publishPosts = new Posts();
        uid = SYUserManager.getInstance().getUserId();

        initViews();
//        initDefaultSelectedTag();
        CircleGetTagsRequest.send(HttpCommon.CIRCLE_GETTAGS_REQUESTID, this, srp_id);
        if (tag_id != null) {
            if (selectedTag == null) {
                selectedTag = new InterestTag();
            }
            selectedTag.setId(tag_id);
        }
        getRole();
    }


    private void getRole() {
        CircleGetMeberRoleRequest.send(HttpCommon.CIRCLE_GETMEBERROLE_REQUESTID, this,
                SYUserManager.getInstance().getToken(), interest_id);
    }


    public void getMemberRoleSuccess(HttpJsonResponse res) {
        nickName = res.getBody().get("nickname").getAsString();
        tv_nickname.setText("(" + nickName + ")");
    }

    private void initViews() {
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        btn_send_niming = findView(R.id.tv_send_niming);
        btn_send_niming.setOnClickListener(this);
        title_textview = findView(R.id.post_title_textview);
        if (publish_type == PUBLISH_TYPE_M_EDIT || publish_type == PUBLISH_TYPE_C_EDIT) {
            btn_send_niming.setVisibility(View.GONE);
            title_textview.setText("编辑");
        } else if (publish_type == PUBLISH_TYPE_C_NEW) {
            title_textview.setText("跟帖");
        }

        ImageButton goBack = (ImageButton) findViewById(R.id.goBack);
        goBack.setOnClickListener(this);

        TextView tv_send = findView(R.id.tv_send);
        tv_send.setOnClickListener(this);

        sel_photo = findView(R.id.sel_photo);
        sel_friend = findView(R.id.sel_friend);
        sel_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showCircleFriend(PublishActivity.this, interest_id, selMembers);
            }
        });
        if (publish_type == PUBLISH_TYPE_C_NEW || publish_type == PUBLISH_TYPE_C_EDIT || publish_type == PUBLISH_TYPE_M_EDIT) {
            sel_friend.setVisibility(View.GONE);
        }

        sel_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_sending) {
                    return;
                }
                ShowPickDialog();
            }
        });

        et_title = findView(R.id.et_title);
        et_title.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
        et_content = (EditText) findViewById(R.id.et_content);

        tv_childcount = (TextView) findViewById(R.id.tv_childcount);
        bolgImageHorList = (HorizontalListView) findViewById(R.id.bolg_gallery);
        bolgImageList = new ArrayList<String>();

        bolgImageAdapter = new PublishBlogImageAdapter(this, bolgImageList);
        boolean isEdit = publish_type == PUBLISH_TYPE_M_EDIT || publish_type == PUBLISH_TYPE_C_EDIT;
        if (isEdit && posts != null && posts.getBlog_id() > 0) {
            if (StringUtils.isNotEmpty(posts.getTitle())) {
                et_title.setText(posts.getTitle());
            }
            if (StringUtils.isNotEmpty(posts.getContent())) {
                et_content.setText(EmojiPattern.getInstace().getExpressionString(this, posts.getContent()));
                et_content.setSelection(posts.getContent().length());
            }
            if (posts.getImages() != null && posts.getImages().size() > 0) {
                for (String s : posts.getImages()) {
                    bolgImageAdapter.addItemPaht(s);
                }
            }
        }

        bolgImageHorList.setAdapter(bolgImageAdapter);
        bolgImageHorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String str = ((PublishBlogImageAdapter.ViewHolder) view.getTag()).bolgImagePath;
                showDeleteAlert(str);
            }
        });


        friendHorListView = (HorizontalListView) findViewById(R.id.friend_list_view);
        selMembers = new ArrayList<CircleMemberItem>();
        if (publish_type == PUBLISH_TYPE_M_EDIT) {
            selMembers = posts.getSelMembers();
            if (null != selMembers && selMembers.size() != 0) {
                findView(R.id.layout_friend).setVisibility(View.VISIBLE);
                findView(R.id.view_friend_divider).setVisibility(View.VISIBLE);
            }
        }
        selectedFriendAdapter = new SelectedFriendAdapter(this, selMembers);
        friendHorListView.setAdapter(selectedFriendAdapter);
        friendHorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (selMembers.size() > position) {
                    if (publish_type == PUBLISH_TYPE_M_EDIT) {
                        return;
                    }
                    //删除提示
                    Dialog alertDialog = new AlertDialog.Builder(PublishActivity.this)
                            .setMessage("确认删除吗?")
                            .setPositiveButton("确定", new OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    selMembers.remove(position);
                                    selectedFriendAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("取消", new OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    alertDialog.show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goBack:
                finish();
                break;
            case R.id.tv_send:
                if (publish_type == PUBLISH_TYPE_M_EDIT && posts.getPosting_state() == 1) {
                    senClick(1);
                } else {
                    senClick(0);
                }
                break;
            case R.id.tv_send_niming:
                senClick(1);
                break;
            default:
                break;
        }
    }

    private void senClick(int posting_state) {
        if (is_sending) {
            return;
        }
        if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
            UIHelper.ToastMessage(this, R.string.networkerror);
            return;
        }
        title = et_title.getText().toString();
        content = et_content.getText().toString();
        if (!IntentUtil.isLogin()) {
            IntentUtil.goLogin(this, true);
            return;
        }

        if (checkData()) {
            if (publish_type == PUBLISH_TYPE_M_NEW && interestTags.size() > 0 && tag_id == null) {
                new SelectTagDialog(this, posting_state).show();
            } else {
                save(posting_state);
            }
        }
    }

    private void save(int posting_state) {
        is_sending = true;
        showProcessDialog();
        uploadedImgUrls = bolgImageAdapter.getBolgImageList();
        totalImgCount = bolgImageList.size();
        if (totalImgCount == 0) {
            savePostsInfo(posting_state);
        } else {
            UploadTask uploadTask = new UploadTask(posting_state);
            uploadTask.execute(bolgImageAdapter.getBolgImageList());
        }
    }

    private boolean checkData() {
        if (title.length() > 32) {
            showToast(R.string.circle_self_bolg_title_count_long);
            return false;
        }
        if (content == null || content.trim().length() == 0) {
            showToast(R.string.self_content_input);
            return false;
        }
        if (content.length() > TEXT_MAX) {
            showToast(R.string.self_bolg_content_count_long);
            return false;
        }
        List<String> bolgImageList = bolgImageAdapter.getBolgImageList();
        if (StringUtils.isEmpty(title) && StringUtils.isEmpty(content) && (bolgImageList == null || bolgImageList.size() == 0)) {
            showToast(R.string.self_content_input);
            return false;
        }
        return true;
    }

    /**
     * 保存评论信息到服务器
     *
     * @author tiansj
     * @date 2013-10-25
     * void
     */
    private void savePostsInfo(int posting_state) {
        String images = new Gson().toJson(uploadedImgUrls);
        List<String> s_uploadedImgUrls = new ArrayList<String>();
        if (uploadedImgUrls != null) {
            for (String imgUrl : uploadedImgUrls) {
                if (imgUrl.contains("upaiyun.com")) {
                    s_uploadedImgUrls.add(imgUrl);
                } else {
                    s_uploadedImgUrls.add(imgUrl);
                }
            }
        }
        String s_images = new Gson().toJson(s_uploadedImgUrls);
        long mblog_id = 0;
        long blog_id = posts == null ? 0 : posts.getBlog_id();
        if (publish_type == PUBLISH_TYPE_M_EDIT || publish_type == PUBLISH_TYPE_C_NEW) {   // 是否跟帖
            mblog_id = posts.getMblog_id();
        }
        if (publish_type == PUBLISH_TYPE_C_NEW) {
            blog_id = 0;
        }

        user_ids = "";
        if (selMembers != null && selMembers.size() > 0) {
            for (CircleMemberItem item : selMembers) {
                if ("".equals(user_ids)) {
                    user_ids += item.getUser_id();
                } else {
                    user_ids += "," + item.getUser_id();
                }
            }
        }
        long user_id = 0;

        if (blog_id > 0) {
            user_id = posts.getUser_id();
            publishPosts.setUser_id(posts.getUser_id());
            publishPosts.setImage_url(posts.getImage_url());
            publishPosts.setNickname(posts.getNickname());
            publishPosts.setIs_mblog(posts.getIs_mblog());
        } else {
            user_id = Long.valueOf(SYUserManager.getInstance().getUserId());
            publishPosts.setUser_id(user_id);
        }
        publishPosts.setMblog_id(mblog_id);
        publishPosts.setBlog_id(blog_id);
        if (publish_type == PUBLISH_TYPE_M_NEW || publish_type == PUBLISH_TYPE_C_NEW) {
            publishPosts.setCreate_time(System.currentTimeMillis() + "");
        } else {
            if (posts != null) {
                publishPosts.setCreate_time(posts.getCreate_time());
                publishPosts.setFloor_num(posts.getFloor_num());
            }
        }
        publishPosts.setImages(s_uploadedImgUrls);
        publishPosts.setTitle(title);
        publishPosts.setContent(content);

        String id = "";
        if (selectedTag != null) {
            id = selectedTag.getId();
        }
        CircleSaveBlogRequest.send(HttpCommon.CIRCLE_SAVEBLOG_REQUESTID, this,
                mblog_id, blog_id, interest_id, title, content,
                s_images, user_ids, id, posting_state);
    }


    @Override
    public void onHttpResponse(IRequest _request) {
        super.onHttpResponse(_request);
        switch (_request.getmId()) {
            case HttpCommon.CIRCLE_SAVEBLOG_REQUESTID:
                savePostsInfoSuccess(_request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRCLE_GETMEBERROLE_REQUESTID:
                getMemberRoleSuccess(_request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRCLE_GETTAGS_REQUESTID:
                getInterestTagsSuccess(_request.<HttpJsonResponse>getResponse());
                break;
        }
    }

    @Override
    public void onHttpError(IRequest _request) {
        IHttpError error = _request.getVolleyError();
        HttpJsonResponse res = _request.<HttpJsonResponse>getResponse();
        if (error.getErrorType() == IHttpError.TYPE_SERVER_ERROR) {
            switch (_request.getmId()) {
                case HttpCommon.CIRCLE_SAVEBLOG_REQUESTID:
                    dismissProcessDialog();
                    if (error.getErrorCode() == 500) {
//                        new HttpContextImpl("savePostsInfo").onJsonCodeError(res); // json状态码错误
                        is_sending = false;
                        return;
                    }
                    if (error.getErrorCode() != 200) {
                        UIHelper.ToastMessage(this, error.getmErrorMessage());
                        is_sending = false;
                        return;
                    }
                    break;
                default:
                    if (error.getErrorCode() != 200) {
                        UIHelper.ToastMessage(this, "网络异常，请重试！");
                    }
                    is_sending = false;
                    dismissProcessDialog();
            }
        } else {
            UIHelper.ToastMessage(this, "网络异常，请重试！");
        }
    }

    public void savePostsInfoSuccess(HttpJsonResponse res) {
        int state = res.getBody().get("state").getAsInt();
        dismissProcessDialog();
        is_sending = false;
        if (state == 1) {
            et_content.setText("");
            bolgImageAdapter.clearBolgImageItem();
            bolgImageAdapter.notifyDataSetChanged();

            UIHelper.ToastMessage(this, "发送成功！");

            // 回传发帖内容
            if (publish_type == PUBLISH_TYPE_M_NEW) {

                long blog_id = res.getBody().get("blog_id").getAsLong();
                publishPosts.setBlog_id(blog_id);
                //统计  圈贴原创
                UpEventAgent.onGroupPublish(this, interest_id + "." + "", "", publishPosts.getTitle(), blog_id + "");
                CircleResponseResultItem item = wrapCricleResponseResultItem(publishPosts);
                int point = res.getBody().get("point").getAsInt();
                SearchResultItem item1 = new SearchResultItem();
                item1.setBlog_id(item.getBlog_id());
                item1.setInterest_id(item.getInterest_id());
                item1.keyword_$eq(srpWord);
                item1.srpId_$eq(srp_id);
                if(selectedTag == null){
                    IntentUtil.startskipDetailPage(PublishActivity.this, item1, point, "", "默认", true);
                }else{
                    IntentUtil.startskipDetailPage(PublishActivity.this, item1, point, selectedTag.getId(), selectedTag.getTag_name(), true);
                }
            } else if (publish_type == PUBLISH_TYPE_M_EDIT) {
                Intent data = getIntent();
                publishPosts.setSelMembers(selMembers);
                data.putExtra("publishPosts", publishPosts);
                data.putExtra("publish_type", publish_type);
                setResult(UIHelper.RESULT_OK, data);
            } else if (publish_type == PUBLISH_TYPE_C_NEW) {
                long blog_id = res.getBody().get("blog_id").getAsLong();
                publishPosts.setBlog_id(blog_id);
                //统计  圈贴跟帖
                UpEventAgent.onGroupComment(this, interest_id + "." + "", "", posts.getBlog_id() + "");
                publishPosts.setGood_num("0");
                Intent data = new Intent();
                data.putExtra("publishPosts", publishPosts);
                data.putExtra("publish_type", publish_type);
                if (is_from_list_publish) {
                    hasChange = true;
                    finishSendBroad();
                }
                setResult(UIHelper.RESULT_OK, data);

            } else if (publish_type == PUBLISH_TYPE_C_EDIT) {
                Intent data = new Intent();
                publishPosts.setHas_praised(posts.isHas_praised());
                publishPosts.setGood_num(posts.getGood_num());
                data.putExtra("publishPosts", publishPosts);
                data.putExtra("publish_type", publish_type);
                setResult(UIHelper.RESULT_OK, data);
            } else {
                setResult(UIHelper.RESULT_OK, null);
            }
            finish();
        } else {
            if (res.getBody().has("is_bantalk")) {
                int isbantalk = res.getBody().get("is_bantalk").getAsInt();
                if (isbantalk == 1) {
                    UIHelper.ToastMessage(this, "您已被禁言！");
                    return;
                }
            }

            UIHelper.ToastMessage(this, "发送失败，请重试！");
        }
    }

    /**
     * 选择提示对话框
     */
    public void ShowPickDialog() {
        List<String> bolgImageList = bolgImageAdapter.getBolgImageList();
        if (bolgImageList.size() >= 9) {
            UIHelper.ToastMessage(this, "最多上传9张图片");
            return;
        }
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
                                                MediaStore.ACTION_IMAGE_CAPTURE);
                                        i.putExtra(
                                                MediaStore.EXTRA_OUTPUT,
                                                imageFileUri);
                                        if (Utils.isIntentSafe(
                                                PublishActivity.this, i)) {
                                            startActivityForResult(i, 2);
                                        } else {
                                            SouYueToast
                                                    .makeText(
                                                            PublishActivity.this,
                                                            getString(R.string.dont_have_camera_app),
                                                            SouYueToast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    } else {
                                        SouYueToast
                                                .makeText(
                                                        PublishActivity.this,
                                                        getString(R.string.cant_insert_album),
                                                        SouYueToast.LENGTH_SHORT)
                                                .show();
                                    }
                                } catch (Exception e) {
                                    SouYueToast.makeText(PublishActivity.this,
                                            getString(R.string.cant_insert_album),
                                            SouYueToast.LENGTH_SHORT).show();
                                }
                                break;
                            case 1: // 相册
                                Intent intent = new Intent(PublishActivity.this, CircleSelImgGroupActivity.class);
                                int len = bolgImageAdapter.getBolgImageList().size();
                                intent.putExtra("piclen", len);
                                startActivityForResult(intent, 1);

                                break;
                            default:
                                break;
                        }
                    }

                });
    }

    private void showDeleteAlert(final String path) {
        Dialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_del_sure))
                .setMessage(getString(R.string.dialog_del_sure_des))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(getString(R.string.dialog_del),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PublishBlogImageAdapter.isAdd = false;
                                bolgImageAdapter.clearBolgImageItem(path);
                                bolgImageAdapter.notifyDataSetChanged();
                            }
                        })
                .setNegativeButton(getString(R.string.dialog_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String picPath = null;
        if (resultCode == 0x200) {
            if (data != null) {
                List<String> list = data.getStringArrayListExtra("imgseldata");
                addImagePath(list);
            }
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 2:// 如果是调用相机拍照时
                    if (imageFileUri != null) {
                        picPath = Utils.getPicPathFromUri(imageFileUri, PublishActivity.this);
                        int degree = 0;
                        if (!StringUtils.isEmpty(picPath))
                            degree = ImageUtil.readPictureDegree(picPath);
                        Matrix matrix = new Matrix();
                        if (degree != 0) {// 解决旋转问题
                            matrix.preRotate(degree);
                        }
                        Log.v("Huang", "相机拍照imageFileUri != null:" + picPath);
                        ArrayList<String> list = new ArrayList<String>();
                        list.add(picPath);
                        addImagePath(list);

                    } else {
                        showToast(R.string.self_get_image_error);
                    }
                    break;
            }
        } else if (resultCode == UIHelper.RESULT_OK) { // @圈成员好友回调
            if (data == null) {
                return;
            }
            selMembers.clear();
            ArrayList<CircleMemberItem> list = (ArrayList<CircleMemberItem>) data.getSerializableExtra("selMembers");
            if (list != null && list.size() > 0) {
                findView(R.id.layout_friend).setVisibility(View.VISIBLE);
                findView(R.id.view_friend_divider).setVisibility(View.VISIBLE);
                selMembers.addAll((ArrayList<CircleMemberItem>) data.getSerializableExtra("selMembers"));
                selectedFriendAdapter.notifyDataSetChanged();
            } else {
                findView(R.id.layout_friend).setVisibility(View.GONE);
                findView(R.id.view_friend_divider).setVisibility(View.GONE);
                selectedFriendAdapter.notifyDataSetChanged();
            }
        }
    }

    // 上传的图片压缩，存储项目下
    private void addImagePath(List<String> picPath) {

        if (picPath == null || picPath.size() == 0) {
            Toast.makeText(PublishActivity.this, R.string.self_get_image_error, Toast.LENGTH_SHORT).show();
            return;
        }

        for (String picPath1 : picPath) {
            if (StringUtils.isEmpty(picPath1)) {
                continue;
            }
            PublishBlogImageAdapter.isAdd = true;
            bolgImageAdapter.addItemPaht(picPath1);
            bolgImageAdapter.notifyDataSetChanged();
        }

//        new BitmapCompressTask().execute(bolgImageAdapter.getBolgImageList());
        new MyCompressWorker().excute(bolgImageAdapter.getBolgImageList());
    }

    public void showToast(int resId) {
        SouYueToast.makeText(this,
                getResources().getString(resId), 0).show();
    }

    // 获取资源String
    public String getResString(int id) {
        return getResources().getString(id);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_WHAT_DISMISS_PROGRESS_DIALOG) {
                if (progressDialog != null && progressDialog.isShowing() && PublishActivity.this != null && !PublishActivity.this.isFinishing()) {
                    progressDialog.dismiss();
                }
            }
        }
    };

    public void showProcessDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("正在发送...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        } else {
            progressDialog.show();
        }
    }

    public void dismissProcessDialog() {
        hideSoftInput();
        handler.sendEmptyMessage(MESSAGE_WHAT_DISMISS_PROGRESS_DIALOG);
    }

    private void hideSoftInput() {
        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View cView = getCurrentFocus();
        if (cView != null) {
            mInputMethodManager.hideSoftInputFromWindow(cView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private CircleResponseResultItem wrapCricleResponseResultItem(Posts mainPosts) {
        CircleResponseResultItem item = new CircleResponseResultItem();
        item.setBlog_id(mainPosts.getBlog_id());
        item.setNickname(mainPosts.getNickname());
        item.setUser_image(mainPosts.getImage_url());
        item.setTitle(mainPosts.getTitle());
        item.setBrief(mainPosts.getContent());
        item.setCreate_time(mainPosts.getCreate_time());
        item.setInterest_id(interest_id);
        item.setImages(mainPosts.getImages());
        item.setUser_id(Long.valueOf(SYUserManager.getInstance().getUserId()));
        item.setIs_prime(0);        // 是否精华 0||1
        item.setTop_status(0);

        return item;
    }

    private void initDefaultSelectedTag() {
        selectedTag = new InterestTag();
        selectedTag.setChecked(true);
        selectedTag.setSrp_id(srp_id);
        selectedTag.setTag_name("默认");
        selectedTag.setId("");
    }

    public void getInterestTagsSuccess(HttpJsonResponse res) {
        List<InterestTag> serverTags = new Gson().fromJson(res.getBodyArray().toString(),
                new TypeToken<List<InterestTag>>() {
                }.getType());
        if (serverTags == null || serverTags.size() == 0) {
            return;
        }
        interestTags.clear();
//        initDefaultSelectedTag();
        for (int i = 0; i < serverTags.size(); i++) {
            if (tag_id != null && serverTags.get(i).getId().equals(tag_id)) {
                selectedTag.setId(tag_id);
                selectedTag.setTag_name(serverTags.get(i).getTag_name());
            }
        }
//        interestTags.add(selectedTag);
        interestTags.addAll(serverTags);
    }


    class UploadTask extends ZSAsyncTask<List<String>, Void, Boolean> {

        private int posting_state;

        public UploadTask(int posting_state) {
            this.posting_state = posting_state;
        }

        UploadToYun uty;
        private List<String> images = null;

        @Override
        protected void onPreExecute() {
            uty = new UploadToYun();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(List<String>... params) {
            images = params[0];
            if (images != null) {
                uploadPics(uty);// 如果有图片，先将图片上传up云，并拿到图片地址
                return send();// 一切准备就绪 发送原创到中搜服务器
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                savePostsInfo(posting_state);
            } else {
                dismissProcessDialog();
                UIHelper.ToastMessage(PublishActivity.this, "图片上传失败，请重试！");
            }
            is_sending = false;
            //handler.sendEmptyMessage(0);
            super.onPostExecute(result);
        }

        /**
         * 上传图片到up云
         *
         * @param uty
         */
        private void uploadPics(UploadToYun uty) {
            Log.e(TAG, "upload image...");
            for (int i = 0; i < imgeList.size(); i++) {
                File f = imgeList.get(i);
                if (null == f || !f.canRead()) {
                    continue;
                }
                String url = null;
                if (!(f.getAbsolutePath().toLowerCase().contains("http:"))) {
                    url = uty.upload(f);
                }
                if (!StringUtils.isEmpty(url)) {
                    url = IUpYunConfig.HOST_IMAGE + url + "!android";
                    // url = IUpYunConfig.HOST_IMAGE + url;
                    images.set(i, url);
                } else
                    break;
            }
        }

        /**
         * 如果图片上传失败1张，就算全部失败
         */
        private boolean send() {
            if (images != null) {
                boolean b = true;
                for (String str : images) {
                    if (!str.toLowerCase().contains("http:")) {
                        b = false;
                        break;
                    }
                }
                if (b) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }
    }

    class UploadToYun implements IUpYunConfig {
        @Override
        /*public String getSaveKey() {
            return "/selfcreate" + formatDate.format(new Date()) + "/" + uid + "/" + uid + (r.nextInt(8999) + 1000) + ".jpg";
        }*/

        public String getSaveKey() {
            StringBuffer bucket = new StringBuffer(uid + "");
            while (bucket.length() < 8) {
                bucket.insert(0, '0');
            }
            return bucket.insert(4, '/').insert(0, "/user/").append(randomTo4()).append(".jpg").toString();
        }

        public String upload(File file) {
            try {
                String policy = UpYunUtils.makePolicy(getSaveKey(), Uploader.getExpiration(), BUCKET_IMAGE);
                String signature = UpYunUtils.signature(policy + "&" + API_IMAGE_KEY);
                return Uploader.upload(policy, signature, UPDATE_HOST + BUCKET_IMAGE, file);
            } catch (UpYunException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private String randomTo4() {
        String s = "";
        int intCount = 0;
        intCount = (new Random()).nextInt(9999);//
        if (intCount < 1000)
            intCount += 1000;
        s = intCount + "";
        return s;
    }

    private void finishSendBroad() {
        Intent data = new Intent();
        int result_type = 4;
        data.setAction(PublishActivity.ACTION_NEW_POST);
        CircleResponseResultItem item = new CircleResponseResultItem();

        item.setBlog_id(posts.getMblog_id());
        item.setGood_num(posts.getGood_num());
        item.setHas_praised(posts.isHas_praised());

        data.putExtra("resultType", result_type);
        data.putExtra(PublishActivity.ACTION_KEY_RESPONSEITEM, item);
        if (hasChange)
            sendBroadcast(data);
//        finish();
    }


    public class SelectTagDialog extends Dialog {
        private Context context;
        private int posting_state;
        private Button btn_cancel;
        private Button btn_ok;
        private GridView gridView;
        private SelectTagAdapter adapter;

        public SelectTagDialog(Context context, int posting_state) {
            super(context, R.style.ent_confirm_dialog_style);
            this.context = context;
            this.posting_state = posting_state;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setContentView(R.layout.circle_select_tag_dialog);
            gridView = (GridView) findViewById(R.id.gridView);
            btn_ok = (Button) findViewById(R.id.ent_confirm_dialog_ok);
            btn_cancel = (Button) findViewById(R.id.ent_confirm_dialog_cancel);

            adapter = new SelectTagAdapter(context);
            gridView.setAdapter(adapter);
            gridView.requestFocus();
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    for (int j = 0, len = interestTags.size(); j < len; j++) {
                        if (j == i) {
                            selectedTag = interestTags.get(j);
                            interestTags.get(j).setChecked(true);
                        } else {
                            interestTags.get(j).setChecked(false);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SelectTagDialog.this.dismiss();
                }
            });

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedTag == null) {
                        UIHelper.ToastMessage(SelectTagDialog.this.getContext(), "请选择一个栏目");
                        return;
                    }
                    SelectTagDialog.this.dismiss();
                    save(posting_state);
                }
            });
        }

        @Override
        public void show() {
            super.show();
        }
    }

    class SelectTagAdapter extends BaseAdapter {
        private Context context;

        SelectTagAdapter(Context context) {
            this.context = context;
        }

        public int getCount() {
            return interestTags.size();
        }

        public Object getItem(int item) {
            return item;
        }

        public long getItemId(int id) {
            return id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.circle_select_tag_item, null);
                holder.button = (RadioButton) convertView.findViewById(R.id.radioButton);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final InterestTag tag = interestTags.get(position);
            holder.button.setText(tag.getTag_name());
            if (tag.isChecked()) {
                holder.button.setChecked(true);
            } else {
                holder.button.setChecked(false);
            }

            return convertView;
        }
    }

    public static class ViewHolder {
        public RadioButton button;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }


    private File persistImage(Bitmap bitmap, String name) {
        try {
            File f = new File(getExternalCacheDir().getPath(), name);
            f.createNewFile();
            OutputStream os;
            os = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);
            os.flush();
            os.close();
            return f;
        } catch (Exception e) {
            Log.e(TAG, "Error writing bitmap", e);
        }
        return null;
    }

    /**
     * 压缩图片..
     */
    class MyCompressWorker {
        private Handler myHander = new Handler();

        public void excute(final List<String> path) {
            new Thread() {
                @Override
                public void run() {
                    final List<File> fileList = new ArrayList<File>();
                    for (String str : path) {
//                Bitmap bitmap = getSmallBitmap(str);
                        Bitmap bitmap = ImageUtil.getSmallBitmap(str);
                        File file = persistImage(bitmap, "bimg" + System.currentTimeMillis() + (int) (Math.random() * 100));
                        fileList.add(file);
                    }
                    myHander.post(new Runnable() {
                        @Override
                        public void run() {
                            imgeList.clear();
                            imgeList.addAll(fileList);
                        }
                    });
                }
            }.start();
        }
    }
}

