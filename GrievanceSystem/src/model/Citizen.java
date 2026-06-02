package model;

public class Citizen {
    private int id;
    private String name;
    private String contact;
    private String email;

    public Citizen() {}

    public Citizen(int id, String name, String contact, String email) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.email = email;
    }

    public int getId()          { return id; }
    public String getName()     { return name; }
    public String getContact()  { return contact; }
    public String getEmail()    { return email; }

    public void setId(int id)               { this.id = id; }
    public void setName(String name)        { this.name = name; }
    public void setContact(String contact)  { this.contact = contact; }
    public void setEmail(String email)      { this.email = email; }

    @Override
    public String toString() {
        return "Citizen{id=" + id + ", name='" + name + "', contact='" + contact + "'}";
    }
}
