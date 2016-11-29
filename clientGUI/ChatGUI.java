package clientGUI;


import javax.jms.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;
import java.util.Arrays;

import com.twitter.Extractor;
import database.Chatroom;
import database.User;
import sibyl.DynamicProducer;

import static java.lang.Thread.sleep;

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

    private Object result;
    static User user;
    private ChatroomGUI chatroom;
    static Session session;
    private Language lan;
    static FancyConsumerGUI consumer;
    private DialogLoading dialogLoading;
    private DialogSettings dialogSettings;
    private DialogLogin dialogLogin;

    /**
     * INICIALIZADORES
     **/
    private ChatGUI() {
        setLanguage();
        try {
            consumer = new FancyConsumerGUI().setChatGUI(this);
            login();
        } catch (JMSException e) {
            JOptionPane.showMessageDialog(this.panelGeneral, lan.getProperty("chNoCon"));
            System.exit(1);
        }
    }

    private void setLanguage() {
        lan = new Language();
        buttonLeaveRoom.setText(lan.getProperty("chLeaveRoom"));
        buttonNewRoom.setText(lan.getProperty("chNewRoom"));
        buttonSend.setText(lan.getProperty("send"));
        buttonSettings.setText(lan.getProperty("settings"));
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
                    case 3:
                        return chatroom.getMention();
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
            public Object getDomainObject(String topic) {
                int size = this.getDataSize();
                for (int i = 0; i < size; i++) {
                    ChatroomGUI chatroomGUI = this.getDomainObject(i);
                    if (chatroomGUI.getName().equals(topic))
                        return chatroomGUI;
                }
                return null;
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
        tableRooms.getColumn("icon").setMinWidth(80);
        tableRooms.getColumn("icon").setMaxWidth(80);
        tableRooms.getColumn("messageCount").setMinWidth(40);
        tableRooms.getColumn("messageCount").setMaxWidth(40);
        tableRooms.getColumn("name").setCellRenderer(new MyTableCellRenderer(0, false));
        tableRooms.getColumn("messageCount").setCellRenderer(new MyTableCellRenderer(3, true));
        tableRooms.getTableHeader().setUI(null);
        tableRooms.setRowHeight(72);
        tableRooms.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                changeRoom(tableRooms.rowAtPoint(e.getPoint()), false);
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
        tableUsers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String message = textSend.getText();
                message += (message.length() == 0) ? "" : " ";
                textSend.setText(message + "@" + modelUsers.getDomainObject(tableUsers.rowAtPoint(e.getPoint())).getHandle());
            }
        });
    }

    /**
     * <b>FUNCTION:</b> configure buttons
     */
    private void configureButtons() {
        buttonSend.addActionListener(e -> sendMessage());
        buttonNewRoom.addActionListener(e -> createRoom(false, 0));
        buttonLeaveRoom.addActionListener(e -> unsubscribe(false));
        buttonSettings.addActionListener(e -> openSettings(false, false));
        buttonRoomName.addActionListener(e -> {
            String newRoomName = JOptionPane.showInputDialog(this, "new room name");
            if (newRoomName != null){
                changeRoomName(false, chatroom.getName(), newRoomName);
            }
        });
    }

    /**
     * METODOS
     **/
    void start(boolean loginOK, String topics) {
        dialogLogin.loginResponse(loginOK);
        if (loginOK) {
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
            frame.setVisible(true);
            panelGeneral.registerKeyboardAction(e -> sendMessage(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | IllegalAccessException | UnsupportedLookAndFeelException |
                    InstantiationException ignored) {
            }
            this.configureButtons();
            this.initializeTables();
            for (String chatRoom : topics.split("\\|"))
                modelRooms.addRow(new ChatroomGUI().setName(chatRoom));
            changeRoom(0, false);
        }
    }

    /**
     * <b>FUNCTION:</b> login method
     */
    private void login() {
        dialogLogin = new DialogLogin();
        dialogLogin.setMinimumSize(new Dimension(300, 200));
        dialogLogin.setTitle("LOGIN");
        dialogLogin.setLocationRelativeTo(panelGeneral);
        dialogLogin.setVisible(true);
        // Login method in DialogLogin
    }

    /**
     * <b>FUNCTION:</b>
     */
    private void logout() {
        // TODO Implementar logut
        System.out.println(user.getHandle() + " has logged out");
    }

    /**
     * <b>FUNCTION:</b> send a message
     */
    private void sendMessage() {
        String text = textSend.getText();
        if (text.length() != 0) {
            DynamicProducerGUI.messageSimple(chatroom.getName(), text, extractMentions(text), user.getHandle());
            textSend.setText("");
        }
    }

    private String extractMentions(String text) {
        Extractor ex = new Extractor();
        String res = "";
        for (String mention : ex.extractMentionedScreennames(text))
            res += (res.equals("")) ? mention : "," + mention;
        return res;
    }

    void newMessage(String chatroom, boolean mention) {
        if (!chatroom.equals(this.chatroom.getName())) {
            ChatroomGUI chatRoom = (ChatroomGUI) modelRooms.getDomainObject(chatroom);
            chatRoom.newNotification(mention);
            modelRooms.notifyTableRowsUpdated(0, modelRooms.getRowCount());
        }
    }

    /**
     * <b>FUNCTION:</b> join or create to a new room
     */
    void createRoom(boolean response, int result) {
        if (!response) {
            DialogJoinRoom dialog = new DialogJoinRoom();
            dialog.setMinimumSize(new Dimension(300, 200));
            dialog.setLocationRelativeTo(panelGeneral);
            dialog.setResizable(false);
            dialog.setVisible(true);
            this.result = dialog.getChatroom();
            if (this.result != null) {
                this.dialogLoading = new DialogLoading();
                DynamicProducerGUI.messageRoomCreate(((ChatroomGUI) this.result).getName(), user.getHandle());
            }
        } else {
            dialogLoading.dispose();
            switch (result) {
                case 0:
                    this.modelRooms.addRow((ChatroomGUI) this.result);
                    this.changeRoom(modelRooms.getDataSize() - 1, false);
                    break;
                case 1:

                    break;
                case 2:
                    break;
            }
        }
    }

    /**
     * <b>FUNCTION:</b> exit the displayed room
     */
    void unsubscribe(boolean response) {
        if (response)
            if (chatroom == null)
                JOptionPane.showMessageDialog(panelGeneral, lan.getProperty("chNoTop"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            else {
                int really = JOptionPane.showConfirmDialog(panelGeneral, lan.getProperty("chLeavConf") +
                        chatroom.getName() + "?", "Really", JOptionPane.YES_NO_OPTION);
                chatroom.setSubscription(false);
                if (really == 0) {
                    DynamicProducerGUI.messageUnsubscribe(chatroom.getName());
                }
            }
        else {
            chatroom.setSubscription(false);
            this.buttonRoomName.setText("");
            this.changeRoom(0, false);
        }
    }

    /**
     * <b>FUNCTION:</b> prints the message passed in the displayed room
     *
     * @param message the message to print
     */
    boolean printMessage(MessageGUI message) {
        modelMessages.addRow(message);
        chatroom.newMessage(message);
        return true;
    }

    /**
     * <b>FUNCTION</b> changes the room displayed to the passed in the argument
     */
    private void changeRoom(int index, boolean response) {
        if (!response) {
            if (index == 0)
                if (modelRooms.getFirst() == null)
                    return;
            modelMessages.clearTableModelData();
            result = modelRooms.getDomainObject(index);
            modelRooms.notifyTableRowsUpdated(index, index);
            DynamicProducerGUI.messageChangeRoom(user.getHandle(), ((ChatroomGUI) result).getName());
            dialogLoading = new DialogLoading();
        } else {
            dialogLoading.dispose();
            modelMessages.addRows(chatroom.getMessages());
            buttonRoomName.setText(chatroom.getName());
            chatroom.setZeroMessages();
        }
    }

    void changeRoomRes(String content, String topic) {
        if (chatroom != null)
            chatroom.eraseMessages();
        chatroom = (ChatroomGUI) result;
        String[] split = content.split("\\|");
        chatroom.setSubscription(true);
        for (int i = 0; i < split.length; i+=3)
            chatroom.addMessage(new MessageGUI()
                    .setDate(Timestamp.valueOf(split[i]).getTime())
                    .setHandle_user(split[i+1])
                    .setText(split[i+2]));
        consumer.changeRoom(topic);
        changeRoom(0, true);
    }

    /**
     * <b>FUNCTION:</b> open the settings panel
     */
    void openSettings(boolean response, boolean status) {
        if (!response) {
            dialogSettings = new DialogSettings();
            dialogSettings.setMinimumSize(new Dimension(375, 200));
            dialogSettings.setResizable(false);
            dialogSettings.setLocationRelativeTo(panelGeneral);
            dialogSettings.setVisible(true);
            switch (dialogSettings.getResult()) {
                case 1:
                    setLanguage();
                    break;
                case 2:
                    logout();
                    break;
            }
        } else {
            dialogSettings.result(status);
        }
    }

    void changeRoomName(boolean response, String chatroom, String aNew) {
        if (!response) {
            DynamicProducerGUI.messageChangeName(chatroom, aNew, user.getHandle());
        }
        else {
            int tam = modelRooms.getDataSize();
            for (int i = 0; i < tam; i++)
                if (modelRooms.getDomainObject(i).getName().equals(chatroom))
                    modelRooms.setValueAt(aNew, i, 2);
        }
    }

    public static void main(String[] args) {
        new ChatGUI();
    }
}
