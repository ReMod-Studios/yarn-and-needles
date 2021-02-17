package com.remodstudios.yaneedles.annotations;

import com.remodstudios.yaneedles.datagen.ResourceGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface ResGen {
    Class<? extends ResourceGenerator> value();

    /**
     * In the format {@code "key1=value1,key2=value2, ... keyN=valueN"}
     */
    String args() default "";
}
