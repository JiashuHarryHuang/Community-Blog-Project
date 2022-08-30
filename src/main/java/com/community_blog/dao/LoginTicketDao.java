package com.community_blog.dao;

import com.community_blog.domain.LoginTicket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Harry
 * @since 2022-07-26
 */
@Deprecated //表示禁用这个数据层，以redis代替
public interface LoginTicketDao extends BaseMapper<LoginTicket> {

}
