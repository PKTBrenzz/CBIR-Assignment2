package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.NumberStringConverter;
import net.semanticmetadata.lire.imageanalysis.features.global.AutoColorCorrelogram;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.imageanalysis.features.global.FCTH;
import net.semanticmetadata.lire.searchers.ImageSearchHits;

import javax.xml.soap.Text;
import java.io.File;
import java.io.IOException;
import java.util.function.UnaryOperator;

public class Controller {

    //Select image directory button
    @FXML private Button selectImgDirButton;

    //Image directory text field
    @FXML private TextField imgDirTextField;

    //Start indexing button
    @FXML private Button startIndexButton;

    //Selected image path text field
    @FXML private TextField imagePathTextField;

    //Select image button
    @FXML private Button selectImgButton;

    //Start seraching button
    @FXML private Button startSearchButton;

    //Type of IndexSeacher choicebox
    @FXML private ChoiceBox typeSearcherChoiceBox;

    //Number of search results text field
    @FXML private TextField numberSearchTextField;

    @FXML private void initialize(){
        typeSearcherChoiceBox.getItems().add("CEDD");
        typeSearcherChoiceBox.getItems().add("FCTH");
        typeSearcherChoiceBox.getItems().add("AutoColorCorrelogram");
        typeSearcherChoiceBox.getItems().add("SimpleColorHistogram");
        typeSearcherChoiceBox.getItems().add("EdgeHistogram");

        typeSearcherChoiceBox.setValue("CEDD");

        System.out.println(typeSearcherChoiceBox.getValue());

        numberSearchTextField.setText("15");

        numberSearchTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!newValue.matches("\\d*")){
                    numberSearchTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    @FXML
    private void selectImgDirButton_pressed() throws IOException {
        Stage stage = (Stage)selectImgDirButton.getScene().getWindow();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select image directory...");
        directoryChooser.setInitialDirectory(new File("C://"));
        File selectedDir = directoryChooser.showDialog(stage);

        if (selectedDir != null){
            imgDirTextField.setText(selectedDir.getCanonicalPath());
        }
    }

    @FXML
    private void startIndexButton_pressed() throws IOException {
        Indexer.startIndex(imgDirTextField.getText());
    }

    @FXML
    private void selectImgButton_pressed() throws IOException {
        Stage stage = (Stage)selectImgButton.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image...");
        fileChooser.setInitialDirectory(new File("C://"));
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("JPG, PNG & GIF Images", "*.jpg", "*.png", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(stage);

        if(selectedFile != null){
            imagePathTextField.setText(selectedFile.getCanonicalPath());
        }
    }

    @FXML
    private void startSearchButton_pressed() throws IOException {
        ImageSearchHits resultHits = Searcher.startSearch(imagePathTextField.getText(), typeSearcherChoiceBox.getValue().toString(), Integer.parseInt(numberSearchTextField.getText()));
    }
}
