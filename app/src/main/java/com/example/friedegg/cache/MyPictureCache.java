package com.example.friedegg.cache;

import android.content.Context;

import com.example.friedegg.base.FEApplication;
import com.example.friedegg.modul.Picture;
import com.example.friedegg.net.JSONParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import de.greenrobot.dao.query.QueryBuilder;
import greendao.PictureCache;
import greendao.PictureCacheDao;

/**
 * Created by 润泽 on 2016/6/5.
 * 无聊图缓存
 */
public class MyPictureCache extends BaseCache {
    private static MyPictureCache instance;
    private static PictureCacheDao mPictureCacheDao;

    private MyPictureCache() {
    }

    public static MyPictureCache getInstance(Context context) {
        if (instance == null) {
            synchronized (MyPictureCache.class) {
                if (instance == null) {
                    instance = new MyPictureCache();
                }
            }
            mDaoSession = FEApplication.getDaoSession(context);
            mPictureCacheDao = mDaoSession.getPictureCacheDao();
        }
        return instance;
    }

    @Override
    public void clearAllCache() {
        mPictureCacheDao.deleteAll();
    }

    @Override
    public ArrayList getCacheByPage(int page) {
        QueryBuilder<PictureCache> query = mPictureCacheDao.queryBuilder().where(PictureCacheDao.Properties.Page.eq("" + page));
        if (query.list().size() > 0) {
            return (ArrayList<Picture>) JSONParser.toObject(query.list().get(0).getResult(),
                    new TypeToken<ArrayList<Picture>>() {
                    }.getType());
        } else {
            return new ArrayList<Picture>();
        }
    }

    @Override
    public void addResultCache(String result, int page) {
        PictureCache pictureCache = new PictureCache();
        pictureCache.setResult(result);
        pictureCache.setPage(page);
        pictureCache.setTime(System.currentTimeMillis());
        mPictureCacheDao.insert(pictureCache);
    }
}
