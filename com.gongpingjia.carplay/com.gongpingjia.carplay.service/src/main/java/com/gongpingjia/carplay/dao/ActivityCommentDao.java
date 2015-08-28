package com.gongpingjia.carplay.dao;

import java.util.List;
import java.util.Map;

import com.gongpingjia.carplay.po.ActivityComment;

public interface ActivityCommentDao {
	int deleteByPrimaryKey(String id);

	int insert(ActivityComment record);

	ActivityComment selectByPrimaryKey(String id);

	int updateByPrimaryKey(ActivityComment record);

	List<Map<String, Object>> selectAuthorAndOrganizerByCommentId(Map<String, Object> param);

	int deleteByPrimaryKey(List<String> commentIds);
}