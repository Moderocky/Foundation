package mx.kenzie.foundation.language;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A compiler converts valid source code into code at a lower level of abstraction
 * (in this case bytecode) which can be run by the (virtual) machine.
 * <p>
 * Entered code should have been verified in advance to catch grammatical errors.
 *
 * @param <Language> The target language
 */
public interface Compiler<Language extends LanguageDefinition> {
    
    PostCompileClass compileClass(final InputStream source);
    
    PostCompileClass[] compile(final InputStream source);
    
    void compileAndLoad(final InputStream source);
    
    default void compileResource(final String main, final File target, final File... sources) {
        final InputStream[] streams = new InputStream[sources.length];
        for (int i = 0; i < sources.length; i++) {
            try {
                streams[i] = new FileInputStream(sources[i]);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        compileResource(main, target, streams);
    }
    
    void compileResource(final String main, final File target, final InputStream... sources);
    
    Language getLanguage();
    
}
