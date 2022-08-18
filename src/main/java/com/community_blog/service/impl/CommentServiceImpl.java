package com.community_blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community_blog.common.MyPage;
import com.community_blog.domain.Comment;
import com.community_blog.dao.CommentDao;
import com.community_blog.domain.DiscussPost;
import com.community_blog.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community_blog.service.IDiscussPostService;
import com.community_blog.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

import static com.community_blog.util.CommunnityConstant.ENTITY_TYPE_POST;

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

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private IDiscussPostService discussPostService;

    /**
     * 分页查询评论
     * @param entityType 评论帖子还是用户
     * @param entityId 评论对象的id
     * @param page 分页查询对象
     */
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

    /**
     * 列表查询评论
     * @param entityType 评论帖子还是用户
     * @param entityId 评论对象的id
     * @return 评论列表
     */
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

    /**
     * 添加评论
     * @param comment 评论对象
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        this.save(comment);

        // 更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            //select count(*) from comment where entity_type = ? and entity_id = ?
            LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            commentLambdaQueryWrapper.eq(Comment::getEntityType, comment.getEntityType())
                    .eq(Comment::getEntityId, comment.getEntityId());
            int commentCount = this.count(commentLambdaQueryWrapper);

            //更新discuss_post表的评论数量
            DiscussPost discussPost = new DiscussPost();
            discussPost.setId(comment.getEntityId());
            discussPost.setCommentCount(commentCount);
            discussPostService.updateById(discussPost);
        }
    }
}
