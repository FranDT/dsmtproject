package leveldb;

import interfaces.AuthenticationManagerInterface;

public class LevelDBAuthenticationManager extends LevelDBManager implements AuthenticationManagerInterface {

    private static LevelDBAuthenticationManager singletonDB;
    // key structure: user (no attributes are necessary in this case, just the user)
    public static LevelDBAuthenticationManager getDB() {
        synchronized (singletonDB) {
            if (singletonDB == null) {
                singletonDB = new LevelDBAuthenticationManager();
            }
        }
        return singletonDB;
    }
    private LevelDBAuthenticationManager() {
        openDB(LevelDBManager.userTable);
    }

    public void finalize() {
        //to be really sure
        closeDB();
    }

    @Override
    public boolean insertUser(String user, String value) {
        if (!isUserPresent(user)) {
            putValue(user, value);
            return true;
        }
        return false;
    }

    @Override
    public boolean isUserPresent(String user) {
        return getValue(user) != null;
    }

    @Override
    public boolean isPasswordCorrect(String user, String value) {
        return isUserPresent(user) && getValue(user).equals(value);
    }

}