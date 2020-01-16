package com.permissionutil;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class PermissionImpl {

    private PermissionUtils mPermissionUtils;
    /**
      传入数组权限
     */
    private String[] permissions;
    /**
      是否显示拒绝后弹窗（未勾选不再提示，默认为true）
     */
    private boolean isRejectDialog = true;
    /**
      拒绝弹窗后点击取消建是否继续显示弹窗，要求赋予权限（默认为false）
     */
    private boolean isRejectNoCancelDialog = false;
    /**
     是否显示拒绝后弹窗（已勾选不再提示，默认为true）
     */
    private boolean isRejectWithNeverDialog = true;

    public static PermissionImpl newPermission() {
        return new PermissionImpl();
    }

    public PermissionImpl activity(FragmentActivity activity) {
        mPermissionUtils = new PermissionUtils(activity);
        return this;
    }

    public PermissionImpl fragment(Fragment fragment) {
        mPermissionUtils = new PermissionUtils(fragment);
        return this;
    }

    /**
      申请的权限(必须在清单文件中声明，否则error)
     @param per
     @return
     */
    public PermissionImpl permission(@NonNull String[] per) {
        permissions = per;
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
     正式申请
     @param callback
     */
    public void requestPermission(final PermissionListener callback) {
        checkNull(mPermissionUtils);
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
            public void onShouldShowRationale(List<String> deniedPermission) {
                if (isRejectWithNeverDialog) {
                    mPermissionUtils.showNeverDialog(deniedPermission, isRejectNoCancelDialog, this);
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
}
