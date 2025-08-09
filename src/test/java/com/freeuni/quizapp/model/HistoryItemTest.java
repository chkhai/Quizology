package com.freeuni.quizapp.model;

import com.freeuni.quizapp.enums.ActionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryItemTest {
    private HistoryItem h;
    private static final Timestamp TIMESTAMP1 = Timestamp.valueOf("2025-07-07 12:40:21");
    private static final Timestamp TIMESTAMP2 = Timestamp.valueOf("2005-10-05 07:11:59");

    @BeforeEach
    public void setUp() {
        h = new HistoryItem(1, 1,1, ActionType.quiz_created, TIMESTAMP1);
    }

    @Test
    public void getIdTest() {
        assertEquals(1, h.getId());
        assertNotEquals(0, h.getId());
    }

    @Test
    public void setIdTest() {
        h.setId(2);
        assertEquals(2, h.getId());
        assertNotEquals(0, h.getId());
        h.setId(1);
    }

    @Test
    public void getUserIdTest() {
        assertEquals(1, h.getUserId());
        assertNotEquals(0, h.getUserId());
    }

    @Test
    public void setUserIdTest() {
        h.setUserId(2);
        assertEquals(2, h.getUserId());
        assertNotEquals(0, h.getUserId());
        h.setUserId(1);
    }

    @Test
    public void getQuizIdTest() {
        assertEquals(1, h.getQuizId());
        assertNotEquals(0, h.getQuizId());
    }

    @Test
    public void setQuizIdTest() {
        h.setQuizId(2);
        assertEquals(2, h.getQuizId());
        assertNotEquals(0, h.getQuizId());
        h.setQuizId(1);
    }

    @Test
    public void getActionTypeTest() {
        assertEquals(ActionType.quiz_created, h.getActionType());
        assertNotEquals(ActionType.quiz_taken, h.getActionType());
    }

    @Test
    public void setActionTypeTest() {
        h.setActionType(ActionType.quiz_taken);
        assertEquals(ActionType.quiz_taken, h.getActionType());
        assertNotEquals(ActionType.quiz_created, h.getActionType());
        h.setActionType(ActionType.quiz_created);
    }

    @Test
    public void getTimestampTest() {
        assertEquals(TIMESTAMP1, h.getTimestamp());
        assertNotEquals(TIMESTAMP2, h.getTimestamp());
    }

    @Test
    public void setTimestampTest() {
        h.setTimestamp(TIMESTAMP2);
        assertEquals(TIMESTAMP2, h.getTimestamp());
        assertNotEquals(TIMESTAMP1, h.getTimestamp());
        h.setTimestamp(TIMESTAMP1);
    }

    @Test
    public void equalsTest() {
        assertFalse(h.equals(null));
        assertFalse(h.equals(""));
        HistoryItem h2 = new HistoryItem(1, 1,1, ActionType.quiz_created, TIMESTAMP1);
        assertTrue(h.equals(h2));
        assertFalse(h.equals(new HistoryItem(2, 1,1, ActionType.quiz_created, TIMESTAMP1)));
        assertFalse(h.equals(new HistoryItem(1, 2,1, ActionType.quiz_created, TIMESTAMP1)));
        assertFalse(h.equals(new HistoryItem(1, 1,2, ActionType.quiz_created, TIMESTAMP1)));
        assertFalse(h.equals(new HistoryItem(1, 1,1, ActionType.quiz_taken, TIMESTAMP1)));
        assertFalse(h.equals(new HistoryItem(1, 1,1, ActionType.quiz_created, TIMESTAMP2)));
    }
}
