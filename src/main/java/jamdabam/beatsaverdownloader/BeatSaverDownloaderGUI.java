package jamdabam.beatsaverdownloader;


import com.sun.javafx.collections.ObservableListWrapper;
import jamdabam.entities.Song;
import jamdabam.gui.ListViewLatestSongs;
import jamdabam.service.SongFilter;
import jamdabam.service.SongServiceImpl;
import jamdabam.service.SongServiceInt;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.util.*;
import java.util.stream.Collectors;

public class BeatSaverDownloaderGUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private int ivStartPage = 0;
    private boolean ivIsLoading = false;

    @Override
    public void start(Stage aPrimaryStage) {

        aPrimaryStage.setTitle("Unser erstes Fenster");
        aPrimaryStage.setWidth(1000);
        aPrimaryStage.setHeight(1000);


        ListViewLatestSongs listViewLatestSongs = new ListViewLatestSongs(new SongServiceImpl());
        Scene scene = new Scene(listViewLatestSongs);
        aPrimaryStage.setScene(scene);
        aPrimaryStage.show();
    }
}
