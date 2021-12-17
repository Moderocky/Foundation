package mx.kenzie.foundation.language;

import java.io.InputStream;

/**
 * Pre-compiled source code can be entered into a verifier to
 * check its validity and spot mistakes, warnings and potential compilation errors.
 *
 * @param <Language> The target language
 */
public interface Verifier<Language extends LanguageDefinition> {
    
    boolean verify(final InputStream source);
    
    Language getLanguage();
}
