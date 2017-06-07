/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author andrusha
 */
@ManagedBean
@SessionScoped
public class ImageController implements Serializable {

    private byte[] uploadedImage;
    UploadedFile file;
    @ManagedProperty(value = "#{bookListController}")
    private BookListController bookListController;

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
    /**
     *Za pomocy tej metody zalaczmy zdjecie pod czas dodawania ksiazki
     */
    public void upload() {      
        if(file.getContents().length==0) {
            return;
        }
        bookListController.getSelectedBook().setImage(file.getContents());
        bookListController.getSelectedBook().setImageEdited(true);
        file = null;
    }
    
    public StreamedContent getDefaultImage() {
        return getStreamedContent(bookListController.getSelectedBook().getImage());
    }
    /**
     * Za pomocy tej metody wyswietlamy zdjecie ksiazki
     * @param image
     * @return 
     */
    private DefaultStreamedContent getStreamedContent(byte[] image) {
        if (image == null) {
            return null;
        }
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(image);
            return new DefaultStreamedContent(inputStream, "image/png");

        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(ImageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public BookListController getBookListController() {
        return bookListController;
    }

    public void setBookListController(BookListController bookListController) {
        this.bookListController = bookListController;
    }
}
