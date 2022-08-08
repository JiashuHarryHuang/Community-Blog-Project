package com.community_blog.util;

import com.community_blog.domain.User;
import org.springframework.stereotype.Component;

/**
 * ThreadLocal获取用户信息
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
