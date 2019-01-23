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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/api/webServer/getBbs")
public class getBbs extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        int pageNum = (Integer.valueOf(request.getParameter("pageNum"))-1)*10;
        int pageCount = 0;

        try {
            /*Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://581d934d308eb.gz.cdb.myqcloud.com:6874/pet", "Yfp123456", "Yfp123456");*/
            Connection conn = DbUtils.getConnect();

            //查出帖子总数，每页10张
            PreparedStatement stmt = conn.prepareStatement("select * from t_bbs");
            PreparedStatement tpStmt;
            ResultSet tpRes;
            ResultSet res = stmt.executeQuery();
            res.last();
            pageCount = res.getRow()/10+1;

            stmt = conn.prepareStatement("select * from t_bbs order by t_timeinfo desc limit ?, ?");
            stmt.setInt(1, pageNum);
            stmt.setInt(2, 10);
            res = stmt.executeQuery();

            JSONArray ja = new JSONArray();
            while (res.next()){
                JSONObject jo = new JSONObject();
                jo.put("ID", res.getString("ID"));
                jo.put("userid", res.getString("userid"));
                jo.put("t_timeinfo", res.getString("t_timeinfo"));
                jo.put("text", res.getString("text"));
                jo.put("deletehava", res.getString("deletehava"));
                jo.put("tagindex", res.getString("tagindex"));

                //查询图片
                JSONArray tpJa = new JSONArray();
                tpStmt = conn.prepareStatement("select piclink from t_picture where tagindex = ?");
                tpStmt.setString(1, res.getString("tagindex"));
                tpRes = tpStmt.executeQuery();
                while (tpRes.next()){
                    tpJa.put(tpRes.getString("piclink"));
                }

                jo.put("tpArr", tpJa);
                ja.put(jo);
            }
            resJo.put("pageCount", pageCount);
            resJo.put("data", ja);
            resJo.put("res", "success");
        }catch (Exception e){
            e.printStackTrace();
            getServletContext().log("get bbs err: "  +e.getMessage());
        }

        pw.println(resJo);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
