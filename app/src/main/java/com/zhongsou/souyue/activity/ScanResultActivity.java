package com.zhongsou.souyue.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;

public class ScanResultActivity extends BaseActivity{

    private String text;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.scan_result);
        getIntentData();
        setData();
    }

    private void setData() {
        TextView textView = (TextView) findViewById(R.id.scan_result_text);
        textView.setText(text);
        findViewById(R.id.scan_result_copy).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                int sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(text);
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE); 
                    android.content.ClipData clip = android.content.ClipData.newPlainText("搜悦",text);
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(ScanResultActivity.this, "已复制到剪切板", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getIntentData() {
        text = getIntent().getStringExtra("content");
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("content", text);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            text = savedInstanceState.getString("content");
        }
    }
    public void onGoBackClick(View view) {
        this.finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
}
