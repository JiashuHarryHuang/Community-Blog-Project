package com.community_blog.dao;

import com.community_blog.CommunityBlogApplication;
import com.community_blog.domain.Message;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityBlogApplication.class)
public class MessageDaoTest {
    @Autowired
    private MessageDao messageDao;

    @Test
    public void selectConversationsTest() {
        List<Message> messages = messageDao.selectConversations(111, 0, 5);
        messages.forEach(System.out::println);
    }

    @Test
    public void selectConversationCountTest() {
        Assert.assertEquals(14, messageDao.selectConversationCount(111));
    }

    @Test
    public void selectMessagesTest() {
        messageDao.selectMessages("111_112", 0, 100).forEach(System.out::println);
    }

    @Test
    public void selectMessagesCountTest() {
        Assert.assertEquals(8, messageDao.selectMessagesCount("111_112"));
    }

    @Test
    public void selectUnreadMessagesCountTest() {
        Assert.assertEquals(1, messageDao.selectUnreadMessageCount(112, "111_112"));
    }

}
