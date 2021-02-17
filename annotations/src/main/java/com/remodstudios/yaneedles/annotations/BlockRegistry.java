package com.remodstudios.yaneedles.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface BlockRegistry {
    String namespace();
    boolean onlyCheckMarkedEntries() default false;

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    @interface Entry {
        String value() default "";
    }
}
