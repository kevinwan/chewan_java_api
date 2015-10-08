package com.gongpingjia.carplay.dao.common;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Collection;
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

    public T findOne(Query query);

    public List<T> findByIds(Collection<K> ids);

    public List<T> findByIds(List<K> ids);

    public List<T> findByIds(K []ids);

    public List<T> find(Query query);

    public void save(T entity);

    public void deleteById(K id);

    public void deleteByIds(K[] ids);

    public void delete(Query query);

    public long count();

    public long count(Query query);

    public void update(K id,T entity);

    public void update(Query query, Update update);

    public void update(K id, Update update);

    public void updateFirst(Query query, Update update);

    public void updateAll(Query query, Update update);

}
