package com.community_blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community_blog.common.MyPage;
import com.community_blog.domain.Comment;
import com.community_blog.dao.CommentDao;
import com.community_blog.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Harry
 * @since 2022-07-26
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentDao, Comment> implements ICommentService {

    @Override
    public void findCommentsByEntity(int entityType, int entityId, MyPage<Comment> page) {
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper=  new LambdaQueryWrapper<>();
        //select * from comment where status = 0 and entity_type = ? and entity_id = ?
        // order by create_time asc limit current - 1, size
        commentLambdaQueryWrapper.eq(Comment::getStatus, 0)
                .eq(Comment::getEntityType, entityType)
                .eq(Comment::getEntityId, entityId)
                .orderByAsc(Comment::getCreateTime);
        this.page(page, commentLambdaQueryWrapper);
    }

    @Override
    public List<Comment> findCommentsByEntity(int entityType, int entityId) {
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper= new LambdaQueryWrapper<>();
        //select * from comment where status = 0 and entity_type = ? and entity_id = ?
        // order by create_time asc limit current - 1, size
        commentLambdaQueryWrapper.eq(Comment::getStatus, 0)
                .eq(Comment::getEntityType, entityType)
                .eq(Comment::getEntityId, entityId)
                .orderByAsc(Comment::getCreateTime);
        return this.list(commentLambdaQueryWrapper);
    }
}
