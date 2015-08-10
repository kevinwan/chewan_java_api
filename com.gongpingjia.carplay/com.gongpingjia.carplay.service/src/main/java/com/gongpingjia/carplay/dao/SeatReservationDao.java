package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.SeatReservation;

public interface SeatReservationDao {
    int deleteByPrimaryKey(String id);

    int insert(SeatReservation record);


    SeatReservation selectByPrimaryKey(String id);


    int updateByPrimaryKey(SeatReservation record);
}