package com.community_blog.controller;

import com.alibaba.fastjson.JSON;
import com.community_blog.common.Result;
import com.community_blog.domain.Event;
import com.community_blog.domain.User;
//import com.community_blog.event.EventProducer;
import com.community_blog.service.IFollowService;
import com.community_blog.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.community_blog.util.CommunityConstant.TOPIC_FOLLOW;

@Controller
public class FollowController {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private IFollowService followService;

//    @Autowired
//    private EventProducer eventProducer;

    /**
     * 关注操作
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return 关注成功信息
     */
    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);

        // 触发关注事件
//        Event event = new Event()
//                .setTopic(TOPIC_FOLLOW)
//                .setUserId(hostHolder.getUser().getId())
//                .setEntityType(entityType)
//                .setEntityId(entityId)
//                .setEntityUserId(entityId);
//        eventProducer.fireEvent(event);

        return JSON.toJSONString(Result.success("已关注！"));
    }

    /**
     * 取关操作
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return 取关成功信息
     */
    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType, entityId);
        return JSON.toJSONString(Result.success("已取消关注！"));
    }
}
