package com.thoughtworks.invokable;

public class Runner {
    @Handle("runner")
    public String run(String name) {
        return "hello " + name;
    }
}
