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
import java.util.Enumeration;

@WebServlet("/api/webServer/daoRuWuYeGongSi")
public class daoRuWuYeGongSi extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        int insertNum = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet res = null;

        try {
            conn = DbUtils.getConnect();

            //获取xls的数据，转换为jsonarray
            JSONArray reqJa = new JSONArray(request.getParameter("xlsJson"));
            //对数据进行重复检查
            boolean testResBool = true;
            //索引从1开始，过滤掉标题行的数据
            for (int i=1; i<reqJa.length(); i++){
                //查找全部物业编号是否有重复的
                stmt = conn.prepareStatement("select wybh from t_1wuyewuye where wybh = ? limit 1");
                stmt.setString(1, reqJa.getJSONArray(i).getString(0));
                res = stmt.executeQuery();
                System.out.println(res.getRow());
                res.last();
                System.out.println(res.getRow());
                if(res.getRow() != 0){
                    System.out.println(res.getString("wybh"));
                    resJo.put("ret", "fail");
                    resJo.put("info", "第" + (i+1) + "行的物业编号已存在服务器");
                    System.out.println("第" + (i+1) + "行的物业编号已存在服务器: " + reqJa.getJSONArray(i).getString(0));
                    testResBool = false;
                    break;
                }
                //查找全部物业名称和描述是否有重复
                stmt = conn.prepareStatement("select * from t_1wuyewuye where wymc = ? and wyms = ? limit 1");
                stmt.setString(1, reqJa.getJSONArray(i).getString(1));
                stmt.setString(2, reqJa.getJSONArray(i).getString(2));
                res = stmt.executeQuery();
                res.last();
                if(res.getRow() != 0){
                    resJo.put("ret", "fail");
                    resJo.put("info", "第" + (i+2) + "行的物业名称和描述已存在服务器");
                    testResBool = false;
                    break;
                }
            }

            //如果检查通过，则全部插入服务器
            if(testResBool){
                System.out.println("数据检查通过，开始插入数据库...");
                for(int i=1; i<reqJa.length(); i++){
                    stmt = conn.prepareStatement("insert into t_1wuyewuye(wybh, wymc, wyms) values(?, ?, ?)");
                    stmt.setString(1, reqJa.getJSONArray(i).getString(0));
                    stmt.setString(2, reqJa.getJSONArray(i).getString(1));
                    stmt.setString(3, reqJa.getJSONArray(i).getString(2));
                    insertNum += stmt.executeUpdate();
                }
                resJo.put("ret", "success");
                resJo.put("insertNum", insertNum);
            }
        }catch (Exception e){
            e.printStackTrace();
            getServletContext().log("import WuYeGongSi err: "  +e.getMessage());
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
