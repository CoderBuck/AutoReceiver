package me.buck.receiver.sample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import me.buck.receiver.annotation.GlobalAction;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @GlobalAction("")
    public void test() {

    }
}
