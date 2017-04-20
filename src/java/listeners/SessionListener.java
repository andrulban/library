/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import java.util.HashMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Web application lifecycle listener.
 *
 * @author andrusha
 */
public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
       HttpSession session = se.getSession();
       ServletContext context = session.getServletContext();
       HashMap<String, HttpSession> sessionMap = (HashMap<String, HttpSession>) context.getAttribute("sessionMap");
       sessionMap.put(session.getId(), session);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
       ServletContext context = session.getServletContext();
       HashMap<String, HttpSession> sessionMap = (HashMap<String, HttpSession>) context.getAttribute("sessionMap");
       sessionMap.remove(session.getId());
    }
}
