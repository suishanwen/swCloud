package com.sw.vote.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 利用Java反射机制对实体类的常用操作
 * @author 【】
 *
 */
public class ObjectUtil {

    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

    final SimpleDateFormat sfCsv = new SimpleDateFormat("yyyyMMdd");

    /**
     * 根据类的名称获取类的属性信息（不包括父类）
     */
    public List<Field> getFields(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        Field[] fields = clazz.getDeclaredFields();
        return new ArrayList<>(Arrays.asList(fields));
    }

    /**
     * 根据类的名称获取属性信息和父类的属性信息
     */
    @SuppressWarnings("unused")
    public List<Field> getAllFields(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        Field[] fields = clazz.getDeclaredFields();
        List<Field> list = new ArrayList<>(Arrays.asList(fields));
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            Field[] superFields = superClazz.getDeclaredFields();
            list.addAll(Arrays.asList(superFields));
        }
        return list;
    }

    /**
     * 根据类名和字段名获取字段在类中的类型
     */
    @SuppressWarnings("unused")
    public String getFieldClassName(String className , String fieldName) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                return field.getType().getSimpleName();
            }
        }
        return "";
    }

    /**
     * 获取传入对象的指定属性的值
     */
    public String getValueByPropName(Object obj, String propName) {
        String value;
        try {
            // 获取对象的属性
            Field field = obj.getClass().getDeclaredField(propName);
            // 对象的属性的访问权限设置为可访问
            field.setAccessible(true);
            value = field.get(obj).toString();
        } catch (Exception e) {
            return null;
        }

        return value;
    }

    /**
     * 根据传入对象获取其属性、类型、属性值的集合
     */
    @SuppressWarnings("unused")
    public List<Map<String, Object>> getFiledsInfo(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> infoMap;
        for (Field field : fields) {
            infoMap = new HashMap<>();
            infoMap.put("type", field.getType().toString());
            infoMap.put("name", field.getName());
            infoMap.put("value", getValueByPropName(o, field.getName()));
            list.add(infoMap);
        }
        return list;
    }


    /**
     * 判断该类是否存在该属性（不包括父类）
     */
    @SuppressWarnings("unused")
    public boolean existsField(String className, String fieldName) throws ClassNotFoundException {
        boolean flag = false;
        Class<?> clazz = Class.forName(className);
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 给该类的该字段设值
     */
    public void setFieldValue(String fieldName, String fieldValue, Object object) {
        Field field = null;
        try {
            boolean isExist = false;
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field var : fields) {
                if (var.getName().equals(fieldName)) {
                    isExist = true;
                    field = object.getClass().getDeclaredField(fieldName);
                    break;
                }
            }

            if (!isExist) {
                field = object.getClass().getSuperclass().getDeclaredField(fieldName);
            }
            // 设置对象的访问权限，保证对private的属性的访问
            field.setAccessible(true);

            if ("class java.math.BigDecimal".equals(field.getType().toString())) {
                field.set(object, new BigDecimal(fieldValue.trim()));
            } else if ("Date".equals(field.getType().getSimpleName())) {
                if (fieldValue.indexOf("/") > 0) {
                    fieldValue = fieldValue.replace('/', '-');
                }
                if (fieldValue.length() == 8) {
                    Date date = sfCsv.parse(fieldValue);
                    field.set(object, date);
                } else if (fieldValue.length() == 10) {
                    Date date = sf.parse(fieldValue);
                    field.set(object, date);
                } else {
                    Date date = sdf.parse(fieldValue);
                    field.set(object, date);
                }
                // field.set(object, sdf.parse(fieldValue));
            } else if ("class java.lang.Integer".equals(field.getType().toString())) {
                field.set(object, Integer.valueOf(fieldValue));
            } else {
                field.set(object, fieldValue);
            }
        } catch (Exception ignored) {

        }

    }
}