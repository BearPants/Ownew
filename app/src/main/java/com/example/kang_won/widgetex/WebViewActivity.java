package com.example.kang_won.widgetex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by 서강원 on 2015-09-24.
 */

public class WebViewActivity extends Activity implements View.OnClickListener, TextView.OnEditorActionListener {

    private LinearLayout mainLayout;
    private WebView webView;
    private ImageButton setBtn;
    private ImageButton refreshBtn;
    private ImageButton backBtn;
    private ImageButton forwardBtn;
    private EditText urlText;
    private String defaultURL = "http://www.naver.com";
    private String curURL = null;
    private int widgetID;

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        webView = (WebView) findViewById(R.id.webView);
        setBtn = (ImageButton) findViewById(R.id.setBtn);
        refreshBtn = (ImageButton) findViewById(R.id.refreshBtn);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        forwardBtn = (ImageButton) findViewById(R.id.forwardBtn);
        urlText = (EditText) findViewById(R.id.urlText);

        Intent receivedIntent = getIntent();
        widgetID = receivedIntent.getIntExtra("WidgetID", 0);
        Log.d("WidgetID = Webview", widgetID + "");

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                urlText.setText(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                urlText.setText(url);
                checkStateGoAndForward();
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);   // javascript
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);         // 확대
        webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND); // 플러그인

        checkStateGoAndForward();
        goURL(defaultURL);

        setBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        forwardBtn.setOnClickListener(this);
        urlText.setOnEditorActionListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == setBtn) {

            curURL = getCurrentURL();

            //   Toast.makeText(getApplicationContext(), curURL, Toast.LENGTH_LONG).show();

            dbManager = new DBManager(getApplicationContext());

            int recordCount = dbManager.getRecordCountAtBookmark(widgetID);
            Log.d("!!!!!!!!!!!!recordCount", "" + recordCount);

            if (recordCount == 0) {
                Log.d("!!!!!!!!!!insert", curURL);
                dbManager.insertDataAtBookmark(widgetID, curURL);
                Log.d("!!!!!!!!!!insertSUCCESS", "SUCCESS");
                String a = dbManager.selectDataAtBookmark(widgetID);
                Log.d("!!!!!!!!!!insertSUCCESS", a);
            } else {
                Log.d("!!!!!!!!!!update", curURL);
                dbManager.updateDataAtBookmark(widgetID, curURL);
                Log.d("!!!!!!!!!!updateSUCCESS", "SUCCESS");
                String a = dbManager.selectDataAtBookmark(widgetID);
                Log.d("!!!!!!!!!!updateSUCCESS", a);
            }

            sendBroadCastForOnUpdate();
            Intent intent = new Intent(WebViewActivity.this, TakeScreenShotActivity.class);
            intent.putExtra("URL", curURL);
            intent.putExtra("WidgetID", widgetID);
            startActivity(intent);
            finish();

            //getApplicationContext().sendBroadcast(intent);
        }  else if(v == refreshBtn){
            webView.reload();

        } else if(v == backBtn && webView.canGoBack()){
            webView.goBack();

        } else if(v == forwardBtn && webView.canGoForward()){
            webView.goForward();

        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if(v.getId() == R.id.urlText && actionId == EditorInfo.IME_ACTION_DONE){

            String inputURL = urlText.getText().toString();

            if(!checkHTTPorHTTPS(inputURL)) {
                inputURL = "http://" + inputURL;
            }

            goURL(inputURL);
        }

        return false;
    }

    public boolean checkHTTPorHTTPS(String inputURL){

        if(inputURL.startsWith("http://") || inputURL.startsWith("https://")){
            return true;
        }

        return false;
    }

    public void checkStateGoAndForward(){

        if(webView.canGoBack() && webView.canGoForward()){
            backBtn.setEnabled(true);
            forwardBtn.setEnabled(true);

        } else if(webView.canGoBack() && !webView.canGoForward()){
            backBtn.setEnabled(true);
            forwardBtn.setEnabled(false);

        } else if(!webView.canGoBack() && webView.canGoForward()) {
            backBtn.setEnabled(false);
            forwardBtn.setEnabled(true);

        } else {
            backBtn.setEnabled(false);
            forwardBtn.setEnabled(false);
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