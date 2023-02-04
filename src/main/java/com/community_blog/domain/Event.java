package com.community_blog.domain;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 封装事件信息
 */
@Getter
@ToString
public class Event implements Serializable {
    /**
     * 评论、点赞、关注
     */
    private String topic;

    /**
     * 当前登录用户id
     */
    private int userId;

    /**
     * 触发事件的是帖子、评论还是用户
     */
    private int entityType;

    /**
     * 帖子/评论id
     */
    private int entityId;

    /**
     * 发布帖子/评论的用户id
     */
    private int entityUserId;

    /**
     * 线程之间发送的数据
     */
    private Map<String, Object> data = new HashMap<>();

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
