package SportifyClubManager.src;

public class AbstractFactory {
    private static AbstractFactory ab = null;
    private AbstractFactory(){};
    public static PostgresFactory createFactory(){
        if (ab == null){AbstractFactory ab = new PostgresFactory();}
        return ab; 
    }


    
}