package com.summer.commons.authoritymanagement;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author summer
 * @DATE 2019/8/1
 * @Describe
 */
public class PermissionsManager {
    private static final String TAG = "PermissionsManager";

    public PermissionsManager() {
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, PermissionCallback callback) {
        List<String> granted = new ArrayList<>();
        List<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }

        if (null == callback) {
            throw new IllegalArgumentException("onRequestPermissionsResult for null callback");
        }

        if (!granted.isEmpty()) {
            callback.onPermissionsGranted(requestCode, granted);
        }

        // Report denied permissions, if any.
        if (!denied.isEmpty()) {
            callback.onPermissionsDenied(requestCode, denied);
        }

        // If 100% successful, call annotated methods
        if (!granted.isEmpty() && denied.isEmpty()) {
            runAnnotatedMethods(callback, requestCode);
        }
    }

    public static boolean hasPermissions(Context context, String[] perms) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (context == null) {
            throw new IllegalArgumentException("Can't check permissions for null context");
        }

        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void requestPermissions(Activity activity, int requestCode, String[] perms) {
        boolean hasPermissions = hasPermissions(activity, perms);

        if (!hasPermissions) {
            ActivityCompat.requestPermissions(activity, perms, requestCode);
        }

    }

    private static void runAnnotatedMethods(@NonNull PermissionCallback object, int requestCode) {
        Class clazz = object.getClass();
        if (isUsingAndroidAnnotations(object)) {
            clazz = clazz.getSuperclass();
        }

        while (clazz != null) {
            for (Method method : clazz.getDeclaredMethods()) {
                PermissionGranted ann = method.getAnnotation(PermissionGranted.class);
                if (ann != null) {
                    if (ann.value() == requestCode) {
                        if (method.getParameterTypes().length > 0) {
                            throw new RuntimeException("注解方法暂不支持传参数");
                        }

                        try {
                            if (!method.isAccessible()) {
                                method.setAccessible(true);
                            }
                            method.invoke(object);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            clazz = clazz.getSuperclass();
        }
    }


    private static boolean isUsingAndroidAnnotations(@NonNull PermissionCallback object) {
        if (!object.getClass().getSimpleName().endsWith("_")) {
            return false;
        }
        try {
            Class clazz = Class.forName("org.androidannotations.api.view.HasViews");
            return clazz.isInstance(object);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
