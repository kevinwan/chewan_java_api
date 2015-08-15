package com.gongpingjia.carplay.dao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.gongpingjia.carplay.po.ActivityApplication;

public interface ActivityApplicationDao {
    int deleteByPrimaryKey(String id);

    int insert(ActivityApplication record);

    ActivityApplication selectByPrimaryKey(String id);

    int updateByPrimaryKey(ActivityApplication record);

	List<LinkedHashMap<String, Long>> selectByCountOfActivityUserAndStatus(Map<String, Object> param);
}