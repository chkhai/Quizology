package com.freeuni.quizapp.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private static User user;
    private static final Timestamp TIMESTAMP1 = Timestamp.valueOf("2025-07-07 12:40:21");
    private static final Timestamp TIMESTAMP2 = Timestamp.valueOf("2005-10-05 07:11:59");

    @BeforeAll
    public static void setUp() {
        user = new User(1, "luka", "hash", false, TIMESTAMP1,
                "bio", "pic.com");
    }

    @Test
    public void idGetterSetterTest() {
        assertEquals(1, user.getId());
        user.setId(2);
        assertEquals(2, user.getId());
        user.setId(1);
    }

    @Test
    public void usernameGetterSetterTest() {
        assertEquals("luka", user.getUsername());
        user.setUsername("nino");
        assertEquals("nino", user.getUsername());
        user.setUsername("luka");
    }

    @Test
    public void hashedPasswordGetterSetterTest() {
        assertEquals("hash", user.getHashedPassword());
        user.setHashedPassword("newHash");
        assertEquals("newHash", user.getHashedPassword());
        user.setHashedPassword("hash");
    }

    @Test
    public void isAdminGetterSetterTest() {
        assertFalse(user.isAdmin());
        user.setAdmin(true);
        assertTrue(user.isAdmin());
        user.setAdmin(false);
    }

    @Test
    public void createdAtGetterSetterTest() {
        user.setCreatedAt(TIMESTAMP2);
        assertEquals(TIMESTAMP2, user.getCreatedAt());
        user.setCreatedAt(TIMESTAMP1);
    }

    @Test
    public void bioGetterSetterTest() {
        assertEquals("bio", user.getBio());
        user.setBio("new bio");
        assertEquals("new bio", user.getBio());
        user.setBio("bio");
    }

    @Test
    public void profilePictureUrlGetterSetterTest() {
        assertEquals("pic.com", user.getProfilePictureUrl());
        user.setProfilePictureUrl("newpic.com");
        assertEquals("newpic.com", user.getProfilePictureUrl());
        user.setProfilePictureUrl("pic.com");
    }

    @Test
    public void quizzesCreatedGetterSetterTest() {
        assertNull(user.getQuizzesCreated());
        List<Quiz> list = new ArrayList<>();
        user.setQuizzesCreated(list);
        assertEquals(list, user.getQuizzesCreated());
        user.setQuizzesCreated(null);
    }

    @Test
    public void quizzesTakenGetterSetterTest() {
        assertNull(user.getQuizzesTaken());
        List<QuizResult> list = new ArrayList<>();
        user.setQuizzesTaken(list);
        assertEquals(list, user.getQuizzesTaken());
        user.setQuizzesTaken(null);
    }

    @Test
    public void friendsGetterSetterTest() {
        assertNull(user.getFriends());
        List<User> list = new ArrayList<>();
        user.setFriends(list);
        assertEquals(list, user.getFriends());
        user.setFriends(null);
    }


    @Test
    public void equalsTest() {
        User u = new User(1, "luka", "hash", false, TIMESTAMP1,
                "bio", "pic.com");
        assertTrue(user.equals(u));
        assertFalse(user.equals(null));
        assertFalse(user.equals(""));
        assertFalse(user.equals(new User(2, "luka", "hash", false, TIMESTAMP1,
                "bio", "pic.com")));
        assertFalse(user.equals(new User(1, "lka", "hash", false, TIMESTAMP1,
                "bio", "pic.com")));
        assertFalse(user.equals(new User(1, "luka", "ash", false, TIMESTAMP1,
                "bio", "pic.com")));
        assertFalse(user.equals(new User(1, "luka", "hash", true, TIMESTAMP1,
                "bio", "pic.com")));
        assertFalse(user.equals(new User(1, "luka", "hash", false, TIMESTAMP2,
                "bio", "pic.com")));
        assertFalse(user.equals(new User(1, "luka", "hash", false, TIMESTAMP1,
                "bo", "pic.com")));
        assertFalse(user.equals(new User(1, "luka", "hash", false, TIMESTAMP1,
                "bio", "pc.com")));
        u.setFriends(new ArrayList<User>());
        assertFalse(user.equals(u));
        u.setFriends(null);
        u.setQuizzesCreated(new ArrayList<Quiz>());
        assertFalse(user.equals(u));
        u.setQuizzesCreated(null);
        u.setQuizzesTaken(new ArrayList<QuizResult>());
        assertFalse(user.equals(u));
        u.setQuizzesTaken(null);
    }
}
