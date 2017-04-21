/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

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
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import jbeans.Book;
import jbeans.ConnectionDB;

@ManagedBean(eager = true)
@SessionScoped
public class BookListController implements Serializable {

    private int booksOnPage = 5;
    private int selectedGenreId; // выбранный жанр
    private char selectedLetter; // выбранная буква алфавита
    private long selectedPageNumber = 1; // выбранный номер страницы в постраничной навигации
    private long totalBooksCount;// общее кол-во книг (не на текущей странице, а всего), нажно для постраничности
    private ArrayList<Integer> pageNumbers = new ArrayList<Integer>(); // общее кол-во страниц
    private String currentSql;// последний выполнный sql без добавления limit
    private boolean isFromPages = false;
    private HashMap<String, SearchType> searchList = new HashMap<>();
    private SearchType searchType;
    private String searchString;
    private ArrayList<Book> currentBookList; // текущий список книг для отображения
    private boolean isEditorMode = false;

    public BookListController() {
        fillBooksAll();
        ResourceBundle bundle = ResourceBundle.getBundle("propertiesFiles.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        searchList.put(bundle.getString("author"), SearchType.AUTHOR);
        searchList.put(bundle.getString("title"), SearchType.TITLE);
    }

    public String editBookList() {
        PreparedStatement preparedStatement = null;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(ConnectionDB.url, ConnectionDB.user, ConnectionDB.password);
            preparedStatement = conn.prepareStatement("update book set name=?, isbn=?, page_count=?, publish_year=?, descr=? where id=?");

            for (Book book : currentBookList) {
                if (book.isIsEditing()) {
                    preparedStatement.setString(1, book.getName());
                    preparedStatement.setString(2, book.getIsbn());
//                prepStmt.setString(3, book.getAuthor());
                    preparedStatement.setInt(3, book.getPageCount());
                    preparedStatement.setInt(4, book.getPublishDate());
//                prepStmt.setString(6, book.getPublisher());
                    preparedStatement.setString(5, book.getDescr());
                    preparedStatement.setLong(6, book.getId());
                    preparedStatement.addBatch();
                }
            }
            preparedStatement.executeBatch();

        } catch (SQLException ex) {
            Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException ex) {
                Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        switchEditorMode();
        cansleBookEditing();
        return "books";
    }

    private void fillBooksBySQL(String sql) {

        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        StringBuilder sqlBuilder = new StringBuilder(sql);
        try {
            conn = DriverManager.getConnection(ConnectionDB.url, ConnectionDB.user, ConnectionDB.password);
            stmt = conn.createStatement();

            if (!isFromPages) {
                rs = stmt.executeQuery(sql);
                rs.last();
                totalBooksCount = rs.getRow();
                fillPageNumbers(totalBooksCount, booksOnPage);
            }

            currentSql = sqlBuilder.toString();
            if (totalBooksCount > booksOnPage) {
                sqlBuilder.append(" limit " + (selectedPageNumber * booksOnPage - booksOnPage) + "," + booksOnPage);
            }
            rs = stmt.executeQuery(sqlBuilder.toString());
            currentBookList = new ArrayList<Book>();
            System.out.println(sqlBuilder);

            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getLong("id"));
                book.setName(rs.getString("name"));
                book.setGenre(rs.getString("genre"));
                book.setIsbn(rs.getString("isbn"));
                book.setAuthor(rs.getString("author"));
                book.setPageCount(rs.getInt("page_count"));
                book.setPublishDate(rs.getInt("publish_year"));
                book.setPublisher(rs.getString("publisher"));
                book.setDescr(rs.getString("descr"));
                currentBookList.add(book);
            }

        } catch (SQLException ex) {
            Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
//<editor-fold defaultstate="collapsed" desc="Filling of bookList by deffrent types">
    public void fillBooksByPage() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        selectedPageNumber = Integer.valueOf(params.get("page"));
        isFromPages = true;
        isEditorMode=false;
        cansleBookEditing();
        fillBooksBySQL(currentSql);
        
    }
    
    public void fillBooksByGenre() {
        isEditorMode=false;
        cansleBookEditing();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        selectedGenreId = Integer.valueOf(params.get("genre_id"));
        submitValues(' ', 1, Integer.valueOf(params.get("genre_id")), false,false,true);
        fillBooksBySQL("select b.id,b.name,b.isbn,b.page_count,b.publish_year, p.name as publisher, a.fio as author, g.name as genre, b.descr, b.image from book b "
                + "inner join author a on b.author_id=a.id "
                + "inner join genre g on b.genre_id=g.id "
                + "inner join publisher p on b.publisher_id=p.id "
                + "where genre_id=" + selectedGenreId + " order by b.name ");
    }
    
    public void fillBooksByLetter() {
        isEditorMode=false;
        cansleBookEditing();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        selectedLetter = params.get("letter").charAt(0);
        submitValues(selectedLetter, 1, -1, false,false,true);
        fillBooksBySQL("select b.id,b.name,b.isbn,b.page_count,b.publish_year, p.name as publisher, a.fio as author, g.name as genre, b.descr, b.image from book b "
                + "inner join author a on b.author_id=a.id "
                + "inner join genre g on b.genre_id=g.id "
                + "inner join publisher p on b.publisher_id=p.id "
                + "where substr(b.name,1,1)='" + selectedLetter + "' order by b.name ");
    }
    
    public void fillBooksBySearch() {
        submitValues(' ', 1, -1, false,false,true);
        if (searchString.trim().length() == 0) {
            fillBooksAll();
            return;
        }
        
        StringBuilder sql = new StringBuilder("select b.descr, b.id,b.name,b.isbn,b.page_count,b.publish_year, p.name as publisher, a.fio as author, g.name as genre, b.image from book b "
                + "inner join author a on b.author_id=a.id "
                + "inner join genre g on b.genre_id=g.id "
                + "inner join publisher p on b.publisher_id=p.id ");
        
        if (searchType == SearchType.AUTHOR) {
            sql.append("where lower(a.fio) like '%" + searchString.toLowerCase() + "%' order by b.name ");
            
        } else if (searchType == SearchType.TITLE) {
            sql.append("where lower(b.name) like '%" + searchString.toLowerCase() + "%' order by b.name ");
        }
        fillBooksBySQL(sql.toString());
        selectedLetter = ' ';
        selectedPageNumber = 1;
        selectedGenreId = -1;
    }
    
    private void fillBooksAll() {
        submitValues(' ', 1, -1, false,false,false);
        fillBooksBySQL("select b.id,b.name,b.isbn,b.page_count,b.publish_year, p.name as publisher, b.descr, "
                + "a.fio as author, g.name as genre, b.image from book b inner join author a on b.author_id=a.id "
                + "inner join genre g on b.genre_id=g.id inner join publisher p on b.publisher_id=p.id order by b.name");
        
    }
//</editor-fold>
    
     private void cansleBookEditing() {
        for (Book book : currentBookList) {
            book.setIsEditing(false);
        }
    }
    
    private void submitValues(Character selectedLetter, long selectedPageNumber, int selectedGenreId, boolean isFromPages, boolean isEditorMode, boolean cancelBookEditing) {
        this.selectedLetter = selectedLetter;
        this.selectedPageNumber = selectedPageNumber;
        this.selectedGenreId = selectedGenreId;
        this.isFromPages = isFromPages;
        this.isEditorMode=isEditorMode;
        if(cancelBookEditing){cansleBookEditing();}

    }

    public void changeBooksCountOnPage(ValueChangeEvent e) {
        booksOnPage = Integer.valueOf(e.getNewValue().toString()).intValue();
        isEditorMode = false;
        cansleBookEditing();
        isFromPages = false;
        selectedPageNumber=1;
        fillBooksBySQL(currentSql);
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
        pageNumbers.clear();
        for (int i = 1; i <= pageCount; i++) {
            pageNumbers.add(i);
        }

    }
    
    public void switchEditorMode() {
        isEditorMode = !isEditorMode;
    }

//<editor-fold defaultstate="collapsed" desc="Books's content and image taking by sql from DB">
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
//</editor-fold>


//<editor-fold defaultstate="collapsed" desc="Getters and setters">
    public SearchType getSearchType() {
        return searchType;
    }
    
    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }
    
    public void setCurrentBookList(ArrayList<Book> currentBookList) {
        this.currentBookList = currentBookList;
    }
    
    public ArrayList<Book> getCurrentBookList() {
        return currentBookList;
        
    }
    
    public int getBooksOnPage() {
        return booksOnPage;
    }
    
    public void setBooksOnPage(int booksOnPage) {
        this.booksOnPage = booksOnPage;
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
    
    public long getSelectedPageNumber() {
        return selectedPageNumber;
    }
    
    public void setSelectedPageNumber(long selectedPageNumber) {
        this.selectedPageNumber = selectedPageNumber;
    }
    
    public long getTotalBooksCount() {
        return totalBooksCount;
    }
    
    public void setTotalBooksCount(long totalBooksCount) {
        this.totalBooksCount = totalBooksCount;
    }
    
    public ArrayList<Integer> getPageNumbers() {
        return pageNumbers;
    }
    
    public void setPageNumbers(ArrayList<Integer> pageNumbers) {
        this.pageNumbers = pageNumbers;
    }
    
    public String getCurrentSql() {
        return currentSql;
    }
    
    public void setCurrentSql(String currentSql) {
        this.currentSql = currentSql;
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
//</editor-fold>

    
}
