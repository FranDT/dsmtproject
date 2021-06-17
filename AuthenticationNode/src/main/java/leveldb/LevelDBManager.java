package leveldb;

import org.iq80.leveldb.*;
import static org.iq80.leveldb.impl.Iq80DBFactory.*;
import java.io.File;
import java.io.IOException;


abstract class LevelDBManager {

    static final String userTable = "user";
    protected DB db;

    protected void openDB(String tableName) {
        if (db != null) return;

        Options options = new Options();
        options.createIfMissing(true);
        try {
            db = factory.open(new File(tableName), options);
        }
        catch (IOException e) {closeDB();}
    }
    protected void closeDB() {
        if (db == null) return;
        try {
            db.close();
            db = null;
        }
        catch (IOException e) {e.printStackTrace();}
    }

    protected void putValue(String key, String value){
        db.put(bytes(key), bytes(value));
    }
    protected void deleteValue(String key){
        db.delete(bytes(key));
    }
    protected String getValue(String key){
        try {
            return asString(db.get(bytes(key)));
        }
        catch (java.util.NoSuchElementException e) {
            return null;
        }
    }
    protected void printTable() {
        try (DBIterator iterator = db.iterator())
        {
            for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());
                String value = asString(iterator.peekNext().getValue());
                System.out.println(key + " = " + value);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}