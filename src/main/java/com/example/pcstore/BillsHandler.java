package com.example.pcstore;

import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BillsHandler implements EventHandler<MouseEvent> {

    private final MainScene ms;

    // ===== Bills cards UI =====
    private ScrollPane billsSP;
    private VBox billsListBox;
    private BillCard selectedCard = null;

    private Label msg;

    public BillsHandler(MainScene ms) {
        this.ms = ms;
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        ms.getRoot().setCenter(createCenterBills());
    }

    // ================= UI =================

    private VBox createCenterBills() {
        VBox wrap = new VBox(18);
        wrap.setPadding(new Insets(18, 25, 18, 25));

        Label title = new Label("My Bills");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#4ade80"));
        title.setEffect(new DropShadow(16, Color.web("#4ade80", 0.35)));

        msg = new Label("");
        msg.setTextFill(Color.web("#a3e635"));
        msg.setStyle("-fx-font-weight: bold;");

        VBox billsCard = createBillsCardsPane();

        wrap.getChildren().addAll(title, msg, billsCard);

        TranslateTransition tt = new TranslateTransition(Duration.millis(220), wrap);
        tt.setFromY(10);
        tt.setToY(0);
        tt.play();

        return wrap;
    }

    // ================= Bills as Cards (with items inside each bill) =================

    private VBox createBillsCardsPane() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle(
                "-fx-background-color: rgba(34, 197, 94, 0.12); " +
                        "-fx-background-radius: 18; " +
                        "-fx-border-color: rgba(74, 222, 128, 0.30); " +
                        "-fx-border-width: 2; -fx-border-radius: 18;"
        );

        Label t = new Label("Bills List");
        t.setFont(Font.font("System", FontWeight.BOLD, 18));
        t.setTextFill(Color.web("#a3e635"));

        Button backBtn = new Button("Back");
        styleRedButton(backBtn);
        backBtn.setOnAction(e -> {
            ms.m.m.root.getChildren().clear();
            ms.handle(null);
        });

        HBox top = new HBox(12, t, new Region(), backBtn);
        HBox.setHgrow(top.getChildren().get(1), Priority.ALWAYS);

        billsListBox = new VBox(12);
        billsListBox.setPadding(new Insets(6));

        billsSP = new ScrollPane(billsListBox);
        billsSP.setFitToWidth(true);
        billsSP.setPrefHeight(640);
        billsSP.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        billsSP.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        billsSP.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        loadBillsAsCards();

        card.getChildren().addAll(top, billsSP);
        return card;
    }

    private void loadBillsAsCards() {
        billsListBox.getChildren().clear();

        if (ms.custId <= 0) {
            Label l = new Label("Login as Customer first.");
            l.setTextFill(Color.web("#ff6b6b"));
            billsListBox.getChildren().add(l);
            return;
        }

        String getBills =
                "SELECT BillID, BillDate, CustID, TotalAmount " +
                        "FROM Bill WHERE CustID=? ORDER BY BillDate DESC";

        try (Connection conn = ms.m.m.conn.connectDB();
             PreparedStatement ps = conn.prepareStatement(getBills)) {

            ps.setInt(1, ms.custId);

            try (ResultSet rs = ps.executeQuery()) {
                boolean any = false;

                while (rs.next()) {
                    any = true;

                    String billId = rs.getString("BillID");
                    String billDate = rs.getString("BillDate");
                    String custId = rs.getString("CustID");
                    String total = rs.getString("TotalAmount");

                    BillRow b = new BillRow(billId, billDate, custId, total);
                    billsListBox.getChildren().add(new BillCard(b));
                }

                if (!any) {
                    Label empty = new Label("No bills yet.");
                    empty.setTextFill(Color.web("#a3e635", 0.7));
                    empty.setPadding(new Insets(10));
                    billsListBox.getChildren().add(empty);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            setMsg("Failed to load bills: " + e.getMessage(), true);

            Label err = new Label("Failed to load bills.");
            err.setTextFill(Color.web("#ff6b6b"));
            err.setPadding(new Insets(10));
            billsListBox.getChildren().add(err);
        }
    }

    // ================= Items fetch (INSERTED ALREADY in Orders at Checkout) =================

    private ArrayList<BillItem> fetchBillItems(String billId) {
        ArrayList<BillItem> list = new ArrayList<>();

        // NOTE: Orders table is where items belong. Bill just groups them by BillID.
        String sql =
                "SELECT p.ProdModel, o.Quantity, o.UnitPrice " +
                        "FROM Orders o JOIN Product p ON o.ProdID = p.ProdID " +
                        "WHERE o.BillID=?";

        try (Connection conn = ms.m.m.conn.connectDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, billId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String model = rs.getString("ProdModel");
                    int qty = rs.getInt("Quantity");
                    double unit = rs.getDouble("UnitPrice");
                    list.add(new BillItem(model, qty, unit));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            setMsg("Failed to load items: " + e.getMessage(), true);
        }

        return list;
    }

    // ================= Bill Card Component (shows items inside) =================

    private class BillCard extends VBox {
        private final BillRow row;
        private boolean expanded = false;
        private final VBox itemsBox = new VBox(8);

        BillCard(BillRow row) {
            super(10);
            this.row = row;

            setPadding(new Insets(14));
            setStyle(normalStyle());
            setMaxWidth(Double.MAX_VALUE);

            Label id = new Label("Bill #" + row.getBillId());
            id.setFont(Font.font("System", FontWeight.BOLD, 18));
            id.setTextFill(Color.web("#4ade80"));

            Label date = new Label("Date: " + row.getBillDate());
            date.setTextFill(Color.web("#a3e635"));
            date.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));

            Label cust = new Label("CustID: " + row.getCustId());
            cust.setTextFill(Color.web("#c7f9cc"));
            cust.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));

            Label total = new Label("Total: $" + row.getTotal());
            total.setTextFill(Color.web("#a3e635"));
            total.setFont(Font.font("System", FontWeight.BOLD, 16));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox line = new HBox(12, date, spacer, total);
            line.setAlignment(Pos.CENTER_LEFT);

            // items container (collapsed initially)
            itemsBox.setPadding(new Insets(10));
            itemsBox.setStyle(
                    "-fx-background-color: rgba(10,14,10,0.35);" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-color: rgba(74,222,128,0.2);" +
                            "-fx-border-width: 1.5;" +
                            "-fx-border-radius: 12;"
            );
            itemsBox.setVisible(false);
            itemsBox.setManaged(false);

            Button toggle = new Button("Show Items");
            styleSoftButton(toggle);
            toggle.setOnAction(e -> toggleItems(toggle));

            HBox actions = new HBox(10, toggle);
            actions.setAlignment(Pos.CENTER_RIGHT);

            getChildren().addAll(id, cust, line, actions, itemsBox);

            // click card -> select + toggle
            setOnMouseClicked(e -> {
                selectThisCard();
                toggleItems(toggle);
            });

            setOnMouseEntered(e -> {
                if (selectedCard != this) setStyle(hoverStyle());
                TranslateTransition tt = new TranslateTransition(Duration.millis(180), this);
                tt.setToY(-4);
                tt.play();
            });

            setOnMouseExited(e -> {
                if (selectedCard != this) setStyle(normalStyle());
                TranslateTransition tt = new TranslateTransition(Duration.millis(180), this);
                tt.setToY(0);
                tt.play();
            });
        }

        private void toggleItems(Button toggleBtn) {
            expanded = !expanded;

            if (expanded) {
                toggleBtn.setText("Hide Items");
                renderItemsInsideCard();
                itemsBox.setManaged(true);
                itemsBox.setVisible(true);
            } else {
                toggleBtn.setText("Show Items");
                itemsBox.getChildren().clear();
                itemsBox.setManaged(false);
                itemsBox.setVisible(false);
            }
        }

        private void renderItemsInsideCard() {
            itemsBox.getChildren().clear();

            ArrayList<BillItem> list = fetchBillItems(row.getBillId());

            if (list.isEmpty()) {
                Label l = new Label("No items in this bill.");
                l.setTextFill(Color.web("#a3e635", 0.7));
                itemsBox.getChildren().add(l);
                return;
            }

            // header row
            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            Label h1 = smallBold("Item");
            Label h2 = smallBold("Qty");
            Label h3 = smallBold("Unit");
            Label h4 = smallBold("Sub");
            Region r = new Region();
            HBox.setHgrow(r, Priority.ALWAYS);
            header.getChildren().addAll(h1, r, h2, h3, h4);
            itemsBox.getChildren().add(header);

            double sum = 0;
            for (BillItem it : list) {
                sum += it.subtotal();

                Label item = new Label(it.model);
                item.setTextFill(Color.web("#a3e635"));
                item.setFont(Font.font("System", 13));

                Label qty = new Label(String.valueOf(it.qty));
                qty.setTextFill(Color.web("#c7f9cc"));

                Label unit = new Label("$" + String.format("%.2f", it.unit));
                unit.setTextFill(Color.web("#c7f9cc"));

                Label sub = new Label("$" + String.format("%.2f", it.subtotal()));
                sub.setTextFill(Color.web("#4ade80"));
                sub.setFont(Font.font("System", FontWeight.BOLD, 13));

                Region rr = new Region();
                HBox.setHgrow(rr, Priority.ALWAYS);

                HBox rowLine = new HBox(10, item, rr, qty, unit, sub);
                rowLine.setAlignment(Pos.CENTER_LEFT);
                itemsBox.getChildren().add(rowLine);
            }

            Separator sep = new Separator();

            Label calc = new Label("Calculated Total: $" + String.format("%.2f", sum));
            calc.setTextFill(Color.web("#a3e635"));
            calc.setFont(Font.font("System", FontWeight.BOLD, 13));

            itemsBox.getChildren().addAll(sep, calc);
        }

        private void selectThisCard() {
            if (selectedCard != null) selectedCard.setStyle(selectedCard.normalStyle());
            selectedCard = this;
            setStyle(selectedStyle());
        }

        private String normalStyle() {
            return "-fx-background-color: rgba(10,14,10,0.40);" +
                    "-fx-background-radius: 14;" +
                    "-fx-border-color: rgba(74, 222, 128, 0.25);" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 14;" +
                    "-fx-cursor: hand;";
        }

        private String hoverStyle() {
            return "-fx-background-color: rgba(10,14,10,0.55);" +
                    "-fx-background-radius: 14;" +
                    "-fx-border-color: rgba(74, 222, 128, 0.55);" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 14;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(74, 222, 128, 0.35), 20, 0, 0, 6);";
        }

        private String selectedStyle() {
            return "-fx-background-color: rgba(26, 46, 26, 0.85);" +
                    "-fx-background-radius: 14;" +
                    "-fx-border-color: #4ade80;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 14;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(74, 222, 128, 0.55), 28, 0, 0, 10);";
        }
    }

    private Label smallBold(String s) {
        Label l = new Label(s);
        l.setTextFill(Color.web("#4ade80"));
        l.setFont(Font.font("System", FontWeight.BOLD, 13));
        return l;
    }

    // ================= Helpers (style + msg) =================

    private void styleSoftButton(Button b) {
        b.setStyle("-fx-background-color: rgba(163, 230, 53, 0.15); " +
                "-fx-text-fill: #a3e635; -fx-font-weight: bold; " +
                "-fx-padding: 10 16; -fx-background-radius: 10; -fx-cursor: hand;" +
                "-fx-border-color: rgba(163, 230, 53, 0.35); -fx-border-width: 2; -fx-border-radius: 10;");
    }

    private void styleRedButton(Button b) {
        b.setStyle(
                "-fx-background-color: rgba(239, 68, 68, 0.85);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        );
        b.setOnMouseEntered(e -> b.setStyle(
                "-fx-background-color: #dc2626;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(239,68,68,0.6), 15, 0, 0, 4);"
        ));
        b.setOnMouseExited(e -> b.setStyle(
                "-fx-background-color: rgba(239, 68, 68, 0.85);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        ));
    }

    private void setMsg(String text, boolean error) {
        if (msg == null) return;
        msg.setText(text);
        msg.setTextFill(error ? Color.web("#ff6b6b") : Color.web("#a3e635"));
    }

    // ================= Models =================

    public static class BillRow {
        private final String billId;
        private final String billDate;
        private final String custId;
        private final String total;

        public BillRow(String billId, String billDate, String custId, String total) {
            this.billId = billId;
            this.billDate = billDate;
            this.custId = custId;
            this.total = total;
        }

        public String getBillId() { return billId; }
        public String getBillDate() { return billDate; }
        public String getCustId() { return custId; }
        public String getTotal() { return total; }
    }

    public static class BillItem {
        public final String model;
        public final int qty;
        public final double unit;

        public BillItem(String model, int qty, double unit) {
            this.model = model;
            this.qty = qty;
            this.unit = unit;
        }

        public double subtotal() {
            return qty * unit;
        }
    }
}
