package Exercices;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

abstract class Employee implements Comparable<Employee>{
    String Id;
    String level;
    double rate;


    public Employee(String id, String level, double rate) {
        Id = id;
        this.level = level;
        this.rate = rate;
    }

    public String getId() {
        return Id;
    }

    public String getLevel() {
        return level;
    }

    public double getRate() {
        return rate;
    }

    abstract double calculateSalary();

    @Override
    public int compareTo(Employee o) {
        return Comparator.comparing(Employee::calculateSalary).reversed()
                .thenComparing(Employee::getLevel).compare(this, o);
    }

    @Override
    public String toString() {
        //Employee ID: 157f3d Level: level10 Salary: 2390.72 Regular hours: 40.00 Overtime hours: 23.14
        return String.format("Employee ID: %s Level: %s Salary: %.2f ", Id, level, calculateSalary());
    }
}

class HourlyEmployee extends Employee{

    double hours;
    double regularHours;
    double overtimeHours;


    public HourlyEmployee(String id, String level, double rate, double hours) {
        super(id, level, rate);

        this.hours = hours;
        this.overtimeHours = Math.max(hours-40,0);
        this.regularHours = hours - overtimeHours;
    }

    @Override
    double calculateSalary() {
        return regularHours*rate + overtimeHours*(rate*1.5);
    }

    @Override
    public String toString() {
        //Regular hours: 40.00 Overtime hours: 23.14
        return super.toString() + String.format("Regular hours: %.2f Overtime hours: %.2f", regularHours, overtimeHours);
    }
}

class FreelanceEmployee extends Employee{

    List<Integer> ticketPoints;

    public FreelanceEmployee(String id, String level, double rate, List<Integer> tickets) {
        super(id, level, rate);
        this.ticketPoints = new ArrayList<>();
        this.ticketPoints = tickets;
    }


    int calculateSumPoints(){
        return ticketPoints.stream().
                mapToInt(t -> t)
                .sum();
    }

    @Override
    double calculateSalary() {
        return calculateSumPoints() * rate;
    }


    @Override
    public String toString() {
        return super.toString() + String.format("Tickets count: %d Tickets points: %d", ticketPoints.size(), calculateSumPoints());
    }
}

class PayrollSystem{

    Map<String,Double> hourlyRateByLevel;
    Map<String,Double> ticketRateByLevel;
    List<Employee> employees;


    PayrollSystem(Map<String,Double> hourlyRateByLevel, Map<String,Double> ticketRateByLevel){
        this.hourlyRateByLevel = hourlyRateByLevel;
        this.ticketRateByLevel = ticketRateByLevel;
        this.employees = new ArrayList<>();
    }

    void readEmployees (InputStream is){

        Scanner scanner = new Scanner(is);
        while (scanner.hasNextLine()){
            String line = scanner.nextLine();

            String []parts = line.split(";");
            //String type = parts[0];
            String id = parts[1];
            String level = parts[2];

            if(parts[0].equals("H")){
                double hours = Double.parseDouble(parts[3]);
                double rate = hourlyRateByLevel.get(level);

                Employee employee = new HourlyEmployee(id, level, rate, hours);
                employees.add(employee);

            }else if (parts[0].equals("F")){

                List<Integer> ticketPoints = new ArrayList<>();
                double rate = ticketRateByLevel.get(level);
                for(int i = 3; i < parts.length; i++){
                    ticketPoints.add(Integer.parseInt(parts[i]));
                }

                Employee employee = new FreelanceEmployee(id, level, rate, ticketPoints);
                employees.add(employee);
            }


        }

    }


//    void readEmployees (InputStream is){
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(is));
//        employees = br.lines().
//                map(line -> FactoryEmployee.factoryEmployee(line, hourlyRateByLevel, ticketRateByLevel))
//                .collect(Collectors.toList());
//
//    }


    Map<String, Set<Employee>> printEmployeesByLevels (OutputStream os, Set<String> levels){
        PrintWriter pw = new PrintWriter(os);
        //level  set od vraboteni na tj level
        Map<String, Set<Employee>> result = employees.stream()
                .collect(Collectors.
                        groupingBy(Employee::getLevel,
                                TreeMap::new,
                                Collectors.toCollection(TreeSet::new)));

        Set<String> keys = new HashSet<>(result.keySet());

        keys.stream()
                .filter(k -> !levels.contains(k))
                .forEach(result::remove);

        return result;
    }

}

//class FactoryEmployee{
//
//    public static Employee factoryEmployee(String line, Map<String,Double> hourlyRateByLevel, Map<String,Double> ticketRateByLevel){
//
//        String []parts = line.split(";");
//        String id = parts[1];
//        String level = parts[2];
//
//        if(parts[0].equals("H")){
//            double hours = Double.parseDouble(parts[3]);
//            double rate = hourlyRateByLevel.get(level);
//
//            return new HourlyEmployee(id, level, rate, hours);
//        }else{
//            double rate = ticketRateByLevel.get(level);
//            List<Integer> ticketPoints = new ArrayList<>();
//            for(int i = 3 ; i< parts.length; i++){
//                ticketPoints.add(Integer.parseInt(parts[i]));
//            }
//
//            return new FreelanceEmployee(id, level, rate, ticketPoints);
//        }
//    }
//}


public class PayrollSystemTest {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 10 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5 + i * 2.5);
        }

        PayrollSystem payrollSystem = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);

        System.out.println("READING OF THE EMPLOYEES DATA");
        payrollSystem.readEmployees(System.in);

        System.out.println("PRINTING EMPLOYEES BY LEVEL");
        Set<String> levels = new LinkedHashSet<>();
        for (int i=5;i<=10;i++) {
            levels.add("level"+i);
        }
        Map<String, Set<Employee>> result = payrollSystem.printEmployeesByLevels(System.out, levels);
        result.forEach((level, employees) -> {
            System.out.println("LEVEL: "+ level);
            System.out.println("Employees: ");
            employees.forEach(System.out::println);
            System.out.println("------------");
        });


    }
}
