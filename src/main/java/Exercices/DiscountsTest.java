package Exercices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

class Store{
    String name;
    Map<Integer, Integer> discountAndOriginalPrice;


    public Store(String line){
        String []parts = line.split("\\s+");
        this.name = parts[0];
        this.discountAndOriginalPrice = new TreeMap<>();
        for (int i = 1; i< parts.length; i++){
            String []priceParts = parts[i].split(":");
            Integer discountPrice = Integer.parseInt(priceParts[0]);
            Integer originalPrice = Integer.parseInt(priceParts[1]);

            discountAndOriginalPrice.put(originalPrice,discountPrice);
        }
    }

    double getDiscountPercent(int original, int discount){
        return Math.floor(100.0 - (100.0/original*discount));
    }

    public double avgDiscount(){

        return discountAndOriginalPrice.entrySet().stream()
                .mapToDouble(entry -> getDiscountPercent(entry.getKey(), entry.getValue()))
                .average().orElse(0.0);
    }

    int getDiscount(int original, int discount){
        return original-discount;
    }

    int totalDiscount(){

        return discountAndOriginalPrice.entrySet()
                .stream().mapToInt(entry -> getDiscount(entry.getKey(), entry.getValue()))
                .sum();
    }

    @Override
    public String toString() {
        Comparator<Map.Entry<Integer, Integer>> comparator = (first, second) ->{
            double p1 = getDiscountPercent(first.getKey(), first.getValue());
            double p2 = getDiscountPercent(second.getKey(), second.getValue());

            int res = Double.compare(p1, p2);

            if(res == 0){
                int y = second.getKey().compareTo(first.getKey());

                if(y == 0)
                    return second.getValue().compareTo(first.getValue());
                else return y;
            }

            return -res;
        };


        return String.format("%s\nAverage discount: %.1f%%\nTotal discount: %d\n" + "%s", name, avgDiscount(), totalDiscount(),
                discountAndOriginalPrice.entrySet().stream()
                        .sorted(comparator).map(entry -> String.format("%2.0f%% %d/%d",
                                getDiscountPercent(entry.getKey(), entry.getValue()), entry.getValue(), entry.getKey()))
                        .collect(Collectors.joining("\n")));

    }

    public String getName() {
        return name;
    }
}
class Discounts{

    List<Store> stores;

    public Discounts() {
        this.stores = new ArrayList<>();
    }

    public int readStores(InputStream inputStream) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line= br.readLine()) != null){
            stores.add(new Store(line));
        }

        return stores.size();
    }

    public List<Store> byAverageDiscount(){
        Comparator<Store> comparator = Comparator.comparing(Store::avgDiscount)
                .thenComparing(Store::getName);

        return stores.stream().
                sorted(comparator.reversed()).
                limit(3).collect(Collectors.toList());
    }

    public List<Store> byTotalDiscount(){
        Comparator<Store> comparator = Comparator.comparing(Store::totalDiscount).thenComparing(Store::getName);

        return stores.stream().sorted(comparator).limit(3).collect(Collectors.toList());
    }
}
public class DiscountsTest {
    public static void main(String[] args) throws IOException {
        Discounts discounts = new Discounts();
        int stores = discounts.readStores(System.in);
        System.out.println("Stores read: " + stores);
        System.out.println("=== By average discount ===");
        discounts.byAverageDiscount().forEach(System.out::println);
        System.out.println("=== By total discount ===");
        discounts.byTotalDiscount().forEach(System.out::println);
    }
}



// Vashiot kod ovde