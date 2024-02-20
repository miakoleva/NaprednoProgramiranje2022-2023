package Exercices;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class TasksManagerTest {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        System.out.println("Tasks reading");
        manager.readTasks(System.in);
        System.out.println("By categories with priority");
        manager.printTasks(System.out, true, true);
        System.out.println("-------------------------");
        System.out.println("By categories without priority");
        manager.printTasks(System.out, false, true);
        System.out.println("-------------------------");
        System.out.println("All tasks without priority");
        manager.printTasks(System.out, false, false);
        System.out.println("-------------------------");
        System.out.println("All tasks with priority");
        manager.printTasks(System.out, true, false);
        System.out.println("-------------------------");

    }
}

abstract class Task{
    String name;
    String description;

    abstract Long getDeadline();
    abstract Integer getPriority();

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' + '}';
    }
}

class ExtendedTaskWithDeadline extends Task{
    LocalDateTime deadline;
    public ExtendedTaskWithDeadline(String name, String description, LocalDateTime deadline) {
        super(name, description);
        this.deadline = deadline;
    }

    @Override
    Long getDeadline() {
        return Math.abs(Duration.between(LocalDateTime.now().minusYears(3), deadline).getSeconds());
    }

    @Override
    Integer getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        return super.toString().replace("}", "") + ", " +
                "deadline=" + deadline +
                '}';
    }
}

class ExtendedTaskWithPriority extends Task{

    int priority;
    public ExtendedTaskWithPriority(String name, String description, int priority) {
        super(name, description);
        this.priority = priority;
    }

    @Override
    Long getDeadline() {
        return Long.MAX_VALUE;
    }

    @Override
    Integer getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return super.toString().replace("}", "")+ ", priority=" + priority +
                '}';
    }
}

class ComplicatedTask extends Task{
    LocalDateTime deadline;
    int priority;


    public ComplicatedTask(String name, String description, LocalDateTime deadline, int priority) {
        super(name, description);
        this.deadline = deadline;
        this.priority = priority;
    }

    @Override
    Long getDeadline() {
        return Math.abs(Duration.between(LocalDateTime.now().minusYears(3), deadline).getSeconds());
    }

    @Override
    Integer getPriority() {
        return  priority;
    }

    @Override
    public String toString() {
        return super.toString().replace("}", "") +
                ", deadline=" + deadline +
                ", priority=" + priority +
                '}';
    }
}

class TaskManager{
    Map<String, List<Task>> tasksByCategory;

    public TaskManager() {
        this.tasksByCategory = new HashMap<>();
    }

    void readTasks (InputStream inputStream){

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        br.lines().forEach(line -> {
            String category = FactoryTask.getCategory(line);
            tasksByCategory.putIfAbsent(category, new ArrayList<>());
            tasksByCategory.computeIfPresent(category, (k,v) -> {
                try {
                    v.add(FactoryTask.createTask(line));
                } catch (DeadlineNotValidException e) {
                    System.out.println(e.getMessage());
                }
                return v;
            });
        });
    }

    void printTasks(OutputStream os, boolean includePriority, boolean includeCategory){
        PrintWriter pw = new PrintWriter(os);

        Comparator<Task> comparatorPriorityAndDeadline = Comparator.comparing(Task::getPriority)
                .thenComparing(Task::getDeadline);

        Comparator<Task> comparatorDeadline = Comparator.comparing(Task::getDeadline);

        Comparator<Task> comparatorCategories = includePriority ? comparatorPriorityAndDeadline : comparatorDeadline;

        if(includeCategory){
            tasksByCategory.forEach((category, tasks) -> {
                pw.println(category.toUpperCase());
                tasks.stream()
                        .sorted(comparatorCategories)
                        .forEach(pw::println);
            });
        }
        else {
            tasksByCategory.values()
                    .stream().flatMap(Collection::stream)
                    .sorted(comparatorCategories)
                    .forEach(pw::println);


        }

        pw.flush();
    }




}

class FactoryTask{

    public static Task createTask(String line) throws DeadlineNotValidException {
        String []parts = line.split(",");

        String name = parts[1];
        String description = parts[2];
        Task task = new Task(name, description) {
            @Override
            Long getDeadline() {
                return Long.MAX_VALUE;
            }

            @Override
            Integer getPriority() {
                return Integer.MAX_VALUE;
            }
        };

        if(parts.length == 3)
            return task;
        else if(parts.length == 4){
            try {
                int priority = Integer.parseInt(parts[3]);
                return new ExtendedTaskWithPriority(name, description, priority);

            } catch (Exception e) {
                LocalDateTime deadline = LocalDateTime.parse(parts[3]);
                if (deadline.isBefore(LocalDateTime.now().minusYears(3)))
                    throw new DeadlineNotValidException(String.format("The deadline %s has already passed", deadline));
                return new ExtendedTaskWithDeadline(name, description, deadline);
            }
        }else {
            LocalDateTime deadline = LocalDateTime.parse(parts[3]);
            if (deadline.isBefore(LocalDateTime.now().minusYears(3)))
                throw new DeadlineNotValidException(String.format("The deadline %s has already passed", deadline));
            int priority = Integer.parseInt(parts[4]);

            return new ComplicatedTask(name, description, deadline, priority);
        }
    }

    public static  String getCategory(String line){
        String []parts = line.split(",");
        return parts[0];
    }
}

class DeadlineNotValidException extends Exception{
    public DeadlineNotValidException(String message) {
        super(message);
    }
}