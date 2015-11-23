package com.gongpingjia.carplay.entity.report;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Administrator on 2015/11/23.
 */
@Document
public class Report {

    @Id
    private String activityReportId;

    /**
     * 被举报人
     */
    @Indexed
    private String reportUserId;

    /**
     * 被举报的活动主键     可以为空；//TODO
     */
    @Indexed
    private String activityId;

    /**
     * 举报人主键，对应User 中的 userId
     */
    @Indexed
    private String userId;

    /**
     * 举报的类型 例如 色情低俗，广告骚扰，政治敏感，诈骗，违法 等
     */
    private String type;

    /**
     * 举报内容
     */
    private String content;

    /**
     * 举报时间           索引降序
     */
    @Indexed(direction = IndexDirection.DESCENDING)
    private Long createTime;

    public String getActivityReportId() {
        return activityReportId;
    }

    public void setActivityReportId(String activityReportId) {
        this.activityReportId = activityReportId;
    }


    public String getReportUserId() {
        return reportUserId;
    }

    public void setReportUserId(String reportUserId) {
        this.reportUserId = reportUserId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
