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

@WebServlet("/api/webServer/daoRuWuYeFei")
public class daoRuWuYeFei extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        int insertNum = 0, xlsColNum = 18;
        Connection conn = null;

        try {
            conn = DbUtils.getConnect();
            PreparedStatement stmt = null;
            //获取xls的数据，转换为jsonarray
            JSONArray reqJa = new JSONArray(request.getParameter("xlsJson"));
            System.out.println(reqJa);
            //处理每行的数据
            for (int i=1; i<reqJa.length(); i++){
                System.out.println("start import " + i);
                try {
                    JSONArray rowJa = new JSONArray(reqJa.get(i).toString());
                    System.out.println("row length: " + rowJa.length());
                    //验证每行的数据
                    if (rowJa != null && rowJa.length() >= xlsColNum){
                        boolean jaValid = true;
                        stmt = conn.prepareStatement("insert into t_1wuyejiaofei(yzbh, mj, wydj, bqwy, cph, dk, dm, dkcj, dmcj, bycf, cfqj, bqcf, sf, byds, bysf, sfqj, bqsf, dqxj) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        for(int j=0; j<xlsColNum; j++){
                            if (rowJa.get(j).toString().length() == 0){
                                System.out.println("col " + j + " is null");
                                jaValid = false;
                                break;
                            }
                            stmt.setString(j+1, rowJa.get(j).toString());
                        }
                        if (jaValid && stmt.executeUpdate() == 1) {
                            System.out.println("insert " + rowJa.get(0));
                            insertNum++;
                        }else {
                            System.out.println("no insert " + rowJa.get(0));
                        }
                    }
                }catch (Exception e1){
                    e1.printStackTrace();

                }

            }
            resJo.put("ret", "success");
            resJo.put("insertNum", insertNum);
        }catch (Exception e){
            e.printStackTrace();
            getServletContext().log("import WuYeGongSi err: "  +e.getMessage());
        }finally {
            DbUtils.closeConn(conn);
        }

        pw.println(resJo);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
