package com.example.friedegg.cache;

import android.content.Context;

import com.example.friedegg.base.FEApplication;
import com.example.friedegg.modul.FreshNews;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import de.greenrobot.dao.query.QueryBuilder;
import greendao.FreshNewsCache;
import greendao.FreshNewsCacheDao;

public class MyFreshNewsCache extends BaseCache {

    private static MyFreshNewsCache instance;
    private static FreshNewsCacheDao mFreshNewsCacheDao;

    private MyFreshNewsCache() {
    }

    public static MyFreshNewsCache getInstance(Context context) {

        if (instance == null) {

            synchronized (FreshNewsCache.class) {
                if (instance == null) {
                    instance = new MyFreshNewsCache();
                }
            }

            mDaoSession = FEApplication.getDaoSession(context);
            mFreshNewsCacheDao = mDaoSession.getFreshNewsCacheDao();
        }
        return instance;
    }

    public void clearAllCache() {
        mFreshNewsCacheDao.deleteAll();
    }

    @Override
    public ArrayList<FreshNews> getCacheByPage(int page) {

        QueryBuilder<FreshNewsCache> query = mFreshNewsCacheDao.queryBuilder().where(FreshNewsCacheDao
                .Properties.Page.eq("" + page));

        if (query.list().size() > 0) {
            try {
                return FreshNews.parseCache(new JSONArray(query.list().get(0)
                        .getResult()));
            } catch (JSONException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        } else {
            return new ArrayList<>();
        }

    }

    @Override
    public void addResultCache(String result, int page) {

      FreshNewsCache freshNewsCache = new FreshNewsCache();
        freshNewsCache.setResult(result);
        freshNewsCache.setPage(page);
        freshNewsCache.setTime(System.currentTimeMillis());

        mFreshNewsCacheDao.insert(freshNewsCache);
    }

}
