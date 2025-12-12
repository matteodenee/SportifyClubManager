package persistance;

import UserManagent.UserDAO;
import licenceManagement.Bl.dao.LicenceDAO;

public abstract class AbstractFactory {

    private static AbstractFactory instance = null;

    protected AbstractFactory() {}

    public static AbstractFactory getFactory() {
        if (instance == null) {
            instance = new PostgresFactory();
        }
        return instance;
    }

    public abstract UserDAO createUserDAO();
    public abstract LicenceDAO createlicenceDAO();

}
