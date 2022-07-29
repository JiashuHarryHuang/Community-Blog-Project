package com.community_blog.domain;

import com.baomidou.mybatisplus.annotation.IdType;
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
public class LoginTicket implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private String ticket;

    /**
     * 0-有效; 1-无效;
     */
    private Integer status;

    private LocalDateTime expired;


}
