<<<<<<< HEAD
package com.example.test3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.json.JSONArray;

import java.util.List;

public class PostureActivity extends AppCompatActivity {
    private WebView view;

    public class JsInterface {
        private WebView view;

        public JsInterface(WebView view) {
            this.view = view;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @JavascriptInterface
        public String refresh(int n) {
            List<int[]> l = A.getInstance().getN(n);
            return (new JSONArray(l)).toString();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posture2);

        WebView.setWebContentsDebuggingEnabled(true);
        view = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        view.addJavascriptInterface(new JsInterface(view), "Android");
        view.loadUrl("file:///android_asset/main.html");
    }
=======
package com.example.test3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.json.JSONArray;

import java.util.List;

public class PostureActivity extends AppCompatActivity {
    private WebView view;

    public class JsInterface {
        private WebView view;

        public JsInterface(WebView view) {
            this.view = view;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @JavascriptInterface
        public String refresh(int n) {
            List<int[]> l = A.getInstance().getN(n);
            return (new JSONArray(l)).toString();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posture2);

        WebView.setWebContentsDebuggingEnabled(true);
        view = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        view.addJavascriptInterface(new JsInterface(view), "Android");
        view.loadUrl("file:///android_asset/main.html");
    }
>>>>>>> 32196bc (test)
}