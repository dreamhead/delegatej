package com.thoughtworks.invokable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DelegateAnnotationToInterfaceBuilder<AnnotationClass extends Annotation, InterfaceClass> {
    private final Class<AnnotationClass> annotationClass;
    private final Class<InterfaceClass> interfaceClass;
    private final Method interfaceMethod;

    DelegateAnnotationToInterfaceBuilder(Class<AnnotationClass> annotationClass, Class<InterfaceClass> interfaceClass) {
        this.annotationClass = annotationClass;
        this.interfaceClass = interfaceClass;
        this.interfaceMethod = interfaceClass.getMethods()[0];
    }

    public List<Invoker<AnnotationClass, InterfaceClass>> with(Object target) {
        List<Invoker<AnnotationClass, InterfaceClass>> invokers = new ArrayList<Invoker<AnnotationClass, InterfaceClass>>();

        for (Method method : traitAnnotatedMethods(target)) {
            invokers.add(createInvoker(target, method));
        }

        return invokers;
    }

    public InterfaceClass trait(Object target) {
        List<Method> methods = traitAnnotatedMethods(target);
        checkArgument(methods.size() == 1, "only 1 annotated method target is expected, but it has " + methods.size() + " annotated methods");
        Invoker<AnnotationClass, InterfaceClass> invoker = createInvoker(target, methods.get(0));
        return invoker.asInterface();
    }

    private Invoker<AnnotationClass, InterfaceClass> createInvoker(Object target, Method method) {
        checkSignature(method);
        return new Invoker<AnnotationClass, InterfaceClass>(method.getAnnotation(this.annotationClass), this.interfaceClass, target, method);
    }

    private void checkSignature(Method method) {
        checkReturnType(method);
        checkParameter(method);
    }

    private void checkParameter(Method method) {
        Class<?>[] annotatedParameterTypes = method.getParameterTypes();
        Class<?>[] interfaceParameterTypes = interfaceMethod.getParameterTypes();
        int annotatedParameterSize = annotatedParameterTypes.length;
        int interfaceParameterSize = interfaceParameterTypes.length;

        checkArgument(annotatedParameterSize == interfaceParameterSize, "annotated method [" + method.getName() + "] should have same size of parameter to interface method");
        for (int i = 0; i < annotatedParameterSize; i++) {
            checkArgument(isAcceptableType(annotatedParameterTypes[i], interfaceParameterTypes[i]), "annotated method [" + method.getName() + "] should have same parameter type to interface method");
        }
    }

    private void checkReturnType(Method method) {
        checkArgument(isAcceptableType(method.getReturnType(), interfaceMethod.getReturnType()), "annotated method [" + method.getName() + "] should have assignable return value to interface method");
    }

    private boolean isAcceptableType(Class<?> annotatedType, Class<?> interfaceType) {
        return interfaceType.isAssignableFrom(annotatedType);
    }

    private List<Method> traitAnnotatedMethods(Object target) {
        List<Method> annotatedMethods = new ArrayList<Method>();
        Method[] methods = target.getClass().getMethods();
        for (Method method : methods) {
            if (method.getAnnotation(this.annotationClass) != null) {
                annotatedMethods.add(method);
            }
        }

        return annotatedMethods;
    }

    private void checkArgument(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
