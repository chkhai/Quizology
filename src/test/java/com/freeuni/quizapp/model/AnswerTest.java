package com.freeuni.quizapp.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AnswerTest {
    static Answer a;

    @BeforeAll
    public static void setUp() {
        a = new Answer(1,1,"shota rutsaveli", true);
    }

    @Test
    public void getIdTest() {
        assertEquals(1, a.getId());
        assertNotEquals(0, a.getId());
    }

    @Test
    public void setIdTest() {
        a.setId(2);
        assertNotEquals(1, a.getId());
        assertNotEquals(0, a.getId());
        assertEquals(2, a.getId());
        a.setId(1);
        assertEquals(1, a.getId());
    }

    @Test
    public void getQuestionId() {
        assertEquals(1, a.getQuestionId());
        assertNotEquals(0, a.getQuestionId());
    }

    @Test
    public void setQuestionId() {
        a.setQuestionId(2);
        assertNotEquals(1, a.getQuestionId());
        assertNotEquals(0, a.getQuestionId());
        assertEquals(2, a.getQuestionId());
        a.setQuestionId(1);
        assertEquals(1, a.getQuestionId());
    }

    @Test
    public void getAnswerTextTest(){
        assertEquals("shota rustaveli", a.getAnswerText());
        assertNotEquals("", a.getAnswerText());
    }

    @Test
    public void setAnswerTextTest(){
        a.setAnswerText("akaki tsereteli");
        assertNotEquals("shota rustaveli", a.getAnswerText());
        assertEquals("akaki tsereteli", a.getAnswerText());
        a.setAnswerText("shota rustaveli");
        assertNotEquals("akaki tsereteli", a.getAnswerText());
    }

    @Test
    public void isCorrectTest(){
        assertTrue(a.isCorrect());
    }

    @Test
    public void setCorrectTest(){
        a.setCorrect(false);
        assertFalse(a.isCorrect());
        a.setCorrect(true);
        assertTrue(a.isCorrect());
    }

    @Test
    public void equalsTest(){
        Answer d = new Answer(1,1,"shota rutsaveli", true);
        a.equals(d);
        assertFalse(a.equals(null));
        assertFalse(a.equals(""));
        assertFalse(a.equals(new Answer(2,1,"shota rutsaveli", true)));
        assertFalse(a.equals(new Answer(1,2,"shota rutsaveli", true)));
        assertFalse(a.equals(new Answer(1,1,"shot rutsaveli", true)));
        assertFalse(a.equals(new Answer(1,1,"shota rutsaveli", false)));


    }
}
