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

    public List<Invoker<AnnotationClass, InterfaceClass>> traitAllWithAnnotation(Object target) {
        return createInvokers(target, traitAnnotatedMethodsWithNonEmptyCheck(target));
    }

    public Invoker<AnnotationClass, InterfaceClass> traitWithAnnotation(Object target) {
        return createInvoker(target, traitAnnotatedMethodAsSingle(target));
    }

    public InterfaceClass trait(Object target) {
        return createInvoker(target, traitAnnotatedMethodAsSingle(target)).asInterface();
    }

    public List<InterfaceClass> traitAll(Object target) {
        return traitInterfaceFromMethod(target, traitAnnotatedMethodsWithNonEmptyCheck(target));
    }

    private Method traitAnnotatedMethodAsSingle(Object target) {
        return traitAnnotatedMethodsWithNonEmptyCheck(target).get(0);
    }

    private List<InterfaceClass> traitInterfaceFromMethod(Object target, List<Method> methods) {
        List<InterfaceClass> results = new ArrayList<InterfaceClass>();
        for (Method method : methods) {
            results.add(createInvoker(target, method).asInterface());
        }

        return results;
    }

    private Invoker<AnnotationClass, InterfaceClass> createInvoker(Object target, Method method) {
        checkSignature(method);
        return new Invoker<AnnotationClass, InterfaceClass>(method.getAnnotation(this.annotationClass), this.interfaceClass, target, method);
    }

    private List<Invoker<AnnotationClass, InterfaceClass>> createInvokers(Object target, List<Method> methods) {
        List<Invoker<AnnotationClass, InterfaceClass>> invokers = new ArrayList<Invoker<AnnotationClass, InterfaceClass>>();
        for (Method method : methods) {
            invokers.add(createInvoker(target, method));
        }

        return invokers;
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
        for (Method method : target.getClass().getMethods()) {
            if (method.getAnnotation(this.annotationClass) != null) {
                annotatedMethods.add(method);
            }
        }

        return annotatedMethods;
    }

    private List<Method> traitAnnotatedMethodsWithNonEmptyCheck(Object target) {
        List<Method> methods = traitAnnotatedMethods(target);
        checkArgument(methods.size() > 0, "annotated method on target is expected");
        return methods;
    }

    private void checkArgument(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
