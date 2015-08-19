package com.gongpingjia.carplay.dao;

import java.util.List;
import java.util.Map;

import com.gongpingjia.carplay.po.SeatReservation;

public interface SeatReservationDao {
	int deleteByPrimaryKey(String id);

	int insert(SeatReservation record);

	int insert(List<SeatReservation> recordList);

	SeatReservation selectByPrimaryKey(String id);

	int updateByPrimaryKey(SeatReservation record);

	List<SeatReservation> selectListByParam(Map<String, Object> param);

	int updateByReservationList(List<SeatReservation> paramList);
	
	Integer selectActivityJoinSeatCount(String activityId);
	
	int updateByOfferdCar(Map<String, Object> param);
	
	int updateByTakePullSeat(SeatReservation record);
}