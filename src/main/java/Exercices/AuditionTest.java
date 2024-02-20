package Exercices;

import java.util.*;

class Participant{

    String city;
    String code;
    String name;
    int age;

    public Participant(String city, String code, String name, int age) {
        this.city = city;
        this.code = code;
        this.name = name;
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return String.format("%s %s %d", code, name, age);
    }


    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null || getClass() != obj.getClass())
            return false;
        Participant p = (Participant) obj;

        return Objects.equals(code, p.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
class Audition{

    Map<String, Set<Participant>> participantsByCity;

    public Audition() {
        this.participantsByCity = new HashMap<>();
    }


    public void addParticpant(String city, String code, String name, int age) {

        Participant participant = new Participant(city, code, name, age);
        participantsByCity.putIfAbsent(participant.city, new HashSet<>());
        participantsByCity.computeIfPresent(participant.city, (k,v) -> {
            v.add(participant);
            return v;
        });
    }

    public void listByCity(String city) {

        Comparator<Participant> comparator = Comparator.comparing(Participant::getName)
                .thenComparing(Participant::getAge).thenComparing(Participant::getCode);

        participantsByCity.get(city).stream().sorted(comparator)
                .forEach(System.out::println);


    }
}
public class AuditionTest {
    public static void main(String[] args) {
        Audition audition = new Audition();
        List<String> cities = new ArrayList<String>();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            if (parts.length > 1) {
                audition.addParticpant(parts[0], parts[1], parts[2],
                        Integer.parseInt(parts[3]));
            } else {
                cities.add(line);
            }
        }
        for (String city : cities) {
            System.out.printf("+++++ %s +++++\n", city);
            audition.listByCity(city);
        }
        scanner.close();
    }
}