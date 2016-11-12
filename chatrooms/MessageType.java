package chatrooms;

/**
 * Created by jrevillas on 08/11/2016.
 */
public enum MessageType {
    /**
     * TYPE       int      0
     * CHATROOM   String   "clashroyale"
     * CONTENT    String   "Mañana presentan una carta nueva."
     * USER       String   "jrevillas"
     */
    MSG_SIMPLE,
    /**
     * TYPE       int      1
     * CHATROOM   String   "clashroyale"
     * CONTENT    String   "Creo que @jruiz y @mnunez ya la tienen."
     * MENTIONS   String   "jruiz,mnunez"
     * USER       String   "jrevillas"
     */
    MSG_WITH_MENTIONS,
    /**
     * TYPE       int      2
     * CHATROOM   String   "dafi"
     * NAME       String   "daetsiinf"
     * USER       String   "jrevillas"
     */
    REQ_ROOM_CHANGE_NAME,
    /**
     * TYPE       int      3
     * CHATROOM   String   "dafi"
     * USER       String   "jrevillas"'
     */
    REQ_ROOM_CREATE,
    /**
     * TYPE       int      4
     * USER       String   "jrevillas"
     * PASSWD     String   "holita"
     */
    REQ_USER_CHANGE_PASSWORD,
    /**
     * TYPE       int      5
     * USER       String   "jrevillas"
     * CHATROOM   String   "chatroomName"
     */
    REQ_USER_JOIN_ROOM,
    /**
     * TYPE       int      6
     * USER       String   "jrevillas"
     * CHATROOM   String   "chatroomName"
     */
    REQ_USER_LEAVE_ROOM,
    /**
     * TYPE       int      7
     * CHATROOM   String   "dafi"
     * NEW        String   "daetsiinf"
     * CONTENT    String   "The creator user has changed the name of this chatroom"
     */
    RES_ROOM_CHANGE_NAME,
    /**
     * TYPE       int      8
     * CHATROOM   String   "jruiz"
     */
    RES_ROOM_CREATE,
    /**
     * TYPE       int      9
     * STATUS     BOOLEAN  true/false
     */
    RES_USER_CHANGE_PASSWORD,
    /**
     * TYPE       int      10
     * CONTENT    String   "newName"
     */
    RES_USER_JOIN_ROOM,
    /**
     * TYPE       int      11
     * CONTENT    String   "last messages"
     */
    RES_USER_LEAVE_ROOM,
    /**
     * TYPE       int      12
     * USER       String   "jruiz"
     */
    REQ_LOGIN,

    /**
     * TYPE       int      13
     * STATUS     BOOLEAN  true/false
     * TOPICS     String   "topic1|topic2|..."
     * CONTENT    String   "last messages"
     */
    RES_LOGIN;
}