package com.permissionutil;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;

/**
  一些公共通用的方法
 */
public class XXPermission {

    /**
     把权限list转变成字符串说明

     @param list
     @return 如：相机、联系人
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
      获取应用名称
     @return
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

     @param stringList
     @return
     */
    public static String[] listToStringArray(List<String> stringList) {
        return stringList.toArray(new String[stringList.size()]);
    }

    /**

     @param strings
     @return
     */
    public static List<String> stringArrayToList(String[] strings) {
        return Arrays.asList(strings);
    }

    /**
     检查权限是否为空
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
     返回应用程序在清单文件中注册的权限
     */
    private static List<String> getManifestPermissions(Context context) {
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

}
