package Exercices;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ShoppingTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ShoppingCart cart = new ShoppingCart();

        int items = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < items; i++) {
            try {
                cart.addItem(sc.nextLine());
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        }

        List<Integer> discountItems = new ArrayList<>();
        int discountItemsCount = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < discountItemsCount; i++) {
            discountItems.add(Integer.parseInt(sc.nextLine()));
        }

        int testCase = Integer.parseInt(sc.nextLine());
        if (testCase == 1) {
            cart.printShoppingCart(System.out);
        } else if (testCase == 2) {
            try {
                cart.blackFridayOffer(discountItems, System.out);
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Invalid test case");
        }
    }
}


class ShoppingCart{
    List<Item> items;

    public ShoppingCart() {
        this.items = new ArrayList<>();
    }

    void addItem(String itemData) throws InvalidOperationException {

        String[] parts = itemData.split(";");
        String type = parts[0];
        int id = Integer.parseInt(parts[1]);
        String name = parts[2];
        int price = Integer.parseInt(parts[3]);

        if(type.equals("WS")){
            int quantity = Integer.parseInt(parts[4]);
            if(quantity == 0)
                throw new InvalidOperationException(String.format("The quantity of the product with id %s can not be 0.", id));
            Item item = new WSItem(type, id, name, price, quantity);
            items.add(item);
        }else {
            double quantity = Double.parseDouble(parts[4]);
            if(quantity == 0)
                throw new InvalidOperationException(String.format("The quantity of the product with id %s can not be 0.", id));
            Item item = new PSItem(type, id, name, price, quantity);
            items.add(item);
        }
    }

    void printShoppingCart(OutputStream os){
        PrintWriter pw = new PrintWriter(os);
        Comparator<Item> comparator =
                Comparator.comparing(Item::calculatePrice).reversed();

        items.stream().sorted(comparator)
                .forEach(i -> pw.println(i.toString()));

        pw.flush();
    }

    void blackFridayOffer(List<Integer> discountItems, OutputStream os) throws InvalidOperationException {

        if(discountItems.isEmpty())
            throw new InvalidOperationException("There are no products with discount.");
        PrintWriter pw = new PrintWriter(os);

        List<Item> discountItemsList =  items.stream().filter(item -> discountItems.contains(item.id)).collect(Collectors.toList());
        discountItemsList.forEach(i -> {
            double zasteda = i.calculatePrice() - i.discountPrice();
            pw.printf("%d - %.2f\n", i.id, zasteda);
        });

        pw.flush();


    }

}

abstract class Item{

    String type;
    int id;
    String name;
    int price;


    public Item(String type, int id, String name, int price) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.price = price;
    }

    abstract double calculatePrice();

    @Override
    public String toString() {
        return String.format("%d - %.2f", id, calculatePrice());
    }
    abstract double discountPrice();
}

class WSItem extends Item{

    int quantity;

    public WSItem(String type, int id, String name, int price, int quantity) {
        super(type, id, name, price);
        this.quantity = quantity;
    }

    @Override
    double calculatePrice() {
        return price*quantity;
    }

    @Override
    double discountPrice() {
        return calculatePrice()*0.9;
    }
}

class PSItem extends Item{
    double quantity;


    public PSItem(String type, int id, String name, int price, double quantity) {
        super(type, id, name, price);
        this.quantity = quantity;
    }

    @Override
    double calculatePrice() {
        return (quantity/1000)*price;
    }

    @Override
    double discountPrice() {
        return calculatePrice()*0.9;
    }
}

class InvalidOperationException extends Exception{
    public InvalidOperationException(String message) {
        super(message);
    }
}