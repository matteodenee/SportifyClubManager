package SportifyClubManager.src;

public class LoginFacade {
    private static  LoginFacade log = null;
    private LoginFacade(){}
    public static LoginFacade createLoginFacade(){
        if (log == null){
            LoginFacade log = new LoginFacade();
        }
        return log;
    }

    public void login( String id,String pwd){
        UserManager um = UserManager.createUserManager();
        um.login(id, pwd);
    }



    

    
}