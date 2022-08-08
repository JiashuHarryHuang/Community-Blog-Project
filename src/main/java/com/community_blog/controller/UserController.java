package com.community_blog.controller;

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

import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static com.community_blog.util.CommunnityConstant.*;

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

    /**
     * 发送邮箱工具
     */
    @Autowired
    private MailClient mailClient;

    /**
     * 模板引擎
     */
    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 域名
     */
    @Value("${community.path.domain}")
    private String domain;

    /**
     * 跳转至注册页面
     * @return 注册页面地址
     */
    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    /**
     * 注册功能
     * @param model 模板渲染工具
     * @param user 用户数据
     * @return 注册成功/失败的地址
     */
    @PostMapping("/register")
    public String register(Model model, @ModelAttribute User user) {
        log.info("Registering: {}", user.toString());

        //数据回显
        model.addAttribute("user", user);

        //验证数据
        Map<String, String> messages = userService.register(user);

        //将错误结果回显
        String usernameMsg = messages.get("usernameMsg");
        String emailMsg = messages.get("emailMsg");
        if (usernameMsg != null) {
            model.addAttribute("usernameMsg", usernameMsg);
            return "/site/register";
        }
        if (emailMsg != null) {
            model.addAttribute("emailMsg", emailMsg);
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
        context.setVariable("email", user.getEmail()); //传数据给模板
        String url = domain + "/user/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        //根据模板的格式发送邮件
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        //设置用户提示信息，并让用户跳转回首页页面，等待用户激活邮箱
        model.addAttribute("msg", "注册成功,我们已经向您的邮箱发送了一封激活邮件,请尽快激活!");
        model.addAttribute("target", "/discussPost/index");

        return "/site/operate-result";
    }

    /**
     * 激活email方法
     * @param model 模板渲染工具
     * @param userId user id
     * @param code 激活码
     * @return 激活成功/失败页面
     */
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable int userId, @PathVariable String code) {
        log.info("激活email");
        int result = userService.activation(userId, code);

        if (result == ACTIVATION_SUCCESS) {
            //激活成功，跳转登陆页面
            model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target", "/loginTicket/login");
        } else if (result == ACTIVATION_REPEAT) {
            //激活重复，跳转首页
            model.addAttribute("msg", "无效操作,该账号已经激活过了!");
            model.addAttribute("target", "/discussPost/index");
        } else {
            //激活失败，跳转首页
            model.addAttribute("msg", "激活失败,您提供的激活码不正确!");
            model.addAttribute("target", "/discussPost/index");
        }
        return "/site/operate-result";
    }

    @GetMapping("/setting")
    public String getSettingPage() {
        return "/site/setting";
    }
}

