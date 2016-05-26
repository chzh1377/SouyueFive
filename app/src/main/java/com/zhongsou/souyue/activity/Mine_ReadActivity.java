package com.zhongsou.souyue.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.MineAdapter;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.module.MineListInfo;
import com.zhongsou.souyue.utils.IntentUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinguanping on 15/1/9.
 */
public class Mine_ReadActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView mListView = null;
    private final String Title[] = {"", "原创", "帖子", "评论", "收藏", "浏览历史"};
    private final String Content[] = {"", "", "", "", "", ""};
    private final int type[] = {0, 1, 1, 1, 1, 1};
    private final int imgId[] = {0,
            R.drawable.mine_read_selfcreate,
            R.drawable.mine_read_bbs,
            R.drawable.mine_read_talk,
            R.drawable.mine_read_favorite,
//            R.drawable.mine_read_download,
            R.drawable.mine_read_history
            };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setContentView(R.layout.fragment_myread_tab);

        ImageButton goback = (ImageButton) findViewById(R.id.goBack);
        goback.setOnClickListener(this);

        TextView title = (TextView) findViewById(R.id.activity_bar_title);
        if (title != null) {
            title.setText("阅读");
        }

        mListView = (ListView) findViewById(R.id.discover_list);
        mListView.setAdapter(new MineAdapter(this, createListData()));
        mListView.setOnItemClickListener(this);
    }

    /**
     * 生成列表数据
     */
    private List<MineListInfo> createListData() {
        List<MineListInfo> listInfos = new ArrayList<MineListInfo>();
        for (int i = 0; i < Title.length; i++) {
            MineListInfo mineListInfo = new MineListInfo();
            mineListInfo.setTitle(Title[i]);
            mineListInfo.setContent(Content[i]);
            mineListInfo.setImgId(imgId[i]);
            mineListInfo.setType(type[i]);
            listInfos.add(mineListInfo);
        }

        return listInfos;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 1://原创
                if (IntentUtil.isLogin()) {
                    IntentUtil.startRead_isCircleActivityWithAnim(this, 1);
                } else {
                    IntentUtil.gotoLogin(this);
                }
                break;
            case 2://帖子
                if (IntentUtil.isLogin()) {
                    IntentUtil.startRead_isCircleActivityWithAnim(this, 2);
                } else {
                    IntentUtil.gotoLogin(this);
                }
                break;
            case 3://评论
                IntentUtil.startRead_CommentaryActivityWithAnim(this);
                break;
            case 4://收藏
                IntentUtil.startRead_FavoriteActivityWithAnim(this);
                break;
//            case 5://离线
//                IntentUtil.startRead_DownLoadActivityWithAnim(this);
//                break;
            case 5:  //浏览历史
                    IntentUtil.startRead_HistoryActivityWithAnim(this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goBack:
                super.onBackPressed();
                break;
            default:
                break;
        }
    }
}
