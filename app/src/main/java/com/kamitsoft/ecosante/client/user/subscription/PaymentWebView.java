package com.kamitsoft.ecosante.client.user.subscription;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.services.FCMService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class PaymentWebView extends AppCompatActivity {

    private LocalBroadcastManager lbm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Paiement");
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        lbm = LocalBroadcastManager.getInstance(getApplicationContext());
        WebView wview = findViewById(R.id.webview);
        WebSettings webSettings = wview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);// allo popup
        wview.setWebViewClient(new WebViewClient() { // avoid redirections out of app
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url){
                return shouldOverrideUrlLoading(url);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request)
            {
                Uri uri = request.getUrl();
                return shouldOverrideUrlLoading(uri.toString());
            }

            private boolean shouldOverrideUrlLoading(final String url)
            {
                wview.loadUrl(url);
                return false; // Returning True means that application wants to leave the current WebView and handle the url itself, otherwise return false.
            }
        });
        Intent i  = getIntent();

        wview.loadUrl(i.getExtras().getString("url"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter ifilt = new IntentFilter(FCMService.ACTION_PAYMENT);
        ifilt.addCategory(FCMService.CAT_PAYMENT_SUCCESS);
        lbm.registerReceiver(receiver, ifilt);
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent data = new Intent();
            data.addCategory(FCMService.CAT_PAYMENT_SUCCESS);
            PaymentWebView.this.setResult(Activity.RESULT_OK,data);
            finish();
        }
    };




}
