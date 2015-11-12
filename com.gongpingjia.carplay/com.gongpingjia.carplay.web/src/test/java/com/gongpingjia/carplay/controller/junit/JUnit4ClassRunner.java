package com.gongpingjia.carplay.controller.junit;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Log4jConfigurer;


import java.io.FileNotFoundException;

/**
 * Created by Administrator on 2015/10/24 0024.
 */
public class JUnit4ClassRunner extends SpringJUnit4ClassRunner {

    static {
        try {
            Log4jConfigurer.initLogging("classpath:conf/log4j.xml");
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Cannot Initialize log4j");
        }
    }

    public JUnit4ClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }
}
