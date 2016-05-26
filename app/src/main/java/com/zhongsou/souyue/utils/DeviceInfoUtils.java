package com.zhongsou.souyue.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Browser;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.zhongsou.souyue.module.AppData;
import com.zhongsou.souyue.module.ResponseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyw on 2016/5/7.
 */
public class DeviceInfoUtils {

    //用户账户
    public static class AccountModel extends ResponseObject {
        private String name;
        private String type;

        public AccountModel(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    private static List<AccountModel> getAccounts(Context context) {
        List<AccountModel> models         = new ArrayList<>();
        AccountManager     accountManager = AccountManager.get(context);
        Account[]          accounts       = accountManager.getAccounts();
        for (Account account : accounts) {
            AccountModel model = new AccountModel(account.name, account.type);
            models.add(model);
        }
        return models;
    }

    //获取浏览器书签
    public static class BookMarkModel extends ResponseObject {
        private String title;
        private String url;
        private String type;


        public BookMarkModel(String title, String url, String type) {
            this.title = title;
            this.url = url;
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }


    private static List<BookMarkModel> getBookMarks(Context context) {
        List<BookMarkModel> models          = new ArrayList<>();
        String              string          = null;
        String              title           = null;
        ContentResolver     contentResolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query((Browser.BOOKMARKS_URI), new String[]{Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL}, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                string = cursor.getString(cursor.getColumnIndex("url"));
                title = cursor.getString(cursor.getColumnIndex("title"));
                Log.d("debug", string == null ? "null" : string);
                Log.d("debug", title == null ? "null" : title);
                models.add(new BookMarkModel(title, string, "bookmarks"));
            }
        } catch (Exception e) {

        }
        try {
            cursor = contentResolver.query(Uri.parse("content://browser/history"), new String[]{"url", "title"}, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                string = cursor.getString(cursor.getColumnIndex("url"));
                title = cursor.getString(cursor.getColumnIndex("title"));
                Log.d("debug", string == null ? "null" : string);
                models.add(new BookMarkModel(title, string, "history"));
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {

        }
        return models;
    }

    /**
     * 获取用户安装的App列表，返回data格式
     *
     * @param context
     * @return
     */
    public static List<AppData> getAppData(Context context) {
        List<PackageInfo>  packages = context.getPackageManager().getInstalledPackages(0);
        ArrayList<AppData> appList  = new ArrayList<AppData>();
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                CharSequence appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager());
                String packageName = packageInfo.applicationInfo.packageName;
                if (appName != null && !"".equals(appName) && packageName != null && !TextUtils.isEmpty(packageName)) {
                    AppData d = new AppData();
                    d.setName(appName.toString());
                    d.setPkg(packageName);
                    appList.add(d);
                }
            }
        }
        return appList;
    }

    static class InfoModels extends ResponseObject {
        private List<AppData>       apps;
        private List<BookMarkModel> bookMarks;
        private List<AccountModel>  accounts;

        public List<AppData> getApps() {
            return apps;
        }

        public void setApps(List<AppData> apps) {
            this.apps = apps;
        }

        public List<BookMarkModel> getBookMarks() {
            return bookMarks;
        }

        public void setBookMarks(List<BookMarkModel> bookMarks) {
            this.bookMarks = bookMarks;
        }

        public List<AccountModel> getAccounts() {
            return accounts;
        }

        public void setAccounts(List<AccountModel> accounts) {
            this.accounts = accounts;
        }
    }

    public static String getJsonStr(Context context) {
        InfoModels infoModels = new InfoModels();
        infoModels.setAccounts(getAccounts(context));
        infoModels.setApps(getAppData(context));
        infoModels.setBookMarks(getBookMarks(context));
        return new Gson().toJson(infoModels);
    }

    public static String getBookMarkJson(Context context) {
        return new Gson().toJson(getBookMarks(context));
    }

    public static String getAccountDataJson(Context context) {
        return new Gson().toJson(getAccounts(context));
    }
}
