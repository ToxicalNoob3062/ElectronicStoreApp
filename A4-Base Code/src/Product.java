import lombok.Getter;

//Base class for all products the store will sell
@Getter
public abstract class Product {
    private final double price;
    private int stockQuantity;
    private int soldQuantity;

    public Product(double initPrice, int initQuantity) {
        price = initPrice;
        stockQuantity = initQuantity;
    }

    //Returns the total revenue (price * amount) if there are at least amount items in stock
    //Return 0 otherwise (i.e., there is no sale completed)
    public double sellUnits(int amount) {
        if (amount > 0 && stockQuantity >= amount) {
            stockQuantity -= amount;
            soldQuantity += amount;
            return price * amount;
        }
        return 0.0;
    }

    //undo the sell operation
    public double undoSellOperation(int amount){
        if (amount <0) return 0.0;
        stockQuantity+=amount;
        soldQuantity-=amount;
        return price*amount;
    }

}