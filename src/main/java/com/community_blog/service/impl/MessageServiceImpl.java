package com.community_blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community_blog.common.MyPage;
import com.community_blog.domain.Message;
import com.community_blog.dao.MessageDao;
import com.community_blog.service.IMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MessageServiceImpl extends ServiceImpl<MessageDao, Message> implements IMessageService {

    @Autowired
    private MessageDao messageDao;

    /**
     * 分页查询最新消息列表
     * @param userId 当前用户id
     * @param page 分页查询对象
     */
    @Override
    public void selectConversations(int userId, MyPage<Message> page) {
        page.setTotal(messageDao.selectConversationCount(userId));
        List<Message> messages = messageDao.selectConversations(userId,
                (int) page.getCurrent() - 1, (int) page.getSize());

        page.setRecords(messages);
    }

    /**
     * 分页查询某个会话的所有私信
     * @param conversationId 会话id
     * @param page 分页对象
     */
    @Override
    public void selectMessages(String conversationId, MyPage<Message> page) {
        page.setTotal(messageDao.selectMessagesCount(conversationId));
        List<Message> messages = messageDao.selectMessages(conversationId,
                (int) page.getCurrent() - 1, (int) page.getSize());

        page.setRecords(messages);
    }

    /**
     * 获取某个会话的私信总数
     * @param conversationId 会话id
     * @return 私信总数
     */
    @Override
    public int selectMessagesCount(String conversationId) {
        return messageDao.selectMessagesCount(conversationId);
    }

    /**
     * 获取某个会话的未读消息数量
     * @param userId 当前用户
     * @param conversationId 会话id
     * @return 未读消息数量
     */
    @Override
    public int selectUnreadMessageCount(int userId, String conversationId) {
        return messageDao.selectUnreadMessageCount(userId, conversationId);
    }

    /**
     * 更新消息状态为已读
     * @param messages 需要更新的消息集合
     */
    @Override
    public void readMessages(List<Message> messages) {
        messages.forEach(message -> message.setStatus(1));
        this.updateBatchById(messages);
    }
}
