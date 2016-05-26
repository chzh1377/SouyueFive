package com.zhongsou.souyue.circle.adapter;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.activity.NewSignatureActivity;
import com.zhongsou.souyue.circle.model.SignatureBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2014/11/4.
 */
public class CircleSignatureAdapter extends BaseAdapter{
    private List<SignatureBean> signatureArr;
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Integer> focusface;
    private SimpleDateFormat format;
    private SimpleDateFormat format1;
    private boolean isSelf;
    private Builder builder;
    public int delPos;
    public CircleSignatureAdapter(Context context,ArrayList<Integer> focusface,boolean isSelf){
    	this.context = context;
    	this.focusface = focusface;
    	this.signatureArr = new ArrayList<SignatureBean>();
    	inflater = LayoutInflater.from(context);
    	format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    	format1 = new SimpleDateFormat("MM-dd hh:mm");
    	this.isSelf = isSelf;
    	
    	builder = new Builder(context);
		builder.setTitle("删除提示");
		builder.setMessage("确定删除该签名？");
    }
    public CircleSignatureAdapter(Context context,List<SignatureBean> signatureArr,ArrayList<Integer> focusface){
        this.signatureArr = signatureArr;
        this.context = context;
        this.focusface = focusface;
        inflater = LayoutInflater.from(context);
    }

    public void addSignatureArr(List<SignatureBean> signatureArr){
    	this.signatureArr.addAll(signatureArr);
    }
    
    public List<SignatureBean> getSignatureArr() {
		return signatureArr;
	}
	public void setSignatureArr(List<SignatureBean> signatureArr) {
		this.signatureArr = signatureArr;
	}
	
	public ArrayList<Integer> getFocusface() {
		return focusface;
	}
	public void setFocusface(ArrayList<Integer> focusface) {
		this.focusface = focusface;
	}
	@Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return signatureArr.size();
    }

    @Override
    public Object getItem(int position) {
        return signatureArr.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.circle_signature_history_item,null);
            vh = new ViewHolder();
            vh.signature = (TextView)convertView.findViewById(R.id.circle_signature_text);
            vh.createTime = (TextView) convertView.findViewById(R.id.time_text);
            vh.delMood = (ImageView) convertView.findViewById(R.id.del_icon);
            vh.left_top = (LinearLayout)convertView.findViewById(R.id.left_top);
            vh.left_center = (LinearLayout)convertView.findViewById(R.id.left_center);
            vh.left_bottom = (LinearLayout)convertView.findViewById(R.id.left_bottom);
            vh.center_line = (View)convertView.findViewById(R.id.center_line);
            vh.face_top_icon = (ImageView) convertView.findViewById(R.id.face_top_icon);
            vh.face_center_icon = (ImageView) convertView.findViewById(R.id.face_center_icon);
            vh.face_bottom_icon = (ImageView) convertView.findViewById(R.id.face_bottom_icon);
            vh.face_top_back = (RelativeLayout) convertView.findViewById(R.id.face_top_back);
            vh.face_center_back = (RelativeLayout) convertView.findViewById(R.id.face_center_back);
            vh.face_bottom_back = (RelativeLayout) convertView.findViewById(R.id.face_bottom_back);
            vh.circle_d_bottom = (ImageView) convertView.findViewById(R.id.circle_d_bottom);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder)convertView.getTag();
        }
        if(position == 0){
        	if(getCount()==1){
        		vh.center_line.setVisibility(View.INVISIBLE);
        		vh.face_top_back.setBackgroundResource(R.drawable.circle_no_cut);
        		vh.circle_d_bottom.setVisibility(View.GONE);
        	}else{
        		vh.center_line.setVisibility(View.VISIBLE);
        	}
        	vh.left_top.setVisibility(View.VISIBLE);
        	vh.left_center.setVisibility(View.GONE);
        	vh.left_bottom.setVisibility(View.GONE);
        	vh.face_top_icon.setImageResource(focusface.get(signatureArr.get(position).getMood_id()));
        }else if(position==getCount()-1){
        	vh.left_top.setVisibility(View.GONE);
        	vh.left_center.setVisibility(View.GONE);
        	vh.left_bottom.setVisibility(View.VISIBLE);
        	vh.center_line.setVisibility(View.INVISIBLE);
        	vh.face_bottom_icon.setImageResource(focusface.get(signatureArr.get(position).getMood_id()));
        	
        }else{
        	vh.left_top.setVisibility(View.GONE);
        	vh.left_center.setVisibility(View.VISIBLE);
        	vh.left_bottom.setVisibility(View.GONE);
        	vh.center_line.setVisibility(View.VISIBLE);
        	vh.face_center_icon.setImageResource(focusface.get(signatureArr.get(position).getMood_id()));
        }
        vh.signature.setText(signatureArr.get(position).getSignature());
        try {
			vh.createTime.setText(format1.format(format.parse(signatureArr.get(position).getCreate_time())));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if(isSelf){
        	vh.delMood.setOnClickListener(new OnClickListener() {
        		
        		@Override
        		public void onClick(View v) {

        			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

        				@Override
        				public void onClick(DialogInterface dialog, int which) {
        					((NewSignatureActivity)context).delMood(signatureArr.get(position).getId()+"");
        					delPos = position;
//                			signatureArr.remove(position);//删除操作挪到他处
        				}
        			});
        			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

        				@Override
        				public void onClick(DialogInterface dialog, int which) {
        				}
        			});
        			builder.create().show();
        			
//				Toast.makeText(context, signatureArr.get(position).getSignature(), Toast.LENGTH_LONG).show();
        		}
        	});
        }else{
        	vh.delMood.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder{
        TextView signature;
        TextView createTime;
        ImageView delMood;
        LinearLayout left_top;
        LinearLayout left_center;
        LinearLayout left_bottom;
        ImageView face_top_icon,face_center_icon,face_bottom_icon,circle_d_bottom;
        RelativeLayout face_top_back,face_center_back,face_bottom_back;
        View center_line;
    }
}
