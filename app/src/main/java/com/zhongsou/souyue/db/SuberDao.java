package com.zhongsou.souyue.db;

import com.zhongsou.souyue.module.SuberedItemInfo;

import java.util.List;

/**
 * Created by wangqiang on 15/8/10.
 */
public interface SuberDao {

    public void addAll(List<SuberedItemInfo> infos);

    public void addOne(SuberedItemInfo info);

    public List<SuberedItemInfo> queryAll();

    public SuberedItemInfo queryOne(String srpId);

    public void clearAll();

    public void clearOne(SuberedItemInfo info);

    void updateDb(List<SuberedItemInfo> info);

    public void close();

    public void clearList(List<SuberedItemInfo> infos);

    public List<SuberedItemInfo> querySomeNotGroup();
}
