package Exercices;
import java.util.*;
import java.util.stream.Collectors;

public class LogsTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LogCollector collector = new LogCollector();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.startsWith("addLog")) {
                collector.addLog(line.replace("addLog ", ""));
            } else if (line.startsWith("printServicesBySeverity")) {
                collector.printServicesBySeverity();
            } else if (line.startsWith("getSeverityDistribution")) {
                String[] parts = line.split("\\s+");
                String service = parts[1];
                String microservice = null;
                if (parts.length == 3) {
                    microservice = parts[2];
                }
                collector.getSeverityDistribution(service, microservice).forEach((k, v) -> System.out.printf("%d -> %d%n", k, v));
            } else if (line.startsWith("displayLogs")) {
                String[] parts = line.split("\\s+");
                String service = parts[1];
                String microservice = null;
                String order = null;
                if (parts.length == 4) {
                    microservice = parts[2];
                    order = parts[3];
                } else {
                    order = parts[2];
                }
                collector.displayLogs(service, microservice, order);
            }
        }
    }
}

enum ComparatorEnum {
    NEWEST_FIRST, OLDEST_FIRST, MOST_SEVERE_FIRST, LEAST_SEVERE_FIRST
}

//enum String {
//    INFO,
//    WARN,
//    ERROR
//}

class Log {
    //service_name microservice_name String message timestamp
    String sName;
    String mName;
    String type;
    String message;
    int timeStamp;

    public Log(String sName, String mName, String type, String message, int timeStamp) {
        this.sName = sName;
        this.mName = mName;
        this.type = type;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public int calculateSeverity() {
        int sum = 0;

        switch (this.type) {
            case "INFO":
                return sum;
            case "WARN":
                sum = 1;
                if (message.contains("might cause error"))
                    sum += 1;
                return sum;
            default:
                sum = 3;
                if (message.contains("fatal")) {
                    sum += 2;
                }
                if (message.contains("exception")) {
                    sum += 3;
                }
                return sum;
        }
    }

    @Override
    public String toString() {
        //Service name: service2 Count of microservices: 3
        // Total logs in service: 5 Average severity for all logs: 3.40 Average number of logs per microservice: 1.67

        return String.format("Service name: %s ", sName);
    }
}

class LogCollector {
    //Service,List<log>
    Map<String, List<Log>> logsByService;
    //Service, Microservice
    Map<String, String> microservicesByServices;
    List<Log> logs;

    public LogCollector() {
        this.logsByService = new HashMap<>();
        this.logs = new ArrayList<>();
    }

    void addLog(String log) {
        String[] parts = log.split("\\s+");
        String sName = parts[0];
        String mName = parts[1];
        String type = parts[2];

//        if (parts[2].equals("INFO")) {
//            String = String.INFO;
//        } else if (parts[2].equals("WARN")) {
//            String = String.WARN;
//        }else {
//            String = String.ERROR;
//        }

        StringBuilder sb = new StringBuilder();

        for (int i = 3; i < parts.length - 1; i++) {
            sb.append(parts[i]).append(" ");
        }

        String message = sb.toString();

        int timeStamp = Integer.parseInt(parts[parts.length - 1]);

        Log log1 = new Log(sName, mName, type, message, timeStamp);
        logs.add(log1);


        logsByService.putIfAbsent(sName, new ArrayList<>());
        logsByService.computeIfPresent(sName, (k, v) -> {
            v.add(log1);


            return v;
        });

    }

    Double getAvgSeverity() {
        int sumSeverity = logs.stream().mapToInt(Log::calculateSeverity).sum();
        return sumSeverity * 1.0 / logs.size();
    }

    //    String printReport(){
//
//    }
    void printServicesBySeverity() {
//        Comparator<Log> comparator = Comparator.comparing(t -> this.getAvgSeverity()).reversed();
        int sumSeverity = logs.stream().mapToInt(Log::calculateSeverity).sum();
        //Service name: service2 Count of microservices: 3 Total logs in service: 5
        // Average severity for all logs: 3.40 Average number of logs per microservice: 1.67
        Comparator<Log> comparator = Comparator.comparing(Log::calculateSeverity).reversed();
        logsByService.entrySet().stream()
//                .sorted((k,v)->v.getValue().stream().sorted(comparator))
                .forEach(entry -> {
                    int countOfMicroservices = (int) entry.getValue().stream().map(it -> it.mName).distinct().count();
                    int totalLogs = entry.getValue().size();
                    double averageSeverity = entry.getValue().stream().mapToDouble(Log::calculateSeverity).average().orElse(0.0);

                    double averageNumberPerMicroservice = totalLogs / (countOfMicroservices * 1.0);
                    System.out.println(String.format("Service name: %s Count of microservices: %d " +
                                    "Total logs in service: %d " +
                                    "Average severity for all logs: %.2f Average number of logs per microservice: %.2f",
                            entry.getKey(), countOfMicroservices, totalLogs, averageSeverity, averageNumberPerMicroservice));
                });


    }

    public Map<Integer, Integer> getSeverityDistribution(String service, String microservice) {
        return new TreeMap<>();

    }

    void displayLogs(String service, String microservice, String order) {
    }
}