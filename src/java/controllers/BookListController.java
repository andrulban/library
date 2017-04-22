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
import javax.faces.application.FacesMessage;
import jbeans.ConnectionDB;
import jbeans.DataGridBooks;
import jbeans.Pager;
import models.BookListDataModel;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

@ManagedBean(eager = true)
@SessionScoped
public class BookListController implements Serializable {

   // private DataTable dataTable;
    private DataGridBooks dataGrid;
    DBHelper dBHelper = DBHelper.getInstance();
    private int selectedGenreId; // выбранный жанр
    private char selectedLetter; // выбранная буква алфавита
    private HashMap<String, SearchType> searchList = new HashMap<>();
    BookListDataModel bookListDataModel;
    private SearchType searchType;
    private String searchString;
    private boolean isEditorMode = false;
    Pager pager = Pager.getInstace();
    Book selectedBook;

    public BookListController() {
        dataGrid=new DataGridBooks();
        bookListDataModel = new BookListDataModel();
        ResourceBundle bundle = ResourceBundle.getBundle("propertiesFiles.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        searchList.put(bundle.getString("author"), SearchType.AUTHOR);
        searchList.put(bundle.getString("title"), SearchType.TITLE);
    }

//<editor-fold defaultstate="collapsed" desc="Filling of bookList by deffrent types">
    public void fillBooksByGenre() {
        isEditorMode = false;
        cancleBookEditing();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        submitValues(' ', Integer.valueOf(params.get("genre_id")), false, false, true);
        dBHelper.getBooksByGenre((long) selectedGenreId);
    }

    public void fillBooksByLetter() {
        isEditorMode = false;
        cancleBookEditing();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        submitValues(params.get("letter").charAt(0), -1, false, false, true);
        dBHelper.getBooksByLetter(selectedLetter);
    }

    public void fillBooksBySearch() {
        submitValues(' ', -1, false, false, true);
        if (searchString.trim().length() == 0) {
            fillBooksAll();
            return;
        }

        if (searchType == SearchType.AUTHOR) {
            dBHelper.getBooksByAuthor(searchString);
        } else if (searchType == SearchType.TITLE) {
            dBHelper.getBooksByName(searchString);
        }
    }

    private void fillBooksAll() {
        submitValues(' ', -1, false, false, false);
        dBHelper.getAllBooks();
    }
//</editor-fold>

    public void updatebooklist() {
        dBHelper.update(selectedBook);
        cancleBookEditing();
        dBHelper.populateList();

        RequestContext.getCurrentInstance().execute("PF('dlgEditBook').hide()");

        ResourceBundle bundle = ResourceBundle.getBundle("propertiesFiles.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(bundle.getString("updated")));
        dataGrid.getDataTable().setFirst(calcSelectedPage());
    }

    public void deleteBook() {
        dBHelper.deleteBook(selectedBook);
        dBHelper.populateList();

        ResourceBundle bundle = ResourceBundle.getBundle("propertiesFiles.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(bundle.getString("deleted")));
        dataGrid.getDataTable().setFirst(calcSelectedPage());

    }

    public void cancleBookEditing() {
        isEditorMode = false;
    }

    private int calcSelectedPage() {
        int page = dataGrid.getDataTable().getPage();// текущий номер страницы (индексация с нуля)

        int leftBound = pager.getTo() * (page - 1);
        int rightBound = pager.getTo() * page;

        boolean goPrevPage = pager.getTotalBooksCount() > leftBound & pager.getTotalBooksCount() <= rightBound;

        if (goPrevPage) {
            page--;
        }

        return (page > 0) ? page * pager.getTo() : 0;
    }

    private void submitValues(Character selectedLetter, int selectedGenreId, boolean isFromPages, boolean isEditorMode, boolean cancelBookEditing) {
        this.selectedLetter = selectedLetter;
        this.selectedGenreId = selectedGenreId;
        this.isEditorMode = isEditorMode;
        dataGrid.getDataTable().setFirst(0);
        if (cancelBookEditing) {
            cancleBookEditing();
        }

    }

    public void changeBooksCountOnPage(ValueChangeEvent e) {
        isEditorMode = false;
        cancleBookEditing();
        dBHelper.populateList();
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

    public void switchEditorMode() {
        isEditorMode = !isEditorMode;
    }

    public void cancelEditMode() {
        isEditorMode = false;
        RequestContext.getCurrentInstance().execute("PF('bookEditDialog').hide();");

    }

    public void switchEditMode() {
        isEditorMode = true;
        RequestContext.getCurrentInstance().execute("PF('bookEditDialog').show();");

    }
    
    
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

    public Pager getPager() {
        return pager;
    }

    public void setPager(Pager pager) {
        this.pager = pager;
    }

    public BookListDataModel getBookListDataModel() {
        return bookListDataModel;
    }

    public void setBookListDataModel(BookListDataModel bookListDataModel) {
        this.bookListDataModel = bookListDataModel;
    }

    public DBHelper getdBHelper() {
        return dBHelper;
    }

    public void setdBHelper(DBHelper dBHelper) {
        this.dBHelper = dBHelper;
    }

    public Book getSelectedBook() {
        return selectedBook;
    }

    public void setSelectedBook(Book selectedBook) {
        this.selectedBook = selectedBook;
    }
    

 
   

    public DataGridBooks getDataGrid() {        
        return dataGrid; 
    }

    public void setDataGrid(DataGridBooks dataGrid) {
        this.dataGrid = dataGrid;
    }
    
     //</editor-fold>

}
