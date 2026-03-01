package com.easychat.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * app发布mapper
 * @author 'Tong'
 * @since 2026/03/02
 */
public interface AppUpdationMapper<T, P> extends BaseMapper {
	// 多条件更新
	Integer updateByParam(@Param("bean") T t, @Param("query") P p);

	// 多条件删除
	Integer deleteByParam(@Param("query") P p);

	// 根据Id查询
	T selectById(@Param("id") Integer id);

	// 根据Id更新
	Integer updateById(@Param("bean") T t, @Param("id") Integer id);

	// 根据Id删除
	Integer deleteById(@Param("id") Integer id);

    // 根据Version查询
    T selectByVersion(@Param("version") String version);

    // 根据Version更新
    Integer updateByVersion(@Param("bean") T t, @Param("version") String version);

    // 根据Version删除
    Integer deleteByVersion(@Param("version") String version);

    T selectLatestUpdate(@Param("appVersion") String appVersion, @Param("uid") String uid);
}