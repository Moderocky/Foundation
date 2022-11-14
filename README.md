Foundation
=====

### Opus #7

Foundation is designed to simplify the process of building classes and methods with bytecode.
Tools like [ASM](https://asm.ow2.io)'s visitor are very effective but make simple tasks unnecessarily difficult and add cumbersome boilerplate.

This over-complication makes writing simple bytecode-generating utilities more difficult than it needs to be and makes the process unpleasant for beginners, leading people to rely on tools such as 'ASMifier' to create the bytecode-generating code.

While such tools are undoubtedly useful (and appropriate in many situations) users can develop an unnecessary dependency on them and find themselves incapable of designing the bytecode without the tool to do it for them.

Foundation is designed to set a balance between doing too much and too little, while also matching the line format and structure of Java code.
Foundation uses [ASM](https://asm.ow2.io) internally to create the bytecode and automatically calculate stack map frames and sizes.


## Foundation 2
From Foundation 2, instructions do **not** directly correspond to JVM 'opcodes'.
This was changed to make it easier and more intuitive to write code.

Foundation 2 aims to reflect Java code line structure, so that users do not need to worry about managing the stack.

```kt
// Object var1 = "hello there"
method.line(STORE_VAR.object(1, CONSTANT.of("hello there"));
// return var1
method.line(RETURN.object(LOAD_VAR.object(1))));
```

### Maven Information

```xml
<repository>
    <id>kenzie</id>
    <url>https://repo.kenzie.mx/releases</url>
</repository>
``` 

```xml
<dependency>
    <groupId>mx.kenzie</groupId>
    <artifactId>foundation</artifactId>
    <version>2.0.0</version>
    <scope>compile</scope>
</dependency>
```

### Supported Functionality

Foundation has instructions for almost all of the basic operation codes, a list of which can be found [here](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html).

Many of these have been wrapped in simplified helper methods for utility (and to make their purpose clearer.)

Foundation also supports the subroutine jump/return instructions which were removed in Java 6 (sadly) - to use these, the compilation target must be set to `JAVA_5` and computation must be set to `1`. Subroutines were re-usable code sections that could be jumped to and would return to the pre-jump position upon completion.

### Examples

Generate and load a very simple class.

```java
class MyClass {
    Class<?> test() {
        final PreClass builder = new PreClass("org.example", "Thing");
        final PreMethod method = new PreMethod(PUBLIC, STATIC, VOID, "main", String[].class);
        method.line(RETURN.none());
        builder.add(method);
        return builder.load(Loader.DEFAULT); // built-in basic class loader
    }
}
 ```

This would generate the code:
```java
public class Thing {
    public static void main(String[] args) {
        return;
    }
}
```

Generate a runnable class.

```java
class MyClass {
    Class<?> test() {
        // references to System.out and out.println(..)
        final CallMethod.Stub target = METHOD.of(PrintStream.class, "println", String.class);
        final AccessField.Stub field = FIELD.of(System.class, "out", PrintStream.class);
        
        final PreClass builder = new PreClass("org.example", "Thing");
        builder.addInterfaces(Runnable.class);
        final PreMethod method = new PreMethod(PUBLIC, VOID, "run");
        method.line(target.call(field.get(), CONSTANT.of("hello there!")));
        method.line(RETURN.none());
        builder.add(method);
        return builder.load(Loader.DEFAULT); // built-in basic class loader
    }
}
```

This would be the equivalent of:
```java
class Thing implements Runnable {
    
    @Override // Overrides are implicit.
    public void run() {
        System.out.println("hello there!");
    }
}
```

