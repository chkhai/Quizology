package com.freeuni.quizapp.model;

import com.freeuni.quizapp.enums.MessageType;

import java.sql.Timestamp;
import java.util.Objects;

public class Message {
        private int id;
        private int senderId;
        private int receiverId;
        private MessageType type;
        private String content;
        private Timestamp sentAt;

    public Message(int id, int senderId, int receiverId, MessageType type, String content, Timestamp sentAt) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
        this.content = content;
        this.sentAt = sentAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        Message message = (Message) o;
        return id == message.id && senderId == message.senderId && receiverId == message.receiverId && type == message.type && Objects.equals(content, message.content) && Objects.equals(sentAt, message.sentAt);
    }

}
