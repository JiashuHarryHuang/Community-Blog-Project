package com.community_blog.controller;


import com.community_blog.annotation.LoginRequired;
import com.community_blog.domain.Comment;
import com.community_blog.service.ICommentService;
import com.community_blog.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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
    private HostHolder hostHolder;

    @LoginRequired
    @PostMapping("/add/{postId}/{current}")
    public String addComment(@PathVariable("postId") int postId, @PathVariable("current") Long current, Comment comment) {
        log.info("添加评论");
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        commentService.addComment(comment);

        return "redirect:/discussPost/detail/" + postId + "?current=" + current;
    }
}

