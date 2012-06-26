package com.thoughtworks.invokable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationDelegateBuilder<AnnotationClass extends Annotation> {
    private Class<AnnotationClass> annotationClass;

    AnnotationDelegateBuilder(Class<AnnotationClass> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public <InterfaceClass> DelegateAnnotationToInterfaceBuilder<AnnotationClass, InterfaceClass> to(Class<InterfaceClass> interfaceClass) {
        Method[] methods = interfaceClass.getMethods();
        if (methods.length > 1) {
            throw new IllegalArgumentException("only 1 method interface is expected");
        }

        return new DelegateAnnotationToInterfaceBuilder<AnnotationClass, InterfaceClass>(this.annotationClass, interfaceClass);
    }
}
