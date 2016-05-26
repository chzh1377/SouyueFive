package com.zhongsou.souyue.service;

import android.content.Context;
import android.widget.Toast;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.PhotoUtils;

import java.io.File;

/**
 * 保存图片到本地
 *
 * @author zhangliang01@zhongsou.com
 */
public class DownloadImageTask extends ZSAsyncTask<String, Void, Integer> {
    private static final int SUCCESS = 0;
    private static final int FAIL = 1;
    private static final int EXIST = 2;

    private Context mContext;

    public DownloadImageTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... params) {
        return storeImage(params[0]);
    }

    @Override
    protected void onPostExecute(Integer what) {
        switch (what) {
            case FAIL:
                Toast.makeText(MainApplication.getInstance(), R.string.down_image_fail, Toast.LENGTH_SHORT).show();
                break;
            case EXIST:
            case SUCCESS:
            default:
                Toast.makeText(MainApplication.getInstance(), R.string.down_image_success, Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private boolean isEmpty(String param) {
        return param == null || "".equals(param.trim());
    }

    /**
     * 保存图片
     *
     * @param url
     * @return
     */
    private int storeImage(String url) {
        System.out.println("url=" + url);
        // Uri uri = null;
        try {
            File f = null;
            if (url.startsWith("http")) {
                f = PhotoUtils.getImageLoader().getDiskCache().get(url);
            } else if (url.startsWith("file://")) {
                String[] itemUrl = url.split("file:/");
                if (itemUrl != null && itemUrl.length == 2) {
                    f = new File(itemUrl[1]);
                }
            } else {
                f = new File(url);
            }
            if (f != null && f.canRead()) {
                int result = ImageManager.saveImageToGallery(mContext, "souyue", System.currentTimeMillis(), "souyue", getFileName(url), null, f);
                return result;
            } else
                return FAIL;
        } catch (Exception e) {
            e.printStackTrace();
            return FAIL;
        }
        // return SUCCESS;
    }

    /**
     * 根据url 获得文件类型
     *
     * @param url
     * @return
     */
    private String getFileName(String url) {
        return System.currentTimeMillis() + "";
//        String name = "";
//        if (!isEmpty(url)) 
//            name = System.currentTimeMillis() + "";
//        if (!name.equals(""))
//            return name;
//        return System.currentTimeMillis() + ".jpg";
    }
}
