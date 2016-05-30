package com.example.friedegg.cache;

import java.util.ArrayList;

import greendao.DaoSession;

/**
 * Created by CRZ on 2016/5/27 15:47.
 */
public abstract class BaseCache<T> {
    public static final String DB_NAME = "friedegg-db";
    protected static DaoSession mDaoSession;
    public abstract void clearAllCache();
    public abstract ArrayList<T> getCacheByPage(int page);
    public abstract void addResultCache(String result, int page);
}
