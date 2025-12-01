module com.psyche.xml {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;

    opens com.psyche.xml to javafx.fxml;
    exports com.psyche.xml;
}