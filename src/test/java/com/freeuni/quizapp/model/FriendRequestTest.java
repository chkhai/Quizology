package com.freeuni.quizapp.model;

import com.freeuni.quizapp.enums.FriendshipStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class FriendRequestTest {

    private static final Timestamp TIMESTAMP1 = Timestamp.valueOf("2025-07-07 12:40:21");
    private static FriendRequest fr;

    @BeforeAll
    public static void setUp() {
        fr = new FriendRequest(1,1,2,
                FriendshipStatus.pending, TIMESTAMP1);
    }

    @Test
    public void getIdTest() {
        assertEquals(1, fr.getId());
        assertNotEquals(0, fr.getId());
    }

    @Test
    public void setIdTest() {
        fr.setId(2);
        assertEquals(2, fr.getId());
        assertNotEquals(0, fr.getId());
        fr.setId(1);
        assertEquals(1, fr.getId());
        assertNotEquals(0, fr.getId());
    }

    @Test
    public void getFromUserIdTest() {
        assertEquals(1, fr.getFromUserId());
        assertNotEquals(0, fr.getFromUserId());
    }

    @Test
    public void setFromUserIdTest() {
        fr.setFromUserId(3);
        assertEquals(3, fr.getFromUserId());
        assertNotEquals(0, fr.getFromUserId());
        fr.setFromUserId(1);
    }

    @Test
    public void getToUserIdTest() {
        assertEquals(2, fr.getToUserId());
        assertNotEquals(0, fr.getToUserId());
    }

    @Test
    public void setToUserIdTest() {
        fr.setToUserId(4);
        assertEquals(4, fr.getToUserId());
        assertNotEquals(0, fr.getToUserId());
        fr.setToUserId(2);
    }

    @Test
    public void getTimestampTest() {
        assertEquals(TIMESTAMP1, fr.getSentAt());
        assertNotEquals(Timestamp.valueOf("2022-07-07 12:40:21"), fr.getSentAt());
    }

    @Test
    public void setTimestampTest() {
        fr.setSentAt(Timestamp.valueOf("2022-07-07 12:40:21"));
        assertNotEquals(TIMESTAMP1, fr.getSentAt());
        assertEquals(Timestamp.valueOf("2022-07-07 12:40:21"), fr.getSentAt());
        fr.setSentAt(TIMESTAMP1);
    }

    @Test
    public void getStatusTest() {
        assertEquals(FriendshipStatus.pending, fr.getStatus());
        assertNotEquals(FriendshipStatus.accepted, fr.getStatus());
    }

    @Test
    public void setStatusTest() {
        fr.setStatus(FriendshipStatus.accepted);
        assertNotEquals(FriendshipStatus.pending, fr.getStatus());
        assertEquals(FriendshipStatus.accepted, fr.getStatus());
        fr.setStatus(FriendshipStatus.pending);
    }

    @Test
    public void equalsTest() {
        assertFalse(fr.equals(null));
        FriendRequest f = new FriendRequest(1, 1, 2, FriendshipStatus.pending,  TIMESTAMP1);
        assertTrue(fr.equals(f));
        assertFalse(fr.equals(""));
        assertFalse(fr.equals(new FriendRequest(2, 1, 2, FriendshipStatus.pending,  TIMESTAMP1)));
        assertFalse(fr.equals(new FriendRequest(1, 2, 2, FriendshipStatus.pending,  TIMESTAMP1)));
        assertFalse(fr.equals(new FriendRequest(1, 1, 3, FriendshipStatus.pending,  TIMESTAMP1)));
        assertFalse(fr.equals(new FriendRequest(1, 1, 2, FriendshipStatus.accepted,  TIMESTAMP1)));
        assertFalse(fr.equals(new FriendRequest(1, 1, 2, FriendshipStatus.pending,  Timestamp.valueOf("2022-07-07 12:40:21"))));
    }
}
