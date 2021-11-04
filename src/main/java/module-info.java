module uk.co.flxs.rftool {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.opencsv;
    
    opens uk.co.flxs.rftool to javafx.graphics, javafx.fxml;
    
    exports uk.co.flxs.rftool;
}
