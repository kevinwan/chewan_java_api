package com.gongpingjia.carplay.dao.user.impl;

import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.user.PhoneVerificationDao;
import com.gongpingjia.carplay.entity.user.PhoneVerification;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("phoneVerificationDao")
public class PhoneVerificationDaoImpl extends BaseDaoImpl<PhoneVerification,String> implements PhoneVerificationDao {
}
