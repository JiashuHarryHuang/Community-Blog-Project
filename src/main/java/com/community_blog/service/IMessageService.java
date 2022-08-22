package com.community_blog.service;

import com.community_blog.common.MyPage;
import com.community_blog.domain.Message;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Harry
 * @since 2022-07-26
 */
public interface IMessageService extends IService<Message> {
    /**
     * 分页查询最新消息列表
     * @param userId 当前用户id
     * @param page 分页查询对象
     */
    void selectConversations(int userId, MyPage<Message> page);

    int selectMessagesCount(String conversationId);

    int selectUnreadMessageCount(int userId, String conversationId);
}
