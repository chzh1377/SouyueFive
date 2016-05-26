package com.zhongsou.souyue.im.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.tuita.sdk.im.db.module.SearchMsgResult;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class IMSearchMoreAdapter extends BaseAdapter {
    private Context context;
//	private AQuery aq;
	private ListView mListView;
    private int type ;
    protected LayoutInflater viewInflater;
	protected List<SearchMsgResult> datas = new ArrayList<SearchMsgResult>();

	BaseViewHolder baseHolder = null;

    public IMSearchMoreAdapter(Context context, int type){
    	this.context = context;
        this.type = type;
    }

    public List<SearchMsgResult> getDatas(){
        return datas;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        final SearchMsgResult item = datas.get(position);

		BaseViewHolder baseHolder = null;
        type = getItemViewType(position);
		if (convertView == null) {
			baseHolder = new BaseViewHolder(context);
            convertView = inflateView(R.layout.im_msg_search_item);
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

         baseHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
         baseHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
         baseHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);

	}

    /**
	 * view绑定数据
	 */
	private void setViewData(BaseViewHolder baseHolder, SearchMsgResult item, int layoutType, int position) {
                String tittle = item.getTitle();
                baseHolder.tv_title.setText(tittle);
                String content = item.getContent();
                if(StringUtils.isEmpty(content)){
                    baseHolder.tv_content.setVisibility(View.GONE);
                }else{
                    baseHolder.tv_content.setText(content);
                }

                if (TextUtils.isEmpty(item.getUserImage())) {
                    baseHolder.iv_image.setImageResource(R.drawable.default_head);
                } else {
                    //aq.id(baseHolder.iv_image).image(item.getUserImage(), true, true,0,R.drawable.default_head);
                    PhotoUtils.showCard(PhotoUtils.UriType.HTTP,item.getUserImage(),baseHolder.iv_image, MyDisplayImageOption.options);
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
        public TextView tv_name ;
		public TextView tv_title;
		public TextView tv_content;
		public ImageView iv_image;
	}

	public void setListView(ListView listView) {
		this.mListView = listView;
	}
}
