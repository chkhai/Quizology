package com.freeuni.quizapp.model;

import com.freeuni.quizapp.enums.FriendshipStatus;

import java.sql.Timestamp;
import java.util.Objects;

public class FriendRequest {
    private int id;
    private int fromUserId;
    private int toUserId;
    private FriendshipStatus status;
    private Timestamp sentAt;

    public FriendRequest(int id, int fromUserId, int toUserId, FriendshipStatus status, Timestamp sentAt) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.status = status;
        this.sentAt = sentAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(int fromUserId) {
        this.fromUserId = fromUserId;
    }

    public int getToUserId() {
        return toUserId;
    }

    public void setToUserId(int toUserId) {
        this.toUserId = toUserId;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FriendRequest that = (FriendRequest) o;
        return id == that.id && fromUserId == that.fromUserId && toUserId == that.toUserId && status == that.status && Objects.equals(sentAt, that.sentAt);
    }

}
