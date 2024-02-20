package Exercices;

import java.util.*;
import java.util.stream.Collectors;

public class LabExercisesTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LabExercises labExercises = new LabExercises();
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            String index = parts[0];
            List<Integer> points = Arrays.stream(parts).skip(1)
                    .mapToInt(Integer::parseInt)
                    .boxed()
                    .collect(Collectors.toList());

            labExercises.addStudent(new Student(index, points));
        }

        System.out.println("===printByAveragePoints (ascending)===");
        labExercises.printByAveragePoints(true, 100);
        System.out.println("===printByAveragePoints (descending)===");
        labExercises.printByAveragePoints(false, 100);
        System.out.println("===failed students===");
        labExercises.failedStudents().forEach(System.out::println);
        System.out.println("===statistics by year");
        labExercises.getStatisticsByYear().entrySet().stream()
                .map(entry -> String.format("%d : %.2f", entry.getKey(), entry.getValue()))
                .forEach(System.out::println);

    }
}

class Student {
    String id;
    List<Integer> labPoints;

    public Student(String id, List<Integer> labPoints) {
        this.id = id;
        this.labPoints = labPoints;
    }

    public double getAvg() {
        int sum = labPoints.stream().mapToInt(p -> p).sum();
        return sum / 10.0;
    }

    public String getId() {
        return id;
    }

    public boolean hasSignature() {
        return labPoints.size() >= 8;
    }

    @Override
    public String toString() {
        return String.format("%s %s %.2f", id, hasSignature() ? "YES" : "NO", getAvg());
    }

    public Integer getYearOfStudies() {
        int y = Integer.parseInt(id.substring(0, 2));

        return 20 - y;
    }

    public double getSum() {
        return labPoints.stream().mapToInt(p -> p).sum();
    }

}

class LabExercises {
    Map<String, Student> studentsById;
    List<Student> studentList;

    public LabExercises() {
        this.studentsById = new HashMap<>();
        this.studentList = new ArrayList<>();
    }

    public void addStudent(Student student) {
        studentsById.put(student.getId(), student);
        studentList.add(student);
    }

    public void printByAveragePoints(boolean ascending, int n) {

        Comparator<Student> comparator = Comparator.comparing(Student::getAvg)
                .thenComparing(Student::getId);

        if (!ascending)
            comparator = comparator.reversed();

        studentList.stream().sorted(comparator)
                .limit(n).forEach(s -> System.out.println(s.toString()));

    }

    public List<Student> failedStudents() {
        Comparator<Student> comparator = Comparator.comparing(Student::getId).thenComparing(Student::getAvg);

        return studentList.stream().filter(s -> !s.hasSignature()).sorted(comparator)
                .collect(Collectors.toList());
    }

    public Map<Integer, Double> getStatisticsByYear() {

        Map<Integer, Double> result =
                studentsById.values().stream().filter(s -> s.hasSignature())
                        .collect(Collectors.groupingBy(Student::getYearOfStudies,
                                HashMap::new, Collectors.averagingDouble(Student::getAvg)));

        return result;
    }
}