package com.webServer;

import com.common.DbUtils;
import com.common.utils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet("/api/webServer/zhouBianShangQuanMgr")
public class zhouBianShangQuanMgr extends HttpServlet {
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
                    stmt = conn.prepareStatement("insert into t_arroundshopandservice set zone = ?, shopname = ?, shopdesc = ?, mobilephone = ?, telephone = ?, ispassed = ?, lat = ?, lng = ?, istop = ?, address = ?, tagindex = ?, mainpic = ?");
                    stmt.setString(1, request.getParameter("zone"));
                    stmt.setString(2, request.getParameter("shopname"));
                    stmt.setString(3, request.getParameter("shopdesc"));
                    stmt.setString(4, request.getParameter("mobilephone"));
                    stmt.setString(5, request.getParameter("telephone"));
                    stmt.setInt(6, Integer.parseInt(request.getParameter("zone")));
                    stmt.setString(7, request.getParameter("lat"));
                    stmt.setString(8, request.getParameter("lng"));
                    stmt.setInt(9, Integer.parseInt(request.getParameter("istop")));
                    stmt.setString(10, request.getParameter("address"));
                    stmt.setString(11, request.getParameter("tagindex"));
                    stmt.setString(12, "0");
                    if (stmt.executeUpdate() == 1){
                        resJo.put("ret", "success");
                        //查找id，作为图片的前缀标识
                        stmt = conn.prepareStatement("select * from t_arroundshopandservice where zone = ? and shopname = ? limit 1");
                        stmt.setInt(1, Integer.valueOf(request.getParameter("zone")));
                        stmt.setString(2, request.getParameter("shopname"));
                        res = stmt.executeQuery();
                        res.next();
                        resJo.put("id", res.getInt("ID"));
                    }else {
                        resJo.put("ret", "fail");
                    }
                    break;
                case "query":
                    int pagenum = Integer.parseInt(request.getParameter("pagenum"));
                    int pageindex = Integer.parseInt(request.getParameter("pageindex"));
                    String querySqlStr = "select * from t_arroundshopandservice where zone like ? and shopname like ? and shopdesc like ? and mobilephone like ? and telephone like ? and address like ?";
                    String ispassedStr = request.getParameter("ispassed").equals("2") ? " and ispassed != ?" : " and ispassed = ?";
                    System.out.println(ispassedStr);
                    String istopStr = request.getParameter("istop").equals("2") ? " and istop != ?" : " and istop = ?";
                    querySqlStr += (ispassedStr + istopStr + " order by t_timeinfo desc limit ?, ?");
                    System.out.println(querySqlStr);
                    stmt = conn.prepareStatement(querySqlStr);
                    stmt.setString(1, "%" + request.getParameter("zone") + "%");
                    stmt.setString(2, "%" + request.getParameter("shopname") + "%");
                    stmt.setString(3, "%" + request.getParameter("shopdesc") + "%");
                    stmt.setString(4, "%" + request.getParameter("mobilephone") + "%");
                    stmt.setString(5, "%" + request.getParameter("telephone") + "%");
                    stmt.setString(6, "%" + request.getParameter("address") + "%");
                    stmt.setInt(7, Integer.valueOf(request.getParameter("ispassed")));
                    stmt.setInt(8, Integer.valueOf(request.getParameter("istop")));
                    stmt.setInt(9, (pageindex-1)*pagenum);
                    stmt.setInt(10, pagenum);
                    res = stmt.executeQuery();
                    res.last();
                    System.out.println("res count: " + res.getRow());
                    res.beforeFirst();
                    JSONArray ja = new JSONArray();
                    while (res.next()){
                        System.out.println(res.getString("zone"));
                        JSONObject jo = new JSONObject();
                        jo.put("ID", res.getInt("ID"));
                        jo.put("zone", res.getString("zone"));
                        jo.put("shopname", res.getString("shopname"));
                        jo.put("shopdesc", res.getString("shopdesc"));
                        jo.put("mobilephone", res.getString("mobilephone"));
                        jo.put("telephone", res.getString("telephone"));
                        jo.put("address", res.getString("address"));
                        jo.put("lat", res.getString("lat"));
                        jo.put("lng", res.getString("lng"));
                        jo.put("ispassed", res.getInt("ispassed"));
                        jo.put("istop", res.getInt("istop"));
                        jo.put("mainpic", res.getString("mainpic"));
                        jo.put("tagindex", res.getString("tagindex"));
                        ja.put(jo);
                    }
                    resJo.put("ret", "success");
                    resJo.put("nodes", ja);
                    break;
                case "del":
                    stmt = conn.prepareStatement("delete from t_arroundshopandservice where ID = ?");
                    stmt.setInt(1, Integer.parseInt(request.getParameter("id")));
                    if (stmt.executeUpdate() == 1){
                        resJo.put("ret", "success");
                    }else {
                        resJo.put("ret", "fail");
                    }
                    break;
                case "change":
                    stmt = conn.prepareStatement("update t_arroundshopandservice set shopname = ?, shopdesc = ?, mobilephone = ?, telephone = ?, tagindex = ?, lat = ?, lng = ?, ispassed = ?, istop = ?, address = ? where ID = ?");
                    stmt.setString(1, request.getParameter("shopname"));
                    stmt.setString(2, request.getParameter("shopdesc"));
                    stmt.setString(3, request.getParameter("mobilephone"));
                    stmt.setString(4, request.getParameter("telephone"));
                    stmt.setString(5, request.getParameter("tagindex"));
                    stmt.setString(6, request.getParameter("lat"));
                    stmt.setString(7, request.getParameter("lng"));
                    stmt.setInt(8, Integer.parseInt(request.getParameter("ispassed")));
                    stmt.setInt(9, Integer.parseInt(request.getParameter("istop")));
                    stmt.setString(10, request.getParameter("address"));
                    stmt.setInt(11, Integer.parseInt(request.getParameter("ID")));
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
