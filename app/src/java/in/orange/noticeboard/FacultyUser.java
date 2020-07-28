package in.orange.noticeboard;

public class FacultyUser{

    public String name,email,department,image,role;
    public FacultyUser(String name,String email,String department,String image,String role)
    {
        this.department=department;
        this.name=name;
        this.email=email;
        this.image=image;
        this.role=role;
    }
    public FacultyUser(){}
}
