package sibyl;

/**
 * Created by jrevillas on 08/11/2016.
 */
public enum Types {
    /**
     * TYPE       int
     * CHATROOM   String   "clashroyale"
     * CONTENT    String   "Mañana presentan una carta nueva."
     * USER       String   "jrevillas"
     */
    MSG_SIMPLE,

    /**
     * TYPE       int
     * CHATROOM   String   "clashroyale"
     * CONTENT    String   "Creo que @jruiz y @mnunez ya la tienen."
     * MENTIONS   String   "jruiz,mnunez"
     * USER       String   "jrevillas"
     */
    MSG_WITH_MENTIONS,

    /**
     * TYPE       int
     * CHATROOM   String   "dafi"
     * NAME       String   "daetsiinf"
     * USER       String   "jrevillas"
     */
    REQ_ROOM_CHANGE_NAME,

    /**
     * TYPE       int
     * CHATROOM   String   "dafi"
     * USER       String   "jrevillas"'
     */
    REQ_ROOM_CREATE,

    /**
     * TYPE       int
     * USER       String   "jrevillas"
     * PASSWD     String   "holita"
     */
    REQ_USER_CHANGE_PASSWORD,

    /**
     * TYPE       int
     * USER       String   "jrevillas"
     * CHATROOM   String   "chatroomName"
     */
    REQ_USER_JOIN_ROOM,

    /**
     * TYPE       int
     * USER       String   "jrevillas"
     * CHATROOM   String   "chatroomName"
     */
    REQ_USER_LEAVE_ROOM,

    /**
     * TYPE     int
     * CHATROOM String
     */
    RES_NEW_MENTION,

    /**
     * TYPE     int
     * CHATROOM String
     */
    RES_NEW_MESSAGE,

    /**
     * TYPE       int
     * CHATROOM   String   "dafi"
     * NEW        String   "daetsiinf"
     * CONTENT    String   "The creator user has changed the name of this chatroom"
     */
    RES_ROOM_CHANGE_NAME,

    /**
     * TYPE       int
     * CHATROOM   String   "jruiz"
     * STATUS     Boolean
     */
    RES_ROOM_CREATE,

    /**
     * TYPE       int
     * STATUS     BOOLEAN  true/false
     */
    RES_USER_CHANGE_PASSWORD,

    /**
     * TYPE       int
     * CONTENT    String   "lastMessages"
     * TOPIC      Topic    "topicx"
     */
    RES_USER_JOIN_ROOM,

    /**
     * TYPE       int
     * STATUS     boolean  true/false
     */
    RES_USER_LEAVE_ROOM,

    /**
     * TYPE       int
     * USER       String   "jruiz"
     * PASSWORD   String   "123456"
     */
    REQ_LOGIN,

    /**
     * TYPE       int
     * STATUS     BOOLEAN  true/false
     * TOPICS     String   "topic1|topic2|..."
     * CONTENT    String   "last messages"
     */
    RES_LOGIN;
}