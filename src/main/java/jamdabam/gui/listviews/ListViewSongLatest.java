package jamdabam.gui.listviews;

import jamdabam.entities.Song;
import jamdabam.gui.listviews.cells.Cell;
import jamdabam.service.SongServiceInt;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

    private ListViewDownloadQueue ivQueue;

    public ListViewSongLatest(final SongServiceInt aSongService, final ListViewDownloadQueue aQueue, final String aDownloadPath) {
        super(aSongService, aDownloadPath);

        ivQueue = aQueue;

        setOnScroll(event -> {
            if (!ivIsLoading && event.getDeltaY() < 0) {
                System.out.println("refresh --> page " + ivPage);
                ivIsLoading = true;
                Platform.runLater(() -> {
                    addSongsFromPage(ivPage++);
                    ivIsLoading = false;
                    System.out.println("refresh done --> page " + ivPage);
                });
            }
        });

        addSongsFromPage(ivPage);
    }

    private void addSongsFromPage(final int aPage) {
        addSongs(ivSongService.getLatestSongPage(aPage));
    }

    @Override
    protected void createCell(final Song aSong) {
        if (!ivCellMap.containsKey(aSong.getKey())) {
            List<Button> buttons = new ArrayList<>();

            boolean exists = ivSongService.exists(aSong, ivDownloadPath);

            Button previewButton = new Button("Preview");
            previewButton.setOnAction(aEvent -> ivSongService.openPreview(aSong));
            previewButton.setMaxWidth(Double.MAX_VALUE);
            buttons.add(previewButton);

            Button downloadPlayButton;
            if (exists) {
                downloadPlayButton = new Button("Play");
                downloadPlayButton.setOnAction(aEvent -> {
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

                                if (ivPreviewProcess != null) {
                                    ivPreviewProcess.destroy();
                                }

                                ivPreviewProcess = runtime.exec(command);
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
            }
            else {
                Button addToQueueButton = new Button("Queue");
                addToQueueButton.setOnAction(aEvent -> ivSongService.openPreview(aSong));
                addToQueueButton.setMaxWidth(Double.MAX_VALUE);
                addToQueueButton.setOnAction(aEvent -> ivQueue.addSong(aSong));
                buttons.add(addToQueueButton);

                downloadPlayButton = new Button("Download");
                downloadPlayButton.setOnAction(aEvent -> {
                    System.out.println("download --> " + aSong.getSongName());
                    downloadPlayButton.setDisable(true);
                    addToQueueButton.setDisable(true);
                    ivQueue.removeSong(aSong);

                    Task<Void> task = new Task<>() {
                        @Override
                        public Void call() {
                            ivSongService.donwloadSong(aSong, ivDownloadPath);
                            ivCellMap.remove(aSong.getKey());
                            Platform.runLater(() -> createCell(aSong));
                            return null;
                        }
                    };
                    task.setOnSucceeded(taskFinishEvent -> refresh());
                    new Thread(task).start();
                });
            }

            downloadPlayButton.setMaxWidth(Double.MAX_VALUE);
            buttons.add(downloadPlayButton);


            ivCellMap.put(aSong.getKey(), new Cell(aSong, this, buttons.toArray(new Button[0])));
        }
    }


    public void cleanPreviewFiles() {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));

        String[] list = tmpDir.list((dir, name) -> name.startsWith(AUDIO_PREVIEW_PREFIX) && name.endsWith(AUDIO_PREVIEW_SUFFIX));

        if (list != null) {
            for (String fileName : list) {
                new File(tmpDir, fileName).delete();
                System.out.println("deleted: " + fileName);
            }
        }
    }
}
