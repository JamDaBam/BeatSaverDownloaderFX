package jamdabam.service;


import jamdabam.entities.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;


public class SongFilter implements Filter {
    private Double ivRating;
    private Integer ivUpVotes;
    private Integer ivDownVotes;
    private Integer ivDownloads;

    public SongFilter(Double aRating) {
        this(aRating, null, null, null);
    }

    public SongFilter(Double aRating, Integer aUpVotes) {
        this(aRating, aUpVotes, null, null);
    }

    public SongFilter(Double aRating, Integer aUpVotes, Integer aDownVotes) {
        this(aRating, aUpVotes, aDownVotes, null);
    }

    public SongFilter(Double aRating, Integer aUpVotes, Integer aDownVotes, Integer aDownloads) {
        ivRating = aRating;
        ivUpVotes = aUpVotes;
        ivDownVotes = aDownVotes;
        ivDownloads = aDownloads;
    }

    @Override
    public List<Song> filter(List<Song> aSongs) {
        List<Song> filteredSongs = null;

        if (aSongs != null) {
            filteredSongs = new ArrayList<>();

            for (Song song : aSongs) {
                boolean isIn = true;

                if (ivRating != null && ivRating.compareTo(song.getStats().getRating()) > 0) {
                    isIn = false;
                }

                if (ivUpVotes != null && ivUpVotes.compareTo(song.getStats().getUpVotes()) > 0) {
                    isIn = false;
                }

                if (ivDownVotes != null && ivDownVotes.compareTo(song.getStats().getDownVotes()) < 0) {
                    isIn = false;
                }

                if (ivDownloads != null && ivDownloads.compareTo(song.getStats().getDownloads()) > 0) {
                    isIn = false;
                }

                if (isIn) {
                    filteredSongs.add(song);
                }
            }
        }

        return filteredSongs == null ? Collections.emptyList() : filteredSongs;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
