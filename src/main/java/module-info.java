module uk.co.flxs.rftool {
    requires javafx.controls;
    requires com.opencsv;
    
    opens uk.co.flxs.rftool to javafx.graphics;
    
    exports uk.co.flxs.rftool;
}
