package com.summer.commons.authoritymanagement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author summer
 * @DATE 2019/8/1
 * @Describe
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionGranted {
    int value();
}
