package database;

public interface Operation {

    public String getName(int id);

    public boolean checkTime(int id);

    public int getId(String name);
}
