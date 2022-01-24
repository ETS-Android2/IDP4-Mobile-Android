package com.summer.commons.viewannotation;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author summer
 * @DATE 2019/7/31
 * @Describe
 */
public class InjectHandler {
    private static final String TAG = "InjectHandler";

    private static final String CONTENT_VIEW_METHOD = "setContentView";
    private static final String FIND_VIEW_METHOD = "findViewById";

    public static void inject(Activity activity) {
        injectContentView(activity);
        injectViews(activity);
        injectListener(activity);
    }

    //注入ContentView
    private static void injectContentView(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        //获取Class上的ContentView注解
        ContentView contentView = clazz.getAnnotation(ContentView.class);
        if (contentView != null) {
            try {
                Method method = clazz.getMethod(CONTENT_VIEW_METHOD, int.class);
                //反射调用Activity的setContentView方法
                method.invoke(activity, contentView.value());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    //注入View
    private static void injectViews(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        //获取Activity中所有的字段
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //获取字段上带有ViewInject标记的字段
            ViewInject viewInject = field.getAnnotation(ViewInject.class);
            if (viewInject != null) {
                try {
                    Method method = clazz.getMethod(FIND_VIEW_METHOD, int.class);
                    //反射调用Activity的findViewById方法获取View对象
                    Object target = method.invoke(activity, viewInject.value());
                    field.setAccessible(true);
                    //对字段设值
                    field.set(activity, target);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void injectListener(final Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        Method[] methods = clazz.getMethods();
        for (final Method method : methods) {
            OnClick onClick = method.getAnnotation(OnClick.class);
            if (onClick != null) {
                try {
                    int[] value = onClick.value();
                    for (int vId : value) {
                        Method methodView = clazz.getMethod(FIND_VIEW_METHOD, int.class);
                        //反射调用Activity的findViewById方法获取View对象
                        Object target = methodView.invoke(activity, vId);
                        View view = (View) target;
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    method.invoke(activity, v);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }
}

