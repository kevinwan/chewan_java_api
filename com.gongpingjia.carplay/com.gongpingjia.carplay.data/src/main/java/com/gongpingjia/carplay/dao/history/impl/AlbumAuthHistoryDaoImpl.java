package com.gongpingjia.carplay.dao.history.impl;

import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.history.AlbumAuthHistoryDao;
import com.gongpingjia.carplay.entity.history.AlbumAuthHistory;
import org.springframework.stereotype.Repository;

/**
 * Created by 123 on 2015/11/12.
 */
@Repository("albumAuthHistoryDaoImpl")
public class AlbumAuthHistoryDaoImpl extends BaseDaoImpl<AlbumAuthHistory, String> implements AlbumAuthHistoryDao {
}
