package com.zhongsou.souyue.activity;
/**
 * 点击查看大图
 */
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.module.TouchGallerySerializable;
import com.zhongsou.souyue.service.DownloadImageTask;
import com.zhongsou.souyue.ui.gallery.GalleryViewPager;
import com.zhongsou.souyue.ui.gallery.UrlPagerAdapter;
import com.zhongsou.souyue.ui.gallery.touchview.PhotoViewAttacher.OnViewTapListener;
import com.zhongsou.souyue.ui.gallery.touchview.UrlTouchImageView.DownLoadLinstener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TouchGalleryActivity extends BaseActivity implements
        OnViewTapListener, DownLoadLinstener {
    private List<String> items = new ArrayList<String>();
    private int pos;
    private GalleryViewPager mViewPager;
    private TextView num;
    private String currentImageUrl;
    private UrlPagerAdapter pagerAdapter;
    private Map<String, Boolean> url2pic = new HashMap<String, Boolean>();
    private ImageButton save;
    private boolean isFromIM;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        setContentView(R.layout.touchgallery);
        initView();
        initFromIntent();
        pagerAdapter = new UrlPagerAdapter(this, items);
        mViewPager = (GalleryViewPager) findViewById(R.id.gallery_viewer);
//		mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(pos);
        setcurrentPage(pos);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int pos) {
                setcurrentPage(pos);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        isFromIM = getIntent().getBooleanExtra("isIM", false);
    }

    private void initView() {
        num = findView(R.id.images_num);
        save = findView(R.id.images_save);
    }

    private void setcurrentPage(int pos) {
        if(pos>=items.size()){
            return;
        }
        currentImageUrl = pagerAdapter.getItem(pos);
        if (num != null)
            num.setText(++pos + "/" + items.size());
        if (url2pic != null && url2pic.get(currentImageUrl)) {
            save.setVisibility(View.VISIBLE);
        } else {
            save.setVisibility(View.INVISIBLE);
        }
    }

    private void initFromIntent() {
        Intent intent = getIntent();
        TouchGallerySerializable tgs = (TouchGallerySerializable) intent
                .getSerializableExtra("touchGalleryItems");
        if(tgs!=null) {
            items.addAll(replaceUrl(tgs.getItems()));
            items.remove("add_pic");
            for (String s : tgs.getItems()) {
                url2pic.put(s, false);
            }
            this.pos = tgs.getClickIndex();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onBackClick(View v) {
        this.finish();
    }

    public void onSaveToSdClick(View v) {
        if (currentImageUrl != null) {
            Toast.makeText(this, R.string.down_image_ing, Toast.LENGTH_SHORT)
                    .show();
           // SaveImageTask sit = new SaveImageTask(this);
            DownloadImageTask task = new DownloadImageTask(this);
            task.execute(currentImageUrl);
        } else {
            Toast.makeText(this, R.string.down_image_fail, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        finish();
    }

    private List<String> replaceUrl(List<String> lists) {

        if (lists == null)
            return null;
        for (int i = 0; i < lists.size(); i++) {
            String url = lists.get(i).trim();
            if (!url.startsWith("http")) {
                url = "file:/" + url;
            }
            lists.set(i,
                    url.replace("!ios", "").replace("!android", ""));
        }
        return lists;
    }

    @Override
    public void downLoadSuccess(String url) {
        url2pic.put(url, true);
        save.setVisibility(View.VISIBLE);
    }



}
