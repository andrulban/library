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

/**
 *
 * @author andrusha
 */
@ManagedBean
@SessionScoped

public class LocaleChanger implements Serializable{
    private Locale locale; 
    
    public LocaleChanger() {
        locale = new Locale("en");
    }

    public Locale getLocale() {
        return locale;
    }
    public void changeLocale(String localeName) {        
        locale = new Locale(localeName);
        HttpServletRequest request =(HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        BookListController sc =(BookListController) request.getSession().getAttribute("bookListController");
        if(sc!=null) {
        sc.setButtonLocale(locale);}
    }    
    
}
