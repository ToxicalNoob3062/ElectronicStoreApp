//Class representing an electronic store
//Has an array of products that represent the items the store can sell

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

public class ElectronicStore {
    public final int MAX_PRODUCTS = 10; //Maximum number of products the store can have

    @Getter
    private int curProducts;
    @Getter
    private final String name;
    @Getter
    private final Product[] stock; //Array to hold all products

    @Getter
    @Setter
    private double revenue;

    public ElectronicStore(String initName) {
        revenue = 0.0;
        name = initName;
        stock = new Product[MAX_PRODUCTS];
        curProducts = 0;
    }

    //Adds a product and returns true if there is space in the array
    //Returns false otherwise
    public void addProduct(Product newProduct) {
        if (curProducts < MAX_PRODUCTS) {
            stock[curProducts] = newProduct;
            curProducts++;
        }
    }

    public double removeFromStock(String productName) {
        // Search for the product in the stock list by name
        for (Product product : stock) {
            if (product != null && product.toString().equals(productName)) {
                // Calculate the total price of the sold units
                return product.sellUnits(1);
            }
        }
        // If the product is not found, return 0.0
        return 0.0;
    }

    public double addToStock(String productName) {
        // Search for the product in the stock list by name
        for (Product product : stock) {
            if (product != null && product.toString().equals(productName)) {
                // Calculate the total price of the units to be restored
                return product.undoSellOperation(1);
            }
        }
        // If the product is not found, return 0.0
        return 0.0;
    }


    public static ElectronicStore createStore() {
        ElectronicStore store1 = new ElectronicStore("Watts Up Electronics");
        Desktop d1 = new Desktop(100, 10, 3.0, 16, false, 250, "Compact");
        Desktop d2 = new Desktop(200, 10, 4.0, 32, true, 500, "Server");
        Laptop l1 = new Laptop(150, 10, 2.5, 16, true, 250, 15);
        Laptop l2 = new Laptop(250, 10, 3.5, 24, true, 500, 16);
        Fridge f1 = new Fridge(500, 10, 250, "White", "Sub Zero", false);
        Fridge f2 = new Fridge(750, 10, 125, "Stainless Steel", "Sub Zero", true);
        ToasterOven t1 = new ToasterOven(25, 10, 50, "Black", "Danby", false);
        ToasterOven t2 = new ToasterOven(75, 10, 50, "Silver", "Toasty", true);
        store1.addProduct(d1);
        store1.addProduct(d2);
        store1.addProduct(l1);
        store1.addProduct(l2);
        store1.addProduct(f1);
        store1.addProduct(f2);
        store1.addProduct(t1);
        store1.addProduct(t2);
        return store1;
    }
}