/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jbeans;

import enums.SearchType;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author andrusha
 */
public class RequestParser {
    private long genreId = 0;
    private ArrayList<Book> list = null;
    private BookList bookList = new BookList();
    
    public ArrayList<Book> requestParsBooksList(HttpServletRequest request, boolean isFirst, long pageNumber) {
        if (request.getParameter("genre_id") != null) {
            try {
                genreId = Long.valueOf(request.getParameter("genre_id"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (genreId == 1) {
                //Один только в случае когда нужно вывести все книги
                list = bookList.getAllBooks(isFirst, pageNumber);
            } else {
                list = bookList.getBooksByGenre(genreId, isFirst, pageNumber);
            }
        } else if (request.getParameter("letter") != null) {
            System.out.println(request.getParameter("letter"));
            list = bookList.getBooksByLetter(request.getParameter("letter"), isFirst, pageNumber);
        } else if (request.getParameter("search_string") != null) {
            if (request.getParameter("search_option").equals("Название")) {
                list = bookList.getBooksBySearch(request.getParameter("search_string"), SearchType.TITLE, isFirst, pageNumber);
            } else {
                list = bookList.getBooksBySearch(request.getParameter("search_string"), SearchType.AUTHOR, isFirst, pageNumber);
            }
        } else {
            list = bookList.getAllBooks(isFirst, 1);
        }
        if (isFirst) {
            request.getSession().setAttribute("AmountOfAvailableBooks", bookList.getAmountOfQueryResults());
        }
        return list;
    }
        
}
