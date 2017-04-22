/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validators;

import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;


@FacesValidator("validators/LoginValidator")
public class LoginValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("propertiesFiles/messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        try {
            String valString = value.toString();
            if (valString.trim().length() < 5) {
                throw new IllegalArgumentException(resourceBundle.getString("error_length")); 
            }
            else if (!Character.isLetter(valString.charAt(0))) {
            throw new IllegalArgumentException(resourceBundle.getString("error_firstLetter")); 
            }
            else if (valString.trim().toLowerCase().equals("login")||valString.trim().toLowerCase().equals("username")) {
            throw new IllegalArgumentException(resourceBundle.getString("error_LogUsr"));
            }
        } catch (IllegalArgumentException ex) {
            FacesMessage message = new FacesMessage(ex.getMessage());
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }
}
