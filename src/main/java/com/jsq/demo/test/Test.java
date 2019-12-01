package com.jsq.demo.test;

import com.alibaba.fastjson.JSON;
import com.jsq.demo.common.utils.CollectionUtil;
import com.jsq.demo.pojo.po.PersonPO;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Test {

    public static void main(String[] args) throws IllegalArgumentException,
            Exception {
        PersonPO p1 = new PersonPO("111", "aaa");
        PersonPO p2 = new PersonPO("222", "bbb");
        PersonPO p3 = new PersonPO("322", "bbb");
        PersonPO p4 = new PersonPO("522", "bbb");
        List list = new ArrayList();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        Map<String, List> test = CollectionUtil.transferMapGroup(list,"name");
        System.out.println(JSON.toJSONString(test));
    }

    public static void test(List list) throws Exception, IllegalAccessException {
        for (int i = 0; i < list.size(); i++) {
            Field[] fields = list.get(i).getClass().getDeclaredFields();
            Object oi = list.get(i);
            for (int j = 0; j < fields.length; j++) {
                if(!fields[j].isAccessible()){
                    fields[j].setAccessible(true);
                }

                System.out.println(fields[j].get(oi));
            }
        }
    }

    public static List extraToListProperty(final Collection collection, final String propertyName){
        List list = new ArrayList(collection.size());
        try {
            for (Object obj : collection) {
                Field[] fields = obj.getClass().getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    if (propertyName.trim().equals(fields[i].getName())){
                        fields[i].setAccessible(true);
                        list.add(fields[i].get(obj));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return list;
    }
}

