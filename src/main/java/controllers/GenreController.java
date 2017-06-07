package controllers;

import db_hibernate.DBHelper;
import view.PageOfBooks;
import entity.ext.GenreExt;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

@ManagedBean(eager = false)
@SessionScoped
public class GenreController implements Serializable, Converter {

    @ManagedProperty(value = "#{bookListController}")
    private BookListController bookListController;
    private List<SelectItem> selectItems = new ArrayList<SelectItem>();
    private Map<Long, GenreExt> map;
    private List<GenreExt> list;
    private PageOfBooks pageOfBooks;
    private DBHelper dBHelper;
    

    /**
     * wykorzystuje sie w momencie redagowania ksiazki, zeby lista dawala podpowidz jaki gatunek jest u tej ksiazki
     */
    @PostConstruct
    public void init() {
        pageOfBooks = bookListController.getPageOfBooks();
        dBHelper = bookListController.getdBHelper();

        map = new HashMap<Long, GenreExt>();
        list = dBHelper.getAllGenres();
        Collections.sort(list, new Comparator<GenreExt>() {
            @Override
            public int compare(GenreExt o1, GenreExt o2) {
                return o1.toString().compareTo(o2.toString());
            }

        });

        for (GenreExt genre : list) {
            map.put(genre.getId(), genre);
            selectItems.add(new SelectItem(genre, genre.getGenreName()));
        }

    }

    public List<SelectItem> getSelectItems() {
        return selectItems;
    }

    public List<GenreExt> getGenreList() {
        return list;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return map.get(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((GenreExt) value).getId().toString();
    }

    public BookListController getBookListController() {
        return bookListController;
    }

    public void setBookListController(BookListController bookListController) {
        this.bookListController = bookListController;
    }

}
