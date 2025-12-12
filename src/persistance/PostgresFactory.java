package persistance;

import UserManagent.PostgresUserDAO;
import UserManagent.UserDAO;
import licenceManagement.Bl.dao.LicenceDAO;
import licenceManagement.Persistance.PostgresLicenceDAO;

public class PostgresFactory extends AbstractFactory{
    public PostgresFactory(){}

    public static PostgresFactory createFactory(){
        return null;}

    @Override
    public UserDAO createUserDAO() {
        return new PostgresUserDAO();
        
    }

    public LicenceDAO createlicenceDAO(){
        return  new PostgresLicenceDAO();
    }


    
}