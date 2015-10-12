package com.example.kang_won.widgetex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by 서강원 on 2015-09-24.
 */

public class WebViewActivity extends Activity implements View.OnClickListener {

    private WebView webView;
    private Button setBtn;
    private Button goBtn;
    private EditText urlText;
    private String defaultURL = null;
    private String curURL = null;

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = (WebView) findViewById(R.id.webView);
        setBtn = (Button) findViewById(R.id.setBtn);
        goBtn = (Button) findViewById(R.id.goBtn);
        urlText = (EditText) findViewById(R.id.urlText);

        defaultURL = "http://www.naver.com";

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                urlText.setText(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        goURL(defaultURL);

        setBtn.setOnClickListener(this);
        goBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == setBtn) {

            curURL = getCurrentURL();

            //   Toast.makeText(getApplicationContext(), curURL, Toast.LENGTH_LONG).show();


            dbManager = new DBManager(getApplicationContext());

            int recordCount = dbManager.getRecordCount(1);
            Log.d("!!!!!!!!!!!!recordCount", "" + recordCount);

            if (recordCount == 0) {
                Log.d("!!!!!!!!!!insert", curURL);
                dbManager.insertData(1, curURL);
                Log.d("!!!!!!!!!!insertSUCCESS", "SUCCESS");
                String a = dbManager.selectData(1);
                Log.d("!!!!!!!!!!insertSUCCESS", a);
            } else {
                Log.d("!!!!!!!!!!update", curURL);
                dbManager.updateData(1, curURL);
                Log.d("!!!!!!!!!!updateSUCCESS", "SUCCESS");
                String a = dbManager.selectData(1);
                Log.d("!!!!!!!!!!updateSUCCESS", a);
            }

            sendBroadCastForOnUpdate();
            Intent intent = new Intent(WebViewActivity.this, TakeScreenShotActivity.class);
            intent.putExtra("URL", curURL);
            startActivity(intent);
            finish();

            //getApplicationContext().sendBroadcast(intent);
        } else if (v == goBtn) {
            goURL(urlText.getText().toString());
        }
    }

    public void goURL(String defaultURL) {
        webView.loadUrl(defaultURL);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public String getCurrentURL() {

        return webView.getUrl();
    }

    public void sendBroadCastForOnUpdate() {
        Intent alarmIntent = new Intent();
        alarmIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");

        getApplicationContext().sendBroadcast(alarmIntent);
    }

}
