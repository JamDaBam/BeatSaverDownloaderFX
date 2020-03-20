package jamdabam.service;


import jamdabam.entities.Song;

import java.util.List;


public interface SongServiceInt {
    public List<Song> getLatestSongs(final int aSongs);

    public List<Song> getLatestSongs(final int aSongs, final Filter aFilter);

    public List<Song> getLatestSongPage(final int aPageNum);

    public List<Song> getLatestSongPage(final int aPageNum, final Filter aFilter);

    public List<Song> getHottestSongs(final int aSongs);

    public List<Song> getHottestSongs(final int aSongs, final Filter aFilter);

    public void downloadSongs(final List<Song> aSongs, final String aTo);

    public boolean donwloadSong(final Song aSong, final String aTo);
}
