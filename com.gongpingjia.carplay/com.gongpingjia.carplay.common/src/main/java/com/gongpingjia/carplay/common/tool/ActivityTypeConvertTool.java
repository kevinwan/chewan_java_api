package com.gongpingjia.carplay.common.tool;

import com.gongpingjia.carplay.common.util.PropertiesUtil;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2015/11/2 0002.
 */
public class ActivityTypeConvertTool {

    private static Map<String, String> typeMap = initTypeMap();

    public static Map<String, String> initTypeMap() {
        Map<String, String> map = new HashMap<>();
        String jsonStr = PropertiesUtil.loadJsonStr(new File("conf/activityConvert.json"));
        if (null != jsonStr) {
            JSONObject object = JSONObject.fromObject(jsonStr);
            Iterator keys = object.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                typeMap.put(key, object.getString(key));
            }
        }
        return map;
    }

    public static String getConvertType(String type) {
        String value = typeMap.get(type);
        if (null == value) {
            return type;
        } else {
            return value;
        }
    }
}
