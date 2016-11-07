package sibyl;

import javax.jms.*;

public class Producer {

    // Definimos las diferentes acciones, lo ponemos como string, solo deja así las claves
    private static final int MSG_JOIN = 0;
    private static final int MSG_LEAVE = 1;
    private static final int MSG_CREATE = 2;
    private static final int MSG_UPDATE_PASSWD = 3;

    public static void main(String[] args) {

        try {

            ConnectionFactory myConnFactory;
            Topic myQueue;

            myConnFactory = new com.sun.messaging.ConnectionFactory();
            Connection myConn = myConnFactory.createConnection();

            Session mySess = myConn.createSession(false, Session.AUTO_ACKNOWLEDGE);

            myQueue = mySess.createTopic("clase");

            MessageProducer myMsgProducer = mySess.createProducer(myQueue);

            MapMessage mapMessage = mySess.createMapMessage();

//            mapMessage.setInt("MSG_TYPE", MSG_JOIN);
//            mapMessage.setString("USER", "jruiz");
//            mapMessage.setString("CHATROOM", "clash royale");
//            mapMessage.setInt("MSG_TYPE", MSG_LEAVE);
//            mapMessage.setString("USER", "jruiz");
//            mapMessage.setString("CHATROOM", "clash royale");
//            mapMessage.setInt("MSG_TYPE", MSG_CREATE);
//            mapMessage.setString("USER", "jruiz");
//            mapMessage.setString("CHATROOM", "clash royale");
            mapMessage.setInt("MSG_TYPE", MSG_UPDATE_PASSWD);
            mapMessage.setString("USER", "Javier");
            mapMessage.setString("PASSWD", "holita");
            myMsgProducer.send(mapMessage);


            System.out.println("Mensaje enviado.");

            // Launcher
            // MessageConsumer myMsgConsumer = mySess.createConsumer(myQueue);

            /*
            // Para leer lo hacemos bien, con .start() por si tienes que inicializar
            // los msgListeners (para funcionar de forma asíncrona)
            myConn.start();

            // Lectura síncrona. Hasta que no haya algo que leer, no paro de comprobar
            StdMessage msg = myMsgConsumer.receive();
            if (msg instanceof TextMessage) {
                TextMessage txtMsg = (TextMessage) msg;
                System.out.println("Read StdMessage: " + txtMsg.getText());
            }
            */

            mySess.close();
            myConn.close();

        } catch (Exception jmse) {
            System.out.println("Exception occurred : " + jmse.toString());
            jmse.printStackTrace();
        }
    }
}
