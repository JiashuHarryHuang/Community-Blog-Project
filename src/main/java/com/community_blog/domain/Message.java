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
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 发送者用户id
     * 1-系统提示
     */
    private Integer fromId;

    /**
     * 接收者用户id
     */
    private Integer toId;

    /**
     * 会话id，由fromId_toId/toId_fromId组成
     * 但先后顺序不代表谁发的消息
     */
    private String conversationId;

    private String content;

    /**
     * 0-未读;1-已读;2-删除;
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


}
