package com.jsq.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsq.component.annotation.RedisCacheInput;
import lombok.Data;

@Data
@TableName("test")
public class TestPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    @RedisCacheInput(database = "test",table = "test",inputKey = "id",outPutKey = "name")
    private String name;
    private String n;
    private Boolean enabled;
}
