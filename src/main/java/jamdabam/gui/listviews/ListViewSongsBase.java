package jamdabam.gui.listviews;

import jamdabam.entities.Song;
import jamdabam.gui.listviews.cells.Cell;
import jamdabam.service.SongServiceInt;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ListViewSongsBase extends ListView<Song> {
    protected final SongServiceInt ivSongService;

    protected final Map<String, Cell> ivCellMap = new HashMap<>();
    protected final Map<String, Song> ivSongs = new LinkedHashMap<>();

    protected final String ivDownloadPath;

    protected Process ivPreviewProcess;

    public ListViewSongsBase(final SongServiceInt aSongService, final String aDownloadPath) {
        ivSongService = aSongService;
        ivDownloadPath = aDownloadPath;

        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setCellFactory(listView -> new ListCell<>() {
            {
                setStyle("-fx-padding: 5px");
            }

            @Override
            protected void updateItem(final Song aSong, final boolean aEmpty) {
                super.updateItem(aSong, aEmpty);

                if (aEmpty) {
                    setGraphic(null);
                }
                else {
                    setGraphic(ivCellMap.get(aSong.getKey()));
                }
            }
        });
    }

    protected void addSong(final Song aSong) {
        addSongs(List.of(aSong));
    }

    protected void addSongs(final List<Song> aNewSongs) {
        for (Song song : aNewSongs) {
            ivSongs.put(song.getKey(), song);
            createCell(song);
        }

        refreshSongs();
    }

    private void refreshSongs() {
        ObservableList<Song> items = getItems();
        int size = items.size();
        items.clear();
        items.addAll(ivSongs.values());

        setItems(items);

        refresh();
        scrollTo(size - 4);
    }

    public void removeSong(final Song aSong) {
        removeSongs(List.of(aSong));
    }

    public void removeSongs(final List<Song> aSongs) {
        for (Song song : aSongs) {
            ivSongs.remove(song.getKey());
        }

        refreshSongs();
    }

    public String getDownloadPath() {
        return ivDownloadPath;
    }

    public SongServiceInt getSongService() {
        return ivSongService;
    }

    protected abstract void createCell(final Song aSong);
}
