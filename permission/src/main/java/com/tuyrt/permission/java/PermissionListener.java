package com.tuyrt.permission.java;

import java.util.List;

public interface PermissionListener {
    /**
      申请的权限都已授权
     */
    void onGranted();

    /**
      拒绝授权
     @param deniedPermission 拒绝的权限
     */
    void onDenied(List<String> deniedPermission);

    /**
     拒绝特殊授权 （系统弹窗、安装未知来源apk）
     @param deniedPermission 拒绝的权限
     */
    void onSpecialDenied(List<String> deniedPermission);

    /**
     拒绝授权（勾选不在提示）
     @param deniedPermission 拒绝的权限
     */
    void onShouldShowRationale(List<String> deniedPermission);
}
