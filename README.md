delegatej
=========

delegatej is annotation-based Java delegate implementation.

# Why

Inheritance sucks.

# Usage

Assume that there is an interface to implement:
```java
public interface Executable {
    String execute(String name);
}
```

Now, we need to implement a runtime Annotation to declare our method:
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Handle {
    String value();
}
```

Here, the class with our Annotation:
```java
public class Runner {
    @Handle("runner")
    public String run(String name) {
        return "hello " + name;
    }
}
```

It's time to show our **delegatej**
```java
Executable executable = delegate(Handle.class).to(Executable.class).trait(new Runner());
executable.execute("dreamhead"); // "hello dreamhead"
```

# Build

install [buildr](http://buildr.apache.org/) if you haven't, and then

    $ buildr package

The artifact will be found in target directory.