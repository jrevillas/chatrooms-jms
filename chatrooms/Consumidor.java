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

    public static MessageConsumer sibylControlConsumer;
    public static MessageProducer sibylControlProducer;

    public Consumidor() {
        try {
            ConnectionFactory myConnFactory = new com.sun.messaging.ConnectionFactory();
            Connection connection = myConnFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Primera queue a la que se suscribe el usuario: conocida (sibyl-control)
            // El primer mensaje que se obtenga debe contener el nombre de la queue a la que
            // el usuario debe suscribirse para interacturar con sibyl y recibir notificaciones.

            // TODO repasar replyTo al tratar los mensajes desde sibyl para responder a ellos
            // reqQ = session.createQueue("queue:///REQUESTQ");
            // repQ = session.createQueue("queue:///REPLYQ");
            // producer = session.createProducer(reqQ);
            // Message requestMessage = session.createTextMessage("Requesting a service");
            // requestMessage.setJMSReplyTo(repQ);
            // producer.send(requestMessage);
            // String selector = "JMSCorrelationID='" + requestMessage.getJMSMessageID()+"'";
            // CONSUMER MÁGICO -> Solo recibe el mensaje que es respuesta al tuyo
            // consumer = session.createConsumer(repQ, selector);
            // connection.start();
            // TODO https://www.ibm.com/developerworks/community/blogs/messaging/entry/jms_request_reply_sample?lang=es

            // sibyl ---> BOOT SEQUENCE incluye SUBSCRIBE(sibyl-control)
            // usuario ---> SUBSCRIBE(sibyl-control)
            // usuario ---> {"USER_HANDLER":"jrevillas","USER_PASSWORD":"12345"} ---> sibyl-control (QUEUE)
            // sibyl ---> {"HANDSHAKE":true,"PUSH_QUEUE":"sibyl-35","LAST_TOPIC":"clashroyale"} ---> sibyl-control (QUEUE)
            // sibyl ---> SUBSCRIPTION(sibyl-35)
            // usuario ---> UNSUBSCRIBE(sibyl-control) SUBSCRIPTION(sibyl-35) y SUBSCRIPTION(clashroyale)
            // sibyl ---> {"LAST_MESSAGES":"...","CHATROOM":"clashroyale"} ---> sibyl-35 (QUEUE)

            Queue sibylControlQueue = session.createQueue("sibylcontrol");
            sibylControlProducer = session.createProducer(sibylControlQueue);
            sibylControlConsumer = session.createConsumer(sibylControlQueue);

            // Tras autenticarse con sibyl, recibirá un mensaje con el topic al que debe
            // suscribirse (último topic abierto). Si se acaba de registrar, es "general"
            // TODO realizar este paso después de la autenticación con sibyl
            Topic chatTopic = session.createTopic("clase");
            MessageConsumer subscriber = session.createConsumer(chatTopic);
            msgProducer = session.createProducer(chatTopic);

            // fijar el handler de los mensajes.
            // TODO pueden y deberían ser distintas funciones, refactor
            subscriber.setMessageListener(this);
            sibylControlConsumer.setMessageListener(this);
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