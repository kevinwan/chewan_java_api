package com.gongpingjia.carplay.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by heyongyu on 2015/9/21.
 */
public interface BaseDao<T,K> {


    /**
     * 查询一个collection下所有的文档
     */
    public List<T> listAll();

    /**
     *
     */
    public T findById(K key);

    /**
     *
     */
    public T findOne(Map<String, Object> params);

    public List<T> find(Map<String, Object> params);

    /**
     *通过mongoDB支持的query查询；与mongodb有一定的耦合性；
     */
//    public T findOne(Query query);

//    public List<T> find(Query query);

    public void save(T entity);

    public void deleteById(K id);

    public void deleteByIds(K[] ids);

    public void deleteByParams(Map<String, Object> params);


//    public void delete(Query query);

    //获取collection所有的document number;
    public long count();

    //获取查询条件下collection中 document number;
    public long count(Map<String, Object> params);

    public void update(T entity);

    public void updateNotNull(T entity);

    public void updateAll(Map<String, Object> queryParams, Map<String, Object> updateParams);

//    public void update(Query query, Update update);
}
