package com.community_blog.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Harry
 * @since 2022-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    /**
     * 评论的目标的类型：帖子、评论、用户...
     * 1-帖子; 2-评论
     */
    private Integer entityType;

    /**
     * 具体是哪个目标，比如id为1的帖子
     */
    private Integer entityId;

    /**
     * 评论评论的时候是回复具体哪个目标
     * 0-评论的是帖子
     */
    private Integer targetId;

    private String content;

    /**
     * 1-禁用; 0-启用
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


}
