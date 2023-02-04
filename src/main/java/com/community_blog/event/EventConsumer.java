package com.community_blog.event;

import com.alibaba.fastjson.JSON;
import com.community_blog.domain.Event;
import com.community_blog.domain.Message;
import com.community_blog.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.community_blog.util.CommunityConstant.*;

/**
 * 消费者
 */
@Slf4j
@Component
public class EventConsumer {
    @Autowired
    private IMessageService messageService;

    /**
     * 处理事件的方法
     * @param record 封装了事件的数据
     */
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            log.error("消息的内容为空!");
            return;
        }

        //获取消息内的Event对象
        Event event = JSON.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error("消息格式错误!");
            return;
        }

        // 发送系统通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());

        // 构建通知的内容
        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        //以key-value的形式存储通知内容以及数据
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSON.toJSONString(content));
        messageService.save(message);
    }
}
