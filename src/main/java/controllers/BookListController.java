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

    public BookListController() { //tworzymy glowny controller, objekt DAO (dBHelper), dataModel (bookListDataModel), i napisy na searchButtonie
        pageOfBooks = new PageOfBooks();
        dBHelper = new DBHelper(pageOfBooks);
        bookListDataModel = new BookListDataModel(pageOfBooks, dBHelper);
        ResourceBundle bundle = ResourceBundle.getBundle("propertiesFiles.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        searchList.put(bundle.getString("author"), ButtonTypeOfSearch.AUTHORNAME);
        searchList.put(bundle.getString("title"), ButtonTypeOfSearch.BOOKTITLE);
    }

//<editor-fold defaultstate="collapsed" desc="Napelniamy liste ksiazek w rozne sposoby">
    public void fillBooksByGenre() { //po gatunku
        cancleBookEditing();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        submitValues(' ', Integer.valueOf(params.get("genre_id")), false, false, true);
        dBHelper.getBooksByGenre((long) selectedGenreId);
    }

    public void fillBooksByLetter() { //po literce
        cancleBookEditing();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        submitValues(params.get("letter").charAt(0), -1, false, false, true);
        dBHelper.getBooksByLetter(selectedLetter);
    }

    public void fillBooksBySearch() {   //po nazwie lub imieni autora
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

    public void fillBooksByFreshness() { //sortujemy po nowosci
        cancleBookEditing();
        submitValues(' ', -1, false, false, false);
        dBHelper.SortAllBooks('F');
    }

    private void fillBooksAll() {   //pobieramy wszystkie ksiazki
        submitValues(' ', -1, false, false, false);
        dBHelper.getAllBooks();
    }
//</editor-fold>

    private boolean validateFields() { //sprawdzanie pol danych pod czas redagowania lub dodawania ksiazki

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

    private void failValidation(String message) {       //kiedy nie przeszla walidacja
        FacesContext.getCurrentInstance().validationFailed();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "error"));
        cancelModes();
    }

    private boolean isNullOrEmpty(Object obj) { //mini comparator
        if (obj == null || obj.toString().equals("")) {
            return true;
        }

        return false;
    }

    public void addBook() { //doawanie ksiazki
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

    public void updatebooklist() {  //redagowanie ksiazki
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

    public void deleteBook() {      //wyrzucenie ksiazki
        dBHelper.deleteBook(selectedBook);
        dBHelper.populateList();

        ResourceBundle bundle = ResourceBundle.getBundle("propertiesFiles.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(bundle.getString("deleted")));
        dataGridBooks.getDataGrid().setFirst(calcSelectedPage());

    }

    private int calcSelectedPage() {    //wyliczamy ilosc potrzebnych do wyswietlenia ksiazek stron
        int page = dataGridBooks.getDataGrid().getPage();

        int leftBound = pageOfBooks.getAmountOnOnePage()* (page - 1);
        int rightBound = pageOfBooks.getAmountOnOnePage() * page;

        boolean goPrevPage = pageOfBooks.getTotalBooksCount() > leftBound & pageOfBooks.getTotalBooksCount() <= rightBound;

        if (goPrevPage) {
            page--;
        }

        return (page > 0) ? page * pageOfBooks.getAmountOnOnePage() : 0;
    }

    private void submitValues(Character selectedLetter, int selectedGenreId, boolean isFromPages, boolean isEditorMode, boolean cancelBookEditing) {
        this.selectedLetter = selectedLetter;   //metoda wywoluje sie wyszukiwaniu ksiazki
        this.selectedGenreId = selectedGenreId;
        this.isEditorMode = isEditorMode;
        dataGridBooks.getDataGrid().setFirst(0);
        if (cancelBookEditing) {
            cancleBookEditing();
        }

    }

    public void cancleBookEditing() {   //kiedy konczymy redagowanie ksiazki
        isEditorMode = false;
    }

    public void setButtonLocale(Locale locale) {    //zmieniamy jezyk na searchButton
        ResourceBundle bundle = ResourceBundle.getBundle("propertiesFiles.messages", locale);
        searchList = new HashMap<>();
        searchList.put(bundle.getString("author"), ButtonTypeOfSearch.AUTHORNAME);
        searchList.put(bundle.getString("title"), ButtonTypeOfSearch.BOOKTITLE);
    }

    public Character[] getRussianLetters() {    //przy zmianie jezyka zmieniaja sie litery wyswietlane na stronie, za pomocy ktorych mozna szukac po pierwszej literce w nazwie
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

    public void cancelModes() { //zamyka okienko do redagowania lub dodawania ksiazki
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
