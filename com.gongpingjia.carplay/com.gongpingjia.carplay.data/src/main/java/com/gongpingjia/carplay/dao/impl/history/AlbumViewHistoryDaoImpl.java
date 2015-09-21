package com.gongpingjia.carplay.dao.impl.history;

import com.gongpingjia.carplay.dao.history.AlbumViewHistoryDao;
import com.gongpingjia.carplay.dao.impl.BaseDaoImpl;
import com.gongpingjia.carplay.entity.history.AlbumViewHistory;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("albumViewHistoryDao")
public class AlbumViewHistoryDaoImpl extends BaseDaoImpl<AlbumViewHistory,String> implements AlbumViewHistoryDao {
}
