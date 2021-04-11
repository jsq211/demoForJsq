package com.jsq.demo.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsq.component.annotation.RedisCacheInput;
import lombok.Data;

import java.util.Date;


@TableName("test")
public class TestPO extends TestParentPO {

    private String n;
    private Boolean enabled;

    @RedisCacheInput(table = "test",inputKey = "id",outPutKey = "createdDate")
    private Date createdDate;

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
