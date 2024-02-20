package Exercices;


import java.util.*;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class CourseTest {

    public static void printStudents(List<Student> students) {
        students.forEach(System.out::println);
    }

    public static void printMap(Map<Integer, Integer> map) {
        map.forEach((k, v) -> System.out.printf("%d -> %d%n", k, v));
    }

    public static void main(String[] args) {
        AdvancedProgrammingCourse advancedProgrammingCourse = new AdvancedProgrammingCourse();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            String command = parts[0];

            if (command.equals("addStudent")) {
                String id = parts[1];
                String name = parts[2];
                advancedProgrammingCourse.addStudent(new Student(id, name));
            } else if (command.equals("updateStudent")) {
                String idNumber = parts[1];
                String activity = parts[2];
                int points = Integer.parseInt(parts[3]);
                advancedProgrammingCourse.updateStudent(idNumber, activity, points);
            } else if (command.equals("getFirstNStudents")) {
                int n = Integer.parseInt(parts[1]);
                printStudents(advancedProgrammingCourse.getFirstNStudents(n));
            } else if (command.equals("getGradeDistribution")) {
                printMap(advancedProgrammingCourse.getGradeDistribution());
            } else {
                advancedProgrammingCourse.printStatistics();
            }
        }
    }
}

class Student{

    String id;
    String name;
    int midterm1Points;
    int midterm2Points;
    int labPoints;

//    public Student(String id, String name, int midterm1Points, int midterm2Points, int labPoints) {
//        this.id = id;
//        this.name = name;
//        this.midterm1Points = midterm1Points;
//        this.midterm2Points = midterm2Points;
//        this.labPoints = labPoints;
//    }

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
        this.midterm1Points = 0;
        this.midterm2Points = 0;
        this.labPoints = 0;
    }

    public int getMidterm1Points() {
        return midterm1Points;
    }

    public void setMidterm1Points(int midterm1Points) {
        this.midterm1Points = midterm1Points;
    }

    public int getMidterm2Points() {
        return midterm2Points;
    }

    public void setMidterm2Points(int midterm2Points) {
        this.midterm2Points = midterm2Points;
    }

    public int getLabPoints() {
        return labPoints;
    }

    public void setLabPoints(int labPoints) {
        this.labPoints = labPoints;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double summaryPoints(){
        return midterm1Points*0.45 + midterm2Points*0.45 + labPoints;
    }

    public int generateGrade(){

        double points = summaryPoints();

        if(points<50)
            return 5;
        else if(points>=50 && points<=60)
            return 6;
        else if(points>=60 && points<=70)
            return 7;
        else if(points>=70 && points<=80)
            return 8;
        else if(points>=80 && points<=90)
            return 9;
        else
            return 10;
    }

    @Override
    public String toString() {
        //ID: 151020 Name: Stefan First midterm: 78 Second midterm 80 Labs: 8 Summary points: 79.10 Grade: 8
        return String.format("ID: %s Name: %s First midterm: %d Second midterm %d Labs: %d Summary points: %.2f Grade: %d",
                id, name, midterm1Points, midterm2Points, labPoints, summaryPoints(), generateGrade());
    }

    public void setPoints(String activity, int points){
        if(activity.equals("midterm1"))
            setMidterm1Points(points);
        if(activity.equals("midterm2"))
            setMidterm2Points(points);
        else if(activity.equals ("labs"))
            setLabPoints(points);
    }

    public boolean hasPassed(){
        return generateGrade() > 5;
    }
}

class AdvancedProgrammingCourse{

    Map<Integer, Integer> numOfEachGrade;
    Map<String, Student> studentById;

    public AdvancedProgrammingCourse() {
        this.numOfEachGrade = new HashMap<> ();
        this.studentById = new HashMap<>();

        for (int i = 5; i<=10; i++){
            numOfEachGrade.put(i, 0);
        }
    }

    public void addStudent (Student s){
        String key = s.getId();
        int grade = s.generateGrade();

//        studentById.putIfAbsent(key, new Student(s.getId(), s.getName(), s.getMidterm1Points(), s.getMidterm2Points(), s.getLabPoints()));
        studentById.put(key, s);
        numOfEachGrade.computeIfPresent(grade, (k,v) -> {
            v++;
            return v;
        });
    }

    public void updateStudent (String idNumber, String activity, int points){

        if((activity.equals("midterm1") && points > 100) || (activity.equals("midterm2") && points > 100)
                || (activity.equals("labs") && points>10))
            throw new RuntimeException();

        int grade = studentById.get(idNumber).generateGrade();
        numOfEachGrade.computeIfPresent(grade, (k,v) -> {
            v--;
            return v;
        });

        studentById.get(idNumber).setPoints(activity, points);

        int newUpdateGrade = studentById.get(idNumber).generateGrade();
        numOfEachGrade.computeIfPresent(newUpdateGrade, (k,v) -> {
            v++;
            return v;
        });

    }

    public List<Student> getFirstNStudents (int n){
        Comparator<Student> comparator =
                Comparator.comparing(Student::summaryPoints).reversed();

        return studentById.values().stream().sorted(comparator)
                .limit(n).collect(Collectors.toList());
    }

    public Map<Integer,Integer> getGradeDistribution(){

        return numOfEachGrade;
    }

    public void printStatistics(){

        DoubleSummaryStatistics dss = studentById.values()
                .stream().filter(Student::hasPassed)
                .mapToDouble(Student::summaryPoints).summaryStatistics();

        //System.out.println(String.format("Count: %d Min: %.2f Average: %.2f Max: %.2f", dss.getCount(), dss.getMin(), dss.getAverage(), dss.getMax()));
        System.out.printf("Count: %d Min: %.2f Average: %.2f Max: %.2f", dss.getCount(), dss.getMin(), dss.getAverage(), dss.getMax());
    }

}