package com.zhongsou.souyue.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.MapAdapter;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.module.PackageInfoMap;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by wangqiang on 15/12/25.
 */
public class MapPopuWindow extends PopupWindow implements AdapterView.OnItemClickListener,View.OnClickListener{
    private View mainview;
    private ListView listView;
    private List<PackageInfoMap> maps;
    private Context mContext;
    private JSClick jsc;
    private View tvCancel;

    public MapPopuWindow(Activity context,List<PackageInfoMap> maps,JSClick jsc){
        super(context);
        this.maps= maps;
        this.mContext = context;
        this.jsc = jsc;
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        mainview=inflater.inflate(R.layout.map_popupwindow, null);
        this.tvCancel = mainview.findViewById(R.id.tv_cancel);
        this.setBackgroundDrawable(new ColorDrawable(0x55000000));
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setContentView(mainview);
        this.listView = (ListView)mainview.findViewById(R.id.lv_map);
        this.setFocusable(true);
        setData();
        this.listView.setOnItemClickListener(this);
        this.tvCancel.setOnClickListener(this);


        //this.setAnimationStyle(R.style.activityStyle);
      //  this.setAnimationStyle(R.style.AnimBottom);
    }

    public void setData(){
        MapAdapter adapter = new MapAdapter(mContext,maps);
        listView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           startup(maps.get(position));
    }

    @Override
    public void onClick(View v) {
        this.dismiss();
    }

    private void startup(PackageInfoMap infoMap){
        String dat;
        Intent intent = null;

        if(infoMap.getPackageName().startsWith("com.autonavi.minimap.custom")){
            dat = "androidamap://route?sourceApplication=softname&slat=" + jsc.getSlat() + "&slon=" + jsc.getSlng() + "&sname=" + jsc.getSname() + "&dlat=" + jsc.getDlat() + "&dlon=" + jsc.getDlng() + "&dname=" + jsc.getDname() + "&dev=" + jsc.getDev() + "&m=" + jsc.getM() + "&t=" + jsc.getType();
            intent = new Intent("android.intent.action.VIEW",
                    android.net.Uri.parse(dat));
            intent.setPackage(infoMap.getPackageName());
            mContext.startActivity(intent);
        }else if("com.baidu.BaiduMap".equals(infoMap.getPackageName())){
            try {
                dat= "intent://map/direction?origin=latlng:"+jsc.getSlat()+","+jsc.getSlng()+"|name:"+jsc.getSname()+"&destination=latlng:"+jsc.getDlat()+","+jsc.getDlng()+"|name:"+jsc.getDname()+
                        "&mode=driving"+"&region="+jsc.getRegion()+"&src=souyue#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";
                intent = Intent.getIntent(dat);
            } catch (URISyntaxException e) {
                Log.e("intent", e.getMessage());
            }
            mContext.startActivity(intent); //启动调用
        }
    }
}