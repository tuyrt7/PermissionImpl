package com.permissionutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 模块外层调用层
 */
public class PermissionImpl {

    private static PermissionUtils mPermissionUtils;
    /**
     存储传入数组权限
     */
    private List<String> permissions;
    /**
     是否显示拒绝后弹窗（未勾选不再提示，默认为true）
     */
    private boolean isRejectDialog = true;

    /**
     拒绝弹窗后点击取消键是否继续显示弹窗，要求赋予权限（默认为false）
     */
    private boolean isRejectNoCancelDialog = false;
    /**
     是否显示拒绝后弹窗（已勾选不再提示，默认为true）
     */
    private boolean isRejectWithNeverDialog = true;
    /**
     是否进入设置页（true 应用设置 /false 系统权限设置，系统权限设置页兼容过各系统厂商的SDK）
     */
    private boolean isEnterAppSetting = true;

    private static PermissionImpl newPermission() {
        return  new PermissionImpl();
    }

    public static PermissionImpl init(FragmentActivity activity) {
        mPermissionUtils = new PermissionUtils(activity);
        return newPermission();
    }

    public static PermissionImpl init(Fragment fragment) {
        mPermissionUtils = new PermissionUtils(fragment);
        return newPermission();
    }

    /**
     申请的权限(必须在清单文件中声明，否则error)

     @param per
     @return
     */
    public PermissionImpl permission(@NonNull String... per) {
        checkNullPermission();
        permissions.addAll(Arrays.asList(per));
        return this;
    }

    /**
     拒绝权限，是否显示弹窗

     @param val
     @return
     */
    public PermissionImpl isRejectDialog(boolean val) {
        isRejectDialog = val;
        return this;
    }

    /**
     弹窗取消键是否再次弹窗提醒必须取得权限

     @param val 默认false,如果是必须要取得的权限设置为true
     @return
     */
    public PermissionImpl isRejectNoCancelDialog(boolean val) {
        isRejectNoCancelDialog = val;
        return this;
    }

    /**
     拒绝权限（勾选不再提示），是否显示弹窗

     @param val
     @return
     */
    public PermissionImpl isRejectWithNeverDialog(boolean val) {
        isRejectWithNeverDialog = val;
        return this;
    }

    /**
     拒绝权限（勾选不再提示），是否显示弹窗

     @param val
     @return
     */
    public PermissionImpl isEnterAppSetting(boolean val) {
        isEnterAppSetting = val;
        return this;
    }

    /**
     正式申请

     @param callback
     */
    public void requestPermission(final PermissionListener callback) {
        checkNull(mPermissionUtils);
        checkNullPermission();

        mPermissionUtils.requestPermissions(permissions, new PermissionListener() {
            @Override
            public void onGranted() {
                callback.onGranted();
            }

            @Override
            public void onDenied(List<String> deniedPermission) {
                if (isRejectDialog) {
                    mPermissionUtils.showCancelDialog(deniedPermission, isRejectNoCancelDialog, this);
                }
                callback.onDenied(deniedPermission);
            }

            @Override
            public void onSpecialDenied(List<String> deniedPermission) {
                mPermissionUtils.showToastHint(deniedPermission);
                callback.onSpecialDenied(deniedPermission);
            }

            @Override
            public void onShouldShowRationale(List<String> deniedPermission) {
                if (isRejectWithNeverDialog) {
                    mPermissionUtils.showNeverDialog(deniedPermission, isRejectNoCancelDialog, isEnterAppSetting, this);
                }
                callback.onShouldShowRationale(deniedPermission);
            }
        });
    }

    private void checkNull(Object o) {
        if (o == null) {
            if (o instanceof PermissionUtils) {
                throw new IllegalArgumentException("must do activity() or fragment()");
            }
        }
    }

    private void checkNullPermission() {
        if (permissions == null) {
            permissions = new ArrayList<>();
        }
    }
}
