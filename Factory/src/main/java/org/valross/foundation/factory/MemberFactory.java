package org.valross.foundation.factory;

import org.valross.foundation.detail.Signature;

public interface MemberFactory {

    MemberFactory name(String name);

    MemberFactory signature(Signature signature);

    ClassFactory source();

}
