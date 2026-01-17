module com.example.pcstore {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
//    requires com.example.pcstore;
    requires javafx.base;


    opens com.example.pcstore to javafx.fxml;
    exports com.example.pcstore;
}