package chatrooms;

/**
 * Created by jrevillas on 08/11/2016.
 */
public enum MessageType {

    /**
     * CHATROOM   String   "clashroyale"
     * CONTENT    String   "Mañana presentan una carta nueva."
     * TYPE       int      0
     * USER       String   "jrevillas"
     */
    MSG_SIMPLE,

    /**
     * CHATROOM   String   "clashroyale"
     * CONTENT    String   "Creo que @jruiz y @mnunez ya la tienen."
     * MENTIONS   String   "jruiz,mnunez"
     * TYPE       int      1
     * USER       String   "jrevillas"
     */
    MSG_WITH_MENTIONS,

    /**
     * CHATROOM   String   "dafi"
     * NAME       String   "daetsiinf"
     * TYPE       int      2
     * USER       String   "jrevillas"
     */
    REQ_ROOM_CHANGE_NAME,

    /**
     * CHATROOM   String   "dafi"
     * TYPE       int      3
     * USER       String   "jrevillas"
     */
    REQ_ROOM_CREATE,

    /**
     * TODO
     * TYPE       int      4
     * USER       String   "jrevillas"
     */
    REQ_USER_CHANGE_PASSWORD,

    /**
     * TODO
     * TYPE       int      5
     * USER       String   "jrevillas"
     */
    REQ_USER_JOIN_ROOM,

    /**
     * TODO
     * TYPE       int      6
     * USER       String   "jrevillas"
     */
    REQ_USER_LEAVE_ROOM,

    /**
     * TODO
     * TYPE       int      7
     * USER       String   "jrevillas"
     */
    RES_LAST_MESSAGES,

    /**
     * CHATROOM   String   "dafi"
     * NAME       String   "daetsiinf"
     * TYPE       int      8
     * USER       String   "jrevillas"
     */
    RES_ROOM_CHANGE_NAME,

    /**
     * TODO
     * TYPE       int      9
     * USER       String   "jrevillas"
     */
    RES_ROOM_CREATE,

    /**
     * TODO
     * TYPE       int      10
     * USER       String   "jrevillas"
     */
    RES_USER_CHANGE_PASSWORD,

    /**
     * TODO
     * TYPE       int      11
     * USER       String   "jrevillas"
     */
    RES_USER_JOIN_ROOM,

    /**
     * TODO
     * TYPE       int      12
     * USER       String   "jrevillas"
     */
    RES_USER_LEAVE_ROOM;

}