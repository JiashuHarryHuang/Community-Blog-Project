package com.community_blog.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community_blog.annotation.LoginRequired;
import com.community_blog.common.Result;
import com.community_blog.domain.DiscussPost;
import com.community_blog.domain.User;
import com.community_blog.service.IDiscussPostService;
import com.community_blog.service.IUserService;
import com.community_blog.util.HostHolder;
import com.community_blog.util.SensitiveFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import com.community_blog.common.MyPage;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Harry
 * @since 2022-07-26
 */
@Controller
@RequestMapping("/discussPost")
@Slf4j
public class DiscussPostController {
    /**
     * 默认第一页
     */
    public final long DEFAULT_CURRENT = 1;

    /**
     * 默认每页显示十行
     */
    public final long DEFAULT_SIZE = 10;

    /**
     * 注入discussPostService
     */
    @Autowired
    private IDiscussPostService discussPostService;

    /**
     * 注入userService
     */
    @Autowired
    private IUserService userService;

    /**
     * ThreadLocal
     */
    @Autowired
    private HostHolder hostHolder;

    /**
     * 敏感词过滤
     */
    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * 分页查询帖子表和用户信息，并将数据发回给前端
     * @param model 模板引擎对象
     * @param current 当前页
     * @param size 每页显示行数
     * @return 模板位置
     */
    @GetMapping("/index")
    public String getIndexPage(Model model, Long current, Long size) {
        log.info("分页查询");

        //初始化page对象
        MyPage<DiscussPost> postPage = new MyPage<>();
        //前端如果不传这些参数，则设置成默认值
        postPage.setCurrent(Objects.requireNonNullElse(current, DEFAULT_CURRENT));
        postPage.setSize(Objects.requireNonNullElse(size, DEFAULT_SIZE));
        postPage.setPath("/discussPost/index");

        //分页查询
        LambdaQueryWrapper<DiscussPost> postWrapper = new LambdaQueryWrapper<>();
        //select * from discuss_post where status != 2 order by type desc, create_time desc limit current-1, size;
        postWrapper.ne(DiscussPost::getStatus, 2)
                .orderByDesc(DiscussPost::getType)
                .orderByDesc(DiscussPost::getCreateTime);
        discussPostService.page(postPage, postWrapper);

        //包含了User对象和DiscussPost对象，可以看成DiscussPostDTO
        List<Map<String, Object>> discussPosts = null;
        if (postPage.getRecords() != null) {
            //处理帖子map集合，将发帖子的用户封装进去
            discussPosts = postPage.getRecords().stream().map(post -> {
                Map<String, Object> postMap = new HashMap<>();
                postMap.put("post", post);

                //获取发帖子的用户
                User user = userService.getById(post.getUserId());
                postMap.put("user", user);
                return postMap;
            }).toList();
        }

        //将数据发回给前端
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("page", postPage);
        return "/index";
    }

    /**
     * 发布帖子
     * @param discussPost 帖子标题和内容
     * @return 成功/失败信息
     */
    @PostMapping("/add")
    @ResponseBody
    @LoginRequired
    public String sendPost(DiscussPost discussPost) {
        log.info("发送帖子");

        //过滤敏感词
        String filteredTitle = sensitiveFilter.filter(discussPost.getTitle());
        String filteredContent = sensitiveFilter.filter(discussPost.getContent());

        //获取当前登录用户
        User user = hostHolder.getUser();
        if (user == null) {
            return JSON.toJSONString(Result.error("你还没有登录哦!"));
        }

        //设置用户
        discussPost.setUserId(user.getId());
        discussPost.setContent(filteredContent);
        discussPost.setTitle(filteredTitle);

        //添加用户到数据库
        discussPostService.save(discussPost);

        //返回成功信息
        return JSON.toJSONString(Result.success("发布成功!"));

    }

    @GetMapping("/detail/{id}")
    public String getDiscussPost(@PathVariable int id, Model model) {
        log.info("获取帖子详情：{}", id);
        DiscussPost discussPost = discussPostService.getById(id);
        model.addAttribute("post", discussPost);

        User user = userService.getById(discussPost.getUserId());
        model.addAttribute("user", user);

        return "/site/discuss-detail";
    }
}