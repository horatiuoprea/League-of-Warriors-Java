import java.util.*;

interface Strategy {
    Student getBestStudent(Collection<Grade> grades);
}

class BestPartialScore implements Strategy {
    @Override
    public Student getBestStudent(Collection<Grade> grades) {
        return grades.stream()
                .max(Grade.compareByPartialScore)
                .map(Grade::getStudent)
                .orElse(null);
    }
}

class BestExamScore implements Strategy {
    @Override
    public Student getBestStudent(Collection<Grade> grades) {
        return grades.stream()
                .max(Grade.compareByExamScore)
                .map(Grade::getStudent)
                .orElse(null);
    }
}

class BestTotalScore implements Strategy {
    @Override
    public Student getBestStudent(Collection<Grade> grades) {
        return grades.stream()
                .max(Grade.compareByTotalScore)
                .map(Grade::getStudent)
                .orElse(null);
    }
}

public class Course {
    private String courseName;
    private Teacher course_teacher;
    private List<Assistant> assistants;
    private List<Grade> grades;
    private List<Student> students;
    private Strategy strategy;

    private Course(CourseBuilder builder) {
        this.courseName = builder.courseName;
        this.course_teacher = builder.course_teacher;
        this.assistants = builder.assistants;
        this.grades = builder.grades;
        this.students = builder.students;
        this.strategy = builder.strategy;
    }

    public String getCourseName() {
        return courseName;
    }

    public Teacher getTeacher() {
        return course_teacher;
    }

    public List<Assistant> getAssistants() {
        return assistants;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public Grade getGrade(Student student) {
        for (Grade gr : grades) {
            if (gr.getStudent().equals(student)) {
                return gr;
            }
        }
        return null;
    }

    public void addGrade(Grade grade) {
        grades.add(grade);
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public Student getBestStudent() {
        if (strategy == null) {
            throw new IllegalStateException("Strategy not set.");
        }
        return strategy.getBestStudent(grades);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(courseName).append("\n");
        sb.append("Nume profesor: ").append(course_teacher.firstName).append(" ").append(course_teacher.lastName)
                .append("\n");
        sb.append("Asistenti: ");
        for (Assistant ast : assistants) {
            sb.append(ast.firstName).append(" ").append(ast.lastName).append("\n");
        }
        sb.append("Studenti si note:\n");
        for (Grade grade : grades) {
            sb.append(grade.getStudent().firstName).append(" ").append(grade.getStudent().lastName).append(" ")
                    .append(grade.getTotal()).append("\n");
        }
        return sb.toString();
    }

    public static class CourseBuilder {
        private String courseName;
        private Teacher course_teacher;
        private Strategy strategy;
        private List<Assistant> assistants = new ArrayList<>();
        private List<Grade> grades = new ArrayList<>();
        private List<Student> students = new ArrayList<>();

        public CourseBuilder(String courseName) {
            this.courseName = courseName;
        }

        public CourseBuilder teacher(Teacher course_teacher) {
            this.course_teacher = course_teacher;
            return this;
        }

        public CourseBuilder assistant(Assistant assistant) {
            this.assistants.add(assistant);
            return this;
        }

        public CourseBuilder grade(Grade grade) {
            this.grades.add(grade);
            return this;
        }

        public CourseBuilder student(Student student) {
            this.students.add(student);
            return this;
        }

        public CourseBuilder strategy(Strategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public Course build() {
            return new Course(this);
        }
    }
}

class Grade {
    private Double partialScore, examScore;
    private Student student;
    private String course;

    public Grade(String course, Student student) {
        partialScore = 0.0;
        examScore = 0.0;
        this.course = course;
        this.student = student;
    }

    public Grade(String course, Student student, Double partialScore, Double examScore) {
        this.partialScore = partialScore;
        this.examScore = examScore;
        this.course = course;
        this.student = student;
    }

    public void setPartialScore(Double score) {
        partialScore = score;
    }

    public void setExamScore(Double score) {
        examScore = score;
    }

    public Double getTotal() {
        return partialScore + examScore;
    }

    public Double getPartialScore() {
        return partialScore;
    }

    public Double getExamScore() {
        return examScore;
    }

    public Student getStudent() {
        return student;
    }

    @Override
    public String toString() {
        return getTotal().toString();
    }

    public static Comparator<Grade> compareByTotalScore = (g1, g2) -> Double.compare(g1.getTotal(), g2.getTotal());

    public static Comparator<Grade> compareByPartialScore = (g1, g2) -> Double.compare(g1.getPartialScore(),
            g2.getPartialScore());

    public static Comparator<Grade> compareByExamScore = (g1, g2) -> Double.compare(g1.getExamScore(),
            g2.getExamScore());
}