package UserManagent;

public class User {
    String id ;
    String pwd ;

    public User(String id , String pwd ){
        this.id = id;
        this.pwd = pwd;
    }

    public String getId() {
        return id;
    }
    public String getPwd() {
        return pwd;
    }

    
}       