package com.example.kang_won.widgetex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by 서강원 on 2015-09-24.
 */

public class WebViewActivity extends Activity {

    private WebView webView;
    private Button setBtn;
    private String defaultURL = null;
    private String curURL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = (WebView)findViewById(R.id.webView);
        setBtn = (Button)findViewById(R.id.setBtn);

        defaultURL = "http://www.naver.com";

        goURL(defaultURL);

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curURL = getCurrentURL();

                Toast.makeText(getApplicationContext(), curURL, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(WebViewActivity.this, WidgetReceiver.class);
                intent.putExtra(WidgetReceiver.STATE, WidgetReceiver.GETWEBVIEWURL);
                intent.putExtra(WidgetReceiver.CURURL_KEY, curURL);
                getApplicationContext().sendBroadcast(intent);
                finish();
            }
        });
    }

    public void goURL(String defaultURL){
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(defaultURL);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()){
            webView.goBack();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public String getCurrentURL(){

        return webView.getUrl();
    }

}
