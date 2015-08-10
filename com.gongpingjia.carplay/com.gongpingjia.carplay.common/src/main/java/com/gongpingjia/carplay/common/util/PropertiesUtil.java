package com.gongpingjia.carplay.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
	 * 存放配置文件的配置信息，配置文件名称为：third-party.config.properties
	 */
	private static Properties thirdConfig = loadThirdConfig();

	/**
	 * 添加第三方配置文件
	 * 
	 * @return 返回配置文件的属性集合
	 */
	private static Properties loadThirdConfig() {
		LOG.debug("Begin load config properties");
		String fileName = PropertiesUtil.class.getClassLoader().getResource("conf/third-party.config.properties")
				.getFile();
		LOG.debug("Load config properties: " + fileName);
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

		LOG.info("Load properties file: " + propFile.getName());
		try {
			inStream = new FileInputStream(propFile);
			properties.load(inStream);
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
		return properties;
	}

	/**
	 * 获取第三方配置信息
	 * 
	 * @return
	 */
	public static Properties getThirdConfig() {
		return thirdConfig;
	}
}
