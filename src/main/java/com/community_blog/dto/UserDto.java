package com.community_blog.dto;

import com.community_blog.domain.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户数据传输对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDto extends User {
    /**
     * 验证码
     */
    private String verifycode;

    /**
     * 记住我
     */
    private boolean rememberme;

    /**
     * 未读消息数量
     */
    private int unreadMessageCount;
}
