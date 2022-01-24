package com.summer.commons.viewannotation;

import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author summer
 * @DATE 2019/7/31
 * @Describe
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface OnClick {
    /** View IDs to which the method will be bound. */
    int[] value() default { View.NO_ID };
}