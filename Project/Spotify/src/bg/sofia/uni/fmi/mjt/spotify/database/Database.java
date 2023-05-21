package bg.sofia.uni.fmi.mjt.spotify.database;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class Database<T extends Identifiable & Serializable> {
    protected Map<String, T> objects;

    public Database(String fileName) {
        Path file = Path.of(fileName);
        objects = new HashMap<>();

        try (var objectInputStream = new ObjectInputStream(Files.newInputStream(file))) {
            Object obj;
            while ((obj = objectInputStream.readObject()) != null) {
                T object = (T) obj;
                objects.put(object.getID(), object);
            }

        }  catch (EOFException e) {
            //throw new IllegalStateException("The files has reached its end", e);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("The files does not exist", e);
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from a file", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(String fileName) {
        Path file = Path.of(fileName);
        try (var objectOutputStream = new ObjectOutputStream(Files.newOutputStream(file))) {
            for (T object : objects.values()) {
                objectOutputStream.writeObject(object);
                objectOutputStream.flush();
            }
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while writing to a file", e);
        }
    }
}
