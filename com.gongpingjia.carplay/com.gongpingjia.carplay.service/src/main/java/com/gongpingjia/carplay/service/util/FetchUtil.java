package com.gongpingjia.carplay.service.util;

import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.user.User;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

/**
 * Created by Administrator on 2015/10/8.
 */
public class FetchUtil {

    public static Update initUpdateFromJson(JSONObject jsonObject, String... params){
        Update update = new Update();

        for (String param : params) {
            update.set(param, jsonObject.get(param));
        }
        return update;
    }
}
