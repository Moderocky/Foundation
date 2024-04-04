package org.valross.foundation.factory;

abstract class ModifiableFactory<Builder> {

    protected final Builder builder;

    ModifiableFactory(Builder builder) {
        this.builder = builder;
    }

}
