package com.tuyrt.permission.demo;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tuyrt.permission.demo.R;
import com.tuyrt.permission.java.AdapterPermissionListener;
import com.tuyrt.permission.java.Permission;
import com.tuyrt.permission.java.PermissionImpl;
import com.tuyrt.permission.java.PermissionSettingPage;

public class PermissionFragment extends Fragment {

    private View root;
    private View mCheck;
    private View mSetting;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = View.inflate(getContext(), R.layout.fragment_permission, null);
        mCheck = root.findViewById(R.id.tv_check);
        mSetting = root.findViewById(R.id.tv_setting);
        mCheck.setOnClickListener(this::check);
        mSetting.setOnClickListener(this::setting);
        return root;
    }

    private void setting(View view) {
        PermissionSettingPage.launchAppDetailsSettings(getContext());
    }

    String[] per = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE
    };

    private void check(View view) {
        PermissionImpl.init(this)
                .permission(Permission.SYSTEM_ALERT_WINDOW,Permission.REQUEST_INSTALL_PACKAGES)
                .permission(per)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)//add 权限
                .dialogTextColor(R.color.colorAccent)
                .isRejectDialog(true)//显示拒绝弹窗
                .isRejectNoCancelDialog(false)//取消后继续弹窗
                .isRejectWithNeverDialog(true)////显示拒绝弹窗
                .isEnterAppSetting(true)//进入应用设置页（false进入系统权限设置，适配各大厂商sdk--未测试，测试过自己华为mate，发现设置true比较方便）
                .requestPermission(new AdapterPermissionListener(){
                    @Override
                    public void onGranted() {
                        Log.d("Fragment", "获取所有的权限");
                        Toast.makeText(getContext(), "Fragment 获取所有的权限", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
