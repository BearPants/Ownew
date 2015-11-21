package com.example.kang_won.widgetex;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private EditText intervalEditText;
    private Button setAlarmBtn;
    private ListView widgetList;
    private CheckBox syncCheckBox;
    private ArrayList<String> widgetContents;
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViews();
        getWidgetContents();
        setListView();

        dbManager.selectDataAtMainConfig("sync");
        dbManager.selectDataAtMainConfig("timeout");


        Intent intent = new Intent(MainActivity.this, WidgetReceiver.class);
        intent.putExtra(WidgetReceiver.STATE, WidgetReceiver.SYNCHRONIZATION);
        PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        long firstTime = SystemClock.elapsedRealtime();
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        long interval = 1000 * 60 * 60 * 24;
       // am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 10 * 1000, sender);

    }

    void setViews() {
        intervalEditText = (EditText) findViewById(R.id.intervalEditText);
        setAlarmBtn = (Button) findViewById(R.id.setAlarmButton);
        widgetList = (ListView) findViewById(R.id.totalWidgetlistView);
        syncCheckBox = (CheckBox) findViewById(R.id.checkBox);
    }

    void getWidgetContents() {
        widgetContents = new ArrayList<String>();
        dbManager = new DBManager(this);
        int widgetIds[] = dbManager.selectAllWidgetIdAtWidgetState();
        for (int i = 0; i < widgetIds.length; i++) {
            if (dbManager.getRecordCountAtWidgetState(widgetIds[i]) == 0) {
                widgetContents.add("아직 설정되지 않았습니다.");
            } else {
                int tempState = dbManager.selectDataAtWidgetState(widgetIds[i]);
                if (tempState == 1) {
                    widgetContents.add("배경화면으로 색상이 설정되어 있습니다");
                }

                if (tempState == 0) {
                    widgetContents.add("배경화면으로 이미지 설정되어 있습니다");
                }

                if (tempState == 2) {
                    String url = dbManager.selectDataAtBookmark(widgetIds[i]);
                    widgetContents.add(url + "이 설정되어있습니다");
                }
                if (tempState == 3) {
                    widgetContents.add("뉴스가 설정되어있습니다");
                }
                if (tempState == 4) {
                    widgetContents.add("유투브설정씨빨");
                }
            }
        }
    }

    void setListView() {
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, widgetContents);
        widgetList.setAdapter(adapter);
        widgetList.setDivider(new ColorDrawable(Color.WHITE));
        widgetList.setDividerHeight(2);
    }


}
