package mx.kenzie.foundation.assembler;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public record Manifest(String main, String creator, String built, String classpath, IManifest... sub)
    implements IManifest {
    
    public Manifest(String main, String creator, String built) {
        this(main, creator, built, null);
    }
    
    public Manifest(String main, String creator) {
        this(main, creator, null, null);
    }
    
    public Manifest() {
        this(null, "Foundation", null, null);
    }
    
    @Override
    public Map<String, String> values() {
        final Map<String, String> map = new HashMap<>();
        map.put("Manifest-Version", "1.0");
        map.put("Archiver-Version", "Zip");
        if (main != null) map.put("Main-Class", main);
        if (creator != null) map.put("Created-By", creator);
        if (built != null) map.put("Built-By", built);
        if (classpath != null) map.put("Class-Path", classpath);
        for (final IManifest manifest : sub) {
            map.putAll(manifest.values());
        }
        return map;
    }
    
    public byte[] data() {
        return this.toString().getBytes(StandardCharsets.UTF_8);
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (final Map.Entry<String, String> entry : this.values().entrySet()) {
            builder.append(entry.getKey())
                .append(": ")
                .append(entry.getValue())
                .append('\n');
        }
        return builder.toString();
    }
}
