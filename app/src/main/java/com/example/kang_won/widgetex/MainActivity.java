package com.example.kang_won.widgetex;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private AlertDialog tempDialog;
    private ListView widgetList;
    private CompoundButton syncSwitch;
    private ArrayList<String> widgetContents;
    private DBManager dbManager;
    private TextView stateText;
    private AlarmManager am;
    private PendingIntent sender;
    private ImageView bgImage;
    private FrameLayout tempView;
    private FrameLayout mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbManager = new DBManager(this);
        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        setViews();
        setText();
        getWidgetContents();
        setListView();
        setSwitch();


    }

    void setSynchronize(long interval) {
        Intent intent = new Intent(MainActivity.this, WidgetReceiver.class);
        intent.putExtra(WidgetReceiver.STATE, WidgetReceiver.SYNCHRONIZATION);
        sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        long firstTime = SystemClock.elapsedRealtime();
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, interval, sender);
    }

    void setViews() {
        stateText = (TextView) findViewById(R.id.stateText);
        widgetList = (ListView) findViewById(R.id.totalWidgetlistView);
        syncSwitch = (CompoundButton) findViewById(R.id.switch_main_1);
        tempView = (FrameLayout) findViewById(R.id.tempView);
        mainView = (FrameLayout) findViewById(R.id.mainView);
        bgImage = (ImageView) findViewById(R.id.bgImage);

        mainView.setVisibility(View.INVISIBLE);
        tempView.setVisibility(View.VISIBLE);
        bgImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempView.setVisibility(View.GONE);
                mainView.setVisibility(View.VISIBLE);
            }
        });

    }

    void getWidgetContents() {
        widgetContents = new ArrayList<String>();

        int widgetIds[] = dbManager.selectAllWidgetIdAtWidgetState();
        for (int i = 0; i < widgetIds.length; i++) {
            if (dbManager.getRecordCountAtWidgetState(widgetIds[i]) == 0) {
                widgetContents.add("아직 설정되지 않았습니다.");
            } else {
                int tempState = dbManager.selectDataAtWidgetState(widgetIds[i]);
                if (tempState == 1) {
                    widgetContents.add("위젯 상태 - 색상");
                }

                if (tempState == 0) {
                    widgetContents.add("위젯 상태 - 이미지");
                }

                if (tempState == 2) {
                    String url = dbManager.selectDataAtBookmark(widgetIds[i]);
                    int from = 0, to = 0;
                    boolean isFirst = true, isSecond = true;

                    for (int k = 0; k < url.length(); k++) {
                        if (url.charAt(k) == '/') {
                            if (isFirst) {
                                isFirst = false;
                                continue;

                            } else if (isSecond) {
                                isSecond = false;
                                from = k + 1;
                                continue;
                            } else {
                                to = k;
                                break;
                            }
                        }

                    }
                    url = url.substring(from, to);
                    widgetContents.add("위젯 상태 - " + url);
                }
                if (tempState == 3) {
                    String temp = dbManager.selectDataAtRSS(widgetIds[i], "RSS_name");

                    widgetContents.add("위젯 상태 - " + temp);

                }
                if (tempState == 4) {
                    String temp = dbManager.selectDataAtRSS(widgetIds[i], "RSS_name");

                    widgetContents.add("위젯 상태 - " + temp);

                }
            }
        }
    }

    void setText() {
        int numOfWidget = dbManager.selectAllWidgetIdAtWidgetState().length;
        boolean isSetted;
        String title;
        if (dbManager.selectDataAtMainConfig("sync") == 1) {
            isSetted = true;
        } else {
            isSetted = false;
        }
        title = String.valueOf(numOfWidget) + "개의 위젯 동작중";
        if (isSetted) {
            long interval = dbManager.selectDataAtMainConfig("timeout");
            interval = interval / 60000;
            title = title + " - 동기화 (" + String.valueOf(interval) + "분)";
        }
        stateText.setText(title);
    }

    void setListView() {
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, widgetContents);
        widgetList.setAdapter(adapter);
        widgetList.setDivider(new ColorDrawable(Color.BLACK));
        widgetList.setDividerHeight(2);
    }

    void setSwitch() {
        if (dbManager.selectDataAtMainConfig("sync") == 1) {
            syncSwitch.setChecked(true);

        } else {
            syncSwitch.setChecked(false);

        }

        syncSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chooseSetting();

                } else {
                    Intent intent = new Intent(MainActivity.this, WidgetReceiver.class);
                    intent.putExtra(WidgetReceiver.STATE, WidgetReceiver.SYNCHRONIZATION);
                    sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                    am.cancel(sender);
                    dbManager.updateSyncAtMainConfig(0);
                    dbManager.updateTimeoutAtMainConfig(0);
                    setText();
                }
            }
        });
    }

    public void chooseSetting() {
        final CharSequence[] items = {"30분", "1시간", "1시간 30분", "2시간", "테스트 - 1분"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("동기화 시간 설정");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("30분")) {
                    long interval = 1800000;
                    setSynchronize(interval);
                    dbManager.updateSyncAtMainConfig(1);
                    dbManager.updateTimeoutAtMainConfig((int) interval);
                    setText();
                } else if (items[item].equals("1시간")) {
                    long interval = 3600000;
                    setSynchronize(interval);
                    dbManager.updateSyncAtMainConfig(1);
                    int ddd = dbManager.selectDataAtMainConfig("sync");
                    dbManager.updateTimeoutAtMainConfig((int) interval);
                    setText();
                } else if (items[item].equals("1시간 30분")) {
                    long interval = 5400000;
                    setSynchronize(interval);
                    dbManager.updateSyncAtMainConfig(1);
                    dbManager.updateTimeoutAtMainConfig((int) interval);
                    setText();
                } else if (items[item].equals("2시간")) {
                    long interval = 7200000;
                    setSynchronize(interval);
                    dbManager.updateSyncAtMainConfig(1);
                    dbManager.updateTimeoutAtMainConfig((int) interval);
                    setText();
                } else if (items[item].equals("테스트 - 1분")) {
                    long interval = 60000;
                    setSynchronize(interval);
                    dbManager.updateSyncAtMainConfig(1);
                    dbManager.updateTimeoutAtMainConfig((int) interval);
                    setText();
                }
            }
        });
        builder.show();
    }

}
