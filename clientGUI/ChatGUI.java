package clientGUI;

import clientGUI.resources.languages.Language;
import com.twitter.Extractor;
import database.User;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;
import java.util.Arrays;

public class ChatGUI {

    // -------------------------------------------------------------------------------------------
    //      FIELDS
    // -------------------------------------------------------------------------------------------
    // INTERFACE COMPONENTS
    private JFrame frame;
    private JPanel panelGeneral;
    private PanelLogin panelLogin;
    private JButton buttonSend;
    private JButton buttonSettings;
    private JButton buttonNewRoom;
    private JButton buttonLeaveRoom;
    private JTextField textSend;
    private JTable tableMessages;
    private JTable tableRooms;
    private JButton buttonRoomName;
    private Component glassPane;
    // DIALOGS
    private DialogLoading dialogLoading;
    private DialogSettings dialogSettings;
    // LISTS & MODELS
    private GenericDomainTableModel<MessageGUI> modelMessages;
    private GenericDomainTableModel<ChatroomGUI> modelRooms;
    // BROOKER UTILS
    private Session session;
    private FancyConsumerGUI consumer;
    // AUXILIARS
    private ChatroomGUI result;
    private User user;
    private ChatroomGUI chatroom;
    private Language lan;

    // -------------------------------------------------------------------------------------------
    //      METHODS
    // -------------------------------------------------------------------------------------------
    // INITIALIZER
    private ChatGUI() {
        setLanguage();
        frame = new JFrame("chatrooms") {
            @Override
            protected void processWindowEvent(WindowEvent e) {
                if (e.getID() == WindowEvent.WINDOW_CLOSING) {
                    logout();
                }
                super.processWindowEvent(e);
            }
        };
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getRootPane().setGlassPane(new JComponent() {
            public void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 100));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        });

        UIManager.put("OptionPane.messageFont", new Font("Roboto Light", 0, 14));
        UIManager.put("OptionPane.buttonFont", new Font("Roboto Light", 0, 16));

        UIManager.put("Button.foreground", Color.decode("#232323"));
        UIManager.put("Button.font", new Font("Roboto Light", 0, 16));

        glassPane = frame.getGlassPane();
        try {
            consumer = new FancyConsumerGUI().setChatGUI(this);
            DynamicProducerGUI.setSession(session);
            login();
        } catch (JMSException e) {
            JOptionPane.showMessageDialog(this.panelGeneral, lan.getProperty("chNoCon"));
            System.exit(1);
        }
    }

    void setLanguage() {
        lan = new Language();
        buttonLeaveRoom.setText(lan.getProperty("chLeaveRoom"));
        buttonNewRoom.setText(lan.getProperty("chNewRoom"));
        buttonSend.setText(lan.getProperty("send"));
        buttonSettings.setText(lan.getProperty("settings"));
    }

    private void initializeTables() {
        // Rooms
        modelRooms = new GenericDomainTableModel<ChatroomGUI>(
                Arrays.asList(new String[]{"icon", "name", "messageCount"})) {
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
        modelMessages = new GenericDomainTableModel<MessageGUI>(
                Arrays.asList(new String[]{"hour", "sender", "body"})) {
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
        tableMessages.setFont(new Font("OpenSansEmoji", 0, 12));
        tableMessages.getTableHeader().setUI(null);
        // Users
//        modelUsers = new GenericDomainTableModel<User>(Arrays.asList(new String[]{"users"})) {
//
//            @Override
//            public Class<?> getColumnClass(int columnIndex) {
//                return String.class;
//            }
//
//            @Override
//            public Object getValueAt(int rowIndex, int columnIndex) {
//                return getDomainObject(rowIndex).getHandle();
//            }
//
//            @Override
//            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
//            }
//        };
//        tableUsers.setModel(modelUsers);
//        tableUsers.getColumn("users").setMinWidth(100);
//        tableUsers.getColumn("users").setMaxWidth(100);
//        tableUsers.getColumn("users").setResizable(false);
//        DefaultTableCellRenderer renderUser = new MyTableCellRenderer(2, false);
//        tableUsers.getColumn("users").setCellRenderer(renderUser);
//        tableUsers.getTableHeader().setUI(null);
//        tableUsers.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                String message = textSend.getText();
//                message += (message.length() == 0) ? "" : " ";
//                textSend.setText(message + "@" + modelUsers.
//                        getDomainObject(tableUsers.rowAtPoint(e.getPoint())).getHandle());
//            }
//        });
    }

    private void configureButtons() {
        buttonSend.addActionListener(e -> sendMessage());
        buttonNewRoom.addActionListener(e -> createRoom(false, false));
        buttonLeaveRoom.addActionListener(e -> leaveRoom(false));
        buttonSettings.addActionListener(e -> openSettings(false, false));
        buttonRoomName.addActionListener(e -> changeRoomName(false, null, null));
    }

    // LOGIN    ----------------------------------------------------------------------------------

    private void login() {
        panelLogin = new PanelLogin().setChatGUI(this);
        frame.setContentPane(panelLogin);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    void loginRequest(String usr, String pwd) {
        user = new User().setHandle(usr).setPassword(String.valueOf(pwd));
        consumer.setHandler(usr);
        panelLogin.setEnabled(false);
        this.showDialogLoading(true);
        DynamicProducerGUI.messageLogin(usr, String.valueOf(pwd));
    }

    void start(boolean loginOK, String topics) {
        this.showDialogLoading(false);
        if (loginOK) {
            this.frame.setVisible(false);
            JOptionPane.showMessageDialog(panelLogin, lan.getProperty("loLogg"));
            DynamicProducerGUI.setProducer(user.getHandle());
            this.configureButtons();
            this.initializeTables();
            for (String chatRoom : topics.split("\\|"))
                modelRooms.addRow(new ChatroomGUI().setName(chatRoom));
            changeRoom(0, false);
            frame.setContentPane(panelGeneral);
            frame.setMinimumSize(new Dimension(970, 670));
            frame.setLocationRelativeTo(null);
            frame.setResizable(true);
            frame.setVisible(true);
            panelGeneral.registerKeyboardAction(e -> sendMessage(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        } else {
            JOptionPane.showMessageDialog(panelLogin, lan.getProperty("loWrPwd"));
            panelLogin.failedLogin();
        }
    }

    // MESSAGES ----------------------------------------------------------------------------------

    private void sendMessage() {
        String text = textSend.getText();
        if (text.length() != 0) {
            DynamicProducerGUI.messageSimple(chatroom.getName(), text, extractMentions(text), user.getHandle());
            textSend.setText("");
        }
    }

    void newMessage(String chatroom, boolean mention) {
        if (!chatroom.equals(this.chatroom.getName())) {
            ChatroomGUI chat = (ChatroomGUI) modelRooms.getDomainObject(chatroom);
            chat.newMessage(mention);
            modelRooms.notifyDomainObjectUpdated(chat);
        }
    }

    void printMessage(MessageGUI message) {
        modelMessages.addRow(message);
    }

    // ROOMS    ----------------------------------------------------------------------------------

    void createRoom(boolean response, boolean result) {
        if (!response) {
            DialogCreateRoom dcr = new DialogCreateRoom();
            dcr.setLocationRelativeTo(panelGeneral);
            dcr.setVisible(true);
            String newRoom = dcr.getResult();
            if (newRoom != null) {
                this.result = new ChatroomGUI().setName(newRoom);
                DynamicProducerGUI.messageRoomCreate(newRoom, user.getHandle());
                showDialogLoading(true);
            }
        } else {
            showDialogLoading(false);
            if (result) {
                this.modelRooms.addRow(this.result);
                this.changeRoom(modelRooms.getDataSize() - 1, false);
            } else {
                JOptionPane.showMessageDialog(panelGeneral, "ERROR", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void changeRoom(int index, boolean response) {
        if (!response) {
            if (index == 0)
                if (modelRooms.getFirst() == null)
                    return;
            modelMessages.clearTableModelData();
            result = modelRooms.getDomainObject(index);
            modelRooms.notifyTableRowsUpdated(index, index);
            DynamicProducerGUI.messageChangeRoom(user.getHandle(), result.getName());
            showDialogLoading(true);
        } else {
            showDialogLoading(false);
            modelMessages.addRows(chatroom.getMessages());
            buttonRoomName.setText(chatroom.getName());
            chatroom.setZeroMessages();
            modelRooms.notifyDomainObjectUpdated(chatroom);
        }
    }

    void changeRoomRes(String content, String topic) {
        if (chatroom != null)
            chatroom.eraseMessages();
        chatroom = result;
        if (!content.equals("")) {
            String[] split = content.split("\\|");
            for (int i = split.length-3; i >= 0; i -= 3)
                modelMessages.addRow(new MessageGUI()
                        .setDate(Timestamp.valueOf(split[i]).getTime())
                        .setHandle_user(split[i + 1])
                        .setText(split[i + 2]));
        }
        consumer.changeRoom(topic);
        changeRoom(0, true);
    }

    void leaveRoom(boolean response) {
        if (!response) {
            DynamicProducerGUI.messageLeave(user.getHandle(), chatroom.getName());
            this.showDialogLoading(true);
        } else {
            showDialogLoading(false);
            JOptionPane.showMessageDialog(panelGeneral, lan.getProperty("chLeavOK"));
        }
    }

    void changeRoomName(boolean response, String chatroom, String aNew) {
        if (!response) {
            String newRoomName;
            DialogChangeName dcn = new DialogChangeName();
            dcn.setLocationRelativeTo(panelGeneral);
            dcn.setVisible(true);
            newRoomName = dcn.getResult();
            if (newRoomName != null) {
                DynamicProducerGUI.messageChangeName(this.chatroom.getName(), newRoomName, user.getHandle());
                showDialogLoading(true);
            }
        } else {
            showDialogLoading(false);
            int tam = modelRooms.getDataSize();
            for (int i = 0; i < tam; i++) {
                ChatroomGUI chat = modelRooms.getDomainObject(i);
                if (chat.getName().equals(chatroom)) {
                    chat.setName(aNew);
                    modelRooms.notifyTableRowsUpdated(i, i);
                    break;
                }
            }
        }
    }

    // AUX      ----------------------------------------------------------------------------------

    void openSettings(boolean response, boolean status) {
        if (!response) {
            dialogSettings = new DialogSettings();
            dialogSettings.setParams(this, user.getHandle());
            dialogSettings.pack();
            dialogSettings.setLocationRelativeTo(panelGeneral);
            dialogSettings.setVisible(true);
        } else {
            dialogSettings.result(status);
        }
    }

    void setSession(Session session) {
        this.session = session;
    }

    private String extractMentions(String text) {
        Extractor ex = new Extractor();
        String res = "";
        for (String mention : ex.extractMentionedScreennames(text))
            res += (res.equals("")) ? mention : "," + mention;
        return res;
    }

    private void showDialogLoading(boolean visibility) {
        glassPane.setVisible(visibility);
        if (visibility) {
            if (dialogLoading == null)
                dialogLoading = new DialogLoading();
            dialogLoading.setVisible(true);
            dialogLoading.setLocationRelativeTo(panelGeneral);
        } else {
            dialogLoading.setVisible(false);
            dialogLoading.dispose();
        }
    }

    void logout() {
        System.exit(0);
    }

    // MAIN     ----------------------------------------------------------------------------------

    public static void main(String[] args) {
        new ChatGUI();
    }
}
