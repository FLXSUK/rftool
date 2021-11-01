package uk.co.flxs.rftool;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application
{
    Label statusMessage;
    Button btn;
    
    @Override
    public void start(Stage stage) {
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();

        statusMessage = new Label("When you click the \"Convert File\" button below, fist select a source file, then a destination file and the conversion will be performed.");
        statusMessage.wrapTextProperty().set(true);
        
        var fileChooser = new FileChooser();
        
        btn = new Button("Convert File...");
        btn.getStyleClass().add("primaryAction");
        btn.setOnAction( e -> {
            btn.setText("Please wait...");
            btn.setDisable(true);
            
            File source = fileChooser.showOpenDialog(stage);
            File destination = fileChooser.showSaveDialog(stage);
            convertCSVFile(source, destination);
        });
        
        var root = new VBox(statusMessage, btn);
        root.setAlignment(Pos.BOTTOM_CENTER);
        root.setPadding(new Insets(10,10,10,10));
        root.setSpacing(30);
        
        var scene = new Scene(root, 520, 260);
        scene.getStylesheets().add(getClass()
             .getResource("css/base.css")
             .toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private void convertSuccess() {
        btn.setDisable(false);
        btn.setText("Convert File...");
        statusMessage.setText("Converson completed!");
    }
    
    private void convertFailed(String message) {
        btn.setDisable(false);
        btn.setText("Convert File...");
        statusMessage.setText("Converson failed! \n" + message);
    }
    
    private void convertCSVFile(File inputFile, File outputFile) {
        Map<String, Integer> totals = new HashMap<>();
        Map<String, String> names = new HashMap<>();
        
        try (
                CSVReader reader = new CSVReader(new FileReader(inputFile));
                CSVWriter writer = new CSVWriter(new FileWriter(outputFile))
        ) {
            List<String[]> r = reader.readAll();
            r.forEach(item -> {
                try {
                    Integer total;
                    if (totals.containsKey(item[0])) {
                        total = totals.get(item[0]);
                        total += Integer.parseInt(item[2]);
                        totals.replace(item[0], total);
                    } else {
                        total = 0;
                        total += Integer.parseInt(item[2]);
                        totals.put(item[0], total);
                        names.put(item[0], item[1]);
                    }
                } catch (Exception e) {}
            });
            
            writer.writeNext(new String[]{"product_id","name","qty"});
            for(String key : totals.keySet()) {
                String[] row = new String[] {key, names.get(key), totals.get(key).toString()};
                writer.writeNext(row);
            }
            convertSuccess();
            
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            convertFailed(ex.getMessage());
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            convertFailed(ex.getMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
            convertFailed(ex.getMessage());
        } catch (CsvException ex) {
            ex.printStackTrace();
            convertFailed(ex.getMessage());
        }
    }
}
