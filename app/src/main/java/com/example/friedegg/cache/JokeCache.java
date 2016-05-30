package com.example.friedegg.cache;

import android.content.Context;

import com.example.friedegg.base.FEApplication;
import com.example.friedegg.modul.Joke;
import com.example.friedegg.net.JSONParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import de.greenrobot.dao.query.QueryBuilder;
import greendao.JokeCacheDao;

/**
 * Created by CRZ on 2016/5/30 10:24.
 * JokeCache
 */
public class JokeCache extends BaseCache {
    private static JokeCache instance;
    private static JokeCacheDao mJokeCacheDao;

    private JokeCache() {
    }

    public static JokeCache getInstance(Context context) {
        if (instance == null) {
            synchronized (JokeCache.class) {
                if (instance == null) {
                    instance = new JokeCache();
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
        QueryBuilder<greendao.JokeCache> query = mJokeCacheDao.queryBuilder().where(JokeCacheDao.Properties.Page.eq("" + page));
        if (query.list().size() > 0) {
            return (ArrayList<Joke>) JSONParser.toObject(query.list().get(0).getResult(), new TypeToken<ArrayList<Joke>>() {
            }.getType());
        }
        return new ArrayList<>();
    }

    @Override
    public void addResultCache(String result, int page) {
        greendao.JokeCache jokeCache = new greendao.JokeCache();
        jokeCache.setResult(result);
        jokeCache.setPage(page);
        jokeCache.setTime(System.currentTimeMillis());
        mJokeCacheDao.insert(jokeCache);
    }
}
