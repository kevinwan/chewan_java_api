package com.gongpingjia.carplay.cache.util;

import java.text.MessageFormat;

public class CacheUtil {

	public interface CacheName {
		String USER_TOKEN = "UserToken";

		String CAR_MODEL = "CarModel:{0}";

		String CAR_BRAND = "CarBrand";

		String EMCHAT_TOKEN = "EmchatToken";
	}

	public static String getCarModelKey(String keyParam) {
		return MessageFormat.format(CacheName.CAR_MODEL, keyParam);
	}

}
