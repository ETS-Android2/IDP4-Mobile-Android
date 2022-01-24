package com.summer.commons.authoritymanagement;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * @author summer
 * @DATE 2019/8/1
 * @Describe 权限申请情况
 */
public interface PermissionCallback {
    /**
     * 已经申请到的权限
     *
     * @param requestCode 标示申请权限由哪次操作发起
     * @param permissions 申请到的权限集合
     */
    void onPermissionsGranted(int requestCode, @NonNull List<String> permissions);

    /**
     * 用户拒绝的权限
     *
     * @param requestCode 标示申请权限由哪次操作发起
     * @param permissions 拒绝的权限集合
     */
    void onPermissionsDenied(int requestCode, @NonNull List<String> permissions);
}
