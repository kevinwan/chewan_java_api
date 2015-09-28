package com.gongpingjia.carplay.entity.activity;

/**
 * Created by licheng on 2015/9/28.
 * <p/>
 * 活动流程
 */
public class Flow {

    private Long start;

    private Long end;

    private String description;

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
