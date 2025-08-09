package com.freeuni.quizapp.model;

import com.freeuni.quizapp.enums.MessageType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class MessagesTest {
    private static Message m;
    private static final Timestamp TIMESTAMP1 = Timestamp.valueOf("2025-07-07 12:40:21");
    private static final Timestamp TIMESTAMP2 = Timestamp.valueOf("2005-10-05 07:11:59");

    @BeforeAll
    public static void setUp() {
        m = new Message(1,1,2, MessageType.text, "hello", TIMESTAMP1);
    }

    @Test
    public void getIdTest(){
        assertEquals(1, m.getId());
        assertNotEquals(0, m.getId());
    }

    @Test
    public void setIdTest(){
        m.setId(2);
        assertEquals(2, m.getId());
        assertNotEquals(0, m.getId());
        m.setId(1);
    }

    @Test
    public void getSenderIdTest(){
        assertEquals(1, m.getSenderId());
        assertNotEquals(0, m.getSenderId());
    }

    @Test
    public void setSenderIdTest(){
        m.setSenderId(2);
        assertEquals(2, m.getSenderId());
        assertNotEquals(0, m.getSenderId());
        m.setSenderId(1);
    }

    @Test
    public void getReceiverIdTest(){
        assertEquals(2, m.getReceiverId());
        assertNotEquals(0, m.getReceiverId());
    }

    @Test
    public void setReceiverIdTest(){
        m.setReceiverId(4);
        assertEquals(4, m.getReceiverId());
        assertNotEquals(0, m.getReceiverId());
        m.setReceiverId(2);
    }

    @Test
    public void getMessageTypeTest(){
        assertEquals(MessageType.text, m.getType());
        assertNotEquals(MessageType.challenge, m.getType());
    }

    @Test
    public void setMessageTypeTest(){
        m.setType(MessageType.challenge);
        assertEquals(MessageType.challenge, m.getType());
        assertNotEquals(MessageType.text, m.getType());
        m.setType(MessageType.text);
    }

    @Test
    public void getContentTest(){
        assertEquals("hello", m.getContent());
        assertNotEquals("heo", m.getContent());
    }

    @Test
    public void setContentTest(){
        m.setContent("bye");
        assertNotEquals("hello", m.getContent());
        assertEquals("bye", m.getContent());
        m.setContent("hello");
    }

    @Test
    public void getTimestampTest(){
        assertEquals(TIMESTAMP1, m.getSentAt());
        assertNotEquals(TIMESTAMP2, m.getSentAt());
    }

    @Test
    public void setTimestampTest(){
        m.setSentAt(TIMESTAMP2);
        assertEquals(TIMESTAMP2, m.getSentAt());
        assertNotEquals(TIMESTAMP1, m.getSentAt());
        m.setSentAt(TIMESTAMP1);
    }

    @Test
    public void equalsTest(){
        assertFalse(m.equals(null));
        assertFalse(m.equals(""));
        Message d = new Message(1,1,2, MessageType.text, "hello", TIMESTAMP1);
        assertTrue(m.equals(d));
        assertFalse(m.equals(new Message(2,1,2, MessageType.text, "hello", TIMESTAMP1)));
        assertFalse(m.equals(new Message(1,2,2, MessageType.text, "hello", TIMESTAMP1)));
        assertFalse(m.equals(new Message(1,1,1, MessageType.text, "hello", TIMESTAMP1)));
        assertFalse(m.equals(new Message(1,1,2, MessageType.friend_request, "hello", TIMESTAMP1)));
        assertFalse(m.equals(new Message(1,1,2, MessageType.text, "bla", TIMESTAMP1)));
        assertFalse(m.equals(new Message(1,1,2, MessageType.text, "hello", TIMESTAMP2)));
    }


}
