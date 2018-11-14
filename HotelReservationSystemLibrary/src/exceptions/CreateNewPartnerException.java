/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author CaiYuqian
 */
public class CreateNewPartnerException extends Exception {

    /**
     * Creates a new instance of <code>CreateNewPartnerException</code> without
     * detail message.
     */
    public CreateNewPartnerException() {
    }

    /**
     * Constructs an instance of <code>CreateNewPartnerException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateNewPartnerException(String msg) {
        super(msg);
    }
}
