/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jbeans;

import org.primefaces.component.datagrid.DataGrid;

/**
 *
 * @author andrusha
 */
public class BookDG {   //dataGrid do strony books
    private DataGrid dg;
    public BookDG() {
        
    }

    public DataGrid getDataGrid() {
        return dg;
    }

    public void setDataGrid(DataGrid dg) {
        this.dg = dg;
    }

    
    
    
}