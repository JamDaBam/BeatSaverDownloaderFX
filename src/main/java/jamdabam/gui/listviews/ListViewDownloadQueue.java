package jamdabam.gui.listviews;

import jamdabam.entities.Song;
import jamdabam.gui.listviews.cells.Cell;
import jamdabam.service.SongServiceInt;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.List;

public class ListViewDownloadQueue extends ListViewSongsBase {
    public ListViewDownloadQueue(final SongServiceInt aSongService) {
        this(aSongService, null);
    }

    public ListViewDownloadQueue(final SongServiceInt aSongService, String aDownloadPath) {
        super(aSongService, aDownloadPath);
    }

    @Override
    protected void createCell(final Song aSong) {
        if (!ivCellMap.containsKey(aSong.getKey())) {
            List<Button> buttons = new ArrayList<>();

            //Buttons
            Button deleteButton = new Button("DELETE");
            deleteButton.setOnAction(aEvent -> System.out.println("delete --> " + aSong.getSongName()));
            deleteButton.setMaxWidth(Double.MAX_VALUE);

            ivCellMap.put(aSong.getKey(), new Cell(aSong, this, deleteButton));
        }
    }
}
