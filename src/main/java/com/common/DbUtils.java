package com.common;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.sql.Connection;
import java.sql.ResultSet;

public class DbUtils {

    static DataSource ds = new DataSource();

    public static void initPool(){
        System.out.println("start init datapool");
        PoolProperties p = new PoolProperties();
        p.setUrl("jdbc:mysql://581d934d308eb.gz.cdb.myqcloud.com:6874/pzhsq");
        p.setDriverClassName("com.mysql.cj.jdbc.Driver");
        p.setUsername("Yfp123456");
        p.setPassword("Yfp123456");
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(10);
        p.setInitialSize(5);
        p.setMaxWait(50);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(10);
        p.setLogAbandoned(false);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
                        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        ds.setPoolProperties(p);
        System.out.println("datapool init success");
    }

    public static Connection getConnect(){
        try {
            return ds.getConnection();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void closeResultSet(ResultSet res){
        if (res != null){
            try {
                res.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void closeConn(Connection conn){
        if (conn != null){
            try {
                conn.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
