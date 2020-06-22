package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.global.*;
import net.semanticmetadata.lire.searchers.GenericFastImageSearcher;
import net.semanticmetadata.lire.searchers.ImageSearchHits;
import net.semanticmetadata.lire.searchers.ImageSearcher;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Searcher {
    public static ImageSearchHits startSearch(String selectedImage, String type, int noResults) throws IOException {
        //Check if the image path is valid or not.
        BufferedImage image;
        if(selectedImage.length() <= 0){
            return null;
        }

        //Read the image path and get the image info
        image = ImageIO.read(new File(selectedImage));

        //Locate the index folder and read them.
        IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get("index")));
        //Create image searcher based on the CEDD extractor that produce a certain number of results
        ImageSearcher imageSearcher = new GenericFastImageSearcher(noResults, CEDD.class);
        if(type.equals("CEDD")){
            imageSearcher = new GenericFastImageSearcher(noResults, CEDD.class);
        }else if(type.equals("FCTH")){
            //FCTH extractor
            imageSearcher = new GenericFastImageSearcher(noResults, FCTH.class);
        }else if(type.equals("AutoColorCorrelogram")){
            //Auto Color Correlogram extractor
            imageSearcher = new GenericFastImageSearcher(noResults, AutoColorCorrelogram.class);
        }else if(type.equals("SimpleColorHistogram")){
            imageSearcher = new GenericFastImageSearcher(noResults, SimpleColorHistogram.class);
        }else if(type.equals("EdgeHistogram")){
            imageSearcher = new GenericFastImageSearcher(noResults, EdgeHistogram.class);
        }

        //Obtain the results
        ImageSearchHits hits = imageSearcher.search(image, ir);

        for(int i = 0; i < hits.length(); i++){
            String filename = ir.document(hits.documentID(i)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
            System.out.println(hits.score(i) + ": \t" + filename);
        }

        //Show the results in a new window.
        FXMLLoader loader = new FXMLLoader(Searcher.class.getResource("search_result.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Search Result");
        SearchResultController searchResultController = loader.getController();
        searchResultController.setResultHits(hits);
        searchResultController.setIndexReader(ir);
        searchResultController.loadImages();
        stage.show();

        return hits;
    }
}
