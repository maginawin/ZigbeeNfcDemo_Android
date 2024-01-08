package com.sunricher.zigbeenfcdemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button letsGoBtn = findViewById(R.id.lets_go_btn);
        letsGoBtn.setOnClickListener(v -> {
            handleLetsGo();
        });

        setupVersionText();
    }

    private void handleLetsGo() {
        if (!isSupportNfc()) {
            showMessageDialog(R.string.note, R.string.nfc_not_support_msg);
            return;
        }
        if (!isNfcEnabled()) {
            showMessageDialog(R.string.note, R.string.nfc_disabled_msg);
            return;
        }
        startActivity(new Intent(MainActivity.this, NfcActivity.class));
    }

    private void showMessageDialog(int title, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void setupVersionText() {
        TextView versionTextView = findViewById(R.id.version_tv);
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            // 获取版本号
            int versionCode = packageInfo.versionCode;
            // 获取版本名
            String versionName = packageInfo.versionName + "_" + versionCode;
            versionTextView.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isSupportNfc() {
        PackageManager packageManager = getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_NFC);
    }

    private boolean isNfcEnabled() {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        return nfcAdapter != null && nfcAdapter.isEnabled();
    }
}