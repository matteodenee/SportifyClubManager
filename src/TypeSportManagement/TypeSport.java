package TypeSportManagement;

public class TypeSport {

    private final int id;
    private final String name;

    public TypeSport(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name; // pour afficher dans la ComboBox
    }

    public static TypeSport valueOf(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'valueOf'");
    }

    public String getNom() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNom'");
    }

    public static Object values() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'values'");
    }
    
}
