package com.gongpingjia.carplay.entity.history;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by 123 on 2015/10/21.
 */
@Document
public class InterestMessage {

    private String id;

    private String relatedId;

    private String type;

    private Long createTime;
}
