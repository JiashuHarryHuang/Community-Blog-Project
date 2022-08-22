package com.community_blog.dao;

import com.community_blog.domain.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Harry
 * @since 2022-07-26
 */
@Mapper
public interface MessageDao extends BaseMapper<Message> {
    /**
     * 查询消息列表，每个会话只返回一条最新的私信.
     * @param userId 当前用户
     * @param current 当前页
     * @param size 每页显示几条数据
     * @return 最新消息列表
     */
    List<Message> selectConversations(int userId, int current, int size);

    /**
     * 查询消息列表的数量
     * @param userId 当前用户
     * @return 消息列表的数量
     */
    int selectConversationCount(int userId);

    /**
     * 查询某个会话的所有消息
     * @param conversationId 会话id
     * @param current 当前页
     * @param size 每页显示几条数据
     * @return 某个会话的所有消息
     */
    List<Message> selectMessages(String conversationId, int current, int size);

    /**
     * 查询某个会话的消息数量
     * @param conversationId 会话id
     * @return 当前会话消息数量
     */
    int selectMessagesCount(String conversationId);

    /**
     * 查询某个会话/所有会话的未读消息数量
     * @param userId 当前用户
     * @param conversationId 会话id
     * @return 未读消息数量
     */
    int selectUnreadMessageCount(int userId, String conversationId);
}
