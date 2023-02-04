package com.community_blog.event;

import com.alibaba.fastjson.JSON;
import com.community_blog.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 生产者
 */
@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 当触发点赞、关注、评论的时候该方法会被调用
     * @param event 封装了事件信息
     */
    public void fireEvent(Event event) {
        // 将事件发布到指定的主题
        kafkaTemplate.send(event.getTopic(), JSON.toJSONString(event));
    }
}
