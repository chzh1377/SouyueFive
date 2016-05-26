package com.zhongsou.souyue.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.zhongsou.souyue.common.R;

import java.lang.reflect.Type;

/**
 * Created by lvqiang on 15/8/15.
 */
public class CommSharePreference {
    public static final int DEFAULT_USER = 0;

    private static CommSharePreference mInstance;
    private        SharedPreferences   mPreferencess;
    private        Context             mContext;
    private Gson mGson = new Gson();

    public static CommSharePreference getInstance() {
        if (mInstance == null) {
            mInstance = new CommSharePreference();
        }
        return mInstance;
    }

    public void initContext(Context context) {
        mContext = context;
    }

    private SharedPreferences instance(long id) {
        Resources resources = mContext.getResources();
        String    key       = resources.getString(R.string.app_name);
        if (mContext == null) {
            throw new IllegalStateException("Must call " + getClass().getName() + ".initContext befor this!!!");
        }
        mPreferencess = mContext.getSharedPreferences(key + id, Context.MODE_PRIVATE);
        return mPreferencess;
    }

    public void putValue(long id, String key, String value) {
        SharedPreferences        prefer = instance(id);
        SharedPreferences.Editor edit   = prefer.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public void putValue(long id, String key, int value) {
        SharedPreferences        prefer = instance(id);
        SharedPreferences.Editor edit   = prefer.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public void putValue(long id, String key, long value) {
        SharedPreferences        prefer = instance(id);
        SharedPreferences.Editor edit   = prefer.edit();
        edit.putLong(key, value);
        edit.commit();
    }

    public void putValue(long id, String key, boolean value) {
        SharedPreferences        prefer = instance(id);
        SharedPreferences.Editor edit   = prefer.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public void putValue(long id, String key, float value) {
        SharedPreferences        prefer = instance(id);
        SharedPreferences.Editor edit   = prefer.edit();
        edit.putFloat(key, value);
        edit.commit();
    }

    public boolean getValue(long id, String key, boolean def) {
        SharedPreferences prefer = instance(id);
        return prefer.getBoolean(key, def);
    }

    public String getValue(long id, String key, String def) {
        SharedPreferences prefer = instance(id);
        return prefer.getString(key, def);
    }

    public int getValue(long id, String key, int def) {
        SharedPreferences prefer = instance(id);
        return prefer.getInt(key, def);
    }

    public long getValue(long id, String key, long def) {
        SharedPreferences prefer = instance(id);
        return prefer.getLong(key, def);
    }

    public float getValue(long id, String key, float def) {
        SharedPreferences prefer = instance(id);
        return prefer.getFloat(key, def);
    }

    public void putBeanToSP(String key, Object value) {
        SharedPreferences prefer = instance(0);
        prefer.edit().putString(key, mGson.toJson(value)).commit();
    }

    public Object getBeanFromSP(String key, Type type) {
        SharedPreferences prefer = instance(0);
        String            json   = prefer.getString(key, "");
        return mGson.fromJson(json, type);
    }

}
