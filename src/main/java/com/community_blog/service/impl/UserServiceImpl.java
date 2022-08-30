package com.community_blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community_blog.domain.User;
import com.community_blog.dao.UserDao;
import com.community_blog.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.community_blog.util.CommunityConstant.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Harry
 * @since 2022-07-26
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements IUserService {
    @Autowired
    private UserDao userDao;

    /**
     * 激活方法
     *
     * @param userId 用户id
     * @param code   激活码
     * @return 激活状态
     */
    @Override
    public int activation(int userId, String code) {
        User user = this.getById(userId);
        if (user == null) {
            return ACTIVATION_FAILURE;
        }

        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (code.equals(user.getActivationCode())) {
            user.setStatus(1);
            this.updateById(user);
            return ACTIVATION_SUCCESS;
        }

        return ACTIVATION_FAILURE;
    }

    @Override
    @Cacheable(value = "user", key="#userId", unless = "#result==null")
    public User getById(Serializable userId) {
        return userDao.selectById(userId);
    }

    @Override
    @CacheEvict(value = "user", key="#user.id")
    public boolean updateById(User user) {
        userDao.updateById(user);
        return true;
    }

    /**
     * 验证用户信息
     * @param user 用户对象
     * @return 验证结果
     */
    @Override
    public Map<String, String> register(User user) {
        Map<String, String> messages = new HashMap<>();

        //判断用户名是否已存在
        String username = user.getUsername();
        //Select count(*) from user where username = ?
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUsername, username);
        int count = this.count(userLambdaQueryWrapper);

        if (count != 0) { //用户名已存在
            messages.put("usernameMsg", "用户名已存在");
        }

        //判断email是否已被注册
        String email = user.getEmail();
        //Select count(*) from user where username = ? or email = ?
        userLambdaQueryWrapper.or().eq(User::getEmail, email);
        count = this.count(userLambdaQueryWrapper);
        if (count != 0) {
            messages.put("emailMsg", "该邮箱已被注册!");
        }

        return messages;
    }

    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUsername, username)
                .eq(User::getStatus, 1);
        return this.getOne(userLambdaQueryWrapper);
    }
}
