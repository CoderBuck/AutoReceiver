package me.buck.receiver.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import me.buck.receiver.annotation.GlobalAction;
import me.buck.receiver.annotation.LocalAction;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @LocalAction("")
    public void test(Intent intent) {

    }

    class My extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

}
