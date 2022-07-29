package com.community_blog.service.impl;

import com.community_blog.domain.Message;
import com.community_blog.dao.MessageDao;
import com.community_blog.service.IMessageService;
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
public class MessageServiceImpl extends ServiceImpl<MessageDao, Message> implements IMessageService {

}
