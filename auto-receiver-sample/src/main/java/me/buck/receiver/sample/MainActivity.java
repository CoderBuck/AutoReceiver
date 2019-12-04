package me.buck.receiver.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import me.buck.receiver.annotation.GlobalAction;
import me.buck.receiver.annotation.LocalAction;


public class MainActivity extends AppCompatActivity {

    private static final String ACTION_1 = "action-1";
    private static final String ACTION_2 = "action-2";
    private static final String ACTION_3 = "action-3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @LocalAction(ACTION_1)
    public void test1(Intent intent) {

    }

    @LocalAction(ACTION_2)
    public void test2(Intent intent) {

    }

    @LocalAction(ACTION_3)
    public void test3(Intent intent) {

    }

    //@GlobalAction(ACTION_1)
    //public void test4(Intent intent) {
    //
    //}
    //
    //@GlobalAction(ACTION_2)
    //public void test5(Intent intent) {
    //
    //}
    //
    //@GlobalAction(ACTION_3)
    //public void test6(Intent intent) {
    //
    //}

    class My extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

}
