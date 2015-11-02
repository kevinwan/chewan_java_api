package com.gongpingjia.carplay.entity.statistic;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/10/28.
 * 埋点1,7
 */
@Document
public class StatisticUserRegister extends StatisticParent {

    public static final String USER_REGISTER_SUCCESS = "userRegisterSuccess";//用户注册事件

}
