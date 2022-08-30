package com.community_blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community_blog.dto.UserDto;
import com.community_blog.domain.LoginTicket;
import com.community_blog.dao.LoginTicketDao;
import com.community_blog.domain.User;
import com.community_blog.service.ILoginTicketService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community_blog.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Harry
 * @since 2022-07-26
 */
@Service
public class LoginTicketServiceImpl extends ServiceImpl<LoginTicketDao, LoginTicket> implements ILoginTicketService {

    @Autowired
    private IUserService userService;

    /**
     * 验证登录信息
     * @param userDto 登录信息对象
     * @return 封装了验证结果和用户的对象
     */
    @Override
    public Map<String, Object> login(UserDto userDto) {
        Map<String, Object> result = new HashMap<>();
        //查看用户是否存在
        String userName = userDto.getUsername();
        User user = userService.getUserByUsername(userName);

        if (user == null) { //账号不存在
            result.put("usernameMsg", "该账号不存在!");
        }else if (user.getStatus() == 0) { //查看用户是否激活
            result.put("usernameMsg", "账户未激活!");
        } else {
            //验证密码
            String password = userDto.getPassword();
            password = DigestUtils.md5DigestAsHex(password.getBytes());
            if (!user.getPassword().equals(password)) {
                result.put("passwordMsg", "密码不正确!");
            }
        }

        //将查询的用户传给控制层
        result.put("user", user);

        return result;
    }
}
