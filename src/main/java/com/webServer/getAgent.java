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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/api/webServer/getAgent")
public class getAgent extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        int pageNum = (Integer.valueOf(request.getParameter("pageNum"))-1)*10;
        int pageCount = 0;
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DbUtils.getConnect();
            /*Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://581d934d308eb.gz.cdb.myqcloud.com:6874/pet", "Yfp123456", "Yfp123456");*/

            //查出帖子总数，每页10张
            stmt = conn.prepareStatement("select * from t_agentcoopreate");
            ResultSet res = stmt.executeQuery();
            res.last();
            pageCount = res.getRow()/10+1;

            stmt = conn.prepareStatement("select * from t_agentcoopreate where uniqueID like ? order by t_timeinfo desc limit ?, ?");
            stmt.setString(1, "%" + request.getParameter("searchField") + "%");
            stmt.setInt(2, pageNum);
            stmt.setInt(3, 10);
            res = stmt.executeQuery();

            JSONArray ja = utils.getResAllData(res, getServletContext());
            res.close();
            resJo.put("pageCount", pageCount);
            resJo.put("data", ja);
            resJo.put("res", "success");
        }catch (Exception e){
            e.printStackTrace();
            getServletContext().log("get agent err: "  +e.getMessage());
        }finally {
            try {
                if (stmt != null){
                    stmt.close();
                }
                if (conn != null) conn.close();
            }catch (Exception e){

            }
        }

        pw.println(resJo);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
