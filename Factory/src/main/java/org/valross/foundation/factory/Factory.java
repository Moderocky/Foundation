package org.valross.foundation.factory;

class Factory<Builder> {

    protected final Builder builder;

    Factory(Builder builder) {
        this.builder = builder;
    }

}
