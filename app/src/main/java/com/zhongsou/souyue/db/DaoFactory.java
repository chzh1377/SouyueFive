package com.zhongsou.souyue.db;

/**
 * Created by wangqiang on 15/8/10.
 * 适配层  工厂设计模式
 */
public class DaoFactory {

    private static SuberDao dao;

    public static SuberDao createDao() {
        if (dao == null) {
            dao = new SuberDaoImp();
        }
        return dao;
    }

}
