package com.community_blog.dto;

import com.community_blog.domain.DiscussPost;
import com.community_blog.domain.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DiscussPostDto extends DiscussPost {
    /**
     * 发帖子的用户
     */
    private User user;

    /**
     * 帖子点赞数量
     */
    private long likeCount;

    /**
     * 帖子点赞状态
     * 1 - 已点赞
     */
    private int likeStatus;
}
