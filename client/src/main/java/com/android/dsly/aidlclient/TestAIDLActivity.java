package com.android.dsly.aidlclient;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.dsly.abridge.ABridge;
import com.android.dsly.abridge.AbridgeCallBack;

import androidx.appcompat.app.AppCompatActivity;

public class TestAIDLActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_show_in_message;
    private EditText et_show_out_message;
    private AbridgeCallBack callBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        findViewById(R.id.acquire_info).setOnClickListener(this);
        tv_show_in_message = findViewById(R.id.tv_show_in_message);
        et_show_out_message = findViewById(R.id.et_show_out_message);
        ABridge.registerAIDLCallBack(callBack = new AbridgeCallBack() {
            @Override
            public void receiveMessage(String message) {
                tv_show_in_message.setText(message);
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.acquire_info) {
            String message = et_show_out_message.getText().toString();
            ABridge.sendAIDLMessage(message);
        }
    }

    @Override
    protected void onDestroy() {
        ABridge.unRegisterAIDLCallBack(callBack);
        super.onDestroy();
    }

}
