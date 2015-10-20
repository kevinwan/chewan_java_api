package com.gongpingjia.carplay.common.util;

import com.alibaba.fastjson.serializer.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by Administrator on 2015/9/28.
 */
public class JsonUtil {

    public static String toJSONString(Object object, SerializerFeature... features) {
        SerializeWriter out = new SerializeWriter();
        String s;
        JSONSerializer serializer = new JSONSerializer(out);
        SerializerFeature arr$[] = features;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$++) {
            SerializerFeature feature = arr$[i$];
            serializer.config(feature, true);
        }

//        serializer.getValueFilters().add(new ValueFilter() {
//            public Object process(Object obj, String s, Object value) {
//                if (null != value) {
//                    if (value instanceof java.util.List) {
//                        return "[]";
//                    } else if (value instanceof java.util.Map) {
//                        return "{}";
//                    }
//                    return value;
//                } else {
//                    return "";
//                }
//            }
//        });

        serializer.getPropertyPreFilters().add(new PropertyPreFilter() {
            @Override
            public boolean apply(JSONSerializer serializer, Object object, String name) {
                try {
                    Field field = object.getClass().getDeclaredField(name);
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(Id.class)) {
                        return false;
                    } else if (field.isAnnotationPresent(Transient.class)) {
                        return false;
                    } else {
                        return true;
                    }
                } catch (Exception e) {
                    //父类的属性;
                    return true;
                }
            }
        });
        serializer.write(object);
        s = out.toString();
        out.close();
        return s;
    }


}
