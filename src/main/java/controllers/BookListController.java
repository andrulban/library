/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import db_hibernate.DBHelper;
import entity.ext.BookExt;
import enums.SearchType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpSession;
import jbeans.DataGridBooks;
import jbeans.Pager;
import models.BookListDataModel;
import org.primefaces.context.RequestContext;

@ManagedBean(eager = true)
@SessionScoped
public class BookListController implements Serializable {

    // private DataTable dataTable;
    private DataGridBooks dataGridBooks;
    private DBHelper dBHelper;
    private int selectedGenreId; // выбранный жанр
    private char selectedLetter; // выбранная буква алфавита
    private HashMap<String, SearchType> searchList = new HashMap<>();
    private BookListDataModel bookListDataModel;
    private SearchType searchType;
    private String searchString;
    private boolean isEditorMode = false;
    private boolean isAddMode = false;
    private Pager pager;
    private BookExt selectedBook;

    public BookListController() {
        pager = new Pager();
        dBHelper = new DBHelper(pager);
        bookListDataModel = new BookListDataModel(pager, dBHelper);
        ResourceBundle bundle = ResourceBundle.getBundle("propertiesFiles.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        searchList.put(bundle.getString("author"), SearchType.AUTHOR);
        searchList.put(bundle.getString("title"), SearchType.TITLE);
    }

//<editor-fold defaultstate="collapsed" desc="Filling of bookList by deffrent types">
    public void fillBooksByGenre() {
        cancleBookEditing();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        submitValues(' ', Integer.valueOf(params.get("genre_id")), false, false, true);
        dBHelper.getBooksByGenre((long) selectedGenreId);
    }

    public void fillBooksByLetter() {
        cancleBookEditing();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        submitValues(params.get("letter").charAt(0), -1, false, false, true);
        dBHelper.getBooksByLetter(selectedLetter);
    }

    public void fillBooksBySearch() {
        cancleBookEditing();
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

    public void fillBooksByFreshness() {
        cancleBookEditing();
        submitValues(' ', -1, false, false, false);
        dBHelper.SortAllBooks('F');
    }

    public void fillBooksByRate() {

    }

    private void fillBooksAll() {
        submitValues(' ', -1, false, false, false);
        dBHelper.getAllBooks();
    }
//</editor-fold>

    private boolean validateFields() {

        if (isNullOrEmpty(selectedBook.getAuthor())
                || isNullOrEmpty(selectedBook.getDescr())
                || isNullOrEmpty(selectedBook.getGenre())
                || isNullOrEmpty(selectedBook.getIsbn())
                || isNullOrEmpty(selectedBook.getName())
                || isNullOrEmpty(selectedBook.getPageCount())
                || isNullOrEmpty(selectedBook.getPublishYear())
                || isNullOrEmpty(selectedBook.getPublisher())) {
            failValidation("error_fill_all_fields");
            return false;

        }

        if (dBHelper.isIsbnExist(selectedBook.getIsbn(), selectedBook.getId())) {
            failValidation("error_isbn_exist");
            return false;
        }

        if (isAddMode) {

            if (selectedBook.getContent() == null) {
                failValidation("error_load_pdf");
                return false;
            }

            if (selectedBook.getImage() == null) {
                failValidation("error_load_image");
                return false;
            }

        }

        return true;

    }

    private void failValidation(String message) {
        FacesContext.getCurrentInstance().validationFailed();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "error"));
        cancelModes();
    }

    private boolean isNullOrEmpty(Object obj) {
        if (obj == null || obj.toString().equals("")) {
            return true;
        }

        return false;
    }

    public void addBook() {
        if (!validateFields()) {
            return;
        }
        dBHelper.add(selectedBook);
        dBHelper.populateList();

        cancelAddMode();

        ResourceBundle bundle = ResourceBundle.getBundle("propertiesFiles.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(bundle.getString("added_new_book")));
        dataGridBooks.getDataGrid().setFirst(calcSelectedPage());
    }

    public void updatebooklist() {
        if (!validateFields()) {
            return;
        }
        dBHelper.update(selectedBook);
        dBHelper.populateList();

        cancleBookEditing();

        ResourceBundle bundle = ResourceBundle.getBundle("propertiesFiles.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(bundle.getString("updated")));
        dataGridBooks.getDataGrid().setFirst(calcSelectedPage());
    }

    public void deleteBook() {
        dBHelper.deleteBook(selectedBook);
        dBHelper.populateList();

        ResourceBundle bundle = ResourceBundle.getBundle("propertiesFiles.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(bundle.getString("deleted")));
        dataGridBooks.getDataGrid().setFirst(calcSelectedPage());

    }

    private int calcSelectedPage() {
        int page = dataGridBooks.getDataGrid().getPage();// текущий номер страницы (индексация с нуля)

        int leftBound = pager.getTo() * (page - 1);
        int rightBound = pager.getTo() * page;

        boolean goPrevPage = pager.getTotalBooksCount() > leftBound & pager.getTotalBooksCount() <= rightBound;

        if (goPrevPage) {
            page--;
        }

        return (page > 0) ? page * pager.getTo() : 0;
    }

    public void searchStringChanged(ValueChangeEvent e) {
        searchString = e.getNewValue().toString();
    }

    public void searchTypeChanged(ValueChangeEvent e) {
        searchType = (SearchType) e.getNewValue();
    }

    private void submitValues(Character selectedLetter, int selectedGenreId, boolean isFromPages, boolean isEditorMode, boolean cancelBookEditing) {
        this.selectedLetter = selectedLetter;
        this.selectedGenreId = selectedGenreId;
        this.isEditorMode = isEditorMode;
        dataGridBooks.getDataGrid().setFirst(0);
        if (cancelBookEditing) {
            cancleBookEditing();
        }

    }

    public void cancleBookEditing() {
        isEditorMode = false;
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
        Character [] letters = null;
        if(FacesContext.getCurrentInstance().getELContext().getLocale().getISO3Language().equals("rus")) {
        letters = new Character[]{'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я',};
        }
        else if(FacesContext.getCurrentInstance().getELContext().getLocale().getISO3Language().equals("eng")) {
            letters = new Character[] {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        }
        else if(FacesContext.getCurrentInstance().getELContext().getLocale().getISO3Language().equals("pol")) {
            letters = new Character[] {'A','Ą','B','C','Ć','D','E','Ę','D','F','H','I','J','K','L','Ł','M','N','Ń','O','Ó','P','R','S','Ś','T','U','W','Y','Z','Ź','Ż'};
        }

        return letters;
    }

    public void switchEditorMode() {
        isEditorMode = !isEditorMode;
    }

    public void cancelModes() {
        isEditorMode = false;
        isAddMode = false;
        RequestContext.getCurrentInstance().execute("PF('bookEditDialog').hide();");

    }

    public void cancelAddMode() {
        isAddMode = false;
        RequestContext.getCurrentInstance().execute("PF('bookEditDialog').hide();");

    }

    public void switchEditMode() {
        isEditorMode = true;
        RequestContext.getCurrentInstance().execute("PF('bookEditDialog').show();");

    }

    public void switchAddMode() {
        isAddMode = true;
        selectedBook = new BookExt();
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

    public BookExt getSelectedBook() {
        return selectedBook;
    }

    public void setSelectedBook(BookExt selectedBook) {
        this.selectedBook = selectedBook;
    }

    public DataGridBooks getDataGridBooks() {
        return dataGridBooks = new DataGridBooks();
    }

    public void setDataGridBooks(DataGridBooks dataGrid) {
        this.dataGridBooks = dataGrid;
    }

    public boolean isIsEditorMode() {
        return isEditorMode;
    }

    public void setIsEditorMode(boolean isEditorMode) {
        this.isEditorMode = isEditorMode;
    }

    public boolean isIsAddMode() {
        return isAddMode;
    }

    public void setIsAddMode(boolean isAddMode) {
        this.isAddMode = isAddMode;
    }

    //</editor-fold>
}