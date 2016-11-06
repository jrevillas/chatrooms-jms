package chatrooms;

import javax.jms.*;
import java.io.IOException;

public class Consumidor implements javax.jms.MessageListener {

    private static final String BLUE = "\u001B[34m";
    private static final String CAKE = "\uD83C\uDF70";
    private static final String PIZZA = "\uD83C\uDF46";
    private static final String POO = "\uD83D\uDCA9";
    private static final String RED = "\u001B[31m";
    private static final String ROCKET = "\uD83D\uDE80";
    private static final String RESET = "\u001B[0m";

    public static MessageProducer msgProducer;
    public static Session session;

    public static MessageConsumer magiConsumer;
    public static MessageProducer magiProducer;

    public Consumidor() {
        try {
            // Inicialización de Conexión y Sesión

            ConnectionFactory myConnFactory = new com.sun.messaging.ConnectionFactory();
            Connection connection = myConnFactory.createConnection();

            // connection.setClientID("DurableSub");

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Obtrención de Destination en variable chatTopic

            // Este es el nombre que tendrá en el broker, por lo que tendrá que ser distinto en cada usuario
            Queue magiQueue = session.createQueue("sibyl");
            magiProducer = session.createProducer(magiQueue);
            magiConsumer = session.createConsumer(magiQueue);

            Topic chatTopic = session.createTopic("clase");
            MessageConsumer subscriber = session.createConsumer(chatTopic);
            msgProducer = session.createProducer(chatTopic);

            // subscriber = mySess.createConsumer(chatTopic);
            subscriber.setMessageListener(this);
            magiConsumer.setMessageListener(this);
            connection.start();
        } catch (JMSException ex) {
            ex.printStackTrace();
        }
    }

    public void onMessage(Message msg) {

        // if !comesFromMagi -> pinta mensaje
        // else -> magiHandler(msg);

        MapMessage mensajeTexto = (MapMessage) msg;
        try {
            if (mensajeTexto.getJMSDestination() instanceof Queue) {
                System.out.println("Acaba de entrar un msg desde Sibyl");
                if (mensajeTexto.getInt("MSG_TYPE") == 2) {
                    RenderEngine.addTopic(mensajeTexto.getString("CHATROOM"));
                }
                if (mensajeTexto.getInt("MSG_TYPE") == 4) {

                    // CONDICION DE SALIDA MELÓN
                    for (int i = 0; i < RenderEngine.getTopics().size(); i++) {
                        if (RenderEngine.getTopics().get(i).equals(mensajeTexto.getString("CHATROOM"))) {
                            RenderEngine.getTopics().set(i, mensajeTexto.getString("NEW"));
                        }
                    }
                }
                if (mensajeTexto.getInt("MSG_TYPE") == 7) {
                    RenderEngine.addMessage(mensajeTexto.getString("MSG_CONTENT"));
                }
            }

            if (mensajeTexto.getJMSDestination() instanceof Topic) {
                System.out.println("Acaba de entrar un msg desde el topic \"clase\"");
                if (mensajeTexto.getInt("MSG_TYPE") == 5) {
                    RenderEngine.addMessage(mensajeTexto.getString("MSG_CONTENT"));
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
        //RenderEngine.addMessage("[@" + RED + "jrevillas" + RESET + "] " + PIZZA + "  " + CAKE + "  " + PIZZA);
        //RenderEngine.addMessage("[@" + RED + "jrevillas" + RESET + "] " + POO + "  " + POO + "  " + POO);

        System.out.print("\n");
        RenderEngine.render();
    }

    public static void main(String[] args) {
        Consumidor instancia = new Consumidor();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}