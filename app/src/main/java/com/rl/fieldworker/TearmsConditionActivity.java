package com.rl.fieldworker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TearmsConditionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tearms_condition);
        WebView webView = findViewById(R.id.web);

        webView.loadUrl("https://rlwork.in/api_gfhjfyRETfkmghTYudgnm/term_condition.html");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
    }
}