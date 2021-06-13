package com.example.test3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class PostureActivity extends AppCompatActivity {
    private WebView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posture2);

        view = (WebView)findViewById(R.id.webview);
        view.loadUrl("file:///android_asset/main.html");
    }
}