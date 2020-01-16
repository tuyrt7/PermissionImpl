package com.permissionutil;

import android.util.Log;
import java.util.List;

/**
  管理回调
 */
public class AdapterPermissionListener implements PermissionListener {
    private static final String TAG = "AdapterPermission";
    @Override
    public void onGranted() {
        Log.d(TAG, "onGranted: ");
    }

    @Override
    public void onDenied(List<String> deniedPermission) {
        Log.d(TAG, "onDenied: ");
    }

    @Override
    public void onSpecialDenied(List<String> deniedPermission) {
        Log.d(TAG, "onSpecialDenied: ");
    }

    @Override
    public void onShouldShowRationale(List<String> deniedPermission) {
        Log.d(TAG, "onShouldShowRationale: ");
    }
}
