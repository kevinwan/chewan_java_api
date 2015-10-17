package com.gongpingjia.carplay.cache.util;

import java.text.MessageFormat;

public class CacheUtil {

    public interface CacheName {
        String USER_TOKEN = "UserToken:{0}";

        String CAR_MODEL = "CarModel:{0}";

        String CAR_BRAND = "CarBrand";

        String EMCHAT_TOKEN = "EmchatToken";

        String AREA_CODE = "Area:{0}";
    }

    public static String getCarModelKey(String keyParam) {
        return MessageFormat.format(CacheName.CAR_MODEL, keyParam);
    }

    public static String getAreaKey(Integer keyParam) {
        return MessageFormat.format(CacheName.AREA_CODE, keyParam);
    }

}
