package com.example.kang_won.widgetex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class XMLparsing {

    public static String getHTML(String targetUrl) {
        URL url;
        HttpURLConnection conn;
        BufferedReader br;
        BufferedReader newBr;
        String line;
        String htmlCode = "";


        try {
            url = new URL(targetUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            String headerType;

            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            headerType = br.readLine();
            br.close();
            conn.disconnect();
            url = new URL(targetUrl);
            conn = (HttpURLConnection) url.openConnection();
            if (headerType.toUpperCase().indexOf("EUC-KR") != -1) {
                br = new BufferedReader((new InputStreamReader(conn.getInputStream(), "EUC-KR")));
            } else /*if (headerType.toUpperCase().indexOf("UTF-8") != -1)*/ {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            }

            boolean flag = true;
            while ((line = br.readLine()) != null) {
                if (flag) {
                    flag = false;
                }
                htmlCode += line + "\n";
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return htmlCode;
    }


}
