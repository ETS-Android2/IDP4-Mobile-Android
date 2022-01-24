package com.summer.commons.viewannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author summer
 * @DATE 2019/7/31
 * @Describe
 */
@Target(ElementType.TYPE)
@Retention(RUNTIME)
public @interface ContentView {
    int value();
}