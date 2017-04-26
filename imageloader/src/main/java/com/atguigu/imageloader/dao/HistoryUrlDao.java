package com.atguigu.imageloader.dao;

import com.atguigu.imageloader.bean.HistoryUrl;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作数据库中表的工具类：CRUD
 * Created by shkstart on 2016/5/9.
 */
public class HistoryUrlDao {

    private DbManager.DaoConfig daoConfig;

    public HistoryUrlDao(){
        daoConfig = new DbManager.DaoConfig()
                    .setDbName("imageloader.db")//设置数据库名
                    .setDbVersion(1);//设置数据库的版本

    }

    /**
     * 查询操作
     * @return
     */
    public List<HistoryUrl> getAll(){
        List<HistoryUrl> list = null;

        DbManager dbManager = x.getDb(daoConfig);//获取数据库的管理器

        try {
            list = dbManager.findAll(HistoryUrl.class);
        } catch (DbException e) {
            e.printStackTrace();
        }finally {
            try {
                dbManager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(list == null){
                list = new ArrayList<>();
            }
        }

        return list;



    }

    /**
     * 添加操作
     * @param historyUrl
     */
    public void save(HistoryUrl historyUrl){//HistoryUrl historyUrl = new HistoryUrl(-1,"www.atguigu.com");
        DbManager dbManager = x.getDb(daoConfig);

        try {
            dbManager.saveBindingId(historyUrl);//添加数据的同时，还可以修改内存中historyUrl的id与数据表中_id一致
        } catch (DbException e) {
            e.printStackTrace();
        }finally {
            try {
                dbManager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



}
