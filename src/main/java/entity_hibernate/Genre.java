package entity_hibernate;
// Generated 17.03.2017 16:15:56 by Hibernate Tools 4.3.1

import java.util.HashSet;
import java.util.Set;

/**
 * Genre generated by hbm2java
 */
public class Genre implements java.io.Serializable {    //prosty objekt wygenerowany za pomocy Hibernate z mojej BD

    private Set bookConnectedWith = new HashSet(0);
    private Long id;
    private String genreName;

    public Genre() {
    }

    public Genre(String genreName) {
        this.genreName = genreName;
    }

    public Genre(String genreName, Set bookConnectedWith) {
        this.genreName = genreName;
        this.bookConnectedWith = bookConnectedWith;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public Set getBookConnectedWith() {
        return bookConnectedWith;
    }

    public void setBookConnectedWith(Set bookConnectedWith) {
        this.bookConnectedWith = bookConnectedWith;
    }

}
