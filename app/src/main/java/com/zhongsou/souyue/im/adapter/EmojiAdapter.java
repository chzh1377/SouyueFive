package com.zhongsou.souyue.im.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.emoji.Emoji;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.view.ExpressionView;

import java.util.List;

/**
 * emoji 界面布局展示
 * @author zwb
 *
 */
public class EmojiAdapter extends BaseAdapter {

    private List<Emoji> data;

    private LayoutInflater inflater;

    private int size=0;

    private Context mContext;
    private LinearLayout mFloatLayout;
    private ImageView mFloatView;
    private TextView mFloatText;
    private int popWidth;
    private int popHeight;
    private int emojiWidth;
    private int emojiHeight;
    private ExpressionView.OnExpressionListener mExpressionListener;

    public EmojiAdapter(Context context , List<Emoji> list) {
        this.mContext = context;
        if(mContext instanceof ExpressionView.OnExpressionListener){
            this.mExpressionListener = (ExpressionView.OnExpressionListener)mContext;
        }
        this.inflater=LayoutInflater.from(context);
        this.data=list;
        this.size=list.size();
        this.popWidth = DeviceUtil.dip2px(context,56);
        this.popHeight = DeviceUtil.dip2px(context,76);
        this.emojiWidth = DeviceUtil.dip2px(context,44);
        this.emojiHeight = DeviceUtil.dip2px(context,44);
    }


    /**
     * 主要处理从圈子那边点击表情过来的逻辑
     * @param context
     * @param expressionListener
     * @param list
     */
	public EmojiAdapter(Context context,ExpressionView.OnExpressionListener expressionListener, List<Emoji> list) {
        this(context,list);
        this.mExpressionListener = expressionListener;
    }

    @Override
    public int getCount() {
        return this.size;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Emoji emoji=data.get(position);
        ViewHolder viewHolder=null;
        if(convertView == null) {
            viewHolder=new ViewHolder();
            convertView=inflater.inflate(R.layout.emoji_gridview_item, null);
            viewHolder.iv_face=(ImageView)convertView.findViewById(R.id.imageview);
            convertView.setTag(viewHolder);
        } else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        if(emoji.getId() == R.drawable.btn_msg_facedelete_selector) {
            convertView.setBackgroundDrawable(null);
            	viewHolder.iv_face.setImageResource(emoji.getId());
        } else if(TextUtils.isEmpty(emoji.getCharacter())) {
            convertView.setBackgroundDrawable(null);
            viewHolder.iv_face.setImageDrawable(null);
        } else {
            viewHolder.iv_face.setTag(emoji);
            viewHolder.iv_face.setImageResource(emoji.getId());
        }
        if(emoji.getId()!=0){
            viewHolder.iv_face.setOnTouchListener(new MTouchListener(convertView,emoji));
        }
        return convertView;
    }

    class ViewHolder {

        public ImageView iv_face;
    }

        private PopupWindow createFloatView(View convertView,Emoji emoji)
        {
            mFloatLayout = (LinearLayout) inflater.inflate(R.layout.im_emoji_preview, null);
            PopupWindow popWindow= new PopupWindow(mFloatLayout,popWidth ,popHeight,true);

            int h[]=new int[2];
            convertView.getLocationInWindow(h);
            int offsetX=-(popWidth-emojiWidth)/2;
            while(h[0] + offsetX + popWidth - convertView.getRootView().getWidth() > 0){
                offsetX-=5;
//			u++;
//			System.out.println("符合条件了第"+u+"次");
            }
            int offsetY=-popHeight-emojiHeight;
//            System.out.println("popWidth = "+popWidth+"emojiWidth = "+emojiWidth+"popHeight = "+popHeight);
            mFloatView = (ImageView)mFloatLayout.findViewById(R.id.iv_emoji_preimg);
            mFloatText = (TextView)mFloatLayout.findViewById(R.id.tv_emoji_pretext);
            mFloatView.setImageResource(emoji.getId());
//            System.out.println("emoji.getCharacter() = "+emoji.getCharacter());
            if(!TextUtils.isEmpty(emoji.getCharacter())){
                mFloatText.setText(emoji.getCharacter().replace("[","").replace("]",""));
            }
            popWindow.setContentView(mFloatLayout);
//            popWindow.showAtLocation(convertView,Gravity.NO_GRAVITY,offsetX,offsetY);
            popWindow.showAsDropDown(convertView,offsetX,offsetY);
            return popWindow;
        }

    private class MTouchListener implements View.OnTouchListener{
        private View mConvertView;
        private Emoji mEmoji;
//        private int mx;
//        private int my;
//        private int mWidth;
//        private int mHeight;
        private PopupWindow popUpWindow;
        public MTouchListener(View converView,Emoji emoji){
            this.mConvertView = converView;
//            this.mx=x;
//            this.my=y;
            this.mEmoji = emoji;
        }
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int i[]=new int[2];
            mConvertView.getLocationOnScreen(i);
//            System.out.println("i[0] = "+i[0]+"i[1]"+i[1]);
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if(mEmoji.getId() != R.drawable.btn_msg_facedelete_selector){
                        popUpWindow=createFloatView(mConvertView,mEmoji);
//                        System.out.println("ACTION_DOWN ：show");
                    }
//                    System.out.println("ACTION_DOWN");
                    break;
//                case MotionEvent.ACTION_MOVE:
//                    System.out.println("event.getRawX() = "+event.getRawX()+"  event.getRawY() = "+event.getRawY());
//                    System.out.println("emojiWidth = "+emojiWidth+"emojiHeight"+emojiHeight);
//
//                    if((event.getRawX()<i[0] || event.getRawX()>i[0]+emojiWidth || event.getRawY()>i[1]+emojiHeight || event.getRawY()<i[0])){
//                        if(popUpWindow!=null&&popUpWindow.isShowing()){
//                            popUpWindow.dismiss();
//                        }
//                    }
//                    break;
                case MotionEvent.ACTION_UP:
                    if(popUpWindow!=null&&popUpWindow.isShowing()){
                        popUpWindow.dismiss();
//                        System.out.println("ACTION_UP ：dismiss");
                    }
                    if(mExpressionListener!=null){
                        mExpressionListener.emojiItemClick(mEmoji);
                    }
//                    System.out.println("ACTION_UP");
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if(popUpWindow!=null&&popUpWindow.isShowing()){
                        popUpWindow.dismiss();
//                        System.out.println("ACTION_CANCEL ：dismiss");
                    }
//                    System.out.println("ACTION_CANCEL");
                    break;
            }

//            System.out.println("this action = "+event.getAction());
            return true;
        }
    }
}