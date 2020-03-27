package jamdabam.gui;

import jamdabam.entities.Song;
import jamdabam.service.SongServiceInt;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ListViewSongsBase extends ListView<Song> {
    protected SongServiceInt ivSongService;

    protected Map<String, VBox> ivCellMap = new HashMap<>();
    protected Map<String, Song> ivSongs = new LinkedHashMap<>();

    protected String ivDownloadPath;

    protected Process previewProcess;

    public ListViewSongsBase(final SongServiceInt aSongService) {
        this(aSongService, null);
    }

    public ListViewSongsBase(final SongServiceInt aSongService, String aDownloadPath) {
        ivSongService = aSongService;
        ivDownloadPath = aDownloadPath;

        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setCellFactory(listView -> new ListCell<>() {
            {
                setStyle("-fx-padding: 5px");
            }

            @Override
            protected void updateItem(Song aSong, boolean aEmpty) {
                super.updateItem(aSong, aEmpty);

                if (aEmpty) {
                    setGraphic(null);
                } else {
                    setGraphic(ivCellMap.get(aSong.getKey()));
                }
            }
        });

        initDownloadPathIfNecessary();
    }

    protected void refreshSongs() {
        ObservableList<Song> items = getItems();
        int size = items.size();
        items.clear();
        items.addAll(ivSongs.values());
        setItems(items);

        refresh();
        scrollTo(size - 4);
    }

    protected void initDownloadPathIfNecessary() {
        if (ivDownloadPath == null || ivDownloadPath.isEmpty()) {
            DirectoryChooser chooseDirectory = new DirectoryChooser();
            chooseDirectory.setTitle("Downloadpfad initialisieren...");
            File directory = chooseDirectory.showDialog(null);

            ivDownloadPath = directory.getAbsolutePath();
        }
    }

    public String getDownloadPath() {
        return ivDownloadPath;
    }
}
