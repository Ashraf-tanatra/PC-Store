package com.example.pcstore;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MainScene implements EventHandler<ActionEvent> {

    BorderPane root;
    private int cartCount = 0;

    private int activeBillId = -1;
     int custId = -1;

    // ✅ FIX: must be field and used everywhere
    private Label cartLabel;

    private TextField searchField;

    // Cart List
    ArrayList<CartItem> items=new  ArrayList<CartItem>();
    int countItems=0;
    Label totalLbl;

    SignInScene m;
    public MainScene(SignInScene root) {
        m = root;
        this.custId = root.currentCustId;
    }


    @Override
    public void handle(ActionEvent actionEvent) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0a0e0a, #1a2e1a);");

        root.setTop(createHeader());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox mainContent = new VBox(30);
        mainContent.setPadding(new Insets(20));
        mainContent.getChildren().addAll(
                createHeroSection(),
                createCategoriesSection(),
                createProductsSection()
        );

        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);
        m.m.root.setCenter(root);
        refreshCartLabelFromState();
    }

    public BorderPane getRoot() {
        return root;
    }

    private HBox createHeader() {
        HBox header = new HBox(535);
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: linear-gradient(to right, #1a3d1a, #0d260d); " +
                "-fx-effect: dropshadow(gaussian, rgba(34, 139, 34, 0.5), 20, 0, 0, 4);");

        Label logo = new Label("⚡ TechVault");
        logo.setFont(Font.font("System", FontWeight.BOLD, 28));
        logo.setTextFill(Color.web("#4ade80"));

        HBox parts = new HBox(30);
        parts.setAlignment(Pos.CENTER);
        Label home = LabelForm("Home");
        Label bills = LabelForm("Bills");
        Label supports = LabelForm("Supports");
        Label profile = LabelForm("Profile");

        SupportHandler supp=new SupportHandler(this);
        supports.setOnMouseClicked(supp);

        home.setOnMouseClicked(e -> {
            m.m.root.getChildren().clear();
            handle(null);
        });
        ProfileHandler pf=new ProfileHandler(this);
        profile.setOnMouseClicked(pf);

        parts.getChildren().addAll(home, bills, profile,supports);

        BillsHandler b=new BillsHandler(this);
        bills.setOnMouseClicked(b);


        // ✅ FIX: use field cartLabel (do NOT shadow it)
        this.cartLabel = new Label("🛒 Cart (0)");
        Button cartBtn = new Button();
        cartBtn.setGraphic(this.cartLabel);
        cartBtn.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8;");
        cartBtn.setOnMouseEntered(e -> cartBtn.setStyle(
                "-fx-background-color: #16a34a; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(gaussian, rgba(34, 197, 94, 0.6), 15, 0, 0, 5);"));
        cartBtn.setOnMouseExited(e -> cartBtn.setStyle(
                "-fx-background-color: #22c55e; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8;"));
        cartBtn.setPrefHeight(40);
        cartBtn.setPrefWidth(130);
        cartBtn.setOnAction(e->{
            openCart();
        });

        Button signOutBtn = new Button("⎋ Sign Out");
        signOutBtn.setPrefHeight(40);
        signOutBtn.setPrefWidth(130);
        signOutBtn.setStyle(
                "-fx-background-color: rgba(239, 68, 68, 0.85);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        );

        signOutBtn.setOnMouseEntered(e -> signOutBtn.setStyle(
                "-fx-background-color: #dc2626;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(239,68,68,0.6), 15, 0, 0, 4);"
        ));

        signOutBtn.setOnMouseExited(e -> signOutBtn.setStyle(
                "-fx-background-color: rgba(239, 68, 68, 0.85);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        ));

        HBox h = new HBox(10);
        h.getChildren().addAll(cartBtn, signOutBtn);

        header.getChildren().addAll(logo, parts, h);

        signOutBtn.setOnAction(e -> {
            m.buildUI();
            m.m.root.setCenter(m.getRoot());
        });

        return header;
    }

    public Label LabelForm(String s){
        Label t = new Label(s);
        t.setTextFill(Color.web("#a3e635"));
        t.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        t.setOnMouseEntered(e -> t.setTextFill(Color.web("#4ade80")));
        t.setOnMouseExited(e -> t.setTextFill(Color.web("#a3e635")));
        return t;
    }

    private void refreshCartLabelFromState() {
        if (cartLabel != null) {
            cartLabel.setText("🛒 Cart (" + cartCount + ")");
        }
    }

    public void addToCart(int prodId) {

        String sql = "SELECT ProdModel, Price, Quantity FROM Product WHERE ProdID=? LIMIT 1";

        try (Connection conn = m.m.conn.connectDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, prodId);
            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    new Alert(Alert.AlertType.ERROR, "Product not found").showAndWait();
                    return;
                }

                String model = rs.getString("ProdModel");
                double price = rs.getDouble("Price");
                int stock = rs.getInt("Quantity");

                // (اختياري) تحقق من المخزون
                if (stock <= 0) {
                    new Alert(Alert.AlertType.WARNING, "Out of stock").showAndWait();
                    return;
                }

                // إذا موجود بالسلة زيد الكمية
                for (CartItem it : items) {
                    if (it.prodId == prodId) {
                        it.qty++;
                        cartCount++;
                        if (cartLabel != null) cartLabel.setText("🛒 Cart (" + cartCount + ")");
                        return;
                    }
                }

                // إذا مش موجود ضيف جديد
                items.add(new CartItem(prodId, model, price, 1));
                cartCount++;
                if (cartLabel != null) cartLabel.setText("🛒 Cart (" + cartCount + ")");

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Add failed: " + ex.getMessage()).showAndWait();
        }
    }



    private VBox createHeroSection() {
        VBox hero = new VBox(20);
        hero.setPadding(new Insets(60, 40, 60, 40));
        hero.setAlignment(Pos.CENTER);
        hero.setStyle("-fx-background-color: rgba(34, 197, 94, 0.15); " +
                "-fx-background-radius: 20; " +
                "-fx-border-color: rgba(74, 222, 128, 0.3); " +
                "-fx-border-width: 2; -fx-border-radius: 20;");

        Label title = new Label("Build Your Dream PC");
        title.setFont(Font.font("System", FontWeight.BOLD, 48));
        title.setTextFill(Color.web("#4ade80"));

        DropShadow titleGlow = new DropShadow();
        titleGlow.setColor(Color.web("#4ade80", 0.4));
        titleGlow.setRadius(20);
        title.setEffect(titleGlow);

        Label subtitle = new Label("Premium PC components and peripherals at competitive prices");
        subtitle.setFont(Font.font("System", 18));
        subtitle.setTextFill(Color.web("#a3e635"));

        HBox searchBar = new HBox(10);
        searchBar.setMaxWidth(600);
        searchBar.setAlignment(Pos.CENTER);

        searchField = new TextField();
        searchField.setPromptText("Search for PC parts, peripherals, and more...");
        searchField.setPrefWidth(450);
        searchField.setPrefHeight(45);
        searchField.setStyle("-fx-background-color: rgba(26, 46, 26, 0.8); " +
                "-fx-text-fill: #e0e0e0; -fx-prompt-text-fill: #888; " +
                "-fx-border-color: #22c55e; -fx-border-width: 2; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-padding: 12; -fx-font-size: 14;");

        Button searchBtn = new Button("Search");
        searchBtn.setPrefHeight(45);
        searchBtn.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 12 30; -fx-background-radius: 10; " +
                "-fx-cursor: hand;");

        searchBar.getChildren().addAll(searchField, searchBtn);
        hero.getChildren().addAll(title, subtitle, searchBar);

        return hero;
    }

    // ✅ IMPORTANT: load images from disk first, then resources fallback
    private String resolveImageUrl(String imgPath) {
        if (imgPath == null) return null;

        String p = imgPath.trim().replace("\\", "/");
        if (p.isEmpty()) return null;

        // 1) disk
        File f = new File(p);
        if (f.exists()) return f.toURI().toString();

        // 2) resources fallback
        var res = getClass().getResource(p.startsWith("/") ? p : "/" + p);
        if (res != null) return res.toExternalForm();

        return null;
    }

    private VBox createCategoriesSection() {
        VBox section = new VBox(20);

        Label sectionTitle = new Label("Shop by Category");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 32));
        sectionTitle.setTextFill(Color.web("#4ade80"));

        GridPane categories = new GridPane();
        categories.setHgap(20);
        categories.setVgap(20);
        categories.setAlignment(Pos.CENTER);

        String sql = "SELECT CatgID, CatgName, ImagePath FROM Category";

        try (Connection conn = m.m.conn.connectDB();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int col = 0;
            int row = 0;

            while (rs.next()) {
                String name = rs.getString("CatgName");
                String id = rs.getString("CatgID");
                String imgPath = rs.getString("ImagePath");

                String url = resolveImageUrl(imgPath);
                if (url == null) {
                    System.out.println("Image NOT found: " + imgPath + " for " + name);
                    continue;
                }

                ImageView iv = new ImageView(new Image(url));
                iv.setFitWidth(150);
                iv.setFitHeight(150);
                iv.setPreserveRatio(true);

                VBox categoryCard = createCategoryCard(iv, name);
                ProductHandler ph=new ProductHandler(this,Integer.valueOf(id),name);
                categoryCard.setOnMouseClicked(ph);
                categories.add(categoryCard, col, row);



                row++;
                if (row == 2) { // 3 columns
                    row = 0;
                    col++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        section.getChildren().addAll(sectionTitle, categories);
        return section;
    }

    private VBox createCategoryCard(ImageView icon, String name) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(250);
        card.setPrefHeight(180);
        card.setStyle("-fx-background-color: rgba(34, 197, 94, 0.15); " +
                "-fx-background-radius: 15; " +
                "-fx-border-color: rgba(74, 222, 128, 0.3); " +
                "-fx-border-width: 2; -fx-border-radius: 15; -fx-cursor: hand;");

        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        nameLabel.setTextFill(Color.web("#4ade80"));

        card.getChildren().addAll(icon, nameLabel);

        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: rgba(34, 197, 94, 0.25); " +
                    "-fx-background-radius: 15; " +
                    "-fx-border-color: #4ade80; " +
                    "-fx-border-width: 2; -fx-border-radius: 15; -fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(74, 222, 128, 0.6), 30, 0, 0, 10);");

            TranslateTransition translate = new TranslateTransition(Duration.millis(300), card);
            translate.setToY(-10);
            translate.play();
        });

        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: rgba(34, 197, 94, 0.15); " +
                    "-fx-background-radius: 15; " +
                    "-fx-border-color: rgba(74, 222, 128, 0.3); " +
                    "-fx-border-width: 2; -fx-border-radius: 15; -fx-cursor: hand;");

            TranslateTransition translate = new TranslateTransition(Duration.millis(300), card);
            translate.setToY(0);
            translate.play();
        });

        return card;
    }
    private VBox createProductsSection() {
        VBox section = new VBox(20);

        Label title = new Label("⭐ Top Rated (5)");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#4ade80"));

        GridPane productsGrid = new GridPane();
        productsGrid.setHgap(20);
        productsGrid.setVgap(20);
        productsGrid.setAlignment(Pos.CENTER);

        String sql = "SELECT ProdModel, Price, Description, ImagePath, Rate FROM Product WHERE Rate = 5";

        int col = 0, row = 0;

        try (Connection conn = m.m.conn.connectDB();   // ✅ لاحظ: غالبًا الصح m.conn مش m.m.conn
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean hasAny = false;

            while (rs.next()) {
                hasAny = true;

                String model = rs.getString("ProdModel");
                double price = rs.getDouble("Price");
                String desc  = rs.getString("Description") ;
                String img   = rs.getString("ImagePath") ;
                int rate     = rs.getInt("Rate");

                VBox productCard = createProductCard(model, desc, price, img, rate);
                productsGrid.add(productCard, col, row);

                col++;
                if (col == 3) { col = 0; row++; }
            }

            if (!hasAny) {
                Label empty = new Label("No products with Rate = 5 yet.");
                empty.setTextFill(Color.web("#a3e635"));
                section.getChildren().addAll(title, empty);
                return section;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Label err = new Label("Failed to load products.");
            err.setTextFill(Color.web("#ff6b6b"));
            section.getChildren().addAll(title, err);
            return section;
        }

        section.getChildren().addAll(title, productsGrid);
        return section;
    }



    private VBox createProductCard(String name, String desc, double price, String imgPath, int rate) {

        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setPrefWidth(300);
        card.setStyle(
                "-fx-background-color: rgba(26, 46, 26, 0.90);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: rgba(74, 222, 128, 0.30);" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 16;"
        );

        // ===== Image =====
        ImageView iv = new ImageView();
        iv.setFitWidth(260);
        iv.setFitHeight(160);
        iv.setPreserveRatio(true);

        String url = resolveImageUrl(imgPath);
        if (url != null) {
            iv.setImage(new Image(url, true));
        }

        StackPane imgBox = new StackPane(iv);
        imgBox.setAlignment(Pos.CENTER);
        imgBox.setPrefHeight(170);
        imgBox.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #1a3d1a, #0d260d);" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: rgba(74, 222, 128, 0.25);" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 12;"
        );

        // ===== Text =====
        Label nameLabel = new Label(name);
        nameLabel.setTextFill(Color.web("#4ade80"));
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        nameLabel.setWrapText(true);

        // ⭐ Rate
        int r = Math.max(0, Math.min(5, rate));
        Label rateLabel = new Label("⭐".repeat(r) + "  (" + r + "/5)");
        rateLabel.setTextFill(Color.web("#a3e635"));
        rateLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        rateLabel.setStyle("-fx-background-color: rgba(34, 197, 94, 0.15); -fx-padding: 6 10; -fx-background-radius: 10;");

        Label priceLabel = new Label("$" + String.format("%.2f", price));
        priceLabel.setTextFill(Color.web("#a3e635"));
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 22));

        Label descLabel = new Label(desc == null ? "" : desc);
        descLabel.setTextFill(Color.web("#a3e635"));
        descLabel.setFont(Font.font("System", 12));
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(48);

        Button addBtn = new Button("Add to Cart");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setPrefHeight(38);
        addBtn.setStyle(
                "-fx-background-color: #22c55e;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        );

        addBtn.setOnAction(e -> {
            // لو بدك add to cart هون لازم يكون عندك prodId
            // حاليا مجرد شكل
        });

        card.getChildren().addAll(imgBox, nameLabel, rateLabel, priceLabel, descLabel, addBtn);
        return card;
    }
    private VBox buildCartView() {
        totalLbl=new Label();
        totalLbl.setText("Total = "+String.valueOf(refreshTotale()));

        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: rgba(26, 46, 26, 0.60); -fx-background-radius: 18;");

        Label title = new Label("🛒 Your Cart");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#4ade80"));

        TableView<CartItem> table = new TableView<>();
        table.getStylesheets().add(getClass().getResource("/TableCSS.css").toExternalForm());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: rgba(10, 14, 10, 0.7); -fx-background-radius: 12;");

        ObservableList<CartItem> data = FXCollections.observableArrayList(items);
        table.setItems(data);

        TableColumn<CartItem, String> colModel = new TableColumn<>("Product");
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<CartItem, Double> colPrice = new TableColumn<>("Unit Price");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        TableColumn<CartItem, Integer> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));

        TableColumn<CartItem, Double> colSub = new TableColumn<>("Subtotal");
        colSub.setCellValueFactory(c ->
                new javafx.beans.property.SimpleDoubleProperty(c.getValue().subtotal()).asObject()
        );

        TableColumn<CartItem, Void> colAct = new TableColumn<>("Action");
        colAct.setCellFactory(tc -> new TableCell<>() {
            private final Button plus = new Button("+");
            private final Button minus = new Button("-");
            private final Button rem = new Button("Remove");
            private final HBox hb = new HBox(8, minus, plus, rem);

            {
                hb.setAlignment(Pos.CENTER);

                plus.setStyle("-fx-background-color:#22c55e;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:8;");
                minus.setStyle("-fx-background-color:#a3e635;-fx-text-fill:black;-fx-font-weight:bold;-fx-background-radius:8;");
                rem.setStyle("-fx-background-color:#ef4444;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:8;");

                plus.setOnAction(e -> {
                    CartItem it = getTableView().getItems().get(getIndex());
                    it.qty++;
                    cartCount++;
                    refreshCartLabelFromState();
                    totalLbl.setText("Total = "+String.valueOf(refreshTotale()));
                    getTableView().refresh();
                });

                minus.setOnAction(e -> {
                    CartItem it = getTableView().getItems().get(getIndex());
                    it.qty--;
                    cartCount--;

                    if (it.qty <= 0) {
                        items.remove(it);
                        getTableView().getItems().remove(it);
                    }
                    refreshCartLabelFromState();
                    totalLbl.setText("Total = "+String.valueOf(refreshTotale()));
                    getTableView().refresh();
                });

                rem.setOnAction(e -> {
                    CartItem it = getTableView().getItems().get(getIndex());
                    cartCount -= it.qty;

                    items.remove(it);
                    getTableView().getItems().remove(it);

                    refreshCartLabelFromState();
                    totalLbl.setText("Total = "+String.valueOf(refreshTotale()));
                    getTableView().refresh();
                });


            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hb);
            }

        });

        table.getColumns().addAll(colModel, colPrice, colQty, colSub, colAct);

        totalLbl.setTextFill(Color.web("#a3e635"));
        totalLbl.setFont(Font.font("System", FontWeight.BOLD, 18));

        Button btnBack = new Button("← Back");
        btnBack.setStyle("-fx-background-color: rgba(163,230,53,0.85); -fx-font-weight:bold; -fx-background-radius:10;");
        btnBack.setOnAction(e -> handle(null));

        Button btnCheckout = new Button("✅ Checkout");
        btnCheckout.setStyle("-fx-background-color:#22c55e;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:10;");
        btnCheckout.setOnAction(e -> {

            if (custId <= 0) {
                new Alert(Alert.AlertType.ERROR, "Login as customer first").showAndWait();
                return;
            }
            if (items.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Cart is empty").showAndWait();
                return;
            }

            String updStock = "UPDATE Product SET Quantity = Quantity - ? WHERE ProdID = ? AND Quantity >= ?";
            String insertBill = "INSERT INTO Bill(BillDate, CustID, TotalAmount) VALUES (?,?,?)";
            String insertOrder =
                    "INSERT INTO Orders(BillID, ProdID, OrderDate, Quantity, UnitPrice, Status ,Items) " +
                            "VALUES (?, ?, ?, ?, ?, TRUE,?)";

            double total = refreshTotale();

            try (Connection conn = m.m.conn.connectDB()) {
                conn.setAutoCommit(false);
                int billId;
                try (PreparedStatement ps = conn.prepareStatement(insertBill, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    ps.setTimestamp(1, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
                    ps.setInt(2, custId);
                    ps.setDouble(3, total);
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            conn.rollback();
                            new Alert(Alert.AlertType.ERROR, "Failed to create bill").showAndWait();
                            return;
                        }
                        billId = keys.getInt(1);
                    }
                }

                // 2) UPDATE STOCK + INSERT ORDERS
                try (PreparedStatement psStock = conn.prepareStatement(updStock);
                     PreparedStatement psOrder = conn.prepareStatement(insertOrder)) {

                    java.sql.Timestamp nowTs = java.sql.Timestamp.valueOf(java.time.LocalDateTime.now());

                    for (CartItem i : items) {

                        psStock.setInt(1, i.getQty());
                        psStock.setInt(2, i.getProdId());
                        psStock.setInt(3, i.getQty());
                        int affected = psStock.executeUpdate();
                        if (affected == 0) {
                            conn.rollback();
                            new Alert(Alert.AlertType.WARNING,
                                    "Not enough stock for: " + i.getModel()).showAndWait();
                            return;
                        }


                        // insert order
                        psOrder.setInt(1, billId);
                        psOrder.setInt(2, i.getProdId());
                        psOrder.setTimestamp(3, nowTs);
                        psOrder.setInt(4, i.getQty());          // ✅ FIX (INT)
                        psOrder.setDouble(5, i.getUnitPrice());
                        psOrder.setString(6, i.getModel());
                        psOrder.executeUpdate();
                    }
                }

                conn.commit();
                conn.setAutoCommit(true);

                items.clear();
                cartCount = 0;
                refreshCartLabelFromState();
                table.setItems(FXCollections.observableArrayList(items));
                totalLbl.setText("Total = 0.0");

                new Alert(Alert.AlertType.INFORMATION, "Checkout done ✅ BillID = " + billId).showAndWait();

            } catch (Exception ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Checkout failed: " + ex.getMessage()).showAndWait();
            }
        });


        HBox bottom = new HBox(15, btnBack, totalLbl, btnCheckout);
        bottom.setAlignment(Pos.CENTER_RIGHT);

        box.getChildren().addAll(title, table, bottom);
        return box;
    }

    public double refreshTotale() {
        double sum=0;
        for (CartItem it : items) sum+=it.subtotal();
        return sum;
    }

    private ObservableList<CartRow> loadCartRows() {
        ObservableList<CartRow> data = FXCollections.observableArrayList();
        if (activeBillId == -1) return data;

        String sql =
                "SELECT o.OrderID, o.ProdID, p.ProdModel, o.Quantity, o.UnitPrice " +
                        "FROM Orders o JOIN Product p ON o.ProdID=p.ProdID " +
                        "WHERE o.BillID=? AND o.Status=TRUE";

        try (Connection conn = m.m.conn.connectDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activeBillId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.add(new CartRow(
                            rs.getInt("OrderID"),
                            rs.getInt("ProdID"),
                            rs.getString("ProdModel"),
                            rs.getInt("Quantity"),
                            rs.getDouble("UnitPrice")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private void refreshCartTable(TableView<CartRow> table) {
        table.setItems(loadCartRows());
        this.cartCount = fetchCartCount();
        if (cartLabel != null) cartLabel.setText("🛒 Cart (" + cartCount + ")");
    }

    private int fetchCartCount() {
        if (activeBillId == -1) return 0;
        String sql = "SELECT IFNULL(SUM(Quantity),0) AS C FROM Orders WHERE BillID=? AND Status=TRUE";
        try (Connection conn = m.m.conn.connectDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activeBillId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("C");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private double fetchBillTotal() {
        if (activeBillId == -1) return 0.0;
        String sql = "SELECT TotalAmount FROM Bill WHERE BillID=? LIMIT 1";
        try (Connection conn = m.m.conn.connectDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activeBillId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("TotalAmount");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private void updateCartQty(int orderId, int prodId, int newQty) {
        String upd = "UPDATE Orders SET Quantity=? WHERE OrderID=?";
        String updBill =
                "UPDATE Bill SET TotalAmount=(" +
                        "SELECT IFNULL(SUM(Quantity*UnitPrice),0) FROM Orders WHERE BillID=? AND Status=TRUE) " +
                        "WHERE BillID=?";

        try (Connection conn = m.m.conn.connectDB()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(upd)) {
                ps.setInt(1, newQty);
                ps.setInt(2, orderId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(updBill)) {
                ps.setInt(1, activeBillId);
                ps.setInt(2, activeBillId);
                ps.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeFromCart(int orderId) {
        String sql = "UPDATE Orders SET Status=FALSE WHERE OrderID=?";
        String updBill =
                "UPDATE Bill SET TotalAmount=(" +
                        "SELECT IFNULL(SUM(Quantity*UnitPrice),0) FROM Orders WHERE BillID=? AND Status=TRUE) " +
                        "WHERE BillID=?";

        try (Connection conn = m.m.conn.connectDB()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, orderId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(updBill)) {
                ps.setInt(1, activeBillId);
                ps.setInt(2, activeBillId);
                ps.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkoutCart() {
        if (activeBillId == -1) return;

        // Checkout: خلّي كل عناصر السلة Status=FALSE (يعني خلصت)
        String sql = "UPDATE Orders SET Status=FALSE WHERE BillID=? AND Status=TRUE";
        try (Connection conn = m.m.conn.connectDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activeBillId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // خلّي السلة الجديدة تبدأ بفاتورة جديدة
        activeBillId = -1;
        this.cartCount = 0;
        if (cartLabel != null) cartLabel.setText("🛒 Cart (0)");
    }

    private void openCart() {
        if (custId <= 0) {
            new Alert(Alert.AlertType.ERROR, "Login as Customer first.").showAndWait();
            return;
        }

        VBox cartView = buildCartView();
        ScrollPane sp = new ScrollPane(cartView);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        root.setCenter(sp);
    }


    public static class CartRow {
        private final int orderId;
        private final int prodId;
        private final String model;
        private int quantity;
        private final double unitPrice;

        public CartRow(int orderId, int prodId, String model, int quantity, double unitPrice) {
            this.orderId = orderId;
            this.prodId = prodId;
            this.model = model;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public int getOrderId() { return orderId; }
        public int getProdId() { return prodId; }
        public String getModel() { return model; }
        public int getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
        public double getSubtotal() { return unitPrice * quantity; }

        public void setQuantity(int q) { this.quantity = q; }
    }
    public static class CartItem {
        int prodId;
        String model;
        double unitPrice;
        int qty;

        public CartItem(int prodId, String model, double unitPrice, int qty) {
            this.prodId = prodId;
            this.model = model;
            this.unitPrice = unitPrice;
            this.qty = qty;
        }

        public int getProdId() {
            return prodId;
        }

        public String getModel() {
            return model;
        }

        public int getQty() {
            return qty;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public double subtotal() { return unitPrice * qty; }
    }


}
