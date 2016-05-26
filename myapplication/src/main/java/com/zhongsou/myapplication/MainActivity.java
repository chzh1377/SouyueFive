package com.zhongsou.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
    private ListView listview;
    private String[] contents = new String[]{"1","2","3","4","5","6","7","8","9"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }


    private void initView() {
        listview = (ListView)findViewById(R.id.listview);
        listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,contents));
        listview.addFooterView(getFootView());
    }

    private View getFootView() {

        return View.inflate(this,R.layout.foot_view,null);
    }

}
