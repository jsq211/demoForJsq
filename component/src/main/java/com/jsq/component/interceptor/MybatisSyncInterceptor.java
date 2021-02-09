package com.jsq.component.interceptor;

import com.google.common.collect.Lists;
import com.jsq.component.config.DatabaseConfig;
import com.jsq.component.config.MybatisSyncComponent;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Properties;


/**
 * mysql同步
 * @author jsq
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class MybatisSyncInterceptor implements Interceptor {

    private static CCJSqlParserManager pm = new CCJSqlParserManager();

    @Autowired
    private MybatisSyncComponent mybatisSyncComponent;
    @Autowired
    private DatabaseConfig databaseConfig;
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];

        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object parameter = invocation.getArgs()[1];

        if (parameter == null) {
            return invocation.proceed();
        }
        if (SqlCommandType.INSERT == sqlCommandType) {
            Object result = invocation.proceed();
            mybatisSyncComponent.insertRedis(databaseConfig.getDatabaseName(),parameter,mappedStatement.getParameterMap());
            return result;
        }
        if (SqlCommandType.UPDATE == sqlCommandType) {
            // 添加修改记录
            Object result = invocation.proceed();
            mybatisSyncComponent.updateRedis(databaseConfig.getDatabaseName(),parameter,mappedStatement.getParameterMap());
            return result;
        }
        if (SqlCommandType.DELETE == sqlCommandType) {
            // 添加删除记录
            List<String> tableNameList = Lists.newArrayList();
            Object result = invocation.proceed();
            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
            String sql = mappedStatement.getBoundSql(parameter).getSql();
            Statement statement = pm.parse(new StringReader(sql));
            tableNameList = tablesNamesFinder.getTableList(statement);
            if (CollectionUtils.isEmpty(tableNameList)){
                return result;
            }
            mybatisSyncComponent.deleteRedis(databaseConfig.getDatabaseName(),parameter,tableNameList.get(0));
            return result;
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // TODO Auto-generated method stub
    }


}