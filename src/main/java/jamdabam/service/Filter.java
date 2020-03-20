package jamdabam.service;


import jamdabam.entities.Song;

import java.util.List;


public interface Filter {
    public List<Song> filter(List<Song> aSongs);
}
