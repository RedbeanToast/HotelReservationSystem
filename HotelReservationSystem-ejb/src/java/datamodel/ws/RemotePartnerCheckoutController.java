package datamodel.ws;

import ejb.session.stateful.PartnerCheckoutController;
import ejb.session.stateful.PartnerCheckoutControllerLocal;
import java.util.Date;



public class RemotePartnerCheckoutController 
{
    private String sessionKey;
    private PartnerCheckoutController partnerCheckoutController;
    private Date expiryDateTime;

    
    
    public RemotePartnerCheckoutController() 
    {
    }

    
    
    public RemotePartnerCheckoutController(String sessionKey, PartnerCheckoutControllerLocal partnerCheckoutControllerLocal, Date expiryDateTime) 
    {
        this.sessionKey = sessionKey;
        this.partnerCheckoutController = partnerCheckoutController;
        this.expiryDateTime = expiryDateTime;
    }

    
    
    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public PartnerCheckoutController getPartnerCheckoutControllerLocal() {
        return partnerCheckoutController;
    }

    public void setPartnerCheckoutControllerLocal(PartnerCheckoutController checkoutControllerLocal) {
        this.partnerCheckoutController = checkoutControllerLocal;
    }

    public Date getExpiryDateTime() {
        return expiryDateTime;
    }

    public void setExpiryDateTime(Date expiryDateTime) {
        this.expiryDateTime = expiryDateTime;
    }
}