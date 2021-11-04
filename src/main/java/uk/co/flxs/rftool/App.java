package uk.co.flxs.rftool;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application implements Initializable
{
    @FXML
    Button selectSource;
    
    @FXML
    Button saveFile;
    
    @FXML
    Label statusText;
    
    File sourceFile;
    List<OrderItem> sourceList;
    
    File convertedFile;
    Map<String, OrderItem> convertedList;
    
    Stage stage;
    FileChooser fileChooser;
    
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        

        var scene = this.initScene();
        scene.getStylesheets().add(getClass()
             .getResource("css/base.css")
             .toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private Scene initScene() throws Exception
    {
        var logo = new Image(getClass().getResourceAsStream("logo.png"));
        Parent root = this.loadFXML("order-data-convert.fxml");
        var scene = new Scene(root, 640, 480);
        return scene;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.fileChooser = new FileChooser();
    }
    
    @FXML
    private void onSelectSource() {
        sourceFile = fileChooser.showOpenDialog(stage);
        loadSource(sourceFile);
    }
    
    @FXML
    private void onSaveFile() {
        var originalName = this.sourceFile.getName();
        var lastDot = originalName.lastIndexOf(".");
        var convertedName = 
                originalName.substring(0, lastDot) + 
                "_CONVERTED" + 
                originalName.substring(lastDot);
        fileChooser.setInitialDirectory(this.sourceFile.getParentFile());
        fileChooser.setInitialFileName(convertedName);
        convertedFile = fileChooser.showSaveDialog(stage);
        saveConvertedFile(convertedFile);
    }
    
    private static Parent loadFXML(String fxmlFilename) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxmlFilename));
        return fxmlLoader.load();
    }
    
    private void saveSuccess() {
        saveFile.setDisable(true);
        statusText.setText("Converted file saved successfully!");
        statusText.setTextFill(Color.GREEN);
    }
    
    private void convertSuccess() {
        saveFile.setDisable(false);
        statusText.setText("Source file loaded successfully!");
        statusText.setTextFill(Color.BLUE);
    }
    
    private void convertFailed(String message) {
        saveFile.setDisable(true);
        statusText.setText("Conversion failed!\n(" + message + ")");
        statusText.setTextFill(Color.RED);
    }
    
    private void loadSource(File sourceFile) {
        try (
            CSVReader reader = new CSVReader(new FileReader(sourceFile));
        ){
            sourceList = new ArrayList<>();
            List<String[]> r = reader.readAll();
            r.forEach(row -> {
                try {
                    OrderItem item = new OrderItem(row[0], row[1], Integer.parseInt(row[2]));
                    sourceList.add(item);
                } catch (NumberFormatException e) {
                    // Ignore rows where qty cannot be converted to an int.
                }
            });
            
            rollUp();
            convertSuccess();
        } catch (NullPointerException ex) {
            convertFailed("No source file selected");
        } catch (FileNotFoundException ex) {
            convertFailed("Could not open source file");
        } catch (IOException ex) {
            convertFailed("Failed to read selected source file");
        } catch (CsvException ex) {
            convertFailed("Badly formatted CSV data in source file");
        }
    }
    
    private void rollUp() {
        this.convertedList = new HashMap();
        this.sourceList.forEach(item -> {
            if (this.convertedList.containsKey(item.getSku())) {
                this.convertedList.get(item.getSku()).increment(item.getQty());
            } else {
                this.convertedList.put(item.getSku(), new OrderItem(
                        item.getSku(), 
                        item.getName(), 
                        item.getQty()));
            }
        });
    }
    
    private void saveConvertedFile(File convertedFile) {
        try (
            CSVWriter writer = new CSVWriter(new FileWriter(convertedFile))
        ) {
            writer.writeNext(new String[]{"product_id","name","qty"});
            this.convertedList.forEach((key, item) -> {
                writer.writeNext(item.asRow());
            });
            saveSuccess();
        } catch (NullPointerException ex) {
            convertFailed("No output file selected");
        } catch (FileNotFoundException ex) {
            convertFailed("Could not open output file");
        } catch (IOException ex) {
            convertFailed("Failed to write to selected output file");
        }
    }


}
