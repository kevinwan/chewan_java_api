package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.PhoneVerification;

public interface PhoneVerificationDao {
    int deleteByPrimaryKey(String phone);

    int insert(PhoneVerification phone);


    PhoneVerification selectByPrimaryKey(String phone);


    int updateByPrimaryKey(PhoneVerification phone);
}