package com.community_blog.controller;


import com.community_blog.annotation.LoginRequired;
import com.community_blog.domain.Comment;
import com.community_blog.domain.DiscussPost;
import com.community_blog.domain.Event;
//import com.community_blog.event.EventProducer;
import com.community_blog.service.ICommentService;
import com.community_blog.service.IDiscussPostService;
import com.community_blog.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import static com.community_blog.util.CommunityConstant.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Harry
 * @since 2022-07-26
 */
@Controller
@RequestMapping("/comment")
@Slf4j
public class CommentController {
    @Autowired
    private ICommentService commentService;

    @Autowired
    private IDiscussPostService discussPostService;

//    @Autowired
//    private EventProducer eventProducer;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 添加评论
     * @param postId 帖子id
     * @param current 当前页
     * @param comment 评论
     * @return 帖子页面
     */
    @LoginRequired
    @PostMapping("/add/{postId}/{current}")
    public String addComment(@PathVariable("postId") int postId,
                             @PathVariable("current") Long current, Comment comment) {
        log.info("添加评论");
        comment.setUserId(hostHolder.getUser().getId());

        // 触发评论事件
//        Event event = new Event()
//                .setTopic(TOPIC_COMMENT)
//                .setUserId(hostHolder.getUser().getId())
//                .setEntityType(comment.getEntityType())
//                .setEntityId(comment.getEntityId())
//                .setData("postId", postId);
//        if (comment.getEntityType() == ENTITY_TYPE_POST) {
//            DiscussPost target = discussPostService.getById(comment.getEntityId());
//            event.setEntityUserId(target.getUserId());
//        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
//            Comment target = commentService.getById(comment.getEntityId());
//            event.setEntityUserId(target.getUserId());
//        }
        commentService.addComment(comment);
//        eventProducer.fireEvent(event);

        return "redirect:/discussPost/detail/" + postId + "?current=" + current;
    }
}

