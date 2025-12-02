package SportifyClubManager.src;

public class UserManager {
    private static  UserManager um = null;
    private User currentUser;
    private UserManager(){
        this.currentUser = null;
    }
    public static UserManager createUserManager(){
        if (um == null){
            UserManager um = new UserManager();
        }
        return um;
    }

    public void login( String id,String pwd){
        AbstractFactory f = AbstractFactory.createFactory();
        UserDAO udao = f.createUserDAO();
        this.currentUser = UserDAO.getuserbyid(id);
    }

    
}