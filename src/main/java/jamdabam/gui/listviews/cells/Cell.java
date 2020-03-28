package jamdabam.gui.listviews.cells;

import jamdabam.configuration.Constants;
import jamdabam.entities.Song;
import jamdabam.gui.listviews.ListViewSongsBase;
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
import java.util.HashMap;
import java.util.Map;

public class Cell extends VBox {
    private static final Font FONT_15 = new Font(15);
    private static final Font FONT_20 = new Font(20);
    private static final Background GREEN_BACKGROUND = new Background(new BackgroundFill(Color.rgb(100, 255, 50, 0.4), CornerRadii.EMPTY, Insets.EMPTY));

    private static final Map<String, Image> COVER_IMAGE_MAP = new HashMap<>();

    public Cell(final Song aSong, final ListViewSongsBase aListView, final Button... aButtons) {
        setSpacing(5);
        setAlignment(Pos.CENTER);

        if (aListView.getSongService().exists(aSong, aListView.getDownloadPath())) {
            setDownloaded();
        }

        //Song Picture Stats
        BorderPane layout = new BorderPane();

        //Title
        Label title = new Label(aSong.getSongAuthorName() + " - " + aSong.getSongName());
        title.setFont(FONT_20);
        layout.setTop(title);

        //Cover
        Image image = COVER_IMAGE_MAP.get(aSong.getKey());
        if (image == null) {
            image = new Image(aSong.getCoverURL(), true);
            COVER_IMAGE_MAP.put(aSong.getKey(), image);
        }
        ImageView cover = new ImageView(image);
        cover.setFitHeight(150);
        cover.setFitWidth(150);
        layout.setLeft(cover);

        //Stats
        GridPane stats = new GridPane();
        stats.setHgap(3);
        stats.setVgap(3);
        stats.setPadding(new

                Insets(10));

        int startRow = 0;

        Label statLabel = new Label(Constants.ICON_KEY);
        statLabel.setFont(FONT_15);
        stats.add(statLabel, 0, 0);

        statLabel = new

                Label(aSong.getKey());
        statLabel.setFont(FONT_15);
        stats.add(statLabel, 1, startRow++);

        statLabel = new

                Label(Constants.ICON_DOWNLOADS);
        statLabel.setFont(FONT_15);
        stats.add(statLabel, 0, startRow);

        statLabel = new

                Label(String.valueOf(aSong.getStats().

                getDownloads()));
        statLabel.setFont(FONT_15);
        stats.add(statLabel, 1, startRow++);

        statLabel = new

                Label(Constants.ICON_UPVOTES);
        statLabel.setFont(FONT_15);
        stats.add(statLabel, 0, startRow);

        statLabel = new

                Label(String.valueOf(aSong.getStats().

                getUpVotes()));
        statLabel.setFont(FONT_15);
        stats.add(statLabel, 1, startRow++);

        statLabel = new

                Label(Constants.ICON_DOWNVOTES);
        statLabel.setFont(FONT_15);
        stats.add(statLabel, 0, startRow);

        statLabel = new

                Label(String.valueOf(aSong.getStats().

                getDownVotes()));
        statLabel.setFont(FONT_15);
        stats.add(statLabel, 1, startRow++);

        statLabel = new

                Label(Constants.ICON_RATING);
        statLabel.setFont(FONT_15);
        stats.add(statLabel, 0, startRow);

        statLabel = new

                Label(BigDecimal.valueOf(aSong.getStats().

                getRating() * 100).

                setScale(1, RoundingMode.HALF_UP).

                toString());
        statLabel.setFont(FONT_15);
        stats.add(statLabel, 1, startRow);

        layout.setRight(stats);

        //Buttons
        if (aButtons != null) {
            HBox buttonLayout = new HBox(5);
            HBox.setHgrow(buttonLayout, Priority.ALWAYS);

            for (Button button : aButtons) {
                buttonLayout.getChildren().add(button);
                HBox.setHgrow(button, Priority.ALWAYS);
            }

            layout.setBottom(buttonLayout);
            getChildren().add(layout);
        }

    }

    public void setDownloaded() {
        setBackground(GREEN_BACKGROUND);
    }
}
