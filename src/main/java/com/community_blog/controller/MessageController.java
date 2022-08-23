package com.community_blog.controller;


import com.alibaba.fastjson.JSON;
import com.community_blog.DTO.MessageDto;
import com.community_blog.annotation.LoginRequired;
import com.community_blog.common.MyPage;
import com.community_blog.common.Result;
import com.community_blog.domain.Message;
import com.community_blog.domain.User;
import com.community_blog.service.IMessageService;
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
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Harry
 * @since 2022-07-26
 */
@Controller
@RequestMapping("/message")
@Slf4j
public class MessageController {

    @Value("${community.path.messagePage}")
    private String messagePage;

    @Value("${community.path.messageDetailPage}")
    private String messageDetailPage;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IUserService userService;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * 获取消息列表
     * @param model 模板引擎
     * @param page 分页对象
     * @return 消息列表路径
     */
    @GetMapping("/page")
    @LoginRequired
    public String getMessagePage(Model model, MyPage<Message> page) {
        log.info("消息列表");

        //获取登录用户对象
        User user = hostHolder.getUser();
        int userId = user.getId();

        //给设置分页对象
        page.setSize(5);
        page.setPath(messagePage);

        //分页查询最新消息
        messageService.selectConversations(userId, page);

        MyPage<MessageDto> conversationPage = new MyPage<>();
        BeanUtils.copyProperties(page, conversationPage, "records");

        List<MessageDto> messageDtoList = page.getRecords().stream().map(message -> {
            //初始化dto对象
            MessageDto messageDto = new MessageDto();
            BeanUtils.copyProperties(message, messageDto);
            String conversationId = messageDto.getConversationId();

            //给DTO属性赋值
            messageDto.setMessageCount(messageService.selectMessagesCount(conversationId));
            messageDto.setUnreadCount(messageService.selectUnreadMessageCount(userId, conversationId));
            int targetId = Objects.equals(userId, messageDto.getFromId())
                    ? messageDto.getToId() : messageDto.getFromId();
            messageDto.setTarget(userService.getById(targetId));

            return messageDto;

        }).toList();

        conversationPage.setRecords(messageDtoList);
        model.addAttribute("page", conversationPage);

        // 查询总未读消息数量
        int letterUnreadCount = messageService.selectUnreadMessageCount(userId, null);
        model.addAttribute("unreadMesageCount", letterUnreadCount);

        return "/site/letter";
    }

    /**
     * 获取私信详情
     * @param conversationId 会话id
     * @param model 模板引擎
     * @param page 分页对象
     * @return 私信详情页
     */
    @GetMapping("/detail/{conversationId}")
    @LoginRequired
    public String getMessageDetail(@PathVariable String conversationId, Model model, MyPage<Message> page) {
        log.info("获取私信详情");

        //设置分页
        page.setSize(5);
        page.setPath(messageDetailPage);

        //获取私信详情
        messageService.selectMessages(conversationId, page);
        //MessageDto分页对象
        MyPage<MessageDto> messagePage = new MyPage<>();
        BeanUtils.copyProperties(page, messagePage, "records");

        //对records进行处理然后赋值到messagePage上
        List<Message> messageList = page.getRecords();
        List<MessageDto> messageDtoList = messageList.stream().map(message -> {
            MessageDto messageDto = new MessageDto();
            BeanUtils.copyProperties(message, messageDto);

            //根据fromId查询fromUser
            User fromUser = userService.getById(message.getFromId());
            messageDto.setFromUser(fromUser);

            return messageDto;
        }).toList();
        messagePage.setRecords(messageDtoList);

        //传给前端
        model.addAttribute("page", messagePage);
        model.addAttribute("target", getMessageTarget(conversationId));

        //更新已读
        if(!messageList.isEmpty()) {
            messageService.readMessages(messageList);
        }

        return "/site/letter-detail";
    }

    /**
     * 获取聊天对象
     * @param conversationId 会话id
     * @return 聊天对象
     */
    private User getMessageTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId() == id0) {
            return userService.getById(id1);
        } else {
            return userService.getById(id0);
        }
    }

    /**
     * 发送私信
     * @param toName 发送对象用户名
     * @param content 发送内容
     * @return 发送成功/失败信息
     */
    @PostMapping("/send")
    @ResponseBody
    @LoginRequired
    public String sendLetter(String toName, String content) {
        log.info("发送私信");

        User target = userService.getUserByUsername(toName);
        if (target == null) {
            return JSON.toJSONString(Result.error("该用户不存在! "));
        }

        //对信息对象进行设置
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }

        //过滤敏感词
        content = HtmlUtils.htmlEscape(sensitiveFilter.filter(content));
        message.setContent(content);
        messageService.save(message);

        return JSON.toJSONString(Result.success("发送成功"));
    }
}

