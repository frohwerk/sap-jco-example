package de.frohwerk.ipm.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import static com.google.common.base.Preconditions.checkNotNull;

public class Annotations {
    public static <A extends Annotation> A annotation(final AnnotatedElement annotatedElement, final Class<A> annotationType) {
        checkNotNull(annotatedElement, "annotatedElement may not be null");
        checkNotNull(annotationType, "annotationType may not be null");
        return checkNotNull(annotatedElement.getAnnotation(annotationType), "%s annotation missing", annotatedElement.getClass().getSimpleName());
    }
}
