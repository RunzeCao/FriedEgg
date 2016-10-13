package com.example.friedegg.cache;

import android.content.Context;

import com.example.friedegg.base.FEApplication;
import com.example.friedegg.modul.Video;
import com.example.friedegg.net.JSONParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import de.greenrobot.dao.query.QueryBuilder;
import greendao.VideoCache;
import greendao.VideoCacheDao;

public class MyVideoCache extends BaseCache {

    private static MyVideoCache instance;
    private static VideoCacheDao mVideoCacheDao;

    private MyVideoCache() {
    }

    public static MyVideoCache getInstance(Context context) {

        if (instance == null) {

            synchronized (MyVideoCache.class) {
                if (instance == null) {
                    instance = new MyVideoCache();
                }
            }

            mDaoSession = FEApplication.getDaoSession(context);
            mVideoCacheDao = mDaoSession.getVideoCacheDao();
        }
        return instance;
    }

    public void clearAllCache() {
        mVideoCacheDao.deleteAll();
    }

    @Override
    public ArrayList<com.example.friedegg.modul.Video> getCacheByPage(int page) {

        QueryBuilder<VideoCache> query = mVideoCacheDao.queryBuilder().where(VideoCacheDao.Properties.Page.eq("" + page));
        if (query.list().size() > 0) {
            return (ArrayList<Video>) JSONParser.toObject(query.list().get(0).getResult(),
                    new TypeToken<ArrayList<Video>>() {
                    }.getType());
        } else {
            return new ArrayList<>();
        }

    }

    @Override
    public void addResultCache(String result, int page) {
        VideoCache jokeCache = new VideoCache();
        jokeCache.setResult(result);
        jokeCache.setPage(page);
        jokeCache.setTime(System.currentTimeMillis());
        mVideoCacheDao.insert(jokeCache);
    }

}
