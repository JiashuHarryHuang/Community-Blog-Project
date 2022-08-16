package com.community_blog.DTO;

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
}
