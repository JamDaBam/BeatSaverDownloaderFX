package jamdabam.service;


import jamdabam.configuration.Constants;
import jamdabam.entities.Song;
import jamdabam.parser.BeatSaverParser;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SongServiceImpl implements SongServiceInt {
    public List<Song> getLatestSongs(final int aSongs) {
        return getLatestSongs(aSongs, null);
    }

    @Override
    public List<Song> getLatestSongs(final int aSongs, final Filter aFilter) {
        return getSongs(aSongs, aFilter, Constants.LATEST_CALL_BASE_URL);
    }

    @Override
    public List<Song> getLatestSongPage(final int aPageNum) {
        return getLatestSongPage(aPageNum, null);
    }

    @Override
    public List<Song> getLatestSongPage(final int aPageNum, final Filter aFilter) {
        return getSongPage(aPageNum, aFilter, Constants.LATEST_CALL_BASE_URL);
    }


    @Override
    public List<Song> getHottestSongs(final int aSongs) {
        return getHottestSongs(aSongs, null);
    }

    @Override
    public List<Song> getHottestSongs(final int aSongs, final Filter aFilter) {
        return getSongs(aSongs, aFilter, Constants.HOTTEST_CALL_BASE_URL);
    }

    private List<Song> getSongPage(final int aPage, final Filter aFilter, final String aCallBaseUrl) {
        List<Song> songs = Collections.emptyList();

        try {
            String fetchSongJson = readUrl(aCallBaseUrl + "/" + aPage);
            songs = aFilter == null ? BeatSaverParser.parse(fetchSongJson)
                    : aFilter.filter(BeatSaverParser.parse(fetchSongJson));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Etwas ist beim lesen der latest Songs schief gelaufen. " + e.getMessage());
        }

        return songs;
    }

    private List<Song> getSongs(final int aSongs, final Filter aFilter, final String aCallBaseUrl) {
        List<Song> res = null;

        int site = 0;
        int size = 0;

        do {
            List<Song> songs = getSongPage(site, aFilter, aCallBaseUrl);

            if (res == null) {
                res = new ArrayList<>();
            }

            size = res.size();

            if (size < aSongs) {
                if (songs.isEmpty()) {
                    break;
                }

                for (Song song : songs) {
                    res.add(song);
                    size++;

                    if (size == aSongs) {
                        break;
                    }
                }
            }

            site++;
        } while (size < aSongs);

        if (res == null) {
            res = Collections.emptyList();
        }

        return res;
    }

    @Override
    public void downloadSongs(final List<Song> aSongs, final String aTo) {
        for (Song song : aSongs) {
            donwloadSong(song, aTo);
        }
    }

    @Override
    public boolean donwloadSong(final Song aSong, final String aTo) {
        boolean isDownloaded = false;

        String downloadName = aSong.getDownloadName();

        // Checks songidprefix of files in downloadpath if found one or more files skip.
        if (!checkSongIdAlreadyDownloaded(aTo, aSong.getKey())) {
            File downloadFile = new File(aTo, downloadName);

            try {
                // Doublecheck if the new file exists
                if (!downloadFile.exists()) {
                    if (!downloadFile.getParentFile().exists()) {
                        downloadFile.getParentFile().mkdirs();
                    }

                    String downloadUrl = aSong.getDirectDownload();
                    URL url = new URL(downloadUrl);
                    saveUrl(downloadFile.toPath(), url, 30, 30);
                    System.out.println("Downloaded: " + downloadName);
                } else {
                    System.out.println("File exists: " + downloadName);
                }

                isDownloaded = true;
            } catch (IOException e) {
                if (e instanceof FileNotFoundException) {
                    System.out.println("File not found " + downloadName);
                }
            }
        } else {
            isDownloaded = true;
            System.out.println("Key exists: " + downloadName);
        }

        return isDownloaded;
    }

    private boolean checkSongIdAlreadyDownloaded(final String aPath, final String aSongId) {
        File dir = new File(aPath);

        // list the files using a anonymous FileFilter
        File[] files = dir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                return file.getName().startsWith(aSongId + " ");
            }
        });

        if (files != null && files.length > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void saveUrl(final Path aFile, final URL aUrl, final int aSecsConnectTimeout, final int aSecsReadTimeout)
            throws MalformedURLException, IOException {
        try (InputStream in = streamFromUrl(aUrl, aSecsConnectTimeout, aSecsReadTimeout)) {
            Files.copy(in, aFile);
        }
    }

    private InputStream streamFromUrl(final URL aUrl, final int aSecsConnectTimeout, final int aSecsReadTimeout) throws IOException {
        URLConnection conn = aUrl.openConnection();
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

        if (aSecsConnectTimeout > 0)
            conn.setConnectTimeout(aSecsConnectTimeout * 1000);
        if (aSecsReadTimeout > 0)
            conn.setReadTimeout(aSecsReadTimeout * 1000);
        return conn.getInputStream();
    }

    private String readUrl(final String urlString) throws IOException {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }

            return buffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
