package com.freeuni.quizapp.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class QuizResultTest {

    private static QuizResult qr;
    private static final Timestamp TIMESTAMP1 = Timestamp.valueOf("2025-07-07 12:40:21");
    private static final Timestamp TIMESTAMP2 = Timestamp.valueOf("2005-10-05 07:11:59");


    @BeforeAll
    public static void setUp() {
        qr = new QuizResult(1, 101, 202, 85, 10, 120, true, TIMESTAMP1);
    }

    @Test
    public void constructorWithAllFieldsTest() {
        assertEquals(1, qr.getId());
        assertEquals(101, qr.getUserId());
        assertEquals(202, qr.getQuizId());
        assertEquals(85, qr.getScore());
        assertEquals(10, qr.getTotalQuestions());
        assertEquals(120, qr.getTimeTakenSeconds());
        assertTrue(qr.isPracticeMode());
        assertEquals(TIMESTAMP1, qr.getCompletedAt());
    }

    @Test
    public void idGetterSetterTest() {
        qr.setId(5);
        assertEquals(5, qr.getId());
        qr.setId(1);
    }

    @Test
    public void userIdGetterSetterTest() {
        qr.setUserId(999);
        assertEquals(999, qr.getUserId());
        qr.setUserId(101);
    }

    @Test
    public void quizIdGetterSetterTest() {
        qr.setQuizId(888);
        assertEquals(888, qr.getQuizId());
        qr.setQuizId(202);
    }

    @Test
    public void scoreGetterSetterTest() {
        qr.setScore(95);
        assertEquals(95, qr.getScore());
        qr.setScore(85);
    }

    @Test
    public void totalQuestionsGetterSetterTest() {
        qr.setTotalQuestions(15);
        assertEquals(15, qr.getTotalQuestions());
        qr.setTotalQuestions(10);
    }

    @Test
    public void timeTakenSecondsGetterSetterTest() {
        qr.setTimeTakenSeconds(300);
        assertEquals(300, qr.getTimeTakenSeconds());
        qr.setTimeTakenSeconds(120);
    }

    @Test
    public void practiceModeGetterSetterTest() {
        qr.setPracticeMode(false);
        assertFalse(qr.isPracticeMode());
        qr.setPracticeMode(true);
    }

    @Test
    public void completedAtGetterSetterTest() {
        qr.setCompletedAt(TIMESTAMP2);
        assertEquals(TIMESTAMP2, qr.getCompletedAt());
        qr.setCompletedAt(TIMESTAMP1);
    }

    @Test
    public void equalsTest() {
        QuizResult same = new QuizResult(1, 101, 202, 85, 10, 120, true, TIMESTAMP1);
        assertTrue(qr.equals(same));
        assertFalse(qr.equals(null));
        assertFalse(qr.equals(""));

        assertFalse(qr.equals(new QuizResult(2, 101, 202, 85, 10, 120, true, TIMESTAMP1)));

        assertFalse(qr.equals(new QuizResult(1, 201, 202, 85, 10, 120, true, TIMESTAMP1)));

        assertFalse(qr.equals(new QuizResult(1, 101, 203, 85, 10, 120, true, TIMESTAMP1)));

        assertFalse(qr.equals(new QuizResult(1, 101, 202, 90, 10, 120, true, TIMESTAMP1)));

        assertFalse(qr.equals(new QuizResult(1, 101, 202, 85, 9, 120, true, TIMESTAMP1)));

        assertFalse(qr.equals(new QuizResult(1, 101, 202, 85, 10, 100, true, TIMESTAMP2)));

        assertFalse(qr.equals( new QuizResult(1, 101, 202, 85, 10, 120, false, TIMESTAMP1)));
    }
}
