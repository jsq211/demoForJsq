package com.jsq.component.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * copy
 * @author jsq
 */
public class BeanUtil extends BeanUtils {
    private static Pattern linePattern = Pattern.compile("_(\\w)");

    public static void copyPropertiesIgnoreNull(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }


    public static String convertName(String columnLabel) {
        columnLabel = columnLabel.toLowerCase();
        Matcher matcher = linePattern.matcher(columnLabel);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static JSONObject convertRedisObject(SqlRowSet sqlRowSet,Set<String> props) {
        JSONObject redisObject = new JSONObject();
        while (sqlRowSet.next()){
            for (String columnName: sqlRowSet.getMetaData().getColumnNames()) {
                String name = BeanUtil.convertName(columnName);
                if (props.contains(name)){
                    Object target = sqlRowSet.getObject(columnName);
                    redisObject.put(name,target);
                }
            }
        }
        return redisObject;
    }
}
