package entity_hibernate;
// Generated 17.03.2017 16:15:56 by Hibernate Tools 4.3.1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Author generated by hbm2java
 * Prosty objekt wygenerowany za pomocy Hibernate z mojej BD
 */
public class Author implements java.io.Serializable {

    private Set bookConnectedWith = new HashSet(0);
    private Long id;
    private String allNames;
    private Date birthdayDate;

    public Author() {
    }

    public Author(Date birthdayDate, Long id, String allNames) {
        this.birthdayDate = birthdayDate;
        this.id = id;
        this.allNames = allNames;
    }

    public Date getBirthdayDate() {
        return birthdayDate;
    }

    public void setBirthdayDate(Date birthdayDate) {
        this.birthdayDate = birthdayDate;
    }

    public Set getBookConnectedWith() {
        return bookConnectedWith;
    }

    public void setBookConnectedWith(Set bookConnectedWith) {
        this.bookConnectedWith = bookConnectedWith;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAllNames() {
        return allNames;
    }

    public void setAllNames(String allNames) {
        this.allNames = allNames;
    }

}
