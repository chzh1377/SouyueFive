package com.zhongsou.souyue.module.firstleader;

import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.common.utils.CommSharePreference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyw on 2016/3/28.
 */
public class UserGuideInfo implements DontObfuscateInterface {
    public static final String TAG        = UserGuideInfo.class.getSimpleName();
    public static final String SAVED_KEY  = "USER_GUIDE_INFO";
    public static final String GENDER_KEY = "gender_key";
    public static final String AGE_KEY = "age_key";
    private String sex;
    private String character;
    private List<ChildGroupItem> subItems = new ArrayList<ChildGroupItem>();

    public UserGuideInfo(String sex, String character, List<ChildGroupItem> subItems) {
        this.sex = sex;
        this.character = character;
        if (subItems != null) {
            for (ChildGroupItem item : subItems) {
                if (item.getIsSelected() == 1) {
                    this.subItems.add(item);
                }
            }
        }
    }

    public String getSex() {
        return sex;
    }

    public String getCharacter() {
        return character;
    }

    public static UserGuideInfo getSavedUser() {
        Object beanFromSP = null;
        try {
            beanFromSP = CommSharePreference.getInstance().getBeanFromSP(SAVED_KEY, new TypeToken<UserGuideInfo>() {
            }.getType());
        } catch (Exception e) {
        }
        return (UserGuideInfo) beanFromSP;
    }

    public List<ChildGroupItem> getSubItems() {
        return subItems;
    }

    public static void clearSavedInfo() {
        CommSharePreference.getInstance().putValue(0, SAVED_KEY, "");
    }

    public void save() {
        CommSharePreference.getInstance().putBeanToSP(SAVED_KEY, this);
    }
}
