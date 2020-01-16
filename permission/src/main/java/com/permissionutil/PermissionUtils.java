package com.permissionutil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class PermissionUtils {

    private static final String TAG = PermissionUtils.class.getSimpleName();

    private Lazy<PermissionFragment> mPermissionFragment;


    public PermissionUtils(@NonNull final Fragment fragment) {
        mPermissionFragment = getLazySingleton(fragment.getChildFragmentManager());
    }

    public PermissionUtils(@NonNull final FragmentActivity activity) {
        mPermissionFragment = getLazySingleton(activity.getSupportFragmentManager());
    }


    @NonNull
    private Lazy<PermissionFragment> getLazySingleton(@NonNull final FragmentManager fragmentManager) {
        return new Lazy<PermissionFragment>() {

            private PermissionFragment rxPermissionsFragment;

            @Override
            public synchronized PermissionFragment get() {
                if (rxPermissionsFragment == null) {
                    rxPermissionsFragment = getPermissionsFragment(fragmentManager);
                }
                return rxPermissionsFragment;
            }

        };
    }

    private PermissionFragment getPermissionsFragment(@NonNull final FragmentManager fragmentManager) {
        PermissionFragment permissionFragment = findPermissionsFragment(fragmentManager);
        boolean isNewInstance = permissionFragment == null;
        if (isNewInstance) {
            permissionFragment = new PermissionFragment();
            fragmentManager
                    .beginTransaction()
                    .add(permissionFragment, TAG)
                    .commitNow();
        }
        return permissionFragment;
    }


    private PermissionFragment findPermissionsFragment(@NonNull final FragmentManager fragmentManager) {
        return (PermissionFragment) fragmentManager.findFragmentByTag(TAG);
    }


    private Context getContext() {
        Context context = mPermissionFragment.get().getContext();
        if (context == null) {
            throw new RuntimeException("context can not be null!");
        }
        return context;
    }

    @FunctionalInterface
    public interface Lazy<V> {
        V get();
    }

    /**
     把权限list转变成字符串说明

     @param list
     @return
     */
    private String permissionToName(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            if (Permission.permissionMap.containsKey(s)) {
                String name = Permission.permissionMap.get(s);
                String toStr = sb.toString();
                //sb名称包含当前全称说明，不继续下次循环
                if (toStr.contains(name)) {
                    continue;
                }
                //添加间隔 、
                if (!TextUtils.isEmpty(toStr)) {
                    sb.append("、");
                }
                sb.append(name);
            }
        }
        return sb.toString();
    }

    private String getAppName(Context context) {
        String appName = "";
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            appName = (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }

    private String[] listToStringArray(List<String> stringList) {
        return stringList.toArray(new String[stringList.size()]);
    }

    private List<String> stringArrayToList(String[] strings) {
        return Arrays.asList(strings);
    }

    /**
     检查权限是否为空
     */
    private boolean hasEmpty(String... strings) {
        boolean hasEmpty = false;
        if (strings != null && strings.length > 0) {
            for (String s : strings) {
                if (TextUtils.isEmpty(s)) {
                    hasEmpty = true;
                    break;
                }
            }
        } else {
            hasEmpty = true;
        }
        return hasEmpty;
    }


    /**
     返回应用程序在清单文件中注册的权限
     */
    private List<String> getManifestPermissions(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            return Arrays.asList(pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     检测权限有没有在清单文件中注册
     */
    private void checkPermissions(Context context, List<String> requestPermissions) {
        List<String> manifest = getManifestPermissions(context);
        if (manifest != null && manifest.size() != 0) {
            for (String permission : requestPermissions) {
                if (!manifest.contains(permission)) {
                    throw new RuntimeException("you must add this permission:" + permission + " to AndroidManifest");
                }
            }
        }
    }


    /**
     外部使用 申请权限

     @param permissions 申请授权的权限
     @param listener    授权回调的监听
     */
    public void requestPermissions(String[] permissions, PermissionListener listener) {
        if (hasEmpty(permissions))
            throw new IllegalArgumentException("permissionMap can't contain null");
        //检查是否在清单文件中注册
        checkPermissions(mPermissionFragment.get().getContext(), stringArrayToList(permissions));

        mPermissionFragment.get().setListener(listener);
        mPermissionFragment.get().requestPermissions(permissions);
    }

    /**
     显示拒绝权限（未勾选不再提示）后弹窗

     @param list             权限list
     @param isRejectNoCancel 是否取消后不在弹窗
     @param listener         监听
     */
    public void showCancelDialog(List<String> list, boolean isRejectNoCancel, PermissionListener listener) {
        new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setMessage(getAppName(getContext()) + "需要" + permissionToName(list) + "权限，是否开启权限。")
                .setPositiveButton("开启权限", (dialog, witch) -> {
                    dialog.dismiss();
                    requestPermissions(listToStringArray(list), listener);
                })
                .setNegativeButton("取消", (dialog, witch) -> {
                    dialog.dismiss();
                    if (isRejectNoCancel) {
                        listener.onDenied(list);
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    /**
     显示拒绝权限（勾选不在提示）后弹窗

     @param list             权限list
     @param isRejectNoCancel 是否取消后不在弹窗
     @param listener         监听
     */
    public void showNeverDialog(List<String> list, boolean isRejectNoCancel, PermissionListener listener) {
        new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setMessage(getAppName(getContext()) + "需要" + permissionToName(list) + "权限,需要进入设置打开。")
                .setPositiveButton("进入设置", (dialog, witch) -> {
                    dialog.dismiss();

                    PermissionSettingPage.launchAppDetailsSettings(getContext());
                    //PermissionSettingPage.start(getContext(),true);
                })
                .setNegativeButton("取消", (dialog, witch) -> {
                    dialog.dismiss();
                    if (isRejectNoCancel) {
                        listener.onShouldShowRationale(list);
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }




}
