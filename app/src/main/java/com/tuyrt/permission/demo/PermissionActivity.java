package com.tuyrt.permission.demo;

import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.tuyrt.permission.java.AdapterPermissionListener;
import com.tuyrt.permission.java.Permission;
import com.tuyrt.permission.java.PermissionImpl;

import java.util.List;

public class PermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, new PermissionFragment())
                .commit();

        findViewById(R.id.tv_checkPermission).setOnClickListener(v -> applyPermission());

        PermissionImpl.init(this).permission(Permission.CAMERA)
                .requestPermission(new AdapterPermissionListener() {
                    @Override
                    public void onGranted() {

                    }
                });
    }

    String[] per = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    //申请权限
    private void applyPermission() {
        PermissionImpl.init(this)
                .permission(per)
                .isRejectNoCancelDialog(true)
                .requestPermission(new AdapterPermissionListener() {
                    //同意所有权限
                    @Override
                    public void onGranted() {
                        Toast.makeText(PermissionActivity.this, "所有权限都已授权", Toast.LENGTH_SHORT).show();
                    }

                    //拒绝权限
                    @Override
                    public void onDenied(List<String> deniedPermission) {
                        super.onDenied(deniedPermission);
                    }
                    //拒绝特殊权限（应用上层弹窗or安装应用）
                    @Override
                    public void onSpecialDenied(List<String> deniedPermission) {
                        super.onSpecialDenied(deniedPermission);
                    }
                    //拒绝授权（勾选不在提示）
                    @Override
                    public void onShouldShowRationale(List<String> deniedPermission) {
                        super.onShouldShowRationale(deniedPermission);
                    }
                });
    }

}
