package com.common;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

public class myInit implements ServletContextListener {
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("init pet-mgr");
        DbUtils.initPool();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("destroy pet-mgr");
    }
}
