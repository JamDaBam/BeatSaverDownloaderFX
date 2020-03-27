package jamdabam.gui;

import jamdabam.configuration.Constants;
import jamdabam.entities.Song;
import jamdabam.service.SongServiceInt;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ListViewSongLatest extends ListViewSongsBase {
    public static final String VLC_RUNTIME = "C:\\Program Files\\VideoLAN\\VLC\\vlc.exe ";
    public static final String AUDIO_PREVIEW_PREFIX = "_AUDIO_PREVIEW_";
    public static final String AUDIO_PREVIEW_SUFFIX = ".egg";

    private int ivPage = 0;

    private boolean ivIsLoading = false;

    public ListViewSongLatest(final SongServiceInt aSongService) {
        this(aSongService, null);
    }

    public ListViewSongLatest(final SongServiceInt aSongService, String aDownloadPath) {
        super(aSongService);

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

            button = new Button("Preview");
            button.setOnAction(aEvent -> ivSongService.openPreview(aSong));

            button.setMaxWidth(Double.MAX_VALUE);
            buttonLayout.getChildren().add(button);
            HBox.setHgrow(button, Priority.ALWAYS);

            if (downloaded) {
                button = new Button("Play");
                button.setOnAction(aEvent -> {
                    try {
                        ZipFile zip = new ZipFile(ivDownloadPath + "/" + aSong.getDownloadName());
                        for (Iterator<? extends ZipEntry> iter = zip.entries().asIterator(); iter.hasNext(); ) {
                            ZipEntry entry = iter.next();
                            System.out.println(entry.getName());
                            if (entry.getName().endsWith(AUDIO_PREVIEW_SUFFIX)) {
                                InputStream in = zip.getInputStream(entry);
                                File tempFile = File.createTempFile(AUDIO_PREVIEW_PREFIX, AUDIO_PREVIEW_SUFFIX);
                                FileOutputStream out = new FileOutputStream(tempFile);
                                IOUtils.copy(in, out);
                                Runtime runtime = Runtime.getRuntime();
                                String command = VLC_RUNTIME + tempFile.getAbsolutePath();
                                System.out.println(command);

                                if (previewProcess != null) {
                                    previewProcess.destroy();
                                }

                                previewProcess = runtime.exec(command);
                                break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Label label = new Label("Es ist ein Fehler aufgetreten: " + e.getMessage());
                        label.setWrapText(true);

                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.getDialogPane().setContent(label);
                        alert.show();
                    }
                });
            } else {
                button = new Button("Download");
                button.setOnAction(aEvent -> {
                    initDownloadPathIfNecessary();
                    ivSongService.donwloadSong(aSong, ivDownloadPath);
                    ivCellMap.remove(aSong.getKey());
                    createCell(aSong);
                    refresh();
                });
            }

            button.setMaxWidth(Double.MAX_VALUE);
            buttonLayout.getChildren().add(button);
            HBox.setHgrow(button, Priority.ALWAYS);
            layout.setBottom(buttonLayout);


            container.getChildren().add(layout);

            ivCellMap.put(aSong.getKey(), container);
        }
    }

    public void cleanPreviewFiles() {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));

        String[] list = tmpDir.list((dir, name) -> {
            return name.startsWith(AUDIO_PREVIEW_PREFIX) && name.endsWith(AUDIO_PREVIEW_SUFFIX);
        });

        if (list != null) {
            for (String fileName : list) {
                new File(tmpDir, fileName).delete();
                System.out.println("deleted: " + fileName);
            }
        }
    }
}
