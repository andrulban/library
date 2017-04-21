package jbeans;

import java.util.ArrayList;
import java.util.List;

public class Pager<T> {

    private int selectedPageNumber = 1;
    private int booksOnPage = 5;
    private long totalBooksCount;
    
    private List<T> list;

    public int getFrom() {
        return selectedPageNumber * booksOnPage - booksOnPage;
    }

    public int getTo() {
        return booksOnPage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void setTotalBooksCount(long totalBooksCount) {
        this.totalBooksCount = totalBooksCount;
    }

    public long getTotalBooksCount() {
        return totalBooksCount;
    }

    public void setSelectedPageNumber(int selectedPageNumber) {
        this.selectedPageNumber = selectedPageNumber;
    }

    public int getSelectedPageNumber() {
        return selectedPageNumber;
    }
    private List<Integer> pageNumbers = new ArrayList<Integer>();

    public List<Integer> getPageNumbers() {// кол-во страниц для постраничности

        int pageCount = 0;

        if (totalBooksCount % booksOnPage == 0) {
            pageCount = booksOnPage > 0 ? (int) (totalBooksCount / booksOnPage) : 0;
        } else {
            pageCount = booksOnPage > 0 ? (int) (totalBooksCount / booksOnPage) + 1 : 0;
        }

        pageNumbers.clear();

        for (int i = 1; i <= pageCount; i++) {
            pageNumbers.add(i);
        }

        return pageNumbers;
    }

    public int getBooksOnPage() {
        return booksOnPage;
    }

    public void setBooksOnPage(int booksCountOnPage) {
        this.booksOnPage = booksCountOnPage;
    }

}
