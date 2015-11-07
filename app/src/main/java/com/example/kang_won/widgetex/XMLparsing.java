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
        String line;
        String htmlCode = "";

        try {
            url = new URL(targetUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            String headerType = conn.getContentType();

            if (headerType.toUpperCase().indexOf("EUC_KR") != -1) {
                br = new BufferedReader((new InputStreamReader(conn.getInputStream(), "EUC-KR")));
            } else if (headerType.toUpperCase().indexOf("UTF-8") != -1) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            while ((line = br.readLine()) != null) {
                htmlCode += line + "\n";
            }

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return htmlCode;
    }


}
