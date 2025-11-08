package com.easychat.utils;

public class ArrayUtils {
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

    public static boolean contains(Object[] array, Object value) {
        if (isEmpty(array)) {
            return false;
        }
        for (Object obj : array) {
            if (obj.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsAll(Object[] array, Object[] values) {
        if (isEmpty(array)) {
            return false;
        }
        for (Object obj : values) {
            if (!contains(array, obj)) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsAny(Object[] array, Object[] values) {
        if (isEmpty(array)) {
            return false;
        }
        for (Object obj : values) {
            if (contains(array, obj)) {
                return true;
            }
        }
        return false;
    }

    public static boolean equals(Object[] array1, Object[] array2) {
        if (array1 == null && array2 == null) {
            return true;
        }
        if (array1 == null || array2 == null) {
            return false;
        }
        if (array1.length != array2.length) {
            return false;
        }
        for (Object o : array1) {
            if (!contains(array2, o)) {
                return false;
            }
        }
        return true;
    }
}
