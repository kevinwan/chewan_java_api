package com.gongpingjia.carplay.dao.impl.user;

import com.gongpingjia.carplay.dao.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.user.AlbumDao;
import com.gongpingjia.carplay.data.user.Album;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("albumDao")
public class AlbumDaoImpl extends BaseDaoImpl<Album,String> implements AlbumDao {
}
