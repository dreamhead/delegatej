package com.thoughtworks.invokable;

import java.lang.annotation.Annotation;

public class Delegate {
    public static <AnnotationClass extends Annotation> AnnotationDelegateBuilder<AnnotationClass> delegate(Class<AnnotationClass> annotationClass) {
        return new AnnotationDelegateBuilder<AnnotationClass>(annotationClass);
    }
}