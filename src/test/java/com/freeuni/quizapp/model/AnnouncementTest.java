package com.freeuni.quizapp.model;

import org.h2.expression.function.CurrentDateTimeValueFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class AnnouncementTest {
    private static Announcement a;
    private static final Timestamp TIMESTAMP1 = Timestamp.valueOf("2025-07-07 12:40:21");
    private static final Timestamp TIMESTAMP2 = Timestamp.valueOf("1995-07-07 12:40:21");



    @BeforeAll
    static void setUp() {
        a = new Announcement(1, 1,"Update!",
                "New Quiz Added!", "quiz.com/newquiz",
                TIMESTAMP1);
    }

    @Test
    public void getTextTest() {
        assertEquals("New Quiz Added!", a.getText());
        assertNotEquals("ew uiz Added", a.getText());
    }

    @Test
    public void setTextTest() {
        assertEquals("New Quiz Added!", a.getText());
        a.setText("New Mode!");
        assertNotEquals("ew uiz Added", a.getText());
        assertEquals("New Mode!", a.getText());
        a.setText("New Quiz Added!");
        assertEquals("New Quiz Added!", a.getText());
    }

    @Test
    public void getIdTest() {
        assertEquals(1,a.getId());
        assertNotEquals(7, a.getId());
    }

    @Test
    public void setIdTest() {
        a.setId(7);
        assertEquals(7, a.getId());
        assertNotEquals(1, a.getId());
        a.setId(1);
        assertEquals(1,a.getId());
    }

    @Test
    public void getUser_idTest() {
        assertEquals(1,a.getUser_id());
        assertNotEquals(8, a.getUser_id());
    }

    @Test
    public void setUser_idTest() {
        a.setUser_id(15);
        assertEquals(15, a.getUser_id());
        assertNotEquals(1, a.getUser_id());
        assertNotEquals(3, a.getUser_id());
        a.setUser_id(1);
        assertEquals(1,a.getUser_id());
    }

    @Test
    public void getTitleTest() {
        assertEquals("Update!", a.getTitle());
        assertNotEquals("new mode", a.getTitle());
    }

    @Test
    public void setTitleTest() {
        a.setTitle("New Mode!");
        assertNotEquals("new quiz", a.getTitle());
        assertEquals("New Mode!", a.getTitle());
        a.setTitle("Update!");
        assertEquals("Update!", a.getTitle());
    }

    @Test
    public void getUrlTest() {
        assertEquals("quiz.com/newquiz", a.getUrl());
        assertNotEquals("new quiz", a.getUrl());
    }

    @Test
    public void setUrlTest() {
        a.setUrl("facebook.com");
        assertEquals("facebook.com", a.getUrl());
        assertNotEquals("quiz.com/newquiz" , a.getUrl());
        a.setUrl("quiz.com/newquiz");
        assertEquals("quiz.com/newquiz", a.getUrl());
    }

    @Test
    public void getCreatedAtTest() {
        assertEquals(TIMESTAMP1, a.getCreatedAt());
        assertNotEquals(TIMESTAMP2, a.getCreatedAt());
    }

    @Test
    public void setCreatedAtTest() {
        a.setCreatedAt(TIMESTAMP2);
        assertEquals(TIMESTAMP2, a.getCreatedAt());
        assertNotEquals(TIMESTAMP1, a.getCreatedAt());
        a.setCreatedAt(TIMESTAMP1);
        assertEquals(TIMESTAMP1, a.getCreatedAt());
    }

    @Test
    public void getCreatedByIdTest() {
        Announcement d = new Announcement(1, 1,"Update!",
                "New Quiz Added!", "quiz.com/newquiz",
                TIMESTAMP1);
        assertTrue(a.equals(d));
        d.setId(2);
        assertFalse(a.equals(d));
        d.setId(1);
        d.setUser_id(3);
        assertFalse(a.equals(d));
        d.setUser_id(1);
        d.setTitle("New Mode!");
        assertFalse(a.equals(d));
        d.setTitle("Update!");
        d.setUrl("facebook.com");
        assertFalse(a.equals(d));
        d.setUrl("quiz.com/newquiz");
        d.setCreatedAt(TIMESTAMP2);
        assertFalse(a.equals(d));
        d.setCreatedAt(TIMESTAMP1);
        d.setText("New Quiz Added");
        assertFalse(a.equals(d));
        d.setText("New Quiz Added!");
        assertFalse(a.equals(null));
        assertFalse(a.equals(""));
    }
}
