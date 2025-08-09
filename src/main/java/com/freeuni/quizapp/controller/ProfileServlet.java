package com.freeuni.quizapp.controller;

import com.freeuni.quizapp.dao.impl.FriendSystemDaoImpl;
import com.freeuni.quizapp.dao.impl.QuizDaoImpl;
import com.freeuni.quizapp.dao.impl.UserDaoImpl;
import com.freeuni.quizapp.dao.interfaces.FriendSystemDao;
import com.freeuni.quizapp.enums.AchievementType;
import com.freeuni.quizapp.enums.FriendshipStatus;
import com.freeuni.quizapp.model.Achievement;
import com.freeuni.quizapp.model.Quiz;
import com.freeuni.quizapp.model.QuizResult;
import com.freeuni.quizapp.model.User;
import com.freeuni.quizapp.service.impl.ProfileServiceImpl;
import com.freeuni.quizapp.service.interfaces.ProfileService;
import com.freeuni.quizapp.util.DBConnector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final ProfileService profileService = new ProfileServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) (session != null ? session.getAttribute("currentUser") : null);

        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String requestedUsername = request.getParameter("username");
        User profileUser = currentUser;
        boolean isViewingOwnProfile = true;

        try {
            if (requestedUsername != null && !requestedUsername.equals(currentUser.getUsername())) {
                try (Connection conn = DBConnector.getConnection()) {
                    UserDaoImpl userDao = new UserDaoImpl(conn);
                    User requestedUser = userDao.getByUsername(requestedUsername, true);
                    if (requestedUser != null) {
                        profileUser = requestedUser;
                        isViewingOwnProfile = false;
                    } else {
                        response.sendRedirect("profile");
                        return;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    response.sendRedirect("profile");
                    return;
                }
            }

            boolean editMode = "true".equals(request.getParameter("edit")) && isViewingOwnProfile;

            FriendshipStatus friendshipStatus = null;
            if (!isViewingOwnProfile) {
                try (Connection friendConn = DBConnector.getConnection()) {
                    FriendSystemDao friendSystemDao = new FriendSystemDaoImpl(friendConn);
                    friendshipStatus = friendSystemDao.getFriendshipStatus(currentUser.getId(), profileUser.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            try {
                List<Quiz> createdQuizzes = profileService.getUserCreatedQuizzes(profileUser.getId());
                Map<Integer, Integer> questionCounts = profileService.getQuestionCounts(createdQuizzes);
                List<QuizResult> quizResults = profileService.getUserQuizResults(profileUser.getId());
                List<String> activityHistory = profileService.buildActivityHistory(quizResults, 5);
                List<Achievement> achievements = profileService.getUserAchievements(profileUser.getId());


                List<String> greatestQuizNames = new ArrayList<>();
                if (achievements != null) {
                    try (Connection conn = DBConnector.getConnection()) {
                        QuizDaoImpl quizDao = new QuizDaoImpl(conn);
                        for (Achievement achievement : achievements) {
                            if (achievement.getType() == com.freeuni.quizapp.enums.AchievementType.I_am_the_Greatest 
                                && achievement.getQuiz_id() > 0) {
                                Quiz quiz = quizDao.getQuizById(achievement.getQuiz_id());
                                if (quiz != null) {
                                    greatestQuizNames.add(quiz.getTitle());
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                int quizzesCreated = createdQuizzes != null ? createdQuizzes.size() : 0;
                int quizzesTaken = quizResults != null ? quizResults.size() : 0;

                if (achievements == null) {
                    achievements = new ArrayList<>();
                }
                
                achievements.removeIf(achievement -> 
                    achievement.getType() == AchievementType.Amateur_Author ||
                    achievement.getType() == AchievementType.Prolific_Author ||
                    achievement.getType() == AchievementType.Prodigious_Author
                );
                
                if (quizzesCreated >= 10) {
                    Achievement prodigiousAuthor = new Achievement(
                        0, 
                        profileUser.getId(),
                        AchievementType.Prodigious_Author,
                        0, 
                        new java.sql.Timestamp(System.currentTimeMillis())
                    );
                    achievements.add(prodigiousAuthor);
                } else if (quizzesCreated >= 5) {
                    Achievement prolificAuthor = new Achievement(
                        0, 
                        profileUser.getId(),
                        AchievementType.Prolific_Author,
                        0, 
                        new java.sql.Timestamp(System.currentTimeMillis())
                    );
                    achievements.add(prolificAuthor);
                } else if (quizzesCreated >= 1) {
                    Achievement amateurAuthor = new Achievement(
                        0, 
                        profileUser.getId(),
                        AchievementType.Amateur_Author,
                        0, 
                        new java.sql.Timestamp(System.currentTimeMillis())
                    );
                    achievements.add(amateurAuthor);
                }

                request.setAttribute("profileUser", profileUser);
                request.setAttribute("isViewingOwnProfile", isViewingOwnProfile);
                request.setAttribute("quizzesCreated", quizzesCreated);
                request.setAttribute("quizzesTaken", quizzesTaken);
                request.setAttribute("createdQuizzes", createdQuizzes);
                request.setAttribute("questionCounts", questionCounts);
                request.setAttribute("history", activityHistory);
                request.setAttribute("achievements", achievements);
                request.setAttribute("greatestQuizNames", greatestQuizNames);
                request.setAttribute("editMode", editMode);
                request.setAttribute("friendshipStatus", friendshipStatus);

            } catch (Exception e) {
                e.printStackTrace();
                
                List<Achievement> fallbackAchievements = new ArrayList<>();
                int fallbackQuizzesCreated = 0;
                
                try {
                    List<Quiz> fallbackCreatedQuizzes = profileService.getUserCreatedQuizzes(profileUser.getId());
                    fallbackQuizzesCreated = fallbackCreatedQuizzes != null ? fallbackCreatedQuizzes.size() : 0;
                    
                    if (fallbackQuizzesCreated >= 10) {
                        fallbackAchievements.add(new Achievement(0, profileUser.getId(), AchievementType.Prodigious_Author, 0, new java.sql.Timestamp(System.currentTimeMillis())));
                    } else if (fallbackQuizzesCreated >= 5) {
                        fallbackAchievements.add(new Achievement(0, profileUser.getId(), AchievementType.Prolific_Author, 0, new java.sql.Timestamp(System.currentTimeMillis())));
                    } else if (fallbackQuizzesCreated >= 1) {
                        fallbackAchievements.add(new Achievement(0, profileUser.getId(), AchievementType.Amateur_Author, 0, new java.sql.Timestamp(System.currentTimeMillis())));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                
                request.setAttribute("profileUser", profileUser);
                request.setAttribute("isViewingOwnProfile", isViewingOwnProfile);
                request.setAttribute("quizzesCreated", fallbackQuizzesCreated);
                request.setAttribute("quizzesTaken", 0);
                request.setAttribute("createdQuizzes", null);
                request.setAttribute("questionCounts", null);
                request.setAttribute("history", null);
                request.setAttribute("achievements", fallbackAchievements);
                request.setAttribute("greatestQuizNames", new ArrayList<>());
                request.setAttribute("editMode", editMode);
                request.setAttribute("friendshipStatus", friendshipStatus);
            }

            request.getRequestDispatcher("profile.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("profile");
        }
    }
} 