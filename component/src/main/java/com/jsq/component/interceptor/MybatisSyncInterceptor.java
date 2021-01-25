package com.jsq.component.interceptor;

import com.jsq.component.config.DatabaseConfig;
import com.jsq.component.util.MybatisSyncComponent;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;
import java.util.Properties;


/**
 * mysql同步
 * @author jsq
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class MybatisSyncInterceptor implements Interceptor {

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
            mybatisSyncComponent.insertRedis(databaseConfig.getDatabaseName(),parameter,mappedStatement.getParameterMap());
            return result;
        }
        if (SqlCommandType.DELETE == sqlCommandType) {
            // 添加删除记录

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