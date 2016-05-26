package com.zhongsou.souyue.adapter.firstleader;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.ZSImageOptions;
import com.facebook.drawee.view.ZSImageView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.firstleader.CharacterList;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zyw on 2016/3/23.
 */
public class LeaderPageTwoAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
    public static final String TAG = LeaderPageTwoAdapter.class.getSimpleName();
    private final Context                     mCtx;
    private       OnCharacterSelectedListener mListener;
    private List<CharacterList> mDatas = new ArrayList<CharacterList>();
    private final LayoutInflater mInflater;
    private       Handler        mHandler;

    public LeaderPageTwoAdapter(Context context, List<CharacterList> datas) {
        this.mCtx = context;
        this.mDatas.addAll(datas);
        mInflater = LayoutInflater.from(context);
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void setOnCharacterSelectedListener(OnCharacterSelectedListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public CharacterList getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.home_first_leader_pagetow_griditem, null);
            viewHolder = new Holder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Holder) convertView.getTag();
        }
        initData(position, viewHolder);
        return convertView;
    }

    private void initData(int position, Holder viewHolder) {
        CharacterList item = getItem(position);
        viewHolder.getTv().setText(item.getName());
        viewHolder.imageView.setImageURI(Uri.parse(item.getImage()), ZSImageOptions.getLocalCircleConfig(mCtx), null);
        viewHolder.getSelected().setVisibility(position == LAST_SELECTED_POS  ? View.VISIBLE : View.INVISIBLE);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LAST_SELECTED_POS = position;
        notifyDataSetChanged();
        if (mListener != null) {
            mListener.onCharacterSelected(getItem(position));
        }
    }

    public static int LAST_SELECTED_POS = -1;

    public void reshowImage() {
        if (mListener != null && LAST_SELECTED_POS != -1) {
            mListener.onCharacterSelected(getItem(LAST_SELECTED_POS));
            notifyDataSetChanged();
        }
    }

    static class Holder {
        View        selected;
        TextView    tv;
        ZSImageView imageView;

        public View getSelected() {
            return selected;
        }

        public Holder(View convertView) {
            this.tv = (TextView) convertView.findViewById(R.id.text);
            this.imageView = (ZSImageView) convertView.findViewById(R.id.image);
            this.selected = convertView.findViewById(R.id.home_leader_pagetwo_selected);
        }

        public ZSImageView getImageView() {
            return imageView;
        }

        public TextView getTv() {
            return tv;
        }
    }

    public interface OnCharacterSelectedListener {
        void onCharacterSelected(CharacterList character);
    }
}
