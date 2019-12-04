package com.jsq.demo.test;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;


import java.util.Date;


/**
 * 基础数据类.这里的排序和excel里面的排序一致
 *
 * @author Jiaju Zhuang
 **/
@ContentRowHeight(10)
@HeadRowHeight(20)
@ColumnWidth(25)

public class DemoData {
    @ColumnWidth(50)
    @ExcelProperty("测试字符串")
    private String string;
    @ExcelProperty("测试日期")
    private Date date;
    @ExcelProperty("测试随机数")
    private Double doubleData;
    @ExcelProperty("测试随机字符串")
    private String code;
    @ExcelProperty("测试随机数字")
    private Integer no;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getDoubleData() {
        return doubleData;
    }

    public void setDoubleData(Double doubleData) {
        this.doubleData = doubleData;
    }
}
