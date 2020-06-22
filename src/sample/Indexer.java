package sample;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import net.semanticmetadata.lire.builders.GlobalDocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.global.*;
import net.semanticmetadata.lire.indexers.tools.text.LuceneIndexWriter;
import net.semanticmetadata.lire.utils.FileUtils;
import net.semanticmetadata.lire.utils.LuceneUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;


public class Indexer {
    public static void startIndex(String imgDir) throws IOException {
        // If the image directory path is empty
        if(imgDir.length() <= 0){
            return;
        }else{
            File file = new File(imgDir);
            //Check if the selected directory is a directory type.
            if (!file.isDirectory()){
                return;
            }
        }

        //Get all images from the selected directory
        ArrayList<String> images = getAllImages(new File(imgDir), true);

        //Create builder that index images using CEDD extractor.
        GlobalDocumentBuilder globalDocumentBuilder = new GlobalDocumentBuilder(CEDD.class);

        //Add extractors (FCTH and Auto Color Correlogram) into the builder
        globalDocumentBuilder.addExtractor(FCTH.class);
        globalDocumentBuilder.addExtractor(AutoColorCorrelogram.class);
        globalDocumentBuilder.addExtractor(SimpleColorHistogram.class);
        globalDocumentBuilder.addExtractor(EdgeHistogram.class);

        //Create a index writer with index path to "index", if the index directory not existed, it will create it
        IndexWriter iw = LuceneUtils.createIndexWriter("index",true, LuceneUtils.AnalyzerType.WhitespaceAnalyzer);

        //Each images will be indexed based on the extractors presented in the builder.
        for(Iterator<String> iterator = images.iterator(); iterator.hasNext();){
            String imageFilePath = iterator.next();
            System.out.println("Indexing "+ imageFilePath);
            try{
                BufferedImage img = ImageIO.read(new FileInputStream(imageFilePath));
                Document document = globalDocumentBuilder.createDocument(img,imageFilePath);
                iw.addDocument(document);
            }catch(Exception e){
                System.err.println("Error reading image or indexing it.");
                e.printStackTrace();
            }
        }

        //Close the writer
        LuceneUtils.closeWriter(iw);
        System.out.println("Finished indexing.");

        //Notify user the indexing is finished
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Finished!");
        a.setContentText("The system has finished indexing!");
        a.show();

    }

    //Get all images from the directory
    private static ArrayList<String> getAllImages(File imageDirectory, boolean descentIntoSubDirectories) throws IOException {
        ArrayList<String> images = new ArrayList<>();
        File[] f = imageDirectory.listFiles();
        for(File file : f){
            //Retrieve all images paths in the directory
            if (file != null && (file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".png"))){
                images.add(file.getCanonicalPath());
            }
            //Retrieve all images paths from the subdirectory in the directory (recursively)
            if(descentIntoSubDirectories && file.isDirectory()){
                ArrayList<String> tmp = getAllImages(file, true);
                if (tmp != null){
                    images.addAll(tmp);
                }
            }
        }

        // Return the list of image paths
        if(images.size() > 0){
            return images;
        }else{
            return null;
        }
    }
}
