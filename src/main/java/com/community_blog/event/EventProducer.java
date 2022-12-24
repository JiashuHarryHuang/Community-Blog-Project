//package com.community_blog.event;
//
//import com.alibaba.fastjson.JSON;
//import com.community_blog.domain.Event;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class EventProducer {
//    @Autowired
//    private KafkaTemplate kafkaTemplate;
//
//    // 处理事件
//    public void fireEvent(Event event) {
//        // 将事件发布到指定的主题
//        kafkaTemplate.send(event.getTopic(), JSON.toJSONString(event));
//    }
//}
