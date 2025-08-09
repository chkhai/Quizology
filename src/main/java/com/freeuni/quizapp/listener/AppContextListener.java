package com.freeuni.quizapp.listener;

import com.freeuni.quizapp.util.DBConnector;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

@WebListener
public class AppContextListener implements ServletContextListener {
    
    private static final ConcurrentHashMap<String, Connection> connectionPool = new ConcurrentHashMap<>();
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Connection testConnection = DBConnector.getConnection();
            if (testConnection != null) {
                testConnection.close();
                sce.getServletContext().setAttribute("dbInitialized", true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sce.getServletContext().setAttribute("dbInitialized", false);
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        connectionPool.values().forEach(connection -> {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        connectionPool.clear();
    }
    
    public static Connection getPooledConnection(String sessionId) {
        try {
            return connectionPool.computeIfAbsent(sessionId, k -> {
                try {
                    return DBConnector.getConnection();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void removeConnection(String sessionId) {
        Connection conn = connectionPool.remove(sessionId);
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
} 