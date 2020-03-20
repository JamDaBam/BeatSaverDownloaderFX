package jamdabam.gui;

import jamdabam.entities.Song;
import jamdabam.service.SongFilter;
import jamdabam.service.SongServiceInt;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class ListViewLatestSongs extends ListView<Song> {
    private int ivPage = 0;

    private SongServiceInt ivSongService;

    private Map<String, VBox> ivCellMap = new HashMap<>();
    private Map<String, Song> ivSongs = new LinkedHashMap<>();
    private boolean ivIsLoading = false;

    public ListViewLatestSongs(SongServiceInt aSongService) {
        ivSongService = aSongService;

        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setCellFactory(listView -> new ListCell<Song>() {

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

        setOnScroll(event -> {
            if (!ivIsLoading && event.getDeltaY() < 0) {
                System.out.println("refresh");
                ivIsLoading = true;
                Platform.runLater(() -> {
                    refreshSongs(ivPage++);
                    ivIsLoading = false;
                    System.out.println("refresh done");
                });
            }
        });

        refreshSongs(ivPage);
    }

    private void refreshSongs(final int aPage) {
        List<Song> newSongs = ivSongService.getLatestSongPage(aPage); //Das muss async werden
        for (Song song : newSongs) {
            ivSongs.put(song.getKey(), song);
            createCell(song);
        }

        ObservableList<Song> items = getItems();
        int size = items.size();
        items.clear();
        items.addAll(ivSongs.values());
        setItems(items);

        refresh();
        scrollTo(size - 4);
    }


    private void createCells(final Collection<Song> aSongs) {
        long l = System.currentTimeMillis();

        for (Song song : aSongs) {
            createCell(song);
        }

        System.out.println("createcells=" + (System.currentTimeMillis() - l));
    }

    private void createCell(final Song aSong) {
        VBox container = ivCellMap.get(aSong.getKey());

        if (container == null) {
            container = new VBox(5);
            container.setAlignment(Pos.CENTER);

            BorderPane layout = new BorderPane();
            Font font = new Font(20);
            Label songAuthorTitleLabel = new Label(aSong.getSongAuthorName() + " - " + aSong.getSongName());
            songAuthorTitleLabel.setFont(font);

            ImageView cover = new ImageView(new Image(aSong.getCoverURL(), true));
            cover.setFitHeight(150);
            cover.setFitWidth(150);

            layout.setLeft(cover);
            layout.setTop(songAuthorTitleLabel);

            //Stats
            GridPane stats = new GridPane();
            stats.setHgap(3);
            stats.setVgap(3);
            stats.setPadding(new Insets(10));

            int startRow = 0;

            stats.add(new Label("\uD83D\uDD11"), 0, 0);
            stats.add(new Label("" + aSong.getKey()), 1, startRow++);

            stats.add(new Label("\uD83D\uDCBE"), 0, startRow);
            stats.add(new Label("" + aSong.getStats().getDownloads()), 1, startRow++);

            stats.add(new Label("\uD83D\uDC4D"), 0, startRow);
            stats.add(new Label("" + aSong.getStats().getUpVotes()), 1, startRow++);

            stats.add(new Label("\uD83D\uDC4E"), 0, startRow);
            stats.add(new Label("" + aSong.getStats().getDownVotes()), 1, startRow++);

            stats.add(new Label("\uD83D\uDCAF"), 0, startRow);
            stats.add(new Label("" + new BigDecimal(aSong.getStats().getRating() * 100).setScale(1, RoundingMode.HALF_UP)), 1, startRow++);

            layout.setRight(stats);

            container.getChildren().add(layout);

            ivCellMap.put(aSong.getKey(), container);
        }
    }

    private VBox getSongList(final List<Song> aHottestSongs) {
        long l = System.currentTimeMillis();
        VBox vBox = new VBox();

        for (Song song : aHottestSongs) {
            Image image = new Image(song.getCoverURL(), 150, 150, true, true);
            ImageView imageView = new ImageView(image);

            BorderPane borderPane = new BorderPane();
            borderPane.setLeft(imageView);
            Label label = new Label(
                    song.getSongAuthorName().toUpperCase() + " - " + song.getSongName().toUpperCase());
            Font font = new Font(20);
            label.setFont(font);
            borderPane.setTop(label);

            GridPane details = new GridPane();
            details.add(new Label(song.getKey()), 0, 0);
            details.add(new Label("" + song.getStats().getDownloads()), 0, 1);
            borderPane.setRight(details);

            vBox.getChildren().add(borderPane);
        }

        System.out.println("getsonglist=" + (System.currentTimeMillis() - l));

        return vBox;
    }
}
