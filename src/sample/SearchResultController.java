package sample;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.searchers.ImageSearchHits;
import org.apache.lucene.index.IndexReader;

import java.io.FileInputStream;
import java.io.IOException;

public class SearchResultController {

    private ImageSearchHits resultHits;
    private IndexReader ir;

    @FXML private VBox resultVBox;

    public void setResultHits(ImageSearchHits resultHits) {
        this.resultHits = resultHits;
    }

    public void setIndexReader(IndexReader indexReader) {
        this.ir = indexReader;
    }

    public void loadImages() throws IOException {
        HBox header = new HBox();
        Label rank = new Label("Rank");
        rank.setPrefWidth(50);
        rank.setAlignment(Pos.CENTER);
        rank.setStyle("-fx-border-color: black;");
        header.getChildren().add(rank);

        Label imageLabel = new Label("Image");
        imageLabel.setPrefWidth(200);
        imageLabel.setAlignment(Pos.CENTER);
        imageLabel.setStyle("-fx-border-color: black;");
        header.getChildren().add(imageLabel);

        Label scoreLabel = new Label("Score");
        scoreLabel.setPrefWidth(300);
        scoreLabel.setAlignment(Pos.CENTER);
        scoreLabel.setStyle("-fx-border-color: black;");
        header.getChildren().add(scoreLabel);

        resultVBox.getChildren().add(header);

        for(int i = 0; i < resultHits.length(); i++){
            HBox entry = new HBox();

            Label data = new Label();
            data.setText(String.valueOf(i + 1));
            data.setPrefSize(50,200);
            data.setAlignment(Pos.CENTER);
            data.setStyle("-fx-border-color: black;");
            entry.getChildren().add(data);

            String filename = ir.document(resultHits.documentID(i)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
            FlowPane pane = new FlowPane();
            Image image = new Image(new FileInputStream(filename));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(200);
            imageView.setFitWidth(200);
            imageView.setPreserveRatio(true);
            imageView.setStyle("-fx-border-color: black;");
            pane.getChildren().add(imageView);
            pane.setStyle("-fx-border-color: black;");
            pane.setPrefSize(200,200);
            entry.getChildren().add(pane);

            Label score = new Label();
            score.setText(String.valueOf(resultHits.score(i)));
            score.setPrefSize(300,200);
            score.setAlignment(Pos.CENTER);
            score.setStyle("-fx-border-color: black;");
            entry.getChildren().add(score);

            resultVBox.getChildren().add(entry);
        }
    }

    @FXML
    private void initialize(){

    }
}
