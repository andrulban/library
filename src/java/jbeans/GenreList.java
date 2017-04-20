/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jbeans;

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
public class GenreList {

    private ArrayList<Genre> genrelist;
    private Genre genre;
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    public GenreList() {
    }

    private ArrayList<Genre> getGenres() {
        try {
            conn = DriverManager.getConnection(ConnectionDB.url, ConnectionDB.user, ConnectionDB.password);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from genre");
            while (rs.next()) {
                genre = new Genre();
                genre.setName(rs.getString("name"));
                genre.setId(rs.getLong("id"));
                genrelist.add(genre);
            }
        } catch (SQLException e) {
            System.out.println("SQLException GenreList");
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

        return genrelist;
    }

    public ArrayList<Genre> getGenresList() {
        if (genrelist != null) {
            return genrelist;
        } else {
            genrelist = new ArrayList<>();
            return getGenres();
        }
    }
}
