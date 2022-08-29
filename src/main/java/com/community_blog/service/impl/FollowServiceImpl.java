package com.community_blog.service.impl;

import com.community_blog.service.IFollowService;
import com.community_blog.service.IUserService;
import com.community_blog.util.CommunityConstant;
import com.community_blog.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl implements IFollowService, CommunityConstant {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IUserService userService;

    /**
     * 关注操作
     * 1. 用户的关注列表中存入该实体的id
     * 2. 实体的粉丝列表中存入该用户的id
     * @param userId 用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     */
    @Override
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //获取用户的关注列表key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                //获取实体的粉丝列表key
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();

                //用户的关注列表中存入该实体的id
                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                //实体的粉丝列表中存入该用户的id
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                return operations.exec();
            }
        });
    }

    /**
     * 取关操作
     * 1. 从该用户的关注列表中移除实体
     * 2. 从实体的粉丝列表中移除用户
     * @param userId 用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     */
    @Override
    public void unfollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //获取用户的关注列表key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                //获取实体的粉丝列表key
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();

                //从该用户的关注列表中移除实体
                operations.opsForZSet().remove(followeeKey, entityId);
                //从实体的粉丝列表中移除用户
                operations.opsForZSet().remove(followerKey, userId);

                return operations.exec();
            }
        });
    }

    /**
     * 获取用户的关注数量
     * @param userId 用户id
     * @param entityType 实体类型
     * @return 用户的关注数量
     */
    @Override
    public long findFolloweeCount(int userId, int entityType) {
        String key = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 获取实体的粉丝数量
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return 实体的粉丝数量
     */
    @Override
    public long findFollowerCount(int entityType, int entityId) {
        String key = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 判断该用户是否关注了该实体
     * @param userId 用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return 判断该用户是否关注了该实体
     */
    @Override
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        //获取用户的关注列表
        String key = RedisKeyUtil.getFolloweeKey(userId, entityType);

        //查询该值的分数，如果查不出来则说明该值不存在
        return redisTemplate.opsForZSet().score(key, entityId) != null;
    }
}
