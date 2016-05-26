package com.zhongsou.souyue.circle.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.download.DownloadDao;
import com.zhongsou.souyue.download.DownloadFileServiceV2;
import com.zhongsou.souyue.download.DownloadInfo;
import com.zhongsou.souyue.download.UrlConsume;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.utils.SYUserManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ValidFragment")
public class DownloadContentPagerFragmentV2 extends Fragment {


    public static final String BROADCAST_REFRESH_LIST =  "com.zhongsou.souyue.download.refresh_list";
    public static final String BROADCAST_DOWNLOAD_FILE = "com.zhongsou.souyue.download.update_ui";

    public static final int TYPE_DOWNLOADING = 0;
    public static final int TYPE_DOWNLOADED = 1;

    private Context context;
    private int fragmentType = 0; // 0下载中，1已缓存
    private int fileType;   // 与DownloadInfo中type保持一直
    private int from; //1是来自下载页面，2是来自我的离线

    private View rootView;
    private ListView contentListView;
    private DownloadListAdapter adapter;
    public  ArrayList<DownloadInfo> downloadData;

    private LinearLayout layout_pause;
    private LinearLayout layout_start;

    // 存放与下载器对应的进度条
    private Map<String, ProgressBar> progressBars = new HashMap<String, ProgressBar>();

    private UpdateReceiver receiver;

    public DownloadContentPagerFragmentV2() {}

    public DownloadContentPagerFragmentV2(int fragmentType, int fileType,int from) {
        this.fragmentType = fragmentType;
        this.fileType = fileType;
        this.from = from;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.circle_dowload_content, null);
        initView();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getActivity();

        downloadData = new ArrayList<DownloadInfo>();
        adapter = new DownloadListAdapter(context,downloadData);
        contentListView.setAdapter(adapter);

        loadData();
        if(fragmentType == TYPE_DOWNLOADED) {
            registerBroadcast();
        }
        if(fragmentType == TYPE_DOWNLOADING) {
            receiver = new UpdateReceiver();

            IntentFilter filter = new IntentFilter();
            filter.addAction(BROADCAST_DOWNLOAD_FILE);
            context.registerReceiver(receiver, filter);

            DownloadFileServiceV2.addItemsToQueue(context, downloadData);
        }
    }

    private void initView() {
        downloadData = new ArrayList<DownloadInfo>();
        contentListView = (ListView) rootView.findViewById(R.id.lv_circle_download_content);

        layout_pause = (LinearLayout) rootView.findViewById(R.id.ll_all_pause);
        layout_start = (LinearLayout) rootView.findViewById(R.id.ll_all_start);
        layout_pause.setVisibility(View.GONE);
        layout_start.setVisibility(View.GONE);

        if(fragmentType == TYPE_DOWNLOADING) {
            layout_start.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 全部开始下载
                    layout_start.setVisibility(View.GONE);
                    layout_pause.setVisibility(View.VISIBLE);
                    for(DownloadInfo item : downloadData) {
                        if(item.getState() == DownloadInfo.STATE_PAUSE || item.getState() == DownloadInfo.STATE_FAILED) {
                            item.setState(DownloadInfo.STATE_INIT);
                            DownloadDao.getInstance(context).updataState(item.getOnlyId(), item.getState());
                        }
                    }
                    DownloadFileServiceV2.addItemsToQueue(context, downloadData);
                    adapter.notifyDataSetChanged();
                }
            });

            layout_pause.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    //全部暂停下载
                    layout_start.setVisibility(View.VISIBLE);
                    layout_pause.setVisibility(View.GONE);

                    for(DownloadInfo item : downloadData) {
                        if(item.getState() != DownloadInfo.STATE_FAILED && item.getState() != DownloadInfo.STATE_COMPLETE) {
                            item.setState(DownloadInfo.STATE_PAUSE);
                            DownloadDao.getInstance(context).updataState(item.getOnlyId(), item.getState());
                        }
                    }
                    DownloadFileServiceV2.pauseQueueItems(context, downloadData);
                    DownloadFileServiceV2.stopThread(context);
                    adapter.notifyDataSetChanged();
                }
            });
        } else {
            rootView.findViewById(R.id.layout_option).setVisibility(View.GONE);
        }

    }

    // 加载数据库下载的文件数据
    private void loadData() {
        if(fragmentType == TYPE_DOWNLOADING) {
            downloadData.addAll(DownloadDao.getInstance(context).getDownloadingInfos(fileType));
			if (from == 1) {
				layout_start.setVisibility(View.GONE);
				layout_pause.setVisibility(View.VISIBLE);
				int len = downloadData.size();
				if (len == 0) {
					layout_start.setVisibility(View.GONE);
					layout_pause.setVisibility(View.GONE);
				}
			} else {
				int len = downloadData.size();
				int count = 0;
				if (len != 0) {
					for (DownloadInfo data : downloadData) {
						if (data.getState() == DownloadInfo.STATE_LOADING) {
							count++;
						}
					}
					if (count == 0) {
						layout_start.setVisibility(View.VISIBLE);
						layout_pause.setVisibility(View.GONE);
					} else {
						layout_start.setVisibility(View.GONE);
						layout_pause.setVisibility(View.VISIBLE);
					}

				}
			}
        } else if(fragmentType == TYPE_DOWNLOADED) {
        	downloadData.clear();
            downloadData.addAll(DownloadDao.getInstance(context).getHasDownloadInfos(fileType));
        }
        adapter.notifyDataSetChanged();
    }

    // 已缓存列表注册广播
    private void registerBroadcast() {
        if(fragmentType == TYPE_DOWNLOADING) {
            return;
        }
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BROADCAST_REFRESH_LIST);
		BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){
                String action = intent.getAction();
                if (action != null && BROADCAST_REFRESH_LIST.equals(action)) {
                    loadData();
                }
            }
		 };
		 broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    private class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            int what = bundle.getInt("what");
            Object obj = bundle.get("obj");

            Message message = Message.obtain();
            message.what = what;
            message.obj = obj;
            mHandler.sendMessage(message);
        }
    }

    /**
     * 利用消息处理机制适时更新进度条
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                DownloadInfo item = (DownloadInfo) msg.obj;
                updateProgress(item);
            } else if (msg.what == 2) {
                DownloadInfo item = (DownloadInfo) msg.obj;
                updateStateChange(item);
            }
        }
    };

    private void updateProgress(final DownloadInfo item) {
        final String onlyId = item.getOnlyId();
        ProgressBar bar = progressBars.get(onlyId);
        if (bar == null) {
            return;
        }
        bar.setProgress(item.getCurLength());
        RelativeLayout layout = (RelativeLayout) bar.getParent();
        TextView tv_downloaded = (TextView) layout.findViewById(R.id.tv_downloaded);
        tv_downloaded.setText(getLengthM(item.getCurLength()) + "M / " + getLengthM(item.getLength()) + "M");


        for(DownloadInfo info : downloadData) {
            if(onlyId.equals(info.getOnlyId())) {
                info.setState(item.getState());
                info.setCurLength(item.getCurLength());
                info.setCompeleteSize(item.getCompeleteSize());
                info.setCurUrl(item.getCurUrl());
                break;
            }
        }


        // 下载完成
        if (item.getState() == DownloadInfo.STATE_COMPLETE) {
            progressBars.remove(onlyId);

            TextView tv_file_tittle = (TextView) layout.findViewById(R.id.tv_file_tittle);
            Toast.makeText(getActivity(), "[" + tv_file_tittle.getText() + "]下载完成！", Toast.LENGTH_SHORT).show();

            // 更新UI
            for (DownloadInfo info : downloadData) {
                if (onlyId.equals(info.getOnlyId())) {
                    downloadData.remove(info);
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
            if(downloadData.size() == 0){
            	layout_pause.setVisibility(View.GONE);
            	layout_start.setVisibility(View.GONE);
            	return;
            }
        }
    }

    private void updateStateChange(DownloadInfo item) {
        // 更新UI
        String onlyId = item.getOnlyId();
        for(DownloadInfo info : downloadData) {
            if(onlyId.equals(info.getOnlyId())) {
                info.setState(item.getState());
                adapter.notifyDataSetChanged();
                break;
            }
        }

        if(item.getState() == DownloadInfo.STATE_COMPLETE) {
            item.setCurLength(item.getLength());
            updateProgress(item);
        }
    }


    class DownloadListAdapter extends BaseAdapter{
        private LayoutInflater mInflater;
        private List<DownloadInfo> data;
        private DisplayImageOptions // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
                options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.book_default_img) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.book_default_img) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.book_default_img) // 设置图片加载或解码过程中发生错误显示的图片
                .showImageOnLoading(R.drawable.book_default_img).cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(10)) // 设置成圆角图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build(); // 创建配置过的DisplayImageOpti
        public DownloadListAdapter (Context context,List<DownloadInfo> data) {
            mInflater = LayoutInflater.from(context);
            this.data=data;
        }

        public void refresh(List<DownloadInfo> data) {
            this.data=data;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
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
            final DownloadInfo fileInfo = data.get(position);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.new_download_list_item, null);
                holder = new ViewHolder();

                holder.resouceName = (TextView) convertView.findViewById(R.id.tv_file_tittle);
                holder.tv_state = (TextView) convertView.findViewById(R.id.tv_state);
                holder.tv_downloaded = (TextView) convertView.findViewById(R.id.tv_downloaded);
                holder.startDownload=(Button) convertView.findViewById(R.id.btn_start);
                holder.pauseDownload=(Button) convertView.findViewById(R.id.btn_pause);
                holder.playDownload=(Button) convertView.findViewById(R.id.btn_play);
                holder.btn_delete=(Button) convertView.findViewById(R.id.btn_delete);
                holder.layout = (LinearLayout)convertView.findViewById(R.id.layout_btn);

                holder.iv_introduce_pic = (ImageView) convertView.findViewById(R.id.iv_introduce_pic);
                holder.pb_download = (ProgressBar) convertView.findViewById(R.id.pb_download);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.resouceName.setText(fileInfo.getName());
            //aQuery.id(holder.iv_introduce_pic).image(fileInfo.getImgUrl(), true, true, 0, R.drawable.book_default_img);
            PhotoUtils.showCard(PhotoUtils.UriType.HTTP,fileInfo.getImgUrl(),holder.iv_introduce_pic, options);
            if(fragmentType == TYPE_DOWNLOADING) {
                // 初始化进度条
                holder.pb_download.setMax(fileInfo.getLength());
                holder.pb_download.setProgress(fileInfo.getCurLength());
                holder.tv_downloaded.setText(getLengthM(fileInfo.getCurLength()) + "M / " + getLengthM(fileInfo.getLength()) + "M");

                progressBars.put(fileInfo.getOnlyId(), holder.pb_download);

                switch (fileInfo.getState()) {
                    //1正常（未开始）,2下载中，3暂停中，4等待中，5已下载
                    case DownloadInfo.STATE_INIT:
                        holder.tv_state.setText("等待中");
                        allGone(holder);
                        holder.startDownload.setVisibility(View.VISIBLE);
                        break;
                    case DownloadInfo.STATE_LOADING:
                        holder.tv_state.setText("下载中");
                        allGone(holder);
                        holder.pauseDownload.setVisibility(View.VISIBLE);
                        break;
                    case DownloadInfo.STATE_PAUSE:
                        holder.tv_state.setText("暂停中");
                        allGone(holder);
                        holder.startDownload.setVisibility(View.VISIBLE);
                        break;
                    case DownloadInfo.STATE_COMPLETE:
                        holder.tv_state.setText("已下载");
                        allGone(holder);
                        holder.playDownload.setVisibility(View.VISIBLE);
                        break;
                    case DownloadInfo.STATE_FAILED:
                        holder.tv_state.setText("下载失败");
                        allGone(holder);
                        holder.startDownload.setVisibility(View.VISIBLE);
                        break;
                }


                // 开始下载
                holder.startDownload.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        for(DownloadInfo data : downloadData) {
                            if(fileInfo.getOnlyId().equals(data.getOnlyId())) {
                                data.setState(DownloadInfo.STATE_INIT);
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                        DownloadDao.getInstance(context).updataState(fileInfo.getOnlyId(), DownloadInfo.STATE_INIT);
                        DownloadFileServiceV2.addItemToQueue(context, fileInfo);

                        LinearLayout layout = (LinearLayout) v.getParent();
                        layout.findViewById(R.id.btn_pause).setVisibility(View.VISIBLE);
                        v.setVisibility(View.GONE);
                    }

                });

                // 暂停下载
                holder.pauseDownload.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        for(DownloadInfo data : downloadData) {
                            if(fileInfo.getOnlyId().equals(data.getOnlyId())) {
                                data.setState(DownloadInfo.STATE_PAUSE);
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }

                        DownloadDao.getInstance(context).updataState(fileInfo.getOnlyId(), DownloadInfo.STATE_PAUSE);
                        DownloadFileServiceV2.pauseQueueItem(context, fileInfo);

                        LinearLayout layout = (LinearLayout) v.getParent();
                        layout.findViewById(R.id.btn_start).setVisibility(View.VISIBLE);
                        v.setVisibility(View.GONE);
                    }
                });
            } else {
                holder.pb_download.setProgress(100);
                holder.tv_state.setText("已完成");
                holder.tv_state.setVisibility(View.GONE);
                holder.pb_download.setVisibility(View.GONE);
                holder.tv_downloaded.setText(getLengthM(fileInfo.getLength()) + "M");
                holder.startDownload.setVisibility(View.GONE);
                holder.pauseDownload.setVisibility(View.GONE);
                holder.playDownload.setVisibility(View.VISIBLE);
                holder.layout.setVisibility(View.GONE);
                // 点击播放
                convertView.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if(fileInfo.getType() == DownloadInfo.DOWNLOAD_TYPE_BOOK) {
//                            String url = UrlConfig.HOST_H5_BOOK_READ + "?online=false" + "&nid=" + fileInfo.getOnlyId() + "&uid=" + SYUserManager.getInstance().getUserId();
//                        	
                        	String url= "";
                        	if(new File("/mnt/sdcard/nreaderOffline/nreaderOff.html").exists()){
                        		 url = "file:///mnt/sdcard/nreaderOffline/nreaderOff.html?online=false" + "&nid=" + fileInfo.getOnlyId() + "&uid=" + SYUserManager.getInstance().getUserId();
                        	}else{
                        		 url = "file:///android_asset/nreaderOff.html?online=false" + "&nid=" + fileInfo.getOnlyId() + "&uid=" + SYUserManager.getInstance().getUserId();
                        	}
                            Intent webViewIntent = new Intent();
                            webViewIntent.setClass(context, WebSrcViewActivity.class);
                            webViewIntent.putExtra(WebSrcViewActivity.PAGE_URL, url);
                            startActivity(webViewIntent);
                            
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            String type = "video/mp4";
                            Uri uri = Uri.parse("file://" + DownloadFileServiceV2.getVideoPath(fileInfo.getOnlyId()));
                            intent.setDataAndType(uri, type);
                            startActivity(intent);
                        }
                    }
                });
            }
            
            holder.btn_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//删除提示
                    Dialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setMessage("确认删除吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                DownloadFileServiceV2.pauseQueueItem(context, fileInfo);

                                // 删除本地文件
                                String str = fileInfo.getOnlyId();
                                if(fileInfo.getType() == DownloadInfo.DOWNLOAD_TYPE_BOOK) {
                                    deleteFile(DownloadFileServiceV2.getBookIndexPath(str));
                                    deleteFile(DownloadFileServiceV2.getBookContentPath(str));
                                } else {
//                                    List<UrlConsume> urlList = JSON.parseArray(fileInfo.getUrls(), UrlConsume.class);
                                    List<UrlConsume> urlList = new Gson().fromJson(fileInfo.getUrls(),new TypeToken< List<UrlConsume>>(){}.getType());
                                    for(UrlConsume url : urlList) {
                                        deleteFile(DownloadFileServiceV2.getVideoUrlPath(str, url.getUrl()));
                                    }
                                    deleteFile(DownloadFileServiceV2.getVideoPath(str));
                                }

                                // 删除数据库信息
                                DownloadDao.getInstance(getActivity()).delete(fileInfo.getOnlyId());

                                // 更新UI
                                progressBars.remove(fileInfo.getOnlyId());
                                for (DownloadInfo info : downloadData) {
                                    if(info.getOnlyId().equals(fileInfo.getOnlyId())) {
                                        downloadData.remove(info);
                                        adapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                if(downloadData.size() == 0){
                                	layout_pause.setVisibility(View.GONE);
                                	layout_start.setVisibility(View.GONE);
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                    alertDialog.show();
				}
			});


            return convertView;

        }

        private void allGone(ViewHolder holder){
            holder.playDownload.setVisibility(View.GONE);
            holder.pauseDownload.setVisibility(View.GONE);
            holder.startDownload.setVisibility(View.GONE);
        }

    }

    static class ViewHolder {
        TextView resouceName, tv_state, tv_downloaded;
        ImageView img_press_doing, img_press_pause;
        ImageView img_press, iv_introduce_pic;
        ProgressBar pb_download;
        Button startDownload , pauseDownload ,playDownload,btn_delete;
        LinearLayout layout;
    }


    private void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }
    

    private String getLengthM(double lengthB){
        double m = lengthB/1024/1024;
        return String.format("%.2f", m);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if(fragmentType == TYPE_DOWNLOADING) {
//            context.unbindService(conn);
            if(receiver != null) {
                context.unregisterReceiver(receiver);
            }
        }
        super.onDestroy();
    }
    
    private void stopService() {
    	Intent i = new Intent(context, DownloadFileServiceV2.class);
    	context.startService(i);
    }
}