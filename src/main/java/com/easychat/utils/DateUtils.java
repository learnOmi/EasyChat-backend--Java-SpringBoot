package com.easychat.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 线程安全的日期工具类模板
 * 使用ThreadLocal解决SimpleDateFormat的线程安全问题
 * 通过缓存机制提高性能，避免重复创建SimpleDateFormat实例
 */
public class DateUtils {
    // 同步锁对象，用于线程安全地创建ThreadLocal
    private static final Object lockObj = new Object();
    
    // 缓存不同日期格式的ThreadLocal<SimpleDateFormat>映射
    // key: 日期格式字符串, value: 对应的ThreadLocal<SimpleDateFormat>
    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<>();

    /**
     * 获取指定格式的SimpleDateFormat实例
     * 使用双重检查锁定模式确保线程安全，同时提高性能
     * @param pattern 日期格式模式
     * @return SimpleDateFormat实例
     */
    private static SimpleDateFormat getSdf(final String pattern) {
        // 从缓存中获取ThreadLocal
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);
        
        // 如果不存在，则创建新的ThreadLocal
        if (tl == null) {
            // 使用同步锁确保线程安全
            synchronized (lockObj) {
                // 双重检查，防止在等待锁的过程中已经被其他线程创建
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    // 创建新的ThreadLocal，并在其initialValue中创建SimpleDateFormat实例
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    // 将新的ThreadLocal存入缓存
                    sdfMap.put(pattern, tl);
                }
            }
        }
        // 从ThreadLocal中获取SimpleDateFormat实例
        return tl.get();
    }

    /**
     * 格式化日期为字符串
     * @param date 日期对象
     * @param pattern 日期格式模式
     * @return 格式化后的字符串
     */
    public static String format(Date date, String pattern) {
        return getSdf(pattern).format(date);
    }

    /**
     * 将字符串解析为日期对象
     * @param dateStr 日期字符串
     * @param pattern 日期格式模式
     * @return 解析后的日期对象
     * @throws ParseException 如果解析失败抛出此异常
     */
    public static Date parse(String dateStr, String pattern) throws ParseException {
        return getSdf(pattern).parse(dateStr);
    }
}
