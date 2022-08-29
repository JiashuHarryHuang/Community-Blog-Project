package com.community_blog.service.impl;

import com.community_blog.domain.DiscussPost;
import com.community_blog.dao.DiscussPostDao;
import com.community_blog.dto.DiscussPostDto;
import com.community_blog.service.IDiscussPostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community_blog.util.HostHolder;
import com.community_blog.util.RedisKeyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import static com.community_blog.util.CommunityConstant.ENTITY_TYPE_POST;

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
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 把点赞的用户id存入一个集合
     * @param userId 点赞的用户
     * @param entityType 点赞的是帖子还是评论
     * @param entityId 点赞的实体id
     * @param entityUserId 发布帖子/评论的用户id
     */
    @Override
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);

                //判断当前用户有没有点过赞
                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);

                operations.multi();

                if (isMember) {
                    //点过赞，则取消点赞，将userId删除
                    operations.opsForSet().remove(entityLikeKey, userId);
                    //并对该实体用户的点赞数减一
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    //没点赞，则把userId加入集合
                    operations.opsForSet().add(entityLikeKey, userId);
                    //并对该实体用户的点赞数加一
                    operations.opsForValue().increment(userLikeKey);
                }

                return operations.exec();
            }
        });

    }

    /**
     * 查询某个实体的点赞数量
     * @param entityType 点赞的是帖子还是评论
     * @param entityId 点赞的实体id
     * @return 某个实体的点赞数量
     */
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * 查询当前用户有没有点赞
     * 1 - 有
     * 0 - 没有
     * @param userId 用户id
     * @param entityType 点赞的是帖子还是评论
     * @param entityId 点赞的实体id
     * @return 当前用户有没有点赞
     */
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    /**
     * 根据id获取帖子以及帖子的点赞数和点赞状态
     * @param id 帖子id
     * @return 帖子DTO对象
     */
    @Override
    public DiscussPostDto getByIdWithLike(int id) {
        //初始化DTO对象
        DiscussPost discussPost= this.getById(id);
        DiscussPostDto discussPostDto = new DiscussPostDto();
        BeanUtils.copyProperties(discussPost, discussPostDto);

        //查询点赞数量并给DTO赋值
        long likeCount = this.findEntityLikeCount(ENTITY_TYPE_POST, id);
        discussPostDto.setLikeCount(likeCount);

        //查询点赞状态并给DTO赋值
        int likeStatus = hostHolder.getUser() == null ? 0 :
                this.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, id);
        discussPostDto.setLikeStatus(likeStatus);
        return discussPostDto;
    }

    /**
     * 查询用户收到的赞
     * @param userId 当前用户id
     * @return 用户收到的赞
     */
    @Override
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count;
    }
}
