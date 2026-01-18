module medicalrecord {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafaker;

    opens medicalrecord to javafx.fxml, org.junit.platform.commons;
    exports medicalrecord;
}