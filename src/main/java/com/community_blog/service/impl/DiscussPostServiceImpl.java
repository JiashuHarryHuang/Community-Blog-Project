package com.community_blog.service.impl;

import com.community_blog.domain.DiscussPost;
import com.community_blog.dao.DiscussPostDao;
import com.community_blog.service.IDiscussPostService;
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
public class DiscussPostServiceImpl extends ServiceImpl<DiscussPostDao, DiscussPost> implements IDiscussPostService {

}
