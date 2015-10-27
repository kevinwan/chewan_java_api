package com.gongpingjia.carplay.common.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import java.io.IOException;
import java.util.*;

/**
 * Created by Administrator on 2015/9/28.
 */
public class CustomObjectMapper extends ObjectMapper {

    public CustomObjectMapper() {
        super();
        // 允许单引号
        this.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 字段和值都加引号
        this.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 数字也加引号
        this.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, false);
        this.configure(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS, true);

        // 空值处理为空串
        this.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {

            @Override
            public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
                if (value instanceof Collection) {
                    gen.writeObject(new ArrayList<>(0));
                } else if (value instanceof Map) {
                    gen.writeObject(new HashMap<>(0));
                } else {
                    gen.writeString("");
                }
            }

            @Override
            public void serialize(Object value, JsonGenerator jgen, SerializerProvider sp) throws IOException, JsonProcessingException {
                if (value instanceof Collection) {
                    jgen.writeObject(new ArrayList<>(0));
                } else if (value instanceof Map) {
                    jgen.writeObject(new HashMap<>(0));
                } else {
                    jgen.writeString("");
                }
            }
        });
    }
}
