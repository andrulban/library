package controllers;

import db_hibernate.DBHelper;
import entity.ext.AuthorExt;
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
import view.PageOfBooks;

@ManagedBean(eager = false)
@SessionScoped
public class AuthorController implements Serializable, Converter {

    @ManagedProperty(value = "#{bookListController}")
    private BookListController bookListController;
    private List<SelectItem> selectItems = new ArrayList<SelectItem>();
    private Map<Long, AuthorExt> map;
    private List<AuthorExt> list;
    private PageOfBooks pageOfBooks;
    private DBHelper dBHelper;
    

    @PostConstruct
    public void init() {
        pageOfBooks = bookListController.getPageOfBooks();
        dBHelper = bookListController.getdBHelper();

        map = new HashMap<Long, AuthorExt>();
        list = dBHelper.getAllAuthors();
        Collections.sort(list, new Comparator<AuthorExt>() {
            @Override
            public int compare(AuthorExt o1, AuthorExt o2) {
                return o1.toString().compareTo(o2.toString());
            }
        }
        );

        for (AuthorExt author : list) {
            map.put(author.getId(), author);
            selectItems.add(new SelectItem(author, author.getAllNames()));
        }
    }

    public List<AuthorExt> getAuthorList() {
        return list;
    }

    public List<SelectItem> getSelectItems() {
        return selectItems;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return map.get(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((AuthorExt) value).getId().toString();
    }

    public BookListController getBookListController() {
        return bookListController;
    }

    public void setBookListController(BookListController bookListController) {
        this.bookListController = bookListController;
    }

}
