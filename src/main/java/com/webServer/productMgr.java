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

@WebServlet("/api/webServer/productMgr")
public class productMgr extends HttpServlet {
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
                    stmt = conn.prepareStatement("insert into t_product set producttypeid = ?, productid = ?, productname = ?, videoid = ?, videolink = ?, tagindex = ?, zone = ?, shoptag = ?, ispassed = ?, istop = ?, orderindex = ?, productdes = ?, num = ?, price = ?, ishowvideolink = ?, isneeduserpic = ?, isneeduserinfo = ?, isneeduseraddress = ?, isneeddesktag = ?, isatmain = ?");
                    stmt.setInt(1, utils.getIntParam(request, "producttypeid"));
                    stmt.setInt(2, utils.getIntParam(request, "productid"));
                    stmt.setString(3, request.getParameter("productname"));
                    stmt.setString(4, request.getParameter("videoid"));
                    stmt.setString(5, request.getParameter("videolink"));
                    stmt.setString(6, request.getParameter("tagindex"));
                    stmt.setString(7, request.getParameter("zone"));
                    stmt.setString(8, request.getParameter("shoptag"));
                    stmt.setInt(9, utils.getIntParam(request, "ispassed"));
                    stmt.setInt(10, utils.getIntParam(request, "istop"));
                    stmt.setInt(11, utils.getIntParam(request, "orderindex"));
                    stmt.setString(12, request.getParameter("productdes"));
                    stmt.setInt(13, utils.getIntParam(request, "num"));
                    stmt.setFloat(14, utils.getFloatParam(request, "price"));
                    stmt.setInt(15, utils.getIntParam(request, "ishowvideolink"));
                    stmt.setInt(16, utils.getIntParam(request, "isneeduserpic"));
                    stmt.setInt(17, utils.getIntParam(request, "isneeduserinfo"));
                    stmt.setInt(18, utils.getIntParam(request, "isneeduseraddress"));
                    stmt.setInt(19, utils.getIntParam(request, "isneeddesktag"));
                    stmt.setInt(20, utils.getIntParam(request, "isatmain"));
                    if (stmt.executeUpdate() == 1){
                        resJo.put("ret", "success");
                        //查找id，作为图片的前缀标识
                        stmt = conn.prepareStatement("select * from t_product where productid = ? limit 1");
                        stmt.setString(1, request.getParameter("productid"));
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
                    String querySqlStr = "select * from t_product where productname like ? and zone like ? and shoptag like ? and productid like ?";
                    String producttypeidStr = request.getParameter("producttypeid").equals("") ? "" : (" and producttypeid = " + Integer.parseInt(request.getParameter("producttypeid")));
                    String ispassedStr = request.getParameter("ispassed").equals("2") ? "" : (" and ispassed = " + Integer.parseInt(request.getParameter("ispassed")));
                    String istopStr = request.getParameter("istop").equals("2") ? "" : (" and istop = " + Integer.parseInt(request.getParameter("istop")));
                    querySqlStr += producttypeidStr + ispassedStr + istopStr + " order by t_timeinfo desc limit ?, ?";
                    stmt = conn.prepareStatement(querySqlStr);
                    stmt.setString(1, "%" + request.getParameter("productname") + "%");
                    stmt.setString(2, "%" + request.getParameter("zone") + "%");
                    stmt.setString(3, "%" + request.getParameter("shoptag") + "%");
                    stmt.setString(4, "%" + request.getParameter("productid") + "%");
                    stmt.setInt(5, (pageindex-1)*pagenum);
                    stmt.setInt(6, pagenum);
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
                        jo.put("producttypeid", res.getInt("producttypeid"));
                        jo.put("productid", res.getString("productid"));
                        jo.put("productname", res.getString("productname"));
                        jo.put("zone", res.getString("zone"));
                        jo.put("tagindex", res.getString("tagindex"));
                        jo.put("num", res.getInt("num"));
                        jo.put("productdes", res.getString("productdes"));
                        jo.put("price", res.getFloat("price"));
                        jo.put("ishowvideolink", res.getInt("ishowvideolink"));
                        jo.put("videolink", res.getString("videolink"));
                        jo.put("ispassed", res.getInt("ispassed"));
                        jo.put("istop", res.getInt("istop"));
                        jo.put("shoptag", res.getString("shoptag"));
                        jo.put("isneeduserpic", res.getInt("isneeduserpic"));
                        jo.put("isneeduserinfo", res.getInt("isneeduserinfo"));
                        jo.put("isneeduseraddress", res.getInt("isneeduseraddress"));
                        jo.put("isneeddesktag", res.getInt("isneeddesktag"));
                        jo.put("isatmain", res.getInt("isatmain"));
                        jo.put("orderindex", res.getInt("orderindex"));
                        ja.put(jo);
                    }
                    resJo.put("ret", "success");
                    resJo.put("nodes", ja);
                    break;
                case "del":
                    stmt = conn.prepareStatement("delete from t_product where ID = ?");
                    stmt.setInt(1, Integer.parseInt(request.getParameter("id")));
                    if (stmt.executeUpdate() == 1){
                        resJo.put("ret", "success");
                    }else {
                        resJo.put("ret", "fail");
                    }
                    break;
                case "change":
                    stmt = conn.prepareStatement("update t_product set producttypeid = ?, productid = ?, productname = ?, videoid = ?, videolink = ?, tagindex = ?, zone = ?, shoptag = ?, ispassed = ?, istop = ?, orderindex = ?, productdes = ?, num = ?, price = ?, ishowvideolink = ?, isneeduserpic = ?, isneeduserinfo = ?, isneeduseraddress = ?, isneeddesktag = ?, isatmain = ? where ID = ?");
                    stmt.setInt(1, utils.getIntParam(request, "producttypeid"));
                    stmt.setInt(2, utils.getIntParam(request, "productid"));
                    stmt.setString(3, request.getParameter("productname"));
                    stmt.setString(4, request.getParameter("videoid"));
                    stmt.setString(5, request.getParameter("videolink"));
                    stmt.setString(6, request.getParameter("tagindex"));
                    stmt.setString(7, request.getParameter("zone"));
                    stmt.setString(8, request.getParameter("shoptag"));
                    stmt.setInt(9, utils.getIntParam(request, "ispassed"));
                    stmt.setInt(10, utils.getIntParam(request, "istop"));
                    stmt.setInt(11, utils.getIntParam(request, "orderindex"));
                    stmt.setString(12, request.getParameter("productdes"));
                    stmt.setInt(13, utils.getIntParam(request, "num"));
                    stmt.setFloat(14, utils.getFloatParam(request, "price"));
                    stmt.setInt(15, utils.getIntParam(request, "ishowvideolink"));
                    stmt.setInt(16, utils.getIntParam(request, "isneeduserpic"));
                    stmt.setInt(17, utils.getIntParam(request, "isneeduserinfo"));
                    stmt.setInt(18, utils.getIntParam(request, "isneeduseraddress"));
                    stmt.setInt(19, utils.getIntParam(request, "isneeddesktag"));
                    stmt.setInt(20, utils.getIntParam(request, "isatmain"));
                    stmt.setInt(21, Integer.parseInt(request.getParameter("ID")));
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
            getServletContext().log("productMgr err: " + utils.getExceptionInfo(e));
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
