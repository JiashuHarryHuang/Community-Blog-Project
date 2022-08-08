package com.community_blog.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community_blog.DTO.UserDto;
import com.community_blog.domain.LoginTicket;
import com.community_blog.domain.User;
import com.community_blog.service.ILoginTicketService;
import com.community_blog.service.IUserService;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static com.community_blog.util.CommunnityConstant.DEFAULT_EXPIRED_SECONDS;
import static com.community_blog.util.CommunnityConstant.REMEMBER_EXPIRED_SECONDS;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Harry
 * @since 2022-07-26
 */
@Controller
@RequestMapping("/loginTicket")
@Slf4j
public class LoginTicketController {
    @Autowired
    private IUserService userService;

    /**
     * 验证码工具
     */
    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private ILoginTicketService loginTicketService;

    /**
     * 跳转至登录页面
     *
     * @return 登录页面地址
     */
    @GetMapping("/login")
    public String getLoginPage() {
        return "/site/login";
    }

    /**
     * 生成验证码
     *
     * @param response 响应数据
     * @param session  会话
     */
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //把验证码存入session
        session.setAttribute("code", text);

        // 将图片返回给浏览器
        response.setContentType("image/png");

        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            log.error("响应验证码失败:" + e.getMessage());
        }
    }

    /**
     * 登录操作
     *
     * @param model    模板
     * @param userDto  前端数据封装对象
     * @param session  会话
     * @param response 响应
     * @return 登录成功/失败页面
     */
    @PostMapping("/login")
    public String login(Model model, UserDto userDto, HttpSession session, HttpServletResponse response) {
        log.info("登录操作");

        //数据回显
        model.addAttribute("user", userDto);

        //验证数据
        Map<String, Object> result = loginTicketService.login(userDto);
        String usernameMsg = (String) result.get("usernameMsg");
        String passwordMsg = (String) result.get("passwordMsg");

        //验证结果回显
        if (usernameMsg != null) {
            model.addAttribute("usernameMsg", usernameMsg);
            return "/site/login";
        }
        if (passwordMsg != null) {
            model.addAttribute("passwordMsg", passwordMsg);
            return "/site/login";
        }

        User user = (User) result.get("user");

        //验证验证码
        String code = (String) session.getAttribute("code");
        String verifycode = userDto.getVerifycode();
        if (!code.equalsIgnoreCase(verifycode)) {
            model.addAttribute("verifycodeMsg", "验证码不正确!");
            return "/site/login";
        }

        //记住我
        int expiredTime = userDto.isRememberme() ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;

        //生成登陆凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(String.valueOf(UUID.randomUUID()));
        loginTicket.setStatus(0);
        loginTicket.setExpired(LocalDateTime.now().plusSeconds(expiredTime));
        loginTicketService.save(loginTicket);

        //生成cookie
        Cookie cookie = new Cookie("ticket", loginTicket.getTicket());
        cookie.setMaxAge(expiredTime);
        //设置路径：让cookie可以被所有路径访问
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/discussPost/index";
    }

    /**
     * 登出操作
     *
     * @param ticket cookie里的登录凭证
     * @return 登陆页面
     */
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        log.info("登出操作");

        //更新状态：update login_ticket set status = 1 where ticket = ?
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket(ticket);
        loginTicket.setStatus(1);
        LambdaQueryWrapper<LoginTicket> loginTicketLambdaQueryWrapper = new LambdaQueryWrapper<>();
        loginTicketLambdaQueryWrapper.eq(LoginTicket::getTicket, ticket);

        loginTicketService.update(loginTicket, loginTicketLambdaQueryWrapper);

        return "redirect:/loginTicket/login";
    }
}