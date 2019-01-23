package com.webServer;

import com.common.DbUtils;
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

@WebServlet("/api/webServer/changeBbsDeleteHava")
public class changeBbsDeleteHava extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        int deleteHava = Integer.valueOf(request.getParameter("deletehava"));
        int ID = Integer.valueOf(request.getParameter("ID"));

        try {
            /*Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://581d934d308eb.gz.cdb.myqcloud.com:6874/pet", "Yfp123456", "Yfp123456");*/
            Connection conn = DbUtils.getConnect();

            //查出帖子总数，每页10张
            PreparedStatement stmt = conn.prepareStatement("update t_bbs set deletehava = ? where ID = ?");
            stmt.setInt(1, deleteHava);
            stmt.setInt(2, ID);
            if (stmt.executeUpdate() == 1){
                resJo.put("res", "success");
            }else{
                resJo.put("res", "fail");
            }

        }catch (Exception e){
            getServletContext().log("update bbs deletehava err: "  +e.getMessage());
        }finally {

        }

        pw.println(resJo);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
