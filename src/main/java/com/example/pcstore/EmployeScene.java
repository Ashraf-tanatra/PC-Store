package com.example.pcstore;

import javafx.animation.TranslateTransition;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class EmployeScene implements EventHandler<ActionEvent> {

    int CatgID = 0;
    private SignInScene s;
    private BorderPane root;
    int gg;

    // Tables
    private TableView<ProductRow> Ptable;
    private ObservableList<ProductRow> Pdata = FXCollections.observableArrayList();

    private TableView<CategoryRow> Ctable;
    private ObservableList<CategoryRow> Cdata = FXCollections.observableArrayList();

    private TableView<UserRow> Utable;
    private ObservableList<UserRow> Udata = FXCollections.observableArrayList();

    private TableView<ObservableList<String>> reportTable = new TableView<>();


    // Products fields
    private TextField tfProdID, tfModel, tfPrice, tfQty, tfCatgID, tfInvID, tfProdImagePath ,tfRate;
    private TextArea taDesc;
    private Label msg;

    // Category fields
    private TextField tfCatgID_C, tfCatgName_C, tfCatgImagePath_C;
    private TextArea taCatgDesc_C;

    // Employee info
    String empName = "";
    String empRole = "";


    HBox btnRow;

    // Users fields
    Label salaryLabel;
    private TextField tfUserID_U, tfUserName_U, tfphone, tfEmail_U, tfUserNameLogin_U ,tfSalary ;
    private PasswordField pfPass_U;
    private ComboBox<String> cbRole_U, cbStatus_U, cbGender_U,cbIsActive;

    // Dash Board Buttons
    Button maxSalaryBtn, empBySalaryBtn, lowStockBtn, lastRestockBtn,
            supplierPurchaseBtn, supplierSupplyBtn, prodByPriceBtn, prodAbove30Btn,
            orderedPerProdBtn, stockPerProdBtn,
            salesBranchBtn, salesEmpBtn, empSalesYearBtn,
            stockLocationBtn, ordersYearBtn, activeOrdersBtn ,activeUsersBtn, revenuePerSaleBtn;
    ArrayList<Button> buttons = new ArrayList<>();


    public EmployeScene(SignInScene s) {
        this.s = s;
        empRole = s.role;
        empName = s.name;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        build();
    }

    public BorderPane getRoot() {
        return root;
    }

    private void build() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0a0e0a, #1a2e1a);");
        root.setTop(createHeader());
        root.setCenter(createCenterC());
        root.setPadding(new Insets(0, 0, 10, 0));
    }

    private HBox createHeader() {
        HBox header = new HBox(25);
        header.setPadding(new Insets(18, 30, 18, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: linear-gradient(to right, #1a3d1a, #0d260d); " +
                "-fx-effect: dropshadow(gaussian, rgba(34, 139, 34, 0.5), 20, 0, 0, 4);");

        Label logo = new Label("⚡ TechVault");
        logo.setFont(Font.font("System", FontWeight.BOLD, 28));
        logo.setTextFill(Color.web("#4ade80"));

        Label badge = new Label("EMPLOYEE PANEL");
        badge.setStyle("-fx-background-color: rgba(34, 197, 94, 0.20);" +
                "-fx-border-color: rgba(74, 222, 128, 0.35);" +
                "-fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;" +
                "-fx-padding: 8 14; -fx-font-weight: bold;");
        badge.setTextFill(Color.web("#a3e635"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button dashboardbtn = new Button("Dashboard");
        dashboardbtn.setPrefWidth(130);
        dashboardbtn.setPrefHeight(40);
       styleGreenButton(dashboardbtn);
        dashboardbtn.setOnAction(e -> {
            if (maxSalaryBtn == null) {
                initDashboardButtons();
                wireDashboardActions();
            }
            root.setCenter(createReportsView());
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

        Button loadEmp = new Button("Load User");
        styleGreenButton(loadEmp);
        loadEmp.setOnAction(e -> {
            if ("ADMIN".equals(empRole)) {
                loadUsers();
                root.setCenter(createCenterU());
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("You Dont Have An Admin Role....");
                alert.showAndWait();
            }
        });

        Button loadCategory = new Button("⟳ Load Categorys");
        styleGreenButton(loadCategory);
        loadCategory.setOnAction(e -> {
            loadCategorys();
            root.setCenter(createCenterC());
        });

        HBox buttons = new HBox(10,dashboardbtn, loadEmp, loadCategory, signOutBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        msg = new Label("");
        msg.setTextFill(Color.web("#a3e635"));
        msg.setStyle("-fx-font-weight: bold;");

        header.getChildren().addAll(logo, badge, spacer, msg, buttons);

        signOutBtn.setOnAction(e -> {
            s.buildUI();
            s.m.root.setCenter(s.getRoot());
        });

        return header;
    }

    private GridPane createDashboard() {
        buttons.clear();
        buttons.addAll(Arrays.asList(
                maxSalaryBtn, empBySalaryBtn, lowStockBtn, lastRestockBtn,
                supplierPurchaseBtn, supplierSupplyBtn, prodByPriceBtn, prodAbove30Btn,
                orderedPerProdBtn, stockPerProdBtn,
                salesBranchBtn, salesEmpBtn, empSalesYearBtn,
                stockLocationBtn, ordersYearBtn, activeOrdersBtn,
                activeUsersBtn, revenuePerSaleBtn
        ));

        int row = 0, col = 0;

        GridPane dashboard = new GridPane();
        dashboard.setHgap(15);
        dashboard.setVgap(15);
        dashboard.setPadding(new Insets(20));
        dashboard.setAlignment(Pos.CENTER);
        dashboard.setEffect(new DropShadow(22, Color.rgb(74, 222, 128, 0.30)));
        dashboard.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, rgba(26,61,26,0.85), rgba(13,38,13,0.85));" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-radius: 18;" +
                        "-fx-border-color: rgba(74, 222, 128, 0.32);" +
                        "-fx-border-width: 2;" +
                        "-fx-effect: dropshadow(gaussian, rgba(34, 197, 94, 0.25), 30, 0, 0, 10);"
        );

        for (int i = 0; i < buttons.size(); i++) {
            buttons.set(i, createButtonStyle(buttons.get(i)));
            dashboard.add(buttons.get(i), col, row);
            col++;
            if (col == 4) { col = 0; row++; }
            if (row == 5) break;
        }

        return dashboard;
    }

    private VBox createReportsView() {
        VBox wrap = new VBox(15);
        wrap.setPadding(new Insets(18, 25, 18, 25));

        GridPane dash = createDashboard();

        reportTable.setPrefHeight(520);
        reportTable.getStylesheets().add(getClass().getResource("/TableCSS.css").toExternalForm());


        wrap.getChildren().addAll(dash, reportTable);
        return wrap;
    }


    private void initDashboardButtons() {
        maxSalaryBtn        = new Button("Highest Salary Employee");
        empBySalaryBtn      = new Button("Employees by Salary");
        lowStockBtn         = new Button("Low Stock Products");
        lastRestockBtn      = new Button("Last Restock per Product");
        supplierPurchaseBtn = new Button("Total Purchases by Supplier");
        supplierSupplyBtn   = new Button("Total Supplied per Supplier");
        prodByPriceBtn      = new Button("Products by Price");
        prodAbove30Btn      = new Button("Products Price > 30");
        orderedPerProdBtn   = new Button("Total Ordered per Product");
        stockPerProdBtn     = new Button("Total Stock per Product");
        salesBranchBtn      = new Button("Sales per Branch");
        salesEmpBtn         = new Button("Sales per Employee");
        empSalesYearBtn     = new Button("Employees Sales in Year");
        stockLocationBtn    = new Button("Stock per Warehouse Location");
        ordersYearBtn       = new Button("Orders This Year");
        activeOrdersBtn     = new Button("Active Orders Count");
        activeUsersBtn      = new Button("Active Users");
        revenuePerSaleBtn   = new Button("Revenue per Sale");
    }

    private void wireDashboardActions() {

        maxSalaryBtn.setOnAction(e -> {
            String sql =
                    "SELECT e.EmpID, p.FirstName, p.SecondName, e.Salary, e.Address " +
                            "FROM Employee e JOIN Person p ON p.PersonID = e.EmpID " +
                            "ORDER BY e.Salary DESC LIMIT 1;";
            loadReport(sql);
        });

        empBySalaryBtn.setOnAction(e -> {
            String sql =
                    "SELECT e.EmpID, p.FirstName, p.SecondName, e.Salary, e.Address " +
                            "FROM Employee e JOIN Person p ON p.PersonID = e.EmpID " +
                            "ORDER BY e.Salary DESC;";
            loadReport(sql);
        });

        lowStockBtn.setOnAction(e -> {
            String sql =
                    "SELECT ProdID, ProdModel, Quantity, Price, CatgID, InvID " +
                            "FROM Product WHERE Quantity < 50 ORDER BY Quantity ASC;";
            loadReport(sql);
        });

        lastRestockBtn.setOnAction(e -> {
            String sql =
                    "SELECT p.ProdID, p.ProdModel, MAX(s.SupplyDate) AS LastRestockDate " +
                            "FROM Product p LEFT JOIN Supply s ON s.ProdID = p.ProdID " +
                            "GROUP BY p.ProdID, p.ProdModel " +
                            "ORDER BY LastRestockDate DESC;";
            loadReport(sql);
        });

        supplierPurchaseBtn.setOnAction(e -> {
            String sql =
                    "SELECT sp.SupID, sp.SupName, COALESCE(SUM(s.Price * s.Quantity), 0) AS TotalPurchaseValue " +
                            "FROM Supplier sp LEFT JOIN Supply s ON s.SupID = sp.SupID " +
                            "GROUP BY sp.SupID, sp.SupName " +
                            "ORDER BY TotalPurchaseValue DESC;";
            loadReport(sql);
        });

        supplierSupplyBtn.setOnAction(e -> {
            String sql =
                    "SELECT sp.SupID, sp.SupName, COALESCE(SUM(s.Quantity),0) AS TotalSuppliedQty " +
                            "FROM Supplier sp LEFT JOIN Supply s ON s.SupID = sp.SupID " +
                            "GROUP BY sp.SupID, sp.SupName " +
                            "ORDER BY TotalSuppliedQty DESC;";
            loadReport(sql);
        });

        prodByPriceBtn.setOnAction(e -> {
            String sql =
                    "SELECT ProdID, ProdModel, Price, Quantity, CatgID, InvID " +
                            "FROM Product ORDER BY Price DESC;";
            loadReport(sql);
        });

        prodAbove30Btn.setOnAction(e -> {
            String sql =
                    "SELECT ProdID, ProdModel, Price, Quantity " +
                            "FROM Product WHERE Price > 30 ORDER BY Price DESC;";
            loadReport(sql);
        });

        orderedPerProdBtn.setOnAction(e -> {
            String sql =
                    "SELECT p.ProdID, p.ProdModel, COALESCE(SUM(o.Quantity),0) AS TotalOrdered " +
                            "FROM Product p LEFT JOIN Orders o ON o.ProdID = p.ProdID " +
                            "GROUP BY p.ProdID, p.ProdModel " +
                            "ORDER BY TotalOrdered DESC;";
            loadReport(sql);
        });

        stockPerProdBtn.setOnAction(e -> {
            String sql =
                    "SELECT ProdID, ProdModel, Quantity AS StockQty, Price " +
                            "FROM Product ORDER BY StockQty DESC;";
            loadReport(sql);
        });


        salesBranchBtn.setOnAction(e -> {
            String sql =
                    "SELECT i.Location AS Branch, COUNT(*) AS OrdersCount " +
                            "FROM Orders o " +
                            "JOIN Product p ON p.ProdID = o.ProdID " +
                            "JOIN Inventory i ON i.InvID = p.InvID " +
                            "GROUP BY i.Location " +
                            "ORDER BY OrdersCount DESC;";
            loadReport(sql);
        });

        salesEmpBtn.setOnAction(e -> {
            // Sales per person = customer bills count (because Bill has CustID only)
            String sql =
                    "SELECT p.PersonID, p.FirstName, p.SecondName, COUNT(b.BillID) AS BillsCount " +
                            "FROM Bill b " +
                            "JOIN Customer c ON c.CustID = b.CustID " +
                            "JOIN Person p ON p.PersonID = c.CustID " +
                            "GROUP BY p.PersonID, p.FirstName, p.SecondName " +
                            "ORDER BY BillsCount DESC;";
            loadReport(sql);
        });

        empSalesYearBtn.setOnAction(e -> {
            TextInputDialog d = new TextInputDialog("2025");
            d.setHeaderText("Enter Year (e.g. 2025)");
            d.setContentText("Year:");
            d.showAndWait().ifPresent(yearTxt -> {
                Integer y = parseInt(yearTxt);
                if (y == null) {
                    new Alert(Alert.AlertType.ERROR, "Invalid year").showAndWait();
                    return;
                }
                String sql =
                        "SELECT DISTINCT p.PersonID, p.FirstName, p.SecondName, COUNT(b.BillID) AS BillsCount " +
                                "FROM Bill b " +
                                "JOIN Customer c ON c.CustID = b.CustID " +
                                "JOIN Person p ON p.PersonID = c.CustID " +
                                "WHERE YEAR(b.BillDate) = " + y + " " +
                                "GROUP BY p.PersonID, p.FirstName, p.SecondName " +
                                "ORDER BY BillsCount DESC;";
                loadReport(sql);
            });
        });

        stockLocationBtn.setOnAction(e -> {
            String sql =
                    "SELECT i.Location, COALESCE(SUM(p.Quantity),0) AS TotalStock " +
                            "FROM Inventory i LEFT JOIN Product p ON p.InvID = i.InvID " +
                            "GROUP BY i.Location " +
                            "ORDER BY TotalStock DESC;";
            loadReport(sql);
        });

        ordersYearBtn.setOnAction(e -> {
            String sql =
                    "SELECT OrderID, BillID, ProdID, OrderDate, Quantity, UnitPrice, Status " +
                            "FROM Orders WHERE YEAR(OrderDate) = YEAR(CURDATE()) " +
                            "ORDER BY OrderDate DESC;";
            loadReport(sql);
        });

        activeOrdersBtn.setOnAction(e -> {
            String sql =
                    "SELECT COUNT(*) AS ActiveOrdersCount FROM Orders WHERE Status = TRUE;";
            loadReport(sql);
        });

        activeUsersBtn.setOnAction(e -> {
            String sql =
                    "SELECT UserID, UserName, Role, ActiveStatus, Email " +
                            "FROM Users WHERE ActiveStatus = TRUE;";
            loadReport(sql);
        });

        revenuePerSaleBtn.setOnAction(e -> {
            String sql =
                    "SELECT b.BillID, b.BillDate, " +
                            "       COALESCE(SUM(o.Quantity * o.UnitPrice), 0) AS Revenue " +
                            "FROM Bill b LEFT JOIN Orders o ON o.BillID = b.BillID " +
                            "GROUP BY b.BillID, b.BillDate " +
                            "ORDER BY Revenue DESC;";
            loadReport(sql);
        });
    }


    private void loadReport(String sql) {
        reportTable.getColumns().clear();
        reportTable.getItems().clear();

        try (Connection conn = s.m.conn.connectDB();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();

            // create columns dynamically
            for (int c = 0; c < cols; c++) {
                final int colIndex = c;
                TableColumn<ObservableList<String>, String> col =
                        new TableColumn<>(md.getColumnLabel(c + 1));
                col.setMinWidth(200);
                col.setPrefWidth(240);
                col.setMaxWidth(400);


                col.setCellValueFactory(data ->
                        new javafx.beans.property.SimpleStringProperty(
                                data.getValue().get(colIndex)
                        ));

                reportTable.getColumns().add(col);
            }

            // fill rows
            while (rs.next()) {
                ObservableList<String> row = javafx.collections.FXCollections.observableArrayList();
                for (int c = 1; c <= cols; c++) {
                    row.add(String.valueOf(rs.getObject(c)));
                }
                reportTable.getItems().add(row);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }
    }


    private Button createButtonStyle(Button b){
        Button b1=b;
        b1.setPrefWidth(220);
        b1.setPrefHeight(45);

        b1.setStyle(
                "-fx-background-color: rgb(34,197,94);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        );

        b1.setOnMouseEntered(e -> b1.setStyle(
                "-fx-background-color: #22c55e;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(116,239,68,0.6), 15, 0, 0, 4);"
        ));

        b1.setOnMouseExited(e -> b1.setStyle(
                "-fx-background-color: rgb(34,197,94);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        ));
        return b1;
    }

    // ======================= USERS =======================

    private VBox createCenterU() {
        VBox wrap = new VBox(18);
        wrap.setPadding(new Insets(18, 25, 18, 25));

        Label title = new Label("Manage Users");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#4ade80"));
        title.setEffect(new DropShadow(16, Color.web("#4ade80", 0.35)));

        HBox content = new HBox(18);
        content.setAlignment(Pos.TOP_CENTER);

        VBox tableCard = createTableCardUsers();
        VBox formCard = createFormCardUsers();

        HBox.setHgrow(tableCard, Priority.ALWAYS);
        content.getChildren().addAll(tableCard, formCard);

        wrap.getChildren().addAll(title, content);
        return wrap;
    }

    private VBox createTableCardUsers() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: rgba(34, 197, 94, 0.12); " +
                "-fx-background-radius: 18; " +
                "-fx-border-color: rgba(74, 222, 128, 0.30); " +
                "-fx-border-width: 2; -fx-border-radius: 18;");

        Label t = new Label("Users List");
        t.setFont(Font.font("System", FontWeight.BOLD, 18));
        t.setTextFill(Color.web("#a3e635"));

        Utable = new TableView<>();
        Utable.getStylesheets().add(getClass().getResource("/TableCSS.css").toExternalForm());
        Utable.setItems(Udata);
        Utable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Utable.setPrefHeight(560);

        TableColumn<UserRow, Integer> cId = new TableColumn<>("UserID");
        cId.setCellValueFactory(new PropertyValueFactory<>("userID"));

        TableColumn<UserRow, String> cName = new TableColumn<>("UserName");
        cName.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<UserRow, String> cRole = new TableColumn<>("Role");
        cRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<UserRow, String> cStatus = new TableColumn<>("Status");
        cStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        Utable.getColumns().addAll(cId, cName, cRole, cStatus);

        Utable.getSelectionModel().selectedItemProperty().addListener((obs, old, row) -> {
            if (row == null) return;

            tfUserID_U.setText(String.valueOf(row.getUserID()));
            tfUserNameLogin_U.setText(row.getUserName());
            pfPass_U.setText(row.getPass());
            cbRole_U.setValue(row.getRole());
            cbStatus_U.setValue(row.getStatus());
            tfEmail_U.setText(row.getEmail() == null ? "" : row.getEmail());

            // ✅ Show salary فقط لو EMP
            if ("EMP".equalsIgnoreCase(row.getRole())) {
                salaryLabel.setVisible(true);
                tfSalary.setVisible(true);

                String sql = "SELECT e.Salary " +
                        "FROM Users u JOIN Employee e ON e.EmpID = u.PersonID " +
                        "WHERE u.UserID = ?";

                try (Connection conn = s.m.conn.connectDB();
                     PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setInt(1, row.getUserID());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            tfSalary.setText(String.valueOf(rs.getDouble("Salary")));
                        } else {
                            tfSalary.setText(""); // لو ما في employee row
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    tfSalary.setText("");
                }

            } else {
                salaryLabel.setVisible(false);
                tfSalary.setVisible(false);
                tfSalary.setText("");
            }
        });

        Utable.setStyle(
                "-fx-background-color: rgba(10,14,10,0.40);" +
                        "-fx-border-color: rgba(74, 222, 128, 0.25);" +
                        "-fx-border-radius: 10; -fx-background-radius: 10;"
        );

        card.getChildren().addAll(t, Utable);
        return card;
    }

    private VBox createFormCardUsers() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setPrefWidth(420);
        card.setStyle("-fx-background-color: rgba(26, 46, 26, 0.90); " +
                "-fx-background-radius: 18; " +
                "-fx-border-color: rgba(74, 222, 128, 0.30); " +
                "-fx-border-width: 2; -fx-border-radius: 18;");

        Label t = new Label("Add / Edit User");
        t.setFont(Font.font("System", FontWeight.BOLD, 18));
        t.setTextFill(Color.web("#a3e635"));

        tfUserID_U = styledTF("User ID (Auto)");
        tfUserID_U.setEditable(false);
        tfSalary=styledTF("Salary");
        tfUserName_U = styledTF("Full Name (First Last)");
        tfphone = styledTF("Phone");
        tfUserNameLogin_U = styledTF("Username (Login)");
        tfEmail_U = styledTF("Email");
        pfPass_U = new PasswordField();
        pfPass_U.setPromptText("Password");
        pfPass_U.setPrefHeight(42);
        pfPass_U.setStyle(
                "-fx-background-color: rgba(26, 46, 26, 0.8);" +
                        "-fx-text-fill: #e0e0e0;" +
                        "-fx-prompt-text-fill: #888;" +
                        "-fx-border-color: rgba(74, 222, 128, 0.35);" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 10;"
        );

        cbRole_U = new ComboBox<>();
        cbRole_U.getItems().addAll("ADMIN", "EMP", "CUST");
        cbRole_U.setPromptText("Role");
        cbRole_U.setPrefHeight(42);

        cbGender_U = new ComboBox<>();
        cbGender_U.getItems().addAll("Male", "Female");
        cbGender_U.setPromptText("Gender");
        cbGender_U.setPrefHeight(42);

        cbStatus_U = new ComboBox<>();
        cbStatus_U.getItems().addAll("Active", "Inactive");
        cbStatus_U.setPromptText("Status");
        cbStatus_U.setPrefHeight(42);

        styleCombo(cbRole_U);
        styleCombo(cbGender_U);
        styleCombo(cbStatus_U);

        btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER);

        Button addBtn = new Button("➕ Add");
        styleGreenButton(addBtn);
        addBtn.setOnAction(e -> addUser());

        Button deleteUser=new Button("Delete");
        styleGreenButton(deleteUser);
        deleteUser.setOnAction(e -> deleteUser());

        Button updateBtn = new Button("✏ Update");
        styleGreenButton(updateBtn);
        updateBtn.setOnAction(e -> {
            updateUser();
            loadUsers();
        });

        Button clearBtn = new Button("🧹 Clear");
        styleGreenButton(clearBtn);
        clearBtn.setOnAction(e -> clearUserForm());

        btnRow.getChildren().addAll(addBtn, updateBtn, clearBtn ,deleteUser);
        HBox h=new HBox(10);
        salaryLabel=new Label("Salary ");
        salaryLabel.setVisible(false);
        tfSalary.setVisible(false);
        h.getChildren().addAll( cbRole_U, salaryLabel , tfSalary);
        card.getChildren().addAll(t, tfUserID_U, tfUserName_U, tfphone, cbGender_U, tfUserNameLogin_U, tfEmail_U, pfPass_U, h, cbStatus_U, btnRow );

        TranslateTransition tt = new TranslateTransition(Duration.millis(400), card);
        tt.setFromY(18);
        tt.setToY(0);
        tt.play();

        return card;
    }
    private void deleteUser() {

        Integer userId = parseInt(tfUserID_U.getText());
        if (userId == null) {
            setMsg("Select User ❗", true);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete user?\n" +
                        "• If Customer has Bills -> will Deactivate (cannot delete).\n" +
                        "• Otherwise -> will delete permanently (Person + User + Role row).",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() != ButtonType.YES) return;

        Connection conn = null;
        try {
            conn = s.m.conn.connectDB();
            conn.setAutoCommit(false);

            // 1) get PersonID + Role
            Integer personId = null;
            String role = null;

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT PersonID, Role FROM Users WHERE UserID=?"
            )) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        personId = rs.getInt("PersonID");
                        role = rs.getString("Role");
                    }
                }
            }

            if (personId == null) {
                setMsg("User not found ❗", true);
                conn.rollback();
                return;
            }

            // 2) block ADMIN
            if ("ADMIN".equalsIgnoreCase(role)) {
                setMsg("Can't delete ADMIN ❗", true);
                conn.rollback();
                return;
            }

            // 3) If CUST -> check bills
            if ("CUST".equalsIgnoreCase(role)) {
                int billsCount = 0;
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT COUNT(*) FROM Bill WHERE CustID=?"
                )) {
                    ps.setInt(1, personId); // CustID == PersonID
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) billsCount = rs.getInt(1);
                    }
                }

                // has bills -> deactivate instead of delete
                if (billsCount > 0) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE Users SET ActiveStatus=FALSE WHERE UserID=?"
                    )) {
                        ps.setInt(1, userId);
                        ps.executeUpdate();
                    }

                    conn.commit();
                    setMsg("Customer has bills → Deactivated ✅", false);
                    loadUsers();
                    clearUserForm();
                    return;
                }
            }

            // 4) delete Person فقط ✅ (رح يعمل cascade على Users + Employee/Customer)
            // Users(PersonID) ON DELETE CASCADE
            // Employee(EmpID->PersonID) ON DELETE CASCADE
            // Customer(CustID->PersonID) ON DELETE CASCADE
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM Person WHERE PersonID=?"
            )) {
                ps.setInt(1, personId);
                int d = ps.executeUpdate();
                if (d == 0) {
                    setMsg("Person not found ❗", true);
                    conn.rollback();
                    return;
                }
            }

            conn.commit();
            setMsg("User deleted ✅", false);
            loadUsers();
            clearUserForm();

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignore) {}
            e.printStackTrace();
            setMsg("Delete failed: " + e.getMessage(), true);
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception ignore) {}
        }
    }

    private void addUser() {
        String fullName = tfUserName_U.getText().trim();
        if (fullName.isEmpty()) {
            setMsg("Enter full name ❗", true);
            return;
        }

        String[] arr = fullName.split("\\s+");
        String firstName = arr[0];
        String lastName = (arr.length >= 2) ? arr[1] : "N/A";

        if (cbGender_U.getValue() == null) {
            setMsg("Select gender ❗", true);
            return;
        }
        String gender = cbGender_U.getValue();

        String phone = tfphone.getText().trim();
        if (phone.isEmpty()) {
            setMsg("Enter phone ❗", true);
            return;
        }

        String userNameLogin = tfUserNameLogin_U.getText().trim();
        String pass = pfPass_U.getText().trim();
        String role = cbRole_U.getValue();
        String status = cbStatus_U.getValue();
        boolean active = "Active".equalsIgnoreCase(status);
        String email = tfEmail_U.getText().trim();

        if (userNameLogin.isEmpty() || pass.isEmpty() || role == null || status == null) {
            setMsg("Fill all user fields ❗", true);
            return;
        }

        String sqlPerson = "INSERT INTO Person(FirstName, SecondName, Gender, Phone) VALUES (?,?,?,?)";
        String sqlUser   = "INSERT INTO Users(PersonID, UserName, Password, Role, ActiveStatus, Email) VALUES (?,?,?,?,?,?)";
        String sqlEmp    = "INSERT INTO Employee(EmpID, Salary, Address) VALUES (?, ?, ?)";
        String sqlCust   = "INSERT INTO Customer(CustID, LastPurchaseDate) VALUES (?, NULL)";

        try (Connection conn = s.m.conn.connectDB()) {
            conn.setAutoCommit(false);

            int personId;

            // 1) insert person
            try (PreparedStatement psPerson = conn.prepareStatement(sqlPerson, Statement.RETURN_GENERATED_KEYS)) {
                psPerson.setString(1, firstName);
                psPerson.setString(2, lastName);
                psPerson.setString(3, gender);
                psPerson.setString(4, phone);
                psPerson.executeUpdate();

                try (ResultSet rs = psPerson.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("Failed to get PersonID");
                    personId = rs.getInt(1);
                }
            }

            // 2) insert users
            try (PreparedStatement psUser = conn.prepareStatement(sqlUser)) {
                psUser.setInt(1, personId);
                psUser.setString(2, userNameLogin);
                psUser.setString(3, pass);
                psUser.setString(4, role);
                psUser.setBoolean(5, active);

                if (email.isEmpty()) psUser.setNull(6, Types.VARCHAR);
                else psUser.setString(6, email);

                psUser.executeUpdate();
            }

            // 3) insert into role-table
            if ("EMP".equalsIgnoreCase(role)) {
                // (اختياري) خليه افتراضي مؤقت
                double defaultSalary = 0.0;
                String defaultAddress = null;

                try (PreparedStatement psEmp = conn.prepareStatement(sqlEmp)) {
                    psEmp.setInt(1, personId);
                    psEmp.setDouble(2, defaultSalary);
                    psEmp.setString(3, defaultAddress);
                    psEmp.executeUpdate();
                }

            } else if ("CUST".equalsIgnoreCase(role)) {
                try (PreparedStatement psCust = conn.prepareStatement(sqlCust)) {
                    psCust.setInt(1, personId);
                    psCust.executeUpdate();
                }
            }
            // ADMIN: لا شيء إضافي (أو اعتبره EMP إذا بدك)

            conn.commit();
            setMsg("User added ✅", false);

            loadUsers();
            clearUserForm();

        } catch (SQLIntegrityConstraintViolationException e) {
            setMsg("Duplicate (phone/username/email) ❗", true);
            try { s.m.conn.connectDB().rollback(); } catch (Exception ignore) {}
        } catch (Exception e) {
            e.printStackTrace();
            setMsg("Add failed: " + e.getMessage(), true);
            try { s.m.conn.connectDB().rollback(); } catch (Exception ignore) {}
        }
    }

    private void updateUser() {
        Integer id = parseInt(tfUserID_U.getText()); // UserID
        String userNameLogin = tfUserNameLogin_U.getText().trim();
        String pass = pfPass_U.getText().trim();
        String role = cbRole_U.getValue();
        String status = cbStatus_U.getValue();
        boolean active = "Active".equalsIgnoreCase(status);
        String email = tfEmail_U.getText().trim();

        if (id == null || userNameLogin.isEmpty() || role == null || status == null) {
            setMsg("Select a user and fill fields ❗", true);
            return;
        }

        Connection conn = null;
        try {
            conn = s.m.conn.connectDB();
            conn.setAutoCommit(false);

            // 1) Update Users
            String sqlUser = "UPDATE Users SET UserName=?, Password=?, Role=?, ActiveStatus=?, Email=? WHERE UserID=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {

                ps.setString(1, userNameLogin);
                ps.setString(2, pass.isEmpty() ? null : pass);
                ps.setString(3, role);
                ps.setBoolean(4, active);
                ps.setString(5, email.isEmpty() ? null : email);
                ps.setInt(6, id);

                int updated = ps.executeUpdate();
                if (updated == 0) {
                    conn.rollback();
                    setMsg("User not found ❗", true);
                    return;
                }
            }

            // 2) If EMP -> update salary in Employee table
            if ("EMP".equalsIgnoreCase(role)) {

                // لازم نجيب PersonID عشان Employee.EmpID = Users.PersonID
                Integer personId = null;
                try (PreparedStatement ps = conn.prepareStatement("SELECT PersonID FROM Users WHERE UserID=?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) personId = rs.getInt(1);
                    }
                }

                if (personId == null) {
                    conn.rollback();
                    setMsg("User person not found ❗", true);
                    return;
                }

                Double salary = parseDouble(tfSalary.getText());
                if (salary == null) {
                    conn.rollback();
                    setMsg("Enter valid salary ❗", true);
                    return;
                }

                // إذا ما كان عنده row بجدول Employee (احتياط)
                int exists = 0;
                try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Employee WHERE EmpID=?")) {
                    ps.setInt(1, personId);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        exists = rs.getInt(1);
                    }
                }

                if (exists == 0) {
                    // create row if missing
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO Employee(EmpID, Salary, Address) VALUES (?, ?, NULL)"
                    )) {
                        ps.setInt(1, personId);
                        ps.setDouble(2, salary);
                        ps.executeUpdate();
                    }
                } else {
                    // update salary
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE Employee SET Salary=? WHERE EmpID=?"
                    )) {
                        ps.setDouble(1, salary);
                        ps.setInt(2, personId);
                        ps.executeUpdate();
                    }
                }
            }

            conn.commit();
            setMsg("User updated ✅", false);

        } catch (SQLIntegrityConstraintViolationException e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignore) {}
            setMsg("Duplicate (username/email) ❗", true);

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignore) {}
            e.printStackTrace();
            setMsg("Update failed: " + e.getMessage(), true);

        } finally {
            try { if (conn != null) conn.close(); } catch (Exception ignore) {}
        }
    }


    private void clearUserForm() {
        tfUserID_U.clear();
        tfUserName_U.clear();
        tfphone.clear();
        tfUserNameLogin_U.clear();
        tfEmail_U.clear();
        pfPass_U.clear();
        cbRole_U.setValue(null);
        cbStatus_U.setValue(null);
        cbGender_U.setValue(null);
        if (Utable != null) Utable.getSelectionModel().clearSelection();
        setMsg("", false);
    }

    // ======================= CATEGORIES & PRODUCTS =======================

    private VBox createCenterC() {
        VBox wrap = new VBox(18);
        wrap.setPadding(new Insets(18, 25, 18, 25));

        Label title = new Label("Manage Categories");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#4ade80"));
        title.setEffect(new DropShadow(16, Color.web("#4ade80", 0.35)));

        HBox content = new HBox(18);
        content.setAlignment(Pos.TOP_CENTER);

        VBox tableCard = createTableCardCategory();
        VBox formCard = createFormCardCategory();

        HBox.setHgrow(tableCard, Priority.ALWAYS);
        content.getChildren().addAll(tableCard, formCard);

        wrap.getChildren().addAll(title, content);
        return wrap;
    }

    private VBox createCenterP() {
        VBox wrap = new VBox(18);
        wrap.setPadding(new Insets(18, 25, 18, 25));

        Label title = new Label("Manage Products");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#4ade80"));
        title.setEffect(new DropShadow(16, Color.web("#4ade80", 0.35)));

        HBox content = new HBox(18);
        content.setAlignment(Pos.TOP_CENTER);

        VBox tableCard = createTableCardProduct();
        VBox formCard = createFormCardProducts();

        HBox.setHgrow(tableCard, Priority.ALWAYS);
        content.getChildren().addAll(tableCard, formCard);

        wrap.getChildren().addAll(title, content);
        return wrap;
    }

    private VBox createTableCardCategory() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: rgba(34, 197, 94, 0.12);" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: rgba(74, 222, 128, 0.30);" +
                "-fx-border-width: 2; -fx-border-radius: 18;");

        Label t = new Label("Categories List");
        t.setFont(Font.font("System", FontWeight.BOLD, 18));
        t.setTextFill(Color.web("#a3e635"));

        Ctable = new TableView<>();
        Ctable.getStylesheets().add(getClass().getResource("/TableCSS.css").toExternalForm());
        Ctable.setItems(Cdata);
        Ctable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Ctable.setPrefHeight(400);

        TableColumn<CategoryRow, Integer> cId = new TableColumn<>("CatgID");
        cId.setCellValueFactory(new PropertyValueFactory<>("catgID"));

        TableColumn<CategoryRow, String> cName = new TableColumn<>("Category Name");
        cName.setCellValueFactory(new PropertyValueFactory<>("catgName"));

        TableColumn<CategoryRow, String> cDesc = new TableColumn<>("Description");
        cDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<CategoryRow, Void> actionCol = new TableColumn<>("Products");
        Ctable.getColumns().addAll(cId, cName, cDesc, actionCol);

        Ctable.getSelectionModel().selectedItemProperty().addListener((obs, old, row) -> {
            if (row == null) return;
            tfCatgID_C.setText(String.valueOf(row.getCatgID()));
            tfCatgName_C.setText(row.getCatgName());
            taCatgDesc_C.setText(row.getDescription() );
            tfCatgImagePath_C.setText(row.getImagePath() );
            cbIsActive.setValue(row.getIsActive());
        });

        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("📦 View");
            {
                btn.setStyle("-fx-background-color: rgba(34,197,94,0.85);" +
                        "-fx-text-fill: white; -fx-font-weight: bold;" +
                        "-fx-background-radius: 8; -fx-cursor: hand;");
                btn.setOnAction(e -> {
                    CategoryRow row = getTableView().getItems().get(getIndex());
                    CatgID = row.getCatgID();
                    loadProducts(CatgID);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        card.getChildren().addAll(t, Ctable);
        return card;
    }

    private VBox createFormCardCategory() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setPrefWidth(380);
        card.setStyle("-fx-background-color: rgba(26, 46, 26, 0.90);" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: rgba(74, 222, 128, 0.30);" +
                "-fx-border-width: 2; -fx-border-radius: 18;");

        Label t = new Label("Add / Edit Category");
        t.setFont(Font.font("System", FontWeight.BOLD, 18));
        t.setTextFill(Color.web("#a3e635"));

        tfCatgID_C = styledTF("Category ID");
        tfCatgID_C.setEditable(false);
        tfCatgName_C = styledTF("Category Name");

        taCatgDesc_C = new TextArea();
        taCatgDesc_C.setPromptText("Description");
        taCatgDesc_C.setWrapText(true);
        taCatgDesc_C.setPrefRowCount(4);
        taCatgDesc_C.setStyle(
                "-fx-control-inner-background: rgba(26, 46, 26, 0.8);" +
                        "-fx-text-fill: #e0e0e0;" +
                        "-fx-prompt-text-fill: #888;" +
                        "-fx-border-color: rgba(74, 222, 128, 0.35);" +
                        "-fx-border-radius: 10; -fx-background-radius: 10;"
        );

        cbIsActive=new ComboBox<>();
        cbIsActive.setPromptText("Activation");
        cbIsActive.setPrefHeight(42);
        cbIsActive.getItems().addAll("ACTIVE", "NOT ACTIVE");

        styleCombo(cbIsActive);

        tfCatgImagePath_C = styledTF("Image Path");
        Button chooseCatgImg = new Button("📁 Choose Image");
        styleGreenButton(chooseCatgImg);
        chooseCatgImg.setOnAction(e -> chooseImage(tfCatgImagePath_C));

        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER);

        Button addBtn = new Button("➕ Add");
        styleGreenButton(addBtn);
        addBtn.setOnAction(e -> addCategory());

        Button updateBtn = new Button("✏ Update");
        styleGreenButton(updateBtn);
        updateBtn.setOnAction(e -> updateCategory());

        Button clearBtn = new Button("🧹 Clear");
        styleGreenButton(clearBtn);
        clearBtn.setOnAction(e -> clearCategoryForm());

        btnRow.getChildren().addAll(addBtn, updateBtn, clearBtn);

        card.getChildren().addAll(
                t,tfCatgName_C,taCatgDesc_C,cbIsActive, tfCatgImagePath_C, chooseCatgImg,btnRow
        );

        return card;
    }

    private VBox createTableCardProduct() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: rgba(34, 197, 94, 0.12); " +
                "-fx-background-radius: 18; " +
                "-fx-border-color: rgba(74, 222, 128, 0.30); " +
                "-fx-border-width: 2; -fx-border-radius: 18;");

        Label t = new Label("Products List");
        t.setFont(Font.font("System", FontWeight.BOLD, 18));
        t.setTextFill(Color.web("#a3e635"));

        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: rgba(239, 68, 68, 0.85);" +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;" +
                "-fx-background-radius: 10; -fx-cursor: hand;");

        backBtn.setOnAction(e -> {
            root.setCenter(createCenterC());
            CatgID = 0;
        });

        Ptable = new TableView<>();
        Ptable.getStylesheets().add(getClass().getResource("/TableCSS.css").toExternalForm());
        Ptable.setItems(Pdata);
        Ptable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Ptable.setPrefHeight(560);

        TableColumn<ProductRow, Integer> cId = new TableColumn<>("ProdID");
        cId.setCellValueFactory(new PropertyValueFactory<>("prodID"));

        TableColumn<ProductRow, String> cModel = new TableColumn<>("ProdModel");
        cModel.setCellValueFactory(new PropertyValueFactory<>("prodModel"));

        TableColumn<ProductRow, Double> cPrice = new TableColumn<>("Price");
        cPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<ProductRow, Integer> cQty = new TableColumn<>("Qty");
        cQty.setCellValueFactory(new PropertyValueFactory<>("qty"));

        TableColumn<ProductRow, Integer> cCat = new TableColumn<>("CatgID");
        cCat.setCellValueFactory(new PropertyValueFactory<>("catgID"));

        TableColumn<ProductRow, Integer> cInv = new TableColumn<>("InvID");
        cInv.setCellValueFactory(new PropertyValueFactory<>("invID"));

        TableColumn<ProductRow, String> des = new TableColumn<>("Description");
        des.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<ProductRow, Integer> rate = new TableColumn<>("rate");
        rate.setCellValueFactory(new PropertyValueFactory<>("rate"));

        Ptable.getColumns().addAll(cId, cModel, cPrice, cQty, cCat, cInv, rate, des);

        Ptable.getSelectionModel().selectedItemProperty().addListener((obs, old, row) -> {
            if (row == null) return;
            tfProdID.setText(String.valueOf(row.getProdID()));
            tfModel.setText(row.getProdModel());
            tfPrice.setText(String.valueOf(row.getPrice()));
            tfQty.setText(String.valueOf(row.getQty()));
            tfCatgID.setText(String.valueOf(row.getCatgID()));
            tfInvID.setText(String.valueOf(row.getInvID()));
            tfRate.setText(String.valueOf(row.getRate()));
            taDesc.setText(row.getDescription() == null ? "" : row.getDescription());
            tfProdImagePath.setText(row.getImagePath() == null ? "" : row.getImagePath());
        });

        HBox head = new HBox(20, t, backBtn);
        card.getChildren().addAll(head, Ptable);
        return card;
    }

    private VBox createFormCardProducts() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setPrefWidth(420);
        card.setStyle("-fx-background-color: rgba(26, 46, 26, 0.90); " +
                "-fx-background-radius: 18; " +
                "-fx-border-color: rgba(74, 222, 128, 0.30); " +
                "-fx-border-width: 2; -fx-border-radius: 18;");

        Label t = new Label("Add / Edit Product");
        t.setFont(Font.font("System", FontWeight.BOLD, 18));
        t.setTextFill(Color.web("#a3e635"));

        tfProdID = styledTF("ProdID (number)");
        tfModel = styledTF("ProdModel");
        tfPrice = styledTF("Price (e.g. 850.00)");
        tfQty = styledTF("Quantity");
        tfCatgID = styledTF("CatgID (e.g. 10)");
        tfRate= styledTF("Rate");
        tfCatgID.setText(String.valueOf(CatgID));
        tfCatgID.setEditable(false);
        tfInvID = styledTF("InvID (e.g. 100)");

        taDesc = new TextArea();
        taDesc.setPromptText("Description (optional)");
        taDesc.setWrapText(true);
        taDesc.setPrefRowCount(4);
        taDesc.setStyle(
                "-fx-control-inner-background: rgba(26, 46, 26, 0.8);" +
                        "-fx-text-fill: #e0e0e0;" +
                        "-fx-prompt-text-fill: #888;" +
                        "-fx-border-color: rgba(74, 222, 128, 0.35);" +
                        "-fx-border-radius: 10; -fx-background-radius: 10;"
        );

        tfProdImagePath = styledTF("Image Path");
        Button chooseProdImg = new Button("📁 Choose Image");
        styleGreenButton(chooseProdImg);
        chooseProdImg.setOnAction(e -> chooseImage(tfProdImagePath));

        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER);

        Button addBtn = new Button("➕ Add");
        styleGreenButton(addBtn);
        addBtn.setOnAction(e -> addProduct());

        Button updateBtn = new Button("✏ Update");
        styleGreenButton(updateBtn);
        updateBtn.setOnAction(e -> {
            updateProduct();
            loadProducts(CatgID);
        });
        Button deleteProduct=new Button("Delete");
        styleGreenButton(deleteProduct);
        deleteProduct.setOnAction(e -> deleteProduct());

        Button clearBtn = new Button("🧹 Clear");
        styleGreenButton(clearBtn);
        clearBtn.setOnAction(e -> clearForm());

        btnRow.getChildren().addAll(addBtn, updateBtn, clearBtn);

        TranslateTransition tt = new TranslateTransition(Duration.millis(400), card);
        tt.setFromY(18);
        tt.setToY(0);
        tt.play();

        card.getChildren().addAll(
                t, tfModel, tfPrice, tfQty, tfCatgID, tfInvID,tfRate,taDesc,tfProdImagePath, chooseProdImg, btnRow
        );

        return card;
    }

    // ======================= DB =======================

    private void loadUsers() {
        String sql = "SELECT UserID, UserName, Password, Role, ActiveStatus, Email FROM Users";
        Udata.clear();

        try (Connection conn = s.m.conn.connectDB();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                boolean active = rs.getBoolean("ActiveStatus");
                String status = active ? "Active" : "Inactive";

                Udata.add(new UserRow(
                        rs.getInt("UserID"),
                        rs.getString("UserName"),
                        rs.getString("Password"),
                        rs.getString("Role"),
                        status,
                        rs.getString("Email")
                ));
            }
            setMsg("Loaded " + Udata.size() + " items ✅", false);

        } catch (Exception e) {
            e.printStackTrace();
            setMsg("Load failed: " + e.getMessage(), true);
        }
    }

    private void loadCategorys() {
        String sql = "SELECT CatgID, CatgName, Description, ImagePath , isActive FROM Category";
        Cdata.clear();

        try (Connection conn = s.m.conn.connectDB();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cdata.add(new CategoryRow(
                        rs.getInt("CatgID"),
                        rs.getString("CatgName"),
                        rs.getString("Description"),
                        rs.getString("ImagePath"),
                        rs.getString("isActive")
                ));
            }
            setMsg("Loaded " + Cdata.size() + " items ✅", false);

        } catch (Exception e) {
            e.printStackTrace();
            setMsg("Load failed: " + e.getMessage(), true);
        }
    }

    private void loadProducts(int catgID) {
        String sql = "SELECT ProdID, Rate,ProdModel, Price, Quantity, CatgID, InvID, Description, ImagePath " +
                "FROM Product WHERE CatgID = ?";

        Pdata.clear();

        try (Connection conn = s.m.conn.connectDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, catgID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pdata.add(new ProductRow(
                            rs.getInt("ProdID"),
                            rs.getString("ProdModel"),
                            rs.getDouble("Price"),
                            rs.getInt("Quantity"),
                            rs.getInt("CatgID"),
                            rs.getInt("InvID"),
                            rs.getString("Description"),
                            rs.getString("ImagePath"),
                            rs.getInt("Rate")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            setMsg("Load failed: " + e.getMessage(), true);
        }

        root.setCenter(createCenterP());
    }

    private void addCategory() {
        Integer id = parseInt(tfCatgID_C.getText());
        String name = tfCatgName_C.getText().trim();
        String desc = taCatgDesc_C.getText().trim();
        String isActive=cbIsActive.getValue();

        if ( name.isEmpty()) {
            setMsg("Category Name required ❗", true);
            return;
        }

        try {
            // ✅ copy image automatically on ADD
            String savedPath = copyToProjectImages(tfCatgImagePath_C.getText());
            tfCatgImagePath_C.setText(savedPath == null ? "" : savedPath);

            String sql = "INSERT INTO Category ( CatgName, Description, ImagePath ,isActive) VALUES ( ?, ?, ?,?)";

            try (Connection conn = s.m.conn.connectDB();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setString(2, desc.isEmpty() ? null : desc);
                ps.setString(3, savedPath);
                ps.setString(4, isActive);

                ps.executeUpdate();
                setMsg("Category added ✅", false);
                loadCategorys();
                clearCategoryForm();
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            setMsg("Category ID/Name already exists ❗", true);
        } catch (Exception e) {
            e.printStackTrace();
            setMsg("Add failed: " + e.getMessage(), true);
        }
    }


    private void updateCategory() {
        Integer id = parseInt(tfCatgID_C.getText());
        String name = tfCatgName_C.getText().trim();
        String desc = taCatgDesc_C.getText().trim();
        String isActive= cbIsActive.getValue();


        if (id == null || name.isEmpty()) {
            setMsg("Select category & fill fields ❗", true);
            return;
        }

        try {
            // ✅ copy image automatically on UPDATE (if absolute path)
            String img = tfCatgImagePath_C.getText().trim();
            String savedPath = copyToProjectImages(img);
            if (savedPath != null) img = savedPath;
            tfCatgImagePath_C.setText(img);

            String sql = "UPDATE Category SET CatgName=?, Description=?, ImagePath=? ,isActive=? WHERE CatgID=?";

            try (Connection conn = s.m.conn.connectDB();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setString(2, desc.isEmpty() ? null : desc);
                ps.setString(3, img.isEmpty() ? null : img);
                ps.setInt(5, id);
                ps.setString(4, isActive);

                int updated = ps.executeUpdate();
                if (updated == 0) setMsg("Category not found ❗", true);
                else {
                    setMsg("Category updated ✅", false);
                    loadCategorys();
                    clearCategoryForm();
                }
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            setMsg("Category name duplicate ❗", true);
        } catch (Exception e) {
            e.printStackTrace();
            setMsg("Update failed: " + e.getMessage(), true);
        }
    }

    private void clearCategoryForm() {
        tfCatgID_C.clear();
        tfCatgName_C.clear();
        taCatgDesc_C.clear();
        tfCatgImagePath_C.clear();
        cbIsActive.setValue("ACTIVE");
        if (Ctable != null) Ctable.getSelectionModel().clearSelection();
        setMsg("", false);
    }

    private void addProduct() {

        Double price = parseDouble(tfPrice.getText());
        Integer qty  = parseInt(tfQty.getText());
        Integer cat  = parseInt(tfCatgID.getText());
        Integer inv  = parseInt(tfInvID.getText());
        String model = tfModel.getText().trim();
        String desc  = taDesc.getText().trim();
        Integer rate = parseInt(tfRate.getText());

        if (price == null || qty == null || cat == null || inv == null || rate == null || model.isEmpty()) {
            setMsg("Fill required fields correctly ❗", true);
            return;
        }

        try {
            String savedPath = copyToProjectImages(tfProdImagePath.getText());
            tfProdImagePath.setText(savedPath == null ? "" : savedPath);

            String sql =
                    "INSERT INTO Product (ProdModel, Rate, Price, Quantity, CatgID, InvID, Description, ImagePath) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = s.m.conn.connectDB();
                 PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

                // (1) model, (2) rate, (3) price, (4) qty, (5) cat, (6) inv, (7) desc, (8) image
                ps.setString(1, model);
                ps.setInt(2, rate);
                ps.setDouble(3, price);
                ps.setInt(4, qty);
                ps.setInt(5, cat);
                ps.setInt(6, inv);

                if (desc.isEmpty()) ps.setNull(7, java.sql.Types.VARCHAR);
                else ps.setString(7, desc);

                if (savedPath == null || savedPath.isBlank()) ps.setNull(8, java.sql.Types.VARCHAR);
                else ps.setString(8, savedPath);

                int rows = ps.executeUpdate();

                // ✅ get generated ProdID
                Integer newId = null;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) newId = rs.getInt(1);
                }

                if (rows > 0) {
                    if (newId != null) {
                        setMsg("Added ✅  (ProdID=" + newId + ")", false);
                        tfProdID.setText(String.valueOf(newId)); // لو بدك تعبيه تلقائي
                    } else {
                        setMsg("Added ✅", false);
                    }
                } else {
                    setMsg("Add failed ❗", true);
                }
            }

        } catch (SQLIntegrityConstraintViolationException fk) {
            // غالباً CatgID أو InvID مش موجودين (FK)
            setMsg("Invalid CatgID / InvID (Foreign Key) ❗", true);
        } catch (Exception e) {
            e.printStackTrace();
            setMsg("Add failed: " + e.getMessage(), true);
        }
    }

    private void deleteProduct() {
        Integer id = parseInt(tfProdID.getText());
        if (id == null) {
            setMsg("Select product to delete ❗", true);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete product permanently?\nIf product has orders, delete is blocked.",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() != ButtonType.YES) return;

        Connection conn = null;
        try {
            conn = s.m.conn.connectDB();
            conn.setAutoCommit(false);

            // block if orders exist
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Orders WHERE ProdID=?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        setMsg("Can't delete: product has orders ❗", true);
                        conn.rollback();
                        return;
                    }
                }
            }

            // delete supply first
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Supply WHERE ProdID=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // delete product
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Product WHERE ProdID=?")) {
                ps.setInt(1, id);
                int d = ps.executeUpdate();
                if (d == 0) {
                    setMsg("Product not found ❗", true);
                    conn.rollback();
                    return;
                }
            }

            conn.commit();
            setMsg("Product deleted ✅", false);
            loadProducts(CatgID);
            clearForm();

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignore) {}
            e.printStackTrace();
            setMsg("Delete failed: " + e.getMessage(), true);
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception ignore) {}
        }
    }


    private void updateProduct() {

        Integer id   = parseInt(tfProdID.getText());     // لازم عشان WHERE
        Double price = parseDouble(tfPrice.getText());
        Integer qty  = parseInt(tfQty.getText());
        Integer cat  = parseInt(tfCatgID.getText());
        Integer inv  = parseInt(tfInvID.getText());
        Integer rate = parseInt(tfRate.getText());

        String model = tfModel.getText().trim();
        String desc  = taDesc.getText().trim();

        if (id == null || price == null || qty == null || cat == null ||rate==null|| inv == null || model.isEmpty()) {
            setMsg("Select a row & fill fields ❗", true);
            return;
        }

        try {
            String img = tfProdImagePath.getText() == null ? "" : tfProdImagePath.getText().trim();

            String savedPath = copyToProjectImages(img);  // لو كان absolute بيرجع path جديد داخل المشروع
            if (savedPath != null) img = savedPath;

            tfProdImagePath.setText(img);

            String sql ="UPDATE Product SET ProdModel=?, Price=?, Quantity=?, CatgID=?, InvID=?, Description=?, ImagePath=?, Rate=? WHERE ProdID=?";

            try (Connection conn = s.m.conn.connectDB();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                // (1) model, (2) price, (3) qty, (4) cat, (5) inv, (6) desc, (7) img, (8) rate, (9) id
                ps.setString(1, model);
                ps.setDouble(2, price);
                ps.setInt(3, qty);
                ps.setInt(4, cat);
                ps.setInt(5, inv);

                if (desc.isEmpty()) ps.setNull(6, java.sql.Types.VARCHAR);
                else ps.setString(6, desc);

                if (img.isEmpty()) ps.setNull(7, java.sql.Types.VARCHAR);
                else ps.setString(7, img);

                ps.setInt(8, rate);
                ps.setInt(9, id);

                int updated = ps.executeUpdate();
                if (updated == 0) setMsg("ProdID not found ❗", true);
                else setMsg("Updated ✅", false);
            }

        } catch (java.sql.SQLIntegrityConstraintViolationException fk) {
            // غالباً CatgID أو InvID مش موجودين (Foreign Key)
            setMsg("Invalid CatgID / InvID (Foreign Key) ❗", true);
        } catch (Exception e) {
            e.printStackTrace();
            setMsg("Update failed: " + e.getMessage(), true);
        }
    }


    private void clearForm() {
        tfProdID.clear();
        tfModel.clear();
        tfPrice.clear();
        tfQty.clear();
        tfCatgID.clear();
        tfInvID.clear();
        taDesc.clear();
        tfRate.clear();
        tfProdImagePath.clear();
        if (Ptable != null) Ptable.getSelectionModel().clearSelection();
        setMsg("", false);
    }

    // ======================= Helpers =======================

    private void chooseImage(TextField target) {
        if (root == null || root.getScene() == null) {
            setMsg("Open scene first ❗", true);
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose Image");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File f = fc.showOpenDialog(root.getScene().getWindow());
        if (f != null) target.setText(f.getAbsolutePath());
    }

    private String copyToProjectImages(String pathFromField) throws Exception {
        if (pathFromField == null) return null;

        String p = pathFromField.trim().replace("\\", "/");
        if (p.isEmpty()) return null;

        // already relative saved
        if (p.startsWith("images/")) return p;

        File src = new File(p);
        if (!src.exists()) throw new Exception("Image file not found!");

        Path imagesDir = Paths.get(System.getProperty("user.dir"), "images");
        if (!Files.exists(imagesDir)) Files.createDirectories(imagesDir);

        String name = src.getName();
        String ext = "";
        int dot = name.lastIndexOf('.');
        if (dot != -1) ext = name.substring(dot);

        String newName = UUID.randomUUID().toString().replace("-", "") + ext;
        Path dest = imagesDir.resolve(newName);

        Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

        return "images/" + newName;
    }

    private TextField styledTF(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefHeight(42);
        tf.setStyle(
                "-fx-background-color: rgba(26, 46, 26, 0.8);" +
                        "-fx-text-fill: #e0e0e0;" +
                        "-fx-prompt-text-fill: #888;" +
                        "-fx-border-color: rgba(74, 222, 128, 0.35);" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 10;"
        );
        return tf;
    }

    private void styleCombo(ComboBox<String> cb) {
        cb.setStyle("-fx-background-color: rgba(26, 46, 26, 0.8);" +
                "-fx-border-color: rgba(74, 222, 128, 0.35);" +
                "-fx-prompt-text-fill: #e0e0e0;" +
                "-fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;" +
                "-fx-text-fill: #e0e0e0;");
    }


    private void styleTable(TableView<?> table) {
        table.setStyle(
                "-fx-background-color: rgba(10, 14, 10, 0.50);" +
                        "-fx-border-color: rgba(74, 222, 128, 0.35);" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;"
        );

        // Apply to all columns
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void styleGreenButton(Button b) {
        b.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 10 16; -fx-background-radius: 10; " +
                "-fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 10 16; -fx-background-radius: 10; " +
                "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(34, 197, 94, 0.6), 15, 0, 0, 5);"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 10 16; -fx-background-radius: 10; " +
                "-fx-cursor: hand;"));
    }

    private void setMsg(String text, boolean error) {
        msg.setText(text);
        msg.setTextFill(error ? Color.web("#ff6b6b") : Color.web("#a3e635"));
    }

    private Integer parseInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return null; }
    }
    private Double parseDouble(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return null; }
    }

    // ======================= Row Models (POJOs for TableView) =======================

    public static class ProductRow {
        private int prodID;
        private String prodModel;
        private double price;
        private int qty;
        private int catgID;
        private int invID;
        private String description;
        private String imagePath;
        private int rate;

        public ProductRow(int prodID, String prodModel, double price, int qty, int catgID, int invID, String description, String imagePath, int rate) {
            this.prodID = prodID;
            this.prodModel = prodModel;
            this.price = price;
            this.qty = qty;
            this.catgID = catgID;
            this.invID = invID;
            this.description = description;
            this.imagePath = imagePath;
            this.rate = rate;
        }

        public int getProdID() { return prodID; }
        public void setProdID(int prodID) { this.prodID = prodID; }

        public String getProdModel() { return prodModel; }
        public void setProdModel(String prodModel) { this.prodModel = prodModel; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public int getQty() { return qty; }
        public void setQty(int qty) { this.qty = qty; }

        public int getCatgID() { return catgID; }
        public void setCatgID(int catgID) { this.catgID = catgID; }

        public int getInvID() { return invID; }
        public void setInvID(int invID) { this.invID = invID; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getImagePath() { return imagePath; }
        public void setImagePath(String imagePath) { this.imagePath = imagePath; }

        public int getRate() { return rate; }
        public void setRate(int rate) { this.rate = rate; }
    }

    public static class CategoryRow {
        private int catgID;
        private String catgName;
        private String description;
        private String imagePath;
        private String isActive;

        public CategoryRow(int catgID, String catgName, String description, String imagePath, String isActive) {
            this.catgID = catgID;
            this.catgName = catgName;
            this.description = description;
            this.imagePath = imagePath;
            this.isActive = isActive;
        }

        public int getCatgID() { return catgID; }
        public String getCatgName() { return catgName; }
        public String getDescription() { return description; }
        public String getImagePath() { return imagePath; }
        public String getIsActive() { return isActive; }



    }

    public static class UserRow {
        private int userID;
        private String userName;
        private String pass;
        private String role;
        private String status;
        private String email;
        private double salary;


        public UserRow(int userID, String userName, String pass, String role, String status, String email) {
            this.userID = userID;
            this.userName = userName;
            this.pass = pass;
            this.role = role;
            this.status = status;
            this.email = email;
        }
        public UserRow(int userID, String userName, String pass, String role, String status, String email, double salary) {
            this.userID = userID;
            this.userName = userName;
            this.pass = pass;
            this.role = role;
            this.status = status;
            this.email = email;
        }

        public int getUserID() { return userID; }
        public void setUserID(int userID) { this.userID = userID; }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

        public String getPass() { return pass; }
        public void setPass(String pass) { this.pass = pass; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public double getSalary() { return salary; }
    }
}