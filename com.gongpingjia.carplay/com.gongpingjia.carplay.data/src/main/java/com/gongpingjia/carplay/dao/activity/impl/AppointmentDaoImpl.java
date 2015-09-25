package com.gongpingjia.carplay.dao.activity.impl;

import com.gongpingjia.carplay.dao.activity.AppointmentDao;
import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.entity.activity.Appointment;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/24.
 */
@Repository("appointmentDao")
public class AppointmentDaoImpl extends BaseDaoImpl<Appointment, String> implements AppointmentDao {
}

