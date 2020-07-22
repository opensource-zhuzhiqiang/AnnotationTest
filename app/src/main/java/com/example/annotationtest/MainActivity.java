package com.example.annotationtest;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.annotations.MyInterface;

@MyInterface
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}