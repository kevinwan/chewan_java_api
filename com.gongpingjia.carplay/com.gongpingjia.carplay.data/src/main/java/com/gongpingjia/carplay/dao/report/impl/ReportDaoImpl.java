package com.gongpingjia.carplay.dao.report.impl;

import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.report.ReportDao;
import com.gongpingjia.carplay.entity.report.Report;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/11/23.
 */
@Repository("reportDao")
public class ReportDaoImpl extends BaseDaoImpl<Report, String> implements ReportDao {
}
