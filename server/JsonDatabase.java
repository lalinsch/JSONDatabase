package server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static server.ServerUtilities.*;

public class JsonDatabase {
    private final Path dbPath;
    private final JsonObject database;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    public static final String ERROR_NO_SUCH_KEY = "{\"response\":\"ERROR\",\"reason\":\"No such key\"}";
    public static final String ERROR_INCORRECT_JSON = "{\"response\":\"ERROR\",\"reason\":\"Incorrect JSON\"}";
    public static final String OK = "{\"response\":\"OK\"}";

    public JsonDatabase(String dbPath) throws IOException {
        this.dbPath = Path.of(dbPath);
        createDatabaseIfNotExists(this.dbPath);
        this.database = readDbFromFile(this.dbPath).getAsJsonObject();
    }

    public String set(String key, String value) throws IOException {
        writeLock.lock();
        try {
            database.addProperty(key, value);
            writeToDatabaseFile(database, dbPath);
        } finally {
            writeLock.unlock();
        }
        return OK;
    }

    public String get(String key) {
        String result = null;
        readLock.lock();
        try {
            JsonElement value = database.get(key);
            if (value != null) {
                result = value.getAsString();
            }
        } finally {
            readLock.unlock();
        }
        return result != null ? "{\"result\" : \"OK\", \"value\" : \"" + result + "\"}" : ERROR_NO_SUCH_KEY;
    }

    public String delete(String key) throws IOException {
        String result = ERROR_NO_SUCH_KEY;
        writeLock.lock();
        try {
            if (database.remove(key) != null) {
                writeToDatabaseFile(database, dbPath);
                result = OK;
            }
        } finally {
            writeLock.unlock();
        }
        return result;
    }

    public String executeJson(String json) throws IOException {
        JsonObject jsonObject;
        String type;
        String key;
        String value = null;

        try {
            jsonObject = JsonParser.parseString(json).getAsJsonObject();
            type = jsonObject.get("type").getAsString();
            key = jsonObject.get("key").getAsString();
            if (type.equals("set")) {
                value = jsonObject.get("value").getAsString();
            }
        } catch (IllegalStateException | NullPointerException | JsonSyntaxException e) {
            return ERROR_INCORRECT_JSON;
        }
        if ((value == null && jsonObject.size() != 2) || (value != null && jsonObject.size() != 3)) {
            return ERROR_INCORRECT_JSON;
        }

        switch (type) {
            case "get": return get(key);
            case "set": return set(key, value);
            case "delete": return delete(key);
            default: return ERROR_INCORRECT_JSON;
        }
    }
}
