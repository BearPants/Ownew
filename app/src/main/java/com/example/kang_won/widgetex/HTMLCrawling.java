package com.example.kang_won.widgetex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 서강원 on 2015-09-24.
 */
public class HTMLCrawling {

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

            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

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
