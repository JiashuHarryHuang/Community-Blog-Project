package com.community_blog.service;

import com.community_blog.dto.UserDto;
import com.community_blog.domain.LoginTicket;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Harry
 * @since 2022-07-26
 */
public interface ILoginTicketService extends IService<LoginTicket> {
    Map<String, Object> login(UserDto userDto);
}
