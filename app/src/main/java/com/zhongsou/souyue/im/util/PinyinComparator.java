package com.zhongsou.souyue.im.util;


import com.tuita.sdk.im.db.helper.PingYinUtil;
import com.tuita.sdk.im.db.module.Contact;

import java.util.Comparator;







public class PinyinComparator implements Comparator<Contact> {

    @Override
    public int compare(Contact o1, Contact o2) {
        String str1 = PingYinUtil.converterToFirstSpell(o1.getNick_name()).substring(0, 1).toUpperCase();
        String str2 = PingYinUtil.converterToFirstSpell(o2.getNick_name()).substring(0, 1).toUpperCase();
        
        if (str1.equals(str2)) {
            return 0;
        } else if ("#".equals(str1) && !"#".equals(str2)) {
            return 1;
        } else if (!"#".equals(str1) && "#".equals(str2)) {
            return -1;
        }

        return str1.compareTo(str2);
    }


}
