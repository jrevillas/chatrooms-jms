package clientGUI;


import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;
import java.util.Arrays;

import database.Chatroom;
import database.User;
import sibyl.Types;

public class ChatGUI {

    /**
     * INTERFACE
     **/
    private JFrame frame;
    private JPanel panelGeneral;
    private JPanel panelMenu;
    private JPanel panelChat;
    private JPanel panelSend;
    private JScrollPane scrollRooms;
    private JButton buttonSend;
    private JButton buttonSettings;
    private JButton buttonNewRoom;
    private JButton buttonLeaveRoom;
    private JTextField textSend;
    private JTable tableMessages;
    private JTable tableRooms;
    private JTable tableUsers;
    private JPanel panelInfoRoom;
    private JSplitPane splitMessage;
    private JScrollPane scrollMensajes;
    private JScrollPane scrollUsers;
    private JSplitPane splitMessages;
    private JButton buttonRoomName;
    private JList listUsers;
    private JPanel panelMessages;
    /**
     * UTILS
     **/
    private GenericDomainTableModel<MessageGUI> modelMessages;
    private GenericDomainTableModel<ChatroomGUI> modelRooms;
    private GenericDomainTableModel<User> modelUsers;

    static User user;
    private ChatroomGUI chatroom;
    private DynamicProducerGUI producer;

    private static int login = -2;
    public Session session;
    private Language lan;

    /**
     * <b>FUNCTION:</b> constructor
     */
    private ChatGUI() {
        try {
            frame = new JFrame("chatrooms") {
                @Override
                protected void processWindowEvent(WindowEvent e) {
                    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
                        logout();
                        System.exit(0);
                    }
                    super.processWindowEvent(e);
                }
            };
            frame.setContentPane(panelGeneral);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setMinimumSize(new Dimension(980, 720));
            frame.setLocationRelativeTo(null);
            frame.pack();
            FancyConsumerGUI consumer = new FancyConsumerGUI();
            consumer.setChatGUI(this);
            this.configureButtons();
            this.initializeTables();
            producer = new DynamicProducerGUI().setProducer(session);
            do {
                login();
                while (login == -2) {
                    // TODO colocar aqui algo que solo avance cuando consumer reciba respuesta de tipo LOGIN_RESPONSE
                }
                if (login == 0) {
                    JOptionPane.showMessageDialog(null, "Welcome back " + user.getHandle(),
                            "LOGGED IN", JOptionPane.INFORMATION_MESSAGE);
                    frame.setVisible(true);
                    panelGeneral.registerKeyboardAction(e -> sendMessage(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } catch (ClassNotFoundException | IllegalAccessException | UnsupportedLookAndFeelException |
                            InstantiationException ignored) {
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "That user is already logged in and \n" +
                            "that's not it's password", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } while (login < 0);
        } catch (JMSException e) {
            JOptionPane.showMessageDialog(null, "Connection Exception, check your connectivity",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    /**
     * <b>FUNCTION:</b> login method
     *
     */
    private void login() {
        MapMessage logMess;
        LoginDialog dialog = new LoginDialog();
        dialog.setMinimumSize(new Dimension(300, 200));
        dialog.setLocationRelativeTo(panelGeneral);
        dialog.setVisible(true);
        // Login method in LoginDialog
        if (user == null) {
            JOptionPane.showMessageDialog(panelGeneral, "Bye bye", "Bye, bye", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        try {
            logMess = session.createMapMessage();
            logMess.setInt("TYPE", Types.REQ_LOGIN.ordinal());
            logMess.setString("USER", user.getHandle());
            logMess.setString("PASSWD", user.getPassword());
            producer.sendMessage(logMess);
            System.out.println("INFO: mensaje enviado");
            // TODO pa pruebas
            loginOK(true, "hola|clash_royale|hum");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    void loginOK(boolean status, String topics) {
        String[] topicArray;
        login = (status) ? 0 : -1;
        if (status) {
            topicArray = topics.split("\\|");
            for (String topic : topicArray) {
                createRoom(topic);
            }
            changeRoom(0);
        }

    }

    /**
     * <b>FUNCTION:</b>
     */
    private void logout() {
        // TODO Implementar logut
        System.out.println(user.getHandle() + " has logged out");
    }

    /**
     * <b>FUNCTION:</b> intialize tables
     */
    private void initializeTables() {
        // Rooms
        modelRooms = new GenericDomainTableModel<ChatroomGUI>(Arrays.asList(new String[]{"icon", "name", "messageCount"})) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return ImageIcon.class;
                    case 1:
                        return String.class;
                    case 2:
                        return int.class;
                    default:
                        return null;
                }
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                ChatroomGUI chatroom = this.getDomainObject(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return chatroom.getIcon();
                    case 1:
                        return chatroom.getName();
                    case 2:
                        return chatroom.getUnreadMessages();
                    default:
                        return null;
                }
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                ChatroomGUI topicRow = this.getDomainObject(rowIndex);
                switch (columnIndex) {
                    case 0:
                        break;
                    case 1:
                        topicRow.setName((String) aValue);
                        break;
                    case 2:
                        break;
                    case 3:
                        topicRow.setMention();
                        break;

                }
            }

            @Override
            public void deleteRow(String topic) {
                int size = this.getDataSize();
                for (int i = 0; i < size; i++) {
                    String aux = this.getDomainObject(i).getName();
                    if (aux.equals(buttonRoomName.getText())) {
                        deleteRow(i);
                        this.notifyTableRowsDeleted(i, i);
                        break;
                    }
                }
            }
        };
        tableRooms.setModel(modelRooms);
        tableRooms.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tableRooms.getColumn("icon").setMinWidth(72);
        tableRooms.getColumn("icon").setMaxWidth(72);
        tableRooms.getColumn("messageCount").setMinWidth(40);
        tableRooms.getColumn("messageCount").setMaxWidth(40);
        //       tableRooms.getColumn ( "icon" ).setCellRenderer ( renderRooms );
        tableRooms.getColumn("name").setCellRenderer(new MyTableCellRenderer(0, false));
        tableRooms.getColumn("messageCount").setCellRenderer(new MyTableCellRenderer(3, true));
        tableRooms.getTableHeader().setUI(null);
        tableRooms.setRowHeight(72);
        tableRooms.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                changeRoom(tableRooms.rowAtPoint(e.getPoint()));
            }
        });
        // Messages
        modelMessages = new GenericDomainTableModel<MessageGUI>(Arrays.asList(new String[]{"hour", "sender", "body"})) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 0) ? Timestamp.class : String.class;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                MessageGUI message = getDomainObject(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return message.getDate();
                    case 1:
                        return message.getHandle_user();
                    case 2:
                        return message.getText();
                    default:
                        throw new ArrayIndexOutOfBoundsException(columnIndex);
                }
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            }
        };
        tableMessages.setModel(modelMessages);
        tableMessages.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        DefaultTableCellRenderer renderHour = new MyTableCellRenderer(1, true);
        DefaultTableCellRenderer renderSender = new MyTableCellRenderer(2, true);
        tableMessages.getColumn("hour").setMinWidth(75);
        tableMessages.getColumn("hour").setMaxWidth(75);
        tableMessages.getColumn("hour").setCellRenderer(renderHour);
        tableMessages.getColumn("sender").setMinWidth(100);
        tableMessages.getColumn("sender").setMaxWidth(100);
        tableMessages.getColumn("sender").setCellRenderer(renderSender);
        tableMessages.getTableHeader().setUI(null);
        // Users
        modelUsers = new GenericDomainTableModel<User>(Arrays.asList(new String[]{"users"})) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return getDomainObject(rowIndex).getHandle();
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            }
        };
        tableUsers.setModel(modelUsers);
        tableUsers.getColumn("users").setMinWidth(100);
        tableUsers.getColumn("users").setMaxWidth(100);
        tableUsers.getColumn("users").setResizable(false);
        DefaultTableCellRenderer renderUser = new MyTableCellRenderer(2, false);
        tableUsers.getColumn("users").setCellRenderer(renderUser);
        tableUsers.getTableHeader().setUI(null);
    }

    /**
     * <b>FUNCTION:</b> configure buttons
     */
    private void configureButtons() {
        buttonSend.addActionListener(e -> sendMessage());
        buttonNewRoom.addActionListener(e -> joinRoom());
        buttonLeaveRoom.addActionListener(e -> exitRoom());
        buttonSettings.addActionListener(e -> openSettings());
    }

    /**
     * <b>FUNCTION:</b> send a message
     */
    private void sendMessage() {
        try {
            String text = textSend.getText();
            if (text.length() != 0) {
                MapMessage message;
                message = session.createMapMessage();
                message.setInt("TYPE", Types.MSG_SIMPLE.ordinal());
                message.setString("CHATROOM", chatroom.getName());
                //message.setString("CHATROOM", "clash_royale");
                message.setString("CONTENT", text);
                message.setString("USER", user.getHandle());
                producer.sendMessage(message);
                textSend.setText("");
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    void newMessage(MessageGUI message) {
        String chatname = message.getChatroom();
        boolean active = chatname.equals(this.chatroom.getName()) && printMessage(message);
        for (ChatroomGUI chatroom : modelRooms.getDomainObjects()) {
            if (chatroom.getName().equals(chatname)) {
                chatroom.newMessage(message, active);
                modelRooms.notifyTableRowsUpdated(0, modelRooms.getRowCount());
                break;
            }
        }


    }

    /**
     * <b>FUNCTION:</b> join or create to a new room
     */
    private void joinRoom() {
        DialogJoinRoom dialog = new DialogJoinRoom();
        dialog.setMinimumSize(new Dimension(300, 200));
        dialog.setLocationRelativeTo(panelGeneral);
        dialog.setResizable(false);
        dialog.setVisible(true);
        Chatroom res = dialog.getChatroom();
        if (res != null) {
            modelRooms.addRow((ChatroomGUI) res);
            this.changeRoom(modelRooms.getDataSize() - 1);
            // TODO <- implement join to a new topic
        }
    }

    /**
     * <b>FUNCTION:</b> exit the displayed room
     */
    private void exitRoom() {
        String roomName = buttonRoomName.getText();
        if (roomName.length() == 0)
            JOptionPane.showMessageDialog(panelGeneral, "No topic selected", "Error", JOptionPane.ERROR_MESSAGE);
        else {
            int really = JOptionPane.showConfirmDialog(panelGeneral, "Are you sure you want to exit " + roomName,
                    "Really", JOptionPane.YES_NO_OPTION);
            if (really == 0) {
                modelRooms.deleteRow(roomName);
                // TODO <- Implementar metodo salir de un topic
                this.buttonRoomName.setText("");
                this.changeRoom(0);
            }
        }
    }

    /**
     * <b>FUNCTION:</b> prints the message passed in the displayed room
     *
     * @param message the message to print
     */
    private boolean printMessage(MessageGUI message) {
        modelMessages.addRow(message);
        tableMessages.scrollRectToVisible(tableMessages
                .getCellRect(tableMessages.getRowCount() - 1, 0, true));
        return true;
    }

    /**
     * <b>FUNCTION</b> changes the room displayed to the passed in the argument
     */
    private void changeRoom(int index) {
        if (index == 0)
            if (modelRooms.getFirst() == null)
                return;
        modelMessages.clearTableModelData();
        modelUsers.clearTableModelData();
        chatroom = modelRooms.getDomainObject(index);
        chatroom.setZeroMessages();
        modelRooms.notifyTableRowsUpdated(index, index);
        buttonRoomName.setText(chatroom.getName());
        modelMessages.addRows(chatroom.getMessages());
        // TODO <- Implementar metodo recuperar mensajes y usuarios
    }

    /**
     * <b>FUNCTION:</b> open the settings panel
     */

    private void openSettings() {
        DialogSettings dialog = new DialogSettings();
        dialog.setMinimumSize(new Dimension(375, 200));
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(panelGeneral);
        dialog.setVisible(true);
        switch (dialog.getResult()) {
            case 1:
                setLanguage();
                break;
            case 2:
                System.out.println(dialog.getHandler());
                break;
            case 3:
                System.out.println(dialog.getPassword());
                break;
            case 4:
                logout();
                break;
        }
    }

    private void setLanguage() {
        lan = new Language();
        buttonLeaveRoom.setText(lan.getProperty("chLeaveRoom"));
        buttonNewRoom.setText(lan.getProperty("chNewRoom"));
        buttonSend.setText(lan.getProperty("send"));
        buttonSettings.setText(lan.getProperty("settings"));
    }

    void changeRoomName(String chatroom, String aNew) {
        int tam = modelRooms.getDataSize();
        for (int i = 0; i < tam; i++) {
            if (modelRooms.getDomainObject(i).getName().equals(chatroom)) {
                modelRooms.setValueAt(aNew, i, 2);
            }
        }
    }

    void createRoom(String chatroom) {
        modelRooms.addRow(new ChatroomGUI().setName(chatroom));
    }

    public static void main(String[] args) {
        new ChatGUI();
    }
}
