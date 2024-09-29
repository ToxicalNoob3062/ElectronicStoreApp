import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ElectronicStoreApp extends Application {
    private ElectronicStore stockModel;
    private final ElectronicStoreView view;

    public ElectronicStoreApp() {
        // Create the model and views
        stockModel = ElectronicStore.createStore();
        view = new ElectronicStoreView();
    }

    // Method to reset the stock to 0 for the cart model
    private ElectronicStore formCartModel(ElectronicStore model) {
        // Resetting the stock to 0 as we won't use the soldQuantity here
        for (Product product : model.getStock()) {
            if (product != null) product.sellUnits(product.getStockQuantity());
        }
        return model;
    }

    @Override
    public void start(Stage primaryStage) {
        // Create application root container
        Pane container = new Pane();

        // Add the view to the container
        container.getChildren().add(view);

        // Populate the screen with some data
        view.update(stockModel);

        // Populate leaderboard
        view.setTop3Products(stockModel.getStock());

        // Attach handlers
        addHandlers();

        // Show the view
        primaryStage.setTitle(stockModel.getName());
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(container));
        primaryStage.show();
    }

    // Method to add event handlers
    private void addHandlers() {
        handleAddButton();
        handleCartList();
        handleRemoveButton();
        handleResetButton();
        handlePunchButton();
        handleStockList();
        handleLeaderboard();
    }

    // Method to handle events for the stock list
    private void handleStockList() {
        // Disable 'Add to Cart' button initially
        view.getAdd().setDisable(true);

        // Listen for selection changes in the stockList ListView
        view.getStockList().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Enable/disable the 'Add to Cart' button based on selection
            view.getAdd().setDisable(newValue == null);
        });

        // Listen for focus changes in the stockList ListView
        view.getStockList().focusedProperty().addListener((observable, oldValue, newValue) -> {
            // Check if the focus is lost and not transferred to the 'Add' button
            if (!newValue && !view.getAdd().isFocused()) {
                view.getStockList().getSelectionModel().clearSelection();
            } else {
                view.getLeaderboard().getSelectionModel().clearSelection();
                view.getCart().getSelectionModel().clearSelection();
            }
        });
    }

    // Method to handle events for the cart list
    private void handleCartList() {
        view.getPunch().setDisable(true);
        view.getRemove().setDisable(true);

        // Listen for changes in the cart ListView items
        view.getCart().getItems().addListener((ListChangeListener.Change<? extends String> change) -> {
            // Enable/disable the 'Punch' button based on cart list contents
            view.getPunch().setDisable(view.getCart().getItems().isEmpty());
        });

        view.getCart().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Enable/disable the 'Remove' button based on selection
            view.getRemove().setDisable(newValue == null);
        });

        // Listen for focus changes in the cart ListView
        view.getCart().focusedProperty().addListener((observable, oldValue, newValue) -> {
            // Check if the focus is lost and not transferred to the 'Remove' button
            if (!newValue && !view.getRemove().isFocused()) {
                view.getCart().getSelectionModel().clearSelection();
            } else {
                view.getStockList().getSelectionModel().clearSelection();
                view.getLeaderboard().getSelectionModel().clearSelection();
            }
        });
    }

    // Method to handle add button click
    private void handleAddButton() {
        view.getAdd().setOnAction(event -> {
            String selectedItem = view.getStockList().getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                double revenue = stockModel.removeFromStock(selectedItem);
                view.setCartValue(revenue, true);
                view.pushEntryToCart(selectedItem);
                view.update(stockModel);
            }
        });
    }

    // Method to handle remove button click
    private void handleRemoveButton() {
        view.getRemove().setOnAction(event -> {
            String selectedItem = view.getCart().getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                String entry = ElectronicStoreView.parseListEntry(selectedItem);
                double revenue = stockModel.addToStock(entry);
                view.setCartValue(revenue, false);
                int stock = ElectronicStoreView.parseListEntryToInt(selectedItem) - 1;
                if (stock == 0) {
                    view.getCart().getItems().remove(selectedItem);
                } else {
                    String updatedEntry = stock + " x " + entry;
                    view.getCart().getItems().set(view.getCart().getSelectionModel().getSelectedIndex(), updatedEntry);
                }
                view.update(stockModel);
            }
        });
    }

    // Method to handle punch button click
    private void handlePunchButton() {
        view.getPunch().setOnAction(actionEvent -> {
            view.setRevenue();
            view.incrementSale();
            view.setPerNumberTextField();
            view.getCart().getItems().clear();
            view.getCartLabel().setText("Cart Stock: ($0.00):");
            view.setTop3Products(stockModel.getStock());
        });
    }

    // Method to handle reset button click
    private void handleResetButton() {
        view.getReset().setOnAction(event -> {
            // Clear all list views
            view.getCart().getItems().clear();
            view.getLeaderboard().getItems().clear();
            view.getStockList().getItems().clear();

            // Recreate the stock model
            stockModel = ElectronicStore.createStore();

            // Update the view with the new stock model
            view.update(stockModel);
            view.setTop3Products(stockModel.getStock());

            // Reset text field values
            view.getSaleTextField().setText("0");
            view.getRevenueTextField().setText("0.00");
            view.getPerNumberTextField().setText("N/A");
            view.getCartLabel().setText("Cart Stock: ($0.00):");
        });
    }

    private void handleLeaderboard() {
        // Listen for selection changes in the leaderboard ListView
        view.getLeaderboard().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Check if a new item is selected in the leaderboard
            if (newValue != null) {
                // Clear selections in the stockList and cart ListView
                view.getStockList().getSelectionModel().clearSelection();
                view.getCart().getSelectionModel().clearSelection();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
