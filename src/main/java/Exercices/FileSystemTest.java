package Exercices;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Partial exam II 2016/2017
 */
public class FileSystemTest {
    public static void main(String[] args) {
        FileSystem fileSystem = new FileSystem();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; i++) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            fileSystem.addFile(parts[0].charAt(0), parts[1],
                    Integer.parseInt(parts[2]),
                    LocalDateTime.of(2016, 12, 29, 0, 0, 0).minusDays(Integer.parseInt(parts[3]))
            );
        }
        int action = scanner.nextInt();
        if (action == 0) {
            scanner.nextLine();
            int size = scanner.nextInt();
            System.out.println("== Find all hidden files with size less then " + size);
            List<File> files = fileSystem.findAllHiddenFilesWithSizeLessThen(size);
            files.forEach(System.out::println);
        } else if (action == 1) {
            scanner.nextLine();
            String[] parts = scanner.nextLine().split(":");
            System.out.println("== Total size of files from folders: " + Arrays.toString(parts));
            int totalSize = fileSystem.totalSizeOfFilesFromFolders(Arrays.stream(parts)
                    .map(s -> s.charAt(0))
                    .collect(Collectors.toList()));
            System.out.println(totalSize);
        } else if (action == 2) {
            System.out.println("== Files by year");
            Map<Integer, Set<File>> byYear = fileSystem.byYear();
            byYear.keySet().stream().sorted()
                    .forEach(key -> {
                        System.out.printf("Year: %d\n", key);
                        Set<File> files = byYear.get(key);
                        files.stream()
                                .sorted()
                                .forEach(System.out::println);
                    });
        } else if (action == 3) {
            System.out.println("== Size by month and day");
            Map<String, Long> byMonthAndDay = fileSystem.sizeByMonthAndDay();
            byMonthAndDay.keySet().stream().sorted()
                    .forEach(key -> System.out.printf("%s -> %d\n", key, byMonthAndDay.get(key)));
        }
        scanner.close();
    }
}

class File implements Comparable<File>{
    String name;
    int size;
    LocalDateTime createdAt;

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public File(String name, int size, LocalDateTime createdAt) {
        this.name = name;
        this.size = size;
        this.createdAt = createdAt;
    }

    @Override
    public int compareTo(File o) {
        Comparator<File> comparator = Comparator.comparing(File::getCreatedAt)
                .thenComparing(File::getName)
                .thenComparing(File::getSize);
        return comparator.compare(this, o);
    }

    public boolean hiddenWithSizeLessThan(int size1){
        return this.name.startsWith(".") && this.size<size1;
    }

    public String byMonthAndDay(){
        return String.format("%s-%s", createdAt.getMonth(), createdAt.getDayOfMonth());
    }

    @Override
    public String toString() {
        return String.format("%-10s %5dB %s", name, size, createdAt.toString());
    }
}

class FileSystem {

    Map<Character, TreeSet<File>> filesByFolder;
    List<File> allFiles;

    public FileSystem() {
        this.filesByFolder = new HashMap<>();
        this.allFiles = new ArrayList<>();
    }

    public void addFile(char folder, String name, int size, LocalDateTime createdAt) {
        File file = new File(name, size, createdAt);
        allFiles.add(file);
        filesByFolder.putIfAbsent(folder, new TreeSet<>());
        filesByFolder.computeIfPresent(folder, (k, v) -> {
            v.add(file);
            return v;
        });

    }

    public List<File> findAllHiddenFilesWithSizeLessThen(int size) {
        List<File> result = allFiles;
        return result.stream()
                .filter(f -> f.hiddenWithSizeLessThan(size)).sorted()
                .collect(Collectors.toList());
    }

    public int totalSizeOfFilesFromFolders(List<Character> folders) {

        return folders.stream()
                .flatMapToInt(folder -> filesByFolder.get(folder).stream()
                        .mapToInt(File::getSize)).sum();

    }

//    Map<String, Set<Employee>> printEmployeesByLevels (OutputStream os, Set<String> levels){
//        PrintWriter pw = new PrintWriter(os);
//        //level  set od vraboteni na tj level
//        Map<String, Set<Employee>> result = employees.stream()
//                .collect(Collectors.
//                        groupingBy(Employee::getLevel,
//                                TreeMap::new,
//                                Collectors.toCollection(TreeSet::new)));
//
//        Set<String> keys = new HashSet<>(result.keySet());
//
//        keys.stream()
//                .filter(k -> !levels.contains(k))
//                .forEach(result::remove);
//
//        return result;
//    }

    public Map<Integer, Set<File>> byYear() {

        return allFiles.stream()
                .collect(Collectors
                        .groupingBy(f -> f.createdAt.getYear(),//key
                                Collectors.toCollection(TreeSet::new)));//value

    }

    public Map<String, Long> sizeByMonthAndDay(){

        return allFiles.stream()
                .collect(Collectors
                        .groupingBy(File::byMonthAndDay,//key
                                TreeMap::new,//into a new map
                                Collectors.summingLong(File::getSize)));//value
    }


}