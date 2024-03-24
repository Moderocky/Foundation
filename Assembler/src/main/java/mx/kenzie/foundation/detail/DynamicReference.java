package mx.kenzie.foundation.detail;

import org.valross.constantine.RecordConstant;

import java.lang.constant.Constable;

public record DynamicReference(Type type, Signature signature, Member.Invocation invocation, Constable... arguments)
    implements RecordConstant {

    public enum Type {
        CONSTANT,
        INVOCATION
    }

}
