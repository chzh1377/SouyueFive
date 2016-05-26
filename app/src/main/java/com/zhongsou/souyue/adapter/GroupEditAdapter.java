package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.AGridDynamic;
import com.zhongsou.souyue.module.GroupSelect;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @auther: qubian
 * @data: 2016/4/1.
 */
public class GroupEditAdapter extends BaseAdapter {
    private ArrayList<AGridDynamic> mDataList;
    private List<GroupSelect> mSelectList;
    private Context mContext;


    public GroupEditAdapter(Context context, List<?> items) {
        this.mContext = context;
        mDataList = new ArrayList<AGridDynamic>();
        for (Object ite : items) {
            if(ite!=null) {
                mDataList.add((AGridDynamic) ite);
            }
        }
    }

    public List<GroupSelect> getmSelectList() {
        return mSelectList;
    }

    public void setmSelectList(List<GroupSelect> mSelectList) {
        this.mSelectList = mSelectList;
    }

    public void pause() {
        ImageLoader.getInstance().pause();
    }

    public void resume() {
        ImageLoader.getInstance().resume();
    }

    public ArrayList<AGridDynamic> getItems() {
        return mDataList;
    }

    public void setData(List<?> items) {
        mDataList.clear();
        for (Object ite : items) {
            mDataList.add((AGridDynamic) ite);
        }
    }

    @Override
    public int getCount() {
        return  mDataList!=null?mDataList.size():0;
    }

    @Override
    public AGridDynamic getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDataList.get(position).getmId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GroupItemCircle holder;
        if (convertView == null) {
            holder = new GroupItemCircle(mContext);
        } else {
            holder = (GroupItemCircle) convertView;
        }
        AGridDynamic item = getItem(position);
        GroupSelect select =mSelectList.get(position);
        holder.build(item, position, select.isSelect());
        item.setmPosition(position);
        return holder;
    }
    public class GroupItemCircle extends LinearLayout {
        private DisplayImageOptions options;
        private AGridDynamic mDataItem;
        public TextView titleText;
        public ImageView image;
        private ImageView isSecret;
        private ImageView isSelectIv;
        private int mPostion;
        private Context context;

        public GroupItemCircle(Context context) {
            super(context);
            this.context = context;
            inflate(context, R.layout.item_group_edit_grid, this);
            setGravity(Gravity.CENTER);
            titleText = (TextView) findViewById(R.id.item_title);
            image = (ImageView) findViewById(R.id.item_img);
            isSecret = (ImageView) findViewById(R.id.issecret);
            isSelectIv = (ImageView) findViewById(R.id.group_edit_select);
            options = MyDisplayImageOption.newoptions;
        }
        public void build(AGridDynamic item, final int position, boolean isSelect) {
            SuberedItemInfo info = (SuberedItemInfo) item;
            titleText.setText(info.getTitle());
            if (!"1".equals(info.getType())) {
                isSecret.setVisibility(View.GONE);
                if (StringUtils.isNotEmpty(info.getImage())) {
                    PhotoUtils.showCard(PhotoUtils.UriType.HTTP, info.getImage(), image, options);
                } else {
                    PhotoUtils.showCard(PhotoUtils.UriType.DRAWABLE, R.drawable.news_default_img_c + "", image, options);
                }
            }
            if ("1".equals(info.getType())) {
                PhotoUtils.showCard(PhotoUtils.UriType.HTTP, info.getImage(),
                        image, options);
                isSecret.setVisibility(View.VISIBLE);
            }
            mPostion = position;
            mDataItem = item;
            isSelectIv.setVisibility(isSelect? View.VISIBLE:View.GONE);
            if (item.getmState() == AGridDynamic.STATE_INIT) {
                setVisibility(VISIBLE);
            } else {
                setVisibility(INVISIBLE);
            }
        }
    }
}