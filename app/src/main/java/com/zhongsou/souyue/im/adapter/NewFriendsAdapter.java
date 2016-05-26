package com.zhongsou.souyue.im.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.tuita.sdk.im.db.module.NewFriend;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.dialog.ImDialog;
import com.zhongsou.souyue.im.dialog.ImDialog.Builder.ImDialogInterface;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.view.SwipeListView;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.ui.ImRequestDialog;
import com.zhongsou.souyue.ui.SouYueToast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class NewFriendsAdapter extends BaseAdapter {
    private SwipeListView listView;
    private List<NewFriend> newFriendslist = new ArrayList<NewFriend>();
//    private AQuery aquery;
    private ImserviceHelp instance = ImserviceHelp.getInstance();

    public interface OnDeleteListener {
        void onDeleteItem(int position);
    }

    private OnDeleteListener deleteListener = new OnDeleteListener() {

        @Override
        public void onDeleteItem(int position) {
            if (listView instanceof SwipeListView) {
                ((SwipeListView) listView).onChanged();
                instance.db_delNewFriend(newFriendslist.get(position).getChat_id());
                newFriendslist.remove(position);
                notifyDataSetChanged();
            }
        }
    };

    private Context mcontext;

    public NewFriendsAdapter(Context mcontext, SwipeListView listView) {
        this.mcontext = mcontext;
        this.listView = listView;
//        aquery = new AQuery(mcontext);
    }

    @Override
    public int getCount() {
        if (newFriendslist != null&&newFriendslist.size()>0) {
            return newFriendslist.size();
        } else {
            return 0;
        }
    }

    public void setData(List<NewFriend> newFriendslist) {
        if(newFriendslist != null){

            this.newFriendslist = newFriendslist;
        }
        notifyDataSetChanged();
    }

    @Override
    public NewFriend getItem(int position) {

        return newFriendslist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mcontext).inflate(R.layout.im_newfriendsitem, parent, false);
            holder.item_left = (RelativeLayout) convertView.findViewById(R.id.item_left);
            holder.item_right = (RelativeLayout) convertView.findViewById(R.id.item_right);
            holder.ivImage = (ImageView) convertView.findViewById(R.id.row_iv_image);
            holder.tvName = (TextView) convertView.findViewById(R.id.row_tv_name);
            holder.tvMessage = (TextView) convertView.findViewById(R.id.row_tv_message);
            holder.bAction_add = (Button) convertView.findViewById(R.id.row_btn_add);
            holder.row_btn_delete = (Button) convertView.findViewById(R.id.row_btn_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LinearLayout.LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        holder.item_left.setLayoutParams(lp1);
        LinearLayout.LayoutParams lp2 = new LayoutParams(listView.getRightViewWidth(), LayoutParams.MATCH_PARENT);
        holder.item_right.setLayoutParams(lp2);
        setViewData(position, (ViewHolder) convertView.getTag());
        return convertView;
    }

    private void setViewData(final int position, final ViewHolder holder) {
        final NewFriend nf = getItem(position);
        if (nf.getComment_name() != null && !"".equals(nf.getComment_name().trim())) {
            holder.tvName.setText(nf.getComment_name());
        } else {
            holder.tvName.setText(nf.getNick_name());
        }
        if("".equals(nf.getAllow_text())&&!"".equals(nf.getOrigin())){
            holder.tvMessage.setText(nf.getOrigin());
        }else {
            holder.tvMessage.setText(nf.getAllow_text());
        }
        int status = nf.getStatus();
        switch (status) {
            case NewFriend.STATUS_WAITING_ADD:
                holder.bAction_add.setText(mcontext.getString(R.string.add_friends_status));
                holder.bAction_add.setTextColor(mcontext.getResources().getColor(R.color.im_operation_text_color));
                holder.bAction_add.setEnabled(true);
                break;
            case NewFriend.STATUS_ALLOW:
                holder.bAction_add.setText(mcontext.getString(R.string.verify_friends_status));
                holder.bAction_add.setTextColor(mcontext.getResources().getColor(R.color.im_friends_operation_text_color));
                holder.bAction_add.setEnabled(true);
                break;
            case NewFriend.STATUS_HAS_ADD:
                holder.bAction_add.setText(mcontext.getString(R.string.already_friends_status));
                holder.bAction_add.setTextColor(mcontext.getResources().getColor(R.color.im_friends_already_friends_status_color));
                holder.bAction_add.setEnabled(false);
                break;
            default:
                holder.bAction_add.setText(mcontext.getString(R.string.wait_friends_status));
                holder.bAction_add.setTextColor(mcontext.getResources().getColor(R.color.im_friends_already_friends_status_color));
                holder.bAction_add.setEnabled(false);
                break;
        }

        //aquery.id(holder.ivImage).image(nf.getAvatar(), true, true);
        PhotoUtils.showCard(PhotoUtils.UriType.HTTP,nf.getAvatar(),holder.ivImage,MyDisplayImageOption.defaultOption);
        holder.row_btn_delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteListener.onDeleteItem(position);
            }
        });
        holder.bAction_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewFriend newFriend=getItem(position);
                int p = newFriend.getStatus();
                switch (p) {
                    case NewFriend.STATUS_ALLOW:// 通过验证
//                        if(ImserviceHelp.getInstance().im_userOp(2, newFriend.getChat_id(), newFriend.getNick_name(), newFriend.getAvatar(), "")){
//                        newFriend.setStatus(NewFriend.STATUS_HAS_ADD);
//                        notifyDataSetChanged();
//                        }else{
//                            Toast.makeText(mcontext,"操作失败",Toast.LENGTH_LONG).show();
//                        }

                        ImRequestDialog mDialog = new ImRequestDialog(mcontext);
                        mDialog.show();
                        if(CMainHttp.getInstance().isNetworkAvailable(mcontext)){
                            if(ImserviceHelp.getInstance().im_userOp(2, newFriend.getChat_id(), newFriend.getNick_name(), newFriend.getAvatar(), "")){
                                mDialog.mDismissDialog();
                                newFriend.setStatus(NewFriend.STATUS_HAS_ADD);
                                notifyDataSetChanged();
//                                SouYueToast.makeText(mcontext, "发送成功", Toast.LENGTH_SHORT).show(); //发送成功不提示
                            }else{
                                mDialog.mDismissDialog();
                                SouYueToast.makeText(mcontext, mcontext.getString(R.string.im_server_busy), Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            mDialog.mDismissDialog();
                            SouYueToast.makeText(mcontext, mcontext.getString(R.string.im_net_unvisiable), Toast.LENGTH_SHORT).show();
                        }


                        break;
                    case NewFriend.STATUS_WAITING_ADD:// 显示添加按钮
                        ImDialog.Builder builder = new ImDialog.Builder(mcontext);
                        builder.setEditMsg(mcontext.getString(R.string.im_dialog_txt_num));
                        builder.setPositiveButton("确定", new ChangeClickListener(newFriend, mcontext));
                        builder.create().show();
                        break;
                    default:
                        break;
                }
                
            }
        });
        
    }
    private class ViewHolder {
        private RelativeLayout item_left;
        private RelativeLayout item_right;
        private Button bAction_add, row_btn_delete;
        private TextView tvName, tvMessage;
        private ImageView ivImage;
    }
    private class ChangeClickListener implements ImDialogInterface{
        NewFriend item;
        WeakReference<Context> weakReference;
        public ChangeClickListener(NewFriend item, Context ctx) {
            this.item = item;
            weakReference = new WeakReference<Context>(ctx);
        }
        @Override
        public void onClick(DialogInterface dialog, View v) {  
            if (item != null && weakReference != null && weakReference.get() != null) {
                item.setStatus(NewFriend.STATUS_WAITING_ALLOW);
                item.setAllow_text(v.getTag().toString());
//                if(ImserviceHelp.getInstance().im_userOp(1, item.getChat_id(), item.getNick_name(), item.getAvatar(), v.getTag().toString())){
//                    dialog.dismiss();
//                    notifyDataSetChanged();
//                }else{
//                    Toast.makeText(mcontext,"操作失败",Toast.LENGTH_LONG).show();
//                }

                ImRequestDialog mDialog = new ImRequestDialog(mcontext);
                mDialog.show();
                if(CMainHttp.getInstance().isNetworkAvailable(mcontext)){
                    if(ImserviceHelp.getInstance().im_userOp(1, item.getChat_id(), item.getNick_name(), item.getAvatar(), v.getTag().toString())){
                        mDialog.mDismissDialog();
                        notifyDataSetChanged();
//                                SouYueToast.makeText(mcontext, "发送成功", Toast.LENGTH_SHORT).show(); //发送成功不提示
                    }else{
                        mDialog.mDismissDialog();
                        SouYueToast.makeText(mcontext, mcontext.getString(R.string.im_server_busy), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    mDialog.mDismissDialog();
                    SouYueToast.makeText(mcontext, mcontext.getString(R.string.im_net_unvisiable), Toast.LENGTH_SHORT).show();
                }
            }
        }
        
    }
}
