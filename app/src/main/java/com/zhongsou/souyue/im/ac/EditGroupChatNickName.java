package com.zhongsou.souyue.im.ac;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.GroupMembers;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.ImUtils;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by zoulu
 * on 14-8-27
 * Description:修改群昵称
 */
public class EditGroupChatNickName extends IMBaseActivity implements View.OnClickListener {
    public static final String TAG = "EditGroupChatNickName";
    public static final String UID = "user_id";
    private ImageButton cancel;
    private EditText edNoteName;
    private TextView title_name,save_tv;
    private String snoteName,groupname;
    private long uid = 0,myGroupName = 1 ;
    private ArrayList<GroupMembers> selMembers;
    private Group mGroup;
    private boolean isKeyBoardShowing;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.editgroupnickname);
        initView();
        initFromIntent();
        setLayouListener();
    }

    private void initView() {
        cancel = (ImageButton) findViewById(R.id.im_edit_cancel);
        edNoteName = (EditText) findViewById(R.id.im_edit_notename);
        title_name = (TextView) findViewById(R.id.title_name);
        save_tv = (TextView) findViewById(R.id.save);
        save_tv.setText(R.string.ent_save);
        setOnclickListener();
    }

    private void setOnclickListener() {
        cancel.setOnClickListener(this);
        save_tv.setOnClickListener(this);
    }

    private void initFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mGroup = (Group) getIntent().getSerializableExtra("group");
            myGroupName = getIntent().getIntExtra("myGroupName", 0);
            groupname =  getIntent().getStringExtra("groupname");
            if (mGroup != null) {
                if(myGroupName == 1) {
                      String mynickname = getIntent().getStringExtra("mynickname");
                	  edNoteName.setText(mynickname);
                } else {
                	 edNoteName.setText(groupname);
                }
            }
        }
        if(myGroupName==1) {
        	 title_name.setText("我的群昵称");
        } else {
        	 title_name.setText("群名称");
        }


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.im_edit_cancel:
                if (edNoteName != null)
                    edNoteName.setText("");
                break;
            case R.id.save:
                if(!CMainHttp.getInstance().isNetworkAvailable(this)) {
                    SouYueToast.makeText(this, getString(R.string.user_login_networkerror), 0).show();
                    return;
                }
                String name = edNoteName.getText().toString().trim().replace(" ","");

                if(StringUtils.isEmpty(name)){
                    if(myGroupName != 1) {
                        SouYueToast.makeText(this, "请填写群名称", 0).show();
                    } else {
                        SouYueToast.makeText(this, "请填写我的群名称", 0).show();
                    }
                    return;
                }
                if(myGroupName != 1) {
                    if(!ImUtils.validateGroupName(edNoteName,EditGroupChatNickName.this)) {
                        return;
                    }
                } else {
                    if(!ImUtils.validateMyGroupName(edNoteName,EditGroupChatNickName.this)) {
                        return;
                    }
                }
                showProgress();
                if(myGroupName==1) {
                    if(ImserviceHelp.getInstance().updateGroupNickNameOp(7, Long.toString(mGroup.getGroup_id()), name)){
                        SouYueToast.makeText(this,R.string.save_success,0).show();
                        Intent intent = new Intent();
                        intent.putExtra(TAG, name);
                        this.setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        SouYueToast.makeText(this,R.string.save_failed, 0).show();
                    }
                } else {
                    if(ImserviceHelp.getInstance().updateGroupNickNameOp(6, Long.toString(mGroup.getGroup_id()), name)){
                        SouYueToast.makeText(this,R.string.save_success,0).show();
                        Intent intent = new Intent();
                        intent.putExtra(TAG, name);
                        this.setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        SouYueToast.makeText(this, R.string.save_failed, 0).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (isKeyBoardShowing) {
            isKeyBoardShowing = false;
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(edNoteName.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }else {
            finish();
        }

    }

    private void setLayouListener() {
        final View activityRootView = findViewById(R.id.ll_edit_root);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        activityRootView.getWindowVisibleDisplayFrame(r);
                        int heightDiff = activityRootView.getRootView()
                                .getHeight() - (r.bottom - r.top);

                        if (heightDiff > 100) {
                            isKeyBoardShowing = true;
                        } else {
                            isKeyBoardShowing = false;
                        }
                    }
                });
    }

}
