package com.zhongsou.souyue.im.ac;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuita.sdk.im.db.module.Group;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.adapter.GroupListAdapter;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.Collections;
import java.util.List;

/**
 * Created by zoulu
 * on 14-8-28
 * Description:群聊列表页
 */
public class GroupListActivity extends IMBaseActivity{
    private GroupListAdapter groupListAdapter;
    private TextView title_name;
    private ListView listView;
    private ImserviceHelp service = ImserviceHelp.getInstance();

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.grouplist);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        listView = (ListView) findViewById(R.id.listView);
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("群聊");
        groupListAdapter = new GroupListAdapter(this);
        final List<Group> groupList = new Gson().fromJson(service.db_findGroupListByGroupidAndIsSaved(Long.valueOf(SYUserManager.getInstance().getUserId()), IMessageConst.STATE_IS_SAVED), new TypeToken<List<Group>>() {
        }.getType());
        if(groupList != null){
            Collections.reverse(groupList);//list倒序
            groupListAdapter.setData(groupList);
            listView.setAdapter(groupListAdapter);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                IMIntentUtil.gotoGroupChatActivity(GroupListActivity.this, (Serializable) groupListAdapter.getItem(i),0);

//                Intent finfo = new Intent(GroupListActivity.this, GroupChatActivity.class);
//                finfo.putExtra("group", (Serializable) groupListAdapter.getItem(i));
//                finfo.putExtra("BUBBLENUM", 0);
//                //获取最近聊天记录
//                MessageRecent messageRecent = ImserviceHelp.getInstance().db_findMessageRecent(groupListAdapter.getItem(i).getGroup_id());
//                if (null != messageRecent) {
//                    finfo.putExtra("draftContent", messageRecent.getDrafttext());
//                    finfo.putExtra("draftForAtContent", messageRecent.getDraftforat());
//                }
//                startActivity(finfo);
//                GroupListActivity.this.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                IMChatActivity.invoke(GroupListActivity.this, IConst.CHAT_TYPE_GROUP, groupListAdapter.getItem(i).getGroup_id());
            }
        });
    }
}
