package com.webServer;

import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.auth.COSSigner;
import com.qcloud.cos.http.HttpMethodName;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@WebServlet("/api/webServer/geneCosApiDel")
public class geneCosApiDel extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        System.out.println(request.getParameter("imgPath"));

        String bucketName = "pet-1252596634";
        COSCredentials cred = new BasicCOSCredentials("AKID5IQkVGYlXHKoN3wXF40AVwTE57WOpNNi", "XyEkT6rmp3SwmNE6fCPOWL5Sn9WOMcr4");
        COSSigner signer = new COSSigner();
        Date expiredTime = new Date(System.currentTimeMillis() + 3600L * 1000L);
        String sign = signer.buildAuthorizationStr(HttpMethodName.DELETE, request.getParameter("key"), cred, expiredTime);
        resJo.put("sign", sign);
        pw.println(resJo);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
