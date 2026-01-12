package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

/**
 * Resolve readable/writable JSON files.
 *
 * <p>Instead of hard-coding paths like "src/main/resources/..." (which breaks when
 * the working directory changes or when packaging a jar), this class uses a runtime
 * folder called <b>data</b> in the current working directory.</p>
 *
 * <p>If a file is missing, it will be bootstrapped from classpath resources
 * under <b>/data/</b> (if present) or created as an empty JSON array.</p>
 */
public final class DataStore {
    private static final String DATA_DIR = "data";
    private static final Set<String> LOGGED = new HashSet<>();

    private DataStore() {}

    public static File getDataFile(String fileName) {
        try {
            Path dir = Path.of(System.getProperty("user.dir"), DATA_DIR);
            Files.createDirectories(dir);

            Path file = dir.resolve(fileName);
            if (!Files.exists(file)) {
                bootstrapFromResources(fileName, file);
            }
            if (LOGGED.add(fileName)) {
                System.out.println("[DataStore] Using: " + file.toAbsolutePath());
            }
            return file.toFile();
        } catch (IOException e) {
            // Fallback to a relative file in current working directory.
            return new File(DATA_DIR + File.separator + fileName);
        }
    }

    private static void bootstrapFromResources(String fileName, Path target) throws IOException {
        String resourcePath = "/data/" + fileName;
        try (InputStream in = DataStore.class.getResourceAsStream(resourcePath)) {
            if (in != null) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                return;
            }
        }

        // Create an empty JSON array so Jackson can parse it as List<...>
        try (FileOutputStream out = new FileOutputStream(target.toFile())) {
            out.write("[]".getBytes());
        }
    }
}
