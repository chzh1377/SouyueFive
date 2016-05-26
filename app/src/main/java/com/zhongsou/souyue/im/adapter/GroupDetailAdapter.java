package com.zhongsou.souyue.im.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.GroupMembers;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.model.PersonPageParam;
import com.zhongsou.souyue.im.ac.AddGroupMemberActivity;
import com.zhongsou.souyue.im.ac.NewGroupDetailsActivity;
import com.zhongsou.souyue.im.dialog.ImDialog;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.IMIntentUtil;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.SYUserManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/6/9.
 */
public class GroupDetailAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<View> mViewList;
    private int listSize;//进来list换算出 item的 总个数
    private final int TYPE_1 = 0;
    private final int TYPE_2 = 1;
    private ImageLoader imgloader;
    private DisplayImageOptions optHeadImg;
    public boolean isInDeleteMode;
    private NewGroupDetailsActivity mContext;

    public static  final int REQUEST_CODE_ADD_USER = 0;
    private List<Contact> contacts;//通讯录
    private List<Contact> compareList = new ArrayList<Contact>();
    private ArrayList<GroupMembers> listMembers;

    public GroupDetailAdapter(NewGroupDetailsActivity context, ArrayList<GroupMembers> list, ArrayList<View> viewList) {
        inflater = LayoutInflater.from(context);
//        listSize = list.size();
        if(list.size()%4>0){
            listSize = list.size()/4+1;
        }else{
            listSize = list.size()/4;
        }
        this.listMembers = list;
        this.mContext = context;
//        this.mList = convertList(list);

        this.mViewList = viewList;
        this.imgloader = ImageLoader.getInstance();
        optHeadImg = new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(true).displayer(new RoundedBitmapDisplayer(10)).showImageForEmptyUri(R.drawable.default_head).showImageOnLoading(R.drawable.default_head).showImageOnFail(R.drawable.default_head).build();
    }

    private void setIsInDeleteMode(boolean isInDeleteMode) {
        this.isInDeleteMode = isInDeleteMode;
        notifyDataSetChanged();
    }

    /**
     * 转换list把一个的转换成四个的
     *
     * @param
     * @return
     */
//    private ArrayList<ArrayList<GroupMembers>> convertList(ArrayList<GroupMembers> list) {
//        ArrayList<ArrayList<GroupMembers>> arrayList = new ArrayList<ArrayList<GroupMembers>>();
//        int i = 0;
//        while (i < listSize) {
//            ArrayList<GroupMembers> arr = new ArrayList<GroupMembers>();
//            for (int j = 0; j < 4 && i < listSize; j++) {
//                arr.add(list.get(i));
//                i++;
//            }
//            arrayList.add(arr);
//        }
//        return arrayList;
//    }

    @Override
    public int getItemViewType(int position) {

        if (position < listSize) {
            return TYPE_1;
        } else {
            return TYPE_2;
        }

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return listSize + mViewList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position > listSize) {
            return listMembers.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        View view = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_1:
                    convertView = inflater.inflate(R.layout.groupdetail_list_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.head_photo_imageview1 = (ImageView) convertView.findViewById(R.id.head_photo_imageview1);
                    viewHolder.head_photo_imageview2 = (ImageView) convertView.findViewById(R.id.head_photo_imageview2);
                    viewHolder.head_photo_imageview3 = (ImageView) convertView.findViewById(R.id.head_photo_imageview3);
                    viewHolder.head_photo_imageview4 = (ImageView) convertView.findViewById(R.id.head_photo_imageview4);
                    viewHolder.username_tv1 = (TextView) convertView.findViewById(R.id.username_tv1);
                    viewHolder.username_tv2 = (TextView) convertView.findViewById(R.id.username_tv2);
                    viewHolder.username_tv3 = (TextView) convertView.findViewById(R.id.username_tv3);
                    viewHolder.username_tv4 = (TextView) convertView.findViewById(R.id.username_tv4);
                    viewHolder.badge_delete1 = (ImageView) convertView.findViewById(R.id.badge_delete1);
                    viewHolder.badge_delete2 = (ImageView) convertView.findViewById(R.id.badge_delete2);
                    viewHolder.badge_delete3 = (ImageView) convertView.findViewById(R.id.badge_delete3);
                    viewHolder.badge_delete4 = (ImageView) convertView.findViewById(R.id.badge_delete4);
                    convertView.setTag(viewHolder);
                    break;
                case TYPE_2:
                    break;
            }

        } else {
            switch (type) {
                case TYPE_1:
                    viewHolder = (ViewHolder) convertView.getTag();
                    break;
                case TYPE_2:
                    break;
            }
        }

        if (type == TYPE_1) {
            int i = 0;
            int h = position*4;
            while (i < 4 && h<listMembers.size()) {
                switch (i){
                    case 0:
                        initViewData(listMembers.get(h),viewHolder.head_photo_imageview1,viewHolder.username_tv1,viewHolder.badge_delete1);
                        break;
                    case 1:
                        initViewData(listMembers.get(h),viewHolder.head_photo_imageview2,viewHolder.username_tv2,viewHolder.badge_delete2);
                        break;
                    case 2:
                        initViewData(listMembers.get(h),viewHolder.head_photo_imageview3,viewHolder.username_tv3,viewHolder.badge_delete3);
                        break;
                    case 3:
                        initViewData(listMembers.get(h),viewHolder.head_photo_imageview4,viewHolder.username_tv4,viewHolder.badge_delete4);
                        break;
                }
//                initViewData(listMembers.get(h), viewHolder, i + 1);
                h++;
                i++;
            }
            for (; i < 4; i++) {
                switch (i){
                    case 0:
                        hideView(viewHolder.head_photo_imageview1,viewHolder.username_tv1,viewHolder.badge_delete1);
                        break;
                    case 1:
                        hideView(viewHolder.head_photo_imageview2, viewHolder.username_tv2, viewHolder.badge_delete2);
                        break;
                    case 2:
                        hideView(viewHolder.head_photo_imageview3, viewHolder.username_tv3, viewHolder.badge_delete3);
                        break;
                    case 3:
                        hideView(viewHolder.head_photo_imageview4, viewHolder.username_tv4, viewHolder.badge_delete4);
                        break;
                }
//                initViewData(null, viewHolder, i + 1);
            }
        } else {
            convertView = mViewList.get(position - listSize);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIsInDeleteMode(false);
            }
        });
        return convertView;
    }

    public class ViewHolder {
        ImageView head_photo_imageview1, head_photo_imageview2, head_photo_imageview3, head_photo_imageview4;
        TextView username_tv1, username_tv2, username_tv3, username_tv4;
        ImageView badge_delete1, badge_delete2, badge_delete3, badge_delete4;
    }

    private void initViewData(final GroupMembers groupMembers, ImageView headPhoto,TextView userName,ImageView badgeDelete) {
        if (groupMembers.getMember_id() == 0) {//减人按钮
            if (isInDeleteMode) {
                // 正处于删除模式下，隐藏删除按钮
                headPhoto.setVisibility(View.GONE);
                userName.setVisibility(View.GONE);
                badgeDelete.setVisibility(View.GONE);
                return;
            } else {
                // 正常模式
                headPhoto.setVisibility(View.VISIBLE);
                userName.setVisibility(View.VISIBLE);
                badgeDelete.setVisibility(View.GONE);
            }
            this.imgloader.displayImage("drawable://"+ R.drawable.minus_btn_normal, headPhoto, optHeadImg);
            userName.setText("");

            headPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setIsInDeleteMode(true);
                    notifyDataSetChanged();
                }
            });
        } else if (groupMembers.getMember_id() == 1) {// 加人按钮
            if (isInDeleteMode) {
                // 正处于删除模式下，隐藏删除按钮
                headPhoto.setVisibility(View.GONE);
                userName.setVisibility(View.GONE);
                badgeDelete.setVisibility(View.GONE);
                return;
            } else {
                // 正常模式
                headPhoto.setVisibility(View.VISIBLE);
                userName.setVisibility(View.VISIBLE);
                badgeDelete.setVisibility(View.GONE);
            }
            this.imgloader.displayImage("drawable://"+ R.drawable.smiley_add_btn_nor, headPhoto, optHeadImg);
            userName.setText("");
            headPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 进入选人页面
                    getFriends(listMembers);
                    Intent intent = new Intent(mContext, AddGroupMemberActivity.class);
                    intent.putExtra("contact", (Serializable) compareList);
                    intent.putExtra("group", mContext.getmGroup());
                    intent.putExtra("member", mContext.getmGroupMembers());
                    intent.putExtra("count", Integer.parseInt(mContext.getCount()));
                    intent.putExtra("maxCount", mContext.getmGroupMax_number());
                    mContext.startActivityForResult(intent, REQUEST_CODE_ADD_USER);

                }
            });

        } else {
            headPhoto.setVisibility(View.VISIBLE);
            userName.setVisibility(View.VISIBLE);
            this.imgloader.displayImage(groupMembers.getMember_avatar(), headPhoto, optHeadImg);
            userName.setText(!TextUtils.isEmpty(groupMembers.getMember_name()) ? groupMembers.getMember_name() : groupMembers.getNick_name());
            //是自己，重新设置(解决更换搜悦头像，详情没变问题。)
            if (SYUserManager.getInstance().getUserId().equals(groupMembers.getMember_id() + "")) {
                this.imgloader.displayImage(SYUserManager.getInstance().getUser().image(), headPhoto, optHeadImg);
            }
            if (isInDeleteMode) {
                // 如果是删除模式下，显示减人图标
                badgeDelete.setVisibility(View.VISIBLE);
            } else {
                badgeDelete.setVisibility(View.INVISIBLE);
            }
//            if (groupMembers != null && groupMembers.getIs_owner() == 1) {
                //是群主，创建者的id == 当前用户id
            if (SYUserManager.getInstance().getUserId().equals(Long.toString(groupMembers.getMember_id()))) {
                badgeDelete.setVisibility(View.INVISIBLE);
            }

            headPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isInDeleteMode) {
                        // 如果是删除自己，return
                        if (SYUserManager.getInstance().getUserId().equals(groupMembers.getMember_id() + "")) {
                            ImDialog.Builder build = new ImDialog.Builder(mContext);
                            build.setMessage("不允许移除自己");
                            build.setPositiveButton(R.string.im_dialog_ok, new ImDialog.Builder.ImDialogInterface() {
                                @Override
                                public void onClick(DialogInterface dialog, View v) {
                                }
                            }).create().show();
                            return;
                        }
                        if (!CMainHttp.getInstance().isNetworkAvailable(mContext)) {
                            SouYueToast.makeText(mContext, mContext.getString(R.string.user_login_networkerror), 0).show();
                            return;
                        }
                        mContext.showProgress("正在删除...");
                        deleteMembersFromGroup(groupMembers);
                    } else {
                        if (!CMainHttp.getInstance().isNetworkAvailable(mContext)) {
                            SouYueToast.makeText(mContext, mContext.getString(R.string.user_login_networkerror), 0).show();
                            return;
                        }
                        // 正常情况下点击user，可以进入用户详情或者聊天页面
                        ImserviceHelp.getInstance().getMemberDetail(9, groupMembers.getGroup_id(), groupMembers.getMember_id());
                        IMIntentUtil.gotoIMFriendInfo(mContext, groupMembers.getMember_id(), groupMembers.getGroup_id(), PersonPageParam.FROM_IM);
                    }
                }

            });
        }
    }

    private void hideView(ImageView headPhoto,TextView userName,ImageView badgeDelete){
        headPhoto.setVisibility(View.GONE);
        userName.setVisibility(View.GONE);
        badgeDelete.setVisibility(View.GONE);
    }

    private void getFriends(List<GroupMembers> mlist) {
        compareList.clear();
        contacts = ImserviceHelp.getInstance().db_getContact();
        for(int i = 0;i<mlist.size();i++){
            for(int j = 0 ;j<contacts.size();j++){
                if(contacts.get(j).getChat_id() == mlist.get(i).getMember_id()){
                    compareList.add(contacts.get(j));
                }
            }
        }
    }

    private void deleteMembersFromGroup(GroupMembers groupMembers){
        ArrayList<Long> ids = new ArrayList<Long>();
        ids.add(groupMembers.getMember_id());
        mContext.service.addOrDeleteGroupMembersOp(3, mContext.getmGroup().getGroup_id() + "", ids);
        listMembers.remove(groupMembers);
        notifyDataSetChanged();
        mContext.updatGroupCount();
    }

}
