package com.webServer;

import com.common.DbUtils;
import com.common.utils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/api/webServer/t_1wuyexiaoquMgr")
public class t_1wuyexiaoquMgr extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        Connection conn = null;
        ResultSet res = null;

        try {
            conn = DbUtils.getConnect();
            PreparedStatement stmt = null;
            System.out.println(request.getParameter("command"));
            switch (request.getParameter("command")) {
                case "getWuYeXiaoQuList":
                    stmt = conn.prepareStatement("select ID, xqbh, xqmc from t_1wuyexiaoqu where wyid = ?");
                    stmt.setInt(1, utils.getIntParam(request, "wyid"));
                    res = stmt.executeQuery();
                    JSONArray ja = new JSONArray();
                    while (res.next()){
                        JSONObject jo = new JSONObject();
                        jo.put("ID", res.getInt("ID"));
                        jo.put("xqbh", res.getString("xqbh"));
                        jo.put("xqmc", res.getString("xqmc"));
                        ja.put(jo);
                    }
                    resJo.put("ret", "success");
                    resJo.put("nodes", ja);
                    break;
            }
        }catch (Exception e){
            resJo.put("info", utils.getExceptionInfo(e));
            getServletContext().log("productMgr err: " + utils.getExceptionInfo(e));
        }finally {
            DbUtils.closeConn(conn);
            DbUtils.closeResultSet(res);
        }

        pw.println(resJo);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
