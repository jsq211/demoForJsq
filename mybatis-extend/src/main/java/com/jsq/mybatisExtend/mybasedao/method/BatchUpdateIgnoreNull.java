package com.jsq.mybatisExtend.mybasedao.method;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 批量更新
 * @author jsq
 */
public class BatchUpdateIgnoreNull extends AbstractMethod {

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sql = "<script>\n<foreach collection=\"list\" item=\"item\" separator=\";\">\nupdate %s %s where %s=#{%s} %s\n</foreach>\n</script>";
        String additional = "" + tableInfo.getLogicDeleteSql(true, true);
        String setSql = sqlSet(tableInfo.isLogicDelete(), false, tableInfo, false, "item", "item.");
        String sqlResult = String.format(sql, tableInfo.getTableName(), setSql, tableInfo.getKeyColumn(), "item." + tableInfo.getKeyProperty(),additional);
        StringBuilder sqlFormat = new StringBuilder(sqlResult);
        int idx = sqlResult.lastIndexOf("</if>");
        sqlFormat.replace(idx-1,idx,"");
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlFormat.toString(), modelClass);
        // 第三个参数必须和RootMapper的自定义方法名一致
        return this.addUpdateMappedStatement(mapperClass, modelClass, "batchUpdateIgnoreNull", sqlSource);
    }
}
