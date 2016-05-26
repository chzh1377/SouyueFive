package com.zhongsou.souyue.im.ac;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.tuita.sdk.im.db.module.MessageRecent;
import com.tuita.sdk.im.db.module.ServiceMessageRecent;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.adapter.ShowSerMessageAdapter;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.IMIntentUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwb
 * on 14-10-23
 * Description:服务号列表页
 */
public class ShowServiceMessageActivity extends IMBaseActivity{
    private ShowSerMessageAdapter showSerMessageAdapter;
    private TextView title_name;
    private ListView listView;
    private ImserviceHelp service = ImserviceHelp.getInstance();
    private List<ServiceMessageRecent> serviceMessageRecentList = new ArrayList<ServiceMessageRecent>();
    private  String draftContent;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.grouplist);
        init();
    }

    private void init(){
        listView = (ListView) findViewById(R.id.listView);
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("服务号");
        draftContent = getIntent().getStringExtra("draftContent");
        showSerMessageAdapter = new ShowSerMessageAdapter(this);
        serviceMessageRecentList = service.db_getServiceMessageByMyid();

        if(serviceMessageRecentList != null){
            showSerMessageAdapter.setData(serviceMessageRecentList);
            listView.setAdapter(showSerMessageAdapter);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                MessageRecent bean =  new MessageRecent();
                bean.setDrafttext(draftContent);

                IMIntentUtil.gotoServiceMessageCateActivity(ShowServiceMessageActivity.this,showSerMessageAdapter.getItem(i),bean);
            }
        });
    }
}
