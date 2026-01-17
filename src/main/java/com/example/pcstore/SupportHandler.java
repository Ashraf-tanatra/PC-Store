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

public class SupportHandler implements EventHandler<MouseEvent> {

    private final MainScene ms;

    public SupportHandler(MainScene ms) {
        this.ms = ms;
    }

    @Override
    public void handle(MouseEvent mouseEvent) {

        VBox wrap = new VBox(20);
        wrap.setPadding(new Insets(25));

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

        Label title = new Label("🛠 Support Center");
        title.setFont(Font.font("System", FontWeight.BOLD, 30));
        title.setTextFill(Color.web("#4ade80"));
        title.setEffect(new DropShadow(16, Color.web("#4ade80", 0.35)));

        head.getChildren().addAll(back, title);

        // ===== Card =====
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setMaxWidth(600);
        card.setStyle(
                "-fx-background-color: rgba(26, 46, 26, 0.70);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: rgba(74, 222, 128, 0.30);" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 16;"
        );

        Label info = new Label(
                "Need help? Contact us or send your issue below.\n" +
                        "We usually respond within 24 hours."
        );
        info.setTextFill(Color.web("#a3e635"));
        info.setFont(Font.font("System", 14));

        // ===== Contact Info =====
        Label name1= styled("Ashraf Tanatra ");
        Label name2= styled("Ihab Fawaqa");
        Label phone1 = styled("📞 Phone: 0592-742-707");
        Label phone2 = styled("📞 Phone: 0528-061-129");
        Label email = styled("📧 Email: support@techvault.com");
        Label hours = styled("⏰ Working Hours: 9 AM - 6 PM");

        HBox hb1 = new HBox(12);
        hb1.setAlignment(Pos.CENTER_LEFT);
        hb1.getChildren().addAll(name1,  phone1);
        HBox hb2 = new HBox(12);
        hb2.setAlignment(Pos.CENTER_LEFT);
        hb2.getChildren().addAll(name2,  phone2);

        // ===== Form =====
        TextField tfSubject = new TextField();
        tfSubject.setPromptText("Subject");
        styleField(tfSubject);

        TextArea taMessage = new TextArea();
        taMessage.setPromptText("Describe your problem...");
        taMessage.setPrefRowCount(5);
        styleField(taMessage);

        Button send = new Button("📨 Send Message");
        send.setPrefHeight(42);
        send.setMaxWidth(Double.MAX_VALUE);
        send.setStyle(
                "-fx-background-color: #22c55e;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-cursor: hand;"
        );

        send.setOnAction(e -> {
            if (tfSubject.getText().isBlank() || taMessage.getText().isBlank()) {
                new Alert(Alert.AlertType.WARNING,
                        "Please fill subject and message").showAndWait();
                return;
            }

            // لاحقًا نربطه DB / Email
            new Alert(Alert.AlertType.INFORMATION,
                    "Message sent successfully ✅\nSupport will contact you soon.")
                    .showAndWait();

            tfSubject.clear();
            taMessage.clear();
        });

        card.getChildren().addAll(
                info,
                new Separator(),
                hb1,hb2, email, hours,
                new Separator(),
                tfSubject, taMessage, send
        );

        wrap.getChildren().addAll(head, card);

        ms.root.setCenter(wrap);

        TranslateTransition tt = new TranslateTransition(Duration.millis(250), wrap);
        tt.setFromY(15);
        tt.setToY(0);
        tt.play();
    }

    // ===== Helpers =====
    private Label styled(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.web("#a3e635"));
        l.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        return l;
    }

    private void styleField(Control c) {
        c.setStyle(
                "-fx-background-color: rgba(26, 46, 26, 0.85);" +
                        "-fx-text-fill: #e0e0e0;" +
                        "-fx-prompt-text-fill: #888;" +
                        "-fx-border-color: #22c55e;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );
    }
}
