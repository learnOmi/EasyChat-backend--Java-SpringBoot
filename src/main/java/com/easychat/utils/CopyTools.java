package com.easychat.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.lang.reflect.Field;

public class CopyTools {
    public static <T, S> List<T> copyList(List<S> sourceList, Class<T> targetClass) {
        return sourceList.stream().map(s -> {
            try {
                T t = targetClass.newInstance();
                BeanUtils.copyProperties(s, t);
                return t;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    public static <T, S> T copy(S source, Class<T> targetClass) {
        try {
            T t = targetClass.newInstance();
            BeanUtils.copyProperties(source, t);
            convertByteFieldsToInt(source, t);
            return t;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 遍历源对象和目标对象的字段，将源对象的 Byte 值赋给目标对象的 Integer 字段
     */
    private static <S, T> void convertByteFieldsToInt(S source, T target) throws IllegalAccessException {
        // 获取源对象的所有字段（包括私有字段）
        Field[] sourceFields = source.getClass().getDeclaredFields();
        // 获取目标对象的所有字段
        Field[] targetFields = target.getClass().getDeclaredFields();

        for (Field sourceField : sourceFields) {
            // 只处理 Byte 类型的字段
            if (sourceField.getType() == Byte.class || sourceField.getType() == byte.class) {
                sourceField.setAccessible(true);
                Object value = sourceField.get(source);

                if (value != null) {
                    // 在目标对象中查找同名字段
                    for (Field targetField : targetFields) {
                        if (targetField.getName().equals(sourceField.getName())) {
                            // 检查目标字段是否是 Integer 类型
                            if (targetField.getType() == Integer.class || targetField.getType() == int.class) {
                                targetField.setAccessible(true);
                                // 执行转换：Byte -> Integer
                                targetField.set(target, ((Byte) value).intValue());
                                break; // 找到匹配并赋值后，跳出内层循环
                            }
                        }
                    }
                }
            }
        }
    }
}
