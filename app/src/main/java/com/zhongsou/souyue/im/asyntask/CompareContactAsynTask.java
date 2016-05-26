package com.zhongsou.souyue.im.asyntask;

import android.os.Handler;
import android.os.Message;
import com.tuita.sdk.im.db.helper.PingYinUtil;
import com.tuita.sdk.im.db.module.Contact;
import com.zhongsou.souyue.im.util.PinyinComparator;
import com.zhongsou.souyue.module.MobiContactEntity;
import com.zhongsou.souyue.service.ZSAsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompareContactAsynTask extends ZSAsyncTask<List<Contact>, Void, MobiContactEntity> {

    private List<Contact> old;
    private Handler handler;
    private Map<String, Integer> alphaIndex;
    private MobiContactEntity result;
    private List<String> firstSpell;

    public CompareContactAsynTask(List<Contact> old, Handler handler) {
        this.old = old;
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        alphaIndex = new HashMap<String, Integer>();
        result = new MobiContactEntity();
        firstSpell = new ArrayList<String>();
        super.onPreExecute();
    }

    protected MobiContactEntity doInBackground(List<Contact>... params) {
        return compareData(params[0]);
    };

    private MobiContactEntity compareData(List<Contact> data) {
    	try {
        if (old != null && old.size() > 0) {
            Collections.sort(old, new PinyinComparator());
            for (int i = 0; i < old.size(); i ++){
                firstSpell.add(PingYinUtil.converterToFirstSpell(old.get(i).getNick_name()).substring(0, 1));
                if (data != null && data.size() > 0) {
                    for (Contact c : data) {
                        if (c.getPhone().equals(old.get(i).getPhone())) {
                        		c.setNick_name(old.get(i).getNick_name());
                            old.set(i, c);
                        }
                    }
                }
                old.get(i).setCatalog(firstSpell.get(i)); 
            }
        }
    	} catch (Exception ex){
    		ex.printStackTrace();
    	}
        getFirstC();
        result.setAlphaIndex(alphaIndex);
        result.setContasts(old);
        return result;
    }

    private void getFirstC() {
        Integer index = 0;
        int initindex = 0;
        for (int i = 0; i < firstSpell.size(); i++) {
            String key1 = firstSpell.get(i).toUpperCase();
            String key2 = null;
            if (i + 1 < firstSpell.size()) {
                key2 = firstSpell.get(i + 1).toUpperCase();
                if (key1.equals(key2)) {
                    ++initindex;
                    if (initindex == 1) {
                        index = i;
                    }
                }
                if (!key1.equals(key2)) {
                    initindex = 0;
                }
            } else {
                index = firstSpell.size() - 1;
            }
            alphaIndex.put(key1, index);
        }
    }

    @Override
    protected void onPostExecute(MobiContactEntity result) {
        if (handler != null) {
            Message msg = new Message();
            msg.obj = result;
            handler.sendMessage(msg);
        }
        super.onPostExecute(result);
    }

}
