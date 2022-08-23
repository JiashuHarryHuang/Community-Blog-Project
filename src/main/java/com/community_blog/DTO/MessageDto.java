package com.community_blog.DTO;

import com.community_blog.domain.Message;
import com.community_blog.domain.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息/会话数据传输对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MessageDto extends Message {
    /**
     * 当前会话总消息数
     */
    private int messageCount;

    /**
     * 当前会话未读消息数
     */
    private int unreadCount;

    /**
     * 当前会话的接收者
     */
    private User target;

    /**
     * 当前会话的发送者
     */
    private User fromUser;
}
