package com.tuyrt.permission.java;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 一些公共通用的方法
 */
class XXPermission {

    /**
     * 是否是 6.0 以上版本
     */
    static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    /**
     * 是否是 8.0 以上版本
     */
    static boolean isOverOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    /**
     * 把权限list转变成字符串说明
     *
     * @param list
     * @return 如：相机、联系人
     */
    public static String permissionToName(List<String> list) {
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

    /**
     * 获取应用名称
     *
     * @return
     */
    public static String getAppName(Context context) {
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

    /**
     * @param stringList
     */
    public static String[] listToStringArray(List<String> stringList) {
        return stringList.toArray(new String[stringList.size()]);
    }

    /**
     * @param strings
     */
    public static List<String> stringArrayToList(String[] strings) {
        return Arrays.asList(strings);
    }

    /**
     * 检查权限是否为空
     */
    public static boolean hasEmpty(List<String> strings) {
        boolean hasEmpty = false;
        if (strings != null && strings.size() > 0) {
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
     * 返回应用程序在清单文件中注册的权限
     */
    public static List<String> getManifestPermissions(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            return Arrays.asList(pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 是否有安装权限
     */
    static boolean isHasInstallPermission(Context context) {
        if (isOverOreo()) {
            return context.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    /**
     * 是否有悬浮窗权限
     */
    static boolean isHasOverlaysPermission(Context context) {
        if (isOverMarshmallow()) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    /**
     * 检测权限有没有在清单文件中注册
     */
    public static void checkPermissions(Context context, List<String> requestPermissions) {
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
     * 获取没有授予的权限
     *
     * @param context               上下文对象
     * @param permissions           需要请求的权限组
     */
    static ArrayList<String> getFailPermissions(Context context, List<String> permissions) {

        // 如果是安卓6.0以下版本就返回null
        if (!isOverMarshmallow()) {
            return null;
        }

        ArrayList<String> failPermissions = null;

        for (String permission : permissions) {

            // 检测安装权限
            if (Permission.REQUEST_INSTALL_PACKAGES.equals(permission)) {

                if (!isHasInstallPermission(context)) {
                    if (failPermissions == null) {
                        failPermissions = new ArrayList<>();
                    }
                    failPermissions.add(permission);
                }
                continue;
            }

            // 检测悬浮窗权限
            if (Permission.SYSTEM_ALERT_WINDOW.equals(permission)) {

                if (!isHasOverlaysPermission(context)) {
                    if (failPermissions == null) {
                        failPermissions = new ArrayList<>();
                    }
                    failPermissions.add(permission);
                }
                continue;
            }

            // 检测8.0的两个新权限
            if (Permission.ANSWER_PHONE_CALLS.equals(permission) || Permission.READ_PHONE_NUMBERS.equals(permission)) {

                // 检查当前的安卓版本是否符合要求
                if (!isOverOreo()) {
                    continue;
                }
            }

            // 把没有授予过的权限加入到集合中
            if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                if (failPermissions == null) {
                    failPermissions = new ArrayList<>();
                }
                failPermissions.add(permission);
            }
        }

        return failPermissions;
    }
}
