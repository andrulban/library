/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jbeans;

import enums.SearchType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andrusha
 */
public class BookList {

    private ArrayList<Book> booklist;
    private Book book;
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    private ArrayList<Book> books;
    private final String countString = "count(";
    private final char rightBracket = ')';
    private long amountOfQueryResults = 0;

    public BookList() {
    }

    private ArrayList<Book> getBookListByQuery(String query) {

        try {
            booklist = new ArrayList<>();
            conn = DriverManager.getConnection(ConnectionDB.url, ConnectionDB.user, ConnectionDB.password);
            stmt = conn.createStatement();
            System.out.println(query);
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getLong("id"));
                book.setName(rs.getString("name"));
                book.setGenre(rs.getString("genre"));
                book.setIsbn(rs.getString("isbn"));
                book.setAuthor(rs.getString("author"));
                book.setPageCount(rs.getInt("page_count"));
                book.setPublishDate(rs.getInt("publish_year"));
                book.setPublisher(rs.getString("publisher"));
                book.setImage(rs.getBytes("image"));
                booklist.add(book);
            }

        } catch (SQLException ex) {
            Logger.getLogger(BookList.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BookList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println(booklist);
        return booklist;
    }

    private void calculateAmountOfQueryByQuery(String query) {
        try {
            booklist = new ArrayList<>();
            conn = DriverManager.getConnection(ConnectionDB.url, ConnectionDB.user, ConnectionDB.password);
            stmt = conn.createStatement();
            System.out.println(query);
            rs = stmt.executeQuery(query);
            rs.next();
            amountOfQueryResults = rs.getLong("Amount");
            System.out.println(amountOfQueryResults);
        } catch (SQLException ex) {
            Logger.getLogger(BookList.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BookList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public ArrayList<Book> getBooksByGenre(long id, boolean isFirstPage, long pageNumber) {
        if (isFirstPage) {
            calculateAmountOfQueryByQuery("select count(*) as Amount from book b inner join genre g on b.genre_id=g.id where g.id="+id);
        }
        return getBookListByQuery("select b.id,b.name,b.isbn,b.page_count,b.publish_year,"
                + "p.name as publisher, a.fio as author, g.name as genre, b.image from book b "
                + "inner join author a on b.author_id=a.id "
                + "inner join genre g on b.genre_id=g.id "
                + "inner join publisher p on b.publisher_id=p.id "
                + "where genre_id=" + id + " order by b.name "
                + "limit "+(0+(pageNumber*6))+","+6);
    }

    public ArrayList<Book> getAllBooks(boolean isFirstPage, long pageNumber) {
        if (isFirstPage) {
            calculateAmountOfQueryByQuery("select count(id) as Amount from book");
        }
        return getBookListByQuery("select b.id,b.name,b.isbn,b.page_count,b.publish_year, p.name as publisher, "
                + "a.fio as author, g.name as genre, b.image from book b inner join author a on b.author_id=a.id "
                + "inner join genre g on b.genre_id=g.id inner join publisher p on b.publisher_id=p.id order by b.name "
                + "limit "+(0+(pageNumber*6))+","+6);
    }

    public ArrayList<Book> getBooksByLetter(String letter, boolean isFirstPage, long pageNumber) {
        if (isFirstPage) {
            calculateAmountOfQueryByQuery("select count(*) as Amount from book b where substr(b.name,1,1)='"+letter+"'");
        }
        return getBookListByQuery("select b.id,b.name,b.isbn,b.page_count,b.publish_year, p.name as publisher,"
                + " a.fio as author, g.name as genre, b.image from book b inner join author a on b.author_id=a.id "
                + "inner join genre g on b.genre_id=g.id "
                + "inner join publisher p on b.publisher_id=p.id  "
                + "where substr(b.name,1,1)='" + letter + "' "
                + "order by b.name  "
                + "limit "+(0+(pageNumber*6))+","+6);
    }

    public ArrayList<Book> getBooksBySearch(String searchStr, SearchType type, boolean isFirstPage, long pageNumber) {
        if (isFirstPage) {
            StringBuilder sqlForAmount = new StringBuilder("select count(*) as Amount from book b inner join author a on b.author_id=a.id ");
            if (type == SearchType.AUTHOR) {
                sqlForAmount.append("where lower(a.fio) like '%" + searchStr.toLowerCase() + "%'");

            } else if (type == SearchType.TITLE) {
                sqlForAmount.append("where lower(b.name) like '%" + searchStr.toLowerCase() + "%'");
            }
            calculateAmountOfQueryByQuery(sqlForAmount.toString());
        }
        StringBuilder sql = new StringBuilder("select b.id,b.name,b.isbn,b.page_count,b.publish_year, p.name as publisher, a.fio as author, g.name as genre, b.image from book b "
                + "inner join author a on b.author_id=a.id "
                + "inner join genre g on b.genre_id=g.id "
                + "inner join publisher p on b.publisher_id=p.id ");

        if (type == SearchType.AUTHOR) {
            sql.append("where lower(a.fio) like '%" + searchStr.toLowerCase() + "%' order by b.name ");

        } else if (type == SearchType.TITLE) {
            sql.append("where lower(b.name) like '%" + searchStr.toLowerCase() + "%' order by b.name ");
        }
        sql.append("limit "+(0+(pageNumber*6))+","+6);
        return getBookListByQuery(sql.toString());
    }

    public long getAmountOfQueryResults() {
        return amountOfQueryResults;
    }
}
