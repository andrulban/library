/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import db_hibernate.DBHelper;
import enums.SearchType;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import entity_hibernate.Book;
import jbeans.ConnectionDB;
import jbeans.Pager;

@ManagedBean(eager = true)
@SessionScoped
public class BookListController implements Serializable {

    private int selectedGenreId; // выбранный жанр
    private char selectedLetter; // выбранная буква алфавита
    private HashMap<String, SearchType> searchList = new HashMap<>();
    private SearchType searchType;
    private String searchString;
    private boolean isEditorMode = false;
    Pager<Book> pager = new Pager<>();
    private transient int row = -1;

    public BookListController() {
        fillBooksAll();
        ResourceBundle bundle = ResourceBundle.getBundle("propertiesFiles.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        searchList.put(bundle.getString("author"), SearchType.AUTHOR);
        searchList.put(bundle.getString("title"), SearchType.TITLE);
    }

//<editor-fold defaultstate="collapsed" desc="Filling of bookList by deffrent types">
    public void fillBooksByPage() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        pager.setSelectedPageNumber(Integer.valueOf(params.get("page")));
        isEditorMode = false;
        row = -1;
        cancleBookEditing();
        DBHelper.getInstance().refreshList();

    } //Nado bydet ispravit

    public void fillBooksByGenre() {
        isEditorMode = false;
        cancleBookEditing();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        selectedGenreId = Integer.valueOf(params.get("genre_id"));
        submitValues(' ', 1, Integer.valueOf(params.get("genre_id")), false, false, true, -1);
        DBHelper.getInstance().getBooksByGenre((long) selectedGenreId,pager);
    }

    public void fillBooksByLetter() {
        isEditorMode = false;
        cancleBookEditing();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        selectedLetter = params.get("letter").charAt(0);
        submitValues(selectedLetter, 1, -1, false, false, true, -1);
        DBHelper.getInstance().getBooksByLetter(selectedLetter, pager);
    }

    public void fillBooksBySearch() {
        submitValues(' ', 1, -1, false, false, true, -1);
        if (searchString.trim().length() == 0) {
            fillBooksAll();
            return;
        }

        if (searchType == SearchType.AUTHOR) {
            DBHelper.getInstance().getBooksByAuthor(searchString, pager);
        } else if (searchType == SearchType.TITLE) {
            DBHelper.getInstance().getBooksByName(searchString, pager);
        }
    }

    private void fillBooksAll() {
        submitValues(' ', 1, -1, false, false, false, -1);
        DBHelper.getInstance().getAllBooks(pager);
    }
//</editor-fold>

    public void updatebooklist() {
        DBHelper.getInstance().update();
        cancleBookEditing();
        DBHelper.getInstance().refreshList();

    }

    public void cancleBookEditing() {
        row = -1;
        isEditorMode = false;
        for (Book book : pager.getList()) {
            book.setIsEditing(false);
        }
    }

    private void submitValues(Character selectedLetter, int selectedPageNumber, int selectedGenreId, boolean isFromPages, boolean isEditorMode, boolean cancelBookEditing, int row) {
        this.selectedLetter = selectedLetter;
        pager.setSelectedPageNumber(selectedPageNumber);
        this.selectedGenreId = selectedGenreId;
        this.isEditorMode = isEditorMode;
        this.row = row;
        if (cancelBookEditing) {
            cancleBookEditing();
        }

    }

    public void changeBooksCountOnPage(ValueChangeEvent e) {
        pager.setBooksOnPage(Integer.valueOf(e.getNewValue().toString()).intValue());
        isEditorMode = false;
        cancleBookEditing();
        pager.setSelectedPageNumber(1);
        row = -1;
        DBHelper.getInstance().refreshList();
    }

    public void setButtonLocale(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("propertiesFiles.messages", locale);
        searchList = new HashMap<>();
        searchList.put(bundle.getString("author"), SearchType.AUTHOR);
        searchList.put(bundle.getString("title"), SearchType.TITLE);
    }

    public Character[] getRussianLetters() {
        Character[] letters = new Character[]{'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я',};
        return letters;
    }

    private void fillPageNumbers(long totalBooksCount, int booksCountOnPage) {

        int pageCount = totalBooksCount > 0 ? (int) (totalBooksCount / booksCountOnPage) : 0;
        if ((totalBooksCount % booksCountOnPage) > 0) {
            pageCount++;
        }
        pager.getPageNumbers().clear();
        for (int i = 1; i <= pageCount; i++) {
            pager.getPageNumbers().add(i);
        }

    }

    public void switchEditorMode() {
        row = -1;
        isEditorMode = !isEditorMode;
    }

//<editor-fold defaultstate="collapsed" desc="Books's content and image taking by sql from DB">
    public byte[] getContent(int id) {
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;

        byte[] content = null;
        try {
            conn = DriverManager.getConnection(ConnectionDB.url, ConnectionDB.user, ConnectionDB.password);
            stmt = conn.createStatement();

            rs = stmt.executeQuery("select content from book where id=" + id);
            while (rs.next()) {
                content = rs.getBytes("content");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Book.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Book.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }

        return content;

    }

    public byte[] getImage(int id) {
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;

        byte[] image = null;

        try {
            conn = DriverManager.getConnection(ConnectionDB.url, ConnectionDB.user, ConnectionDB.password);
            stmt = conn.createStatement();

            rs = stmt.executeQuery("select image from book where id=" + id);
            while (rs.next()) {
                image = rs.getBytes("image");
            }

        } catch (SQLException ex) {
            Logger.getLogger(Book.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Book.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }

        return image;
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Getters and setters">
    public SearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }

    public int getSelectedGenreId() {
        return selectedGenreId;
    }

    public void setSelectedGenreId(int selectedGenreId) {
        this.selectedGenreId = selectedGenreId;
    }

    public char getSelectedLetter() {
        return selectedLetter;
    }

    public void setSelectedLetter(char selectedLetter) {
        this.selectedLetter = selectedLetter;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public HashMap<String, SearchType> getSearchList() {
        return searchList;
    }

    public void setSearchList(HashMap<String, SearchType> searchList) {
        this.searchList = searchList;
    }

    public boolean getIsEditorMode() {
        return isEditorMode;
    }

    public Pager<Book> getPager() {
        return pager;
    }

    public void setPager(Pager<Book> pager) {
        this.pager = pager;
    }

    public int getRow() {
        row += 1;
        return row;
    }

    public void setRow() {
        row = -1;
    }
//</editor-fold>
}
