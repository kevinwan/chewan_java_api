package com.gongpingjia.carplay.dao.activity;

import com.gongpingjia.carplay.dao.common.BaseDao;
import com.gongpingjia.carplay.entity.activity.PushInfo;

import java.util.Collection;
import java.util.Set;

/**
 * Created by licheng on 2015/10/26.
 */
public interface PushInfoDao extends BaseDao<PushInfo, String> {

    /**
     * 根据用户的ID列表，获取在列表中的用户已经达到推送上限的用户ID的列表
     *
     * @param receivedUserIds 接收消息的用户
     * @param maxReceived     已接受推送的数量
     * @return 返回Map信息
     */
    Set<String> groupByReceivedUsers(Collection<String> receivedUserIds, Integer maxReceived);
}
