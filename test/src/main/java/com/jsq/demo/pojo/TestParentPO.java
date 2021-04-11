package com.jsq.demo.pojo;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.jsq.component.annotation.RedisCacheInput;

/**
 * @author jsq
 * created on 2021/2/3
 **/
public class TestParentPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    @RedisCacheInput(table = "test",inputKey = "id",outPutKey = "name")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
