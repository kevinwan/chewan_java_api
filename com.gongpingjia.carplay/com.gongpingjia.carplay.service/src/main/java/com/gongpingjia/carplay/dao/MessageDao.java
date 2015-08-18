package com.gongpingjia.carplay.dao;

import java.util.List;
import java.util.Map;

import com.gongpingjia.carplay.po.Message;

public interface MessageDao {
	int deleteByPrimaryKey(String id);

	int insert(Message record);

	Message selectByPrimaryKey(String id);

	int updateByPrimaryKey(Message record);

	List<Map<String, Object>> selectCountByUserAndTypeComment(Map<String, Object> param);

	List<Map<String, Object>> selectCountByUserAndTypeNotComment(Map<String, Object> param);

	List<Map<String, Object>> selectContentByUserAndTypeComment(Map<String, Object> param);

	List<Map<String, Object>> selectContentByUserAndTypeNotComment(Map<String, Object> param);

	List<Map<String, Object>> selectMessageListByUserAndTypeComment(Map<String, Object> param);

	List<Map<String, Object>> selectMessageListByUserAndTypeNotComment(Map<String, Object> param);

	int updateIsCheckedByUserAndTypeComment(Map<String, Object> param);

	int updateIsCheckedByUserAndTypeCommentNotComment(Map<String, Object> param);
}