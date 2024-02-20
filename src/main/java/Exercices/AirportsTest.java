package Exercices;

import java.util.*;

public class AirportsTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Airports airports = new Airports();
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] codes = new String[n];
        for (int i = 0; i < n; ++i) {
            String al = scanner.nextLine();
            String[] parts = al.split(";");
            airports.addAirport(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
            codes[i] = parts[2];
        }
        int nn = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < nn; ++i) {
            String fl = scanner.nextLine();
            String[] parts = fl.split(";");
            airports.addFlights(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        }
        int f = scanner.nextInt();
        int t = scanner.nextInt();
        String from = codes[f];
        String to = codes[t];
        System.out.printf("===== FLIGHTS FROM %S =====\n", from);
        airports.showFlightsFromAirport(from);
        System.out.printf("===== DIRECT FLIGHTS FROM %S TO %S =====\n", from, to);
        airports.showDirectFlightsFromTo(from, to);
        t += 5;
        t = t % n;
        to = codes[t];
        System.out.printf("===== DIRECT FLIGHTS TO %S =====\n", to);
        airports.showDirectFlightsTo(to);
    }
}

// vashiot kod ovde

class Airport{
    String name;
    String country;
    String code;
    int passengers;
    Map<String, TreeSet<Flight>> mapByFlights; //do odredena destinacija to moze da ima povekje letovi

    public Airport(String name, String country, String code, int passengers) {
        this.name = name;
        this.country = country;
        this.code = code;
        this.passengers = passengers;
        this.mapByFlights = new TreeMap<>();
    }

    public String getCode() {
        return code;
    }

    public String getCountry() {
        return country;
    }

    public boolean hasAFlight(String from){
        return Objects.equals(from, code);
    }

    public void addFlight(String to, Flight flight){
        mapByFlights.computeIfAbsent(to, (k) -> {
            TreeSet<Flight> set = new TreeSet<>();
            set.add(flight);
            return set;
        });

        mapByFlights.computeIfPresent(to, (k,v) -> {
            v.add(flight);
            return v;
        });
    }

    @Override
    public String toString() {
        return String.format("%s (%s)\n%s\n%d", name, code, country, passengers);
    }
}

class Flight implements Comparable<Flight>{
    String from;
    String to;
    int time;
    int duration;

    public Flight(String from, String to, int time, int duration) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.duration = duration;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getTime() {
        return time;
    }

    @Override
    public int compareTo(Flight o) {
        Comparator<Flight> comparator = Comparator.comparing(Flight::getTo).thenComparing(Flight::getTime);
        return comparator.compare(this, o);
    }

    String startTime(){
        int hours = time/60;
        int mins = time%60;

        return String.format("%02d:%02d", hours, mins);
    }

    String endTime(){
        int hours = (time+duration)/60;
        int mins = (time+duration)%60;

        if(hours>=24)
            hours-=24;

        return String.format("%02d:%02d", hours, mins);

    }

    String calculateDuration(){

        int startHour = time/60;
        int startMin = time%60;

        int endHour = (time+duration)/60;
        int endMin = (time+duration)%60;
        boolean day = false;

        if(endHour>=24){
            day = true;
        }

        int durationMin = endMin-startMin;
        if(durationMin < 0) {
            durationMin += 60;
            endHour--;
        }

        int durationHour = endHour - startHour;

        if(day)
            return String.format("+1d %dh%02dm", durationHour, durationMin);

        return String.format("%dh%02dm", durationHour, durationMin);
    }


    @Override
    public String toString() {
        return String.format("%s-%s %s-%s %s", from, to, startTime(), endTime(), calculateDuration());
    }




}

class Airports{

    Map<String, Airport> airportsByCode;
    List<Flight> allFlights;

    public Airports() {
        this.airportsByCode = new HashMap<>();
        this.allFlights = new ArrayList<>();
    }

    public void addAirport(String name, String country, String code, int passengers){
        Airport airport = new Airport(name, country, code, passengers);
        airportsByCode.put(code, airport);
    }

    public void addFlights(String from, String to, int time, int duration){

        Flight flight = new Flight(from, to, time, duration);
        allFlights.add(flight);

        airportsByCode.computeIfPresent(from, (k,v) -> {
            v.addFlight(to, flight);
            return v;
        });
    }

    public void showFlightsFromAirport(String code){

        Airport airport = airportsByCode.get(code);

        System.out.println(airport);
        int idx = 1;

        for(String to : airport.mapByFlights.keySet()){
            for (Flight flight : airport.mapByFlights.get(to)){
                System.out.printf("%d. %s\n", idx++, flight);
            }
        }

//        airport.mapByFlights.values()
//                .forEach(System.out::println);
    }


    public void showDirectFlightsFromTo(String from, String to){
        Airport airport = airportsByCode.get(from);

        if(!airport.mapByFlights.containsKey(to)) {
            System.out.printf("No flights from %s to %s\n", from, to);
            return;
        }

        airport.mapByFlights
                .get(to)
                .forEach(System.out::println);
    }

    public void showDirectFlightsTo(String to){

        TreeSet<Flight> newSet = new TreeSet<>();
        airportsByCode.values()
                .stream()
                .filter(each -> each.mapByFlights.get(to)!=null)
                .forEach(each -> each.mapByFlights.get(to)
                        .stream().forEach(flight -> newSet.add(flight)));

        newSet.forEach(System.out::println);
    }
}