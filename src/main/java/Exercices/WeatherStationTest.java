package Exercices;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WeatherStationTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        int n = scanner.nextInt();
        scanner.nextLine();
        WeatherStation ws = new WeatherStation(n);
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("=====")) {
                break;
            }
            String[] parts = line.split(" ");
            float temp = Float.parseFloat(parts[0]);
            float wind = Float.parseFloat(parts[1]);
            float hum = Float.parseFloat(parts[2]);
            float vis = Float.parseFloat(parts[3]);
            line = scanner.nextLine();
            Date date = df.parse(line);
            ws.addMeasurment(temp, wind, hum, vis, date);
        }
        String line = scanner.nextLine();
        Date from = df.parse(line);
        line = scanner.nextLine();
        Date to = df.parse(line);
        scanner.close();
        System.out.println(ws.total());
        try {
            ws.status(from, to);
        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }
}

class Measure {

    float temperature;
    float humidity;
    float wind;
    float visibility;
    Date date;

    public Measure(float temperature, float humidity, float wind, float visibility, Date date) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.wind = wind;
        this.visibility = visibility;
        this.date = date;
    }

    @Override
    public String toString() {
        //41.8 9.4 km/h 40.8% 20.7 km Tue Dec 17 23:35:15 GMT 2013
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT' yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return String.format("%.1f %.1f km/h %.1f%% %.1f km %s", temperature, humidity, wind, visibility, dateFormat.format(date));
    }

    public Date getDate() {
        return date;
    }

    public float getTemperature() {
        return temperature;
    }
}

class WeatherStation {

    int x;
    List<Measure> measures;

    public WeatherStation(int n) {
        this.measures = new ArrayList<>();
        this.x = n;
    }


    public void addMeasurment(float temp, float wind, float hum, float vis, Date date) {

//        measures.add(new Measure(temp,wind,hum,vis,date));
        List<Measure> measureListForDelete = new ArrayList<>();
        measures.forEach(mes -> {
            long days = TimeUnit.MILLISECONDS.toDays(date.getTime() - mes.date.getTime());
            if (days >= x) {
                measureListForDelete.add(mes);
            }
        });

        measures.removeAll(measureListForDelete);

        List<Measure> newList = new ArrayList<>();
        newList = measures.stream().filter(measure -> {
            double minutes = TimeUnit.MILLISECONDS.toMinutes(date.getTime() - measure.date.getTime());
            return minutes <= 2.5;
        }).collect(Collectors.toList());

        if (newList.isEmpty()){
            measures.add(new Measure(temp,wind,hum,vis,date));
        }
    }

    public int total() {
        return measures.size();
    }

    public void status(Date from, Date to) {
        Comparator<Measure> mesComp = Comparator.comparing(Measure::getDate);
        List<Measure> newList = new ArrayList<>();
        this.measures.stream().sorted(mesComp).forEach(mes -> {
            if ((mes.date.after(from) && mes.date.before(to)) || (mes.date.equals(from) || mes.date.equals(to))) {
                newList.add(mes);
            }
        });

        DoubleSummaryStatistics dss = newList.stream().mapToDouble(Measure::getTemperature).summaryStatistics();

        if (newList.isEmpty()){
            throw new RuntimeException();
        }else {
            newList.forEach(measure -> System.out.println(measure.toString()));
            System.out.printf("Average temperature: %.2f", dss.getAverage());

        }
    }
}

// vashiot kod ovde