/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jbeans;

import controllers.BookListController;
import java.io.Serializable;
import java.util.Locale;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;


@ManagedBean
@SessionScoped
/**
 * Ustalamy jezyk na stronie za pomocy tego objektu
 * @author andrusha
 */
public class LocaleChanger implements Serializable {    //

    private Locale locale;

    /**
     * Z samego poczatku jezyk - Angielski
     */
    public LocaleChanger() {
        locale = new Locale("en");
    }

    public Locale getLocale() {
        return locale;
    }

    /**
     * Ta metoda zmienia jezyki na stronie: na Polski, Angielski lub Rosyjski
     * @param localeName 
     */
    public void changeLocale(String localeName) {   //
        locale = new Locale(localeName);
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        BookListController bl = (BookListController) request.getSession().getAttribute("bookListController");
        if (bl != null) {
            bl.setButtonLocale(locale);
        }
    }

}
