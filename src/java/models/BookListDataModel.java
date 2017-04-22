/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import db_hibernate.DBHelper;
import entity_hibernate.Book;
import java.util.List;
import java.util.Map;
import jbeans.Pager;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author andrusha
 */
public class BookListDataModel extends LazyDataModel<Book>{
    Pager pager = Pager.getInstace();
    DBHelper dBHelper = DBHelper.getInstance();
    List<Book> list;
    
    

    @Override
    public Object getRowKey(Book book) {
        return book.getId();
    }

    @Override
    public Book getRowData(String rowKey) {
        int key = Integer.valueOf(rowKey);
        for (Book book: pager.getList()) {
            if(book.getId()==key) {
                return book;
            }
        }
        return null;
    }

    @Override
    public List<Book> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        pager.setFrom(first);
        pager.setTo(pageSize);
     
        dBHelper.populateList();
        String longString = String.valueOf(pager.getTotalBooksCount());
        int smth = Integer.valueOf(longString);
        this.setRowCount(smth);  
        
        return pager.getList();        
    }
    
    
}
