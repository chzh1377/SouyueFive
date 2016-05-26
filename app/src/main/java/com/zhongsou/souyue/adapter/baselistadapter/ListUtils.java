package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.StringUtil;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.FootItemBean;
import com.zhongsou.souyue.module.listmodule.TitleIconBean;
import com.zhongsou.souyue.utils.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * @description: ListView 工具类，获取视图类型，数量，和渲染器类型
 * @auther: qubian
 * @data: 2015/12/22.
 */

public class ListUtils {

    private static final int ItemTypeCount= 11;

    /**
     *  根据获取的数据，返回渲染器类型
     * @param type
     * @return
     */
    public static int getItemViewType(Object type)
    {
        BaseListData data = (BaseListData) type;
        switch (data.getViewType())
        {
            case BaseListData.view_Type_default:
                return ListTypeRender.TYPE_DEFAULT;
            case BaseListData.view_Type_img_1:
            case BaseListData.view_Type_video_one_Img:
                return ListTypeRender.TYPE_ONE_IMAGE;
            case BaseListData.view_Type_img_3:
                return ListTypeRender.TYPE_THREE_IMAGE;
            case BaseListData.view_Type_SPECIA:
                return ListTypeRender.TYPE_SPECIAL;
            case BaseListData.view_Type_img_f:
                return ListTypeRender.TYPE_FOCUS;
            case BaseListData.view_Type_img_b:
                return ListTypeRender.TYPE_BIGIMAGE;
            case BaseListData.view_Type_JOKE:
                return ListTypeRender.TYPE_JOKE;
            case BaseListData.view_Type_video_0:
                return ListTypeRender.TYPE_VIDEO;
            case BaseListData.VIEW_TYPE_IMG_CAROUSEL :
                return ListTypeRender.TYPE_BANNER;
            case BaseListData.view_Type_CLICK_REFRESH:
                return ListTypeRender.TYPE_FRESH;
            case BaseListData.view_Type_SEARCH_RESULT:
                return ListTypeRender.TYPE_SEARCH_RESULT;
            default:
                return ListTypeRender.TYPE_DEFAULT;
        }
    }

    /**
     * 获取底部视图 类型
     * @param type
     * @return
     */
    public static int getBottomViewType(Object type)
    {
        FootItemBean data = ((BaseListData)type).getFootView();
        if (data == null){
            return BottomViewRender.BottomViewDEFAULT;
        }
        switch (data.getFootType())
        {
            case FootItemBean.FOOT_VIEW_TYPE_DEFAULT:
                return BottomViewRender.BottomViewTYPE1;
            case FootItemBean.FOOT_VIEW_TYPE_2:
                return BottomViewRender.BottomViewTYPE2;
            case FootItemBean.FOOT_VIEW_TYPE_3:
                return BottomViewRender.BottomViewTYPE3;
            case FootItemBean.FOOT_VIEW_TYPE_4:
                return BottomViewRender.BottomViewTYPE4;
            case FootItemBean.FOOT_VIEW_TYPE_5:
                return BottomViewRender.BottomViewTYPE5;
            default:
                return BottomViewRender.BottomViewDEFAULT;
        }
    }

    /**
     *  类型数量
     * @return
     */
    public static int getItemTypeCount() {
        return ItemTypeCount;
    }

    /**
     *  根据渲染器类型获取 对应的渲染器
     * @param context
     * @param itemType
     * @param adapter
     * @return
     */
    public static BaseListTypeRender getItemTypeRender(
            Context context,final int itemType,int bottomType, BaseListViewAdapter adapter)
    {
        BaseListTypeRender typeRender = null;
        switch (itemType)
        {
            case ListTypeRender.TYPE_DEFAULT:
                typeRender =new DefaultRender(context,itemType,bottomType,adapter);
                break;
            case ListTypeRender.TYPE_FOCUS:
                typeRender = new FocusRender(context,itemType,bottomType,adapter);
                break;
            case ListTypeRender.TYPE_ONE_IMAGE:
                typeRender = new OneImageRender(context,itemType,bottomType,adapter);
                break;
            case ListTypeRender.TYPE_THREE_IMAGE:
                typeRender = new ThreeImageRender(context,itemType,bottomType,adapter);
                break;
            case ListTypeRender.TYPE_SPECIAL:
                typeRender = new SpecialRender(context,itemType,bottomType,adapter);
                break;
            case ListTypeRender.TYPE_BIGIMAGE:
                typeRender = new BigImageRender(context,itemType,bottomType,adapter);
                break;
            case ListTypeRender.TYPE_JOKE:
                typeRender = new JokeRender(context,itemType,bottomType,adapter);
                break;
            case ListTypeRender.TYPE_VIDEO:
                typeRender = new VideoBiliRender(context,itemType,bottomType,adapter);
//                typeRender = new VitamioRender(context,itemType,bottomType,adapter);
                break;
            case ListTypeRender.TYPE_BANNER:
                typeRender = new BannerRender(context,itemType,bottomType,adapter);
                break;
            case ListTypeRender.TYPE_FRESH:
                typeRender = new FreshRender(context,itemType,bottomType,adapter);
                break;
            case ListTypeRender.TYPE_SEARCH_RESULT:
                typeRender = new SearchResultRender(context,itemType,bottomType,adapter);
                break;
            default:
                typeRender =new DefaultRender(context,itemType,bottomType,adapter);
                break;
        }
        return typeRender;
    }

    public static BaseBottomViewRender getBottomView(Context mContext,int bottomType, BaseListViewAdapter mAdaper)
    {
        BottomViewRender bottomViewRender=null;
        // 底部可以没有数据 default
        switch (bottomType)
        {
            case BottomViewRender.BottomViewDEFAULT:
                return null;
            case  BottomViewRender.BottomViewTYPE1:
                bottomViewRender = new BottomViewRender1(mContext,bottomType,mAdaper);
                break;
            case BottomViewRender.BottomViewTYPE2:
                bottomViewRender = new BottomViewRender2(mContext,bottomType,mAdaper);
                break;
            case BottomViewRender.BottomViewTYPE3:
                bottomViewRender = new BottomViewRender3(mContext,bottomType,mAdaper);
                break;
            case BottomViewRender.BottomViewTYPE4:
                bottomViewRender = new BottomViewRender4(mContext,bottomType,mAdaper);
                break;
            case  BottomViewRender.BottomViewTYPE5:
                bottomViewRender = new BottomViewRender5(mContext,bottomType,mAdaper);
                break;
            default:
                bottomViewRender = new BottomViewRender1(mContext,bottomType,mAdaper);
                break;
        }
        return bottomViewRender;
    }
    /**
     * 计算标题所在位置
     * @param list
     * @param title
     * @return
     */
    public static String calcTitle(Context context ,ArrayList<TitleIconBean> list, String title)
    {
        StringBuffer stringBuffer = new StringBuffer();
        int count=0;
        for (TitleIconBean info:list)
        {
            count +=info.getWord().length();
        }
        String s =context.getString(R.string.space);
        for(int i =0 ;i<count;i++)
        {
            stringBuffer.append(s);
            if(i/2 ==0)
            {
                stringBuffer.append(s);
            }
        }
        stringBuffer.append(title);
        return  stringBuffer.toString();
    }
    public static String calcTitle(Context context , Map<String ,String> map, String title)
    {
        if(map ==null)
        {
            return  title;
        }
        StringBuffer stringBuffer = new StringBuffer();
        if (map !=null) {
            String s = context.getString(R.string.space);
            int count = 0;
            for (String key : map.keySet()) {
                count += key.length();
            }
            for (int i = 0; i < count; i++) {
                stringBuffer.append(s);
                if (i / 2 == 0) {
                    stringBuffer.append(s);
                }
            }
        }
        stringBuffer.append(title);
        return  stringBuffer.toString();
    }

    /**
     * 设置文本显示状态， -1 不显示，0 显示 后边文字,后边文字为空，不显示，其他显示数字
     * @param textView
     * @param tag
     * @param StringID
     */
    public static void setViewStatus(TextView textView, int tag, String StringID)
    {
        StringBuilder sb=new StringBuilder();
        if(tag ==BottomViewRender.VIEWFOTINVISIBLE)
        {
            textView.setVisibility(View.GONE);
        }else  if(tag ==BottomViewRender.BottomViewDEFAULT)
        {
            if(StringUtils.isEmpty(StringID))
            {
                textView.setVisibility(View.GONE);
            }else
            {
                textView.setVisibility(View.VISIBLE);
                sb.append(StringID);
                textView.setText(sb.toString());
            }
        }else
        {
            textView.setVisibility(View.VISIBLE);
            int len =4-String.valueOf(tag).length();
            if(len<0)
            {
                DecimalFormat df=null;
                double c = tag/10000.0;
                String result;
                if(len==-1)//1.2w
                {
                    df= new DecimalFormat("#.#");
                    result=df.format(c);
                    sb.append(result);
                    sb.append("w");
                }else if(len==-2)//12w
                {
                    df= new DecimalFormat("##");
                    result=df.format(c);
                    sb.append(result);
                    sb.append("w");
                }else
                {
                    result="百万" ;
                    sb.append(result);
                }

            }else
            {
                sb.append(tag);
            }
            textView.setText(sb.toString());
        }
    }

    /**
     * 显示多少张图
     * @param view
     * @param count
     */
    public static void setTextViewForImageCount(TextView view,int count)
    {
        if(count ==BottomViewRender.VIEWFOTINVISIBLE ||
                count ==BottomViewRender.BottomViewDEFAULT||
                count ==BottomViewRender.BottomViewTYPE1)
        {
            view.setVisibility(View.GONE);
        }else
        {
            view.setVisibility(View.VISIBLE);
            view.setText(count+"张图");
        }
    }
    /**
     *  设置 图片，VIew 状态，为0  -1 不显示
     * @param view
     * @param tag
     */
    public static void setViewStatusForGone(View view, int tag)
    {
        if(tag ==BottomViewRender.VIEWFOTINVISIBLE||tag ==BottomViewRender.BottomViewDEFAULT)
        {
            view.setVisibility(View.GONE);
        }else
        {
            view.setVisibility(View.VISIBLE);
        }
    }
    /**
     *  设置 图片，VIew 状态，为0  -1 不显示
     * @param view
     * @param tag
     */
    public static void setViewForGone(View view, int tag)
    {
        if(tag ==BottomViewRender.VIEWFOTINVISIBLE)
        {
            view.setVisibility(View.GONE);
        }else
        {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 点赞和评论，0 -1 不显示 否则显示数字
     * @param view
     * @param tag
     */
    public static void setViewStatusForText(TextView view, int tag)
    {
        if(tag ==BottomViewRender.VIEWFOTINVISIBLE||tag ==BottomViewRender.BottomViewDEFAULT)
        {
            view.setVisibility(View.GONE);
        }else
        {
            view.setVisibility(View.VISIBLE);
            view.setText(tag+"");
        }
    }
    /**
     * 设置文字，为空 不显示
     * @param textView
     * @param text
     */
    public static void setViewString(TextView textView,String text)
    {
        if(StringUtils.isEmpty(text))
        {
            textView.setVisibility(View.GONE);
        }else
        {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        }
    }

    /**
     * 设置显示时间，为0  -1 不显示
     * @param textView
     * @param text
     */
    public static void setViewTime(TextView textView,long text)
    {
        if(text ==BottomViewRender.VIEWFOTINVISIBLE || text ==BottomViewRender.BottomViewDEFAULT)
        {
            textView.setVisibility(View.GONE);
        }else
        {
            textView.setVisibility(View.VISIBLE);
            textView.setText(StringUtils.computingTime(text));
        }
    }
}
