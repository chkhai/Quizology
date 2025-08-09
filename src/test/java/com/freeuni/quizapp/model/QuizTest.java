package com.freeuni.quizapp.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuizTest {

    private static Quiz quiz;
    private static final Timestamp TIMESTAMP1 = Timestamp.valueOf("2025-07-07 12:40:21");
    private static final Timestamp TIMESTAMP2 = Timestamp.valueOf("2005-10-05 07:11:59");


    @BeforeAll
    public static void setUp() {
        quiz = new Quiz(1, "Sample Quiz", "Description", 101,
                    true, false, true, false, TIMESTAMP1);
    }

    @Test
    public void getIdTest() {
        assertEquals(1, quiz.getId());
        assertNotEquals(2, quiz.getId());
    }

    @Test
    public void setIdTest() {
        quiz.setId(2);
        assertEquals(2, quiz.getId());
        quiz.setId(1);
    }

    @Test
    public void getTitleTest() {
        assertEquals("Sample Quiz", quiz.getTitle());
        assertNotEquals("Another Title", quiz.getTitle());
    }

    @Test
    public void setTitleTest() {
        quiz.setTitle("Updated Title");
        assertEquals("Updated Title", quiz.getTitle());
        quiz.setTitle("Sample Quiz");
    }

    @Test
    public void getDescriptionTest() {
        assertEquals("Description", quiz.getDescription());
    }

    @Test
    public void setDescriptionTest() {
        quiz.setDescription("New Description");
        assertEquals("New Description", quiz.getDescription());
        quiz.setDescription("Description");
    }

    @Test
    public void getCreatorIdTest() {
        assertEquals(101, quiz.getCreatorId());
    }

    @Test
    public void setCreatorIdTest() {
        quiz.setCreatorId(202);
        assertEquals(202, quiz.getCreatorId());
        quiz.setCreatorId(101);
    }

    @Test
    public void isRandomTest() {
        assertTrue(quiz.isRandom());
    }

    @Test
    public void setRandomTest() {
        quiz.setRandom(false);
        assertFalse(quiz.isRandom());
        quiz.setRandom(true);
    }

    @Test
    public void isOnePageTest() {
        assertFalse(quiz.isOnePage());
    }

    @Test
    public void setOnePageTest() {
        quiz.setOnePage(true);
        assertTrue(quiz.isOnePage());
        quiz.setOnePage(false);
    }

    @Test
    public void isImmediateCorrectionTest() {
        assertTrue(quiz.isImmediateCorrection());
    }

    @Test
    public void setImmediateCorrectionTest() {
        quiz.setImmediateCorrection(false);
        assertFalse(quiz.isImmediateCorrection());
        quiz.setImmediateCorrection(true);
    }

    @Test
    public void isPracticeModeEnabledTest() {
        assertFalse(quiz.isPracticeModeEnabled());
    }

    @Test
    public void setPracticeModeEnabledTest() {
        quiz.setPracticeModeEnabled(true);
        assertTrue(quiz.isPracticeModeEnabled());
        quiz.setPracticeModeEnabled(false);
    }

    @Test
    public void getCreatedAtTest() {
        assertEquals(TIMESTAMP1, quiz.getCreatedAt());
    }

    @Test
    public void setCreatedAtTest() {
        quiz.setCreatedAt(TIMESTAMP2);
        assertEquals(TIMESTAMP2, quiz.getCreatedAt());
        quiz.setCreatedAt(TIMESTAMP1);
    }

    @Test
    public void getAndSetQuestionsTest() {
        assertNull(quiz.getQuestions());
        List<Question> list = new ArrayList<>();
        Question q = new Question(1, 1, "Q1", null, null);
        list.add(q);
        quiz.setQuestions(list);
        assertEquals(list, quiz.getQuestions());
        quiz.setQuestions(null);
    }

    @Test
    public void equalsTest() throws Exception {
        Quiz q = new Quiz(1, "Sample Quiz", "Description", 101,
                true, false, true, false, TIMESTAMP1);
        assertFalse(quiz.equals(null));
        assertFalse(quiz.equals(""));
        assertTrue(quiz.equals(q));
        assertFalse(quiz.equals(new Quiz(2, "Sample Quiz", "Description", 101,
                true, false, true, false, TIMESTAMP1)));
        assertFalse(quiz.equals(new Quiz(1, "Saple Quiz", "Description", 101,
                true, false, true, false, TIMESTAMP1)));
        assertFalse(quiz.equals(new Quiz(1, "Sample Quiz", "escription", 101,
                true, false, true, false, TIMESTAMP1)));
        assertFalse(quiz.equals(new Quiz(1, "Sample Quiz", "Description", 11,
                true, false, true, false, TIMESTAMP1)));
        assertFalse(quiz.equals(new Quiz(1, "Sample Quiz", "Description", 101,
                false, false, true, false, TIMESTAMP1)));
        assertFalse(quiz.equals(new Quiz(1, "Sample Quiz", "Description", 101,
                true, true, true, false, TIMESTAMP1)));
        assertFalse(quiz.equals(new Quiz(1, "Sample Quiz", "Description", 101,
                true, false, false, false, TIMESTAMP1)));
        assertFalse(quiz.equals(new Quiz(1, "Sample Quiz", "Description", 101,
                true, false, true, true, TIMESTAMP1)));
        assertFalse(quiz.equals(new Quiz(1, "Sample Quiz", "Description", 101,
                true, false, true, false, TIMESTAMP2)));
    }
}
