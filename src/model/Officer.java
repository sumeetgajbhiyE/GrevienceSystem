package model;

public class Officer {
    private int id;
    private String name;
    private String department;
    private int resolvedCount;

    public Officer() {}

    public Officer(int id, String name, String department, int resolvedCount) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.resolvedCount = resolvedCount;
    }

    public int getId()              { return id; }
    public String getName()         { return name; }
    public String getDepartment()   { return department; }
    public int getResolvedCount()   { return resolvedCount; }

    public void setId(int id)                       { this.id = id; }
    public void setName(String name)                { this.name = name; }
    public void setDepartment(String department)    { this.department = department; }
    public void setResolvedCount(int count)         { this.resolvedCount = count; }

    @Override
    public String toString() {
        return "Officer{id=" + id + ", name='" + name + "', dept='" + department + "', resolved=" + resolvedCount + "}";
    }
}
