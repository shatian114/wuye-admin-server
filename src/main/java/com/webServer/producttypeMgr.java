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

@WebServlet("/api/webServer/producttypeMgr")
public class producttypeMgr extends HttpServlet {
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
                case "add":
                    stmt = conn.prepareStatement("insert into t_producttype set producttypeid = ?, producttypename = ?");
                    stmt.setInt(1, Integer.parseInt(request.getParameter("producttypeid")));
                    stmt.setString(2, request.getParameter("producttypename"));
                    if (stmt.executeUpdate() == 1){
                        resJo.put("ret", "success");
                    }else {
                        resJo.put("ret", "fail");
                    }
                    break;
                case "query":
                    int pagenum = Integer.parseInt(request.getParameter("pagenum"));
                    int pageindex = Integer.parseInt(request.getParameter("pageindex"));
                    String querySqlStr = "select * from t_producttype where producttypename like ?";
                    String producttypeidStr = request.getParameter("producttypeid").equals("") ? "" : (" and producttypeid = " + Integer.parseInt(request.getParameter("producttypeid")));
                    querySqlStr += producttypeidStr + " order by t_timeinfo desc limit ?, ?";
                    stmt = conn.prepareStatement(querySqlStr);
                    stmt.setString(1, "%" + request.getParameter("producttypename") + "%");
                    stmt.setInt(2, (pageindex-1)*pagenum);
                    stmt.setInt(3, pagenum);
                    res = stmt.executeQuery();
                    res.last();
                    int pageCount = res.getRow()/pagenum + 1;
                    resJo.put("pageCount", pageCount);
                    System.out.println("res count: " + res.getRow());
                    res.beforeFirst();
                    JSONArray ja = new JSONArray();
                    while (res.next()){
                        JSONObject jo = new JSONObject();
                        jo.put("ID", res.getInt("ID"));
                        jo.put("producttypeid", res.getString("producttypeid"));
                        jo.put("producttypename", res.getString("producttypename"));
                        ja.put(jo);
                    }
                    resJo.put("ret", "success");
                    resJo.put("nodes", ja);
                    break;
                case "del":
                    stmt = conn.prepareStatement("delete from t_producttype where ID = ?");
                    stmt.setInt(1, Integer.parseInt(request.getParameter("id")));
                    if (stmt.executeUpdate() == 1){
                        resJo.put("ret", "success");
                    }else {
                        resJo.put("ret", "fail");
                    }
                    break;
                case "change":
                    stmt = conn.prepareStatement("update t_producttype set producttypeid = ?, producttypename = ? where ID = ?");
                    stmt.setInt(1, Integer.parseInt(request.getParameter("producttypeid")));
                    stmt.setString(2, request.getParameter("producttypename"));
                    stmt.setInt(3, Integer.parseInt(request.getParameter("ID")));
                    if (stmt.executeUpdate() == 1){
                        resJo.put("ret", "success");
                    }else{
                        resJo.put("ret", "fail");
                        resJo.put("info", "sql exe err");
                    }
                    break;
            }
        }catch (Exception e){
            resJo.put("info", utils.getExceptionInfo(e));
            getServletContext().log("zhouBianShangQuanMgr err: " + utils.getExceptionInfo(e));
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
