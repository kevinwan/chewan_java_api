package com.gongpingjia.carplay.dao.common.impl;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gongpingjia.carplay.util.JsonUtil;
import com.gongpingjia.carplay.dao.common.BaseDao;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

/**
 * Created by heyongyu on 2015/9/21.
 */
public class BaseDaoImpl<T, K> implements BaseDao<T, K> {

    private Class cls;

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
    public T findOne(Query query) {
        return mongoTemplate.findOne(query, getCls());
    }

    @Override
    public List<T> findByIds(Collection<K> ids) {
        return mongoTemplate.find(Query.query(Criteria.where("_id").in(ids)), getCls());
    }

    @Override
    public List<T> findByIds(List<K> ids) {
        return mongoTemplate.find(Query.query(Criteria.where("_id").in(ids)), getCls());
    }

    @Override
    public List<T> findByIds(K[] ids) {
        return mongoTemplate.find(Query.query(Criteria.where("_id").in(ids)), getCls());
    }

    @Override
    public List<T> find(Query query) {
        return mongoTemplate.find(query, getCls());
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
    public void deleteByIds(Collection<K> ids) {
        mongoTemplate.remove(Query.query(Criteria.where("_id").in(ids)), getCls());
    }

    @Override
    public void delete(Query query) {
        mongoTemplate.remove(query, getCls());
    }

    @Override
    public long count() {
        return mongoTemplate.count(null, getCls());
    }

    @Override
    public long count(Query query) {
        return mongoTemplate.count(query, getCls());
    }

    @Override
    public void update(K id, T entity) {
        BasicDBObject queryObj = new BasicDBObject("_id", new ObjectId(id.toString()));
        String jsonStr = JsonUtil.toJSONString(entity, SerializerFeature.WriteMapNullValue);
        DBObject dbObject = (DBObject) JSON.parse(jsonStr);
        mongoTemplate.getCollection(getCollectionName(entity)).update(queryObj, dbObject);
    }

    @Override
    public void update(Query query, Update update) {
        mongoTemplate.updateMulti(query, update, getCls());
    }

    @Override
    public void update(K id, Update update) {
        Query idQuery = Query.query(Criteria.where("_id").is(id));
        mongoTemplate.updateFirst(idQuery, update, getCls());
    }

    @Override
    public void updateFirst(Query query, Update update) {
        mongoTemplate.updateFirst(query, update, getCls());
    }

    @Override
    public void updateAll(Query query, Update update) {
        mongoTemplate.updateMulti(query, update, getCls());
    }

    private String getCollectionName(Object object) {
        Document annotation = object.getClass().getAnnotation(Document.class);
        if (null == annotation) {
            throw new RuntimeException("the object has no Document Annotation");
        }
        if (StringUtils.isNotEmpty(annotation.collection())) {
            return annotation.collection();
        } else {
            String simpleName = object.getClass().getSimpleName();
            char[] chars = simpleName.toCharArray();
            if (chars[0] >= 'A' && chars[0] <= 'Z') {
                chars[0] += 32;
            } else {
                throw new RuntimeException("the class first letter must be up case");
            }
            return new String(chars);
        }
    }
}
