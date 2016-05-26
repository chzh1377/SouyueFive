/*
 * Copyright (C) 2013 47 Degrees, LLC
 *  http://47deg.com
 *  hello@47deg.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.zhongsou.souyue.im.ac;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import com.tuita.sdk.im.db.module.Group;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.module.ImShareNews;
import com.zhongsou.souyue.im.util.IMIntentUtil;
import com.zhongsou.souyue.module.ChatMsgEntity;

import java.io.Serializable;

/**
 * 通讯录或者好友查找<br>
 * 依据Intent传递的key=ContactsListActivity.START_TYPE的值判断<br>true表示显示为通讯录<br>false表示为好友查找
 * @author "jianxing.fan@iftek.cn"
 *
 */
public class ContactsListActivity extends IMBaseActivity{
	public static final String START_TYPE = "start_type";
	public static final String START_FROM="TigerGame";
	public static final String SHOWGROUPCHAT = "showgroup";//是否显示群聊 ，true不显示  ，false显示
    public static final String SHOWSERVICEMESSAGE = "showservicemessage";//是否显示服务号 ，true不显示  ，false显示
    public static final String FROMFRIENDINFOACTIVITY = "fromfriendactivity";//是否来自好友名片。
    public static final String SHOWCARD = "showcard";//分享个人、群名片
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_swipe_contacts_list_view_activity);
    }

    //索要
    public static void startSuoyaoAct(Activity act) {
        Intent intent = new Intent(act, ContactsListActivity.class);
        Bundle bundle=new Bundle();
        bundle.putBoolean(ContactsListActivity.START_TYPE, false);
        bundle.putString(IMChatActivity.KEY_ACTION, IMChatActivity.ACTION_ASK_COIN);
        bundle.putBoolean(ContactsListActivity.START_FROM, true);
        intent.putExtras(bundle);
        act.startActivity(intent);
    }
    //分享搜悦新闻，原创到Im好友
    public static void startSYIMFriendAct(Activity act,ImShareNews newscontent) {
        Intent intent = new Intent(act, ContactsListActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable(ImShareNews.NEWSCONTENT, newscontent);
        bundle.putBoolean(ContactsListActivity.START_TYPE, false);
        bundle.putString(IMChatActivity.KEY_ACTION, IMChatActivity.ACTION_SHARE_IMFRIEND);
        bundle.putBoolean(ContactsListActivity.START_FROM, false);
        intent.putExtras(bundle);
        act.startActivity(intent);
    }
    
    //转发消息多条
    public static void startForwardAct(Activity act,Object editList) {
        Intent intent = new Intent(act, ContactsListActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable(ChatMsgEntity.FORWARD, (Serializable) editList);
        bundle.putBoolean(ContactsListActivity.START_TYPE, false);
        bundle.putString(IMChatActivity.KEY_ACTION, IMChatActivity.ACTION_FORWARD);
        bundle.putBoolean(ContactsListActivity.START_FROM, false);
        intent.putExtras(bundle);
        if(editList instanceof ChatMsgEntity){
            act.startActivity(intent);
        }else{
            act.startActivityForResult(intent, IMChatActivity.CODE_FORWARD);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == IMIntentUtil.SHAREGROUP){
            setResult(IMIntentUtil.SHAREGROUP);
            finish();
        }
        if(resultCode == IMIntentUtil.SCARD){
            Group group = (Group) data.getSerializableExtra(IMChatActivity.KEY_GET_CARD_ID);
            Intent backIntent = new Intent();
            backIntent.putExtra(IMChatActivity.KEY_GET_CARD_ID, group);
            setResult(IMIntentUtil.SCARD, backIntent);
//            overridePendingTransition(R.anim.left_in, R.anim.left_out);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        finish();
    }

}
