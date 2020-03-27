package jamdabam.gui;

import jamdabam.configuration.Constants;
import jamdabam.entities.Song;
import jamdabam.service.SongServiceInt;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ListViewDownloadQueue extends ListViewSongsBase {
    public ListViewDownloadQueue(final SongServiceInt aSongService) {
        this(aSongService, null);
    }

    public ListViewDownloadQueue(final SongServiceInt aSongService, String aDownloadPath) {
        super(aSongService, aDownloadPath);
        refreshSongs(0);
    }

    private void refreshSongs(final int aPage) {
        List<Song> newSongs = ivSongService.getLatestSongPage(aPage);
        for (Song song : newSongs) {
            ivSongs.put(song.getKey(), song);
            createCell(song);
        }

        refreshSongs();
    }

    private void createCell(final Song aSong) {
        VBox container = ivCellMap.get(aSong.getKey());
        if (container == null) {
            container = new VBox(5);
            container.setAlignment(Pos.CENTER);

            Boolean downloaded = aSong.getDownloaded();
            if (downloaded == null) {
                downloaded = ivSongService.exists(aSong, ivDownloadPath);
                aSong.setDownloaded(downloaded);
            }

            if (downloaded) {
                container.setBackground(new Background(new BackgroundFill(Color.rgb(100, 255, 50, 0.4), CornerRadii.EMPTY, Insets.EMPTY)));
            }

            //Song Picture Stats
            BorderPane layout = new BorderPane();

            //Title
            Font font = new Font(20);
            Label title = new Label(aSong.getSongAuthorName() + " - " + aSong.getSongName());
            title.setFont(font);
            layout.setTop(title);

            //Cover
            ImageView cover = new ImageView(new Image(aSong.getCoverURL(), true));
            cover.setFitHeight(150);
            cover.setFitWidth(150);
            layout.setLeft(cover);

            //Stats
            GridPane stats = new GridPane();
            stats.setHgap(3);
            stats.setVgap(3);
            stats.setPadding(new Insets(10));

            font = new Font(15);

            int startRow = 0;

            Label statLabel = new Label(Constants.ICON_KEY);
            statLabel.setFont(font);
            stats.add(statLabel, 0, 0);

            statLabel = new Label(aSong.getKey());
            statLabel.setFont(font);
            stats.add(statLabel, 1, startRow++);

            statLabel = new Label(Constants.ICON_DOWNLOADS);
            statLabel.setFont(font);
            stats.add(statLabel, 0, startRow);

            statLabel = new Label(String.valueOf(aSong.getStats().getDownloads()));
            statLabel.setFont(font);
            stats.add(statLabel, 1, startRow++);

            statLabel = new Label(Constants.ICON_UPVOTES);
            statLabel.setFont(font);
            stats.add(statLabel, 0, startRow);

            statLabel = new Label(String.valueOf(aSong.getStats().getUpVotes()));
            statLabel.setFont(font);
            stats.add(statLabel, 1, startRow++);

            statLabel = new Label(Constants.ICON_DOWNVOTES);
            statLabel.setFont(font);
            stats.add(statLabel, 0, startRow);

            statLabel = new Label(String.valueOf(aSong.getStats().getDownVotes()));
            statLabel.setFont(font);
            stats.add(statLabel, 1, startRow++);

            statLabel = new Label(Constants.ICON_RATING);
            statLabel.setFont(font);
            stats.add(statLabel, 0, startRow);

            statLabel = new Label(BigDecimal.valueOf(aSong.getStats().getRating() * 100).setScale(1, RoundingMode.HALF_UP).toString());
            statLabel.setFont(font);
            stats.add(statLabel, 1, startRow++);

            layout.setRight(stats);

            //Buttons
            HBox buttonLayout = new HBox(5);
            HBox.setHgrow(buttonLayout, Priority.ALWAYS);
            Button button;

            button = new Button("DELETE");
            button.setOnAction(aEvent -> System.out.println("delete"));

            button.setMaxWidth(Double.MAX_VALUE);
            buttonLayout.getChildren().add(button);
            HBox.setHgrow(button, Priority.ALWAYS);

            container.getChildren().add(layout);

            ivCellMap.put(aSong.getKey(), container);
        }
    }
}
