/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataModel;

import db_hibernate.DBHelper;
import entity.ext.BookExt;
import java.util.List;
import java.util.Map;
import view.PageOfBooks;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *Za pomocy nastepujacych trzech metod dziala dataGrid na stronie books
 * @author andrusha
 */
public class BookListDataModel extends LazyDataModel<BookExt>{
    PageOfBooks pageOfBooks;
    DBHelper dBHelper;
    List<BookExt> list;
    
    public  BookListDataModel(PageOfBooks pageOfBooks, DBHelper dBHelper) {
        this.pageOfBooks = pageOfBooks;
        this.dBHelper = dBHelper;
    }

    
    @Override
    public Object getRowKey(BookExt book) {
        return book.getId();
    }

    @Override
    public BookExt getRowData(String rowKey) {
        int key = Integer.valueOf(rowKey);
        for (BookExt book: pageOfBooks.getListOfBookExts()) {
            if(book.getId()==key) {
                return book;
            }
        }
        return null;
    }

    @Override
    public List<BookExt> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        pageOfBooks.setStart(first);
        pageOfBooks.setAmountOnOnePage(pageSize);
     
        dBHelper.populateList();
        String longString = String.valueOf(pageOfBooks.getTotalBooksCount());
        int smth = Integer.valueOf(longString);
        this.setRowCount(smth);  
        
        return pageOfBooks.getListOfBookExts();        
    }
    
    
}
