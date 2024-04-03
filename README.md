Foundation
=====

## Note

Foundation 3 is currently in the testing phase and may undergo changes without notice.
Foundation 3 aims to be semi-compatible with Foundation 2 projects, but due to the change in its backing library,
full compatibility cannot be guaranteed.

### Opus #7

Foundation is a tool for writing virtual machine code, a class file assembler, and a library for creating compilers that
target JVM bytecode.

Foundation 3 is modularised into the following libraries:

- foundation-assembler: for assembling class files in a linear format (like [ASM](https://asm.ow2.io)).
- foundation-factory: a helper layer for building method code in the Java paradigm.
- foundation-legacy: the former Foundation 2, for preserving compatibility.

## Motivations

Foundation is designed to simplify the process of building classes and methods with bytecode.
Tools like [ASM](https://asm.ow2.io)'s 'visitor' are very effective but make simple tasks unnecessarily difficult and
add
cumbersome boilerplate.

This over-complication makes writing simple bytecode-generating utilities more difficult than it needs to be and makes
the process unpleasant for beginners, leading people to rely on tools such as 'ASMifier' to create the
bytecode-generating code.

While such tools are undoubtedly useful (and appropriate in many situations) users can develop an unnecessary dependency
on them and find themselves incapable of designing the bytecode without a tool to generate it for them.

Foundation is designed to set a balance between doing too much and too little, while also matching the line format and
structure of Java code.

# Foundation 3: Assembler

Foundation 3 entirely removes the dependency on ObjectWeb's [ASM](https://asm.ow2.io) library, and contains its own
fully-supported class file assembler.

## Design Goals

In order to achieve its speed and low memory footprint, ASM has some incredibly strict design limitations:

- Elements have to be visited in a specific order.
- Operation codes have to be entered with a particular method.
- Once visited, an element cannot be re-visited, accessed or altered in any way.
- Safety checking and verification are left to the user.
- Errors (particularly in frame calculation) are almost impossible to trace.

Foundation versions 1 and 2 attempted to overcome some of these limitations by storing all the details and then building
the class file in a single pass at the end. While this was effective for overcoming the ordering constraints,
it could not address any of the other problems listed above.

Foundation 3 includes its own assembler (a replacement for ASM) that was designed with overcoming these limitations in
mind.

## Ordering of Access & Creation

A key requirement for Foundation 3's assembler was not to limit the order in which class and code members could be
created, as much as was feasibly possible.
Since the class file itself has a fixed order in which elements appear (meta, fields, methods, attributes), this meant
that the builder could not write the class bytes _directly_ to the file, since a user might create a method and then go
back and make a field.

The solution was to create a data graph format that was flexible enough to be edited.
This brought with it an additional problem: lots of class file elements use indices to reference each other (e.g.
constant index 3, jump forward 4 spaces), and if elements can be re-ordered then these indices will be invalidated.

This is most relevant to the class file's constant pool, which stores everything from the class names to the literal
values used in code. The easiest solution (which ASM uses) is to enter things as they appear and then save the index
at which they were entered. However, this has a drawback of leaving the constant pool in a very untidy state with
no proper ordering, and also creates a forward referencing issue (where a constant pool entry might reference something
that was entered after it, e.g. `3: class named #4, 4: 'java/lang/String'`). This in itself is not prohibited by the
virtual machine specification, but it *is* a problem for anything that is searching for a pool entry in order.

Foundation's solution to all of these problems is the tabular reference; a two-byte dynamic pointer to an entry in a
table, such as a constant pool entry or an instruction in a code vector.
These references mean the constant pool can be sorted, re-ordered or can have items added or removed without breaking
any of the elements that reference it.
During a phase called 'finalisation', which occurs before the class code is built, the references can be baked into
their actual number format since no more changes to the table will occur.

## Operation Codes

> Definition: a 'well-formed instruction' consists of an operation code (opcode) followed by the
> bytes required to complete the instruction.

Method code is written in a sequence of operation codes. Some codes are atomic (followed by nothing else) and
well-formed instructions in their own right, (e.g. `iload_0/26`) whereas others are followed by a fixed number of data
bytes
(e.g. `iload/21 <index>` or `goto/167 <index>`), and need other data to be provided in order to constitute a
well-formed instruction (i.e. writing `goto` on its own does nothing, it **must** be followed by two branch bytes).

As a result, the process to write a bytecode changes according to the opcode used. This gives the designer a choice:
either the designer allows the user to write _whatever_ they want (e.g. `write(byte opcode, byte... bytes)`,
in which case it becomes the user's responsibility to read and understand the virtual machine specification and what
each opcode requires, or the designer has to create individual processes for opcodes based on the information they
require (e.g. `writeJump(byte opcode, short target)`). ASM follows the latter option, having individual visit methods
for different instruction types.

The approach used by Foundation's assembler is a combination of the two. A single form of the `write(Element)` method
is provided, and the process to create a well-formed instruction is attached to the opcode constants in
`mx.kenzie.foundation.assembler.code.OpCode`.

For atomic codes, where the opcode alone is a well-formed instruction, the opcode constant can be entered as-is:

```java
import mx.kenzie.foundation.assembler.tool.CodeBuilder;

class Example {

    void example(CodeBuilder builder) {
        builder.write(POP);
        builder.write(ALOAD_0, ILOAD_1);
        builder.write(SWAP);
    }

}
```

In the other case, a well-formed instruction can be created from a non-atomic opcode constant:

```java
import mx.kenzie.foundation.assembler.tool.CodeBuilder;

class Example {

    void example(CodeBuilder builder) {
        builder.write(ALOAD.var(6), CHECKCAST.type(String.class));
    }

}
```

The advantage of this approach is that a well-formed instruction can be stored (or provided by some other part of a
program) and then be entered into the code vector as-is without having to know which entry method to use.

## Safety and Verification

# Foundation 2 & Legacy Library

Since Foundation 2, instructions for the class builder do **not** directly correspond to JVM 'opcodes'.
This was changed to make it easier and more intuitive to write code.

Foundation 2 aims to reflect Java code line structure, so that users do not need to worry about managing the stack.

```
// java
Object var1 = "hello there"
// builder
STORE_VAR.object(1, CONSTANT.of("hello there"))
```

```
// java
return var1
// builder
RETURN.object(LOAD_VAR.object(1)))
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
    <version>3.0.0</version>
    <scope>compile</scope>
</dependency>
```

### Supported Functionality

Foundation supports almost all basic instructions from Java, including variable and field access, method calls,
branches, arithmetic and instantiation.

Foundation 2 has dropped support for individual bytecode instructions to provide more safety for beginners handling
stack operations.

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

