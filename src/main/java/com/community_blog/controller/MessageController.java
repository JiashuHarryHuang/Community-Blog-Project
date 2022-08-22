package com.community_blog.controller;


import com.community_blog.DTO.MessageDto;
import com.community_blog.annotation.LoginRequired;
import com.community_blog.common.MyPage;
import com.community_blog.dao.MessageDao;
import com.community_blog.domain.Message;
import com.community_blog.domain.User;
import com.community_blog.service.IMessageService;
import com.community_blog.service.IUserService;
import com.community_blog.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IUserService userService;

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
}

