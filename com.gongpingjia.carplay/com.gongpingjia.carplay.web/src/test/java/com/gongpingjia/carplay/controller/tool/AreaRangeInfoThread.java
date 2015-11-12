package com.gongpingjia.carplay.controller.tool;

import com.gongpingjia.carplay.dao.common.AreaRangeDao;
import com.gongpingjia.carplay.entity.common.AreaRange;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Administrator on 2015/11/11.
 */
public class AreaRangeInfoThread implements Runnable {

    private List<AreaRange> areaRangeList;

    private AreaRangeDao areaRangeDao;



    public AreaRangeInfoThread(List<AreaRange> areaRangeList,AreaRangeDao areaRangeDao) {
        this.areaRangeList = areaRangeList;
        this.areaRangeDao = areaRangeDao;
    }



    @Override
    public void run() {
        for (AreaRange areaRange : areaRangeList) {
            areaRangeDao.save(areaRange);
        }
    }
}
