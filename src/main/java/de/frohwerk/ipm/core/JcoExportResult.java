package de.frohwerk.ipm.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JcoExportResult {
    String parameter() default "";
}
