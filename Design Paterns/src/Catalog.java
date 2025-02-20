import java.util.*;

public class Catalog implements Subject {

    private ArrayList<Course> lista_cursuri;
    private static Catalog instance = null;
    private List<Observer> list_obsv;

    private Catalog() {
        lista_cursuri = new ArrayList<Course>();
        list_obsv = new ArrayList<Observer>();
    }

    public static Catalog getInstance() {
        if (instance == null)
            instance = new Catalog();
        return instance;
    }

    public void addCourse(Course course) {
        lista_cursuri.add(course);
    }

    public Course getCourse(String numCourse) {
        for (Course c : lista_cursuri) {
            if (numCourse.equals(c.getCourseName()))
                return c;
        }
        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Course crs : lista_cursuri) {
            sb.append(crs.toString());
        }
        return sb.toString();
    }

    public void addObserver(Observer o) {
        list_obsv.add(o);
    }

    public void removeObserver(Observer o) {
        list_obsv.remove(o);
    }

    public void notifyObservers(Grade grade) {
        Notification new_not = new Notification("A fost adaugata o noua nota: " + grade.getTotal());

        for (Observer o : list_obsv) {
            Parent p = (Parent) o;
            if (grade.getStudent().isParent(p))
                p.update(new_not);
        }
    }
}