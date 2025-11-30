module com.psyche.potokisort {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.psyche.potokisort to javafx.fxml;
    exports com.psyche.potokisort;
}