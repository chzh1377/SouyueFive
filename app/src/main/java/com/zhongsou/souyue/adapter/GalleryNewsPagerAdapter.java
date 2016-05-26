/*
 Copyright (c) 2013 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.zhongsou.souyue.adapter;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.GalleryNewsActivity;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.GalleryNewsItem;
import com.zhongsou.souyue.module.GalleryNewsList;
import com.zhongsou.souyue.module.GalleryRecommendItem;
import com.zhongsou.souyue.ui.gallery.touchview.UrlTouchImageView;
import com.zhongsou.souyue.ui.gallery.touchview.UrlTouchImageView.DownLoadLinstener;

import java.util.ArrayList;
import java.util.List;


/**
 * 需要改动的adapter,坑。
 */
public class GalleryNewsPagerAdapter extends PagerAdapter implements ImageLoadingListener{

    private GalleryNewsList mNewsList;
    private Context mContext;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;

    public static final String RELATE_TAG = "relate";


    //adapter
    private final GalleryRecommendAdapter mAdapter;

    /**
     * 参数：上下文，前面的大图对象，后面的推荐
     *
     * @param context
     * @param mNewsList
     */
    public GalleryNewsPagerAdapter(Context context, GalleryNewsList mNewsList) {
        this.mContext = context;
        mShortAnimationDuration = mContext.getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        this.mNewsList = mNewsList;
        mAdapter = new GalleryRecommendAdapter(mContext, mNewsList.getRelate(), (GalleryNewsActivity) mContext);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        if (RELATE_TAG.equals(getItem(position))) {
            //如果是推荐页
            View lastPage = View.inflate(mContext, R.layout.gallerynews_activity_recommend, null);
            initRecommendView(lastPage);
            collection.addView(lastPage);
            return lastPage;
        } else {
            //如果不是推荐页
            UrlTouchImageView iv = new UrlTouchImageView(mContext);
            iv.setDownLoadLinstener((DownLoadLinstener) mContext);
            iv.setBgUrl(mNewsList.getContent().get(position).getUrl());
            collection.addView(iv, 0);
            return iv;
        }
    }

    private void initRecommendView(View lastPage) {
        ListView listView = (ListView) lastPage.findViewById(R.id.gallery_recommend_list);
        listView.setAdapter(mAdapter);
    }

    @Override
    public int getCount() {
        //如果recommendlist 不是空的话就+1;
        return mNewsList.getContent().size() + (mNewsList.getRelate() == null ? 0 : 1);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0.equals(arg1);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public Object getItem(int pos) {
        if (pos < mNewsList.getContent().size()) {
            return mNewsList.getContent().get(pos).getUrl();
        }
        return RELATE_TAG;
    }

    /**
     * 推荐界面的apapter
     */

    private class GalleryRecommendAdapter extends BaseAdapter {

        private final List<GalleryRecommendItem> mList;
        private final Context mContext;
        private final OnItemClickListener mListener;

        public GalleryRecommendAdapter(Context mContext, List<GalleryNewsItem> relate, OnItemClickListener listener) {
            this.mList = new ArrayList<GalleryRecommendItem>();
            this.mContext = mContext;
            mListener = listener;
            initmList(relate);
        }

        private void initmList(List<GalleryNewsItem> relate) {
            //传入的relate的大小一定大于5，原因请看com.zhongsou.souyue.module.GalleryNewsList.getRelate(),
            //或者为空
            if (relate == null)
                return;
            //根据需求，只取前五个
            //line 1
            mList.add(new GalleryRecommendItem(GalleryRecommendItem.TYPE_RECOMMENDLIST_DOUBLE, relate.get(0), relate.get(1)));
            //line 2
            mList.add(new GalleryRecommendItem(GalleryRecommendItem.TYPE_RECOMMEND_LIST_SINGLE, relate.get(2)));
            //line 3
            mList.add(new GalleryRecommendItem(GalleryRecommendItem.TYPE_RECOMMENDLIST_DOUBLE, relate.get(3), relate.get(4)));
        }


        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * getview 只显示了一页的内容，所以无需viewholder.不过为了以后的扩展方便，还是写成listview
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (mList.get(position).type == GalleryNewsItem.TYPE_RECOMMEND_LIST_SINGLE) {
                view = View.inflate(mContext, R.layout.gallerynews_recommend_item_singleline, null);
            }else{
                view = View.inflate(mContext, R.layout.gallerynews_recommend_item, null);
            }
            setViewData(view, mList.get(position));
            return view;
        }

        /**
         * 设置view的显示数据
         *
         * @param convertView
         * @param galleryRecommendInfo
         */
        private void setViewData(View convertView, final GalleryRecommendItem galleryRecommendInfo) {
            //findView
            ImageView iv_recommend_1 = (ImageView) convertView.findViewById(R.id.gallery_recommend_image1);
            TextView tv_recommend_1 = (TextView) convertView.findViewById(R.id.gallery_recommend_text1);
            ImageView iv_recommend_2 = (ImageView) convertView.findViewById(R.id.gallery_recommend_image2);
            TextView tv_recommend_2 = (TextView) convertView.findViewById(R.id.gallery_recommend_text2);
            //set listener
            convertView.findViewById(R.id.gallery_recommend_item_1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(galleryRecommendInfo.items.get(0));
                }
            });
            View rightItem = convertView.findViewById(R.id.gallery_recommend_item_2);
            if (rightItem != null) {
                rightItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onItemClick(galleryRecommendInfo.items.get(1));
                    }
                });
            }
            //set data
            if (galleryRecommendInfo.type == GalleryNewsItem.TYPE_RECOMMEND_LIST_SINGLE) {
                //单行显示的数据
                PhotoUtils.showCard(PhotoUtils.UriType.HTTP, galleryRecommendInfo.items.get(0).getImg(), iv_recommend_1, MyDisplayImageOption.getOptions(R.drawable.pic_default),GalleryNewsPagerAdapter.this);
                tv_recommend_1.setText(galleryRecommendInfo.items.get(0).getTitle());
            } else if (galleryRecommendInfo.type == GalleryNewsItem.TYPE_RECOMMENDLIST_DOUBLE) {
                //双行数据
                //第一行
                PhotoUtils.showCard(PhotoUtils.UriType.HTTP, galleryRecommendInfo.items.get(0).getImg(), iv_recommend_1, MyDisplayImageOption.getOptions(R.drawable.pic_default),GalleryNewsPagerAdapter.this);
                tv_recommend_1.setText(galleryRecommendInfo.items.get(0).getTitle());
                //第二行
                PhotoUtils.showCard(PhotoUtils.UriType.HTTP, galleryRecommendInfo.items.get(1).getImg(), iv_recommend_2, MyDisplayImageOption.getOptions(R.drawable.pic_default),GalleryNewsPagerAdapter.this);
                tv_recommend_2.setText(galleryRecommendInfo.items.get(1).getTitle());
            }
        }

    }




        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            ImageView v = (ImageView)view;
            v.setScaleType(ImageView.ScaleType.CENTER);
        }


        @Override
        public void onLoadingCancelled(String imageUri, View view) {

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            ImageView v = (ImageView)view;
            v.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            ImageView v = (ImageView)view;
            v.setScaleType(ImageView.ScaleType.CENTER);
        }


    /**
     * 点击事件回调
     */
    public interface OnItemClickListener {
        void onItemClick(GalleryNewsItem item);
    }

}
