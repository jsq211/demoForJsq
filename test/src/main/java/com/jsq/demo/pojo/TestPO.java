package com.jsq.demo.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("test")
public class TestPO extends TestParentPO {

    private String n;
    private Boolean enabled;

    @Override
    public String toString() {
        return super.toString();
    }
}
