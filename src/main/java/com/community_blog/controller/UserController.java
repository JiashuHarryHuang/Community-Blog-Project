package com.community_blog.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community_blog.domain.User;
import com.community_blog.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Random;
import java.util.UUID;

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
            model.addAttribute("emailMsg", "该邮箱已被注册");
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

        return "/site/operate-result";
    }
}

