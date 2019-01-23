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

@WebServlet("/api/webServer/delAgent")
public class delAgent extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();

        try {
            Connection conn = DbUtils.getConnect();
            PreparedStatement stmt = conn.prepareStatement("delete from t_agentcoopreate where ID = ?");
            stmt.setInt(1, Integer.valueOf(request.getParameter("id")));
            stmt.executeUpdate();

            resJo.put("res", "success");
        }catch (Exception e){
            e.printStackTrace();
            getServletContext().log("del agent err: "  +e.getMessage());
        }

        pw.println(resJo);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
