package com.freeuni.quizapp.model;
import com.freeuni.quizapp.dao.impl.FriendSystemDaoImpl;
import com.freeuni.quizapp.dao.impl.QuizDaoImpl;
import com.freeuni.quizapp.dao.impl.QuizResultDaoImpl;
import com.freeuni.quizapp.dao.interfaces.FriendSystemDao;
import com.freeuni.quizapp.dao.interfaces.QuizDao;
import com.freeuni.quizapp.dao.interfaces.QuizResultDao;
import com.freeuni.quizapp.util.DBConnector;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;


public class User {
    private int id;
    private String username;
    private String hashedPassword;
    private boolean isAdmin;
    private Timestamp createdAt;
    private String bio;
    private String profilePictureUrl;
    private List<Quiz> quizzesCreated;
    private List<QuizResult> quizzesTaken;
    private List<User> friends;

    public User(int id, String username, String hashed_password, boolean isAdmin, Timestamp createdAt, String bio, String profilePictureUrl) {
        this.id = id;
        this.username = username;
        this.hashedPassword = hashed_password;
        this.isAdmin = isAdmin;
        this.createdAt = createdAt;
        this.bio = bio;
        this.profilePictureUrl = profilePictureUrl;
    }

    public List<Quiz> getQuizzesCreated() {
        return quizzesCreated;
    }

    public void setQuizzesCreated(List<Quiz> quizzesCreated) {
        this.quizzesCreated = quizzesCreated;
    }

    public List<QuizResult> getQuizzesTaken() {
        return quizzesTaken;
    }

    public void setQuizzesTaken(List<QuizResult> quizzesTaken) {
        this.quizzesTaken = quizzesTaken;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getBio() {
        return bio;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && isAdmin == user.isAdmin && Objects.equals(username, user.username) && Objects.equals(hashedPassword, user.hashedPassword) && Objects.equals(createdAt, user.createdAt) && Objects.equals(bio, user.bio) && Objects.equals(profilePictureUrl, user.profilePictureUrl) && Objects.equals(quizzesCreated, user.quizzesCreated) && Objects.equals(quizzesTaken, user.quizzesTaken) && Objects.equals(friends, user.friends);
    }

}
