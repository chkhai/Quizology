package com.freeuni.quizapp.listener;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@WebListener
public class SessionListener implements HttpSessionListener {
    
    private static final AtomicInteger activeSessions = new AtomicInteger(0);
    private static final ConcurrentHashMap<String, String> sessionUsers = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> userSessions = new ConcurrentHashMap<>();
    
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        activeSessions.incrementAndGet();
        se.getSession().getServletContext().setAttribute("activeSessions", activeSessions.get());
    }
    
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        activeSessions.decrementAndGet();
        se.getSession().getServletContext().setAttribute("activeSessions", activeSessions.get());
        
        String sessionId = se.getSession().getId();
        String username = sessionUsers.remove(sessionId);
        if (username != null) {
            userSessions.remove(username);
        }
        
        AppContextListener.removeConnection(sessionId);
    }
    
    public static void addUserSession(String username, String sessionId) {
        String oldSessionId = userSessions.put(username, sessionId);
        sessionUsers.put(sessionId, username);
        
        if (oldSessionId != null && !oldSessionId.equals(sessionId)) {
            sessionUsers.remove(oldSessionId);
            AppContextListener.removeConnection(oldSessionId);
        }
    }
    
    public static void removeUserSession(String username) {
        String sessionId = userSessions.remove(username);
        if (sessionId != null) {
            sessionUsers.remove(sessionId);
            AppContextListener.removeConnection(sessionId);
        }
    }
    
    public static boolean isUserLoggedIn(String username) {
        return userSessions.containsKey(username);
    }
    
    public static String getUserSession(String username) {
        return userSessions.get(username);
    }
    
    public static int getActiveSessionCount() {
        return activeSessions.get();
    }
} 