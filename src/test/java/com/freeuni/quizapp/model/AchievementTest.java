package com.freeuni.quizapp.model;

import com.freeuni.quizapp.enums.AchievementType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class AchievementTest {
    private static Achievement a1;
    private static Achievement a2;
    private static Achievement a3;
    private static Achievement a4;

    private static final Timestamp TIMESTAMP1 = Timestamp.valueOf("2025-07-07 12:40:21");
    private static final Timestamp TIMESTAMP2 = Timestamp.valueOf("2005-10-05 07:11:59");


    @BeforeAll
    public static void setUp(){
        a1 = new Achievement(1, 1, AchievementType.I_am_the_Greatest, 1, TIMESTAMP1);
        a2 = new Achievement(2,2, AchievementType.Quiz_Machine, 1, TIMESTAMP2);
        a3 = new Achievement(3,3, AchievementType.Amateur_Author, 2, TIMESTAMP2);
        a4 = new Achievement(4,5, AchievementType.Prodigious_Author, 2, TIMESTAMP1);
    }

    @Test
    public void getQuiz_idTest(){
        assertEquals(1, a2.getQuiz_id());
        assertEquals(a1.getQuiz_id(), a2.getQuiz_id());
        assertEquals(a3.getQuiz_id(), a4.getQuiz_id());
        assertEquals(2, a3.getQuiz_id());
    }

    @Test
    public void setQuiz_idTest(){
        a1.setQuiz_id(2);
        assertEquals(2, a1.getQuiz_id());
        a1.setQuiz_id(1);
        assertEquals(1, a1.getQuiz_id());
    }

    @Test
    public void getIdTest(){
        assertEquals(1, a1.getId());
        assertEquals(2, a2.getId());
        assertEquals(3, a3.getId());
        assertEquals(4, a4.getId());
        assertNotEquals(a1.getQuiz_id(), a3.getQuiz_id());
    }

    @Test
    public void setIdTest(){
        a2.setId(5);
        assertEquals(5, a2.getId());
        a2.setId(6);
        assertEquals(6, a2.getId());
        a2.setId(2);
        assertEquals(2, a2.getId());
    }

    @Test
    public void getUserIdTest(){
        assertEquals(1, a1.getUserId());
        assertEquals(2, a2.getUserId());
        assertEquals(3, a3.getUserId());
        assertNotEquals(a1.getUserId(), a2.getUserId());
        assertNotEquals(4, a4.getUserId());
        assertEquals(5, a4.getUserId());
    }

    @Test
    public void setUserIdTest(){
        a1.setUserId(4);
        assertEquals(4, a1.getUserId());
        a1.setUserId(5);
        assertEquals(5, a1.getUserId());
        a1.setUserId(1);
    }

    @Test
    public void getTypeTest(){
        assertEquals(AchievementType.I_am_the_Greatest, a1.getType());
        assertEquals(AchievementType.Quiz_Machine, a2.getType());
        assertEquals(AchievementType.Amateur_Author, a3.getType());
        assertNotEquals(AchievementType.Amateur_Author, a4.getType());
        assertEquals(AchievementType.Prodigious_Author, a4.getType());
    }

    @Test
    public void setTypeTest(){
        a1.setType(AchievementType.Quiz_Machine);
        assertEquals(AchievementType.Quiz_Machine, a1.getType());
        a1.setType(AchievementType.Prodigious_Author);
        assertEquals(AchievementType.Prodigious_Author, a1.getType());
        a1.setType(AchievementType.I_am_the_Greatest);
        assertEquals(AchievementType.I_am_the_Greatest, a1.getType());
    }

    @Test
    public void getTimestampTest(){
        assertEquals(TIMESTAMP1, a1.getAchievedAt());
        assertEquals(TIMESTAMP2, a2.getAchievedAt());
        assertNotEquals(TIMESTAMP1, a2.getAchievedAt());
        assertEquals(TIMESTAMP2, a3.getAchievedAt());
        assertNotEquals(TIMESTAMP2, a4.getAchievedAt());
        assertEquals(TIMESTAMP1, a4.getAchievedAt());
    }

    @Test
    public void setTimestampTest(){
        a1.setAchievedAt(TIMESTAMP2);
        assertEquals(TIMESTAMP2, a1.getAchievedAt());
        a1.setAchievedAt(TIMESTAMP1);
        assertEquals(TIMESTAMP1, a1.getAchievedAt());
    }

    @Test
    public void equalsTest(){
        assertFalse(a1.equals(null));
        assertFalse(a1.equals(""));
        assertFalse(a1.equals(a2));
        Achievement a = new Achievement(3,3, AchievementType.Amateur_Author, 2, TIMESTAMP2);
        assertTrue(a.equals(a3));
        assertFalse(a.equals(a2));
        assertFalse(a.equals(new Achievement(3, 2, AchievementType.I_am_the_Greatest, 2, TIMESTAMP2)));
        assertFalse(a.equals(new Achievement(3, 3, AchievementType.I_am_the_Greatest, 2, TIMESTAMP1)));
    }
}
