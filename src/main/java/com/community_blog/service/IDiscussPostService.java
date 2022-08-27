package com.community_blog.service;

import com.community_blog.domain.DiscussPost;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community_blog.dto.DiscussPostDto;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Harry
 * @since 2022-07-26
 */
public interface IDiscussPostService extends IService<DiscussPost> {

    /**
     * 把点赞的用户id存入一个集合
     * @param userId 点赞的用户
     * @param entityType 点赞的是帖子还是评论
     * @param entityId 点赞的实体id
     */
    void like(int userId, int entityType, int entityId);

    /**
     * 查询某个实体的点赞数量
     * @param entityType 点赞的是帖子还是评论
     * @param entityId 点赞的实体id
     * @return 某个实体的点赞数量
     */
    long findEntityLikeCount(int entityType, int entityId);

    /**
     * 查询当前用户有没有点赞
     * 1 - 有
     * 0 - 没有
     * @param userId 用户id
     * @param entityType 点赞的是帖子还是评论
     * @param entityId 点赞的实体id
     * @return 当前用户有没有点赞
     */
    int findEntityLikeStatus(int userId, int entityType, int entityId);

    /**
     * 根据id获取帖子以及帖子的点赞数和点赞状态
     * @param id 帖子id
     * @return 帖子DTO对象
     */
    DiscussPostDto getByIdWithLike(int id);
}
