package jbeans;

import entity_hibernate.Book;
import java.util.List;

public class Pager {

    private long totalBooksCount;
    private Book selectedBook;
    private int rowIndex;
    private int from;
    private int to;
    private List<Book> list;
    private static Pager pager;

    private Pager() {

    }    

    public static Pager getInstace() {
        if(pager==null) {
            return pager=new Pager();
        }
        else {
            return pager;
        }
    }
    public List<Book> getList() {
        return list;
    }

    public void setList(List<Book> list) {
        rowIndex=-1;
        this.list = list;
    }

    public void setTotalBooksCount(long totalBooksCount) {
        this.totalBooksCount = totalBooksCount;
    }

    public long getTotalBooksCount() {
        return totalBooksCount;
    }

    public Book getSelectedBook() {
        return selectedBook;
    }

    public void setSelectedBook(Book selectedBook) {
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
