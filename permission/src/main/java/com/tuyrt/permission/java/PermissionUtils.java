package com.tuyrt.permission.java;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/**
 * 管理权限申请Fragment，对申请权限回调包装一层（管理弹窗、提示）
 */
public class PermissionUtils {

    private static final String TAG = PermissionUtils.class.getSimpleName();

    private Lazy<PermissionFragment> mPermissionFragment;

    private int mDialogTextColor;

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
     * 外部使用 申请权限
     *
     * @param permissions 申请授权的权限
     * @param listener    授权回调的监听
     */
    public void requestPermissions(List<String> permissions, PermissionListener listener) {
        if (XXPermission.hasEmpty(permissions)) {
            throw new IllegalArgumentException("permission can't be null");
        }
        //检查是否在清单文件中注册
        XXPermission.checkPermissions(getContext(), permissions);

        mPermissionFragment.get().init(listener, permissions);
        mPermissionFragment.get().requestPermissions();
    }


    public void setDialogTextColor(int dialogTextColor) {
        mDialogTextColor = dialogTextColor;
    }

    /**
     * 显示拒绝权限（未勾选不再提示）后弹窗
     *
     * @param list             权限list
     * @param isRejectNoCancel 是否取消后不在弹窗
     * @param listener         监听
     */
    public void showCancelDialog(List<String> list, boolean isRejectNoCancel, PermissionListener listener) {
        CharSequence msg = getMsg(list);

        new AlertDialog.Builder(getContext())
                .setMessage(msg)
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
     * 显示拒绝权限（勾选不再提示）后弹窗
     *
     * @param list             权限list
     * @param isRejectNoCancel 是否取消后不在弹窗
     * @param listener         监听
     */
    public void showNeverDialog(List<String> list, boolean isRejectNoCancel, boolean isEnterAppSetting, PermissionListener listener) {
        CharSequence msg = getMsg(list);
        new AlertDialog.Builder(getContext())
                .setMessage(msg)
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

    private CharSequence getMsg(List<String> permissionStrList) {
        String appName = XXPermission.getAppName(getContext());
        String permissionStr = XXPermission.permissionToName(permissionStrList);

        String msg = String.format("%s 需要 [%s] 权限,是否进入设置手动打开。", appName, permissionStr);

        SpannableString span = new SpannableString(msg);
        //应用名称字体加粗：设置字体样式（Examples include "NORMAL", "BOLD", "ITALIC", "BOLD_ITALIC"）
        span.setSpan(new StyleSpan(Typeface.BOLD), 0, appName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//        //设置字体大小（第二个参数如果为true，表示前面的字体大小单位为dip，否则为px）
//        span.setSpan(new AbsoluteSizeSpan(20), 14, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        span.setSpan(new AbsoluteSizeSpan(20, true), 16, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//        //设置相对字体大小，相对于默认字体的大小（0.5f表示默认字体的一半，2.0f表示默认字体的两倍)
//        span.setSpan(new RelativeSizeSpan(0.5f), 21, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        span.setSpan(new RelativeSizeSpan(2.0f), 23, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //设置字体颜色
        span.setSpan(new ForegroundColorSpan(mDialogTextColor == -1 ? getColorPrimary() : getDialogTextColor()), appName.length() + 4, appName.length() + 6 + permissionStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        //设置字体背景颜色
//        span.setSpan(new BackgroundColorSpan(Color.YELLOW), 37, 45, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return span;
    }

    private int getDialogTextColor() {
        return getContext().getResources().getColor(mDialogTextColor);
    }

    public int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    /**
     * 特殊权限被拒绝
     *
     * @param deniedPermission
     */
    public void showToastHint(List<String> deniedPermission) {
        String msg = String.format("%s权限申请被拒绝", XXPermission.permissionToName(deniedPermission));
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
