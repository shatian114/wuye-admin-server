package com.common;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class utils {
    public static Connection getConn(){
        Connection conn = null;
        try{
            Context initCtx = new InitialContext();
            DataSource ds = (DataSource) initCtx.lookup("java:/comp/env/jdbc/petDb");
            conn = ds.getConnection();
        }catch (Exception e){
            e.printStackTrace();
        }
        return conn;
    }

    public static JSONArray getResAllData(ResultSet res, ServletContext ctx){
        JSONArray ja = new JSONArray();
        try {
            ResultSetMetaData resMd = res.getMetaData();
            int colCount = resMd.getColumnCount();
            ctx.log("colCount: " + colCount);
            while (res.next()){
                JSONObject jo = new JSONObject();
                for (int i=1; i<=colCount; i++){
                    ctx.log("colclassName: " + resMd.getColumnClassName(i));
                    if (resMd.getColumnClassName(i).equals("java.lang.String")){
                        jo.put(resMd.getColumnLabel(i), res.getString(resMd.getColumnLabel(i)));
                    }else {
                        jo.put(resMd.getColumnLabel(i), res.getInt(resMd.getColumnLabel(i)));
                    }
                }
                ja.put(jo);
            }
            return ja;
        }catch (Exception e){
            ctx.log("getResAllData err: " + e.getMessage());
        }
        return ja;
    }

    public static String getExceptionInfo(Exception e){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(bos);
        e.printStackTrace(pout);
        String infoStr = new String(bos.toByteArray());
        try {
            pout.close();
        }catch (Exception e1){

        }
        return infoStr;
    }

    public static int getIntParam(HttpServletRequest request, String key) {
        return Integer.parseInt(request.getParameter(key));
    }

    public static float getFloatParam(HttpServletRequest request, String key) {
        return Float.parseFloat(request.getParameter(key));
    }
}
