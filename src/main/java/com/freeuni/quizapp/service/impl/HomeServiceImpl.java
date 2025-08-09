package com.freeuni.quizapp.service.impl;

import com.freeuni.quizapp.dao.impl.*;
import com.freeuni.quizapp.dao.interfaces.*;
import com.freeuni.quizapp.enums.ActionType;
import com.freeuni.quizapp.model.*;
import com.freeuni.quizapp.service.interfaces.HomeService;
import com.freeuni.quizapp.util.DBConnector;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HomeServiceImpl implements HomeService {
    private final Connection con;
    private final QuizResultDao quizResultDao;
    private final QuizDao quizDao;
    private final AchievementDao achievementDao;
    private final MessageDao messageDao;
    private final FriendSystemDao friendSystemDao;
    private final HttpServletRequest request;
    private final User user;

    public HomeServiceImpl(HttpServletRequest request) {
        try {
            con = DBConnector.getConnection();
            quizResultDao = new QuizResultDaoImpl(con);
            quizDao = new QuizDaoImpl(con);
            achievementDao = new AchievementDaoImpl(con);
            messageDao = new MessageDaoImpl(con);
            friendSystemDao  = new FriendSystemDaoImpl(con);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.request = request;
        user = (User) request.getSession().getAttribute("currentUser");
    }

    public void storePopularQuizzes() throws SQLException {
        List<Quiz> popularQuizzes = quizResultDao.listPopularQuizzes(5);
        request.setAttribute("popularQuizzes", popularQuizzes);
    }

    public void storeRecentlyTakenQuizzes() throws SQLException {
        if (user == null) return;
        List<QuizResult> quizResults = quizResultDao.getUsersQuizResults(user.getId());
        quizResults.sort((r1, r2) -> r2.getCompletedAt().compareTo(r1.getCompletedAt()));
        int n = 5;
        List<Quiz> res = null;
        if (!quizResults.isEmpty())
            res = new ArrayList<>();
        for (QuizResult quizResult : quizResults) {
            if (n-- < 1) break;
            res.add(quizDao.getQuizById(quizResult.getQuizId()));
        }
        request.setAttribute("recentlyTakenQuizzes", res);
    }

    public void storeRecentQuizzes() throws SQLException {
        List<Quiz> recentQuizzes = quizDao.listRecentQuizzes(5);
        request.setAttribute("recentQuizzes", recentQuizzes);
    }

    public void storeRecentlyCreatedQuizzes() throws SQLException {
        if (user != null) {
            List<Quiz> usersCreatedQuizzes = quizDao.findUsersCreatedQuizzes(user.getId());
            usersCreatedQuizzes.sort((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()));
            request.setAttribute("recentlyCreatedQuizzes", usersCreatedQuizzes);
        }
    }

    public void storeAchievements() throws SQLException {
        if (user != null) {
            List<Achievement> achievements = achievementDao.getAchievements(user.getId());
            request.setAttribute("achievements", achievements);
        }
    }

    public void storeMessages() throws SQLException {
        if (user != null) {
            List<User> messengers = messageDao.getInboxPeopleList(user.getId());
            List<Message> messages = new ArrayList<>();
            for (User messenger : messengers) {
                List<Message> toAdd = messageDao.getMessages((messenger.getId()), user.getId())
                        .stream().filter(x -> x.getReceiverId() == user.getId()).toList();
                messages.addAll(toAdd);
            }
            request.setAttribute("messages", messages);
        }
    }

    public void storeFriendsActivities() throws SQLException {
        if (user == null) return;
        List<Activity> activities = new ArrayList<>();
        List<User> friends = friendSystemDao.getUsersFriends(user.getId());
        for (User friend : friends) {
            List<Quiz> created = quizDao.findUsersCreatedQuizzes(friend.getId());
            for (Quiz quiz : created) {
                Activity activity = new Activity(friend, ActionType.quiz_created, quiz, quiz.getCreatedAt());
                activities.add(activity);
            }
            List<QuizResult> taken = quizResultDao.getUsersQuizResults(friend.getId());
            for (QuizResult quizResult : taken) {
                Quiz quiz = quizDao.getQuizById(quizResult.getQuizId());
                Activity activity = new Activity(friend, ActionType.quiz_taken,
                        quiz, quizResult.getCompletedAt());
                activities.add(activity);
            }
            List<Achievement> achievements = achievementDao.getAchievements(friend.getId());
            for (Achievement achievement : achievements) {
                Activity activity = new Activity(friend, ActionType.achievement_earned,
                        achievement.getType(), achievement.getAchievedAt());
                activities.add(activity);
            }
        }
        activities.sort((r1, r2) -> r2.getTimestamp().compareTo(r1.getTimestamp()));
        List<Activity> res =  new ArrayList<>();
        for (int i = 0; i < 5 && i < activities.size(); i++) {
            res.add(activities.get(i));
        }
        request.setAttribute("friendsActivities", res);
    }
}
