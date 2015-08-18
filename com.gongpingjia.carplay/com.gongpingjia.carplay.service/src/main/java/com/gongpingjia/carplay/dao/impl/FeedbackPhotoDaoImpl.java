package com.gongpingjia.carplay.dao.impl;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.FeedbackPhotoDao;
import com.gongpingjia.carplay.po.FeedbackPhoto;

@Service
public class FeedbackPhotoDaoImpl implements FeedbackPhotoDao {

	@Override
	public int deleteByPrimaryKey(String id) {
		return DASUtil.delete(FeedbackPhoto.class.getName(), "deleteByPrimaryKey", id);
	}

	@Override
	public int insert(FeedbackPhoto record) {
		return DASUtil.save(FeedbackPhoto.class.getName(), "insert", record);
	}

	@Override
	public FeedbackPhoto selectByPrimaryKey(String id) {
		return DASUtil.selectOne(FeedbackPhoto.class.getName(), "selectByPrimaryKey", id);
	}

	@Override
	public int updateByPrimaryKey(FeedbackPhoto record) {
		return DASUtil.update(FeedbackPhoto.class.getName(), "updateByPrimaryKey", record);
	}

}
