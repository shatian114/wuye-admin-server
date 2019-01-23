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

@WebServlet("/api/webServer/daoRuXiaoQu")
public class daoRuXiaoQu extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        int insertNum = 0, xlsColNum = 16;
        Connection conn = null;
        ResultSet res = null;

        try {
            conn = DbUtils.getConnect();
            PreparedStatement stmt = null;
            //获取xls的数据，转换为jsonarray
            JSONArray reqJa = new JSONArray(request.getParameter("xlsJson"));
            //检查wyid下面的xqbh是否有重复
            boolean testResBool = true;
            for (int i=1; i<reqJa.length(); i++){
                stmt = conn.prepareStatement("select * from t_1wuyexiaoqu where wyid = ? and xqbh = ? limit 1");
                stmt.setInt(1, com.common.utils.getIntParam(request, "wyid"));
                stmt.setString(2, reqJa.getJSONArray(i).getString(0));
                res = stmt.executeQuery();
                res.last();
                if(res.getRow() != 0){
                    System.out.println(res.getString("xqbh"));
                    resJo.put("ret", "fail");
                    resJo.put("info", "第" + (i+1) + "行的小区编号已存在服务器");
                    System.out.println("第" + (i+1) + "行的小区编号已存在服务器: " + reqJa.getJSONArray(i).getString(0));
                    testResBool = false;
                    break;
                }
            }
            //如果检查通过，则全部插入服务器
            if(testResBool){
                System.out.println("数据检查通过，开始插入数据库...");
                for(int i=1; i<reqJa.length(); i++){
                    stmt = conn.prepareStatement("insert into t_1wuyexiaoqu(wyid, xqbh, xqmc, sheng, shi, qu, ld, lds, hxs, gdc, lsc, wyf, sf, dkc, dmc, piclink) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    stmt.setInt(1, utils.getIntParam(request, "wyid"));
                    for(int j=0; j<15; j++){
                        stmt.setString(j + 2, reqJa.getJSONArray(i).getString(j));
                    }
                    insertNum += stmt.executeUpdate();
                }
                resJo.put("ret", "success");
                resJo.put("insertNum", insertNum);
            }
        }catch (Exception e){
            e.printStackTrace();
            getServletContext().log("import xiaoqu err: "  +e.getMessage());
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
