package com.gongpingjia.carplay.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author licheng
 *
 */
public class PropertiesUtil {

	private static final Logger LOG = LoggerFactory.getLogger(PropertiesUtil.class);

	/**
	 * 存放配置文件的配置信息，配置文件名称为：carplay.config.properties
	 */
	private static Properties config = loadConfig();

	/**
	 * 加载配置文件
	 * 
	 * @return 返回配置文件的属性集合
	 */
	private static Properties loadConfig() {
		LOG.debug("Begin load config properties");
		String fileName = PropertiesUtil.class.getClassLoader().getResource("conf/carplay.config.properties").getFile();
		LOG.debug("Load config properties: {}", fileName);
		return loadProperties(new File(fileName));
	}

	/**
	 * 加载配置文件
	 * 
	 * @param propFile
	 * @return
	 */
	private static Properties loadProperties(File propFile) {
		Properties properties = new Properties();
		InputStream inStream = null;
		InputStreamReader inReader = null;

		LOG.info("Load properties file: {}", propFile.getName());
		try {
			inStream = new FileInputStream(propFile);
			inReader = new InputStreamReader(inStream, Constants.Charset.UTF8);
			properties.load(inReader);
		} catch (FileNotFoundException e) {
			LOG.warn(e.getMessage(), e);
		} catch (IOException e) {
			LOG.warn(e.getMessage(), e);
		} finally {
			if (inReader != null) {
				try {
					inReader.close();
				} catch (IOException e) {
					LOG.warn(e.getMessage(), e);
				}
			}

			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					LOG.warn(e.getMessage(), e);
				}
			}
		}
		return properties;
	}

	/**
	 * 获取配置属性的值
	 * 
	 * @param propName
	 *            属性名称
	 * @param defaultValue
	 *            默认值
	 * @return 返回属性值
	 */
	public static String getProperty(String propName, String defaultValue) {
		return config.getProperty(propName, defaultValue);
	}
	
	/**
	 * 获取配置属性的值
	 * 
	 * @param propName
	 *            属性名称
	 * @param defaultValue
	 *            默认值
	 * @return 返回属性值
	 */
	public static boolean getProperty(String propName, boolean defaultValue) {
		String prop = config.getProperty(propName, String.valueOf(defaultValue));
		return Boolean.valueOf(prop);
	}

	/**
	 * 获取配置属性的值
	 * 
	 * @param propName
	 *            属性名称
	 * @param defaultValue
	 *            默认值
	 * @return 返回属性值
	 */
	public static int getProperty(String propName, int defaultValue) {
		String prop = config.getProperty(propName, String.valueOf(defaultValue));
		int value = defaultValue;
		try {
			value = Integer.valueOf(prop);
		} catch (NumberFormatException e) {
			LOG.warn("Parse config property failure, property name: {}", propName, e);
		}
		return value;
	}
}
