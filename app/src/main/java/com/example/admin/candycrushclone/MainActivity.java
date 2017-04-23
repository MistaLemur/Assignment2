package com.example.admin.candycrushclone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Application entry point
        super.onCreate(savedInstanceState);
        setContentView(new BoardView(this)); //Create a new boardview
    }
}
