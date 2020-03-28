package jamdabam.service;


import jamdabam.entities.Song;

import java.util.List;


public interface Filter {
    List<Song> filter(List<Song> aSongs);
}
