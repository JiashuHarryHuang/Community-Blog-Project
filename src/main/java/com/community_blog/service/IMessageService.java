package com.community_blog.service;

import com.community_blog.common.MyPage;
import com.community_blog.domain.Message;
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
public interface IMessageService extends IService<Message> {
    /**
     * 分页查询最新消息列表
     * @param userId 当前用户id
     * @param page 分页查询对象
     */
    void selectConversations(int userId, MyPage<Message> page);

    /**
     * 分页查询某个会话的所有私信
     * @param conversationId 会话id
     * @param page 分页对象
     */
    void selectMessages(String conversationId, MyPage<Message> page);

    /**
     * 获取某个会话的私信总数
     * @param conversationId 会话id
     * @return 私信总数
     */
    int selectMessagesCount(String conversationId);

    /**
     * 获取某个会话的未读消息数量
     * @param userId 当前用户
     * @param conversationId 会话id
     * @return 未读消息数量
     */
    int selectUnreadMessageCount(int userId, String conversationId);

    /**
     * 查询一个用户所有的未读消息数量
     * @param userId 用户id
     * @return 一个用户所有的未读消息数量
     */
    int selectUnreadMessageTotalCount(int userId);

    /**
     * 更新消息状态为已读
     * @param messages 需要更新的消息集合
     */
    void readMessages(List<Message> messages);

    Message selectLatestNotice(int userId, String topic);

    int selectNoticeCount(int userId, String topic);

    int selectNoticeUnreadCount(int userId, String topic);
}
