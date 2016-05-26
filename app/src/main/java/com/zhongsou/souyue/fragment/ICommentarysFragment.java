package com.zhongsou.souyue.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.ICommentaryActivity;
import com.zhongsou.souyue.adapter.UserReplyListAdapter;
import com.zhongsou.souyue.circle.model.CircleBlogReply;
import com.zhongsou.souyue.module.CircleBlogReplyList;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.detail.MyCommentListReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.NetWorkUtils;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.List;
/**
 * @description: 添加的注释，我的评论 和 回复我的
 * 嵌套在  ICommentaryActivity 里面
 *
 * @auther: qubian 加注释
 * @data: 2015/12/12.
 */
public final class ICommentarysFragment extends Fragment implements
        OnItemClickListener, ProgressBarHelper.ProgressBarClickListener
    ,IVolleyResponse{
    private static final String KEY_CONTENT = "TestFragment:Content";
    private ListView list;
    private View loadMoreView, footerView;
    private TextView tv_loadmore, icomments_nodata;
    private LinearLayout load_more_parent;

//    private Http http;
    private UserReplyListAdapter adapter;

    private String mContent = "";
    private String token = "";
    private String loading = "";
    private String nomore_loading = "";
    private long lastId = 0;
    private boolean hasMore;
    private ProgressBarHelper pbHelper;

    public static ICommentarysFragment newInstance(String content, String token) {
        ICommentarysFragment fragment = new ICommentarysFragment();
        fragment.mContent = content;
        fragment.token = token;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        http = new Http(ICommentarysFragment.this);
        adapter = new UserReplyListAdapter(getActivity());
        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.sy_fragment_icomments_layout, null);
        init(layout, inflater);
        return layout;
    }

    private void init(View layout, LayoutInflater inflater) {
        pbHelper = new ProgressBarHelper(getActivity(),
                layout.findViewById(R.id.ll_data_loading));
        pbHelper.setProgressBarClickListener(this);
        pbHelper.showLoading();

        icomments_nodata = (TextView) layout.findViewById(R.id.icomments_nodata);
        list = (ListView) layout.findViewById(R.id.lv_list_icommentary);
        list.setOnItemClickListener(this);
        footerView = inflater.inflate(R.layout.load_more, null);
        load_more_parent = (LinearLayout) footerView.findViewById(R.id.load_more_parent);
        loadMoreView = footerView.findViewById(R.id.load_more_progress);
        tv_loadmore = (TextView) footerView.findViewById(R.id.btn_load_more);
        loading = getResString(R.string.more_loading);
//        nomore_loading = getResString(R.string.nomore_loading);
        tv_loadmore.setText(loading);
        list.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            	 if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {  
					if ((view.getLastVisiblePosition() == view.getCount() - 1)) {//判断是否滑动到底部
						//处理数据请求、失败处理
						if (NetWorkUtils.isNetworkAvailable()) {
							doMoreList();
						} else {
							loadMoreView.setVisibility(View.GONE);
							tv_loadmore.setText(R.string.mores);
							Toast.makeText(getActivity(), R.string.networkerror, Toast.LENGTH_SHORT).show();
						}
						
					}
				}
            }

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}

        });
        list.setOnItemClickListener(this);
        list.addFooterView(footerView);
        list.setAdapter(adapter);
        doInitList();
    }

    private void doInitList() {
        if ((hasMore || lastId == 0)) {
//            http.commentListMy(token, lastId, getType());
            getMyCommentList(token, lastId, getType());
            hasMore = false;

        }
    }

    /**
     * 网络 获取列表
     * @param token
     * @param commentLastId
     * @param commentType
     */
    public void getMyCommentList(String token, long commentLastId, Integer commentType)
    {
        MyCommentListReq req = new MyCommentListReq(HttpCommon.DETAIL_COMMENT_MY_ID,this);
        req.setParams(token, commentLastId, commentType);
        CMainHttp.getInstance().doRequest(req);
    }

    @Override
    public void onHttpResponse(IRequest _request) {
        switch (_request.getmId())
        {
            case HttpCommon.DETAIL_COMMENT_MY_ID:
                commentListMySuccess(new CircleBlogReplyList(_request.<HttpJsonResponse>getResponse()));
                break;
        }

    }

    @Override
    public void onHttpError(IRequest _request) {
        showEoor();
    }

    @Override
    public void onHttpStart(IRequest _request) {

    }
    private void doMoreList() {
        if (hasMore)
            doInitList();
    }

    /**
     * 为1返回我的评论信息，为2返回回复信息
     *
     * @return
     */
    private int getType() {
        if (ICommentaryActivity.CONTENT[0].equals(mContent))
            return 1;
        else
            return 2;
    }

    // 获取资源String
    public String getResString(int id) {
        return this.getResources().getString(id);
    }

    public void commentListMySuccess(CircleBlogReplyList circleBlogReplyList) {
        if (pbHelper != null && pbHelper.isLoading) {
            pbHelper.goneLoading();
        }
        if (adapter != null && lastId == 0)
            adapter.clearNotices();
        loadMoreView.setVisibility(View.GONE);
        List<CircleBlogReply> circleList = circleBlogReplyList.circleList();
        if (circleBlogReplyList != null && circleList.size() != 0) {
            LogDebugUtil.v("TAG", "lastId=" + lastId + "; size=" + circleList.size());
            list.setVisibility(View.VISIBLE);
            lastId = Long.parseLong(circleList.get(circleList.size() - 1).getSubBlog().getComment_id());
            hasMore = circleBlogReplyList.hasMore();
            for (CircleBlogReply circleBlogReply : circleList) {
                if (getType() == 1) {
                    circleBlogReply.setCommentType(CircleBlogReply.COMMENTTYPE_MINE);
                } else {
                    circleBlogReply.setCommentType(CircleBlogReply.COMMENTTYPE_OTHER_TO_ME);
                }
                adapter.addItem(circleBlogReply);
            }
            adapter.notifyDataSetChanged();
        }
        if (adapter.getCount() <= 0) {
            icomments_nodata.setVisibility(View.VISIBLE);
            switch (getType()) {
                case 1:
                  // icomments_nodata.setText(R.string.usercomment_commit_empty);
                    pbHelper.showNoData();
                    break;
                case 2:
                  //  icomments_nodata.setText(R.string.usercomment_reply_empty);
                    pbHelper.showNoData();
                    break;
                default:
                    break;
            }
            list.setVisibility(View.GONE);
        }
        if (hasMore) {
            loadMoreView.setVisibility(View.VISIBLE);
            tv_loadmore.setText(loading);
        } else {
//            tv_loadmore.setText(nomore_loading);
            footerView.setVisibility(View.GONE);
            load_more_parent.setVisibility(View.GONE);
        }
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus as) {
//
//    }

    public void showEoor()
    {
        if(pbHelper != null && pbHelper.isLoading) {
            pbHelper.goneLoading();
        }
        pbHelper.showNetError();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        try {
        if (footerView == view) {
            LogDebugUtil.v("TAG", "onItemClick loadMoreView");
            return;
        }
        // 中间新闻详情页
        CircleBlogReply circleBlogReply = ((UserReplyListAdapter.ViewHolder) view.getTag()).circleBlogReply;
        SearchResultItem resultItem = new SearchResultItem();
        resultItem.title_$eq(circleBlogReply.getMainBlog().getTitle());
        resultItem.keyword_$eq(circleBlogReply.getMainBlog().getSrp_word());
        resultItem.date_$eq(circleBlogReply.getSubBlog().getCreate_time() + "");
        resultItem.url_$eq(circleBlogReply.getMainBlog().getUrl());
        resultItem.srpId_$eq(circleBlogReply.getMainBlog().getSrp_id());
        resultItem.image_$eq(circleBlogReply.getMainBlog().getImages());
        resultItem.category_$eq(circleBlogReply.getMainBlog().getCategory());
        //跳转图集
        if (!TextUtils.isEmpty(resultItem.url())) {
            if(ConstantsUtils.FR_INFO_PICTURES.equals(resultItem.category())){
                //跳图集
                IntentUtil.getToGalleryNews(getActivity(),resultItem);
                return ;
            }
            else if(ConstantsUtils.FR_INFO_VIDEO.equals(resultItem.category()))
            {
                // 视频类型 不支持跳转 5.2
//                IntentUtil.gotoVideoDetail(getActivity(),resultItem);
                return ;
            }
        }
        if (StringUtils.isNotEmpty(circleBlogReply.getMainBlog().getMblog_id())) {
            resultItem.setBlog_id(Long.parseLong(circleBlogReply.getMainBlog().getMblog_id()));
        }
        if (StringUtils.isNotEmpty(resultItem.url()) || StringUtils.isNotEmpty(resultItem.getBlog_id() > 0)) {
            IntentUtil.skipDetailPage(getActivity(), resultItem, 0);
            getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }

        /*switch (circleBlogReply.getCommentType()) {
            case 1://我的评论
                if (StringUtils.isNotEmpty(resultItem.url()) || StringUtils.isNotEmpty(resultItem.getBlog_id() > 0)) {
                    IntentUtil.skipDetailPage(getActivity(), resultItem, 0);
                    getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
                }
                break;
            case 3://回复我的
                Intent i = new Intent();
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable("searchResultItem", (Serializable) resultItem);
                i.setClass(getActivity(), CommentaryActivity.class);
                i.putExtras(bundle2);
                startActivity(i);
                break;
            default:
                break;
        }*/

//            switch (circleBlogReply.getMainBlog().getType()) {
//                case 1://圈子
////                    IntentUtil.skipCircleDetailPage(getActivity(), circleBlogReply.getMainBlog().getSrp_word(),
//                            circleBlogReply.getMainBlog().getSrp_id(), circleBlogReply.getMainBlog().getBlog_id());
//                    break;
//                case 2://srp新闻
////                    IntentUtil.skipNewsDetailPage(getActivity(), circleBlogReply.getMainBlog().getSrp_word(),
//                            circleBlogReply.getMainBlog().getSrp_id(), circleBlogReply.getMainBlog().getUrl(), "mycomments");
//                    break;
//            }


        /*    Intent intent = new Intent();
            SearchResultItem resultItem = new SearchResultItem();
            intent.setClass(getActivity(), DetailActivity.class);
            Bundle bundle = new Bundle();
            resultItem.title_$eq(circleBlogReply.getMainBlog().getTitle());
            resultItem.keyword_$eq(circleBlogReply.getMainBlog().getSrp_word());
            resultItem.date_$eq(circleBlogReply.getSubBlog().getCreate_time() + "");
            resultItem.url_$eq(circleBlogReply.getMainBlog().getUrl());
            resultItem.srpId_$eq(circleBlogReply.getMainBlog().getSrp_id());
            resultItem.setBlog_id(Integer.parseInt(circleBlogReply.getMainBlog().getBlog_id()));
            resultItem.image_$eq(circleBlogReply.getMainBlog().getImages());

            bundle.putSerializable("searchResultItem", (Serializable) resultItem);
            intent.putExtras(bundle);
            intent.putExtra("from", "mycomments");
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);*/

            /*Intent i = new Intent();
            Bundle bundle2 = new Bundle();
            bundle2.putSerializable("searchResultItem", (Serializable) resultItem);
            i.setClass(getActivity(), CommentaryActivity.class);
            i.putExtras(bundle2);
            startActivity(i);*/

//        } catch (Exception ex) {
//
//        }
    }

    @Override
    public void clickRefresh() {
        doInitList();
    }

}
