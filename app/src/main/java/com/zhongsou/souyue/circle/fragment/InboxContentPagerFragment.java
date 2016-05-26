package com.zhongsou.souyue.circle.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SRPActivity;
import com.zhongsou.souyue.circle.activity.CircleInboxActivity;
import com.zhongsou.souyue.circle.model.RecommendInfo;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.StringUtils;
import com.zhongsou.souyue.circle.view.PullToRefreshListView;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CircleGetSysRecommendListRequest;
import com.zhongsou.souyue.net.circle.CircleGetUserRecommendListRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wlong on 14-4-28.
 */
public class InboxContentPagerFragment extends Fragment implements IVolleyResponse {

    private static final int DEFAULT_IMAGE_ID = R.drawable.default_image;

    private static final int DEFAULT_PAGE_SIZE = 10;
    //条目中最多显示的图片个数，该值可<=4
    private static final int DEFAULT_ITEM_IMG_MAX_SIZE = 3;
    private int pno = 1;
    private String token;
    private long interest_id;
    private long last_id = 0;

    private PullToRefreshListView contentListView;
    private View rootView;
    //推荐类型
    private int recomendType = 0;

    private RecommendListAdapter adapter;

//    private Http http;

    private boolean canLoad = true;

    protected ProgressBarHelper progress;

    public InboxContentPagerFragment(int recomendType, long interest_id) {
        this.recomendType = recomendType;
        this.interest_id = interest_id;
        adapter = new RecommendListAdapter(null);
//        http = new Http(this);
        token = SYUserManager.getInstance().getToken();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.circle_inbox_content, null);
        contentListView = (PullToRefreshListView) rootView.findViewById(R.id.lv_circle_inbox_content);
        contentListView.setRefreshTime("上次刷新：" + StringUtils.convertDate(System.currentTimeMillis() + ""));
        contentListView.setCanLoadMore(true);
        contentListView.setCanRefresh(true);
        contentListView.setRefreshListener(new PullToRefreshListView.PullRefreshListener() {
            @Override
            public void refresh() {
                last_id = 0;
                pno = 1;
                requsetRecommendList();
                canLoad = true;
                contentListView.setRefreshTime("上次刷新：" + StringUtils.convertDate(System.currentTimeMillis() + ""));
            }
        });

        contentListView.setLoadMoreListener(new PullToRefreshListView.LoadMoreListener() {
            @Override
            public void loadMore() {
                if (canLoad)
                    requsetRecommendList();
            }
        });


        contentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UIHelper.showRecommendInfo(getActivity(), adapter.getRecommendIds(), position, recomendType);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View loading = rootView.findViewById(R.id.ll_data_loading);
        if (loading != null) {
            progress = new ProgressBarHelper(getActivity(), loading);
            progress.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
                @Override
                public void clickRefresh() {
                    progressCallBack();
                }
            });

            progress.showLoading();
            if (!CMainHttp.getInstance().isNetworkAvailable(getActivity())) {
                progress.showNetError();
            }
        }

        contentListView.setAdapter(adapter);

        requsetRecommendList();

    }

    private void requsetRecommendList() {
        if (recomendType == CircleInboxActivity.RECOMMEND_TYPE_USER) {
//            http.getUserRecommendList(interest_id +"", token, DEFAULT_PAGE_SIZE, 1, last_id);
            CircleGetUserRecommendListRequest.send(HttpCommon.CIRCLE_GETUSERRECOMMENDLIST_REQUESTID, this, interest_id + "", token, DEFAULT_PAGE_SIZE, 1, last_id);
        }

        if (recomendType == CircleInboxActivity.RECOMMEND_TYPE_SYS) {
            CircleGetSysRecommendListRequest.send(HttpCommon.CIRCLE_GETSYSRECOMMENDLIST_REQUESTID,this,interest_id + "", token, DEFAULT_PAGE_SIZE, 1, last_id);
//            http.getSysRecommendList(interest_id + "", token, DEFAULT_PAGE_SIZE, 1, last_id);
        }
    }

    public void getUserRecommendListSuccess(HttpJsonResponse res) {

        if (res != null) {
            String jsonBody = res.getBodyArray().toString();
            onGetResult(jsonBody);

        }

    }

    public void getSysRecommendListSuccess(HttpJsonResponse res) {
        if (res != null) {
            String jsonBody = res.getBodyArray().toString();

            onGetResult(jsonBody);
        }
    }

    private void onGetResult(String jsonBody) {
        try {

//            List<RecommendInfo> recommendInfoList = JSON.parseArray(jsonBody, RecommendInfo.class);
            List<RecommendInfo> recommendInfoList = new Gson().fromJson(jsonBody,new TypeToken<List<RecommendInfo>>(){}.getType());

            if (recommendInfoList != null && recommendInfoList.size() > 0) {
                last_id = recommendInfoList.get(recommendInfoList.size() - 1).getRecommend_id();
                //如果加载到了数据。
                if (progress.isLoading)
                    progress.goneLoading();

                if (pno == 1) {
                    //如果是首次加载或者是下拉刷新
                    if (recommendInfoList.size() < DEFAULT_PAGE_SIZE) {
                        //加载完毕
                        allLoad();
                    } else {
                        //还可以加载
                        canLoadMore();
                    }
                    contentListView.goneRefreshLoading();
                    //更新数据
                    adapter.setRecommendInfoList(recommendInfoList, true);
                    contentListView.setRefreshTime("上次刷新：" + StringUtils.convertDate(System.currentTimeMillis() + ""));
                    return;

                } else {
                    //非首次加载或者下拉刷新
                    if (recommendInfoList.size() < DEFAULT_PAGE_SIZE) {
                        //加载完毕
                        allLoad();
                    } else {
                        //还可以加载
                        canLoadMore();
                    }

                    adapter.setRecommendInfoList(recommendInfoList, false);
                    // contentListView.setSelection( (pno -1) * DEFAULT_PAGE_SIZE - contentListView.getCurrentVisiableCount());
                    return;
                }


            } else if (recommendInfoList != null && recommendInfoList.size() == 0) {

                if (pno == 1) {
                    progress.showNoData();
                    contentListView.setCanLoadMore(false);
                    contentListView.goneRefreshLoading();
                    return;
                } else {
                    allLoad();
                }

            } else {
                Log.e(InboxContentPagerFragment.class.getName(), "数据解析错误");
                progress.showNetError();
            }

        } catch (Exception e) {
            e.printStackTrace();
            progress.showNetError();
        }
    }

    private void allLoad() {
        contentListView.setCanLoadMore(false);
        canLoad = false;
        if (((CircleInboxActivity) getActivity()).getCurrentType() == recomendType)
            SouYueToast.makeText(getActivity(), "已经加载全部", SouYueToast.LENGTH_SHORT).show();
    }

    private void canLoadMore() {
        contentListView.setCanLoadMore(true);
        pno++;
        canLoad = true;
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus status) {
//        if ("getUserRecommendList".equals(methodName)|| "getSysRecommendList".equals(methodName)) {
//            if(pno == 1) {
//                progress.showNetError();
//            }
//            contentListView.goneRefreshLoading();
//        }
//    }

    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()) {
            case HttpCommon.CIRCLE_GETUSERRECOMMENDLIST_REQUESTID:
                getUserRecommendListSuccess(request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRCLE_GETSYSRECOMMENDLIST_REQUESTID:
                getSysRecommendListSuccess(request.<HttpJsonResponse>getResponse());
                break;
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        switch (request.getmId()) {
            case HttpCommon.CIRCLE_GETUSERRECOMMENDLIST_REQUESTID: // 用户推荐
            case HttpCommon.CIRCLE_GETSYSRECOMMENDLIST_REQUESTID: // 系统推荐
                if (pno == 1) {
                    progress.showNetError();
                }
                contentListView.goneRefreshLoading();
                break;
//            case HttpCommon.CIRCLE_GETSYSRECOMMENDLIST_REQUESTID:
//                break;
        }
    }

    @Override
    public void onHttpStart(IRequest request) {

    }


    class RecommendListAdapter extends BaseAdapter {

        private List<RecommendInfo> recommendInfoList;


        public RecommendListAdapter(List<RecommendInfo> recommendList) {
            if (recommendList != null) {
                recommendInfoList = recommendList;
            }

            if (recommendInfoList == null) {
                recommendInfoList = new ArrayList<RecommendInfo>();
            }
        }

        @Override
        public int getCount() {
            return recommendInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View item = null;
            ViewHolder holder = null;

            if (convertView == null) {
                item = View.inflate(getActivity(), R.layout.circle_inbox_content_item, null);
                holder = new ViewHolder();
                holder.title = (TextView) item.findViewById(R.id.tv_circle_inbox_item_title);
                holder.nick_time = (TextView) item.findViewById(R.id.tv_circle_inbox_item_nick_time);
                holder.theme = (TextView) item.findViewById(R.id.tv_circle_inbox_item_theme);
                holder.contentText = (TextView) item.findViewById(R.id.tv_circle_inbox_item_content_text);
                holder.state = (TextView) item.findViewById(R.id.tv_circle_inbox_state);
                //holder.img1 = (ImageView) item.findViewById(R.id.iv_circle_inbox_item_content_img1);
                holder.img2 = (ImageView) item.findViewById(R.id.iv_circle_inbox_item_content_img2);
                holder.img3 = (ImageView) item.findViewById(R.id.iv_circle_inbox_item_content_img3);
                holder.img4 = (ImageView) item.findViewById(R.id.iv_circle_inbox_item_content_img4);
                holder.llImages = (LinearLayout) item.findViewById(R.id.ll_circle_inbox_item_content_imgs);
                //holder.rlImages = (RelativeLayout) item.findViewById(R.id.rl_circle_inbox_item_content_imgs);

                item.setTag(holder);

            } else {
                item = convertView;
                holder = (ViewHolder) item.getTag();
            }

            final RecommendInfo recommendInfo = recommendInfoList.get(position);
            if (!"".equals(recommendInfo.getTitle()) && recommendInfo.getTitle() != null) {
                holder.title.setVisibility(View.VISIBLE);
                holder.title.setText(recommendInfo.getTitle());
            } else
                holder.title.setVisibility(View.GONE);
            holder.contentText.setText(recommendInfo.getBrief());
            if (recomendType == CircleInboxActivity.RECOMMEND_TYPE_USER)
                holder.nick_time.setText(recommendInfo.getNickname() + "－" + StringUtils.convertDate(String.valueOf(recommendInfo.getCreate_time())));
            else
                holder.nick_time.setText(StringUtils.convertDate(recommendInfo.getCreate_time() + ""));
            if (!"".equals(recommendInfo.getSrp_word())) {
                holder.theme.setText(recommendInfo.getSrp_word());
                holder.theme.setClickable(true);
                holder.theme.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openSrpActivity(recommendInfo.getSrp_word(), recommendInfo.getSrp_id());
                    }
                });
            }
            setState(holder.state, recommendInfo.getAudit_state());

            loadImages(holder, recommendInfo.getImages());

            return item;
        }

        public void updateStates(Bundle states) {
            for (RecommendInfo info : recommendInfoList) {
                int state = states.getInt(info.getRecommend_id() + "", info.getAudit_state());
                if (info.getAudit_state() != state)
                    info.setAudit_state(state);
            }
            contentListView.setSelection((pno - 1) * DEFAULT_PAGE_SIZE);
            notifyDataSetChanged();
        }

        public void setRecommendInfoList(List<RecommendInfo> recommends, boolean reset) {
            if (recommendInfoList == null || recommendInfoList.size() == 0 || reset) {
                recommendInfoList = recommends;
            } else {
                recommendInfoList.addAll(recommends);
            }

            notifyDataSetChanged();
        }


        //加载内容中的图片
        private void loadImages(ViewHolder holder, List<String> imgs) {
            if (imgs == null || imgs.size() == 0) {
                holder.llImages.setVisibility(View.GONE);
                return;
            }

            int imgsSize = imgs.size();
            if (imgsSize > DEFAULT_ITEM_IMG_MAX_SIZE)
                imgsSize = DEFAULT_ITEM_IMG_MAX_SIZE;
            holder.llImages.setVisibility(View.VISIBLE);
            holder.img2.setVisibility(View.INVISIBLE);
            holder.img3.setVisibility(View.INVISIBLE);
            holder.img4.setVisibility(View.INVISIBLE);
            switch (imgsSize) {
                case 1:
//                    holder.img1.setVisibility(View.VISIBLE);
//                    holder.llImages.setVisibility(View.GONE);
//                    loadImage(holder.img1, imgs.get(0));
                    //holder.img1.setVisibility(View.GONE);
                    loadImage(holder.img2, imgs.get(0));
                    holder.img2.setVisibility(View.VISIBLE);
                    holder.img3.setVisibility(View.INVISIBLE);
                    holder.img4.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    loadImage(holder.img2, imgs.get(0));
                    loadImage(holder.img3, imgs.get(1));
                    holder.img2.setVisibility(View.VISIBLE);
                    holder.img3.setVisibility(View.VISIBLE);
                    holder.img4.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    loadImage(holder.img2, imgs.get(0));
                    loadImage(holder.img3, imgs.get(1));
                    loadImage(holder.img4, imgs.get(2));
                    holder.img2.setVisibility(View.VISIBLE);
                    holder.img3.setVisibility(View.VISIBLE);
                    holder.img4.setVisibility(View.VISIBLE);
                    break;
                /*case 4:
                    holder.img1.setVisibility(View.VISIBLE);
                    holder.llImages.setVisibility(View.VISIBLE);
                    loadImage(holder.img1, imgs.get(0));
                    loadImage(holder.img2, imgs.get(1));
                    loadImage(holder.img3, imgs.get(2));
                    loadImage(holder.img4, imgs.get(3));
                    break;*/

            }
        }

        private void setState(TextView stateTV, int state) {
            switch (state) {
                case 0:
                    //待审
                    stateTV.setBackgroundResource(R.drawable.circle_inbox_item_state_bg_gray);
                    stateTV.setText("审核后发布");
                    break;
                case 1:
                    //已进入精华区
                    stateTV.setBackgroundResource(R.drawable.circle_inbox_item_state_bg_blue);
                    stateTV.setText("已发布");
                    break;
                case 2:
                    //已拒绝
                    stateTV.setBackgroundResource(R.drawable.circle_inbox_item_state_bg_yellow);
                    stateTV.setText("已拒绝");
                    break;
                case 3:
                    //已进入圈吧
                    stateTV.setBackgroundResource(R.drawable.circle_inbox_item_state_bg_blue);
                    stateTV.setText("已发布");
                    break;
            }
        }

        public long[] getRecommendIds() {
            final int length = recommendInfoList.size();
            long[] ids = new long[length];
            for (int i = 0; i < length; i++) {
                ids[i] = recommendInfoList.get(i).getRecommend_id();
            }
            return ids;
        }
    }

    private void loadImage(ImageView imageView, String url) {
        //aQuery.id(imageView).image(url,true, true, 0, DEFAULT_IMAGE_ID);
        PhotoUtils.showCard(PhotoUtils.UriType.HTTP, url, imageView, MyDisplayImageOption.smalloptions);
    }

    static class ViewHolder {
        TextView title, nick_time, state, contentText, theme;
        ImageView img1, img2, img3, img4;
        LinearLayout llImages;
        //RelativeLayout rlImages;
    }

    protected void progressCallBack() {
        progress.showLoading();
        requsetRecommendList();
    }

    public void onStateResult(Intent data) {
        if (data != null && data.getExtras() != null)
            adapter.updateStates(data.getExtras());
    }


    private void openSrpActivity(String srpWord, String srpId) {
        Intent intent = new Intent(getActivity(), SRPActivity.class);
        intent.putExtra("keyword", srpWord);
        intent.putExtra("srpId", srpId);
        startActivity(intent);
    }

}
