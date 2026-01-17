package com.example.pcstore;

import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProfileHandler implements EventHandler<MouseEvent> {

    private final MainScene ms;
    private int custID;

    private TextField tfFullName;
    private TextField tfUserName;
    private PasswordField tfPassword;
    private TextField tfPhone;
    private TextField tfEmail;

    public ProfileHandler(MainScene mainScene) {
        this.ms = mainScene;
    }

    @Override
    public void handle(MouseEvent mouseEvent) {

        custID = ms.custId;
        if (custID <= 0) {
            new Alert(Alert.AlertType.ERROR, "Login as Customer first.").showAndWait();
            return;
        }

        VBox wrap = new VBox(18);
        wrap.setPadding(new Insets(20));

        // ===== Header =====
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
        back.setOnAction(e -> ms.handle(null));

        Label title = new Label("👤 My Profile");
        title.setFont(Font.font("System", FontWeight.BOLD, 30));
        title.setTextFill(Color.web("#4ade80"));
        title.setEffect(new DropShadow(16, Color.web("#4ade80", 0.35)));

        head.getChildren().addAll(back, title);

        // ===== Card =====
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setMaxWidth(560);
        card.setStyle(
                "-fx-background-color: rgba(26, 46, 26, 0.70);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: rgba(74, 222, 128, 0.30);" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 16;"
        );

        tfFullName = styledTF("Full Name");
        tfUserName = styledTF("UserName");
        tfPassword = styledPF("Password");
        tfPhone    = styledTF("Phone");
        tfEmail    = styledTF("Email");

        Button btnUpdate = new Button("✅ Update");
        btnUpdate.setMaxWidth(Double.MAX_VALUE);
        btnUpdate.setPrefHeight(42);
        btnUpdate.setStyle(
                "-fx-background-color:#22c55e;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-radius:10;" +
                        "-fx-cursor: hand;"
        );

        btnUpdate.setOnAction(e -> updateProfile());

        card.getChildren().addAll(
                labeledRow("Full Name:", tfFullName),
                labeledRow("UserName:", tfUserName),
                labeledRow("Password:", tfPassword),
                labeledRow("Phone:", tfPhone),
                labeledRow("Email:", tfEmail),
                btnUpdate
        );

        wrap.getChildren().addAll(head, card);
        ms.root.setCenter(wrap);

        TranslateTransition tt = new TranslateTransition(Duration.millis(250), wrap);
        tt.setFromY(10);
        tt.setToY(0);
        tt.play();

        // ✅ load after UI created
        loadCustomerProfile();
    }

    // ===== UI helpers =====
    private HBox labeledRow(String label, Control field) {
        Label l = new Label(label);
        l.setTextFill(Color.web("#a3e635"));
        l.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        l.setMinWidth(110);

        HBox row = new HBox(12, l, field);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private TextField styledTF(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefHeight(38);
        tf.setStyle(
                "-fx-background-color: rgba(10, 14, 10, 0.65);" +
                        "-fx-text-fill: #e0e0e0;" +
                        "-fx-prompt-text-fill: #88a;" +
                        "-fx-border-color: rgba(74, 222, 128, 0.30);" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );
        HBox.setHgrow(tf, Priority.ALWAYS);
        return tf;
    }

    private PasswordField styledPF(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setPrefHeight(38);
        pf.setStyle(
                "-fx-background-color: rgba(10, 14, 10, 0.65);" +
                        "-fx-text-fill: #e0e0e0;" +
                        "-fx-prompt-text-fill: #88a;" +
                        "-fx-border-color: rgba(74, 222, 128, 0.30);" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );
        HBox.setHgrow(pf, Priority.ALWAYS);
        return pf;
    }

    // ===== DB load =====
    private void loadCustomerProfile() {
        String sql =
                "SELECT  p.FirstName, p.SecondName,p.Phone,u.UserName, u.Password,u.Email FROM Users u JOIN Person p ON u.UserID = p.PersonID WHERE u.UserID = ?;";

        try (Connection conn = ms.m.m.conn.connectDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, custID);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    new Alert(Alert.AlertType.ERROR, "Profile not found for ID=" + custID).showAndWait();
                    return;
                }

                String fullName = safe(rs.getString("FirstName")) + " " + safe(rs.getString("SecondName"));
                tfFullName.setText(fullName.trim());
                tfPhone.setText(safe(rs.getString("Phone")));
                tfEmail.setText(safe(rs.getString("Email")));
                tfUserName.setText(safe(rs.getString("UserName")));
                tfPassword.setText(safe(rs.getString("Password")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Load failed: " + e.getMessage()).showAndWait();
        }
    }

    // ===== Update =====
    private void updateProfile() {

        String full = tfFullName.getText().trim();
        String first = full, second = "";
        if (full.contains(" ")) {
            int idx = full.indexOf(' ');
            first = full.substring(0, idx).trim();
            second = full.substring(idx + 1).trim();
        }

        String updPerson = "UPDATE Person SET FirstName=?, SecondName=?, Phone=? WHERE PersonID=?";
        String updUser   = "UPDATE Users SET UserName=?, Password=?, Email=? WHERE UserID=?";

        try (Connection conn = ms.m.m.conn.connectDB()) {
            conn.setAutoCommit(false);

            // ✅ update Person (NO email here)
            try (PreparedStatement ps1 = conn.prepareStatement(updPerson)) {
                ps1.setString(1, first);
                ps1.setString(2, second);
                ps1.setString(3, tfPhone.getText().trim());
                ps1.setInt(4, custID);
                ps1.executeUpdate();
            }

            // ✅ update Users (email here)
            try (PreparedStatement ps2 = conn.prepareStatement(updUser)) {
                ps2.setString(1, tfUserName.getText().trim());
                ps2.setString(2, tfPassword.getText().trim());
                ps2.setString(3, tfEmail.getText().trim());
                ps2.setInt(4, custID);
                ps2.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);

            new Alert(Alert.AlertType.INFORMATION, "Profile updated ✅").showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Update failed: " + e.getMessage()).showAndWait();
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
