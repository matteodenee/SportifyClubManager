package SportifyClubManager.src;

public class UserManager {
    private static  UserManager um = null;
    private User currentUser;
    private UserManager(){
        this.currentUser = null;
    }
    public static UserManager createUserManager(){
        if (um == null){
             um = new UserManager();
        }
        return um;
    }

    public User login( String id,String pwd){
        AbstractFactory f = AbstractFactory.createFactory();
        UserDAO udao = f.createUserDAO();
        User user = udao.getUserById(id);
        if (user != null){
            if (user.getPwd() == pwd) {
                this.currentUser = user;
            }
            else{ this.currentUser = null;

            }
        }
        return this.currentUser ;
        
    }

    
}