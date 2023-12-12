module OOS.bank {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    requires javafx.graphics;

    opens bank;
    opens bank.exceptions;
    exports bank.exceptions;
    exports bank;
    opens com.oos.bank to javafx.fxml;
    exports com.oos.bank;
}