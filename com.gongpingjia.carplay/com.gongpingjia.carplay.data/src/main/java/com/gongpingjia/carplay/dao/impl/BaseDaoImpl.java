package com.gongpingjia.carplay.dao.impl;

import com.gongpingjia.carplay.dao.BaseDao;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

/**
 * Created by heyongyu on 2015/9/21.
 */
public class BaseDaoImpl<T, K> implements BaseDao<T, K> {

    private Class<T> cls;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Class<T> getCls() {
        if (this.cls == null) {
            ParameterizedType type = (ParameterizedType) getClass()
                    .getGenericSuperclass();
            this.cls = ((Class) type.getActualTypeArguments()[0]);
        }
        return this.cls;
    }

    @Override
    public List<T> listAll() {
        return mongoTemplate.findAll(getCls());
    }

    @Override
    public T findById(K id) {
        return mongoTemplate.findById(id, getCls());
    }

    @Override
    public T findOne(Map<String, Object> params) {
        Criteria criteria = getCriteriaFromMap(params);
        return mongoTemplate.findOne(Query.query(criteria), getCls());
    }

    @Override
    public List<T> find(Map<String, Object> params) {
        Criteria criteria = getCriteriaFromMap(params);
        return mongoTemplate.find(Query.query(criteria), getCls());
    }

    @Override
    public void save(T entity) {
        mongoTemplate.save(entity);
    }

    @Override
    public void deleteById(K id) {
        mongoTemplate.remove(Query.query(Criteria.where("_id").is(id)), getCls());
    }

    @Override
    public void deleteByIds(K[] ids) {
        mongoTemplate.remove(Query.query(Criteria.where("_id").in(ids)), getCls());
    }

    @Override
    public void deleteByParams(Map<String, Object> params) {
        Criteria criteria = getCriteriaFromMap(params);
        mongoTemplate.remove(Query.query(criteria), getCls());
    }


    @Override
    public long count() {
        return mongoTemplate.count(null, getCls());
    }

    @Override
    public long count(Map<String, Object> params) {
        Criteria criteria = getCriteriaFromMap(params);
        return mongoTemplate.count(Query.query(criteria), getCls());
    }

    @Override
    public void update(T entity) {
        try {
            //通过id 获取数据
            K id = (K) BeanUtils.getProperty(entity, "id");
            Update update = new Update();
            Field[] fields = this.getCls().getDeclaredFields();
            for (Field field : fields) {
                //不包含Transient的属性；
                if (field.getAnnotation(Transient.class) != null) {
                    continue;
                }
                String fieldName = field.getName();
                // id不添加其中
                if (fieldName != null && !"id".equals(fieldName)) {
                    // 使用set更新器,如果没有就会添加，有就会更新
                    update.set(fieldName, BeanUtils.getProperty(entity, fieldName));
                }
            }
            this.mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(id)), update, this.getCls());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void updateNotNull(T entity) {
        try {
            //通过id 获取数据
            K id = (K) BeanUtils.getProperty(entity, "id");
            Update update = new Update();
            Field[] fields = this.getCls().getDeclaredFields();
            for (Field field : fields) {
                //不包含Transient的属性；
                if (field.getAnnotation(Transient.class) != null) {
                    continue;
                }
                String fieldName = field.getName();
                // id不添加其中
                if (fieldName != null && !"id".equals(fieldName)) {
                    // 使用set更新器,如果没有就会添加，有就会更新
                    Object fieldVal = BeanUtils.getProperty(entity, fieldName);
                    if (null != fieldVal) {
                        update.set(fieldName, fieldVal);
                    }
                }
            }
            this.mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(id)), update, this.getCls());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void updateAll(Map<String, Object> queryParams, Map<String, Object> updateParams) {
        Criteria criteria = getCriteriaFromMap(queryParams);
        Update update = getUpdateFromMap(updateParams);
        mongoTemplate.updateMulti(Query.query(criteria), update,getCls());
    }

    private Criteria getCriteriaFromMap(Map<String, Object> params) {
        Criteria criteria = new Criteria();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            criteria.where(param.getKey()).is(param.getValue());
        }
        return criteria;
    }

    private Update getUpdateFromMap(Map<String, Object> params) {
        Update update = new Update();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            update.set(param.getKey(), param.getValue());
        }
        return update;
    }
}
