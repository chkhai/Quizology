package com.freeuni.quizapp.util;

import com.freeuni.quizapp.util.PasswordHasher;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PasswordHasherTest {

    // Used http://www.sha1-online.com/
    @Test
    public void hashPasswordTest() throws NoSuchAlgorithmException {
        assertEquals("a85cce133b87c29967f0c4cce6eaf76bf5d3f68b", PasswordHasher.hashPassword("lkhiz23"));
        assertEquals("f87c1ea92d312bb8be0a16dfafd375f813f8255e", PasswordHasher.hashPassword("lchkh23"));
        assertEquals("a27d4f58662f66473fe3e5f50bd70c44c1513f0f", PasswordHasher.hashPassword("sansi23"));
        assertEquals("ccc28cccf8128a3f57f62b46407e4aa24f57a2b7", PasswordHasher.hashPassword("akave23"));
        assertEquals("dccb1290851d4887f849da9f1370629056592f36", PasswordHasher.hashPassword("lbegi23"));
    }

    @Test
    public void isDeterministic() throws NoSuchAlgorithmException {
        assertEquals(PasswordHasher.hashPassword("test1"), PasswordHasher.hashPassword("test1"));
        assertEquals(PasswordHasher.hashPassword("test2"), PasswordHasher.hashPassword("test2"));
    }
}
