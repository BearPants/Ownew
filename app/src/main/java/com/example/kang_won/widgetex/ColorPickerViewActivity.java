package com.example.kang_won.widgetex;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.rarepebble.colorpicker.ColorPickerView;

public class ColorPickerViewActivity extends Activity {
    private ColorPickerView picker;
    private Button setBtn;
    public Context getContext() {
        return this;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_color_picker_view);
        ComponentName cn = getComponentName();

        picker = (ColorPickerView) findViewById(R.id.colorPicker);
        picker.setColor(0xffff0000);
        setBtn = (Button) findViewById(R.id.setButton);
        setBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v1) {
                int colorCode = picker.getColor();
                Intent intent = new Intent(ColorPickerViewActivity.this, WidgetReceiver.class);
                intent.putExtra(WidgetReceiver.STATE, WidgetReceiver.COLOR);
                intent.putExtra(WidgetReceiver.COLOR_KEY, colorCode);
                getContext().sendBroadcast(intent);
                finish();

            }
        });
    }
}