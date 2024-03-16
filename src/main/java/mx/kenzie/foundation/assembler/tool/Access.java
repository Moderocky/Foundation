package mx.kenzie.foundation.assembler.tool;

public interface Access {

    int ACC_PUBLIC = 0x0001; //Declared public; may be accessed from outside its package.
    int ACC_FINAL = 0x0010; //Declared final; no subclasses allowed.
    int ACC_SUPER = 0x0020; //Treat superclass methods specially when invoked by the invokespecial instruction.
    int ACC_INTERFACE = 0x0200; //Is an interface, not a class.
    int ACC_ABSTRACT = 0x0400; //Declared abstract; may not be instantiated.

}
