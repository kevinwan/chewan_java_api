package com.gongpingjia.carplay.common.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class BeanUtil implements ApplicationContextAware {
	private static ApplicationContext context;

	/**
	 * Spring注入的时候，会调用接口的该方法，注入context对象 ==暂时理解为Findbugs误报==
	 */
	@Override
	public void setApplicationContext(ApplicationContext context) {
		BeanUtil.context = context;
	}

	public static ApplicationContext getApplicationContext() {
		if (context == null)
			throw new IllegalStateException("applicaitonContext not injected correctly");
		return context;
	}

	public static <T> T getBean(String name, Class<T> type) {
		return context.getBean(name, type);
	}

	public static Object getBean(String name) {
		return context.getBean(name);
	}

	public static <T> T getBean(Class<T> type) {
		return context.getBean(type);
	}

}
