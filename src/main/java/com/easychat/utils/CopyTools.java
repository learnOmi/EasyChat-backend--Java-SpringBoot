package com.easychat.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

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
            return t;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
