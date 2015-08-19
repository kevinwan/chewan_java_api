package com.gongpingjia.carplay.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.SeatReservationDao;
import com.gongpingjia.carplay.po.SeatReservation;

@Service
public class SeatReservationDaoImpl implements SeatReservationDao {

	@Override
	public int deleteByPrimaryKey(String id) {
		return DASUtil.delete(SeatReservation.class.getName(), "deleteByPrimaryKey", id);
	}

	@Override
	public int insert(SeatReservation record) {
		return DASUtil.save(SeatReservation.class.getName(), "insert", record);
	}

	@Override
	public SeatReservation selectByPrimaryKey(String id) {
		return DASUtil.selectOne(SeatReservation.class.getName(), "selectByPrimaryKey", id);
	}

	@Override
	public int updateByPrimaryKey(SeatReservation record) {
		return DASUtil.update(SeatReservation.class.getName(), "updateByPrimaryKey", record);
	}

	@Override
	public List<SeatReservation> selectListByParam(Map<String, Object> param) {
		return DASUtil.selectList(SeatReservation.class.getName(), "selectListByParam", param);
	}

	@Override
	public int updateByReservationList(List<SeatReservation> paramList) {
		return DASUtil.updateList(SeatReservation.class.getName(), "updateByPrimaryKey", paramList);
	}

	@Override
	public int insert(List<SeatReservation> recordList) {
		return DASUtil.saveList(SeatReservation.class.getName(), "insert", recordList);
	}

	@Override
	public Integer selectActivityJoinSeatCount(String activityId) {
		return DASUtil.selectOne(SeatReservation.class.getName(), "selectActivityJoinSeatCount", activityId);
	}

	@Override
	public int updateByOfferdCar(Map<String, Object> param) {
		return DASUtil.update(SeatReservation.class.getName(), "updateByOfferdCar", param);
	}

	@Override
	public int updateByTakeSeat(SeatReservation record) {
		return DASUtil.update(SeatReservation.class.getName(), "updateByTakeSeat", record);
	}

	@Override
	public int deleteByParam(Map<String, Object> param) {
		return DASUtil.delete(SeatReservation.class.getName(), "deleteByParam", param);
	}

	@Override
	public List<String> selectCarIdsByActivityId(String activityId) {
		return DASUtil.selectList(SeatReservation.class.getName(), "selectCarIdsByActivityId", activityId);
	}

	@Override
	public int updateByQuitActivity(Map<String, Object> param) {
		return DASUtil.update(SeatReservation.class.getName(), "updateByQuitActivity", param);
	}

}
