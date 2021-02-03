package com.jsq.demo.pojo;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.jsq.component.annotation.RedisCacheInput;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jsq
 * created on 2021/2/3
 **/
@Getter
@Setter
public class TestParentPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    @RedisCacheInput(database = "test",table = "test",inputKey = "id",outPutKey = "name")
    private String name;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
