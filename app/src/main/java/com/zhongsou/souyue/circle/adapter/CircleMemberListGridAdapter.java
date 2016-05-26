package com.zhongsou.souyue.circle.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.model.CircleMemberItem;
import com.zhongsou.souyue.circle.model.PersonPageParam;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.ui.UIHelper.ChangeDateCallback;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.countUtils.ZSSdkUtil;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.BanTalkReq;
import com.zhongsou.souyue.net.circle.KickOutCircleReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.Utils;

import java.util.List;

public class CircleMemberListGridAdapter extends BaseAdapter implements IVolleyResponse{
    private List<CircleMemberItem> mItems;
    private Context mContext;
//    private AQuery aQuery;
//    private Http http;
    private boolean isBanTalk = false;
    private View view1;
    private  CircleMemberItem item1;
    private int type;
    private ChangeDateCallback listener;
    private String new_srpId;
    private String keyWord;
    private int role;
    
    public CircleMemberListGridAdapter(Context context,List<CircleMemberItem> mItems,int type) {
        mContext = context;
        this.mItems = mItems;
        this.type = type;
//        aQuery = new AQuery(context);
//        http = new Http(this);
    }
    
    public void setChangerListener(ChangeDateCallback listener){
    	this.listener = listener;
    }


	static class ViewHolder {
    	ImageView image;
    	ImageView imgBanTalk;
    	TextView text;
    	ImageView imgPrize;
    	ImageView imgBgPrize;
    }
 
    @Override
    public int getCount() {
        return mItems.size();
    }
 
    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
    	ViewHolder viewHolder = null;
        if(convertView == null) {
        	viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.circle_member_list_item, null);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.imgBanTalk = (ImageView) convertView.findViewById(R.id.icon_jinyan);
            viewHolder.imgPrize = (ImageView) convertView.findViewById(R.id.circle_prize);
            viewHolder.imgBgPrize = (ImageView) convertView.findViewById(R.id.circle_prize_bg);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.image.setImageResource(R.drawable.circle_default_head);
         final CircleMemberItem item = (CircleMemberItem) getItem(position);
        viewHolder.text.setText(item.getNickname());
        if (!TextUtils.isEmpty(item.getImage())) {
            //aQuery.id(viewHolder.image).image(  Utils.getImageUrl(item.getImage()) , true, true, 0, R.drawable.default_head);
           PhotoUtils.showCard(PhotoUtils.UriType.HTTP,  Utils.getImageUrl(item.getImage()),viewHolder.image,MyDisplayImageOption.options);
        }
        if(item.getIs_bantalk() == Constant.MEMBER_BAN_TALK_YES){
        	viewHolder.imgBanTalk.setVisibility(View.VISIBLE);
       }
        if(item.getIs_bantalk() == Constant.MEMBER_BAN_TALK_NO){
        	viewHolder.imgBanTalk.setVisibility(View.GONE);
		}
        if(item.getRole() == 1){
        	viewHolder.imgBgPrize.setVisibility(View.VISIBLE);
        	viewHolder.imgPrize.setVisibility(View.VISIBLE);
        }else{
        	viewHolder.imgBgPrize.setVisibility(View.GONE);
        	viewHolder.imgPrize.setVisibility(View.GONE);
        }
      //点击item显示名片
        convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {   //之后做个跳转就行
              /*  Intent mypostIntent = new Intent(mContext, CircleVCardActivity.class);
                mypostIntent.putExtra("open_mode", CircleVCardActivity.OPEN_MODE_VISITING_CARD);
                mypostIntent.putExtra("member_id", item.getMember_id());
                mypostIntent.putExtra("user_id", item.getUser_id());
                mypostIntent.putExtra("interest_id", item.getInterest_id());
                mContext.startActivity(mypostIntent);*/
                PersonPageParam param = new PersonPageParam();
                param.setSrp_id(getNew_srpId());
                param.setInterest_id(item.getInterest_id());
                param.setFrom(PersonPageParam.FROM_INTEREST);
                param.setViewerUid(item.getUser_id());
                param.setCircleName(getKeyWord());
                param.setInterest_id(item.getInterest_id());
                UIHelper.showPersonPage((Activity)mContext, param);
				
			}
		});
        //长按禁言操作
        convertView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				if(role != Constant.ROLE_ADMIN && role != Constant.ROLE_SUB_ADMIN){
					return true;
				}
				if((role == Constant.ROLE_ADMIN || role == Constant.ROLE_SUB_ADMIN) && item.getRole() == 1){
					UIHelper.ToastMessage(mContext , "不能给圈主禁言");
					return true;
				}
                if(role == Constant.ROLE_SUB_ADMIN && item.getRole() == 4){
                    UIHelper.ToastMessage(mContext , "不能给副圈主禁言");
                    return true;
                }
				view1 = v;
				item1 = item;
				//弹窗
				Dialog dialog = getDialog(mContext);
				dialog.show();
				dialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT);
				return true;
			}
		});
        return convertView;
    }

    public static DisplayImageOptions // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
            options = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.default_logo) // 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.drawable.default_logo) // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.default_logo) // 设置图片加载或解码过程中发生错误显示的图片
            .showImageOnLoading(R.drawable.default_logo).cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedBitmapDisplayer(0))
            .build();
    
    @SuppressLint("NewApi")
	private Dialog getDialog(final Context mContext) {
		final Dialog dialog = new Dialog(mContext,R.style.MMTheme_DataSheet);
		View contentView = LayoutInflater.from(mContext).inflate(R.layout.circle_member_manger_dialog, null);
		dialog.setCanceledOnTouchOutside(true);
		
		ImageView img = null;
	    TextView msg2Adder;
		Button btnBantalk, btnQingChu,btnCancle;
		
		img = (ImageView)contentView.findViewById(R.id.et_cricle_member_manger_logo);
		msg2Adder = (TextView) contentView.findViewById(R.id.circle_name);
		btnBantalk = (Button) contentView.findViewById(R.id.btn_cricle_manage_bantalk);
		btnQingChu = (Button) contentView.findViewById(R.id.btn_cricle_member_qingchu);
		btnCancle = (Button) contentView.findViewById(R.id.btn_cricle_member_cancle);
		
		if(item1.getIs_bantalk() == Constant.MEMBER_BAN_TALK_NO){
			btnBantalk.setText("禁止发言");
			btnBantalk.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_save));
		}else if(item1.getIs_bantalk() == Constant.MEMBER_BAN_TALK_YES){
			btnBantalk.setText("解除禁言");
			btnBantalk.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_save));
		}
		
		if(type == Constant.INTEREST_TYPE_PRIVATE &&  role == Constant.ROLE_ADMIN){  //type == 1
			btnQingChu.setVisibility(View.VISIBLE);
		}else if(type == Constant.INTEREST_TYPE_NORMAL){    //type == 0
			btnQingChu.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(item1.getImage())) {
			// aQuery.id(img).image(item1.getImage(), true, true,0,R.drawable.default_logo);
                        PhotoUtils.showCard( PhotoUtils.UriType.HTTP, item1.getImage(),img, options);
		 }
		msg2Adder.setText(item1.getNickname());
		btnCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
			
		});
		
		btnQingChu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDeleteAlert();
				dialog.dismiss();
			}
		});
		
		btnBantalk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(item1.getIs_bantalk() == Constant.MEMBER_BAN_TALK_NO){   //执行禁言
					isBanTalk = true;
//					http.banTalk(item1.getMember_id(), 1);
					setBankTalk(item1.getMember_id(), 1);
				}
				else if(item1.getIs_bantalk() == Constant.MEMBER_BAN_TALK_YES){
					isBanTalk = false; 
//					http.banTalk(item1.getMember_id(), 0);
					setBankTalk(item1.getMember_id(), 0);
				}

				dialog.dismiss();
			}
		});
		Window w = dialog.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.gravity = Gravity.CENTER;
		dialog.onWindowAttributesChanged(lp);
		dialog.setContentView(contentView);
		return dialog;
	}

	/**
	 * 设置禁言操作
	 * @param memberId
	 * @param status
     */
	private void setBankTalk(long memberId, int status)
	{
		BanTalkReq req = new BanTalkReq(HttpCommon.CIRLCE_BAN_TALK_ID,this);
		req.setParams(memberId,status);
		CMainHttp.getInstance().doRequest(req);

	}

	@Override
	public void onHttpResponse(IRequest request) {
		switch (request.getmId())
		{
			case HttpCommon.CIRLCE_BAN_TALK_ID:
				banTalkSuccess(request.<HttpJsonResponse>getResponse());
				break;
			case HttpCommon.CIRLCE_KICKOUT_MEMBER:
				kickCircleSuccess(request.<HttpJsonResponse>getResponse());
				break;
		}
	}

	@Override
	public void onHttpError(IRequest request) {
		switch (request.getmId())
		{
			case HttpCommon.CIRLCE_BAN_TALK_ID:
				break;
		}
	}

	@Override
	public void onHttpStart(IRequest request) {

	}
    
    
    private void showDeleteAlert() {
        Dialog alertDialog = new AlertDialog.Builder(mContext)
                .setMessage("确定删除该圈成员？")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                            	http.kickCircle(item1.getMember_id(), SYUserManager.getInstance().getToken());
								kickoutcircle(item1.getMember_id(), SYUserManager.getInstance().getToken());

                                UpEventAgent.onGroupQuit(mContext,item1.getInterest_id()+"."+"","");
                            	dialog.dismiss();
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
    }

	/**
	 * 提出圈子
	 * @param memberId
	 * @param token
     */
	private void kickoutcircle(long memberId, String token)
	{
		KickOutCircleReq req = new KickOutCircleReq(HttpCommon.CIRLCE_KICKOUT_MEMBER,this);
		req.setParams(memberId,token, ZSSdkUtil.OTHER_SUBSCRIBE_MENU);
		CMainHttp.getInstance().doRequest(req);
	}
    public void banTalkSuccess(HttpJsonResponse res){
    	int statusCode = res.getCode();
	      if(statusCode != 200){
	          return;
	      }
	      ViewHolder viewHolder =(ViewHolder) view1.getTag();
		if(res.getBody().get("result").getAsInt() == 1){
	    	  if(isBanTalk){
	    		  viewHolder.imgBanTalk.setVisibility(View.VISIBLE);
	    		  item1.setIs_bantalk(1);
	    		  Toast.makeText(mContext, "该圈成员已被禁言", Toast.LENGTH_SHORT).show();
	    	  }else{
		    	  viewHolder.imgBanTalk.setVisibility(View.GONE);
		    	  item1.setIs_bantalk(0);
		    	  Toast.makeText(mContext, "该圈成员已解除禁言", Toast.LENGTH_SHORT).show();
		      }
	    }else{
	    	  Toast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
	      }
    }
    
    public void kickCircleSuccess(HttpJsonResponse res ){
    	int statusCode = res.getCode();
	      if(statusCode != 200){
	          return;
	      }
	      if(listener != null){
	    	  listener.changeDate();
	      }
	      Toast.makeText(mContext, "请出圈子成功", Toast.LENGTH_SHORT).show();  
    }

    public String getNew_srpId() {
        return new_srpId;
    }

    public void setNew_srpId(String new_srpId) {
        this.new_srpId = new_srpId;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}