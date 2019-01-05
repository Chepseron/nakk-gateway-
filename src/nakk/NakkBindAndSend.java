package nakk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.jsmpp.SMPPConstant;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageType;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.TimeFormatter;
import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.Session;
import org.jsmpp.util.InvalidDeliveryReceiptException;

public class NakkBindAndSend {

    private TimeFormatter tF = new AbsoluteTimeFormatter();
    Logger LOGGER = Logger.getLogger("MyLog");
    SMPPSession smppSession = new SMPPSession();

    public static void main(String[] args) {
        NakkBindAndSend bindAndSend = new NakkBindAndSend();
        String message = "Welcome to Nakk Am! Over 400 million Leones na ready for u pocket! Game starts 20th December, get ready!";
        bindAndSend.sendTextMessage(args[0], message);
    }

    public NakkBindAndSend() {
        try {
            FileHandler fh = new FileHandler("MyLogFile.log");
            LOGGER.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            smppSession = initSession();
        } catch (IOException ex) {
            Logger.getLogger(MessageReceiverListenerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(MessageReceiverListenerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public NakkBindAndSend(String param) {

    }
    private final String smppIp = "192.168.1.54";
    private int port = 2775;
    private final String username = "625";
    private final String password = "7BzdSud8";

    public String sendTextMessage(String MSISDN, String message) {
        ESMClass esmClass = new ESMClass();
        GeneralDataCoding dataCoding = new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false);
        try {
            System.out.println("sending to " + MSISDN);
            smppSession.submitShortMessage(
                    "CMT",
                    TypeOfNumber.NETWORK_SPECIFIC,
                    NumberingPlanIndicator.UNKNOWN,
                    "625",
                    TypeOfNumber.INTERNATIONAL,
                    NumberingPlanIndicator.ISDN,
                    MSISDN,
                    esmClass,
                    (byte) 0,
                    (byte) 1,
                    tF.format(new Date()),
                    null,
                    new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
                    (byte) 0,
                    dataCoding,
                    (byte) 0,
                    message.getBytes());
            System.out.println("sent message to number " + MSISDN);
            return "sent message to number " + MSISDN;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("++++++++++++++++++++++++++++++++++" + e.getMessage());
            return e.getMessage();
        }
    }

    public SMPPSession initSession() {
        SMPPSession session = new SMPPSession();
        try {
            session.setMessageReceiverListener(new MessageReceiverListenerImpl());
            String systemId = session.connectAndBind(smppIp, Integer.valueOf(port), new BindParameter(BindType.BIND_TRX, username, password, "smpp", TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.NATIONAL, null));
            System.out.println("Connected with SMPP with system id {} " + systemId);
            LOGGER.info("Connected with SMPP with system id {} " + systemId);
        } catch (IOException e) {
            System.out.println("Failed to connect with error " + e);
            LOGGER.info("I/O error occured " + e);
            session = null;
        }
        return session;
    }

}

enum DeliveryReceiptState {

    ESME_ROK(0, "Ok - Message Acceptable"),
    ESME_RINVMSGLEN(1, "Invalid Message Length"),
    ESME_RINVCMDLEN(2, "Invalid Command Length"),
    ESME_RINVCMDID(3, "Invalid Command ID"),
    ESME_RINVBNDSTS(4, "Invalid bind status"),
    ESME_RALYBND(5, "Bind attempted when already bound"),
    ESME_RINVPRTFLG(6, "Invalid priority flag"),
    ESME_RINVREGDLVFLG(7, "Invalid registered-delivery flag"),
    ESME_RSYSERR(8, "SMSC system error"),
    ESME_RINVSRCADR(9, "Invalid source address"),
    ESME_RINVDSTADR(11, "Invalid destination address"),
    ESME_RINVMSGID(12, "Invalid message-id"),
    NOT_FOUND(000, "Couldn't resolve.Ask admin to add.");
    private int value;
    private String description;

    DeliveryReceiptState(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public static DeliveryReceiptState getDescription(int value) {
        for (DeliveryReceiptState item : values()) {
            if (item.value() == value) {
                return item;
            }
        }
        return NOT_FOUND;
    }

    public int value() {
        return value;
    }

    public String description() {
        return description;
    }

}

class MessageReceiverListenerImpl implements MessageReceiverListener {

    Logger LOGGER = Logger.getLogger("MyLog");

    public MessageReceiverListenerImpl() {
        try {
            FileHandler fh = new FileHandler("MyLogFile.log");
            LOGGER.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (IOException ex) {
            Logger.getLogger(MessageReceiverListenerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(MessageReceiverListenerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private static final String DATASM_NOT_IMPLEMENTED = "data_sm not implemented";

    public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
        if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) {
            try {
                DeliveryReceipt delReceipt = deliverSm.getShortMessageAsDeliveryReceipt();
                long id = Long.parseLong(delReceipt.getId()) & 0xffffffff;
                String messageId = Long.toString(id, 16).toUpperCase();
                System.out.println("Receiving delivery receipt for message '" + messageId + " ' from " + deliverSm.getSourceAddr() + " to " + deliverSm.getDestAddress() + " : " + delReceipt);
                LOGGER.info("Receiving delivery receipt for message '{}' from {} to {}: {}" + messageId + deliverSm.getSourceAddr() + deliverSm.getDestAddress() + delReceipt);
            } catch (InvalidDeliveryReceiptException e) {
                LOGGER.info("Failed getting delivery receipt " + e);
            }
        } else {
            NakkBindAndSend nakkSend = new NakkBindAndSend("param");
            System.out.println("Receiving message : " + new String(deliverSm.getShortMessage()) + " From " + deliverSm.getSourceAddr());
            try {
                URL url = new URL("http://104.248.23.16:9002/bet?msisdn=" + deliverSm.getSourceAddr() + "&text=" + deliverSm.getShortMessage() + "");
                URLConnection conn = url.openConnection();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    System.out.println(inputLine);
                    nakkSend.sendTextMessage(deliverSm.getSourceAddr(), inputLine);
                }
                br.close();

                System.out.println("Done");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void onAcceptAlertNotification(AlertNotification alertNotification) {
        LOGGER.info("AlertNotification not implemented");
    }

    public DataSmResult onAcceptDataSm(DataSm dataSm, Session source)
            throws ProcessRequestException {
        LOGGER.info("DataSm not implemented");
        throw new ProcessRequestException(DATASM_NOT_IMPLEMENTED, SMPPConstant.STAT_ESME_RINVCMDID);
    }
}
