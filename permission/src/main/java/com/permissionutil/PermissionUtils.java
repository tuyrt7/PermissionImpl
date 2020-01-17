package com.permissionutil;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/**
  管理权限申请Fragment，对申请权限回调包装一层（管理弹窗、提示）
 */
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
     外部使用 申请权限

     @param permissions 申请授权的权限
     @param listener    授权回调的监听
     */
    public void requestPermissions(List<String> permissions, PermissionListener listener) {
        if (XXPermission.hasEmpty(permissions))
            throw new IllegalArgumentException("permission can't be null");
        //检查是否在清单文件中注册
        XXPermission.checkPermissions(getContext(), permissions);

        mPermissionFragment.get().init(listener, permissions);
        mPermissionFragment.get().requestPermissions();
    }

    /**
     显示拒绝权限（未勾选不再提示）后弹窗

     @param list             权限list
     @param isRejectNoCancel 是否取消后不在弹窗
     @param listener         监听
     */
    public void showCancelDialog(List<String> list, boolean isRejectNoCancel, PermissionListener listener) {
        String str1 = XXPermission.getAppName(getContext());
        String str2 = "需要 ";
        String str3 = XXPermission.permissionToName(list);
        String str4 = " 权限,是否开启权限。";
        SpannableStringBuilder builder = new SpannableStringBuilder(str1 + str2 + str3 + str4);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#FF5153")),
                0, (str1).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#FF5153")),
                (str1 + str2).length(), (str1 + str2 + str3).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setMessage(builder)
                .setPositiveButton("开启权限", (dialog, witch) -> {
                    dialog.dismiss();
                    requestPermissions(list, listener);
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
     显示拒绝权限（勾选不再提示）后弹窗

     @param list             权限list
     @param isRejectNoCancel 是否取消后不在弹窗
     @param listener         监听
     */
    public void showNeverDialog(List<String> list, boolean isRejectNoCancel, boolean isEnterAppSetting, PermissionListener listener) {
        String str1 = XXPermission.getAppName(getContext());
        String str2 = "需要 ";
        String str3 = XXPermission.permissionToName(list);
        String str4 = " 权限,是否进入设置手动打开。";
        SpannableStringBuilder builder = new SpannableStringBuilder(str1 + str2 + str3 + str4);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#FF5153")),
                0, (str1).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#FF5153")),
                (str1 + str2).length(), (str1 + str2 + str3).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setMessage(builder)
                .setPositiveButton("进入设置", (dialog, witch) -> {
                    dialog.dismiss();
                    if (isEnterAppSetting) {
                        PermissionSettingPage.launchAppDetailsSettings(getContext());
                    } else {
                        PermissionSettingPage.start(getContext(), true);
                    }
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

    /**
     特殊权限被拒绝

     @param deniedPermission
     */
    public void showToastHint(List<String> deniedPermission) {
        String msg = String.format("%s权限申请被拒绝", XXPermission.permissionToName(deniedPermission));
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
