package com.gongpingjia.carplay.dao.common.impl;

import com.gongpingjia.carplay.dao.common.VersionDao;
import com.gongpingjia.carplay.entity.common.Version;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("versionDao")
public class VersionDaoImpl extends BaseDaoImpl<Version,String> implements VersionDao {
}
