package com.community_blog.util;

/**
 * 生成redis key的工具类
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";

    /**
     * 根据实体的类型和id生成对应的key
     * 值为点赞的用户id集合 （点赞列表）
     * @param entityType 实体的类型
     * @param entityId 实体id
     * @return 对应的key
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 根据用户id生成对应的key
     * 值为当前用户所收到的赞
     * @param userId 用户id
     * @return 对应的key
     */
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * 根据用户id和实体类型生成的key
     * 值为该用户关注的所有实体id的集合 （关注列表）
     * @param userId 用户id
     * @param entityType 实体类型
     * @return 代表一个用户关注的所有实体的key
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 根据实体类型和实体id生成的key
     * 值为所有粉丝的id集合 （粉丝列表）
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return 代表所有粉丝的id集合的key
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }
}
