import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import java.util.*;

@Getter
public class ElectronicStoreView extends HBox {

    // List Views
    private final ListView<String> stockList, cart, leaderboard;

    // Buttons
    private final Button add, remove, punch, reset;

    // Text Fields
    @Setter private TextField saleTextField = new TextField("0");
    @Setter private TextField revenueTextField;
    @Setter private TextField perNumberTextField;

    // Label
    private final Label cartLabel;

    public ElectronicStoreView() {
        // Set screen size
        setPrefSize(800, 400);

        // Initialize list views
        stockList = new ListView<>(); stockList.setPrefSize(300, 320);
        cart = new ListView<>(); cart.setPrefSize(300, 320);
        leaderboard = new ListView<>(); leaderboard.setPrefSize(200, 160);

        // Initialize buttons
        add = new Button("Add"); add.setPrefSize(120, 40);
        remove = new Button("Remove"); remove.setPrefSize(120, 40);
        punch = new Button("Punch"); punch.setPrefSize(120, 40);
        reset = new Button("Reset"); reset.setPrefSize(120, 40);

        // Initialize text fields
        revenueTextField = new TextField("0.00");
        perNumberTextField = new TextField("N/A");

        // Initialize label
        cartLabel = new Label("Cart Stock: ($0.00):");

        // Add components to HBox
        setAlignment(Pos.CENTER);
        getChildren().addAll(createLeftPart(), createMiddlePart(), createRightPart());
    }

    // Method to update view based on model changes
    public void update(ElectronicStore stockModel) {
        displayModel(stockModel.getStock(), stockModel.getCurProducts(), stockList,false);
    }

    // Method to parse list entry string
    public static String parseListEntry(String entry) {
        String[] parts = entry.split("x");
        return parts[1].trim();
    }

    // Method to parse list entry to integer
    public static Integer parseListEntryToInt(String entry) {
        String[] parts = entry.split("x");
        return Integer.parseInt(parts[0].trim());
    }

    // Method to set value for 'Per Number' text field
    public void setPerNumberTextField() {
        int sale = Integer.parseInt(saleTextField.getText());
        double revenue = Double.parseDouble(revenueTextField.getText());
        String perNumberText = (sale != 0) ? String.format("%.2f", (revenue / sale)) : "N/A";
        perNumberTextField.setText(perNumberText);
    }

    // Method to increment sale count
    public void incrementSale() {
        int currentSale = Integer.parseInt(saleTextField.getText());
        currentSale++;
        saleTextField.setText(Integer.toString(currentSale));
    }

    // Method to update revenue
    public void setRevenue() {
        double revenue = extractCartVal() + Double.parseDouble(revenueTextField.getText());
        revenueTextField.setText(String.format("%.2f", revenue));
    }

    // Method to add entry to cart
    public void pushEntryToCart(String entry) {
        for (String existingEntry : cart.getItems()) {
            if (parseListEntry(existingEntry).equals(entry)) {
                int quantity = Integer.parseInt(existingEntry.split(" x ")[0]);
                quantity++;
                cart.getItems().set(cart.getItems().indexOf(existingEntry), quantity + " x " + entry);
                return;
            }
        }
        cart.getItems().add("1 x " + entry);
    }

    // Method to set top 3 products in leaderboard
    public void setTop3Products(Product[] products) {
        Product[] filteredProducts = Arrays.stream(products)
                .filter(Objects::nonNull)
                .toArray(Product[]::new);
        Arrays.sort(filteredProducts, Comparator.comparingInt(Product::getSoldQuantity).reversed());
        int length = Math.min(3, filteredProducts.length);
        Product[] top3 = new Product[length];
        System.arraycopy(filteredProducts, 0, top3, 0, length);
        displayModel(top3, top3.length, leaderboard,true);
    }

    // Method to display model in list view
    private void displayModel(Product[] products, int endsAt, ListView<String> list,boolean specialCase) {
        ObservableList<String> items = list.getItems();
        int lastIndexUpdated = 0;
        for (int i = 0; i < endsAt; i++) {
            Product product = products[i];
            int stockQuantity = product.getStockQuantity();
            if (stockQuantity > 0 || specialCase) {
                String productName = product.toString();
                if (lastIndexUpdated < items.size()) {
                    String existingItem = items.get(lastIndexUpdated);
                    if (!productName.equals(existingItem)) {
                        list.getItems().set(lastIndexUpdated, productName);
                    }
                } else {
                    list.getItems().add(productName);
                }
                lastIndexUpdated++;
            }
        }
        list.getItems().remove(lastIndexUpdated, items.size());
    }

    // Method to extract cart value
    public double extractCartVal() {
        String labelText = cartLabel.getText();
        String numericPart = labelText.substring(labelText.indexOf("$") + 1).replaceAll("[^\\d.]", "");
        return Double.parseDouble(numericPart);
    }

    // Method to set cart value
    public void setCartValue(double value, boolean direction) {
        String labelText = cartLabel.getText();
        String numericPart = labelText.substring(labelText.indexOf("$") + 1).replaceAll("[^\\d.]", "");
        double currentTotal = Double.parseDouble(numericPart);
        if (direction) {
            currentTotal += value;
        } else {
            currentTotal -= value;
        }
        String formattedTotal = (currentTotal == 0.0) ? "Cart Stock: ($0.00)" : String.format("Cart Stock: ($%.2f)", currentTotal);
        cartLabel.setText(formattedTotal);
    }

    // Create middle part of the UI
    private VBox createMiddlePart() {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(5));
        VBox.setMargin(add, new Insets(5, 0, 0, 0));
        Label label = new Label("Store Stock:");
        label.setPadding(new Insets(0, 0, 5, 0));
        box.getChildren().addAll(label, stockList, add);
        return box;
    }

    // Create left part of the UI
    private VBox createLeftPart() {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(5));
        box.setPrefSize(300, 320);
        VBox.setMargin(reset, new Insets(5, 0, 0, 0));
        box.getChildren().addAll(createStoreSummary(), createLeaderboard(), reset);
        return box;
    }

    // Create right part of the UI
    private VBox createRightPart() {
        VBox box = new VBox();
        HBox buttons = new HBox();
        box.setPadding(new Insets(5));
        box.setAlignment(Pos.CENTER);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        VBox.setMargin(buttons, new Insets(5, 0, 0, 0));
        buttons.getChildren().addAll(remove, punch);
        cartLabel.setPadding(new Insets(0, 0, 5, 0));
        box.getChildren().addAll(cartLabel, cart, buttons);
        return box;
    }

    // Create store summary section
    private VBox createStoreSummary() {
        VBox box = new VBox(10);
        box.setPrefSize(200, 160);
        box.setAlignment(Pos.CENTER);
        Label titleLabel = new Label("Store Summary:");
        box.getChildren().add(titleLabel);
        GridPane gridPane = getSummary();
        gridPane.setAlignment(Pos.CENTER);
        box.getChildren().addAll(gridPane);
        for (int i = 0; i < box.getChildren().size(); i++) {
            VBox.setMargin(box.getChildren().get(i), new Insets(0, 0, 10, 0));
        }
        return box;
    }

    // Create grid pane for store summary details
    private GridPane getSummary() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        Label saleLabel = new Label("#Sale:");
        setupSummaryTextField(saleTextField);
        gridPane.addRow(0, saleLabel, saleTextField);
        GridPane.setHalignment(saleLabel, HPos.RIGHT);
        Label revenueLabel = new Label("$Revenue:");
        setupSummaryTextField(revenueTextField);
        gridPane.addRow(1, revenueLabel, revenueTextField);
        GridPane.setHalignment(revenueLabel, HPos.RIGHT);
        Label perNumberLabel = new Label("$/#:");
        setupSummaryTextField(perNumberTextField);
        gridPane.addRow(2, perNumberLabel, perNumberTextField);
        GridPane.setHalignment(perNumberLabel, HPos.RIGHT);
        return gridPane;
    }

    // Setup text field for store summary
    private void setupSummaryTextField(TextField textField) {
        textField.setEditable(false);
        textField.setPrefWidth(80);
    }

    // Create leaderboard section
    private VBox createLeaderboard() {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        Label label = new Label("Leaderboard:");
        label.setPadding(new Insets(0, 0, 5, 0));
        box.getChildren().addAll(label, leaderboard);
        return box;
    }
}
