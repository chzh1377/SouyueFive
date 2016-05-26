package com.zhongsou.souyue.share;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SettingActivity;
import com.zhongsou.souyue.module.SpecialDialogData;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.home.SysRecSpecialRequest;
import com.zhongsou.souyue.net.srp.SpecialRecListRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.net.volley.SpecialDialogHttp;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.MyImageLoader;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.UserInfoUtils;
import com.zhongsou.souyue.utils.Utils;

import java.util.List;

public class SpecialRecommendDialog extends Dialog implements IVolleyResponse,android.view.View.OnClickListener,OnItemClickListener{
    protected SpecialDialogHttp mVolleyHttp;
    private CheckBox isRecommend;
    private ListView list;
    private TextView special_close;
    private Context mcontext;
    private List<SpecialDialogData> sData;
    private SpecialAdapter specialAdapter;
//    protected static final int GET_SPECIAL_LIST_DATA=0;
    protected static final int SUBSCRIBESPECIAL_SPECIAL=1;
    private static int position;
    private CMainHttp mMainHttp;
    private boolean checked;
//    private SpecialSubRecever receiver;
    private IntentFilter mFilter;
    public static SpecialRecommendDialog specialrecommenddialog;
    private static boolean isDialogShow;
    private static int isNewFirst;
    private boolean pushSpecialState;
    private User user;
    public static SpecialRecommendDialog getInstance(){
        if(specialrecommenddialog == null){
            specialrecommenddialog=new SpecialRecommendDialog(UserInfoUtils.getActivity());
            ++isNewFirst;
        }
        if(isNewFirst==0&&!isDialogShow){
            ++isNewFirst;
            specialrecommenddialog=new SpecialRecommendDialog(UserInfoUtils.getActivity());
        }
        return specialrecommenddialog;
    }

    private SpecialRecommendDialog(Context context) {
        super(context,R.style.DialogSpecial);
        this.mcontext=context;
        mVolleyHttp =new SpecialDialogHttp(mcontext);
        mMainHttp = CMainHttp.getInstance();
//        receiver = new SpecialSubRecever();
//        mFilter = new IntentFilter(SRPActivity.subScribe);
//        LocalBroadcastManager.getInstance(mcontext).registerReceiver(receiver,mFilter);
        specialrecommenddialog=this;
        setOwnerActivity((Activity)context);
    } 
    
    @Override
    public void show() {
        isDialogShow=true;
        isNewFirst=0;
        super.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.special_recomend_layout);
        initView();
    }


    private void initView(){
        isRecommend=(CheckBox) findViewById(R.id.special_checkbox);
        user= SYUserManager.getInstance().getUser();
        pushSpecialState=UserInfoUtils.getSpecialState(user, pushSpecialState);
        isRecommend.setChecked(!pushSpecialState);
        isRecommend.setOnCheckedChangeListener(new OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton paramCompoundButton, boolean checked) {
                SpecialRecommendDialog.this.checked=checked;
                 String state;
                 if(checked){
                     state="0";
                 }else{
                     state="1";
                 }
                 SysRecSpecialRequest sysRecRequest = new SysRecSpecialRequest(HttpCommon.RECOMMEND_SPECIAL_SWITCH, SpecialRecommendDialog.this);
                 sysRecRequest.setParams(state);
                 mMainHttp.doRequest(sysRecRequest);
            }
        });
        list=(ListView) findViewById(R.id.special_data_list);
        special_close=(TextView) findViewById(R.id.special_close);
        special_close.setOnClickListener(this);
        specialAdapter=new SpecialAdapter();
        list.setAdapter(specialAdapter);
        list.setOnItemClickListener(this);
    }
    
    public void getData(){
        if(!isDialogShow){
        	SpecialRecListRequest request = new SpecialRecListRequest(HttpCommon.RECOMMEND_SPECIAL_LIST_ID, this).setParams();
            mMainHttp.doRequest(request);
//        	mVolleyHttp.getSpecialListData(GET_SPECIAL_LIST_DATA,this);
        }
        
    }
    public void showTopDialog() {
        int height;
        if(sData==null||sData.size()==0){
            return;
        }
        if(sData.size()==1){
            if(TextUtils.isEmpty(sData.get(0).getDescreption())){
                height=(int) DeviceUtil.dip2px(this.mcontext, 220);
            }else{
                height=(int) DeviceUtil.dip2px(this.mcontext, 270);
            }
        }else if(sData.size()==2){
            height=(int) DeviceUtil.dip2px(this.mcontext, 360);
            if(!TextUtils.isEmpty(sData.get(0).getDescreption())){
                height=(int) DeviceUtil.dip2px(this.mcontext, 50) + height;
            }
            if(!TextUtils.isEmpty(sData.get(1).getDescreption())){
                height=(int) DeviceUtil.dip2px(this.mcontext, 50) + height;
            }
        }else{
            height=(int) (Utils.getScreenHeight(this.mcontext)*0.77);
        }
        int width=(int) (Utils.getScreenWidth(this.mcontext)*0.85);
         //设置点击外围解散
         setCanceledOnTouchOutside(false);
         if(!isShowing()){
             show();
         }
         Window dialogWindow = getWindow();
         WindowManager.LayoutParams lp = dialogWindow.getAttributes();
         lp.width = width; // 宽度
         lp.height = height; // 高度
         dialogWindow.setAttributes(lp);
         isRecommend.setChecked(false);
         specialAdapter.notifyDataSetChanged();
     }
    public void setData(List<SpecialDialogData> sData){
        this.sData=sData;
        showTopDialog();
            
    }
    @Override
    public void onHttpResponse(IRequest _request) {
        int id=_request.getmId();
        switch (id) {
            case HttpCommon.RECOMMEND_SPECIAL_LIST_ID:
                List<SpecialDialogData> data = (List<SpecialDialogData>) _request.getResponse();
                if(data!=null&&data.size()>0){
                    setData(data);
                }
                break;
            case SUBSCRIBESPECIAL_SPECIAL:
                SouYueToast.makeText(mcontext,
                    R.string.subscribe__success, SouYueToast.LENGTH_SHORT).show();
                SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.KEY_UPDATE, true);
                //把那个数据的state变成已订阅
                if(sData!=null){
                    sData.get(position).setStatus("1");
                    specialAdapter.notifyDataSetChanged();
                }
                break;
            case HttpCommon.RECOMMEND_SPECIAL_SWITCH :
                UserInfoUtils.setSpecialState(!checked, user);
                break;
            default:
                break;
        }
     
    }
    @Override
    public void onHttpError(IRequest _request) {
        int id = _request.getmId();
        switch (id) {
            case SUBSCRIBESPECIAL_SPECIAL:
                
                break;

            default:
                break;
        }
    }
    @Override
    public void onHttpStart(IRequest _request) {
        
    }
    @Override
    public void onClick(View paramView) {
        this.dismiss();
    }
    
    private class SpecialAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if(sData!=null&&sData.size()>0){
                return sData.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if(sData!=null){
                return sData.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder=new ViewHolder();
                convertView = LayoutInflater.from(SpecialRecommendDialog.this.mcontext).inflate(R.layout.special_item_layout,parent, false);
                holder.special_image=(ImageView) convertView.findViewById(R.id.special_image);
                holder.special_tag_text= (TextView) convertView.findViewById(R.id.special_tag_text);
                holder.special_title=(TextView) convertView.findViewById(R.id.special_title);
                holder.special_subscribe_count=(TextView) convertView.findViewById(R.id.special_subscribe_count);
                holder.special_desc=(TextView) convertView.findViewById(R.id.special_desc);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            initData(holder,position);
            if(position == 0){
                convertView.findViewById(R.id.special_dialog_holder).setVisibility(View.GONE);
            }else{
                convertView.findViewById(R.id.special_dialog_holder).setVisibility(View.VISIBLE);
            }
            return convertView;
        }
        private class ViewHolder {
            ImageView special_image;
            TextView special_tag_text;
            TextView special_title;
            TextView special_subscribe_count;
            TextView special_desc;
            
        }
        private void initData(ViewHolder holder,final int position){
            if(sData!=null){
                final SpecialDialogData dialogData=sData.get(position);
                MyImageLoader.imageLoader.displayImage(dialogData.getPic(), holder.special_image,
                    MyImageLoader.ViewPageroptions);
                if(StringUtils.isNotEmpty(dialogData.getTag())){
                    holder.special_tag_text.setVisibility(View.VISIBLE);
                    holder.special_tag_text.setText(dialogData.getTag());
                }else{
                    holder.special_tag_text.setVisibility(View.GONE);
                }
                holder.special_title.setText(dialogData.getTitle());
                holder.special_subscribe_count.setText(dialogData.getSubscribeNum()+"人订阅");
                if(StringUtils.isNotEmpty(dialogData.getDescreption())) {
                	holder.special_desc.setVisibility(View.VISIBLE);
                	holder.special_desc.setText(dialogData.getDescreption());
                }else {
                	holder.special_desc.setVisibility(View.GONE);
//                	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                	lp.setMargins(0, 0, 0, 0);
//                	holder.special_desc.setLayoutParams(lp);
                }
                /*holder.special_desc.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View paramView) {
                        SearchResultItem item = new SearchResultItem();
                        item.url_$eq(dialogData.getFirst_content_url());
                        item.keyword_$eq(dialogData.getKeyword());
                        item.srpId_$eq(dialogData.getSrpid());
                        IntentUtil.startskipDetailPage(mcontext, item); 
                        if(sData.size()<=1){
                            dismiss();
                        }
                    }
                });*/
            }
           
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        SpecialDialogData data=sData.get(position);
        if(data!=null){
//            IntentUtil.gotoSrp(mcontext,data.getKeyword(),data.getSrpid());
            IntentUtil.gotoSpecilTopic(mcontext,data);
            if(sData.size()<=1){
                dismiss();
            }
            
        }
        
    }
    @Override
    public void dismiss() {
        /*if(receiver!=null){
            LocalBroadcastManager.getInstance(mcontext).unregisterReceiver(receiver);
        }*/
        isDialogShow=false;
        SYSharedPreferences.getInstance().putString(SYSharedPreferences.KEY_SHOW_TIMESTAMP_SPECIAL, SYUserManager.getInstance().getUser().userId()+","+System.currentTimeMillis());
        if(mVolleyHttp!=null)
            mVolleyHttp.cancelAll();
        super.dismiss();
    }
    
}
