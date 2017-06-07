package controllers;

import db_hibernate.DBHelper;
import entity.ext.PublisherExt;
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
public class PublisherController implements Serializable, Converter {

    @ManagedProperty(value = "#{bookListController}")
    private BookListController bookListController;
    private List<SelectItem> selectItems = new ArrayList<SelectItem>();
    private Map<Long, PublisherExt> map;
    private List<PublisherExt> list;
    private PageOfBooks pageOfBooks;
    private DBHelper dBHelper;
    
    /**
     * Wykorzystuje sie w momencie redagowania ksiazki, zeby lista dawala podpowidz jakie wydawnictwo jest u tej ksiazki
     */
    @PostConstruct
    public void init() {
        pageOfBooks = bookListController.getPageOfBooks();
        dBHelper = bookListController.getdBHelper();

        map = new HashMap<Long, PublisherExt>();
        list = dBHelper.getAllPublishers();

        Collections.sort(list, new Comparator<PublisherExt>() {
            @Override
            public int compare(PublisherExt o1, PublisherExt o2) {
                return o1.toString().compareTo(o2.toString());
            }

        });

        for (PublisherExt publisher : list) {
            map.put(publisher.getId(), publisher);
            selectItems.add(new SelectItem(publisher, publisher.getPublisherName()));
        }

    }

    public List<SelectItem> getSelectItems() {
        return selectItems;
    }

    public List<PublisherExt> getPublisherList() {
        return list;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return map.get(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((PublisherExt) value).getId().toString();
    }

    public BookListController getBookListController() {
        return bookListController;
    }

    public void setBookListController(BookListController bookListController) {
        this.bookListController = bookListController;
    }
}
