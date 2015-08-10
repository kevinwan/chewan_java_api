package com.gongpingjia.carplay.controller.server;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({ @ContextConfiguration(name = "parent", locations = "classpath*:META-INF/spring/*.service.xml"),
		@ContextConfiguration(name = "child", locations = "classpath:conf/springmvc-servlet.xml") })
public class BaseTest {

}
