package Exercices;

//package mk.ukim.finki.vtor_kolokvium;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class OperationNotAllowedException extends Exception {
    public OperationNotAllowedException(String message) {
        super(message);
    }
}

abstract class Student {
    String id;
    List<Subject> subjects;
    Map<Integer, List<Integer>> gradesByTerm;

    public String getId() {
        return id;
    }

    public Student(String id) {
        this.id = id;
//        this.yearsOfStudies = yearsOfStudies;
        this.subjects = new ArrayList<>();
        this.gradesByTerm = new HashMap<>();
    }

    public abstract boolean hasGraduated();

    public abstract void addGrade(int term, String courseName, int grade) throws OperationNotAllowedException;

    void validation(int term) throws OperationNotAllowedException {
        if (!gradesByTerm.containsKey(term))
            throw new OperationNotAllowedException(String.format("Term %d is not possible for student with ID %s", term, id));
        if (gradesByTerm.get(term).size() == 3)
            throw new OperationNotAllowedException(String.format("Student %s already has 3 grades in term %d", id, term));
    }

    public double avgGrade() {
        return gradesByTerm.values()
                .stream()
                .flatMap(Collection::stream)
                .mapToInt(g -> g)
                .average().orElse(5.0);
    }

    public double avgGradeForTerm(int term) {
        return gradesByTerm.get(term)
                .stream().mapToInt(g -> g)
                .average().orElse(5.0);
    }

    int countOfCoursesPassed() {
        return gradesByTerm.values()
                .stream()
                .mapToInt(List::size)
                .sum();
    }

    public String graduationLog() {
        return String.format("Student with ID %s graduated with average grade %.2f", id, avgGrade());
    }

    public String getTermReport(int term) {
        return String.format("Term %d\nCourses: %d\nAverage grade for term: %.2f", term,
                gradesByTerm.get(term).size(), avgGradeForTerm(term));
    }

    public String shortReport(String id) {
        return String.format("Student: %s Courses passed: %d Average grade: %.2f", id, countOfCoursesPassed(), avgGrade());
    }

    public String getDetailedReport() {
        Comparator<Subject> comparator = Comparator.comparing(Subject::getName);
        subjects = subjects.stream().sorted(comparator).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Student: %s\n", id));
        gradesByTerm.keySet()
                .forEach(term -> sb.append(getTermReport(term)).append("\n"));
        sb.append(String.format("Average grade: %.2f\nCourses attended: %s", avgGrade(),
                String.join(",", subjects.toString().replaceAll(" ", "").replaceAll("]", "").replaceAll("\\[", ""))));
        return sb.toString();
    }


}

class ThreeYearStudent extends Student {

    public ThreeYearStudent(String id) {
        super(id);
        IntStream.range(1, 7).forEach(i -> gradesByTerm.putIfAbsent(i, new ArrayList<>()));

    }


    @Override
    public boolean hasGraduated() {
        int coursesPassed = gradesByTerm.values()
                .stream().mapToInt(List::size)
                .sum();
        return coursesPassed == 18;
    }

    @Override
    public void addGrade(int term, String courseName, int grade) throws OperationNotAllowedException {
        validation(term);
        gradesByTerm.get(term).add(grade);
        Subject subject = new Subject(courseName);
        subjects.add(subject);

        //return hasGraduated();
    }


    @Override
    public String graduationLog() {
        return super.graduationLog() + " in 3 years.";
    }


}

class FourYearStudent extends Student {

    public FourYearStudent(String id) {
        super(id);
        IntStream.range(1, 9).forEach(i -> gradesByTerm.putIfAbsent(i, new ArrayList<>()));
    }

    @Override
    public boolean hasGraduated() {
        int coursesPassed = gradesByTerm.values()
                .stream().mapToInt(List::size).sum();

        return coursesPassed == 24;
    }

    @Override
    public void addGrade(int term, String courseName, int grade) throws OperationNotAllowedException {
        validation(term);
        gradesByTerm.get(term).add(grade);
        Subject subject = new Subject(courseName);
        subjects.add(subject);

    }

    @Override
    public String graduationLog() {
        return super.graduationLog() + " in 4 years.";
    }

}

class Subject {
    String name;
    List<Integer> grades;

    public Subject(String name) {
        this.name = name;
        this.grades = new ArrayList<>();
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }


    public int getListeners() {
        return grades.size();
    }

    public double getAvgGradeForCourse() {
        return grades.stream()
                .mapToDouble(g -> g).average().orElse(5.0);
    }

}

class Faculty {

    //    Map<Integer, List<Student>> studentsByYearOfStudies;
    Map<String, Student> studentsById;
    Map<String, Subject> subjectsByName;
    List<String> logs;

    public Faculty() {
        this.studentsById = new HashMap<>();
        this.subjectsByName = new HashMap<>();
        this.logs = new ArrayList<>();
    }

    void addStudent(String id, int yearsOfStudies) {
        studentsById.put(id, yearsOfStudies == 3 ? new ThreeYearStudent(id) : new FourYearStudent(id));
    }

    void addGradeToStudent(String studentId, int term, String courseName, int grade) throws OperationNotAllowedException {
//        try {
//            Student student = studentsById.get(studentId);
//            if (student.hasGraduated()) {
//                logs.add(student.graduationLog());
//                studentsById.remove(studentId);
//            }
//
//            student.addGrade(term, courseName, grade);
//            subjectsByName.get(courseName).grades.add(grade);
//        }catch (Exception e){
//
//        }

        if(studentsById.containsKey(studentId)){
            Student student = studentsById.get(studentId);
            student.addGrade(term, courseName, grade);
//            subjectsByName.get(courseName).grades.add(grade);
            subjectsByName.putIfAbsent(courseName, new Subject(courseName));
            subjectsByName.computeIfPresent(courseName, (k,v) -> {
                v.grades.add(grade);
                return v;
            });
            if(student.hasGraduated()){
                logs.add(student.graduationLog());
                studentsById.remove(studentId);
            }
        }



    }

    String getFacultyLogs() {
        StringBuilder sb = new StringBuilder();
        for (String log : logs) {
            sb.append(log).append("\n");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    String getDetailedReportForStudent(String id) {
        return studentsById.get(id).getDetailedReport();
    }

    void printFirstNStudents(int n) {
        Comparator<Student> comparator = Comparator.comparing(Student::countOfCoursesPassed)
                .thenComparing(Student::avgGrade).thenComparing(Student::getId).reversed();

        studentsById.values()
                .stream().sorted(comparator)
                .limit(n).forEach(s -> System.out.println(s.shortReport(s.id)));
    }

    void printCourses() {

        Comparator<Subject> comparator = Comparator.comparing(Subject::getListeners).thenComparing(Subject::getAvgGradeForCourse)
                .thenComparing(Subject::getName);
//        subjectsByName.values().stream().collect(Collectors.groupingBy(, TreeMap::new, Collectors.toCollection(TreeSet::new)))

        List<Subject> neewList = subjectsByName.values().stream().sorted(comparator).collect(Collectors.toList());
        neewList.forEach(c -> System.out.printf("%s %d %.2f\n", c.name, c.getListeners(), c.getAvgGradeForCourse()));


    }
}

public class FacultyTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();

        if (testCase == 1) {
            System.out.println("TESTING addStudent AND printFirstNStudents");
            Faculty faculty = new Faculty();
            for (int i = 0; i < 10; i++) {
                faculty.addStudent("student" + i, (i % 2 == 0) ? 3 : 4);
            }
            faculty.printFirstNStudents(10);

        } else if (testCase == 2) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            try {
                faculty.addGradeToStudent("123", 7, "NP", 10);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
            try {
                faculty.addGradeToStudent("1234", 9, "NP", 8);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        } else if (testCase == 3) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("123", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("1234", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else if (testCase == 4) {
            System.out.println("Testing addGrade for graduation");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            int counter = 1;
            for (int i = 1; i <= 6; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("123", i, "course" + counter, (i % 2 == 0) ? 7 : 8);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            counter = 1;
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("1234", i, "course" + counter, (j % 2 == 0) ? 7 : 10);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            System.out.println("LOGS");
            System.out.println(faculty.getFacultyLogs());
            System.out.println("PRINT STUDENTS (there shouldn't be anything after this line!");
            faculty.printFirstNStudents(2);
        } else if (testCase == 5 || testCase == 6 || testCase == 7) {
            System.out.println("Testing addGrade and printFirstNStudents (not graduated student)");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), i % 5 + 6);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            if (testCase == 5)
                faculty.printFirstNStudents(10);
            else if (testCase == 6)
                faculty.printFirstNStudents(3);
            else
                faculty.printFirstNStudents(20);
        } else if (testCase == 8 || testCase == 9) {
            System.out.println("TESTING DETAILED REPORT");
            Faculty faculty = new Faculty();
            faculty.addStudent("student1", ((testCase == 8) ? 3 : 4));
            int grade = 6;
            int counterCounter = 1;
            for (int i = 1; i < ((testCase == 8) ? 6 : 8); i++) {
                for (int j = 1; j < 3; j++) {
                    try {
                        faculty.addGradeToStudent("student1", i, "course" + counterCounter, grade);
                    } catch (OperationNotAllowedException e) {
                        e.printStackTrace();
                    }
                    grade++;
                    if (grade == 10)
                        grade = 5;
                    ++counterCounter;
                }
            }
            System.out.println(faculty.getDetailedReportForStudent("student1"));
        } else if (testCase == 10) {
            System.out.println("TESTING PRINT COURSES");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            faculty.printCourses();
        } else if (testCase == 11) {
            System.out.println("INTEGRATION TEST");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 2 : 3); k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }

            }

            for (int i = 11; i < 15; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= 3; k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            System.out.println("LOGS");
            System.out.println(faculty.getFacultyLogs());
            System.out.println("DETAILED REPORT FOR STUDENT");
            System.out.println(faculty.getDetailedReportForStudent("student2"));
            try {
                System.out.println(faculty.getDetailedReportForStudent("student11"));
                System.out.println("The graduated students should be deleted!!!");
            } catch (NullPointerException e) {
                System.out.println("The graduated students are really deleted");
            }
            System.out.println("FIRST N STUDENTS");
            faculty.printFirstNStudents(10);
            System.out.println("COURSES");
            faculty.printCourses();
        }
    }
}
