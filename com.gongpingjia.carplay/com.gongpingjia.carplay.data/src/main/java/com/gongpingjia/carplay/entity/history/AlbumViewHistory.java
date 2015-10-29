package com.gongpingjia.carplay.entity.history;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/9/21.
 * 相册查看历史
 */
@Document
public class AlbumViewHistory {
    @Id
    private String id;

    @Indexed
    private String userId;

    //userId
    @Indexed
    private String viewUserId;

    @Indexed(direction = IndexDirection.DESCENDING)
    private Long viewTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getViewUserId() {
        return viewUserId;
    }

    public void setViewUserId(String viewUserId) {
        this.viewUserId = viewUserId;
    }

    public Long getViewTime() {
        return viewTime;
    }

    public void setViewTime(Long viewTime) {
        this.viewTime = viewTime;
    }
}
