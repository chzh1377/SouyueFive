package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.share.ShareType;
import com.zhongsou.souyue.share.ShareTypeHelper;
import com.zhongsou.souyue.utils.ShareConstantsUtils;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;

import java.util.List;

/**
 * 分享
 *
 * @author zhangliang01@zhongsou.com
 */
public class ShareAdapter extends BaseAdapter {
    private void initFromShareTypes() {
        if (this.shareTypes != null) {
            this.mTitles = ShareTypeHelper.getTitles(shareTypes);
            this.mIcons = ShareTypeHelper.getDrawables(shareTypes);
            this.mIds = ShareTypeHelper.getIds(shareTypes);
        }
    }

    private String[] mTitles;
    private int[] mIds;
    private int[] mIcons;
    private final LayoutInflater mInflater;
    private List<ShareType> shareTypes;
    private Context context;

    public ShareAdapter(Context context, String shareType) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        if (StringUtils.isEmpty(shareType)) {
            return;
        }
        if (shareType.equals(ShareConstantsUtils.READABILITY) || shareType.equals(ShareConstantsUtils.WEBSRCVIEW) || shareType.equals(ShareConstantsUtils.SELFCREATEDETAIL)) {
            shareTypes = ShareTypeHelper.hasAll();//share_names
        } else if (shareType.equals(ShareConstantsUtils.RECOMMENDFRIEND) || shareType.equals(ShareConstantsUtils.WEBSRCVIEWKEYWORD) || shareType.equals(ShareConstantsUtils.SEARCH)||shareType.equals(ShareConstantsUtils.VIDEO)) {
            shareTypes = ShareTypeHelper.without搜悦();//re_share_names
        } else if (shareType.equals(ShareConstantsUtils.SUPERSRP)) {
            shareTypes = ShareTypeHelper.without网友推荐区();//share_names_rankdetail
        } else if (shareType.equals(ShareConstantsUtils.DIMENSIONALCODE) || shareType.equals(ShareConstantsUtils.QRCODEA) || shareType.equals(ShareConstantsUtils.QRCODEF)) {
            shareTypes = ShareTypeHelper.has微信And微博And邮件();//re_share_names_code
        } else if (shareType.equals(ShareConstantsUtils.READABILITYKEYWORD)) {
            shareTypes = ShareTypeHelper.without搜悦();//share_names_rss
        } else if (shareType.equals(ShareConstantsUtils.SRP) || shareType.equals(ShareConstantsUtils.WEBSRCVIEWWEBTYPE)) {
            shareTypes = ShareTypeHelper.without搜悦好友And第三方();//share_names_srp
        } else if (shareType.equals(ShareConstantsUtils.WEBSRCVIEWWEBTYPE)) {
            shareTypes = ShareTypeHelper.without搜悦好友And第三方();//share_names_srp
        } else if (shareType.equals(ShareConstantsUtils.NEW_DETAIL)) {
            shareTypes = ShareTypeHelper.without搜悦网友And网友推荐区();
        } else if (shareType.equals(ShareConstantsUtils.GALLERY_NEWS)) {
            shareTypes = ShareTypeHelper.withThirdpartAndCircle();
        } else {
            shareTypes = ShareTypeHelper.without搜悦();//re_share_names
        }
        initFromShareTypes();
    }

    @Override
    public int getCount() {
        return mIds.length;
    }

    @SuppressWarnings("boxing")
    @Override
    public Integer getItem(int position) {
        return mIds[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.sharemenu_item, parent, false);
            holder.share_way = (TextView) convertView.findViewById(R.id.share_way);
            holder.share_icon = (ImageView) convertView.findViewById(R.id.share_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.share_way.setText(mTitles[position]);
        holder.share_icon.setImageDrawable(ShareAdapter.this.context.getResources().getDrawable(mIcons[position]));
        return convertView;
    }

    private class ViewHolder {
        TextView share_way;
        ImageView share_icon;
    }

    public ShareAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        if (Utils.isWX()) {
            shareTypes = ShareTypeHelper.has微信And微博And邮件();//re_share_names_code_weixin
        } else {
            shareTypes = ShareTypeHelper.re_share_names_code2();//re_share_names_code2
        }
        initFromShareTypes();
    }

    public ShareAdapter(Context context, boolean isRecommend, String keyword) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        if (!isRecommend) {
            if (StringUtils.isEmpty(keyword)) {
                if (Utils.isWX()) {
                    shareTypes = ShareTypeHelper.withoutQQ();//share_names_weixin
                } else {
                    shareTypes = ShareTypeHelper.re_share_names2();//re_share_names2
                }
            } else {
                if (StringUtils.isSuperSrp(keyword, null) != 0) {
                    if (Utils.isWX()) {
                        shareTypes = ShareTypeHelper.without网友推荐区AndQQ();//share_names_rankdetail_weixin
                    } else {
                        shareTypes = ShareTypeHelper.without网友推荐区And腾讯();//share_names_rankdetail2
                    }
                } else {
                    if (Utils.isWX()) {
                        shareTypes = ShareTypeHelper.withoutQQ();//share_names_weixin
                    } else {
                        shareTypes = ShareTypeHelper.without腾讯();//share_names2
                    }
                }
            }
        } else {
            if (Utils.isWX()) {
                shareTypes = ShareTypeHelper.without搜悦AndQQ();//re_share_names_weixin
            } else {
                shareTypes = ShareTypeHelper.re_share_names2();//re_share_names2
            }
        }
        initFromShareTypes();
    }
}
