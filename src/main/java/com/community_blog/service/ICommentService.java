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
    void findCommentsByEntity(int entityType, int entityId, MyPage<Comment> page);
    List<Comment> findCommentsByEntity(int entityType, int entityId);
}
