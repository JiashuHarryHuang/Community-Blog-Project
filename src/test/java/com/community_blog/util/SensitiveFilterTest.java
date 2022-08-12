package com.community_blog.util;

import com.community_blog.CommunityBlogApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityBlogApplication.class)
public class SensitiveFilterTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        Assert.assertEquals("abcd***efghi", sensitiveFilter.filter("abcd赌博efghi"));
        Assert.assertEquals("赌abc博", sensitiveFilter.filter("赌abc博"));
        Assert.assertEquals("abc***", sensitiveFilter.filter("abc赌博"));
        Assert.assertEquals("***abc", sensitiveFilter.filter("赌博abc"));
        Assert.assertEquals("***", sensitiveFilter.filter("赌*博"));
        Assert.assertEquals("*abc", sensitiveFilter.filter("*abc"));
    }

}
