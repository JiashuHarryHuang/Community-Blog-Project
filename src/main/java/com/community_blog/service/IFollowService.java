package com.community_blog.service;

import org.springframework.stereotype.Service;

public interface IFollowService {
    /**
     * 关注操作
     * 1. 用户的关注列表中存入该实体的id
     * 2. 实体的粉丝列表中存入该用户的id
     * @param userId 用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     */
    void follow(int userId, int entityType, int entityId);

    /**
     * 取关操作
     * 1. 从该用户的关注列表中移除实体
     * 2. 从实体的粉丝列表中移除用户
     * @param userId 用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     */
    void unfollow(int userId, int entityType, int entityId);

    /**
     * 获取用户的关注数量
     * @param userId 用户id
     * @param entityType 实体类型
     * @return 用户的关注数量
     */
    long findFolloweeCount(int userId, int entityType);

    /**
     * 获取实体的粉丝数量
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return 实体的粉丝数量
     */
    long findFollowerCount(int entityType, int entityId);

    /**
     * 判断该用户是否关注了该实体
     * @param userId 用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return 判断该用户是否关注了该实体
     */
    boolean hasFollowed(int userId, int entityType, int entityId);
}
