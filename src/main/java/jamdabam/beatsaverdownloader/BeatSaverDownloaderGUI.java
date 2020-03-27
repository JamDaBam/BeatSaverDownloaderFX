package jamdabam.beatsaverdownloader;


import jamdabam.gui.listviews.ListViewDownloadQueue;
import jamdabam.gui.listviews.ListViewSongLatest;
import jamdabam.service.SongServiceImpl;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.DirectoryChooser;
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

        String initDownloadPath = initDownloadPath();
        SongServiceImpl songService = new SongServiceImpl();
        ListViewDownloadQueue listViewDownloadQueue = new ListViewDownloadQueue(songService, initDownloadPath);
        ListViewSongLatest listViewLatestSongs = new ListViewSongLatest(songService, listViewDownloadQueue, initDownloadPath);

        TabPane tabPane = new TabPane();
        Tab songListTab = new Tab("Latest", listViewLatestSongs);
        songListTab.setClosable(false);
        tabPane.getTabs().add(songListTab);

        Tab downloadQueueTab = new Tab("Queue", listViewDownloadQueue);
        downloadQueueTab.setClosable(false);
        tabPane.getTabs().add(downloadQueueTab);

        Scene scene = new Scene(tabPane);

        aPrimaryStage.setScene(scene);
        aPrimaryStage.show();
        aPrimaryStage.setOnCloseRequest(aEvent -> listViewLatestSongs.cleanPreviewFiles());
    }

    private String initDownloadPath() {
        DirectoryChooser chooseDirectory = new DirectoryChooser();
        chooseDirectory.setTitle("Downloadpfad initialisieren...");
        return chooseDirectory.showDialog(null).getAbsolutePath();
    }
}
