package com.community_blog.dao;

import com.community_blog.domain.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Harry
 * @since 2022-07-26
 */
@Mapper
public interface CommentDao extends BaseMapper<Comment> {

}
