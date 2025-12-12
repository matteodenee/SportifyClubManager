package SportifyClubManager.facade;

import SportifyClubManager.services.User;
import SportifyClubManager.services.UserManager;


public class LoginFacade {
    private static  LoginFacade log = null;
    private LoginFacade(){}
    public static LoginFacade createLoginFacade(){
        if (log == null){
             log = new LoginFacade();
        }
        return log;
    }

    public User login( String id,String pwd){
        UserManager um = UserManager.createUserManager();
        return um.login(id, pwd);
    }



    

    
}
