package mx.kenzie.foundation.language;

import mx.kenzie.foundation.ClassBuilder;

public record PostCompileClass(byte[] code, String name, String internalName) {
    
    
    public Class<?> compileAndLoad() {
        return new ClassBuilder(this).loadClass(name, code);
    }
    
}
