package com.gongpingjia.carplay.dao;

import java.util.List;
import java.util.Map;

import com.gongpingjia.carplay.po.AuthenticationApplication;

public interface AuthenticationApplicationDao {
    int deleteByPrimaryKey(String id);

    int insert(AuthenticationApplication record);


    AuthenticationApplication selectByPrimaryKey(String id);


    int updateByPrimaryKey(AuthenticationApplication record);
    
    List<AuthenticationApplication> selectByParam(Map<String, Object> param);

	List<Map<String, Object>> selectCarModelbyId(Map<String, Object> param);
}