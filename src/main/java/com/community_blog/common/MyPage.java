package com.community_blog.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 在MybatisPlus提供的page类的基础上添加一些属性
 * @param <T>
 */
public class MyPage<T> extends Page<T> {
    /**
     * 访问路径，用于前端复用
     */
    private String path;

    /**
     * 起始页
     */
    private final long START_PAGE = 1;

    public long getStartPage() {
        return START_PAGE;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取最后一页的数值
     * @return 最后一页的数值
     */
    public long getLastPage() {
        return (total % size == 0) ? (total / size) : (total / size + 1);
    }
}
