import java.util.*;

interface Element {
  void accept(Visitor visitor);
}

interface Visitor {
  void visit(Assistant assistant);

  void visit(Teacher teacher);
}

class Pair<K, V1, V2> {
  private K key;
  private V1 value1;
  private V2 value2;

  public Pair(K key, V1 value1, V2 value2) {
    this.key = key;
    this.value1 = value1;
    this.value2 = value2;
  }

  public K getKey() {
    return key;
  }

  public V1 getValue1() {
    return value1;
  }

  public V2 getValue2() {
    return value2;
  }
}

public class ScoreVisitor implements Visitor {
  private HashMap<Teacher, ArrayList<Pair<Student, String, Double>>> examScores;
  private HashMap<Assistant, ArrayList<Pair<Student, String, Double>>> partialScores;
  private Catalog catalog;

  public ScoreVisitor(HashMap<Teacher, ArrayList<Pair<Student, String, Double>>> examScores,
      HashMap<Assistant, ArrayList<Pair<Student, String, Double>>> partialScores, Catalog catalog) {
    this.examScores = examScores;
    this.partialScores = partialScores;
    this.catalog = catalog;
  }

  public void visit(Assistant assistant) {
    ArrayList<Pair<Student, String, Double>> scores = partialScores.get(assistant);
    if (scores != null) {
      for (Pair<Student, String, Double> entry : scores) {
        Student student = entry.getKey();
        String courseName = entry.getValue1();
        Double score = entry.getValue2();

        Course course = catalog.getCourse(courseName);
        Grade grade = course.getGrade(student);
        if (grade == null) {
          grade = new Grade(courseName, student);
          course.addGrade(grade);
        }
        grade.setPartialScore(score);
      }
    }
  }

  public void visit(Teacher teacher) {
    ArrayList<Pair<Student, String, Double>> scores = examScores.get(teacher);
    if (scores != null) {
      for (Pair<Student, String, Double> entry : scores) {
        Student student = entry.getKey();
        String courseName = entry.getValue1();
        Double score = entry.getValue2();

        Course course = catalog.getCourse(courseName);
        Grade grade = course.getGrade(student);
        if (grade == null) {
          grade = new Grade(courseName, student);
          course.addGrade(grade);
        }
        grade.setExamScore(score);
      }
    }
  }

  @Override
  public String toString() {
    return catalog.toString();
  }
}