package com.gongpingjia.carplay.initial;

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

    @Override
    public void onApplicationEvent(ContextRefreshedEvent arg0) {
        //refresh awt context
        if (AppContext.getAppContext() == null) {
            SunToolkit.createNewAppContext();
        }
    }
}
