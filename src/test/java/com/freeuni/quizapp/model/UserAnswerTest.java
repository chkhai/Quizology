package com.freeuni.quizapp.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserAnswerTest {

    private static UserAnswer answer;

    @BeforeAll
    public static void setUp() {
        answer = new UserAnswer(1, 10, 100, "Shota Rustaveli", true);
    }

    @Test
    public void idGetterSetterTest() {
        assertEquals(1, answer.getId());
        answer.setId(2);
        assertEquals(2, answer.getId());
        answer.setId(1);
    }

    @Test
    public void userIdGetterSetterTest() {
        assertEquals(10, answer.getUserId());
        answer.setUserId(20);
        assertEquals(20, answer.getUserId());
        answer.setUserId(10);
    }

    @Test
    public void questionIdGetterSetterTest() {
        assertEquals(100, answer.getQuestionId());
        answer.setQuestionId(200);
        assertEquals(200, answer.getQuestionId());
        answer.setQuestionId(100);
    }

    @Test
    public void givenAnswerGetterSetterTest() {
        assertEquals("Shota Rustaveli", answer.getGivenAnswer());
        answer.setGivenAnswer("Ilia Chavchavadze");
        assertEquals("Ilia Chavchavadze", answer.getGivenAnswer());
        answer.setGivenAnswer("Shota Rustaveli");
    }

    @Test
    public void isCorrectGetterSetterTest() {
        assertTrue(answer.isCorrect());
        answer.setCorrect(false);
        assertFalse(answer.isCorrect());
        answer.setCorrect(true);
    }

    @Test
    public void equalsTest() {
        UserAnswer same = new UserAnswer(1, 10, 100, "Shota Rustaveli", true);
        assertTrue(answer.equals(same));
        assertFalse(answer.equals(null));
        assertFalse(answer.equals(""));

        assertFalse(answer.equals(new UserAnswer(2, 10, 100, "Shota Rustaveli", true)));
        assertFalse(answer.equals(new UserAnswer(1, 11, 100, "Shota Rustaveli", true)));
        assertFalse(answer.equals(new UserAnswer(1, 10, 101, "Shota Rustaveli", true)));
        assertFalse(answer.equals(new UserAnswer(1, 10, 100, "Shota Rustavel", true)));
        assertFalse(answer.equals(new UserAnswer(1, 10, 100, "Shota Rustaveli", false)));
    }
}
