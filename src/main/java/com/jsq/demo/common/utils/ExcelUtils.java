package com.jsq.demo.common.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.jsq.demo.test.DemoData;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Excel工具类 导出导入
 * 基于 alibaba.easyexcel进行开发
 * @author jsq
 */
public class ExcelUtils {
    /**
     * 导出到excel 单列sheet
     * @param fileName 导出文件名
     * @param list 导出数据 注意：每次导出的文件类型必须相同 且必须使用对应注解
     * 对应导出文件需要注解为 字段名称 {@link ExcelProperty} 需要导出字段 注解中需标注表头信息
     *              {@link ExcelIgnore} 忽略该注解对应字段
     *              忽略所有未添加 ExcelProperty 注解的字段 {@link ExcelIgnoreUnannotated}
     */
    public static void writeSingleSheet(String fileName, List list) {
        fileName = fileName + LocalDate.now() + ".xlsx";
        // 通过读取list中的属性去填写信息头
        if (CollectionUtil.isNotEmpty(list)){
            ExcelWriter excelWriter = EasyExcel.write(fileName, DemoData.class).build();
            // 这里注意 如果同一个sheet只要创建一次
            WriteSheet writeSheet = EasyExcel.writerSheet(fileName).build();
            // 分页去数据库查询数据 这里可以去数据库查询每一页的数据
            excelWriter.write(list, writeSheet);
            /// 千万别忘记finish 会帮忙关闭流
            excelWriter.finish();
        }
    }

    /**
     * 导出到excel 多列sheet
     * @param fileName 导出文件名
     * @param list 导出数据 注意：每次导出的文件类型必须相同 且必须使用对应注解
     * 对应导出文件需要注解为 字段名称 {@link ExcelProperty} 需要导出字段 注解中需标注表头信息
     *              {@link ExcelIgnore} 忽略该注解对应字段
     *              忽略所有未添加 ExcelProperty 注解的字段 {@link ExcelIgnoreUnannotated}
     *              {@link ColumnWidth}、{@link HeadRowHeight}、{@link ContentRowHeight}指定宽度或高度
     */
    public static void writeMultiSheet(String fileName, List list,int pageSize) {
        String writeFileName = fileName + LocalDate.now() + ".xlsx";
        // 通过读取list中的属性去填写信息头
        if (CollectionUtil.isNotEmpty(list)){
            List<List<DemoData>> data = CollectionUtil.getSubList(list,pageSize);
            ExcelWriter excelWriter = EasyExcel.write(writeFileName, DemoData.class).build();

            for (int i = 0; i < data.size(); i++) {
                WriteSheet writeSheet = EasyExcel.writerSheet(i, "测试" + i).build();
                List<DemoData> subData = data.get(i);
                excelWriter.write(subData, writeSheet);

            }
            /// 千万别忘记finish 会帮忙关闭流
            excelWriter.finish();
        }
    }

    public static void  writerSheets(int sheetNo,String sheetName,List data,ExcelWriter excelWriter,WriteSheet writeSheet){
        if (CollectionUtil.isNotEmpty(data)){
            // 分页写入
            writeSheet = EasyExcel.writerSheet(sheetNo, sheetName + "--" + sheetNo).build();
            excelWriter.write(data, writeSheet);
        }
    }

    public static void main(String[] args) {
        List<DemoData> demoDataList = new ArrayList<>(5000);
        for (int i = 0; i < 5000; i++) {
            DemoData demoData = new DemoData();
            demoData.setString("testString");
            demoData.setDate(LocalDateTime.now());
            demoData.setDoubleData(Math.random());
            demoDataList.add(demoData);
        }
        ExcelUtils.writeSingleSheet("test",demoDataList);
    }
}
