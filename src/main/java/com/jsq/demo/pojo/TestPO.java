package com.jsq.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("test")
public class TestPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String n;
}
