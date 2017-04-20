/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jbeans;

import java.sql.Connection;
import java.sql.Driver;
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
public class AuthorList {

    private ArrayList<Author> authorlist;
    private Author author;
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    public AuthorList() {
    }

    private ArrayList<Author> getAuthors() {
        try {
            conn = DriverManager.getConnection(ConnectionDB.url, ConnectionDB.user, ConnectionDB.password);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from author order by fio");
            while (rs.next()) {
                author = new Author();
                author.setName(rs.getString("fio"));
                author.setId(rs.getLong("id"));
                authorlist.add(author);
            }
        } catch (SQLException e) {
            System.out.println("SQLException AuthorList");
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BookList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return authorlist;
    }

    public ArrayList<Author> getAutorsList() {
        if (authorlist != null) {
            return authorlist;
        } else {
            authorlist = new ArrayList<>();
            return getAuthors();
        }
    }
}
