package com.zhongsou.souyue.im.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tuita.sdk.im.db.helper.PingYinUtil;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.SearchMsgResult;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.ac.IMChatActivity;
import com.zhongsou.souyue.im.ac.IMSearchItemHasMoreActivity;
import com.zhongsou.souyue.im.ac.IMSearchMoreActivity;
import com.zhongsou.souyue.im.search.ListResult;
import com.zhongsou.souyue.im.search.Session;
import com.zhongsou.souyue.im.util.ImUtils;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by x
 * 一二三级搜索界面适配器
 */
public class IMSearchAdapter extends BaseAdapter {
    //列表布局类型
    public static final int TYPE_ITEM_TOP = 0;		//顶部组名
    public static final int TYPE_ITEM = 1;		//数据内容
    public static final int TYPE_ITEM_BOTTOM = 2;	//底部更多

    private long mTestTime;     //测试速度

    private Context context;
//	private AQuery aq;
	private ListView mListView;
    private String keyWord = "";
    private int type ;
    private int page ;  //几级页面  1：一级页面 2：二级页面（点击bottom查看更多后跳转的页面）
    private ListResult listResult;
    protected LayoutInflater viewInflater;
    private int convertResultSize ;
	protected List<SearchMsgResult> datas = new ArrayList<SearchMsgResult>();
	BaseViewHolder baseHolder = null;
    private DisplayImageOptions options;

    public IMSearchAdapter(Context context, int type){
    	this.context = context;
        this.type = type;
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .displayer(new RoundedBitmapDisplayer(10))
                .showImageOnLoading(R.drawable.default_head).build();
    }

    public int getConvertResultSize() {
        return convertResultSize;
    }

    public void setConvertResultSize(int convertResultSize) {
        this.convertResultSize = convertResultSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<SearchMsgResult> getDatas(){
        return datas;
    }


    public ListResult getListResult() {
        return listResult;
    }

    public void setListResult(ListResult listResult) {
        this.listResult = listResult;
    }

    public String getKeyWord(){
        return keyWord;
    }

    public void setKeyWord(String keyWord){
        this.keyWord = keyWord;
    }

	@Override
	public int getCount() {
        return datas.size();
	}

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
	public long getItemId(int position) {
		return position;
	}

    public synchronized void addMore(List<SearchMsgResult> datas) {
        this.datas.addAll(datas);
        if (datas.size() > 0) {
            notifyDataSetChanged();
        }
    }

    public void clearData() {
        this.datas.clear();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        SearchMsgResult item = datas.get(position);
        return item.getLayoutType();
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        final SearchMsgResult item = datas.get(position);

		BaseViewHolder baseHolder = null;
        type = getItemViewType(position);
		if (convertView == null) {
			baseHolder = new BaseViewHolder(context);
            switch (type) {
                case TYPE_ITEM_TOP:
                    convertView = inflateView(R.layout.im_msg_search_item_header);
                    break;
                case TYPE_ITEM:
                    convertView = inflateView(R.layout.im_msg_search_item);
                    break;
                case TYPE_ITEM_BOTTOM:
                    convertView = inflateView(R.layout.im_msg_search_item_bottom);
                    break;
            }
            initViewHolder(convertView, baseHolder, type);
            convertView.setTag(baseHolder);

		} else {
            baseHolder = (BaseViewHolder) convertView.getTag();
		}

		setViewData(baseHolder, item, type, position);

		return convertView;
	}

    /**
	 * 获取view
	 */
	private void initViewHolder(View convertView, BaseViewHolder baseHolder, int layoutType) {

        switch (layoutType) {
            case TYPE_ITEM_TOP:
                baseHolder.im_search_top_item = (LinearLayout) convertView.findViewById(R.id.im_search_top_item);
                baseHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                baseHolder.tv_top_view = (TextView) convertView.findViewById(R.id.tv_top_view);
                break;
            case TYPE_ITEM:
                baseHolder.rl_search_item = (RelativeLayout) convertView.findViewById(R.id.rl_search_item);
                baseHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                baseHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                baseHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
                baseHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                break;
            case TYPE_ITEM_BOTTOM:
                baseHolder.rl_search_more = (RelativeLayout) convertView.findViewById(R.id.rl_search_more);
                break;
        }

	}

    /**
	 * view绑定数据
	 */
	private void setViewData(BaseViewHolder baseHolder, final SearchMsgResult item, int layoutType, final int position) {
        switch (layoutType) {
            case TYPE_ITEM_TOP:
                if(position==datas.size()-1){//容错处理，防止没数据还显示 top item
                    baseHolder.im_search_top_item.setVisibility(View.GONE);
                }else {
                    baseHolder.im_search_top_item.setVisibility(View.VISIBLE);
                    baseHolder.tv_name.setText(item.getGroupName());
                    if(position==0){//如果（联系人、群聊、聊天记录）条目在最上面，隐藏灰条
                        baseHolder.tv_top_view.setVisibility(View.GONE);
                    }else {
                        baseHolder.tv_top_view.setVisibility(View.VISIBLE);
                    }
                }

                break;

            case TYPE_ITEM:
                baseHolder.rl_search_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(item.getGroupType()==0){
                            IMChatActivity.invoke((Activity)context, IConst.CHAT_TYPE_PRIVATE, item.getChat_id());
                        }else if(item.getGroupType()==1){
                        	IMChatActivity.invoke((Activity)context, IConst.CHAT_TYPE_GROUP,item.getChat_id());
                        }else{
                            //聊天記錄
                            if(item.isHasMore()){//大於一條
                                Intent intent = new Intent();
                                int type = item.getGroupType();
                                Bundle bundle = new Bundle();
                                bundle.putInt("groupType", type);
                                bundle.putInt("historyType", item.getHistoryType());
                                bundle.putSerializable("listResult", listResult);
                                if(page==1){//一级页面这么走
                                    Session session = listResult.getSessionList().get(position-getConvertResultSize()-1);
                                    bundle.putSerializable("session", session);
                                }else {//二级页面
                                    Session session = listResult.getSessionList().get(position);
                                    bundle.putSerializable("session", session);
                                }
                                bundle.putString("keyWord",getKeyWord());
                                bundle.putString("tittle",item.getTitle());
                                bundle.putString("image",item.getUserImage());
                                bundle.putLong("chatId",item.getChat_id());
                                bundle.putString("pageTittle",item.getTitle());
                                intent.putExtras(bundle);
                                intent.setClass(context, IMSearchItemHasMoreActivity.class);
                                context.startActivity(intent);
                            }
                            if (!item.isHasMore()){//只有一條跳會話頁并定位
                                if(item.getHistoryType()==0){
                                	IMChatActivity.invokeTarget((Activity) context, IConst.CHAT_TYPE_PRIVATE, item.getChat_id(), item.getMsgId());
                                }else if(item.getHistoryType()==1)
                                	IMChatActivity.invokeTarget((Activity) context, IConst.CHAT_TYPE_GROUP, item.getChat_id(), item.getMsgId());
                            }
                        }
                    }
                });
                String tittle = item.getTitle();
                //若为聊天记录，仅仅高亮聊天记录内容
                if(item.getGroupType()==2){
                    baseHolder.tv_title.setText(tittle);
                }else {
                    baseHolder.tv_title.setText(ImUtils.getHighlightText(tittle, PingYinUtil.converter2FirstSpell(tittle), getKeyWord()));
                }
                mTestTime = System.currentTimeMillis();
                String content = item.getContent();
                System.out.println("---->mTestTime---->position:"+position+"---1节点："+ (System.currentTimeMillis() - mTestTime) + "毫秒");
//                String localNick =  "";
                System.out.println("---->mTestTime---->position:"+position+"---1.5节点："+ (System.currentTimeMillis() - mTestTime) + "毫秒");
                //消息记录的搜索完全匹配，群聊私聊是忽略大小写
                if(StringUtils.isEmpty(content)){
                    baseHolder.tv_content.setVisibility(View.GONE);
                }else if(item.getGroupType()==2 && item.isHasMore()){
                    baseHolder.tv_content.setVisibility(View.VISIBLE);
                    baseHolder.tv_content.setText(content); //有多条消息，匹配文字不高亮
                }else {
                    baseHolder.tv_content.setVisibility(View.VISIBLE);
                    System.out.println("---->mTestTime---->position:"+position+"---2节点："+ (System.currentTimeMillis() - mTestTime) + "毫秒");
                    //如果文字过多只截取中间一部分内容高亮
//                    String mContent = ImUtils.getTextIndex(content, "", getKeyWord());
                    System.out.println("---->mTestTime---->position:"+position+"---3节点："+ (System.currentTimeMillis() - mTestTime) + "毫秒");
                    Spanned afterContent ;
                    String mContent = ImUtils.getTextIndex(content, "", getKeyWord());
                    String localNick =  PingYinUtil.converter2FirstSpell(mContent);
                    if(item.getGroupType()==0){//搜索出联系人,显示备注名,不区分大小写
                        if(mContent.toUpperCase().contains(getKeyWord().toUpperCase())||
                                localNick.toUpperCase().contains(getKeyWord().toUpperCase())){
                            baseHolder.tv_content.setVisibility(View.VISIBLE);
                            afterContent = ImUtils.getHighlightText("昵称：",mContent, localNick, getKeyWord());//联系人不区分大小写
                            baseHolder.tv_content.setText(afterContent);
                        }else {
                            baseHolder.tv_content.setVisibility(View.GONE);
                        }
                    }else {
//                        afterContent = ImUtils.getHighlightTextIgnore(mContent, "", getKeyWord());
                        afterContent = ImUtils.getHighlightText(mContent, "", getKeyWord());
                        baseHolder.tv_content.setText(afterContent);
                    }

                }
                if(page==3){
                    baseHolder.tv_time.setVisibility(View.VISIBLE);
                    baseHolder.tv_time.setText(StringUtils.convertDate(String.valueOf(item.getTime())));
                }

                if (TextUtils.isEmpty(item.getUserImage())) {
                    baseHolder.iv_image.setImageResource(R.drawable.default_head);
                } else {
                        ImageLoader.getInstance().displayImage(
                                item.getUserImage(),
                                baseHolder.iv_image,options);
                }

                break;

            case TYPE_ITEM_BOTTOM:
                baseHolder.rl_search_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        int type = item.getGroupType();
                        Bundle bundle = new Bundle();
                        bundle.putInt("groupType",type);
                        if (type==2){//聊天记录更多
                            bundle.putSerializable("listResult",listResult);
                        }
                        bundle.putInt("historyType", item.getHistoryType());
                        bundle.putString("keyWord",getKeyWord());
                        bundle.putString("pageTittle","查看更多结果");
                        intent.putExtras(bundle);
                        intent.setClass(context, IMSearchMoreActivity.class);
                        context.startActivity(intent);
                    }
                });
                break;
        }
	}

	protected View inflateView(int id) {
		LayoutInflater viewInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return viewInflater.inflate(id, null);
	}

	public static class BaseViewHolder {
		public Context context;
		BaseViewHolder(Context context) {
			this.context = context;
		}
        public  LinearLayout im_search_top_item;
        public RelativeLayout rl_search_item;
        public RelativeLayout rl_search_more;
        public TextView tv_top_view ;
        public TextView tv_name ;
		public TextView tv_title;
        public TextView tv_time;
		public TextView tv_content;
		public ImageView iv_image;
	}

	public void setListView(ListView listView) {
		this.mListView = listView;
	}
}
