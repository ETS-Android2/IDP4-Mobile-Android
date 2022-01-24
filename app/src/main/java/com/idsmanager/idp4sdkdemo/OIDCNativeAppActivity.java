package com.idsmanager.idp4sdkdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.summer.commons.viewannotation.ContentView;
import com.summer.commons.viewannotation.InjectHandler;
import com.summer.commons.viewannotation.ViewInject;

@ContentView(R.layout.activity_oidc_native_app)
public class OIDCNativeAppActivity extends AppCompatActivity {
    private static final String TAG = "OIDCNativeAppActivity";
    @ViewInject(R.id.editText)
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        InjectHandler.inject(this);
    }

    @Override
    protected void onResume() {
        Uri uri = getIntent().getData();

        if (uri != null) {
            editText.setText(uri.toString());
        }
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart() called");
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Uri uri = getIntent().getData();
        Log.d(TAG, "onNewIntent() called with: intent = [" + intent + "],uri = [" + uri + "]");
        super.onNewIntent(intent);
        if (uri != null) {
            editText.setText(uri.toString());
        }
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart() called");
        super.onRestart();

    }
}