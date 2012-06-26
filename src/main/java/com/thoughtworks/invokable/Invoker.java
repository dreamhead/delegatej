package com.thoughtworks.invokable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Invoker<AnnotationClass, InterfaceClass> {
    private AnnotationClass annotation;
    private final Class<InterfaceClass> interfaceClass;
    private final Object target;
    private final Method method;

    public Invoker(AnnotationClass annotation, Class<InterfaceClass> interfaceClass, Object target, Method method) {
        this.annotation = annotation;
        this.interfaceClass = interfaceClass;
        this.target = target;
        this.method = method;
    }

    public InterfaceClass asInterface() {
        return interfaceClass.cast(createInstance());
    }

    private Object createInstance() {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]{interfaceClass}, new InternalInvoker(target, method));
    }

    public AnnotationClass annotation() {
        return this.annotation;
    }

    private static class InternalInvoker implements InvocationHandler {
        private final Object target;
        private final Method targetMethod;

        public InternalInvoker(Object target, Method targetMethod) {
            this.target = target;
            this.targetMethod = targetMethod;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            return targetMethod.invoke(target, objects);
        }
    }
}
