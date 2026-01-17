package com.example.pcstore;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProductHandler implements EventHandler<MouseEvent> {

    MainScene ms;




    // لازم تمررهم من الـ CategoryCard
    private final int catgId;
    private final String catgName;

    public ProductHandler(MainScene ms, int catgId, String catgName) {
        this.ms = ms;
        this.catgId = catgId;
        this.catgName = catgName;
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        System.out.println("CLICK CATG ID = " + catgId + " NAME=" + catgName);


            // ===== Wrapper (بدون ما نكسر تصميم MainScene) =====
            VBox wrap = new VBox(18);
            wrap.setPadding(new Insets(20));

            // ===== Title + Back =====
            HBox head = new HBox(12);
            head.setAlignment(Pos.CENTER_LEFT);

            Button back = new Button("⬅ Back");
            back.setPrefHeight(38);
            back.setStyle(
                    "-fx-background-color: rgba(239, 68, 68, 0.85);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 10;" +
                            "-fx-cursor: hand;"
            );

            Label title = new Label("📦 " + catgName + " Products");
            title.setFont(Font.font("System", FontWeight.BOLD, 30));
            title.setTextFill(Color.web("#4ade80"));
            title.setEffect(new DropShadow(16, Color.web("#4ade80", 0.35)));

            head.getChildren().addAll(back, title);

            // ===== Grid Products =====
            ScrollPane sp = new ScrollPane();
            sp.setFitToWidth(true);
            sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
            sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            GridPane grid = new GridPane();
            grid.setHgap(18);
            grid.setVgap(18);
            grid.setAlignment(Pos.TOP_CENTER);

            VBox gridWrap = new VBox(12, grid);
            gridWrap.setPadding(new Insets(10, 0, 20, 0));
            gridWrap.setAlignment(Pos.TOP_CENTER);

            sp.setContent(gridWrap);

            // ===== Empty message =====
            Label empty = new Label("");
            empty.setTextFill(Color.web("#a3e635"));
            empty.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));

            wrap.getChildren().addAll(head, empty, sp);

            // ✅ Back يرجع للـ Home (المحتوى الأصلي)
            back.setOnAction(e -> {
                ms.handle(null);
            });
            loadProductsIntoGrid(grid, empty);
            ms.root.setCenter(wrap);

            TranslateTransition tt = new TranslateTransition(Duration.millis(300), wrap);
            tt.setFromY(15);
            tt.setToY(0);
            tt.play();

    }


    private void loadProductsIntoGrid(GridPane grid, Label emptyLabel) {

        grid.getChildren().clear();
        emptyLabel.setText("");

        String sql =
                "SELECT ProdID, Rate,ProdModel, Price, Quantity, Description, ImagePath " +
                        "FROM Product WHERE CatgID = ?";

        int col = 0, row = 0;
        int count = 0;

        try (Connection conn = ms.m.m.conn.connectDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, catgId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    int prodId = rs.getInt("ProdID");
                    String model = rs.getString("ProdModel");
                    double price = rs.getDouble("Price");
                    int qty = rs.getInt("Quantity");
                    String desc = rs.getString("Description");
                    String imgPath = rs.getString("ImagePath");
                    double rate = rs.getDouble("Rate");

                    VBox card = createProductCard(prodId, rate,model, price, qty, desc, imgPath);
                    grid.add(card, col, row);

                    col++;
                    if (col == 3) { // 3 per row
                        col = 0;
                        row++;
                    }

                    count++;
                }
            }

            if (count == 0) {
                emptyLabel.setText("No products found in this category.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            emptyLabel.setText("Error loading products: " + e.getMessage());
        }
    }

    // ===== نفس روح التصميم الأخضر + Card =====
    private VBox createProductCard(int prodId,double rate, String model, double price, int qty, String desc, String imgPath) {

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
        Label name = new Label(model == null ? ("Product #" + prodId) : model);
        name.setTextFill(Color.web("#4ade80"));
        name.setFont(Font.font("System", FontWeight.BOLD, 16));
        name.setWrapText(true);

        Label priceLabel = new Label("$" + String.format("%.2f", price));
        priceLabel.setTextFill(Color.web("#a3e635"));
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 22));

        String rr="";
        for (int i = 0; i < rate; i++) {
            rr+="⭐";
        }
        Label rateLabel= new Label("Rate :" + rr);
        rateLabel.setTextFill(Color.web("#c7f9cc"));
        rateLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));

        Label qtyLabel = new Label("Qty: " + qty);
        qtyLabel.setTextFill(Color.web("#c7f9cc"));
        qtyLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));

        Label descLabel = new Label(desc == null ? "" : desc);
        descLabel.setTextFill(Color.web("#a3e635"));
        descLabel.setFont(Font.font("System", 12));
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(48);

        Button add = new Button("Add to Cart");
        add.setMaxWidth(Double.MAX_VALUE);
        add.setPrefHeight(38);
        add.setStyle(
                "-fx-background-color: #22c55e;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        );

        add.setOnMouseEntered(e -> add.setStyle(
                "-fx-background-color: #16a34a;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(34, 197, 94, 0.55), 15, 0, 0, 5);"
        ));
        add.setOnMouseExited(e -> add.setStyle(
                "-fx-background-color: #22c55e;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        ));

        add.setOnAction(e -> {
            ms.addToCart(prodId);

            add.setText("✓ Added!");
            add.setStyle(
                    "-fx-background-color: #16a34a;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 10;" +
                            "-fx-cursor: hand;"
            );

            new Thread(() -> {
                try {
                    Thread.sleep(1200);
                    javafx.application.Platform.runLater(() -> {
                        add.setText("Add to Cart");
                        add.setStyle(
                                "-fx-background-color: #22c55e;" +
                                        "-fx-text-fill: white;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-background-radius: 10;" +
                                        "-fx-cursor: hand;"
                        );
                    });
                } catch (InterruptedException ignored) {}
            }).start();
        });


        card.getChildren().addAll(imgBox, name, priceLabel, qtyLabel,rateLabel, descLabel, add);

        // Hover animation
        card.setOnMouseEntered(e -> {
            card.setStyle(
                    "-fx-background-color: rgba(26, 46, 26, 0.90);" +
                            "-fx-background-radius: 16;" +
                            "-fx-border-color: #4ade80;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 16;" +
                            "-fx-effect: dropshadow(gaussian, rgba(74, 222, 128, 0.55), 30, 0, 0, 10);"
            );
            TranslateTransition tt = new TranslateTransition(Duration.millis(250), card);
            tt.setToY(-8);
            tt.play();
        });

        card.setOnMouseExited(e -> {
            card.setStyle(
                    "-fx-background-color: rgba(26, 46, 26, 0.90);" +
                            "-fx-background-radius: 16;" +
                            "-fx-border-color: rgba(74, 222, 128, 0.30);" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 16;"
            );
            TranslateTransition tt = new TranslateTransition(Duration.millis(250), card);
            tt.setToY(0);
            tt.play();
        });

        return card;
    }

    // ✅ يدعم: images/.. أو absolute أو resource
    private String resolveImageUrl(String imgPath) {
        if (imgPath == null) return null;
        String p = imgPath.trim().replace("\\", "/");
        if (p.isEmpty()) return null;

        File f = new File(p);
        if (f.exists()) return f.toURI().toString();

        var res = getClass().getResource(p.startsWith("/") ? p : "/" + p);
        if (res != null) return res.toExternalForm();

        return null;
    }


}
