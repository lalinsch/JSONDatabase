package server;

import com.google.gson.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static server.ServerUtilities.createDatabaseIfNotExists;
import static server.ServerUtilities.writeToDatabaseFile;

public class JsonDatabase {
    private final Path dbPath;
    private final JsonObject database;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    public static final String ERROR_NO_SUCH_KEY = "{\"response\":\"ERROR\",\"reason\":\"No such key\"}";
    public static final String OK = "{\"response\":\"OK\"}";

    public JsonDatabase(String dbPath) throws IOException {
        this.dbPath = Path.of(dbPath);
        createDatabaseIfNotExists(this.dbPath);
        this.database = readFromFile(this.dbPath).getAsJsonObject();
    }

    //Reads the database from file and converts it into a JsonElement
    public JsonElement readFromFile(Path path) {
        JsonElement databaseTree = null;
        readLock.lock();
        try (Reader reader = Files.newBufferedReader(path)) {
            databaseTree = JsonParser.parseReader(reader);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        readLock.unlock();
        return databaseTree;
    }

    //After making changes, writes to the database file
    public void writeToFile(Path dbFilePath) {
        writeLock.lock();
        try (FileOutputStream fos = new FileOutputStream(dbFilePath.toString());
             OutputStreamWriter osr = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            gson.toJson(database, osr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeLock.unlock();
    }

    //method that executes the clients requests depending on the type.
    public String executeJson(String inputJson) throws IOException {
        JsonElement clientElement = JsonParser.parseString(inputJson);
        JsonObject clientObject = clientElement.getAsJsonObject();
        JsonElement clientTask = clientObject.get("type");
        String type = clientTask.getAsString();
        String response = null;

        JsonElement element = JsonParser.parseString(OK);
        JsonObject value = element.getAsJsonObject();

        if (!"exit".equals(type)) {
            switch (type) {
                case "set":
                    if (!clientObject.get("key").isJsonArray()) {
                        String newKey = clientObject.get("key").getAsString();
                        JsonElement newValue = clientObject.get("value");
                        database.add(newKey, newValue);
                        writeToFile(dbPath);
                        response = OK;
                    } else {
                        JsonArray clientArray = clientObject.get("key").getAsJsonArray();
                        JsonObject tempObject = database.getAsJsonObject();
                        for (int i = 0; i < clientArray.size(); i++) {
                            if (tempObject.has(clientArray.get(i).getAsString())) {
                                tempObject = tempObject.get(clientArray.get(i).getAsString()).getAsJsonObject();
                                if (i == clientArray.size() - 2) {
                                    tempObject.add(clientArray.get(i + 1).getAsString(), clientObject.get("value"));
                                    writeToDatabaseFile(database, dbPath);
                                    response = OK;
                                }
                            }
                        }
                    }
                    break;
                case "get":
                    if (clientObject.get("key").isJsonArray()) {
                        JsonArray clientArray = clientObject.get("key").getAsJsonArray();
                        JsonObject tempDatabase = database.getAsJsonObject();
                        for (int i = 0; i < clientArray.size(); i++) {
                            if (tempDatabase.has(clientArray.get(i).getAsString())) {
                                if (tempDatabase.get(clientArray.get(i).getAsString()).isJsonObject()) {
                                    tempDatabase = tempDatabase.get(clientArray.get(i).getAsString()).getAsJsonObject();
                                    if (i == clientArray.size() - 1) {
                                        value.add("value", tempDatabase);
                                        break;
                                    }
                                } else {
                                    value.add("value", tempDatabase.get(clientArray.get(i).getAsString()));
                                    break;
                                }
                            } else {
                                value.addProperty("response", "ERROR");
                                value.addProperty("reason", "No such key");
                            }
                        }
                    }
                    break;
                case "delete":
                    if (clientObject.get("key").isJsonArray()) {
                        JsonArray clientArray = clientObject.getAsJsonArray();
                        JsonObject tempObject = database.getAsJsonObject();
                        for (int i = 0; i < tempObject.size(); i++) {
                            if (tempObject.has(clientArray.get(i).getAsString())) {
                                tempObject = tempObject.get(clientArray.get(i).getAsString()).getAsJsonObject();
                                if (i == clientArray.size() - 2) {
                                    tempObject.remove(clientArray.get(i + 1).getAsString());
                                    writeToDatabaseFile(database, dbPath);
                                    response = OK;
                                }
                                break;
                            } else {
                                response = ERROR_NO_SUCH_KEY;
                            }
                        }
                    } else {
                        if (database.has(clientObject.get("key").getAsString())) {
                            database.remove(clientObject.get("key").getAsString());
                            writeToDatabaseFile(database, dbPath);
                            response = OK;
                        } else {
                            response = ERROR_NO_SUCH_KEY;
                        }
                    }
            }
        }
        if ("get".equals(type)) {
            return value.toString();
        } else {
            return response;
        }
    }
}
