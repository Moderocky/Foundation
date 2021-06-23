package mx.kenzie.foundation.opcodes;

import static org.objectweb.asm.Opcodes.*;

public enum JavaVersion {
    JAVA_17(V17),
    JAVA_16(V16),
    JAVA_15(V15),
    JAVA_14(V14),
    JAVA_13(V13),
    JAVA_12(V12),
    JAVA_11(V11),
    JAVA_10(V10),
    JAVA_9(V9),
    JAVA_8(V1_8),
    JAVA_7(V1_7),
    JAVA_6(V1_6),
    JAVA_5(V1_5),
    JAVA_4(V1_4),
    JAVA_3(V1_3),
    JAVA_2(V1_2),
    JAVA_1(V1_1);
    public final int version;
    
    JavaVersion(int version) {
        this.version = version;
    }
}
