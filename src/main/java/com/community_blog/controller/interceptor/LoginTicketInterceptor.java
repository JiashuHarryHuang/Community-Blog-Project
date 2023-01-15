package com.community_blog.controller.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community_blog.domain.LoginTicket;
import com.community_blog.domain.User;
import com.community_blog.dto.UserDto;
import com.community_blog.service.ILoginTicketService;
import com.community_blog.service.IMessageService;
import com.community_blog.service.IUserService;
import com.community_blog.util.CookieUtil;
import com.community_blog.util.HostHolder;
import com.community_blog.util.RedisKeyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IUserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private IMessageService messageService;

    /**
     * 验证用户是否持有登录凭证，如果有，则将用户对象加入ThreadLocal
     * @param request 请求
     * @param response 响应
     * @param handler handler
     * @return true
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从cookie中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");

        if (ticket != null) {
            // 查询凭证：从redis取
            String ticketKey = RedisKeyUtil.getTicketKey(ticket);
            LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
            // 检查凭证是否有效
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().isAfter(LocalDateTime.now())) {
                User user = userService.getById(loginTicket.getUserId());
                UserDto userDto = new UserDto();
                BeanUtils.copyProperties(user, userDto);

                int unreadMesageCount = messageService.selectUnreadMessageTotalCount(userDto.getId());
                userDto.setUnreadMessageCount(unreadMesageCount);
                // 将当前用户信息存入ThreadLocal
                hostHolder.setUser(userDto);
            }
        }

        return true;
    }

    /**
     * 控制器方法后将ThreadLocal里的用户对象加入模板引擎
     * @param request 请求
     * @param response 响应
     * @param handler handler
     * @param modelAndView 模板引擎
     * @throws Exception 异常
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    /**
     * 模板渲染后删除ThreadLocal里的用户对象
     * @param request 请求
     * @param response 响应
     * @param handler handler
     * @param ex 异常
     * @throws Exception 异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }

}
