package com.blog4java.plugin.utils;

import java.lang.reflect.Field;

public abstract class ReflectionUtils {

    /**
     * 利用反射获取指定对象的指定属性
     *
     * @param target 目标对象
     * @param fieldName 目标属性
     * @return 目标属性的值
     */
    public static Object getFieldValue(Object target, String fieldName) {
        Object result = null;
        Field field = ReflectionUtils.getField(target, fieldName);
        if (field != null) {
            field.setAccessible(true);
            try {
                result = field.get(target);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 利用反射获取指定对象里面的指定属性
     *
     * @param target
     *            目标对象
     * @param fieldName
     *            目标属性
     * @return 目标字段
     */
    private static Field getField(Object target, String fieldName) {
        Field field = null;
        for (Class<?> clazz = target.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                // ignore
            }
        }
        return field;
    }

    /**
     * 利用反射设置指定对象的指定属性为指定的值
     *
     * @param target 目标对象
     * @param fieldName 目标属性
     * @param fieldValue 目标值
     */
    public static void setFieldValue(Object target, String fieldName, String fieldValue) {
        Field field = ReflectionUtils.getField(target, fieldName);
        if (field != null) {
            try {
                field.setAccessible(true);
                field.set(target, fieldValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
