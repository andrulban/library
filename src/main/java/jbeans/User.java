/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jbeans;

import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@SessionScoped
@ManagedBean

public class User implements Serializable {

    private String username;
    private String password;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String goHome() {
        return "index";
    }

    public String goToBooks() {
        return "books";
    }

    public String login() {
        try {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            if (request.getUserPrincipal() == null || (request.getUserPrincipal() != null && !request.getUserPrincipal().getName().equals(username))) {
                request.logout();
                request.login(username, password);
            }
            return "tobooksfromindex";
        } catch (ServletException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            ResourceBundle rs = ResourceBundle.getBundle("propertiesFiles/messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
            FacesMessage message = new FacesMessage(rs.getString("index_invalidLogOrPas"));
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage("login_form", message);

        }
        return "index";
    }

    public String logout() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            request.logout();
        } catch (ServletException e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
            return "index";
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
