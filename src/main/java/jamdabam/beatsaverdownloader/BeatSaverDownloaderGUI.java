package jamdabam.beatsaverdownloader;


import jamdabam.gui.ListViewDownloadQueue;
import jamdabam.gui.ListViewSongLatest;
import jamdabam.service.SongServiceImpl;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class BeatSaverDownloaderGUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage aPrimaryStage) {

        aPrimaryStage.setTitle("BeatSaverDownloaderFX");
        aPrimaryStage.setWidth(1000);
        aPrimaryStage.setHeight(1000);

        SongServiceImpl songService = new SongServiceImpl();
        ListViewSongLatest listViewLatestSongs = new ListViewSongLatest(songService);
        TabPane tabPane = new TabPane();
        Tab songListTab = new Tab("Songs", listViewLatestSongs);
        songListTab.setClosable(false);
        tabPane.getTabs().add(songListTab);

        ListViewDownloadQueue listViewDownloadQueue = new ListViewDownloadQueue(songService, listViewLatestSongs.getDownloadPath());
        Tab downloadQueueTab = new Tab("Queue", listViewDownloadQueue);
        downloadQueueTab.setClosable(false);
        tabPane.getTabs().add(downloadQueueTab);

        Scene scene = new Scene(tabPane);

        aPrimaryStage.setScene(scene);
        aPrimaryStage.show();
        aPrimaryStage.setOnCloseRequest(aEvent -> listViewLatestSongs.cleanPreviewFiles());
    }
}
