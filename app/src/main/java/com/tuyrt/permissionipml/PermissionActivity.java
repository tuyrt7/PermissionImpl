package com.tuyrt.permissionipml;

import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;

import com.permissionutil.AdapterPermissionListener;
import com.permissionutil.PermissionImpl;

import androidx.appcompat.app.AppCompatActivity;

public class PermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, new PermissionFragment())
                .commit();

        findViewById(R.id.tv_checkPermission).setOnClickListener(v -> applyPermission());
    }

    String[] per = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    //申请权限
    private void applyPermission() {
        PermissionImpl.newPermission()
                .activity(this)
                .permission(per)
                .isRejectNoCancelDialog(true)
                .requestPermission(new AdapterPermissionListener() {
                    @Override
                    public void onGranted() {
                        Toast.makeText(PermissionActivity.this, "所有权限都已授权", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
