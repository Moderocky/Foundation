package mx.kenzie.foundation;

interface SubBuilder {
    
    ClassBuilder finish();
    
    SubBuilder setModifiers(int modifiers);
    
    SubBuilder addModifiers(int... modifiers);
    
    SubBuilder removeModifiers(int... modifiers);
    
}
