package com.community_blog.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community_blog.domain.User;
import com.community_blog.service.IUserService;
import com.community_blog.util.MailClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Random;
import java.util.UUID;

import static com.community_blog.util.CommunnityConstant.ACTIVATION_REPEAT;
import static com.community_blog.util.CommunnityConstant.ACTIVATION_SUCCESS;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Harry
 * @since 2022-07-26
 */
@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    @PostMapping("/register")
    public String register(Model model, @ModelAttribute User user) {
        log.info("Registering: {}", user.toString());

        //判断用户名是否已存在
        String username = user.getUsername();
        //Select count(*) from user where username = ?
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUsername, username);
        int count = userService.count(userLambdaQueryWrapper);
        if (count != 0) { //用户名已存在
            model.addAttribute("usernameMsg", "用户名已存在");
            model.addAttribute("user", user);
            return "/site/register";
        }

        //判断email是否已被注册
        String email = user.getEmail();
        //Select count(*) from user where username = ? or email = ?
        userLambdaQueryWrapper.or().eq(User::getEmail, email);
        count = userService.count(userLambdaQueryWrapper);
        if (count != 0) {
            model.addAttribute("emailMsg", "该邮箱已被注册!");
            model.addAttribute("user", user);
            return "/site/register";
        }

        //对密码进行加密
        String password = user.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        user.setPassword(password);

        //补充数据
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setStatus(0);
        user.setType(0);
        user.setActivationCode(UUID.randomUUID().toString());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));

        //保存用户
        userService.save(user);

        //发送数据回前端
        model.addAttribute("msg", "注册成功,我们已经向您的邮箱发送了一封激活邮件,请尽快激活!");
        model.addAttribute("target", "/index");

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email", email);
        String url = domain + "/user/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        model.addAttribute("msg", "注册成功,我们已经向您的邮箱发送了一封激活邮件,请尽快激活!");
        model.addAttribute("target", "/discussPost/index");

        return "/site/operate-result";
    }

    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable int userId, @PathVariable String code) {
        log.info("激活email");
        int result = userService.activation(userId, code);

        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target", "/user/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作,该账号已经激活过了!");
            model.addAttribute("target", "/discussPost/index");
        } else {
            model.addAttribute("msg", "激活失败,您提供的激活码不正确!");
            model.addAttribute("target", "/discussPost/index");
        }
        return "/site/operate-result";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "/site/login";
    }

}

