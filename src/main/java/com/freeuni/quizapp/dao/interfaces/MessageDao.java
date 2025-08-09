package com.freeuni.quizapp.dao.interfaces;

import com.freeuni.quizapp.enums.MessageType;
import com.freeuni.quizapp.model.Message;
import com.freeuni.quizapp.model.User;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public interface MessageDao {

    void addMessage(int from_id, int to_id, MessageType type, String text) throws SQLException;

    void removeMessage(int m_id) throws SQLException;

    Message getLastMessage(int from_id, int to_id) throws SQLException;

    List<Message> getMessages(int from_id, int to_id) throws SQLException;

    List<User> getInboxPeopleList(int user_id) throws SQLException;

    Message getMessage(int m_id) throws SQLException;

    Timestamp getSentMessageTime(int from_id, int to_id, String text) throws SQLException;
}
