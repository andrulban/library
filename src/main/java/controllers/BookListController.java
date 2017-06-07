/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import db_hibernate.DBHelper;
import entity.ext.BookExt;
import enums.ButtonTypeOfSearch;
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
import jbeans.BookDG;
import view.PageOfBooks;
import dataModel.BookListDataModel;
import org.primefaces.context.RequestContext;

@ManagedBean(eager = true)
@SessionScoped
public class BookListController implements Serializable {

    // private DataTable dataTable;
    private BookDG dataGridBooks;
    private DBHelper dBHelper;
    private int selectedGenreId; // выбранный жанр
    private char selectedLetter; // выбранная буква алфавита
    private HashMap<String, ButtonTypeOfSearch> searchList = new HashMap<>();
    private BookListDataModel bookListDataModel;
    private ButtonTypeOfSearch buttonTypeOfSearch;
    private String searchString;
    private boolean isEditorMode = false;
    private boolean isAddMode = false;
    private PageOfBooks pageOfBooks;
    private BookExt selectedBook;

    /**
     * Tworzymy glowny controller, objekt DAO (dBHelper), dataModel (bookListDataModel), i napisy na searchButtonie
     */
    public BookListController() { 
        pageOfBooks = new PageOfBooks();
        dBHelper = new DBHelper(pageOfBooks);
        bookListDataModel = new BookListDataModel(pageOfBooks, dBHelper);
        ResourceBundle bundle = ResourceBundle.getBundle("propertiesFiles.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        searchList.put(bundle.getString("author"), ButtonTypeOfSearch.AUTHORNAME);
        searchList.put(bundle.getString("title"), ButtonTypeOfSearch.BOOKTITLE);
    }

//<editor-fold defaultstate="collapsed" desc="Napelniamy liste ksiazek w rozne sposoby">
    /**
     * Napelnia liste ksiazek po gatunku
     */
    public void fillBooksByGenre() {
        cancleBookEditing();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        submitValues(' ', Integer.valueOf(params.get("genre_id")), false, false, true);
        dBHelper.getBooksByGenre((long) selectedGenreId);
    }
    /**
     * Napelnia liste ksiazek po literce
     */
    public void fillBooksByLetter() { 
        cancleBookEditing();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        submitValues(params.get("letter").charAt(0), -1, false, false, true);
        dBHelper.getBooksByLetter(selectedLetter);
    }
    /**
     * Napelnia liste ksiazek po nazwie lub imieni autora
     */
    public void fillBooksBySearch() {   
        cancleBookEditing();
        submitValues(' ', -1, false, false, true);
        if (searchString.trim().length() == 0) {
            fillBooksAll();
            return;
        }

        if (buttonTypeOfSearch == ButtonTypeOfSearch.AUTHORNAME) {
            dBHelper.getBooksByAuthor(searchString);
        } else if (buttonTypeOfSearch == ButtonTypeOfSearch.BOOKTITLE) {
            dBHelper.getBooksByName(searchString);
        }
    }
    /**
     * Sotruje liste ksiazek  po nowosci
     */
    public void fillBooksByFreshness() {
        cancleBookEditing();
        submitValues(' ', -1, false, false, false);
        dBHelper.SortAllBooks('F');
    }
    /**
     * Napelnia liste ksiazek wszystkimi ksiazki
     */
    private void fillBooksAll() {   
        submitValues(' ', -1, false, false, false);
        dBHelper.getAllBooks();
    }
//</editor-fold>
    /**
     * Sprawdza pol danych pod czas redagowania lub dodawania ksiazki
     */
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

            if (selectedBook.getContent()== null) {
                failValidation("error_load_pdf");
                return false;
            }

            if (selectedBook.getImage()== null) {
                failValidation("error_load_image");
                return false;
            }

        }

        return true;

    }
    /**
     * Metoda wywolywana kiedy nie przeszla walidacja
     */
    private void failValidation(String message) {       
        FacesContext.getCurrentInstance().validationFailed();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "error"));
        cancelModes();
    }
    /**
     * Mini comparator
     */
    private boolean isNullOrEmpty(Object obj) { 
        if (obj == null || obj.toString().equals("")) {
            return true;
        }

        return false;
    }
    /**
     * Doawaje ksiazki do BD
     */
    public void addBook() { //
        if (!validateFields()) {
            return;
        }
        dBHelper.add(selectedBook);
        dBHelper.populateList();

        cancelModes();

        ResourceBundle bundle = ResourceBundle.getBundle("propertiesFiles.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(bundle.getString("added_new_book")));
        dataGridBooks.getDataGrid().setFirst(calcSelectedPage());
    }
    /**
     * Redaguje ksiazke
     */
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
    /**
     * Wyrzuca ksiazke
     */
    public void deleteBook() {      
        dBHelper.deleteBook(selectedBook);
        dBHelper.populateList();

        ResourceBundle bundle = ResourceBundle.getBundle("propertiesFiles.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(bundle.getString("deleted")));
        dataGridBooks.getDataGrid().setFirst(calcSelectedPage());

    }
    /**
     * Wylicza ilosc potrzebnych do wyswietlenia ksiazek stron
     */
    private int calcSelectedPage() {
        int page = dataGridBooks.getDataGrid().getPage();

        int leftBound = pageOfBooks.getAmountOnOnePage()* (page - 1);
        int rightBound = pageOfBooks.getAmountOnOnePage() * page;

        boolean goPrevPage = pageOfBooks.getTotalBooksCount() > leftBound & pageOfBooks.getTotalBooksCount() <= rightBound;

        if (goPrevPage) {
            page--;
        }

        return (page > 0) ? page * pageOfBooks.getAmountOnOnePage() : 0;
    }
    /**
     * Wywoluje sie po wyszukiwaniu ksiazki
     */
    private void submitValues(Character selectedLetter, int selectedGenreId, boolean isFromPages, boolean isEditorMode, boolean cancelBookEditing) {
        this.selectedLetter = selectedLetter;   
        this.selectedGenreId = selectedGenreId;
        this.isEditorMode = isEditorMode;
        dataGridBooks.getDataGrid().setFirst(0);
        if (cancelBookEditing) {
            cancleBookEditing();
        }

    }
    /**
     * Wywoluje sie kiedy konczymy redagowanie ksiazki
     */
    public void cancleBookEditing() {   
        isEditorMode = false;
    }
    /**
     * Zmieniamy jezyk tekstu na searchButton
     */
    public void setButtonLocale(Locale locale) {    
        ResourceBundle bundle = ResourceBundle.getBundle("propertiesFiles.messages", locale);
        searchList = new HashMap<>();
        searchList.put(bundle.getString("author"), ButtonTypeOfSearch.AUTHORNAME);
        searchList.put(bundle.getString("title"), ButtonTypeOfSearch.BOOKTITLE);
    }
    /**
     * Przy zmianie jezyka zmienia litery wyswietlane na stronie, za pomocy ktorych mozna szukac po pierwszej literce w nazwie
     */
    public Character[] getRussianLetters() {    
        Character[] letters = null;
        if (FacesContext.getCurrentInstance().getELContext().getLocale().getISO3Language().equals("rus")) {
            letters = new Character[]{'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я',};
        } else if (FacesContext.getCurrentInstance().getELContext().getLocale().getISO3Language().equals("eng")) {
            letters = new Character[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        } else if (FacesContext.getCurrentInstance().getELContext().getLocale().getISO3Language().equals("pol")) {
            letters = new Character[]{'A', 'Ą', 'B', 'C', 'Ć', 'D', 'E', 'Ę', 'D', 'F', 'H', 'I', 'J', 'K', 'L', 'Ł', 'M', 'N', 'Ń', 'O', 'Ó', 'P', 'R', 'S', 'Ś', 'T', 'U', 'W', 'Y', 'Z', 'Ź', 'Ż'};
        }

        return letters;
    }

    

    public void switchEditorMode() {
        isEditorMode = !isEditorMode;
    }
    /**
     * Zamyka okienko do redagowania lub dodawania ksiazki
     */
    public void cancelModes() {
        isEditorMode = false;
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
    public ButtonTypeOfSearch getButtonTypeOfSearch() {
        return buttonTypeOfSearch;
    }

    public void setButtonTypeOfSearch(ButtonTypeOfSearch buttonTypeOfSearch) {
        this.buttonTypeOfSearch = buttonTypeOfSearch;
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

    public HashMap<String, ButtonTypeOfSearch> getSearchList() {
        return searchList;
    }

    public void setSearchList(HashMap<String, ButtonTypeOfSearch> searchList) {
        this.searchList = searchList;
    }

    public boolean getIsEditorMode() {
        return isEditorMode;
    }

    public PageOfBooks getPageOfBooks() {
        return pageOfBooks;
    }

    public void setPageOfBooks(PageOfBooks pageOfBooks) {
        this.pageOfBooks = pageOfBooks;
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

    public BookDG getDataGridBooks() {
        return dataGridBooks = new BookDG();
    }

    public void setDataGridBooks(BookDG dataGrid) {
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
