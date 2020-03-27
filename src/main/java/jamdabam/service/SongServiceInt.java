package jamdabam.service;


import jamdabam.entities.Song;

import java.util.List;


public interface SongServiceInt {
    List<Song> getLatestSongs(final int aSongs);

    List<Song> getLatestSongs(final int aSongs, final Filter aFilter);

    List<Song> getLatestSongPage(final int aPageNum);

    List<Song> getLatestSongPage(final int aPageNum, final Filter aFilter);

    List<Song> getHottestSongs(final int aSongs);

    List<Song> getHottestSongs(final int aSongs, final Filter aFilter);

    boolean exists(final Song aSong, final String aDirectory);

    void downloadSongs(final List<Song> aSongs, final String aDirectory);

    boolean donwloadSong(final Song aSong, final String aDirectory);

    void openPreview(final Song aSong);
}
