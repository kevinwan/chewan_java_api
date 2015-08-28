package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.FeedbackPhoto;

public interface FeedbackPhotoDao {
	int deleteByPrimaryKey(String id);

	int insert(FeedbackPhoto record);

	FeedbackPhoto selectByPrimaryKey(String id);

	int updateByPrimaryKey(FeedbackPhoto record);
}