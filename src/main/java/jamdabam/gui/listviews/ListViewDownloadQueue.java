package jamdabam.gui.listviews;

import jamdabam.entities.Song;
import jamdabam.gui.listviews.cells.Cell;
import jamdabam.service.SongServiceInt;
import javafx.scene.control.Button;

public class ListViewDownloadQueue extends ListViewSongsBase {
    public ListViewDownloadQueue(final SongServiceInt aSongService, final String aDownloadPath) {
        super(aSongService, aDownloadPath);
    }

    @Override
    protected void createCell(final Song aSong) {
        if (!ivCellMap.containsKey(aSong.getKey())) {

            //Buttons
            Button deleteButton = new Button("X");
            deleteButton.setOnAction(aEvent -> removeSong(aSong));
            deleteButton.setMaxWidth(Double.MAX_VALUE);

            ivCellMap.put(aSong.getKey(), new Cell(aSong, this, deleteButton));
        }
    }
}
