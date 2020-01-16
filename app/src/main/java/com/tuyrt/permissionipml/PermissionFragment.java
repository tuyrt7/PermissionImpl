package com.tuyrt.permissionipml;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.permissionutil.AdapterPermissionListener;
import com.permissionutil.PermissionImpl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PermissionFragment extends Fragment {

    private View root;
    private View mCheck;

    String[] per = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = View.inflate(getContext(), R.layout.fragment_permission, null);
        mCheck = root.findViewById(R.id.tv_check);
        mCheck.setOnClickListener(this::check);
        return root;
    }

    private void check(View view) {
        PermissionImpl.newPermission()
                .fragment(this)
                .permission(per)
                .requestPermission(new AdapterPermissionListener(){
                    @Override
                    public void onGranted() {
                        Log.d("Fragment", "获取所有的权限");
                        Toast.makeText(getContext(), "Fragment 获取所有的权限", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
