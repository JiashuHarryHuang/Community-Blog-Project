package com.community_blog.service.impl;

import com.community_blog.domain.User;
import com.community_blog.dao.UserDao;
import com.community_blog.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
