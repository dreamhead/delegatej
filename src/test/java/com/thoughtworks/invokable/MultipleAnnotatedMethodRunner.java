package com.thoughtworks.invokable;

public class MultipleAnnotatedMethodRunner {
    @Handle("runner")
    public String run(String name) {
        return "run " + name;
    }

    @Handle("go")
    public String go(String name) {
        return "go " + name;
    }
}
