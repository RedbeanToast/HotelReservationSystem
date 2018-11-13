package ejb.session.singleton;

import datamodel.ws.RemotePartnerCheckoutController;
import ejb.session.stateful.PartnerCheckoutController;
import ejb.session.stateful.PartnerCheckoutControllerLocal;
import exceptions.RemotePartnerCheckoutControllerNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Singleton
@LocalBean

public class RemotePartnerCheckoutManager {

    private List<RemotePartnerCheckoutController> remotePartnerCheckoutControllers;

    public RemotePartnerCheckoutManager() {
        remotePartnerCheckoutControllers = new ArrayList<>();
    }

    @Lock(LockType.WRITE)
    public String createNewRemotePartnerCheckoutController() {
        PartnerCheckoutControllerLocal partnerCheckoutControllerLocal = lookupPartnerCheckoutControllerLocal();

        if (partnerCheckoutControllerLocal != null) {
            String sessionKey = UUID.randomUUID().toString();
            Date expiryDateTime = new Date();
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(expiryDateTime);
            gregorianCalendar.add(GregorianCalendar.MINUTE, 15);
            expiryDateTime = gregorianCalendar.getTime();
            RemotePartnerCheckoutController remotePartnerCheckoutController = new RemotePartnerCheckoutController(sessionKey, partnerCheckoutControllerLocal, expiryDateTime);
            remotePartnerCheckoutControllers.add(remotePartnerCheckoutController);

            System.out.println("********** RemoteCheckoutManager.createNewRemoteCheckoutController(): " + sessionKey);

            return sessionKey;
        } else {
            return null;
        }
    }

    @Lock(LockType.READ)
    public PartnerCheckoutController retrieveRemotePartnerCheckoutController(String sessionKey) throws RemotePartnerCheckoutControllerNotFoundException {
        for (RemotePartnerCheckoutController remotePartnerCheckoutController : remotePartnerCheckoutControllers) {
            if (remotePartnerCheckoutController.getSessionKey().equals(sessionKey)) {
                return remotePartnerCheckoutController.getPartnerCheckoutControllerLocal();
            }
        }

        throw new RemotePartnerCheckoutControllerNotFoundException("The remote checkout session " + sessionKey + " either does not exist or has expired");
    }

    @Lock(LockType.WRITE)
    public void removeRemotePartnerCheckoutController(String sessionKey) {
        RemotePartnerCheckoutController remotePartnerCheckoutControllerToRemove = null;

        for (RemotePartnerCheckoutController remoteCheckoutController : remotePartnerCheckoutControllers) {
            if (remoteCheckoutController.getSessionKey().equals(sessionKey)) {
                remotePartnerCheckoutControllerToRemove = remoteCheckoutController;

                break;
            }
        }

        if (remotePartnerCheckoutControllerToRemove != null) {
            remotePartnerCheckoutControllerToRemove.getPartnerCheckoutControllerLocal().remove();
            remotePartnerCheckoutControllerToRemove.setPartnerCheckoutControllerLocal(null);
            remotePartnerCheckoutControllers.remove(remotePartnerCheckoutControllerToRemove);
        }
    }

    @Schedule(hour = "*", minute = "*/1")
    @Lock(LockType.WRITE)
    public void removeExpiredRemoteCheckoutControllers() {
        System.out.println("********** RemoteCheckoutManager.removeExpiredRemoteCheckoutControllers()");

        List<RemotePartnerCheckoutController> expiredRemoteCheckoutControllers = new ArrayList<>();

        for (RemotePartnerCheckoutController remoteCheckoutController : remotePartnerCheckoutControllers) {
            System.out.println("********** Checking: " + remoteCheckoutController.getSessionKey() + "; " + remoteCheckoutController.getExpiryDateTime());

            if (remoteCheckoutController.getExpiryDateTime().compareTo(new Date()) < 0) {
                expiredRemoteCheckoutControllers.add(remoteCheckoutController);
            }
        }

        if (!expiredRemoteCheckoutControllers.isEmpty()) {
            remotePartnerCheckoutControllers.removeAll(expiredRemoteCheckoutControllers);
        }
    }

    private PartnerCheckoutControllerLocal lookupPartnerCheckoutControllerLocal() {
        try {
            Context c = new InitialContext();
            PartnerCheckoutControllerLocal partnerCheckoutControllerLocal = (PartnerCheckoutControllerLocal) c.lookup("java:module/PartnerCheckoutController!ejb.session.stateful.PartnerCheckoutControllerLocal");

            return partnerCheckoutControllerLocal;
        } catch (NamingException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
