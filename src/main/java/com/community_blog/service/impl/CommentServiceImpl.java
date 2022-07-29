package com.community_blog.service.impl;

import com.community_blog.domain.Comment;
import com.community_blog.dao.CommentDao;
import com.community_blog.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
