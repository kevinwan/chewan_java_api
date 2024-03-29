package com.gongpingjia.carplay.common.custom;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by licheng on 2015/9/28.
 * JsonMapper的定制
 */
public class ObjectMappingCustom extends ObjectMapper {

    public ObjectMappingCustom() {
        super();
        // 允许单引号
        this.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 字段和值都加引号
        this.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 数字也加引号
        this.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
        this.configure(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS, true);
        // 空值处理为空串
        this.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {

            @Override
            public void serialize(Object value, JsonGenerator jgen, SerializerProvider sp) throws IOException, JsonProcessingException {
                jgen.writeString("");
            }
        });

        this.getSerializerProvider().setDefaultKeySerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {

            }
        });
    }
}
