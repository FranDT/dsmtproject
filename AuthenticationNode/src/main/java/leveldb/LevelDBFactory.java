package leveldb;

import interfaces.DBFactory;
import interfaces.AuthenticationManagerInterface;

public class LevelDBFactory implements DBFactory {

    @Override
    public AuthenticationManagerInterface createUserManager() {
        return new LevelDBAuthenticationManager();
    }
}
