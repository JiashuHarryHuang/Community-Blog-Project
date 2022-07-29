package com.community_blog.service.impl;

import com.community_blog.domain.LoginTicket;
import com.community_blog.dao.LoginTicketDao;
import com.community_blog.service.ILoginTicketService;
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
public class LoginTicketServiceImpl extends ServiceImpl<LoginTicketDao, LoginTicket> implements ILoginTicketService {

}
