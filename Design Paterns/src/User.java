import java.util.*;

public abstract class User {
  protected String firstName, lastName;

  public User(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public User() {
    this(null, null);
  }

  public String toString() {
    return firstName + " " + lastName;
  }
}

enum Users {
  Student, Parent, Assistant, Teacher
}

class Student extends User {

  private Parent parent;

  public Parent getParent() {
    return parent;
  }

  public void setParent(Parent parent) {
    this.parent = parent;
  }

  public void setMother(Parent mother) {
    setParent(mother);
  }

  public void setFather(Parent father) {
    setParent(father);
  }

  public boolean isParent(Parent parent) {
    return this.parent == parent;
  }

  public Student(String firstName, String lastName) {
    super(firstName, lastName);
  }
}

class Parent extends User implements Observer {

  private List<Notification> lista_notif;

  public Parent(String firstName, String lastName) {
    super(firstName, lastName);
    lista_notif = new ArrayList<Notification>();
  }

  public void update(Notification notification) {
    lista_notif.add(notification);
    System.out.println(notification);
  }
}

class Assistant extends User implements Element {
  public Assistant(String firstName, String lastName) {
      super(firstName, lastName);
  }

  public void accept(Visitor visitor) {
      visitor.visit(this);
  }
}

class Teacher extends User implements Element {
  public Teacher(String firstName, String lastName) {
      super(firstName, lastName);
  }

  public void accept(Visitor visitor) {
      visitor.visit(this);
  }
}

class UserFactory {

  public static User createUser(String str_type, String firstName, String lastName) {
    Users type = Users.valueOf(str_type);
    switch (type) {
      case Student:
        return new Student(firstName, lastName);
      case Parent:
        return new Parent(firstName, lastName);
      case Assistant:
        return new Assistant(firstName, lastName);
      case Teacher:
        return new Teacher(firstName, lastName);
      default:
        return null;
    }
  }
}