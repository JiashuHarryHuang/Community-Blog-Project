package com.community_blog.util;

/**
 * 生成redis key的工具类
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";

    /**
     * 根据实体的类型和id生成对应的key
     * like:entity:entityType:entityId -> set(userId)
     * @param entityType 实体的类型
     * @param entityId 实体id
     * @return 对应的key
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }
}
