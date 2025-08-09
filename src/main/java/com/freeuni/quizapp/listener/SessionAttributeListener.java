package com.freeuni.quizapp.listener;

import com.freeuni.quizapp.model.User;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.concurrent.ConcurrentHashMap;

@WebListener
public class SessionAttributeListener implements HttpSessionAttributeListener {
    
    private static final ConcurrentHashMap<String, Long> userLoginTimes = new ConcurrentHashMap<>();
    
    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        if ("currentUser".equals(se.getName())) {
            User user = (User) se.getValue();
            if (user != null) {
                String sessionId = se.getSession().getId();
                String username = user.getUsername();
                
                SessionListener.addUserSession(username, sessionId);
                userLoginTimes.put(username, System.currentTimeMillis());
                
                se.getSession().getServletContext().setAttribute("loggedInUsers", 
                    userLoginTimes.size());
            }
        }
    }
    
    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        if ("currentUser".equals(se.getName())) {
            User user = (User) se.getValue();
            if (user != null) {
                String username = user.getUsername();
                SessionListener.removeUserSession(username);
                userLoginTimes.remove(username);
                
                se.getSession().getServletContext().setAttribute("loggedInUsers", 
                    userLoginTimes.size());
            }
        }
    }
    
    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        if ("currentUser".equals(se.getName())) {
            User oldUser = (User) se.getValue();
            User newUser = (User) se.getSession().getAttribute("currentUser");
            
            if (oldUser != null) {
                SessionListener.removeUserSession(oldUser.getUsername());
                userLoginTimes.remove(oldUser.getUsername());
            }
            
            if (newUser != null) {
                String sessionId = se.getSession().getId();
                String username = newUser.getUsername();
                
                SessionListener.addUserSession(username, sessionId);
                userLoginTimes.put(username, System.currentTimeMillis());
            }
            
            se.getSession().getServletContext().setAttribute("loggedInUsers", 
                userLoginTimes.size());
        }
    }
    
    public static boolean isUserCurrentlyLoggedIn(String username) {
        return userLoginTimes.containsKey(username);
    }
    
    public static Long getUserLoginTime(String username) {
        return userLoginTimes.get(username);
    }
    
    public static int getLoggedInUserCount() {
        return userLoginTimes.size();
    }
} 