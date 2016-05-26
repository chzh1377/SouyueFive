package com.zhongsou.souyue.db;

import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.net.AsyncTask;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.List;

/**
 * Created by wangqiang on 15/8/5.
 * 订阅 数据访问对象
 */
public class SuberDaoImp implements SuberDao {

    private String userId;
    private boolean isPre = true;
    private SYSharedPreferences sysp;
    public static final int CODE_OK = 0;
    public static final int CODE_ERROR = -1;
    private int resultCode = 0;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public SuberDaoImp() {
        sysp = SYSharedPreferences.getInstance();
    }

    /**
     * 批量插入
     *
     * @param infos
     */
    @Override
    public void addAll(List<SuberedItemInfo> infos) {
        resultCode = CODE_OK;
        SuberTableDBHelper helper = SuberTableDBHelper.getInstance();
        isPre = sysp.getPosition();
        userId = SYUserManager.getInstance().getUserId();
        try {
            helper.insertForList(infos, userId, isPre);
        } catch (Exception e) {
            e.printStackTrace();
            resultCode = CODE_ERROR;
        }
        helper = null;
    }

    public void initAll(List<SuberedItemInfo> infos) {
        resultCode = CODE_OK;
        SuberTableDBHelper helper = SuberTableDBHelper.getInstance();
        isPre = sysp.getPosition();
        userId = SYUserManager.getInstance().getUserId();
        try {
            helper.initForList(infos, userId);
        } catch (Exception e) {
            e.printStackTrace();
            resultCode = CODE_ERROR;
        }
    }

    /**
     * 添加订阅
     *
     * @param info
     */
    @Override
    public void addOne(SuberedItemInfo info) {
        resultCode = CODE_OK;
        SuberTableDBHelper helper = SuberTableDBHelper.getInstance();
        sysp = SYSharedPreferences.getInstance();
        userId = SYUserManager.getInstance().getUserId();
        isPre = sysp.getPosition();
        try {
            helper.insertOne(info, userId, isPre);
        } catch (Exception e) {
            e.printStackTrace();
            resultCode = CODE_ERROR;
        }
    }

    /**
     * 查询
     *
     * @return
     */
    @Override
    public List<SuberedItemInfo> queryAll() {
        resultCode = CODE_OK;
        SuberTableDBHelper helper = SuberTableDBHelper.getInstance();
        userId = SYUserManager.getInstance().getUserId();
        if (StringUtils.isEmpty(userId)) {
            userId = "0";
        }
        List<SuberedItemInfo> infos = null;
        try {
            infos = helper.queryAll(userId);
            if (infos == null) {
                resultCode = CODE_ERROR;
            }
            return infos;
        } catch (Exception e) {
            e.printStackTrace();
            resultCode = CODE_ERROR;
        }
        return null;

    }

    /**
     * 根据id获取suberItemInfo
     *
     * @param srpId
     * @return
     */
    public SuberedItemInfo queryOne(String srpId) {
        resultCode = CODE_OK;
        SuberTableDBHelper helper = SuberTableDBHelper.getInstance();
        userId = SYUserManager.getInstance().getUserId();
        try {
            return helper.queryOne(userId, srpId);
        } catch (Exception e) {
            e.printStackTrace();
            resultCode = CODE_ERROR;
        }
        return null;

    }

    /**
     * 批量删除
     */
    @Override
    public void clearAll() {
        resultCode = CODE_OK;
        SuberTableDBHelper helper = SuberTableDBHelper.getInstance();
        userId = SYUserManager.getInstance().getUserId();
        try {
            helper.deleteAll(userId);
        } catch (Exception e) {
            e.printStackTrace();
            resultCode = CODE_ERROR;
        }

    }

    /**
     * 取消订阅
     *
     * @param info
     */
    @Override
    public void clearOne(SuberedItemInfo info) {
        resultCode = CODE_OK;
        SuberTableDBHelper helper = SuberTableDBHelper.getInstance();
        userId = SYUserManager.getInstance().getUserId();
        try {
            helper.delete(info, userId);
        } catch (Exception e) {
            e.printStackTrace();
            resultCode = CODE_ERROR;
        }
    }

    /**
     * 批量删除
     *
     * @param infos
     */
    public void clearList(List<SuberedItemInfo> infos) {
        resultCode = CODE_OK;
        SuberTableDBHelper helper = SuberTableDBHelper.getInstance();
        userId = SYUserManager.getInstance().getUserId();
        try {
            helper.deleteList(infos, userId);
        } catch (Exception e) {
            e.printStackTrace();
            resultCode = CODE_ERROR;
        }
    }

    @Override
    public List<SuberedItemInfo> querySomeNotGroup() {
        resultCode = CODE_OK;
        SuberTableDBHelper helper = SuberTableDBHelper.getInstance();
        userId = SYUserManager.getInstance().getUserId();
        if (StringUtils.isEmpty(userId)) {
            userId = "0";
        }
        List<SuberedItemInfo> infos = null;
        try {
            infos = helper.queryDiction(userId);
            if (infos == null) {
                resultCode = CODE_ERROR;
            }
            return infos;
        } catch (Exception e) {
            e.printStackTrace();
            resultCode = CODE_ERROR;
        }
        return null;
    }

    @Override
    public void close() {
        SuberTableDBHelper.getInstance().close();
    }

    public void updateDb(final List<SuberedItemInfo> infos) {
        DBAsyncTask task = new DBAsyncTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, infos);
    }

    class DBAsyncTask extends AsyncTask<List<SuberedItemInfo>, Void, Void> {
        @Override
        protected Void doInBackground(List<SuberedItemInfo>... params) {
            List<SuberedItemInfo> infos = params[0];
            clearAll();
            initAll(infos);
            return null;
        }
    }
}
