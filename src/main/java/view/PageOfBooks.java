package view;

import entity.ext.BookExt;
import java.util.List;

public class PageOfBooks {  //model a same List w ktorym sa wszystkie ksiazki ktory sa wyswietlane na stronie books

    private List<BookExt> listOfBookExts;
    private BookExt currentBookExt;
    private long totalBooksCount;
    private int rowIndex;
    private int start;
    private int amountOnOnePage;


    public List<BookExt> getListOfBookExts() {
        return listOfBookExts;
    }

    public void setListOfBookExts(List<BookExt> listOfBookExts) {
        rowIndex = -1;
        this.listOfBookExts = listOfBookExts;
    }

    public void setTotalBooksCount(long totalBooksCount) {
        this.totalBooksCount = totalBooksCount;
    }

    public long getTotalBooksCount() {
        return totalBooksCount;
    }

    public BookExt getCurrentBookExt() {
        return currentBookExt;
    }

    public void setCurrentBookExt(BookExt currentBookExt) {
        this.currentBookExt = currentBookExt;
    }

    public int getRowIndex() {
        return ++rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getAmountOnOnePage() {
        return amountOnOnePage;
    }

    public void setAmountOnOnePage(int amountOnOnePage) {
        this.amountOnOnePage = amountOnOnePage;
    }

}
