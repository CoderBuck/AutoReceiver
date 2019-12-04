package me.buck.receiver.sample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.buck.receiver.AutoReceiver;
import me.buck.receiver.annotation.GlobalAction;
import me.buck.receiver.annotation.LocalAction;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String ACTION_1 = "action-1";
    private static final String ACTION_2 = "action-2";
    private static final String ACTION_3 = "action-3";

    @BindView(R.id.btn1) Button mBtn1;
    @BindView(R.id.btn2) Button mBtn2;
    @BindView(R.id.btn3) Button mBtn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        AutoReceiver.bindLocal(this);
        AutoReceiver.bindGlobal(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AutoReceiver.unbindLocal(this);
        AutoReceiver.unbindGlobal(this);
    }

    @LocalAction(ACTION_1)
    public void test1(Intent intent) {
        Log.i(TAG, "test1: " + intent.getAction());
    }

    @LocalAction(ACTION_2)
    public void test2(Intent intent) {
        Log.i(TAG, "test2: " + intent.getAction());
    }

    @LocalAction(ACTION_3)
    public void test3(Intent intent) {
        Log.i(TAG, "test3: " + intent.getAction());
    }

    @OnClick(R.id.btn1)
    public void onMBtn1Clicked() {
        send(ACTION_1);
    }

    @OnClick(R.id.btn2)
    public void onMBtn2Clicked() {
        sendBroadcast(new Intent(ACTION_2));
    }

    @OnClick(R.id.btn3)
    public void onMBtn3Clicked() {
        send(ACTION_3);
        sendBroadcast(new Intent(ACTION_3));
    }

    void send(String action) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(action));
    }

    @GlobalAction(ACTION_1)
    public void test4(Intent intent) {
        Log.i(TAG, "test4: " + intent.getAction());
    }

    @GlobalAction(ACTION_2)
    public void test5(Intent intent) {
        Log.i(TAG, "test5: " + intent.getAction());
    }

    @GlobalAction(ACTION_3)
    public void test6(Intent intent) {
        Log.i(TAG, "test6: " + intent.getAction());
    }

}
