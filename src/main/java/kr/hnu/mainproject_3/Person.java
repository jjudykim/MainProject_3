package kr.hnu.mainproject_3;

import java.io.Serializable;

public class Person implements Serializable {
    private String id;
    private String password;
    private String name;
    private String department;
    private String photo;

    public Person(String id, String password, String name, String department, String photo) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.department = department;
        this.photo = photo;
    }

    public Person(String id, String password, String name, String department) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.department = department;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setDepartment(String department) { this.department = department; }

    public void setPhoto(String photo) { this.photo = photo; }

    public String getID()
    {
        return this.id;
    }

    public String getPassword()
    {
        return this.password;
    }

    public String getName()
    {
        return this.name;
    }

    public String getDepartment() { return this.department; }

    public String getPhoto() { return this.photo; }
}
