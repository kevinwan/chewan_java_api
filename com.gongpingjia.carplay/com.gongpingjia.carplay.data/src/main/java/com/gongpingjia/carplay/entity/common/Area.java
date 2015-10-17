package com.gongpingjia.carplay.entity.common;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/10/17.
 * 区域信息
 */
@Document
public class Area {

    @Id
    private String id;

    private Integer code;

    @Indexed
    private Integer parentId;

    private String name;

    private Byte level;

    private Boolean valiad;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getLevel() {
        return level;
    }

    public void setLevel(Byte level) {
        this.level = level;
    }

    public Boolean getValiad() {
        return valiad;
    }

    public void setValiad(Boolean valiad) {
        this.valiad = valiad;
    }
}
