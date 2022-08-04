package com.community_blog.service.impl;

import com.community_blog.domain.User;
import com.community_blog.dao.UserDao;
import com.community_blog.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import static com.community_blog.util.CommunnityConstant.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Harry
 * @since 2022-07-26
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements IUserService {

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
}
