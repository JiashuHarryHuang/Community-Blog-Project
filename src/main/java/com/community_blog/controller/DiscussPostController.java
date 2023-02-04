package com.community_blog.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community_blog.domain.Event;
import com.community_blog.dto.CommentDto;
import com.community_blog.dto.DiscussPostDto;
import com.community_blog.annotation.LoginRequired;
import com.community_blog.common.Result;
import com.community_blog.domain.Comment;
import com.community_blog.domain.DiscussPost;
import com.community_blog.domain.User;
import com.community_blog.event.EventProducer;
import com.community_blog.service.ICommentService;
import com.community_blog.service.IDiscussPostService;
import com.community_blog.service.IUserService;
import com.community_blog.util.HostHolder;
import com.community_blog.util.SensitiveFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import com.community_blog.common.MyPage;

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
@RequestMapping("/discussPost")
@Slf4j
public class DiscussPostController {
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
     * 帖子分页查询路径
     */
    @Value("${community.path.postPage}")
    private String postPagePath;

    /**
     * 评论分页查询路径
     */
    @Value("${community.path.commentPage}")
    private String commentPagePath;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private EventProducer eventProducer;

    /**
     * 分页查询帖子表和用户信息，并将数据发回给前端
     * @param model 模板引擎对象
     * @param page 接收前端传来的current和size，默认初始化成current=1, size=10
     * @return 模板位置
     */
    @GetMapping("/index")
    public String getIndexPage(Model model, MyPage<DiscussPost> page) {
        log.info("分页查询");

        //初始化page对象
        page.setPath(postPagePath);

        //分页查询
        LambdaQueryWrapper<DiscussPost> postWrapper = new LambdaQueryWrapper<>();
        //select * from discuss_post where status != 2 order by type desc, create_time desc limit current-1, size;
        postWrapper.ne(DiscussPost::getStatus, 2)
                .orderByDesc(DiscussPost::getType)
                .orderByDesc(DiscussPost::getCreateTime);
        discussPostService.page(page, postWrapper);

        MyPage<DiscussPostDto> discussPostPage = new MyPage<>();
        //复制对象
        BeanUtils.copyProperties(page, discussPostPage, "records");

        if (page.getRecords() != null) {
            List<DiscussPostDto> discussPostDtos = page.getRecords().stream().map(post -> {
                //给每个discussPostDto的user属性赋值
                DiscussPostDto discussPostDto = new DiscussPostDto();
                BeanUtils.copyProperties(post, discussPostDto);
                User user = userService.getById(post.getUserId());
                discussPostDto.setUser(user);

                //给likeCount赋值
                long likeCount = discussPostService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostDto.getId());
                discussPostDto.setLikeCount(likeCount);

                return discussPostDto;
            }).toList();

            discussPostPage.setRecords(discussPostDtos);
        }

        //将数据发回给前端
        model.addAttribute("page", discussPostPage);
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

        //设置用户并过滤敏感词
        discussPost.setUserId(user.getId());
        discussPost.setContent(filteredContent);
        discussPost.setTitle(filteredTitle);

        //添加用户到数据库
        discussPostService.save(discussPost);

        //返回成功信息
        return JSON.toJSONString(Result.success("发布成功!"));

    }

    /**
     * 获取帖子内容
     * @param postId 帖子id
     * @param model 模板引擎
     * @param page 接收前端的current和size对象
     * @return 帖子页面
     */
    @GetMapping("/detail/{postId}")
    public String getDiscussPost(@PathVariable int postId, Model model, MyPage<Comment> page) {
        log.info("获取帖子详情：{}", postId);
        //获取帖子
        DiscussPostDto discussPostDto = discussPostService.getByIdWithLike(postId);
        model.addAttribute("post", discussPostDto);

        //获取用户
        User user = userService.getById(discussPostDto.getUserId());
        model.addAttribute("user", user);

        //设置MyPage对象
        page.setSize(5L);
        page.setPath(commentPagePath + postId);

        //分页查询帖子评论
        commentService.findCommentsByEntity(ENTITY_TYPE_POST, discussPostDto.getId(), page);

        //数据传输对象
        MyPage<CommentDto> commentPage = new MyPage<>();
        //复制对象
        BeanUtils.copyProperties(page, commentPage, "records");

        //遍历page对象，然后给每个commentDto赋值，最后装配到一个List集合
        List<CommentDto> commentDtoList = page.getRecords().stream().map(comment -> {
            CommentDto commentDto = new CommentDto();
            BeanUtils.copyProperties(comment, commentDto);

            //设置发表评论的用户
            commentDto.setCommenter(userService.getById(commentDto.getUserId()));

            //设置评论点赞数量
            commentDto.setLikeCount(discussPostService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId()));

            //设置评论点赞状态
            commentDto.setLikeStatus(hostHolder.getUser() == null ? 0 :
                    discussPostService.findEntityLikeStatus(hostHolder.getUser().getId(),
                            ENTITY_TYPE_COMMENT, comment.getId()));

            //查询回复
            List<Comment> replies = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId());

            //遍历回复对象，对每个replyDto进行赋值
            List<CommentDto> repliesDto = replies.stream().map(reply -> {
                CommentDto replyDto = new CommentDto();
                BeanUtils.copyProperties(reply, replyDto);

                //设置发表回复的用户
                replyDto.setCommenter(userService.getById(reply.getUserId()));

                //设置回复的点赞数量
                replyDto.setLikeCount(discussPostService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId()));

                //设置评论点赞状态
                commentDto.setLikeStatus(hostHolder.getUser() == null ? 0 :
                        discussPostService.findEntityLikeStatus(hostHolder.getUser().getId(),
                                ENTITY_TYPE_COMMENT, reply.getId()));

                //设置回复的对象，如果id为0则设置成null
                 User target = reply.getTargetId() == 0 ? null : userService.getById(reply.getTargetId());
                replyDto.setTarget(target);

                return replyDto;
            }).toList();

            //设置回复集合
            commentDto.setReplies(repliesDto);

            return commentDto;

        }).toList();

        commentPage.setRecords(commentDtoList);

        //数据传回前端
        model.addAttribute("page", commentPage);

        return "/site/discuss-detail";
    }

    /**
     * 点赞功能
     * @param entityType 实体类型
     * @param entityId 实体id
     * @param entityUserId 发布帖子/评论的用户id
     * @return 点赞成功信息以及点赞数据
     */
    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        //获取登录用户
        User user = hostHolder.getUser();
        int userId = user.getId();

        //点赞
        discussPostService.like(userId, entityType, entityId, entityUserId);

        //查询点赞数
        long likeCount = discussPostService.findEntityLikeCount(entityType, entityId);

        //查询用户点赞状态
        int likeStatus = discussPostService.findEntityLikeStatus(userId, entityType, entityId);

        // 触发点赞事件
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }

        //返回数据给前端
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        return JSON.toJSONString(Result.success(map));
    }
}