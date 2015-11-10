package com.gongpingjia.carplay.initial;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

/**
 * Created by Administrator on 2015/11/9.
 */
@SuppressWarnings("restriction")
@Service
@Scope("singleton")
public class ContextConfiguration implements ApplicationListener<ContextRefreshedEvent> {


    private static Logger logger = LoggerFactory.getLogger(ContextConfiguration.class);

    public void onApplicationEvent(ContextRefreshedEvent arg0) {
        //refresh awt context
        if (AppContext.getAppContext() == null) {
            logger.info("^^^^^^^^^^^^^^^^^^^____^^^^^^^^^^^^^^^^^^^^^  AppContext.getAppContext is null ^^^^^^^^^^^^^^^^^^^____^^^^^^^^^^^^^^^^^^^^^");
            SunToolkit.createNewAppContext();
        } else {
            logger.info("^^^^^^^^^^^^^^^^^^^____^^^^^^^^^^^^^^^^^^^^^  AppContext.getAppContext not null ^^^^^^^^^^^^^^^^^^^____^^^^^^^^^^^^^^^^^^^^^");
        }
    }
}
