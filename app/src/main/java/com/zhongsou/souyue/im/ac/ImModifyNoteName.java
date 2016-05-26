package com.zhongsou.souyue.im.ac;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.ImUtils;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.StringUtils;

public class ImModifyNoteName extends IMBaseActivity implements OnClickListener{
    public static final String TAG = "ImModifyNoteName";
    public static final String UID = "user_id";
    private TextView title, save;
    private ImageButton cancel;
    private EditText edNoteName;
    private String snoteName;
    private long uid = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_modify_note_name);
        title = (TextView) findViewById(R.id.activity_bar_title);
        edNoteName = (EditText) findViewById(R.id.im_edit_notename);
        cancel = (ImageButton) findViewById(R.id.im_edit_cancel);
        save = (TextView) findViewById(R.id.text_btn);
        save.setText(R.string.im_modify_save);
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
        initFromIntent();

    }

    private void initFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            snoteName = intent.getStringExtra(TAG);
            if (snoteName != null) {
            	edNoteName.setText(snoteName);
            }
            Editable eatable = edNoteName.getText();
            if(eatable != null && eatable.length() > 1) {
                Selection.setSelection(eatable, eatable.length());
            }
            uid = getIntent().getLongExtra(UID,0);
        }
        title.setText(R.string.im_modify_notename);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_btn:
                if(!CMainHttp.getInstance().isNetworkAvailable(this)) {
                    SouYueToast.makeText(this, getResources().getString(R.string.networkerror),0).show();
                    return;
                }
                String name = edNoteName.getText().toString().trim().replace(" ","");
                if(StringUtils.isEmpty(name)){
                    SouYueToast.makeText(this, "请填写该用户的备注名", 0).show();
                    return;
                }
                if(ImUtils.validateNoteNameforIm(edNoteName,ImModifyNoteName.this)){
                    if(ImserviceHelp.getInstance().im_update(3, uid, name)){
                        hideKeyboard();
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
            case R.id.im_edit_cancel:
                if (edNoteName != null)
                    edNoteName.setText("");
                break;
        }
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(edNoteName.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
