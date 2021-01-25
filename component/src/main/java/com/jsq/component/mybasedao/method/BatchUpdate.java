package com.jsq.component.mybasedao.method;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.function.Predicate;

/**
 * 批量更新
 * @author jsq
 */
public class BatchUpdate extends AbstractMethod {
    @Setter
    @Accessors(chain = true)
    private Predicate<TableFieldInfo> predicate;
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sql = "<script>\n<foreach collection=\"list\" item=\"et\" separator=\";\">\nupdate %s %s where %s=#{%s} %s\n</foreach>\n</script>";
        SqlMethod sqlMethod = SqlMethod.UPDATE_BY_ID;
        String additional = optlockVersion(tableInfo) + tableInfo.getLogicDeleteSql(true, true);
        String sqlSet = this.filterTableFieldInfo(tableInfo.getFieldList(), getPredicate(),
                i -> i.getSqlSet(true, ENTITY_DOT), NEWLINE);
        sqlSet = SqlScriptUtils.convertSet(sqlSet);
        String sqlResult = String.format(sql, tableInfo.getTableName(), sqlSet, tableInfo.getKeyColumn(), "et." + tableInfo.getKeyProperty(),additional);
        StringBuilder sqlFormat = new StringBuilder(sqlResult);
        int idx = sqlResult.lastIndexOf("\n</set>");
        sqlFormat.replace(idx-1,idx,"");

        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlFormat.toString(), modelClass);
        return addUpdateMappedStatement(mapperClass, modelClass, getMethod(sqlMethod), sqlSource);
    }
    private Predicate<TableFieldInfo> getPredicate() {
        Predicate<TableFieldInfo> noLogic = t -> !t.isLogicDelete();
        if (predicate != null) {
            return noLogic.and(predicate);
        }
        return noLogic;
    }

    @Override
    public String getMethod(SqlMethod sqlMethod) {
        return "batchUpdate";
    }
}
