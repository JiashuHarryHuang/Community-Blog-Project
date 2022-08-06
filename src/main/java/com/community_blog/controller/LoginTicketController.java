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
import java.util.UUID;

import static com.community_blog.util.CommunnityConstant.DEFAULT_EXPIRED_SECONDS;
import static com.community_blog.util.CommunnityConstant.REMEMBER_EXPIRED_SECONDS;

/**
 * <p>
 *  前端控制器
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
     * @return 登录页面地址
     */
    @GetMapping("/login")
    public String getLoginPage() {
        return "/site/login";
    }

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

    @PostMapping("/login")
    public String login(Model model, UserDto userDto, HttpSession session, HttpServletResponse response) {
        log.info("登录操作");

        //查看用户是否存在
        String userName = userDto.getUsername();
        //select * from user where username = ?
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUsername, userName);
        User user = userService.getOne(userLambdaQueryWrapper);

        //数据回显
        model.addAttribute("user", userDto);

        if (user == null) {
            model.addAttribute("usernameMsg", "该账号不存在!");
            return "/site/login";
        }

        //查看用户是否激活
        if (user.getStatus() == 0) {
            model.addAttribute("usernameMsg", "账户未激活!");
            return "/site/login";
        }

        //验证密码
        String password = userDto.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!user.getPassword().equals(password)) {
            model.addAttribute("passwordMsg", "密码不正确!");
            return "/site/login";
        }

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
        response.addCookie(cookie);
        return "redirect:/discussPost/index";
    }

    @GetMapping("/logout")
    public String logout(Model model, @CookieValue("ticket") String ticket) {
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
