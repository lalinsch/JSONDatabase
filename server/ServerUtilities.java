package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ServerUtilities {
    public static JsonElement readDbFromFile(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            return JsonParser.parseReader(reader);
        }
    }

    public static void writeToDatabaseFile(JsonObject database, Path path) throws IOException {
        try (Writer writer = Files.newBufferedWriter(path)) {
            new Gson().toJson(database, writer);
        }
    }

    public static void createDatabaseIfNotExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.writeString(path, "{}");
        }
    }
}
