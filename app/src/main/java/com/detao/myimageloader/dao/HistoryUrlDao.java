package com.detao.myimageloader.dao;

import com.detao.myimageloader.bean.HistoryUrl;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shaoronggang on 2017/2/20.
 * 操作history_url表的dao类,操作数据库中表的工具类：CRUD
 */

public class HistoryUrlDao {
    private DbManager.DaoConfig daoConfig;

    public HistoryUrlDao() {
        daoConfig = new DbManager.DaoConfig()
                .setDbName("imageLoader.db")  //设置数据库的表名
                .setDbVersion(1);//设置数据库的版本
    }

    /**
     * 查询数据
     * @return
     */
    public List<HistoryUrl> getAll(){
        List<HistoryUrl> list = null;
        DbManager dbManager = x.getDb(daoConfig); // xUtils中的数据库获取数据库的管理器

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
            if(list == null) {
                list = new ArrayList<>();
            }
        }

        return list;
    }

    /**
     * 添加操作
     * @param historyUrl
     */
    public  void  save(HistoryUrl historyUrl){
        DbManager dbManager = x.getDb(daoConfig); //获取数据库的管理器

        try {
            dbManager.saveBindingId(historyUrl); //添加数据的同时，还可以修改内存中的historyUrl的id与数据表中的_id一致
        } catch (DbException e) {
            e.printStackTrace();
        } finally {
            try {
                dbManager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }




}
