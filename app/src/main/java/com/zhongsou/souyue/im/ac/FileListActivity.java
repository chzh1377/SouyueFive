package com.zhongsou.souyue.im.ac;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tuita.sdk.im.db.helper.MessageHistoryDaoHelper;
import com.tuita.sdk.im.db.module.MessageFile;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.adapter.FileListAdapter;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.transfile.IMFieUtil;
import com.zhongsou.souyue.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhou on 2015/11/12.
 */
public class FileListActivity extends IMBaseActivity implements View.OnClickListener {
    private ListView lvFile;
    private ImageButton btnBack;
    private ImserviceHelp mImServiceHelp;
    private FileListAdapter mFilelistAdapter;
    private List<MessageFile> mFiles;
    private TextView tvEdit;
    private TextView tvAllFiles;
    private RelativeLayout rlDelete;
    private ImageView ivDelete;
    private List<MessageFile> mDeleteFileList = new ArrayList<MessageFile>();
    private MessageFile mClickfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_file_list);
        initView();
        initData();
        initEvent();
    }

    /**
     * 初始化各种事件监听
     */
    private void initEvent() {
        btnBack.setOnClickListener(this);
        tvEdit.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        lvFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mClickfile = mFilelistAdapter.getItem(position);
                if (mFilelistAdapter.ismIsEdit()) {  //在编辑状态下
                    FileListAdapter.ViewHolder viewHolder = (FileListAdapter.ViewHolder) view.getTag();
                    mFilelistAdapter.getmSelectedMap().put(mClickfile.getId(), viewHolder.cbEdit.isChecked());
                    if (!viewHolder.cbEdit.isChecked()) {
                        mDeleteFileList.add(mClickfile);
                        mFilelistAdapter.getmSelectedMap().put(mClickfile.getId(), true);
                    } else {
                        mDeleteFileList.remove(mClickfile);
                        mFilelistAdapter.getmSelectedMap().remove(mClickfile.getId());
                    }
                    mFilelistAdapter.notifyDataSetChanged();
                } else { //正常状态下点击打开
                    IMFieUtil.openFile(FileListActivity.this, new File(mClickfile.getLocalpath()));
                }
            }
        });
    }

    /**
     * 初始化界面
     */
    private void initView() {
        lvFile = (ListView) findViewById(R.id.lv_file);
        btnBack = (ImageButton) findViewById(R.id.ib_back);
        tvEdit = findView(R.id.btn_edit);
        tvEdit.setText(getResources().getString(R.string.im_edit));
        tvAllFiles = findView(R.id.tv_all_file);
        tvAllFiles.setText(getResources().getString(R.string.im_filelistall_title));
        rlDelete = (RelativeLayout) findViewById(R.id.rl_delete);
        ivDelete = (ImageView) findViewById(R.id.iv_delete);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mDeleteFileList.clear();
        mImServiceHelp = ImserviceHelp.getInstance();
        mFiles = mImServiceHelp.getAllDownLoadFiles();
        mFilelistAdapter = new FileListAdapter(this, mFiles);
        lvFile.setAdapter(mFilelistAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:     //返回
                finish();
                break;
            case R.id.btn_edit:     //编辑
                if (mFilelistAdapter != null) {
                    mFilelistAdapter.getmSelectedMap().clear();
                    mDeleteFileList.clear();
                    if (mFilelistAdapter.ismIsEdit()) {      //如果是编辑状态
                        mFilelistAdapter.setmIsEdit(false);
                        rlDelete.setVisibility(View.GONE);
                        tvEdit.setText(getResources().getString(R.string.im_edit));
                    } else {     //不是编辑状态
                        mFilelistAdapter.setmIsEdit(true);
                        rlDelete.setVisibility(View.VISIBLE);
                        tvEdit.setText(getResources().getString(R.string.im_completion));
                    }
                    mFilelistAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.iv_delete:   //删除
                if (mDeleteFileList.size() > 0) {
                    for (MessageFile msgFile : mDeleteFileList) {
                        FileUtils.retireFile(msgFile.getLocalpath());
                        long id = MessageHistoryDaoHelper.getInstance(this).findMsgChatId(msgFile.getId());
                        mImServiceHelp.deleteFile(msgFile.getId());
                        //容错  history里面 会有没有  此文件id情况
                        if (id != -1) {
                            MessageHistoryDaoHelper.getInstance(FileListActivity.this).updateMsgFileId(id, -1);//写-1 是因为默认-1时则认为不存在
                        }
                        mFiles.remove(msgFile);
                    }
                    mDeleteFileList.clear();
                    mFilelistAdapter.setmMessageFileList(mFiles);
                    mFilelistAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFilelistAdapter != null) {
            mDeleteFileList.clear();
            mFilelistAdapter.getmSelectedMap().clear();
        }
    }
}
