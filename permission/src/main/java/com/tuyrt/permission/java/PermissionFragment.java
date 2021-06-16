package com.tuyrt.permission.java;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionFragment extends Fragment implements Runnable {

    private static final String TAG = "PFragment";

    /**
     全局的 Handler 对象
     */
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    private PermissionListener listener;
    private List<String> permissions;
    /**
     回调对象存放
     */
    private static final SparseArray<PermissionListener> PERMISSION_ARRAY = new SparseArray<>();
    /**
     请求码
     */
    private static int REQUEST_CODE = 225;

    private boolean isInstallApply;//标记特殊权限（安装apk）申请的标记
    private boolean isAlertApply;//标记特殊权限（系统弹窗）申请的标记

    public void init(PermissionListener listener, List<String> permissions) {
        this.listener = listener;
        this.permissions = permissions;

        isInstallApply = false;
        isAlertApply = false;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissions() {
        Log.d(TAG, "requestPermissions: ");
        if (permissions.contains(Permission.REQUEST_INSTALL_PACKAGES) && !isHasInstallPermission(getActivity()) && !isInstallApply) {
            isInstallApply = true;
            // 跳转到允许安装未知来源设置页面
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getActivity().getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
            return;
        }

        if (permissions.contains(Permission.SYSTEM_ALERT_WINDOW) && !isHasOverlaysPermission(getActivity()) && !isAlertApply) {
            isAlertApply = true;
            // 跳转到悬浮窗设置页面
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
            return;
        }

        //需要发起请求的权限集合
        List<String> requestPermissionList = new ArrayList<>();
        //找出所有未授权的权限
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionList.add(permission);
            }
        }

        if (requestPermissionList.isEmpty()) {
            //已经全部授权
            permissionAllGranted();
        } else {
            //申请授权
            requestPermissions(XXPermission.listToStringArray(requestPermissionList), REQUEST_CODE);
        }
    }

    /**
     是否是6.0以上版本
     */
    private boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     是否是8.0以上版本
     */
    private boolean isOverOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    /**
     是否有安装权限
     */
    private boolean isHasInstallPermission(Context context) {
        if (isOverOreo()) {
            return context.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    /**
     是否有悬浮窗权限
     */
    private boolean isHasOverlaysPermission(Context context) {
        if (isOverMarshmallow()) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: " + requestCode);
        if (requestCode == REQUEST_CODE) {
            // 需要延迟执行，不然有些华为机型授权了但是获取不到权限
            HANDLER.postDelayed(this, 500);
        }
    }

    @Override
    public void run() {
        // 如果用户离开太久，会导致 Activity 被回收掉，所以这里要判断当前 Fragment 是否有被添加到 Activity（可在开发者模式中开启不保留活动复现崩溃的 Bug）
        if (isAdded()) {
            // 请求其他危险权限
            requestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult: " + requestCode);
        if (requestCode != REQUEST_CODE) {
            return;
        }

        List<String> deniedSpecialPermissionList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            // 重新检查安装权限
            if (Permission.REQUEST_INSTALL_PACKAGES.equals(permissions[i])) {
                if (isHasInstallPermission(getActivity())) {
                    grantResults[i] = PackageManager.PERMISSION_GRANTED;
                } else {
                    grantResults[i] = PackageManager.PERMISSION_DENIED;
                    deniedSpecialPermissionList.add(permissions[i]);
                }
            }

            // 重新检查悬浮窗权限
            if (Permission.SYSTEM_ALERT_WINDOW.equals(permissions[i])) {
                if (isHasOverlaysPermission(getActivity())) {
                    grantResults[i] = PackageManager.PERMISSION_GRANTED;
                } else {
                    grantResults[i] = PackageManager.PERMISSION_DENIED;
                    deniedSpecialPermissionList.add(permissions[i]);
                }
            }

            // 重新检查8.0的两个新权限
            if (permissions[i].equals(Permission.ANSWER_PHONE_CALLS) || permissions[i].equals(Permission.READ_PHONE_NUMBERS)) {
                // 检查当前的安卓版本是否符合要求
                if (!isOverOreo()) {
                    grantResults[i] = PackageManager.PERMISSION_GRANTED;
                }
            }
        }

        if (grantResults.length > 0) {
            List<String> deniedPermissionList = new ArrayList<>();//用来保存被拒绝的权限
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissionList.add(permissions[i]);
                }
            }

            if (deniedPermissionList.isEmpty()) {
                //已经全部授权
                permissionAllGranted();
            } else {
                //已拒绝去除特殊权限
                if (!deniedSpecialPermissionList.isEmpty()) {
                    deniedPermissionList.removeAll(deniedSpecialPermissionList);
                }

                //存储勾选不再提示的权限
                List<String> neverPermissionList = new ArrayList<>();
                for (String deniedPermission : deniedPermissionList) {
                    //勾选了对话框中”Don’t ask again”的选项, 返回false
                    boolean flag = shouldShowRequestPermissionRationale(deniedPermission);
                    if (!flag) {
                        neverPermissionList.add(deniedPermission);
                    }
                }

                if (!neverPermissionList.isEmpty()) {
                    //勾选不再提示的拒绝授权回调
                    permissionShouldShowRationale(neverPermissionList);
                    return;
                }

                if (!deniedPermissionList.isEmpty()) {
                    //普通拒绝授权
                    permissionHasDenied(deniedPermissionList);
                    return;
                }

                //特殊拒绝授权
                permissionSpecialHasDenied(deniedSpecialPermissionList);
            }
        }
    }


    /**
     权限全部已经授权
     */
    private void permissionAllGranted() {
        if (listener != null) {
            listener.onGranted();
        }
    }

    /**
     权限被拒绝

     @param deniedList 被拒绝的权限List
     */
    private void permissionHasDenied(List<String> deniedList) {
        if (listener != null) {
            listener.onDenied(deniedList);
        }
    }

    /**
     特殊权限被拒绝

     @param deniedList 被拒绝的权限List
     */
    private void permissionSpecialHasDenied(List<String> deniedList) {
        if (listener != null) {
            listener.onSpecialDenied(deniedList);
        }
    }


    /**
     权限被拒绝并且勾选了不在询问

     @param deniedList 勾选了不在询问的权限List
     */
    private void permissionShouldShowRationale(List<String> deniedList) {
        if (listener != null) {
            listener.onShouldShowRationale(deniedList);
        }
    }

}
