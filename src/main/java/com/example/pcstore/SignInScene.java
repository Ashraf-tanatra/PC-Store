package com.example.pcstore;

import javafx.animation.*;
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
import javafx.util.Duration;

import java.sql.*;

public class SignInScene implements EventHandler<ActionEvent> {

    private BorderPane root;

    String role = "";
    String name = "";

    // Sign In
    private TextField siEmail;
    private PasswordField siPassword;

    // Sign Up
    private TextField suName1;
    private TextField suName2;
    private TextField phone;
    private TextField suEmail;     // (هنا بنستخدمه كـ UserName)
    private ComboBox<String> gender;
    private PasswordField suPassword;
    private PasswordField suConfirm;

    private VBox formContainer;
    private Button tabSignIn;
    private Button tabSignUp;
    private Label errorLabel;

     Main m;

    // User Information
    public int currentPersonId = -1;
    public int currentCustId = -1;
    public int currentUserId = -1;
    public String currentUserName = "";

    public SignInScene(Main m) {
        this.m = m;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        buildUI();
        m.root.setCenter(root);
    }

    void buildUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0a0e0a, #1a2e1a);");

        root.setTop(createHeader());

        StackPane center = new StackPane();
        center.setPadding(new Insets(40));
        root.setCenter(center);

        VBox authCard = new VBox(20);
        authCard.setAlignment(Pos.TOP_CENTER);
        authCard.setPadding(new Insets(40));
        authCard.setMaxWidth(540);
        authCard.setStyle(
                "-fx-background-color: rgba(34, 197, 94, 0.08);" +
                        "-fx-background-radius: 24;" +
                        "-fx-border-color: rgba(74, 222, 128, 0.3);" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 24;" +
                        "-fx-effect: dropshadow(gaussian, rgba(74, 222, 128, 0.2), 40, 0, 0, 12);"
        );

        VBox titleBox = new VBox(8);
        titleBox.setAlignment(Pos.CENTER);

        Label title = new Label("Welcome to TechVault");
        title.setFont(Font.font("System", FontWeight.BOLD, 36));
        title.setTextFill(Color.web("#4ade80"));

        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#4ade80", 0.4));
        glow.setRadius(20);
        title.setEffect(glow);

        Label sub = new Label("Secure access to your tech ecosystem");
        sub.setFont(Font.font("System", FontWeight.NORMAL, 14));
        sub.setTextFill(Color.web("#a3e635", 0.85));

        titleBox.getChildren().addAll(title, sub);

        HBox tabs = new HBox(16);
        tabs.setAlignment(Pos.CENTER);
        tabs.setPadding(new Insets(10, 0, 10, 0));

        tabSignIn = new Button("Sign In");
        tabSignUp = new Button("Sign Up");

        styleTab(tabSignIn, true);
        styleTab(tabSignUp, false);

        tabSignIn.setOnAction(e -> switchToSignIn());
        tabSignUp.setOnAction(e -> switchToSignUp());

        tabs.getChildren().addAll(tabSignIn, tabSignUp);

        errorLabel = new Label();
        errorLabel.setTextFill(Color.web("#ef4444"));
        errorLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        errorLabel.setVisible(false);
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(450);
        errorLabel.setAlignment(Pos.CENTER);

        formContainer = new VBox(16);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(10, 0, 0, 0));
        formContainer.getChildren().setAll(createSignInForm());

        authCard.getChildren().addAll(titleBox, tabs, errorLabel, formContainer);

        animateCardEntrance(authCard);
        center.getChildren().add(authCard);
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(20, 35, 20, 35));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
                "-fx-background-color: linear-gradient(to right, rgba(26, 61, 26, 0.95), rgba(13, 38, 13, 0.95));" +
                        "-fx-effect: dropshadow(gaussian, rgba(34, 139, 34, 0.4), 25, 0, 0, 5);"
        );

        Label logo = new Label("⚡ TechVault");
        logo.setFont(Font.font("System", FontWeight.BOLD, 28));
        logo.setTextFill(Color.web("#4ade80"));

        DropShadow logoGlow = new DropShadow();
        logoGlow.setColor(Color.web("#4ade80", 0.3));
        logoGlow.setRadius(15);
        logo.setEffect(logoGlow);

        header.setAlignment(Pos.TOP_LEFT);
        header.getChildren().addAll(logo);
        return header;
    }

    private void styleTab(Button b, boolean active) {
        if (active) {
            b.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #22c55e, #16a34a);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 14;" +
                            "-fx-padding: 12 28;" +
                            "-fx-background-radius: 14;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, rgba(34, 197, 94, 0.4), 15, 0, 0, 4);"
            );
        } else {
            b.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: #a3e635;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 14;" +
                            "-fx-padding: 12 28;" +
                            "-fx-background-radius: 14;" +
                            "-fx-border-color: rgba(74, 222, 128, 0.25);" +
                            "-fx-border-width: 1.5;" +
                            "-fx-border-radius: 14;" +
                            "-fx-cursor: hand;"
            );
        }

        b.setOnMouseEntered(e -> {
            if (!isActiveTab(b)) {
                b.setStyle(
                        "-fx-background-color: rgba(34, 197, 94, 0.15);" +
                                "-fx-text-fill: #4ade80;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 14;" +
                                "-fx-padding: 12 28;" +
                                "-fx-background-radius: 14;" +
                                "-fx-border-color: rgba(74, 222, 128, 0.5);" +
                                "-fx-border-width: 1.5;" +
                                "-fx-border-radius: 14;" +
                                "-fx-cursor: hand;"
                );
            }
        });

        b.setOnMouseExited(e -> {
            if (!isActiveTab(b)) styleTab(b, false);
        });
    }

    private boolean isActiveTab(Button b) {
        String s = b.getStyle();
        return s != null && s.contains("linear-gradient");
    }

    private VBox createSignInForm() {
        VBox box = new VBox(14);
        box.setAlignment(Pos.CENTER);

        siEmail = styledTextField("Username", false, "👤");
        siPassword = (PasswordField) styledTextField("Password", true, "🔒");

        Button signInBtn = primaryButton("Sign In");
        signInBtn.setOnAction(e -> handleSignIn());

        Label hint = new Label("Don't have an account yet?");
        hint.setTextFill(Color.web("#a3e635", 0.7));
        hint.setFont(Font.font("System", 12));

        box.getChildren().addAll(siEmail, siPassword, signInBtn, hint);
        return box;
    }

    private VBox createSignUpForm() {
        VBox box = new VBox(14);
        box.setAlignment(Pos.CENTER);

        suName1 = styledTextField("First Name", false, "👤");
        suName2 = styledTextField("Second Name", false, "👤");
        suEmail = styledTextField("Username (or email)", false, "📧"); // نخزنه كـ UserName
        gender  = new ComboBox<>();
        gender.getItems().addAll("Male", "Female");
        gender.setPromptText("Gender" + "  " + "⚧");
        gender.setStyle("-fx-background-color: rgba(26, 46, 26, 0.6);" +
                "-fx-text-fill: #e8e8e8;" +
                "-fx-prompt-text-fill: rgba(163, 230, 53, 0.5);" +
                "-fx-border-color: rgba(74, 222, 128, 0.3);" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;" +
                "-fx-padding: 14;" +
                "-fx-font-size: 14;");
        gender.setPrefWidth(450);
        gender.setPrefHeight(50);
        phone   = styledTextField("Phone", false, "📞");
        suPassword = (PasswordField) styledTextField("Password", true, "🔒");
        suConfirm  = (PasswordField) styledTextField("Confirm password", true, "🔒");

        Button createBtn = primaryButton("Create Account");
        createBtn.setOnAction(e -> handleSignUp());

        Label hint = new Label("Already have an account?");
        hint.setTextFill(Color.web("#a3e635", 0.7));
        hint.setFont(Font.font("System", 12));

        box.getChildren().addAll(suName1, suName2, suEmail, gender, phone, suPassword, suConfirm, createBtn, hint);
        return box;
    }

    private TextField styledTextField(String prompt, boolean password, String icon) {
        TextField tf = password ? new PasswordField() : new TextField();
        tf.setPromptText(icon + "  " + prompt);
        tf.setPrefWidth(450);
        tf.setPrefHeight(50);

        String base =
                "-fx-background-color: rgba(26, 46, 26, 0.6);" +
                        "-fx-text-fill: #e8e8e8;" +
                        "-fx-prompt-text-fill: rgba(163, 230, 53, 0.5);" +
                        "-fx-border-color: rgba(74, 222, 128, 0.3);" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 14;" +
                        "-fx-background-radius: 14;" +
                        "-fx-padding: 14;" +
                        "-fx-font-size: 14;";

        String focused =
                "-fx-background-color: rgba(26, 46, 26, 0.85);" +
                        "-fx-text-fill: #e8e8e8;" +
                        "-fx-prompt-text-fill: rgba(163, 230, 53, 0.5);" +
                        "-fx-border-color: #4ade80;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 14;" +
                        "-fx-background-radius: 14;" +
                        "-fx-padding: 14;" +
                        "-fx-font-size: 14;" +
                        "-fx-effect: dropshadow(gaussian, rgba(74, 222, 128, 0.3), 20, 0, 0, 8);";

        tf.setStyle(base);

        tf.focusedProperty().addListener((obs, oldV, newV) -> {
            if (newV) {
                tf.setStyle(focused);
                animateFieldFocus(tf);
            } else {
                tf.setStyle(base);
            }
        });

        return tf;
    }

    private Button primaryButton(String text) {
        Button b = new Button(text);
        b.setPrefWidth(450);
        b.setPrefHeight(50);
        b.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #22c55e, #16a34a);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 15;" +
                        "-fx-padding: 14;" +
                        "-fx-background-radius: 14;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(34, 197, 94, 0.3), 15, 0, 0, 5);"
        );

        b.setOnMouseEntered(e -> {
            b.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #16a34a, #15803d);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 15;" +
                            "-fx-padding: 14;" +
                            "-fx-background-radius: 14;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, rgba(34, 197, 94, 0.6), 20, 0, 0, 8);"
            );
            animateButtonHover(b, true);
        });

        b.setOnMouseExited(e -> {
            b.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #22c55e, #16a34a);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 15;" +
                            "-fx-padding: 14;" +
                            "-fx-background-radius: 14;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, rgba(34, 197, 94, 0.3), 15, 0, 0, 5);"
            );
            animateButtonHover(b, false);
        });

        return b;
    }

    // ===================== LOGIC =====================

    private void handleSignIn() {
        String username = siEmail.getText().trim();
        String password = siPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please fill in all fields", true);
            return; // مهم
        }

        if (authenticateUser(username, password)) {
            showMessage("Sign in successful!", false);

            if ("CUST".equals(role)) {
                MainScene ms = new MainScene(this);
                ms.handle(null);
                m.root.setCenter(ms.getRoot());

            } else if ("EMP".equals(role) || "ADMIN".equals(role)) {
                EmployeScene e = new EmployeScene(this);
                e.handle(null);
                m.root.setCenter(e.getRoot());
                name = username;
            }

        } else {
            showMessage("Invalid username or password!", true);
        }
    }

    private boolean authenticateUser(String username, String password) {
        String sql = "SELECT UserID, PersonID, Password, Role FROM Users WHERE UserName = ? LIMIT 1";

        try (Connection conn = m.conn.connectDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;

                currentUserId = rs.getInt("UserID");
                currentPersonId = rs.getInt("PersonID");
                currentUserName = username;

                String dbPassword = rs.getString("Password");
                role = rs.getString("Role");

                if ("CUST".equals(role)) {
                    currentCustId = currentPersonId;
                } else {
                    currentCustId = -1;
                }

                return dbPassword != null && dbPassword.equals(password);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("DB Error: " + e.getMessage(), true);
            return false;
        }
    }

    private void handleSignUp() {
        String name1 = suName1.getText().trim();
        String name2 = suName2.getText().trim();
        String username = suEmail.getText().trim();
        String ph = phone.getText().trim();

        String gen = (gender.getValue() == null) ? null : gender.getValue().trim();

        String pass = suPassword.getText();
        String conf = suConfirm.getText();

        if (name1.isEmpty() || name2.isEmpty() || username.isEmpty() || pass.isEmpty() || conf.isEmpty()) {
            showMessage("Please fill in all fields", true);
            return;
        }

        if (!pass.equals(conf)) {
            showMessage("Passwords do not match", true);
            shakeField(suConfirm);
            return;
        }

        String check = "SELECT 1 FROM Users WHERE UserName = ? LIMIT 1";
        String insPerson = "INSERT INTO Person(FirstName, SecondName, Gender, Phone) VALUES (?, ?, ?, ?)";
        String insUser = "INSERT INTO Users(PersonID, UserName, Password, Role, ActiveStatus) VALUES (?, ?, ?, 'CUST', TRUE)";
        String insCust = "INSERT INTO Customer(CustID, LastPurchaseDate) VALUES (?, NULL)";

        try (Connection conn = m.conn.connectDB()) {
            conn.setAutoCommit(false);

            // 1) check username
            try (PreparedStatement ps = conn.prepareStatement(check)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        conn.rollback();
                        showMessage("Username already exists!", true);
                        return;
                    }
                }
            }

            // 2) insert person + get generated key
            int newPersonId;
            try (PreparedStatement ps = conn.prepareStatement(insPerson, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name1);
                ps.setString(2, name2);

                if (gen == null || gen.isEmpty()) ps.setNull(3, Types.VARCHAR);
                else ps.setString(3, gen);

                if (ph.isEmpty()) ps.setNull(4, Types.VARCHAR);
                else ps.setString(4, ph);

                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        conn.rollback();
                        showMessage("Failed to create person.", true);
                        return;
                    }
                    newPersonId = keys.getInt(1);
                }
            }

            // 3) insert user
            try (PreparedStatement ps = conn.prepareStatement(insUser)) {
                ps.setInt(1, newPersonId);
                ps.setString(2, username);
                ps.setString(3, pass);
                ps.executeUpdate();
            }

            // 4) insert customer (IMPORTANT ✅)
            try (PreparedStatement ps = conn.prepareStatement(insCust)) {
                ps.setInt(1, newPersonId);
                ps.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);

            showMessage("Account created ✅ Now sign in.", false);

            // clear fields
            suName1.clear();
            suName2.clear();
            suEmail.clear();
            phone.clear();
            gender.setValue(null); // ✅ مش clear items
            suPassword.clear();
            suConfirm.clear();

            switchToSignIn();

        } catch (Exception e) {
            try { m.conn.connectDB().rollback(); } catch (Exception ignore) {}
            e.printStackTrace();
            showMessage("DB Error: " + e.getMessage(), true);
        }
    }

    // ===================== UI Helpers =====================

    private void showMessage(String message, boolean isError) {
        errorLabel.setText(message);
        errorLabel.setTextFill(isError ? Color.web("#ef4444") : Color.web("#4ade80"));
        errorLabel.setVisible(true);
    }

    private void animateCardEntrance(VBox card) {
        card.setOpacity(0);
        card.setTranslateY(30);

        FadeTransition fade = new FadeTransition(Duration.millis(600), card);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(600), card);
        slide.setFromY(30);
        slide.setToY(0);

        ParallelTransition parallel = new ParallelTransition(fade, slide);
        parallel.setDelay(Duration.millis(100));
        parallel.play();
    }

    private void animateFieldFocus(TextField field) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(150), field);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.02);
        scale.setToY(1.02);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);
        scale.play();
    }

    private void animateButtonHover(Button button, boolean enter) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
        scale.setToX(enter ? 1.03 : 1.0);
        scale.setToY(enter ? 1.03 : 1.0);
        scale.play();
    }

    private void shakeField(TextField field) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), field);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }

    private void switchToSignIn() {
        styleTab(tabSignIn, true);
        styleTab(tabSignUp, false);
        errorLabel.setVisible(false);
        animateFormSwap(createSignInForm());
    }

    private void switchToSignUp() {
        styleTab(tabSignIn, false);
        styleTab(tabSignUp, true);
        errorLabel.setVisible(false);
        animateFormSwap(createSignUpForm());
    }

    private void animateFormSwap(VBox newForm) {
        FadeTransition out = new FadeTransition(Duration.millis(200), formContainer);
        out.setFromValue(1);
        out.setToValue(0);

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(200), formContainer);
        slideOut.setByX(-20);

        ParallelTransition exitAnim = new ParallelTransition(out, slideOut);

        exitAnim.setOnFinished(e -> {
            formContainer.getChildren().setAll(newForm);
            formContainer.setTranslateX(20);

            FadeTransition in = new FadeTransition(Duration.millis(250), formContainer);
            in.setFromValue(0);
            in.setToValue(1);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(250), formContainer);
            slideIn.setToX(0);

            ParallelTransition enterAnim = new ParallelTransition(in, slideIn);
            enterAnim.play();
        });

        exitAnim.play();
    }

    public BorderPane getRoot() {
        return root;
    }
}
