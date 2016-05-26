package com.zhongsou.souyue.circle.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.circle.adapter.MyPostListAdapter;
import com.zhongsou.souyue.circle.model.CircleResponseResult;
import com.zhongsou.souyue.circle.model.CircleResponseResultItem;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CircleMemberListRequest;
import com.zhongsou.souyue.net.volley.CSouyueHttpError;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.IntentUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的圈内发帖
 */
public class MyPostActivity extends RightSwipeActivity implements ProgressBarHelper.ProgressBarClickListener,View.OnClickListener {

//    private Http http;
//    private AQuery aquery;
    private ProgressBarHelper pbHelper;
    private String token = "";
    private String fromEssence;
    private TextView activity_bar_title;
    private ListView my_posts_listview;
    private long interest_id;
    private long user_id;
    private MyPostListAdapter adapter;
    private boolean isLoading = false;
    private int page = 1;//页码
    private static int psize = 10;//分页大小

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_mypost_layout);
        pbHelper = new ProgressBarHelper(MyPostActivity.this, findViewById(R.id.ll_data_loading));
        pbHelper.setProgressBarClickListener(this);
        token = getIntent().getStringExtra("token");
        interest_id = this.getIntent().getLongExtra("interest_id", 0);
        user_id = this.getIntent().getLongExtra("user_id", 0);
        fromEssence = getIntent().getStringExtra("fromEssence");
//        http = new Http(this);
//        aquery = new AQuery(this);
        initView();

        loadData();
    }

    private void initView() {
        activity_bar_title = (TextView) this.findViewById(R.id.activity_bar_title);
        activity_bar_title.setText("我的圈内发帖");
        my_posts_listview = (ListView) this.findViewById(R.id.my_posts_listview);

        adapter = new MyPostListAdapter(this, interest_id);
        adapter.setListView(getListView());
        my_posts_listview.setAdapter(adapter);
        my_posts_listview.setOnScrollListener(new ScrollListener());
        my_posts_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
                // 点击列表进详情
                CircleResponseResultItem item = (CircleResponseResultItem) adapter.getItem(position);
//                UIHelper.showPostsDetail(MyPostActivity.this,item.getBlog_id(), item.getInterest_id(), null);
                SearchResultItem item1 = new SearchResultItem();
                item1.setBlog_id(item.getBlog_id());
                item1.setInterest_id(item.getInterest_id());
                IntentUtil.skipDetailPage(MyPostActivity.this, item1, CircleIndexActivity.REQUEST_CODE_POST_DETAIL_ACTIVITY);
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(PublishActivity.ACTION_NEW_POST);
        registerReceiver(receiver, filter);

    }

    private void loadData() {
        if(!isLoading) {
            isLoading = true;
           // http.getMemberPostList(user_id, interest_id, page, psize);
            CircleMemberListRequest req = new CircleMemberListRequest(HttpCommon.CIRCLE_MEMBERLIST_REQUEST,this);
            req.addParams(user_id, interest_id, page, psize);
            mMainHttp.doRequest(req);
        }
    }

    /**
     * 针对发帖操作，接受广播更新圈吧列表
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int type = intent.getIntExtra("resultType", 0);
            if (PublishActivity.ACTION_NEW_POST.equals(action)) {
                CircleResponseResultItem item = (CircleResponseResultItem) intent.getSerializableExtra(PublishActivity.ACTION_KEY_RESPONSEITEM);
                update(item, type);
            }
        }
    };

    /**
     * 更新listview中对应的item
     * @param item
     * @param type
     */
    public void update(CircleResponseResultItem item, int type) {
        Message msg = mHandler.obtainMessage();
        msg.obj = item;
        msg.arg1 = type;
        msg.sendToTarget();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            adapter.updateSingleRow((CircleResponseResultItem) msg.obj, msg.arg1);
            loadData();
        }
    };

    public ListView getListView() {
        return my_posts_listview;
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus status) {
//        pbHelper.goneLoading();
//        if("getMemberPostList".equals(methodName)) {
//            SouYueToast.makeText(MyPostActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
    }

    private class ScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                // 判断滚动到底部
                if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                    mHandler.sendEmptyMessage(0);
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
        }

    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // TODO Auto-generated method stub
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 200 && resultCode != RESULT_CANCELED) {  //圈吧相机
//            String picPath = null;
//            if (adapter.getImageFileUri() != null) {
//                picPath = Utils.getPicPathFromUri(adapter.getImageFileUri(), this);
//                int degree = 0;
//                if (!StringUtils.isEmpty(picPath))
//                    degree = ImageUtil.readPictureDegree(picPath);
//                Matrix matrix = new Matrix();
//                if (degree != 0) {// 解决旋转问题
//                    matrix.preRotate(degree);
//                }
//                Log.v("Huang", "相机拍照imageFileUri != null:" + picPath);
//
//                ArrayList<String> list = new ArrayList<String>();
//                list.add(picPath);
//                adapter.getCircleFollowDialog().addImagePath(list);
//
//            } else {
//                // showToast(R.string.self_get_image_error);
//            }
//        } else if (resultCode == 0x200) {  //圈吧相册
//            List<String> list = new ArrayList<String>();
//            list = data.getStringArrayListExtra("imgseldata");
//            adapter.getCircleFollowDialog().addImagePath(list);
//        }
//    }


    @Override
    public void onHttpError(IRequest request) {
        super.onHttpError(request);
        pbHelper.goneLoading();
        switch (request.getmId()){
            case HttpCommon.CIRCLE_MEMBERLIST_REQUEST:
               IHttpError error =  request.getVolleyError();
                if(error.getErrorType()== IHttpError.TYPE_HTTP_ERROR){
                    pbHelper.showNetError();
                }else {
                    SouYueToast.makeText(MyPostActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onHttpResponse(IRequest request) {
        super.onHttpResponse(request);
        HttpJsonResponse response = request.getResponse();
        switch (request.getmId()){
            case HttpCommon.CIRCLE_MEMBERLIST_REQUEST:
                pbHelper.goneLoading();
                isLoading = false;
                CircleResponseResult result = new CircleResponseResult(response);
                List<CircleResponseResultItem> templist = new ArrayList<CircleResponseResultItem>();
                templist = result.getItems();
                if (templist != null && templist.size() > 0) {
                    adapter.addMore(templist);
                    page++;
                }

                if (templist == null || templist.isEmpty() || templist.size() < psize) {
                    if(adapter.getCount() != 0) {
//                my_posts_listview.removeFooterView(footerView);
//                getMore.setVisibility(View.VISIBLE);
//                getMore.setText(R.string.cricle_no_more_data);
                    }else{
                        pbHelper.showNoData();
                    }

                }

                break;
        }
    }

    @Override
    public void clickRefresh() {
        loadData();
    }

//    public void getMemberPostListSuccess(HttpJsonResponse res, AjaxStatus status) {
//        pbHelper.goneLoading();
//        isLoading = false;
//        int statusCode = res.getCode();
//        if (statusCode != 200) {
//            pbHelper.showNetError();
//            return;
//        }
//        CircleResponseResult result = new CircleResponseResult(res);
//        List<CircleResponseResultItem> templist = new ArrayList<CircleResponseResultItem>();
//        templist = result.getItems();
//        if (templist != null && templist.size() > 0) {
//            adapter.addMore(templist);
//            page++;
//        }
//
//        if (templist == null || templist.isEmpty() || templist.size() < psize) {
//            if(adapter.getCount() != 0) {
////                my_posts_listview.removeFooterView(footerView);
////                getMore.setVisibility(View.VISIBLE);
////                getMore.setText(R.string.cricle_no_more_data);
//            }else{
//                pbHelper.showNoData();
//            }
//
//        }
//
//    }
}
