import lombok.Getter;

//Abstract class capturing shared state between Fridge and ToasterOven
@Getter
public abstract class Appliance extends Product {
    private final int wattage;
    private final String color;
    private final String brand;

    public Appliance(double initPrice, int initQuantity, int initWattage, String initColor, String initBrand) {
        super(initPrice, initQuantity);
        wattage = initWattage;
        color = initColor;
        brand = initBrand;
    }

}