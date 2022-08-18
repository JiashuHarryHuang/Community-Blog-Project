package com.community_blog.service;

import com.community_blog.common.MyPage;
import com.community_blog.domain.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Harry
 * @since 2022-07-26
 */
public interface ICommentService extends IService<Comment> {
    /**
     * 分页查询评论
     * @param entityType 评论帖子还是用户
     * @param entityId 评论对象的id
     * @param page 分页查询对象
     */
    void findCommentsByEntity(int entityType, int entityId, MyPage<Comment> page);

    /**
     * 列表查询评论
     * @param entityType 评论帖子还是用户
     * @param entityId 评论对象的id
     * @return 评论列表
     */
    List<Comment> findCommentsByEntity(int entityType, int entityId);

    /**
     * 添加评论
     * @param comment 评论对象
     */
    void addComment(Comment comment);
}
