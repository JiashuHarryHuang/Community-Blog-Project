package com.community_blog.service;

import com.community_blog.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Harry
 * @since 2022-07-26
 */
public interface IUserService extends IService<User> {
    /**
     * 激活方法
     * @param userId 用户id
     * @param code 激活码
     * @return 激活状态
     */
    int activation(int userId, String code);
}