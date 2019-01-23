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

@WebServlet("/api/webServer/addAgent")
public class addAgent extends HttpServlet {
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

            //判断添加或修改
            if (request.getParameter("addOrChange").equals("add")){
                //添加代理，先查找要添加的代理是否存在
                stmt = conn.prepareStatement("select uniqueID from t_agentcoopreate where uniqueID = ? limit 1");
                stmt.setInt(1, Integer.valueOf(request.getParameter("uniqueID")));
                res = stmt.executeQuery();
                res.last();
                System.out.println(res.getRow());
                if (res.getRow() == 1){
                    resJo.put("res", "uniqueID已存在");
                }else {
                    stmt = conn.prepareStatement("insert into t_agentcoopreate set uniqueID = ?, password = ?, mobile = ?, email = ?, nickname = ?, rate = ?, ispaygurantee = ?, guranteeamount = ?, qq = ?, weixin = ?, zone = ?, clearperoid = ?, isnormal = ?");
                    stmt.setInt(1, Integer.valueOf(request.getParameter("uniqueID")));
                    stmt.setString(2, request.getParameter("password"));
                    stmt.setString(3, request.getParameter("mobile"));
                    stmt.setString(4, request.getParameter("email"));
                    stmt.setString(5, request.getParameter("nickname"));
                    stmt.setInt(6, Integer.valueOf(request.getParameter("rate")));
                    stmt.setInt(7, Integer.valueOf(request.getParameter("ispaygurantee")));
                    stmt.setInt(8, Integer.valueOf(request.getParameter("guranteeamount")));
                    stmt.setString(9, request.getParameter("qq"));
                    stmt.setString(10, request.getParameter("weixin"));
                    stmt.setString(11, request.getParameter("zone"));
                    stmt.setInt(12, Integer.valueOf(request.getParameter("clearperoid")));
                    stmt.setInt(13, Integer.valueOf(request.getParameter("isnormal")));
                    if (stmt.executeUpdate() == 1){
                        resJo.put("res", "添加成功");
                    }else {
                        resJo.put("res", "数据库错误");
                    }
                }
            }else {
                stmt = conn.prepareStatement("update t_agentcoopreate set uniqueID = ?, password = ?, mobile = ?, email = ?, nickname = ?, rate = ?, ispaygurantee = ?, guranteeamount = ?, qq = ?, weixin = ?, zone = ?, clearperoid = ?, isnormal = ? where ID = ?");
                stmt.setInt(1, Integer.valueOf(request.getParameter("uniqueID")));
                stmt.setString(2, request.getParameter("password"));
                stmt.setString(3, request.getParameter("mobile"));
                stmt.setString(4, request.getParameter("email"));
                stmt.setString(5, request.getParameter("nickname"));
                stmt.setInt(6, Integer.valueOf(request.getParameter("rate")));
                stmt.setInt(7, Integer.valueOf(request.getParameter("ispaygurantee")));
                stmt.setInt(8, Integer.valueOf(request.getParameter("guranteeamount")));
                stmt.setString(9, request.getParameter("qq"));
                stmt.setString(10, request.getParameter("weixin"));
                stmt.setString(11, request.getParameter("zone"));
                stmt.setInt(12, Integer.valueOf(request.getParameter("clearperoid")));
                stmt.setInt(13, Integer.valueOf(request.getParameter("isnormal")));
                stmt.setInt(14, Integer.valueOf(request.getParameter("ID")));
                if (stmt.executeUpdate() == 1){
                    resJo.put("res", "修改成功");
                }else {
                    resJo.put("res", "数据库错误");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            getServletContext().log("change agent err: "  +e.getMessage());
        }finally {
            DbUtils.closeResultSet(res);
            DbUtils.closeConn(conn);
        }

        pw.println(resJo);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
