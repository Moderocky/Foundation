package mx.kenzie.foundation.assembler;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public interface IManifest {
    
    default byte[] data() {
        final StringBuilder builder = new StringBuilder();
        for (final Map.Entry<String, String> entry : this.values().entrySet()) {
            builder.append(entry.getKey())
                .append(": ")
                .append(entry.getValue())
                .append('\n');
        }
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }
    
    Map<String, String> values();
    
}
