package com.jsq.component.interceptor;

import com.jsq.component.config.DatabaseConfig;
import com.jsq.component.config.MybatisSyncComponent;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.util.CollectionUtils;

import java.io.StringReader;
import java.util.List;
import java.util.Properties;


/**
 * mysql同步
 * @author jsq
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class MybatisSyncInterceptor implements Interceptor {

    private static final CCJSqlParserManager PARSER_MANAGER = new CCJSqlParserManager();
    private static final TablesNamesFinder TABLES_NAMES_FINDER = new TablesNamesFinder();

    private final MybatisSyncComponent mybatisSyncComponent;
    private final DatabaseConfig databaseConfig;

    public MybatisSyncInterceptor(MybatisSyncComponent mybatisSyncComponent, DatabaseConfig databaseConfig) {
        this.mybatisSyncComponent = mybatisSyncComponent;
        this.databaseConfig = databaseConfig;
    }

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
            mybatisSyncComponent.insertRedis(parameter,mappedStatement.getParameterMap());
            return result;
        }
        if (SqlCommandType.UPDATE == sqlCommandType) {
            // 添加修改记录
            Object result = invocation.proceed();
            mybatisSyncComponent.updateRedis(parameter,mappedStatement.getParameterMap());
            return result;
        }
        if (SqlCommandType.DELETE == sqlCommandType) {
            // 添加删除记录
            Object result = invocation.proceed();
            String sql = mappedStatement.getBoundSql(parameter).getSql();
            Statement statement = PARSER_MANAGER.parse(new StringReader(sql));
            List<String> tableNameList = TABLES_NAMES_FINDER.getTableList(statement);
            if (CollectionUtils.isEmpty(tableNameList)){
                return result;
            }
            mybatisSyncComponent.deleteRedis(parameter,tableNameList.get(0));
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