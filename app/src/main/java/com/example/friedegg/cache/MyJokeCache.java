package com.example.friedegg.cache;

import android.content.Context;

import com.example.friedegg.base.FEApplication;
import com.example.friedegg.modul.Joke;
import com.example.friedegg.net.JSONParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import de.greenrobot.dao.query.QueryBuilder;
import greendao.JokeCache;
import greendao.JokeCacheDao;

/**
 * Created by CRZ on 2016/5/30 10:24.
 * MyJokeCache
 */
public class MyJokeCache extends BaseCache {
    private static MyJokeCache instance;
    private static JokeCacheDao mJokeCacheDao;

    private MyJokeCache() {
    }

    public static MyJokeCache getInstance(Context context) {
        if (instance == null) {
            synchronized (MyJokeCache.class) {
                if (instance == null) {
                    instance = new MyJokeCache();
                }
            }
            mDaoSession = FEApplication.getDaoSession(context);
            mJokeCacheDao = mDaoSession.getJokeCacheDao();
        }
        return instance;
    }

    @Override
    public void clearAllCache() {
        mJokeCacheDao.deleteAll();
    }

    @Override
    public ArrayList<Joke> getCacheByPage(int page) {
        QueryBuilder<JokeCache> query = mJokeCacheDao.queryBuilder().where(JokeCacheDao.Properties.Page.eq("" + page));
        if (query.list().size() > 0) {
            return (ArrayList<Joke>) JSONParser.toObject(query.list().get(0).getResult(), new TypeToken<ArrayList<Joke>>() {
            }.getType());
        }
        return new ArrayList<>();
    }

    @Override
    public void addResultCache(String result, int page) {
        JokeCache jokeCache = new JokeCache();
        jokeCache.setResult(result);
        jokeCache.setPage(page);
        jokeCache.setTime(System.currentTimeMillis());
        mJokeCacheDao.insert(jokeCache);
    }
}
