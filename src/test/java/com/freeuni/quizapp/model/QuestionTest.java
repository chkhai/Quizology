package com.freeuni.quizapp.model;

import com.freeuni.quizapp.enums.QuestionType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuestionTest {
    private static Question q;

    @BeforeAll
    public static void setUp() {
        q = new Question(1, 1, "bla", QuestionType.question_response, "ques.com");
    }

    @Test
    public void getIdTest(){
        assertEquals(1,  q.getId());
        assertNotEquals(0,  q.getId());
    }

    @Test
    public void setIdTest(){
        q.setId(2);
        assertEquals(2,  q.getId());
        assertNotEquals(0,  q.getId());
        q.setId(1);
    }

    @Test
    public void getQuizIdTest(){
        assertEquals(1,  q.getQuizId());
        assertNotEquals(0,  q.getQuizId());
    }

    @Test
    public void setQuizIdTest(){
        q.setQuizId(2);
        assertEquals(2,  q.getQuizId());
        assertNotEquals(0,  q.getQuizId());
        q.setQuizId(1);
    }

    @Test
    public void getTextTest(){
        assertEquals("bla", q.getText());
        assertNotEquals("ba", q.getText());
    }

    @Test
    public void setTextTest(){
        q.setText("bl");
        assertEquals("bl", q.getText());
        assertNotEquals("bla", q.getText());
        q.setText("bla");
    }

    @Test
    public void getTypeTest(){
        assertEquals(QuestionType.question_response,q.getType());
        assertNotEquals(QuestionType.fill_in_blank, q.getType());
    }

    @Test
    public void setTypeTest(){
        q.setType(QuestionType.fill_in_blank);
        assertEquals(QuestionType.fill_in_blank, q.getType());
        assertNotEquals(QuestionType.multiple_choice, q.getType());
        q.setType(QuestionType.question_response);
    }

    @Test
    public void getImageUrlTest(){
        assertEquals("ques.com", q.getImageUrl());
        assertNotEquals("es.com", q.getImageUrl());
    }

    @Test
    public void setImageUrlTest(){
        q.setImageUrl("que.com");
        assertEquals("que.com", q.getImageUrl());
        assertNotEquals("qcascue.com", q.getImageUrl());
        q.setImageUrl("ques.com");
    }

    @Test
    public void getAnswersTest(){
        assertNull(q.getAnswers());
    }

    @Test
    public void setAnswersTest(){
        List<Answer> lst = new ArrayList<Answer>();
        Answer d = new Answer(1,1,"shota rutsaveli", true);
        lst.add(d);
        q.setAnswers(lst);
        assertEquals(lst, q.getAnswers());
        assertNotEquals(0,  q.getAnswers().size());
        q.setAnswers(null);
    }

    @Test
    public void equalsTest(){
        assertFalse(q.equals(null));
        assertFalse(q.equals("bla"));
        Question d = new Question(1, 1, "bla", QuestionType.question_response, "ques.com");
        assertTrue(q.equals(d));
        assertFalse(q.equals(new Question(2, 1, "bla", QuestionType.question_response, "ques.com")));
        assertFalse(q.equals(new Question(1, 2, "bla", QuestionType.question_response, "ques.com")));
        assertFalse(q.equals(new Question(1, 1, "bl", QuestionType.question_response, "ques.com")));
        assertFalse(q.equals(new Question(1, 1, "bla", QuestionType.fill_in_blank, "ques.com")));
        assertFalse(q.equals(new Question(1, 1, "bla", QuestionType.question_response, "es.com")));




    }
}
