package de.frohwerk.ipm.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JcoImportParameter {
    String value();
}
