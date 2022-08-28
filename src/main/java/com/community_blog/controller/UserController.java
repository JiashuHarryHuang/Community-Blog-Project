package com.community_blog.controller;

import com.community_blog.annotation.LoginRequired;
import com.community_blog.domain.User;
import com.community_blog.service.IDiscussPostService;
import com.community_blog.service.IUserService;
import com.community_blog.util.HostHolder;
import com.community_blog.util.MailClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
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

    @Autowired
    private IDiscussPostService discussPostService;

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
     * 图片存放地址
     */
    @Value("${community.path.img}")
    private String uploadPath;

    @Autowired
    private HostHolder hostHolder;

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
    @LoginRequired
    public String getSettingPage() {
        return "/site/setting";
    }

    /**
     * 上传图片功能
     * @param headerImage 上传的图片文件
     * @param model 模板
     * @return 回到首页
     */
    @PostMapping("/upload")
    @LoginRequired
    public String upload(MultipartFile headerImage, Model model) {
        log.info("上传图片");

        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }

        //获取图片名
        String fileName = headerImage.getOriginalFilename();
        assert fileName != null;
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (!(".jpg".equals(suffix)
                || ".png".equals(suffix)
                || "jpeg".equals(suffix)
                || ".gif".equals(suffix))) {
            model.addAttribute("error", "文件的格式不正确!");
            return "/site/setting";
        }

        // 生成随机文件名
        fileName = UUID.randomUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            log.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }

        // 更新当前用户的头像的路径(web访问路径)
        // http://localhost:8080/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + "/user/header/" + fileName;
        user.setHeaderUrl(headerUrl);
        userService.updateById(user);

        return "redirect:/discussPost/index";
    }

    /**
     * 获取头像图片
     * @param fileName 图片名
     * @param response 响应对象
     */
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (//放置资源
                //创建输入流和输出流
                FileInputStream fis = new FileInputStream(fileName);
                ServletOutputStream os = response.getOutputStream();
        ) {
            //复制输入流到输出流：将服务器存储的图片展示到网页上
            IOUtils.copy(fis, os);
        } catch (IOException e) {
            log.error("读取头像失败: " + e.getMessage());
        }
    }

    /**
     * 个人主页
     * @param userId 用户id
     * @param model 模板引擎
     * @return 个人主页页面
     */
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }

        // 用户
        model.addAttribute("user", user);
        // 点赞数量
        int likeCount = discussPostService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        return "/site/profile";
    }
}