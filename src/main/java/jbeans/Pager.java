package jbeans;

import entity.ext.BookExt;
import java.util.List;

public class Pager {

    private long totalBooksCount;
    private BookExt selectedBook;
    private int rowIndex;
    private int from;
    private int to;
    private List<BookExt> list;

    public Pager() {

    }    
   
    public List<BookExt> getList() {
        return list;
    }

    public void setList(List<BookExt> list) {
        rowIndex=-1;
        this.list = list;
    }

    public void setTotalBooksCount(long totalBooksCount) {
        this.totalBooksCount = totalBooksCount;
    }

    public long getTotalBooksCount() {
        return totalBooksCount;
    }

    public BookExt getSelectedBook() {
        return selectedBook;
    }

    public void setSelectedBook(BookExt selectedBook) {
        this.selectedBook = selectedBook;
    }

    public int getRowIndex() {
        return ++rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex=rowIndex;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

}
